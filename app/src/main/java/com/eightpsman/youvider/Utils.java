package com.eightpsman.youvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Youvider
 * Created by 8psman on 1/24/2015.
 * Email: 8psman@gmail.com
 */
public class Utils {

    public static String getStringFromStream(InputStream inputStream){
        StringWriter writer = new StringWriter();
        char[] buffer = new char[1024];
        int bytes;
        try{
            InputStreamReader reader = new InputStreamReader(inputStream);
            while ((bytes = reader.read(buffer)) != -1){
                writer.write(buffer, 0, bytes);
            }
        }catch (IOException ex){
            return null;
        }
        return writer.toString();
    }

    public static String getSafeFileNameFor(String name){
        if (name == null) return null;
        String newName = "";
        for (int i=0; i<name.length(); i++){
            char c = name.charAt(i);
            boolean isValidCharacter =
                    Character.isLetterOrDigit(c) ||
                    c == ' ' || c == '.' || c == '-' || c == '_' || c == '\'';
                    ;
            if (isValidCharacter)
                newName += c;
            else newName += "_";
        }
        return newName;
    }
}
