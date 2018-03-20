package net.copralianetwork.copralia.Sync.App.Banners;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.copralianetwork.copralia.Sync.App.AppProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by marcos on 25/11/16.
 */

public class HomeBanner {

    private Context mContext;
    private String bannerID;

    public HomeBanner(Context ctx,String bannerID){
        this.mContext = ctx;
        this.bannerID = bannerID;

        /* Si no existe el banner se crea */
        Cursor aux = mContext.getContentResolver().query(Uri.withAppendedPath(AppProvider.URI_HOME_BANNER,bannerID),null,null,null,null);
        if(aux.getCount() == 0){
            ContentValues values = new ContentValues();
            values.put("ID",bannerID);
            mContext.getContentResolver().insert(AppProvider.URI_HOME_BANNER,values);
        }
    }

    public void updateData(Map<String, String> message){
        ContentValues data = HomeBannerUtils.getContentValues(message);
        updateData(data);
    }
    public void updateData(JSONObject banner){
        ContentValues data = HomeBannerUtils.getContentValues(banner);
        updateData(data);
    }
    public void updateData(ContentValues values){
        mContext.getContentResolver().update(Uri.withAppendedPath(AppProvider.URI_HOME_BANNER,bannerID),values,null,null);
    }
    public void remove(){
        mContext.getContentResolver().delete(Uri.withAppendedPath(AppProvider.URI_HOME_BANNER,bannerID),null,null);
    }
}
