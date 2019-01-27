package it.therickys93.wiki;

import android.content.Context;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky on 1/27/19.
 */

public class MacroUtils {

    public static boolean saveMacrosToFile(Context context, String filename, List<Macro> macroList){
        JsonArray list = new JsonArray();
        for(int i = 0; i< macroList.size(); i++){
            list.add(macroList.get(i).toJson());
        }
        return FileUtils.saveToFile(context, filename, list.toString());
    }

    public static List<Macro> loadMacrosFromFile(Context context, String filename){
        if(FileUtils.isFileAlreadyCreated(context, filename)){
            String content = FileUtils.readFromFile(context, filename);
            // TODO: implementare parsing
            return null;
        } else {
            List<Macro> macros = new ArrayList<>();
            FileUtils.saveToFile(context, filename, "[]");
            return macros;
        }
    }

}
