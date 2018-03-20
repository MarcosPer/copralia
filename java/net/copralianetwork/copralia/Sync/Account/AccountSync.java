package net.copralianetwork.copralia.Sync.Account;

import android.content.Context;

import net.copralianetwork.copralia.Sync.Account.Favs.ProdFav;

import java.util.Map;

/**
 * Created by marcos on 6/11/16.
 */

public class AccountSync {

    public static void SyncCloudMessage(Context ctx, Map<String, String> message) {
        String[] action = message.get("action").split("_");
        switch (action[1]){
            case "prodfav":
                ProdFav fav = new ProdFav(ctx);
                switch (action[2]){
                    case "add":
                        fav.addProduct(message);
                        break;
                    case "remove":
                        fav.removeProduct(message.get("prodID"));
                        break;
                    case "clear":
                        fav.removeAll();
                        break;
                }
                break;
            default:
                break;
        }
    }
}
