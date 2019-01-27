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

public class MacroActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

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

        List<Sendable> list = new ArrayList<>();
        list.add(new On("prova", 2));
        list.add(new On("prova", 4));
        list.add(new On("prova", 5));
        list.add(new On("prova", 6));
        list.add(new On("prova", 7));
        list.add(new On("prova", 3));
        list.add(new On("prova", 1));
        list.add(new On("prova", 0));
        macros.add(new Macro("Accendi tutto", list));

        List<Sendable> list1 = new ArrayList<>();
        list1.add(new Off("prova", 2));
        list1.add(new Off("prova", 4));
        list1.add(new Off("prova", 5));
        list1.add(new Off("prova", 6));
        list1.add(new Off("prova", 7));
        list1.add(new Off("prova", 3));
        list1.add(new Off("prova", 1));
        list1.add(new Off("prova", 0));
        macros.add(new Macro("Spegni tutto", list1));

        this.listAdapter = new MacroListAdapter(MacroActivity.this, macros);
        this.listView.setAdapter(this.listAdapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        if(macros != null){
            Macro macro = this.macros.get(index);
            new ExecuteAsyncTask().execute(macro.getSendable());
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
            default:
                return super.onOptionsItemSelected(item);
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
