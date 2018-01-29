package it.therickys93.wiki;

import android.content.Context;
import it.therickys93.wikiapi.model.House;

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

}
