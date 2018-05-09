package com.ids.ids.backService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ids.ids.User;

import java.util.Timer;
import java.util.TimerTask;

public class BackServiceThread {

    private static BackServiceThread instance = null;

    private Timer timer;
    private FromToServer fromToServer;
    private Notifica notifica;

    public BackServiceThread(Context context){

        notifica = new Notifica();
        fromToServer = new FromToServer(context);
        timer = new Timer();

    }
    public void init(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int n=0;
                while(true){
                    Log.i("PROVA SERVICE", "Evento n."+n++);
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){ }

                    if(fromToServer.StatoEmergenza() && User.getInstance(null).getNotifica()==0){
                      // notifica.sendSimpleNotification();
                        User.getInstance(null).setNotifica(1);
                        Intent intent = new Intent(User.getInstance(null).getContext(), Notifica.class);
                        User.getInstance(null).getContext().startActivity(intent);
                    }

                }
            }
        }, 0, 2000);    //Periodo 2 secondi
    }

    public static BackServiceThread getInstance(Context context) {
        if (instance == null)
            instance = new BackServiceThread(context);
        return instance;
    }
}
