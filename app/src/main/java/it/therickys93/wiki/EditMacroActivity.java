package it.therickys93.wiki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

public class EditMacroActivity extends AppCompatActivity {

    private List<Macro> macros;
    private Macro macro;
    private EditText editText;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_macro);
        editText = (EditText)findViewById(R.id.edit_macro_name);

        this.macros = MacroUtils.loadMacrosFromFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME);
        Bundle b = getIntent().getExtras();
        index = 0;
        if(b != null)
            index = b.getInt("id");
        this.macro = this.macros.get(index);
        this.macros.remove(index);
        updateUI();
    }

    private void updateUI(){
        editText.setText(this.macro.getName());
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
                saveMacro();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveMacro() {
        this.macro.setName(this.editText.getText().toString());
        this.macros.add(index, this.macro);
        MacroUtils.saveMacrosToFile(MainActivity.getAppContext(), Wiki.Controller.MACRO_FILENAME, this.macros);
        finish();
    }

}
