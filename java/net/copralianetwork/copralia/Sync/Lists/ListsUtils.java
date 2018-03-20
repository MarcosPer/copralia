package net.copralianetwork.copralia.Sync.Lists;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marcos on 28/08/16.
 */
public class ListsUtils {

    /*
        Añade la lista al sistema LOCAL, no registra en el servidor usuarios
     */
    public static void addList(Context ctx, String ID, String nombre, Integer created){
        ContentValues datos = new ContentValues(0);
        datos.put("listID", ID);
        datos.put("name", nombre);
        datos.put("created",created);
        datos.put("lastmod",created);
        ctx.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"lists"), datos);
    }


    public static void addMemberToList(Context ctx, String listID, String userID, Integer role, String name){
        ContentValues datos = new ContentValues(0);
        datos.put("listID", listID);
        datos.put("userID", userID);
        datos.put("name", name);
        datos.put("role", role);
        ctx.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+listID+"/members"), datos);
    }

    public static void addProductToList(Context ctx,String listID, Integer id, String nombre, String marca, String formato, Float precio, int superid, float cant, int added, String addedby){
        ContentValues datos = new ContentValues(0);
        datos.put("list", listID);
        datos.put("prodID", id);
        datos.put("nombre", nombre);
        datos.put("marca", marca);
        datos.put("formato", formato);
        datos.put("precio", precio);
        datos.put("superID", superid);
        datos.put("cant", cant);
        datos.put("added", added);
        datos.put("addedby", addedby);
        ctx.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+listID+"/prods"), datos);

    }
    /*public static void addProductToList(Context ctx,String listID, JSONObject prod){


        try {
            ContentValues datos = new ContentValues(0);
            datos.put("list", listID);
            datos.put("prodID", prod.getInt("ID"));
            datos.put("nombre", prod.getString("nombre"));
            datos.put("marca", prod.getString("marca"));
            datos.put("formato", prod.getString("formato"));
            datos.put("precio", prod.getDouble("precio"));
            datos.put("superID", prod.getInt("superID"));
            datos.put("cant", prod.getDouble("cantidad"));
            datos.put("added", prod.getInt("added"));
            datos.put("addedby", prod.getString("added_by"));
            ctx.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+listID+"/prods"), datos);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }*/

    public static void deleteList(Context ctx, String listID){
        ctx.getContentResolver().delete(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+listID+"/info"),null,null);
    }


    public static void updateProdList(Context ctx,JSONObject message) throws ListSync.UnSyncedList, JSONException {

        String listID = message.getString("listID");

        JSONObject prod = message.getJSONObject("prod");


        ContentValues datos = new ContentValues(0);
        //TODO: Aqui me he quedado, ya esta hecho el update del provider queda pasar la info del JSONObject prod al content values
        // if updated rows == 0
        int affectedRows = ctx.getContentResolver().update(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+listID+"/prods"),datos,null,null);

        //Si no ha actualizado nada, lista desincronizada.
        if(affectedRows == 0){
            throw new ListSync.UnSyncedList(message.getString("listID"));
        }



    }

    public static void deleteProdList(Context ctx){

    }



    public static class ListInfo{
        public String ID;
        public String Name;

        ListInfo(String ID, String name){
            this.ID = ID;
            this.Name = name;
        }
        
        public static ListInfo getListInfo(String ID){
            return new ListInfo("","");
        }
    }



}
