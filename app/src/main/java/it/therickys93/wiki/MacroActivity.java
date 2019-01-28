package it.therickys93.wiki;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.controller.Off;
import it.therickys93.wikiapi.controller.On;
import it.therickys93.wikiapi.controller.Sendable;
import it.therickys93.wikiapi.controller.WikiController;
import it.therickys93.wikiapi.model.Led;

public class MacroActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private List<Macro> macros;
    private ListView listView;
    private MacroListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.macro_activity);
        this.macros = new ArrayList<>();

        this.listView = (ListView) findViewById(R.id.listmacro);
        this.listView.setOnItemLongClickListener(this);
        this.listView.setOnItemClickListener(this);

        this.macros = MacroUtils.loadMacrosFromFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME);
        this.listAdapter = new MacroListAdapter(MacroActivity.this, macros);
        this.listView.setAdapter(this.listAdapter);
    }

    private void updateUI() {
        this.listAdapter = new MacroListAdapter(MacroActivity.this, macros);
        this.listView.setAdapter(this.listAdapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        if(macros != null){
            this.macros.remove(index);
            this.listAdapter.updateMacros(this.macros);
            MacroUtils.saveMacrosToFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME, this.macros);
            this.listAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private void addMacro() {
        List<Sendable> sendable = new ArrayList<>();
        sendable.add(new On(new Led("pippo", "prova", 0)));
        sendable.add(new Off(new Led("pippo", "prova", 1)));
        sendable.add(new On(new Led("pippo", "prova", 2)));
        sendable.add(new Off(new Led("pippo", "prova", 3)));
        sendable.add(new On(new Led("pippo", "prova", 4)));
        sendable.add(new Off(new Led("pippo", "prova", 5)));
        sendable.add(new On(new Led("pippo", "prova", 6)));
        sendable.add(new Off(new Led("pippo", "prova", 7)));
        this.macros.add(new Macro("test", sendable));
        this.listAdapter.updateMacros(this.macros);
        MacroUtils.saveMacrosToFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME, this.macros);
        this.listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.macro_menu, menu);
        return true;
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
            case R.id.macro:
                intent = new Intent(this, MacroActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.addMacro:
                addMacro();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(macros != null){
            Macro macro = this.macros.get(i);
            new ExecuteAsyncTask().execute(macro.getSendable());
        }
    }

    private class ExecuteAsyncTask extends AsyncTask<List<Sendable>, Void, Boolean> {

        public ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MacroActivity.this);
            dialog.setMessage("operazione in corso ...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(List<Sendable>[] lists) {
            List<Sendable> pippo = lists[0];
            SharedPreferences settings = getSharedPreferences(Wiki.Controller.Settings.NAME, 0);
            String server = settings.getString(Wiki.Controller.Settings.SERVER, Wiki.Controller.DEFAULT_URL);
            WikiController wikiController = new WikiController(server);
            try {
                for (int i = 0; i < pippo.size(); i++) {
                    wikiController.execute(pippo.get(i));
                    Thread.sleep(1500);
                }
                return true;
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(aBoolean){
                Toast.makeText(MacroActivity.this, Wiki.Controller.Response.OK, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MacroActivity.this, Wiki.Controller.Response.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
