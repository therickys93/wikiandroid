package it.therickys93.wiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import it.therickys93.wikiapi.Off;
import it.therickys93.wikiapi.On;
import it.therickys93.wikiapi.Reset;
import it.therickys93.wikiapi.Response;
import it.therickys93.wikiapi.WikiController;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER = "192.168.15.12";
    private static final String KEY = "arduino";

    private EditText serverEditText;
    private EditText keyEditText;
    private Spinner  lightSpinner;
    private TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverEditText = (EditText) findViewById(R.id.server_edit_text);
        keyEditText = (EditText) findViewById(R.id.key_edit_text);
        lightSpinner = (Spinner) findViewById(R.id.spinner_light);
        versionTextView = (TextView) findViewById(R.id.version_label);


        SharedPreferences settings = getSharedPreferences("MySettingsWiki", 0);
        String server = settings.getString("WIKI_SERVER", SERVER);
        serverEditText.setText(server);
        String key = settings.getString("WIKI_KEY", KEY);
        keyEditText.setText(key);

        serverEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        serverEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(serverEditText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    // save the new value
                    SharedPreferences settings = getSharedPreferences("MySettingsWiki", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("WIKI_SERVER", serverEditText.getText().toString());
                    editor.commit();
                }
                return false;
            }
        });

        keyEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        keyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(keyEditText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    // save the new value
                    SharedPreferences settings = getSharedPreferences("MySettingsWiki", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("WIKI_KEY", keyEditText.getText().toString());
                    editor.commit();
                }
                return false;
            }
        });

        versionTextView.setText("Versione applicazione: " + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);

    }

    private String getServer(){
        return "http://" + this.serverEditText.getText().toString();
    }

    private String getKey(){
        return this.keyEditText.getText().toString();
    }

    private int getLed(){
        return this.lightSpinner.getSelectedItemPosition();
    }

    public void onButtonClicked(View view){
        new OnButtonAsyncTask().execute();
    }

    public void offButtonClicked(View view){
        new OffButtonAsyncTask().execute();
    }

    public void statusButtonClicked(View view){
        new StatusButtonAsyncTask().execute();
    }

    public void resetButtonClicked(View view){
        new ResetButtonAsyncTask().execute();
    }

    private class OnButtonAsyncTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                WikiController wikiController = new WikiController(getServer());
                String response = wikiController.execute(new On(getKey(), getLed()));
                Response status = Response.parseSuccess(response);
                return status.ok();
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "ERRORE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OffButtonAsyncTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                WikiController wikiController = new WikiController(getServer());
                String response = wikiController.execute(new Off(getKey(), getLed()));
                Response status = Response.parseSuccess(response);
                return status.ok();
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "ERRORE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ResetButtonAsyncTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                WikiController wikiController = new WikiController(getServer());
                String response = wikiController.execute(new Reset(getKey()));
                Response status = Response.parseSuccess(response);
                return status.ok();
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "ERRORE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class StatusButtonAsyncTask extends AsyncTask<Void, Void, Response>{

        @Override
        protected Response doInBackground(Void... voids) {
            try {
                WikiController wikiController = new WikiController(getServer());
                String response = wikiController.execute(new it.therickys93.wikiapi.Status(getKey()));
                Response status = Response.parseSuccess(response);
                return status;
            } catch(Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response status) {
            super.onPostExecute(status);
            if(status == null){
                Toast.makeText(MainActivity.this, "ERRORE", Toast.LENGTH_SHORT).show();
            } else {
                if(status.ok()) {
                    if (status.message().charAt(getLed()) == '1'){
                        Toast.makeText(MainActivity.this, "ACCESO", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "SPENTO", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "ERRORE", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
