package net.copralianetwork.copralia.Sync;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseEngine {
    private Context mContext;

    public FirebaseEngine(Context ctx){
        this.mContext = ctx;
    }

    private String getMessageID() {
        SharedPreferences prefs = mContext.getSharedPreferences("firebase", Context.MODE_PRIVATE);
        int lastID = prefs.getInt("lastID", 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("lastID",lastID+1);
        edit.apply();
        return "m-"+lastID;
    }

    public void sendUpstreamMessage(Map<String, String> data){
        RemoteMessage.Builder msg = new RemoteMessage.Builder(FirebaseListener.SENDER_ID);
        msg.setMessageId(getMessageID());
        for (Map.Entry<String, String> dataline : data.entrySet()){
            msg.addData(dataline.getKey(),dataline.getValue());
        }
        FirebaseMessaging.getInstance().send(msg.build());
    }

}
