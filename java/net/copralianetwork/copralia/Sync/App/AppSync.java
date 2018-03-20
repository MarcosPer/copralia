package net.copralianetwork.copralia.Sync.App;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.RemoteMessage;

import net.copralianetwork.copralia.Sync.Account.Favs.ProdFav;
import net.copralianetwork.copralia.Sync.App.Banners.HomeBanner;
import net.copralianetwork.copralia.Sync.App.Banners.HomeBannerUtils;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by marcos on 24/11/16.
 */

public class AppSync {

    public static void remoteHomeBannerSync(final Context ctx){
        ctx.getContentResolver().delete(AppProvider.URI_HOME_BANNER,null,null);
        APIRequest request = new APIRequest(ctx,
            Request.Method.GET, "http://192.168.1.218:8080/banners",
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray banners = response.getJSONArray("banners");
                        for (int i = 0; i < banners.length(); i++) {
                            JSONObject bannerData = banners.getJSONObject(i);
                            HomeBanner banner = new HomeBanner(ctx,String.valueOf(bannerData.getInt("ID")));
                            banner.updateData(bannerData);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        );
        VolleySingleton.getInstance(ctx).addToRequestQueue(request);
    }

    public static void SyncCloudMessage(Context ctx, Map<String, String> message){
        String[] action = message.get("action").split("_");
        switch (action[1]){
            case "home-banner":
                HomeBanner banner = new HomeBanner(ctx,message.get("bannerID"));
                switch (action[2]){
                    case "add":
                        banner.updateData(message);
                        break;
                    case "remove":
                        banner.remove();
                        break;
                }
                break;
            default:
                break;
        }
    }
}
