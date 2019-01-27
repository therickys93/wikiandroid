package it.therickys93.wiki;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky on 1/27/19.
 */

public class MacroUtils {

    public static boolean saveMacrosToFile(Context context, String filename, List<Macro> macroList){
        // TODO: da implementare
        String toBeSaved = "[]";
        return FileUtils.saveToFile(context, filename, toBeSaved);
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
