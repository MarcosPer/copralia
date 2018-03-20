package net.copralianetwork.copralia.Sync.Account.Favs;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Account.AccountProvider;
import net.copralianetwork.copralia.Sync.Lists.ListsProvider;
import net.copralianetwork.copralia.Sync.Lists.ListsUtils;
import net.copralianetwork.copralia.Sync.Lists.Models.List;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marcos on 12/11/16.
 */

public class FavsSync {
    public static void RemoveFavsSync(final Context ctx){

        //TODO: ELIMINAR DB LOCAL APARTADO LISTS
        APIRequest request = new APIRequest(ctx,
                Request.Method.GET,
                "http://192.168.1.218:8080/prod/favs",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ProdFav prodFav = new ProdFav(ctx);
                            prodFav.removeAll();
                            JSONArray favoritos = response.getJSONArray("favs");
                            if(favoritos.length() > 0){
                                for (int i = 0; i < favoritos.length(); i++) {
                                    JSONObject favorito = favoritos.getJSONObject(i);
                                    prodFav.addProduct(favorito);
                                }
                                Toast.makeText(ctx,"Favoritos sincronizados",Toast.LENGTH_SHORT).show();
                            }
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
}
