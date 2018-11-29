package it.therickys93.wiki;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ricky on 1/29/18.
 */

public class FileUtils {

    public static boolean isFileAlreadyCreated(Context context, String filename)
    {
        File file = getFile(context, filename);
        return file.exists();
    }

    private static File getFile(Context context, String filename){
        File directory = context.getExternalFilesDir(null);
        if(!directory.exists())
            directory.mkdirs();
        return new File(directory, filename);
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String readFromFile(Context context, String filename){
        File file = getFile(context, filename);
        String response;
        try {
            FileInputStream fin = new FileInputStream(file);
            response = FileUtils.convertStreamToString(fin);
            fin.close();
        } catch (Exception e){
            response = null;
        }
        return response;
    }

    public static boolean saveToFile(Context context, String filename, String content)
    {
        return saveToFile(context, filename, content, false);
    }

    public static boolean appendToFile(Context context, String filename, String content)
    {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String toBeSaved = "[" + timeStamp + "] " + content + System.lineSeparator();
        return saveToFile(context, filename, toBeSaved, true);
    }

    private static boolean saveToFile(Context context, String fileName, String content, boolean appending){
        File file = FileUtils.getFile(context, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, appending);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
            return true;
        } catch(Exception e){
            return false;
        }
    }

}
