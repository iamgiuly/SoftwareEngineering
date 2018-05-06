package com.ids.ids.backService;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class BackServiceThread {

    private static BackServiceThread instance = null;

    private Timer timer = new Timer();
    private FromToServer fromToServer = new FromToServer();
    private Notifica notifica = new Notifica();

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

                    if(fromToServer.StatoEmergenza())
                        notifica.sendSimpleNotification();
                }
            }
        }, 0, 2000);    //Periodo 2 secondi
    }

    public static BackServiceThread getInstance() {
        if (instance == null)
            instance = new BackServiceThread();
        return instance;
    }
}
