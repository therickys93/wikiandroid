package it.therickys93.wiki;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import it.therickys93.wikiapi.model.House;
import it.therickys93.wikiapi.model.Led;

/**
 * Created by Ricky on 1/29/18.
 */

public class HouseUtils {

    public static boolean saveHouseToFile(Context context, String filename, House house)
    {
        String houseString = house.toJson().toString();
        return FileUtils.saveToFile(context, filename, houseString);
    }

    public static House loadHouseFromFile(Context context, String filename)
    {
        if(FileUtils.isFileAlreadyCreated(context, filename)) {
            String fileContent = FileUtils.readFromFile(context, filename);
            return House.fromJson(fileContent);
        } else {
            House house = new House();
            FileUtils.saveToFile(context, filename, house.toJson().toString());
            return house;
        }
    }

    public static List<Led> getLedsFromHouse(House house){
        List<Led> leds = new ArrayList<>();
        for(int index = 0; index < house.getLedCount(); index++){
            leds.add(house.getLedAt(index));
        }
        return leds;
    }

}
