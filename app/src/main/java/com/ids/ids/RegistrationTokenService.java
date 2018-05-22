package com.ids.ids;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ids.ids.toServer.CommunicationServer;

/**
 * Created by User on 12/05/2018.
 */

public class RegistrationTokenService extends IntentService {

    public static String token;
    private static final String TAG = "RegIntentService";

    public final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private NotificationManager mNotificationManager;

    public RegistrationTokenService() {

        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {

            if(FirebaseInstanceId.getInstance().getToken() != null && !sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER,false)) {

                token = FirebaseInstanceId.getInstance().getToken();
                // [END get_token]
                Log.i(TAG, "Firebase Registration Token: " + token);


                sendRegistrationToServer(token);
                sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
                sendNotifica("Registrazione prodotto avvenuta","La registrazione è avvenuta correttamente");

            }/*else
                throw new Exception("La registrazione del prodotto è fallita.\nCausa mancata connessione al server");*/


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
            sendNotifica("Registrazione prodotto fallita","Siamo spiacenti");
        }

    }

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) throws IOException {

        CommunicationServer.getInstance(this).registrationTokenTask(token);
    }

    public void sendNotifica(String Titolo,String messaggio){

        //Creazione notifica
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, "0000")
                .setContentTitle(Titolo)
                .setContentText(messaggio)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.fireexiticon)
                .setDefaults(Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_LIGHTS
                        | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(0, notificationBuilder.build());
    }
}


