package net.copralianetwork.copralia.Sync.Lists;

import android.content.Context;

import net.copralianetwork.copralia.Sync.Lists.Models.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marcos on 9/09/16.
 */
public class ProdSync {
/*
    public static void ProdSync(Context ctx, JSONObject message) throws JSONException, ListSync.UnSyncedList {

        //Obtenemos y separamos el action
        String[] action = message.getString("action").split("_");

        List lista = new List(message.getString("listID"));

        switch (action[2]) {
            case "add":
                lista.addProduct(ctx,message.getJSONObject("prod"));

                //ListsUtils.addProductToList(ctx, message.getString("listID"), message.getJSONObject("prod"));
                break;
            case "update":
                ListsUtils.updateProdList(ctx, message);
                break;
        }

    }
*/

}
