package it.therickys93.wiki;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ricky on 1/27/19.
 */

public class MacroListAdapter extends BaseAdapter {

    private Activity activity;
    private List<Macro> macros;
    private static LayoutInflater inflater=null;
    public MacroListAdapter(Activity activity, List<Macro> macros) {
        this.activity = activity;
        this.macros = macros;
        this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(macros == null || macros.size() == 0){
            return 1;
        }
        return macros.size();
    }

    public void updateMacros(List<Macro> macros){
        this.macros = macros;
    }

    @Override
    public Macro getItem(int i) {
        return this.macros.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null) {
            System.out.println("view == null");
            vi = this.inflater.inflate(R.layout.macro_list, null);
        }
        TextView name = (TextView)vi.findViewById(R.id.macroname);
        Button button = (Button)vi.findViewById(R.id.macrobutton);
        if(macros == null || macros.size() == 0){
            name.setText("Nessuna macro trovata");
            button.setVisibility(View.GONE);
        } else {
            Macro macro = macros.get(i);
            name.setText(macro.getName());
            button.setVisibility(View.VISIBLE);
            button.setTag(i);
        }
        return vi;
    }
}
