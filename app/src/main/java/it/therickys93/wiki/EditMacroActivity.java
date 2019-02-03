package it.therickys93.wiki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.controller.Off;
import it.therickys93.wikiapi.controller.On;
import it.therickys93.wikiapi.controller.OpenClose;
import it.therickys93.wikiapi.controller.Sendable;
import it.therickys93.wikiapi.model.House;
import it.therickys93.wikiapi.model.Led;

public class EditMacroActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private List<Macro> macros;

    private List<Sendable> sendables;
    private Macro macro;
    private EditText editText;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private Spinner lights_spinner;
    private Spinner spinner_actions;
    private House house;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_macro);
        editText = (EditText)findViewById(R.id.edit_macro_name);
        listView = (ListView)findViewById(R.id.macro_sendable);
        lights_spinner = (Spinner)findViewById(R.id.spinner_light);
        spinner_actions = (Spinner)findViewById(R.id.spinner_actions);
        listView.setOnItemLongClickListener(this);

        this.house = HouseUtils.loadHouseFromFile(MainActivity.getAppContext(), Wiki.Controller.DEFAULT_FILENAME);

        this.macros = MacroUtils.loadMacrosFromFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME);
        Bundle b = getIntent().getExtras();
        index = this.macros.size();
        if(b != null) {
            index = b.getInt("id");
            this.macro = this.macros.get(index);
            this.macros.remove(index);
        } else {
            index = this.macros.size();
            this.macro = new Macro("", new ArrayList<Sendable>());
        }
        updateUI();
    }

    private List<String> getLedNames(){
        List<String> names = new ArrayList<>();
        List<Led> leds = HouseUtils.getLedsFromHouse(this.house);
        if(leds.size() > 0) {
            for (Led l : leds) {
                names.add(l.getName());
            }
        } else {
            names.add("Nessun Accessorio trovato");
        }
        return names;
    }

    private void populateSpinner(){
        List<String> leds = getLedNames();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, leds);
        this.lights_spinner.setAdapter(dataAdapter);
    }

    private void updateUI(){
        editText.setText(this.macro.getName());
        List<String> list = new ArrayList<>();
        this.sendables = this.macro.getSendable();
        for(int i = 0; i < this.sendables.size(); i++){
            list.add(this.sendables.get(i).getType() + " " + this.sendables.get(i).getLed().getName());
        }
        this.arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(this.arrayAdapter);
        populateSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_macro_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_macro:
                save();
                return true;
            case R.id.cancel_macro:
                cancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save() {
        this.macro.setName(this.editText.getText().toString());
        this.macro.setSendable(this.sendables);
        this.macros.add(index, this.macro);
        MacroUtils.saveMacrosToFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME, this.macros);
        finish();
    }

    private Led getLed() {
        if(this.house.getLedCount() > 0) {
            return this.house.getLedAt(this.lights_spinner.getSelectedItemPosition());
        } else {
            return null;
        }
    }

    private String getAction() {
        List<String> array = new ArrayList<>();
        array.add("Accendi");
        array.add("Spegni");
        array.add("Apri");
        array.add("Chiudi");
        return array.get(this.spinner_actions.getSelectedItemPosition());
    }

    public void addSendable(View view){
        Led led = getLed();
        String action = getAction();
        switch (action){
            case "Accendi":
                this.sendables.add(new On(led));
                break;
            case "Spegni":
                this.sendables.add(new Off(led));
                break;
            case "Apri":
                this.sendables.add(new OpenClose(led));
                break;
            case "Chiudi":
                this.sendables.add(new OpenClose(led));
                break;
            default:
                break;
        }
        this.arrayAdapter.notifyDataSetChanged();
        updateUI();
    }

    public void cancel() {
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(this.sendables != null && this.sendables.size() > 0){
            this.sendables.remove(i);
            this.arrayAdapter.notifyDataSetChanged();
            updateUI();
        }
        return true;
    }
}
