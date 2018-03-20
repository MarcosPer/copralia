package net.copralianetwork.copralia.Sync.Account.Favs;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import net.copralianetwork.copralia.Sync.Account.AccountProvider;
import net.copralianetwork.copralia.Sync.Lists.ProdUtils;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by marcos on 6/11/16.
 */

public class ProdFav {
    private Context mContext;
    public ProdFav (Context ctx){
        mContext = ctx;
    }
    public  void addProduct(Map<String, String> message) throws NullPointerException{
        ContentValues aux = ProdUtils.getContentValues(message);
        if(aux != null){
            mContext.getContentResolver().insert(Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav"), aux);
        }else{
            Log.d("COPRALIAPP","Producto no añadido error 10610");
        }
    }
    public  void addProduct(JSONObject object) throws NullPointerException{
        ContentValues aux = ProdUtils.getContentValuesObject(object);
        if(aux != null){
            mContext.getContentResolver().insert(Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav"), aux);
        }else{
            Log.d("COPRALIAPP","Producto no añadido error 10610");
        }
    }
    public  void removeProduct(String prodID) throws NullPointerException{
        mContext.getContentResolver().delete(Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav/"+prodID),null,null);
    }
    public void removeAll(){
        mContext.getContentResolver().delete(Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav"),null,null);
    }
}
