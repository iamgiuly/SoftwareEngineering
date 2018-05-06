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
import android.view.View;
import android.view.View.OnClickListener;

import com.ids.ids.R;
import com.ids.ids.ui.EmergenzaActivity;

public class Notifica extends Activity {

    private static final int SIMPLE_NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID="0000";

    NotificationManager mNotificationManager;

    //todo metodo collegato alla View Activity main_notifica per fare test - DA ELIMINARE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_notifica);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        View btnSendSimpleNotification = findViewById(R.id.btnSendSimpleNotification);
        btnSendSimpleNotification.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendSimpleNotification();
            }
        });

        View btnCancelNotify = findViewById(R.id.btnCancelNotification);
        btnCancelNotify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelSimpleNotification();
            }
        });
    }

    protected void cancelSimpleNotification() {
        mNotificationManager.cancel(SIMPLE_NOTIFICATION_ID);
    }

    //Metodo CREAZIONE DELLA NOTIFICATION BAR
    public void sendSimpleNotification() {

        //Valida per Android Oreo 8.0 o superiori, altrimenti CHANNEL_ID ignorato - CREA UN CANALE DI COMUNICAZIONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }

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
        Intent notificationIntent = new Intent(this, EmergenzaActivity
                .class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notificationBuilder.setContentIntent(contentIntent);

        // Impostazzione suono, luci e vibrazione di default
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(SIMPLE_NOTIFICATION_ID,
                notificationBuilder.build());
    }



}

