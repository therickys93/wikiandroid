package it.therickys93.wiki;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

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
        this.leds = HouseUtils.getLedsFromHouse(MainActivity.house);
        this.adapter = new LedListAdapter(HouseConfigurationActivity.this, leds);
        this.listView.setAdapter(adapter);
        this.listView.setOnItemLongClickListener(this);
    }

    public void save(View view){
        HouseUtils.saveHouseToFile(MainActivity.getAppContext(), MainActivity.WIKI_FILENAME, MainActivity.house);
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

        dialogBuilder.setTitle("Nuovo Led");
        dialogBuilder.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = ledname.getText().toString();
                String key = ledkey.getText().toString();
                int position = ledPosition.getSelectedItemPosition();
                MainActivity.house.addLed(new Led(name, key, position));
                // reload the new list
                leds = HouseUtils.getLedsFromHouse(MainActivity.house);
                adapter = new LedListAdapter(HouseConfigurationActivity.this, leds);
                listView.setAdapter(adapter);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        if(leds == null){

        } else {
            this.leds.remove(index);
            this.adapter.updateLeds(this.leds);
            this.adapter.notifyDataSetChanged();
        }
        return true;
    }
}
