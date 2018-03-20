package net.copralianetwork.copralia.Sync;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcos on 6/11/16.
 */

public class CloudActions {

    private Context mContext;

    public CloudActions(Context ctx){
        this.mContext = ctx;
    }


    /* Favoritos */
    public void addProdFav(String prodID){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","account_prodfav_add");
        data.put("prodID",prodID);
        fbe.sendUpstreamMessage(data);
    }
    public void removeProdFav(String prodID){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","account_prodfav_remove");
        data.put("prodID",prodID);
        fbe.sendUpstreamMessage(data);
    }
    public void clearProdFav(){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","account_prodfav_clear");
        fbe.sendUpstreamMessage(data);
    }
    /* Listas */
    public void clearList(String listID){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","list_clear");
        data.put("listID",listID);
        fbe.sendUpstreamMessage(data);
    }

    /* Listas productos */
    public void addProdList(String listID, String prodID){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","list_prod_add");
        data.put("listID",listID);
        data.put("prodID",""+prodID);
        fbe.sendUpstreamMessage(data);
    }
    public void removeProdList(String listID, String prodID){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","list_prod_remove");
        data.put("listID",listID);
        data.put("prodID",""+prodID);
        fbe.sendUpstreamMessage(data);
    }
    public void updateProdListCant(String listID,String prodID,int cant){
        if(cant > 0){
            FirebaseEngine fbe = new FirebaseEngine(mContext);
            Map<String,String> data = new HashMap<>();
            data.put("action","list_prod_update");
            data.put("listID",listID);
            data.put("prodID",""+prodID);
            data.put("prod_cant", String.valueOf(cant));
            fbe.sendUpstreamMessage(data);
        }
    }
    public void updateProdListBuy(String listID,String prodID,boolean buy){
        FirebaseEngine fbe = new FirebaseEngine(mContext);
        Map<String,String> data = new HashMap<>();
        data.put("action","list_prod_update");
        data.put("listID",listID);
        data.put("prodID",""+prodID);
        if(buy){
            data.put("prod_buy","1");
        }else{
            data.put("prod_buy","0");
        }
        fbe.sendUpstreamMessage(data);
    }
}
