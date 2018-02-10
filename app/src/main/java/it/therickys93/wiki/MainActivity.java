package it.therickys93.wiki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.controller.*;
import it.therickys93.wikiapi.model.House;
import it.therickys93.wikiapi.model.Led;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER = "192.168.15.12";
    public static final String WIKI_FILENAME = "wiki.json";

    private EditText serverEditText;
    private Spinner  lightSpinner;
    private TextView versionTextView;

    private static Context context;
    public static House house;

    public static Context getAppContext(){
        return MainActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        serverEditText = (EditText) findViewById(R.id.server_edit_text);
        lightSpinner = (Spinner) findViewById(R.id.spinner_light);
        versionTextView = (TextView) findViewById(R.id.version_label);

        MainActivity.house = HouseUtils.loadHouseFromFile(MainActivity.getAppContext(), WIKI_FILENAME);

        SharedPreferences settings = getSharedPreferences("MySettingsWiki", 0);
        String server = settings.getString("WIKI_SERVER", SERVER);
        serverEditText.setText(server);

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

        versionTextView.setText("Versione applicazione: " + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private List<String> getLedNames(){
        List<String> names = new ArrayList<>();
        List<Led> leds = HouseUtils.getLedsFromHouse(MainActivity.house);
        if(leds.size() > 0) {
            for (Led l : leds) {
                names.add(l.getName());
            }
        } else {
            names.add("Nessun Led trovato");
        }
        return names;
    }

    private void populateSpinner(){
        List<String> leds = getLedNames();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, leds);
        this.lightSpinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateSpinner();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.house:
                intent = new Intent(this, HouseConfigurationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getServer(){
        return "http://" + this.serverEditText.getText().toString();
    }

    private Led getLed() {
        if(MainActivity.house.getLedCount() > 0) {
            return MainActivity.house.getLedAt(this.lightSpinner.getSelectedItemPosition());
        } else {
            return null;
        }
    }

    public void onButtonClicked(View view){
        new OnButtonAsyncTask().execute(getLed());
    }

    public void offButtonClicked(View view){
        new OffButtonAsyncTask().execute(getLed());
    }

    public void statusButtonClicked(View view){
        new StatusButtonAsyncTask().execute(getLed());
    }

    public void resetButtonClicked(View view){
        new ResetButtonAsyncTask().execute(getLed());
    }

    private class OnButtonAsyncTask extends AsyncTask<Led, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Led... led) {
            try {
                WikiRequest wikiController = new WikiRequest(getServer());
                String response = wikiController.execute(new On(led[0]));
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

    private class OffButtonAsyncTask extends AsyncTask<Led, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Led... led) {
            try {
                WikiRequest wikiController = new WikiRequest(getServer());
                String response = wikiController.execute(new Off(led[0]));
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


    private class ResetButtonAsyncTask extends AsyncTask<Led, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Led... led) {
            try {
                WikiRequest wikiController = new WikiRequest(getServer());
                String response = wikiController.execute(new Reset(led[0].getKey()));
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

    private class StatusButtonAsyncTask extends AsyncTask<Led, Void, Response> {

        @Override
        protected Response doInBackground(Led... led) {
            try {
                WikiRequest wikiController = new WikiRequest(getServer());
                String response = wikiController.execute(new it.therickys93.wikiapi.controller.Status(led[0].getKey()));
                Response status = Response.parseSuccess(response);
                return status;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response status) {
            super.onPostExecute(status);
            if (status == null) {
                Toast.makeText(MainActivity.this, "ERRORE", Toast.LENGTH_SHORT).show();
            } else {
                if (status.ok()) {
                    if (status.message().charAt(lightSpinner.getSelectedItemPosition()) == '1') {
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
