package it.therickys93.wiki;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.controller.Off;
import it.therickys93.wikiapi.controller.On;
import it.therickys93.wikiapi.controller.OpenClose;
import it.therickys93.wikiapi.controller.Sendable;
import it.therickys93.wikiapi.model.Led;

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

    private static List<Macro> parseMacroFromString(String content){
        List<Macro> macros = new ArrayList<>();
        List<Sendable> sendables = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(content).getAsJsonArray();
        JsonObject macroObject;
        String macroName;
        JsonArray sendableArray;
        JsonObject sendableObject;
        String name;
        String type;
        String key;
        int position;
        for(int i = 0; i < array.size(); i++){
            macroObject = array.get(i).getAsJsonObject();
            macroName = macroObject.get("name").getAsString();
            sendableArray = macroObject.get("sendable").getAsJsonArray();
            sendables.removeAll(sendables);
            for(int j = 0; j < sendableArray.size(); j++) {
                sendableObject = sendableArray.get(j).getAsJsonObject();
                name = sendableObject.get("name").getAsString();
                type = sendableObject.get("type").getAsString();
                key = sendableObject.get("key").getAsString();
                position = sendableObject.get("position").getAsInt();
                switch(type) {
                    case "Accendi":
                        sendables.add(new On(new Led(name, key, position)));
                        break;
                    case "Spegni":
                        sendables.add(new Off(new Led(name, key, position)));
                        break;
                    case "Apri/Chiudi":
                        sendables.add(new OpenClose(new Led(name, key, position)));
                        break;
                    default:
                        break;
                }
            }
            macros.add(new Macro(macroName, sendables));
        }
        return macros;
    }

    public static List<Macro> loadMacrosFromFile(Context context, String filename){
        if(FileUtils.isFileAlreadyCreated(context, filename)){
            String content = FileUtils.readFromFile(context, filename);
            return MacroUtils.parseMacroFromString(content);
        } else {
            List<Macro> macros = new ArrayList<>();
            FileUtils.saveToFile(context, filename, "[]");
            return macros;
        }
    }

}
