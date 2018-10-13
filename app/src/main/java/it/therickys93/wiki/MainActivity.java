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

        MainActivity.house = HouseUtils.loadHouseFromFile(MainActivity.getAppContext(), Wiki.Controller.DEFAULT_FILENAME);

        populateServer();

        serverEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        serverEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(serverEditText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    // save the new value
                    SharedPreferences settings = getSharedPreferences(Wiki.Controller.Settings.NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Wiki.Controller.Settings.SERVER, serverEditText.getText().toString());
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
            names.add("Nessun Accessorio trovato");
        }
        return names;
    }

    private void populateServer(){
        SharedPreferences settings = getSharedPreferences(Wiki.Controller.Settings.NAME, 0);
        String server = settings.getString(Wiki.Controller.Settings.SERVER, Wiki.Controller.DEFAULT_URL);
        serverEditText.setText(server);
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
        populateServer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.main:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.house:
                intent = new Intent(this, HouseConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.ai:
                intent = new Intent(this, AIActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void qrcodeScanner(View view){
        Intent intent = new Intent(this, QRCodeScannerActivity.class);
        intent.putExtra(Wiki.QRCode.SETTINGS, Wiki.Controller.Settings.NAME);
        intent.putExtra(Wiki.QRCode.URL, Wiki.Controller.Settings.SERVER);
        startActivity(intent);
    }

    private String getServer(){
        return this.serverEditText.getText().toString();
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

    public void openButtonClicked(View view){
        new OpenCloseAsyncTask().execute(getLed());
    }

    public void closeButtonClicked(View view){
        new OpenCloseAsyncTask().execute(getLed());
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
                WikiController wikiController = new WikiController(getServer());
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
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OffButtonAsyncTask extends AsyncTask<Led, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Led... led) {
            try {
                WikiController wikiController = new WikiController(getServer());
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
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class OpenCloseAsyncTask extends AsyncTask<Led, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Led... led) {
            try {
                WikiController wikiController = new WikiController(getServer());
                String response = wikiController.execute(new OpenClose(led[0]));
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
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ResetButtonAsyncTask extends AsyncTask<Led, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Led... led) {
            try {
                WikiController wikiController = new WikiController(getServer());
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
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class StatusButtonAsyncTask extends AsyncTask<Led, Void, Response> {

        @Override
        protected Response doInBackground(Led... led) {
            try {
                WikiController wikiController = new WikiController(getServer());
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
                Toast.makeText(MainActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            } else {
                if (status.ok()) {
                    if (status.message().charAt(lightSpinner.getSelectedItemPosition()) == '1') {
                        Toast.makeText(MainActivity.this, Wiki.Controller.Response.ON, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, Wiki.Controller.Response.OFF, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
