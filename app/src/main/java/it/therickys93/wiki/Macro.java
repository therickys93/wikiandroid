package it.therickys93.wiki;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.controller.Sendable;

/**
 * Created by Ricky on 1/26/19.
 */

public class Macro
{
    private String name;
    private List<Sendable> sendable;

    public Macro(String name, List<Sendable> sendable){
        this.name = name;
        this.sendable = new ArrayList<>(sendable);
    }

    public String getName() {
        return this.name;
    }

    public List<Sendable> getSendable(){
        return this.sendable;
    }

    public JsonObject toJson() {
        JsonObject macroObject = new JsonObject();
        macroObject.addProperty("name", this.name);
        JsonArray sendableArray = new JsonArray();
        for(int i = 0; i < this.sendable.size(); i++){
            JsonObject object = new JsonObject();
            object.addProperty("type", sendable.get(i).getType());
            object.addProperty("name", sendable.get(i).getLed().getName());
            object.addProperty("key", sendable.get(i).getLed().getKey());
            object.addProperty("position", sendable.get(i).getLed().getPosition());
            sendableArray.add(object);
        }
        macroObject.add("sendable", sendableArray);
        return macroObject;
    }

}
