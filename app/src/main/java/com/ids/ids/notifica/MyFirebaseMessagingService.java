package com.ids.ids.notifica;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ids.ids.R;
import com.ids.ids.ui.MainActivity;

/**
 * Classe per la ricezione dei messaggi in arrivo da parte di Firebase
 *
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {


    /**
     * Receiver
     *
     * @param remoteMessage messaggio in arrivo da Firebase
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Creiamo il pending intent che verrà lanciato quando la notifica viene premuta
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("AvviaTastoEmergenza", true);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        //Creazione notifica
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, "0000")
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.nodo_incendio)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_LIGHTS
                        | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(0, notificationBuilder.build());
    }
}
