package net.copralianetwork.copralia.Sync.Lists;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by marcos on 5/10/16.
 */

public class ProdUtils {


    public static ContentValues getContentValues(Map<String,String> message) throws NullPointerException{
        ContentValues datos = new ContentValues(0);

        if (!message.containsKey("prodID")) {
            return null;
        }

        datos.put("prodID", Integer.valueOf(message.get("prodID")));

        if (message.containsKey("prod_nombre")){
            datos.put("nombre", message.get("prod_nombre"));
        }
        if (message.containsKey("prod_marca")){
            datos.put("marca", message.get("prod_marca"));
        }
        if (message.containsKey("prod_formato")) {
            datos.put("formato", message.get("prod_formato"));
        }
        if(message.containsKey("prod_precio")){
            datos.put("precio", Double.valueOf(message.get("prod_precio")));
        }
        if(message.containsKey("prod_storeID")){
            datos.put("storeID", message.get("prod_storeID"));
        }
        if(message.containsKey("prod_cant")){
            datos.put("cant", Double.valueOf(message.get("prod_cant")));
        }
        if(message.containsKey("prod_added")){
            datos.put("added", message.get("prod_added"));
        }
        if(message.containsKey("prod_buy")){
            datos.put("buy", message.get("prod_buy"));
        }
        if(message.containsKey("prod_added_by")){
            datos.put("addedby", Integer.valueOf(message.get("prod_added_by")));
        }

        return datos;

    }
    public static ContentValues getContentValuesObject(JSONObject message) {
        ContentValues datos = new ContentValues(0);

        if (!message.has("prodID")) {
            return null;
        }
        try{
            datos.put("prodID", message.getInt("prodID"));

            if (message.has("nombre")){
                datos.put("nombre", message.getString("nombre"));
            }
            if (message.has("marca")){
                datos.put("marca", message.getString("marca"));
            }
            if (message.has("formato")) {
                datos.put("formato", message.getString("formato"));
            }
            if(message.has("precio")){
                datos.put("precio", message.getDouble("precio"));
            }
            if(message.has("superID")){
                datos.put("storeID", message.getInt("superID"));
            }
            if(message.has("cantidad")){
                datos.put("cant", message.getDouble("cantidad"));
            }
            if(message.has("added")){
                datos.put("added", message.getString("added"));
            }
            if(message.has("buy")){
                datos.put("buy", message.getInt("buy"));
            }
            if(message.has("addedby")){
                datos.put("addedby", message.getString("added_by"));
            }
            return datos;
        }catch (JSONException e){
            return null;
        }
    }

    /* Tabla productos */
    public static ContentValues getProdTableValues(ContentValues in){
        ContentValues aux = new ContentValues(0);
        aux.put("ID", in.getAsInteger("prodID"));
        aux.put("nombre", in.getAsString("nombre"));
        aux.put("marca", in.getAsString("marca"));
        aux.put("formato", in.getAsString("formato"));
        return aux;
    }

    public static ContentValues getListProdTableValues(ContentValues in){
        ContentValues aux = new ContentValues(0);
        aux.put("prodID", in.getAsInteger("prodID"));
        aux.put("precio", in.getAsDouble("precio"));
        aux.put("storeID", in.getAsString("storeID"));
        aux.put("formato", in.getAsString("formato"));
        return aux;
    }
}
