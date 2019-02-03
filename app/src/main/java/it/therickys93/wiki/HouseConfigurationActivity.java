package it.therickys93.wiki;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import it.therickys93.wikiapi.controller.Download;
import it.therickys93.wikiapi.controller.Init;
import it.therickys93.wikiapi.controller.Response;
import it.therickys93.wikiapi.controller.Upload;
import it.therickys93.wikiapi.controller.WikiController;
import it.therickys93.wikiapi.model.House;
import it.therickys93.wikiapi.model.Led;

public class HouseConfigurationActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private ListView listView;
    private List<Led> leds;
    private LedListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_configuration);

        // ledlist
        this.listView = (ListView) findViewById(R.id.ledlist);
        this.listView.setOnItemLongClickListener(this);
        updateUI();
    }

    public void save(View view){
        HouseUtils.saveHouseToFile(MainActivity.getAppContext(), Wiki.Controller.DEFAULT_FILENAME, MainActivity.house);
        Toast.makeText(MainActivity.getAppContext(), "Salvato", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void add(View view){
        showDialog();
    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText ledname = (EditText) dialogView.findViewById(R.id.editledname);
        final EditText ledkey  = (EditText) dialogView.findViewById(R.id.editledkey);
        final Spinner  ledPosition = (Spinner) dialogView.findViewById(R.id.spinnerlights);

        dialogBuilder.setTitle("Nuovo Accessorio");
        dialogBuilder.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = ledname.getText().toString();
                String key = ledkey.getText().toString();
                int position = ledPosition.getSelectedItemPosition();
                MainActivity.house.addLed(new Led(name, key, position));
                new InitButtonAsyncTask().execute(key);
                // reload the new list
                updateUI();
            }
        });
        dialogBuilder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // non fare nulla
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void updateUI() {
        leds = HouseUtils.getLedsFromHouse(MainActivity.house);
        adapter = new LedListAdapter(HouseConfigurationActivity.this, leds);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        if(leds != null && leds.size() > 0){
            this.leds.remove(index);
            this.adapter.updateLeds(this.leds);
            MainActivity.house.removeLedAt(index);
            this.adapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.house_configuration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                new UploadAsyncTask().execute();
                return true;
            case R.id.download:
                new DownloadAsyncTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class InitButtonAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... key) {
            try {
                SharedPreferences settings = getSharedPreferences(Wiki.Controller.Settings.NAME, 0);
                String server = settings.getString(Wiki.Controller.Settings.SERVER, Wiki.Controller.DEFAULT_URL);
                WikiController wikiController = new WikiController(server);
                String response = wikiController.execute(new Init(key[0]));
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
                FileUtils.appendToFile(MainActivity.getAppContext(), Wiki.Controller.LOG_FILENAME, "Init key: OK");
            } else {
                FileUtils.appendToFile(MainActivity.getAppContext(), Wiki.Controller.LOG_FILENAME, "Init key: ERRORE");
            }
        }
    }

    private class UploadAsyncTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SharedPreferences settings = getSharedPreferences(Wiki.Controller.Settings.NAME, 0);
                String server = settings.getString(Wiki.Controller.Settings.SERVER, Wiki.Controller.DEFAULT_URL);
                WikiController request = new WikiController(server);
                String response = request.execute(new Upload(MainActivity.house));
                Response res = Response.parseSuccess(response);
                return res.ok();
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(HouseConfigurationActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HouseConfigurationActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadAsyncTask extends AsyncTask<Void, Void, House>{

        @Override
        protected House doInBackground(Void... voids) {
            try {
                SharedPreferences settings = getSharedPreferences(Wiki.Controller.Settings.NAME, 0);
                String server = settings.getString(Wiki.Controller.Settings.SERVER, Wiki.Controller.DEFAULT_URL);
                WikiController request = new WikiController(server);
                String response = request.execute(new Download());
                return House.fromJson(response);
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(House house) {
            super.onPostExecute(house);
            if(house == null){
                Toast.makeText(HouseConfigurationActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            } else {
                MainActivity.house = house;
                updateUI();
                Toast.makeText(HouseConfigurationActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
