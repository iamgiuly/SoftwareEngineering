package com.ids.ids.backService;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


/*Classe che invia l'intent implicito nel caso in cui l'app venga chiusa o il telefono riavviato, deve corrispondere ad un nodo nel manifest.xml */

public class ReceiverCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){        //pushIntent inviato per versioni post-O
                pushIntent = new Intent(context, BackServicePostOreo.class);
                BackServicePostOreo.enqueueWork(context, pushIntent);}
                else{
                pushIntent = new Intent(context, BackServicePreOreo.class);      //pushIntent inviato per versioni pre-0
                context.startService(pushIntent);
                            }
        }
    }}

