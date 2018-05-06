package com.ids.ids.backService;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

/*Classe che implementa il service in background per versioni Android precedenti ad Oreo*/
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class BackServicePreOreo extends Service {

    @Override
    public void onCreate(){
        super.onCreate();
        BackServiceThread.getInstance().init();
    }

    //metodo per far partire il service, viene richiamato dalla MainActivity al primo avvio
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.onCreate();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.i("SERVICE", "Distruzione Service");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}