package net.copralianetwork.copralia.Sync.Lists;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marcos on 5/10/16.
 */

public class MemberUtils {


    public static ContentValues getContentValues(JSONObject prod) {
        ContentValues datos = new ContentValues(0);

        if (!prod.has("userID")) {
            return null;
        }
        try{
            datos.put("userID", prod.getInt("userID"));

            if (prod.has("name")){
                datos.put("name", prod.getString("name"));
            }
            if (prod.has("lastname")){
                datos.put("lastname", prod.getString("lastname"));
            }
            if (prod.has("role")) {
                datos.put("role", prod.getInt("formato"));
            }


            return datos;
        }catch (JSONException e){
            return null;
        }
    }
}
