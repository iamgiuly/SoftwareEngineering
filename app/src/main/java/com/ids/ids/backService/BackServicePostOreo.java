package com.ids.ids.backService;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

/*Classe che implementa il servizio in background per versioni 8.0 di Android e superiori */
public class BackServicePostOreo extends JobIntentService {

    public static final int JOB_ID = 1;

    @Override
    public void onCreate(){
        super.onCreate();
        BackServiceThread.getInstance(this).init();
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BackServicePostOreo.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        onCreate();
    }
}