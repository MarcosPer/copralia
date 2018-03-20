package net.copralianetwork.copralia.Sync.App.Banners;

import android.content.ContentValues;
import android.content.Context;

import net.copralianetwork.copralia.Sync.App.AppProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by marcos on 25/11/16.
 */

public class HomeBannerUtils {



    public static void addBanner(Context ctx,ContentValues contentValues){
        ctx.getContentResolver().insert(AppProvider.URI_HOME_BANNER,contentValues);
    }
    public static void addBanner(Context ctx, JSONObject banner){
        ContentValues values = getContentValues(banner);
        addBanner(ctx,values);
    }

    public static ContentValues getContentValues(JSONObject banner){
        ContentValues aux = new ContentValues();
        try{
            if(!banner.has("ID") && !banner.has("image")){
                return null;
            }else{
                aux.put("ID",banner.getInt("ID"));
                aux.put("image",banner.getString("image"));
            }
            if(banner.has("url")){
                aux.put("url",banner.getString("url"));
            }
            if(banner.has("intent")){
                aux.put("intent",banner.getString(("intent")));
            }
            if(banner.has("date")){
                aux.put("date",banner.getString(("date")));
            }
        }catch (JSONException e){
            return null;
        }
        return aux;
    }

    public static ContentValues getContentValues(Map<String, String> banner) {
        ContentValues aux = new ContentValues();
        if(!banner.containsKey("ID") && !banner.containsKey("image")){
            return null;
        }else{
            aux.put("ID",banner.get("ID"));
            aux.put("image",banner.get("image"));
        }
        if(banner.containsKey("url")){
            aux.put("url",banner.get("url"));
        }
        if(banner.containsKey("intent")){
            aux.put("intent",banner.get(("intent")));
        }
        if(banner.containsKey("date")){
            aux.put("date",banner.get(("date")));
        }
        return aux;
    }
}
