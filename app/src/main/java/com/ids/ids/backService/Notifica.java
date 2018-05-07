package com.ids.ids.backService;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.v7.app.ActionBar;


import com.ids.ids.R;
import com.ids.ids.User;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.ui.MainActivity;

public class Notifica extends Activity{

    private static final int SIMPLE_NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID="0000";

    private NotificationManager mNotificationManager;

    //todo metodo collegato alla View Activity main_notifica per fare test - DA ELIMINARE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                Notifica.this, CHANNEL_ID);

        // Titolo e testo della notifica
        notificationBuilder.setContentTitle("EMERGENZA");
        notificationBuilder.setContentText("Allarme incendio. Clicca per aprire l'App.");

        // Testo che compare nella barra di stato non appena compare la notifica
        notificationBuilder.setTicker("Allarme incendio");

        // Data e ora della notifica
        notificationBuilder.setWhen(System.currentTimeMillis());

        // Icona della notifica
        notificationBuilder.setSmallIcon(R.drawable.nodo_incendio);

        // Creiamo il pending intent che verrà lanciato quando la notifica viene premuta -> gli passiamo EmergenzaActivity
       Intent notificationIntent = new Intent(Notifica.this, MainActivity.class );
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notificationBuilder.setContentIntent(contentIntent);

        // Impostazzione suono, luci e vibrazione di default
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(SIMPLE_NOTIFICATION_ID,
                notificationBuilder.build());

    }

    protected void cancelSimpleNotification() {
        mNotificationManager.cancel(SIMPLE_NOTIFICATION_ID);
    }

    //Metodo CREAZIONE DELLA NOTIFICATION BAR
    public void sendSimpleNotification() {



    }

}

