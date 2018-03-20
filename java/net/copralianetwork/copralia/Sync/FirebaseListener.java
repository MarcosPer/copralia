package net.copralianetwork.copralia.Sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.copralianetwork.copralia.Activities.Principal.ActividadInicio;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Account.AccountSync;
import net.copralianetwork.copralia.Sync.App.AppSync;
import net.copralianetwork.copralia.Sync.Lists.ListSync;


public class FirebaseListener extends FirebaseMessagingService {

    public static String SENDER_ID = "126281660599@gcm.googleapis.com";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //TODO: Meter aqui el parseador
        //showNotification(remoteMessage.getData().toString());
        String[] action = remoteMessage.getData().get("action").split("_");

        switch (action[0]){
            case "list":
                ListSync.SyncCloudMessage(getApplicationContext(),remoteMessage.getData());
                break;
            case "account":
                AccountSync.SyncCloudMessage(getApplicationContext(),remoteMessage.getData());
                break;
            case "app":
                AppSync.SyncCloudMessage(getApplicationContext(),remoteMessage.getData());
                break;
        }

    }

    private void showNotification(String message) {

        Intent i = new Intent(this,ActividadInicio.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM Test")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}