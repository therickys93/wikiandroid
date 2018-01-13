package it.therickys93.wiki;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.therickys93.wikiapi.Off;
import it.therickys93.wikiapi.On;
import it.therickys93.wikiapi.Reset;
import it.therickys93.wikiapi.Response;
import it.therickys93.wikiapi.WikiController;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER = "http://192.168.15.12";
    private static final String KEY = "arduino";

    private EditText serverEditText;
    private Spinner  lightSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverEditText = (EditText) findViewById(R.id.server_edit_text);
        lightSpinner = (Spinner) findViewById(R.id.spinner1);

        serverEditText.setText(SERVER);

    }

    private String getServer(){
        return this.serverEditText.getText().toString();
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
                String response = wikiController.execute(new On(KEY, getLed()));
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
                String response = wikiController.execute(new Off(KEY, getLed()));
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
                String response = wikiController.execute(new Reset(KEY));
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
                String response = wikiController.execute(new it.therickys93.wikiapi.Status(KEY));
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
