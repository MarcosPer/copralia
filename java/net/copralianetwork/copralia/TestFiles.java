package net.copralianetwork.copralia;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import net.copralianetwork.copralia.Activities.ProdFav.ProdFavActivity;
import net.copralianetwork.copralia.Activities.Scanner.ScannerActivity;
import net.copralianetwork.copralia.Activities.Shop.ShopActivity;
import net.copralianetwork.copralia.Sync.Account.Favs.FavsSync;
import net.copralianetwork.copralia.Sync.FirebaseEngine;
import net.copralianetwork.copralia.Sync.Lists.ListSync;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcos on 20/10/16.
 */

public class TestFiles {
    public static void test1(Context ctx){
        Intent dbmanager = new Intent(ctx,ScannerActivity.class);
        dbmanager.putExtra("type",ScannerActivity.PROD_SCAN);
        ctx.startActivity(dbmanager);
    }
    public static void test2(Context ctx){
        ListSync.RemoteListsSync(ctx);
    }
    public static void test3(Context ctx){
        /*FirebaseEngine fbe = new FirebaseEngine(ctx);

        Map<String,String> data = new HashMap<>();
        data.put("action","account_prodfav_add");
        data.put("prodID","851");

        fbe.sendUpstreamMessage(data);*/

        Intent shop = new Intent(ctx, ShopActivity.class);
        ctx.startActivity(shop);

    }
}
