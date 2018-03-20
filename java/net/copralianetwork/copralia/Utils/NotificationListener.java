package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Marcos on 01/12/2015.
 */
public class NotificationListener {
    public static void processCloudMessage(JSONObject data, Context ctx){
        try{
            String method = data.getString("method");
            if(method != null){
                switch (method){
                    case"normal":
                        NotificationUtils.communNotification(ctx,data.getString("title"),data.getString("message"));
                        break;
                    case "action":
                        try {
                            Class<?> t;
                            t = Class.forName("net.copralianetwork.copralia."+data.getString("action"));
                            Intent intent = new Intent(ctx,t);
                            JSONArray parametros = data.getJSONArray("action_parameters");

                            for(int i = 0; i < parametros.length(); i++)
                            {
                                JSONObject item  = parametros.getJSONObject(i);
                                intent.putExtra(item.getString("key"),item.getString("value"));
                            }


                            NotificationUtils.communNotificationAction(ctx, intent, data.getString("title"), data.getString("message"));

                        } catch (Exception ignored){
                            ignored.printStackTrace();
                            NotificationUtils.communNotification(ctx,data.getString("title"),data.getString("message"));
                        }
                        break;
                    default:
                        break;
                }
            }
        }catch (Exception e){

        }
    }

}
