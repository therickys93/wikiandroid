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

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.controller.Sendable;

public class EditMacroActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private List<Macro> macros;

    private List<Sendable> sendables;
    private Macro macro;
    private EditText editText;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_macro);
        editText = (EditText)findViewById(R.id.edit_macro_name);
        listView = (ListView)findViewById(R.id.macro_sendable);
        listView.setOnItemLongClickListener(this);

        this.macros = MacroUtils.loadMacrosFromFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME);
        Bundle b = getIntent().getExtras();
        index = this.macros.size();
        if(b != null) {
            index = b.getInt("id");
            this.macro = this.macros.get(index);
        } else {
            index = this.macros.size();
            this.macro = new Macro("", new ArrayList<Sendable>());
        }
        this.macros.remove(index);
        updateUI();
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
