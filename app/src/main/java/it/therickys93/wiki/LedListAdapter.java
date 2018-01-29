package it.therickys93.wiki;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import it.therickys93.wikiapi.model.Led;

/**
 * Created by Ricky on 1/29/18.
 */

public class LedListAdapter extends BaseAdapter {

    private Activity activity;
    private List<Led> leds;
    private static LayoutInflater inflater=null;
    public LedListAdapter(Activity a, List<Led> leds) {
         this.activity = a;
         this.leds = leds;
         this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if(leds == null || leds.size() == 0){
            return 1;
        }
        return leds.size();
    }

    public void updateProdotti(List<Led> prodotti){
        this.leds = prodotti;
    }

    public Led getItem(int position) {
        return leds.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null) {
            System.out.println("view == null");
            vi = this.inflater.inflate(R.layout.list_layout, null);
        }
        TextView name = (TextView)vi.findViewById(R.id.ledname);
        TextView key = (TextView)vi.findViewById(R.id.ledkey);
        TextView pos = (TextView)vi.findViewById(R.id.ledposition);
        if(leds == null || leds.size() == 0){
            name.setText("Nessun led trovato");
            key.setText("");
            pos.setText("");
        } else {
            Led led = leds.get(position);
            name.setText(led.getName());
            key.setText("chiave: " + led.getKey());
            pos.setText("# " + led.getPosition());
        }
        return vi;
    }
}


