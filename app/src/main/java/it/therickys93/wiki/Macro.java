package it.therickys93.wiki;

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
        this.sendable = sendable;
    }

    public String getName() {
        return this.name;
    }

    public List<Sendable> getSendable(){
        return this.sendable;
    }
}
