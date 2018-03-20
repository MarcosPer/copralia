package net.copralianetwork.copralia.Sync.Lists;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;

import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Lists.Models.List;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcos on 29/08/16.
 */
public class ListSync {

    public static void RemoteListSync(final Context ctx, String listID){

        Log.d("MEITAG","JEJEDO");


        APIRequest request = new APIRequest(ctx,
                Request.Method.GET,
                "http://192.168.1.218:8080/list/"+listID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SyncList(ctx,response);
                        } catch (Exception e) {
                            Log.d("MEITAG","ERROR1" + e.toString());
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("MEITAG","ERROR2" + error.toString());

                        Toast.makeText(ctx, "Tu puta madre", Toast.LENGTH_LONG).show();

                    }
                }
        );

        VolleySingleton.getInstance(ctx).addToRequestQueue(request);

    }
    public static void RemoteListsSync(final Context ctx){
        //TODO: ELIMINAR DB LOCAL APARTADO LISTS
        APIRequest request = new APIRequest(ctx,
                Request.Method.GET,
                "http://192.168.1.218:8080/lists",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray listas = response.getJSONArray("lists");

                            for (int i = 0; i < listas.length(); i++) {
                                JSONObject lista = listas.getJSONObject(i);
                                SyncList(ctx,lista);
                            }

                            Toast.makeText(ctx,"Listas sincronizadas",Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(ctx, ctx.getText(R.string.error_conexion), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        VolleySingleton.getInstance(ctx).addToRequestQueue(request);



    }
    public static void SyncList(Context ctx,JSONObject lista){
        //Agregar nueva información
        try {

            String listID = lista.getJSONObject("info").getString("listID");
            List list = new List(listID,ctx);

            /* Eliminar la lista localmente */
            list.removeList();

            /* Agregamos info de la nueva lista */
            JSONObject info = lista.getJSONObject("info");
            ListsUtils.addList(ctx,info.getString("listID"),info.getString("name"),info.getInt("created"));

            /* Agregamos info de todos los miembros */
            if(!lista.isNull("members")) {
                JSONArray members = lista.getJSONArray("members");
                for (int i = 0; i < members.length(); i++) {
                    JSONObject member = members.getJSONObject(i);
                    ListsUtils.addMemberToList(ctx, listID, member.getString("userID"), member.getInt("role"), member.getString("name"));
                }
            }
            /* Agregamos info de todos los productos */
            if(!lista.isNull("prods")){
                JSONArray prods = lista.getJSONArray("prods");
                for (int i = 0; i < prods.length(); i++) {
                    JSONObject prod = prods.getJSONObject(i);
                    list.addProduct(prod);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //JSONArray members = lista.getJSONObject("info");
    }

    public static void SyncCloudMessage(Context ctx, Map<String,String> message) throws NullPointerException {

        try {
            List lista = new List(message.get("listID"),ctx);

            String[] action = message.get("action").split("_");

            switch (action[1]) {
                //list_sync
                case "sync":
                    RemoteListSync(ctx, lista.getListID());
                    break;
                case "add":
                    RemoteListSync(ctx, lista.getListID());
                    //TODO: Notificación nueva lista
                    break;
                case "clear":
                    lista.clear();
                    break;
                case "remove":
                    lista.removeList();
                    break;
                case "prod":
                    //list_prod_x

                    switch (action[2]) {
                        //list_prod_add
                        case "add":
                            lista.addProduct(message);
                            break;
                        //list_prod_update
                        case "update":
                            lista.updateProduct(message);
                            break;
                        //list_prod_remove
                        case "remove":
                            lista.removeProduct(message);
                            break;
                    }
                    break;
                case "member":
                    JSONObject member = null;
                    switch (action[2]) {
                        //list_member_add
                        case "add":
                            lista.addMember(member);
                            break;
                        //list_member_update
                        case "update":
                            //lista.updateMember(member);
                            break;
                        //list_member_remove
                        case "remove":
                            //lista.removeMember(member);
                            break;
                    }
                    break;
            }
        }catch (UnSyncedList e){
            /* Lista desincronizada, forzar sincronizacion */
            RemoteListSync(ctx,e.getListID());
        }

    }

    public static class UnSyncedList extends Exception{
        private String listID;

        public UnSyncedList(String listID){
            this.listID = listID;

        }

        public String getListID() {
            return listID;
        }


    }

}
