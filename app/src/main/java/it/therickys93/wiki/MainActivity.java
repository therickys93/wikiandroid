package it.therickys93.wiki;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import it.therickys93.wikiapi.Off;
import it.therickys93.wikiapi.On;
import it.therickys93.wikiapi.Response;
import it.therickys93.wikiapi.WikiController;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER = "http://192.168.15.12";
    private static final String KEY = "arduino";
    private static final int LED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View view){
        new OnButtonAsyncTask().execute();
    }

    public void offButtonClicked(View view){
        new OffButtonAsyncTask().execute();
    }

    private class OnButtonAsyncTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                WikiController wikiController = new WikiController(SERVER);
                String response = wikiController.execute(new On(KEY, LED));
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
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OffButtonAsyncTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                WikiController wikiController = new WikiController(SERVER);
                String response = wikiController.execute(new Off(KEY, LED));
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
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
