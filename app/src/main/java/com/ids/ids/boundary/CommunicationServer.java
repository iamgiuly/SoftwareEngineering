package com.ids.ids.boundary;

import android.app.Activity;
import android.support.annotation.RequiresApi;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.ids.ids.boundary.ServerTask.AggiornaDatiMappaTask;
import com.ids.ids.boundary.ServerTask.DownloadPercorsoNormaleTask;
import com.ids.ids.boundary.ServerTask.DownloadPercorsoTask;
import com.ids.ids.boundary.ServerTask.DownloadInfoMappaTask;
import com.ids.ids.boundary.ServerTask.InvioNodiTask;

import com.ids.ids.control.UserController;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.ui.MappaView;
import com.ids.ids.utils.Parametri;

public class CommunicationServer {

    private static CommunicationServer instance = null;
    private static final String TAG = "CommunicationServer";

    private final Handler handler = new Handler();
    private UserController userController;
    private Context context;
    private int Piano;

    private CommunicationServer(Context contxt) {

        context = contxt;
    }

    /**
     * ottiene come parametri gli ID dei nodi da inviare nella segnalazione,
     * invia una richiesta RESTful con questi ID,
     * riceve come risposta l'eventuale successo dell'operazione
     */
    public void inviaNodiSottoIncendio(ArrayList<Nodo> nodi/*, Context contxt*/) {

        new InvioNodiTask(nodi, context).execute();
    }

    public void richiestaPercorsoNormale(String macPosU, int piano, MappaView mv, Mappa mappa, String macDest ,boolean enable){

        new DownloadPercorsoNormaleTask(context , macPosU, piano, mv, mappa , macDest , enable).execute();
    }


    public void richiediPercorso(String mac, int piano, MappaView mV, Mappa map) {

        new DownloadPercorsoTask(context , mac, piano, mV, map).execute();
    }

    /**
     * Avvia il task per la richiesta della mappa
     *
     * Recupera la mappa del Piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     *
     * @param  posizioneU
     */
    public void richiestaMappa(/*Context contxt,*/ String posizioneU) {

        new DownloadInfoMappaTask(context, posizioneU).execute();
    }

    /**
     * In base all enable avvia o ferma il runnable relativo alla richiesta di aggiornamenti al server
     *
     * @param enable
     */
    public void richiestaAggiornamenti(Boolean enable, int piano) {

        Piano = piano;
        if (enable)
            handler.postDelayed(Aggiorna, Parametri.T_AGGIORNAMENTI);
        else
            handler.removeCallbacks(Aggiorna);
    }

    // Runnable per l aggiornamento
    private final Runnable Aggiorna = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            Log.i(TAG,"Richiesta Aggiornamenti");
            String dati_mappa_aggiornata = null;
            try {
                dati_mappa_aggiornata = new AggiornaDatiMappaTask(Piano).execute().get();

                if (dati_mappa_aggiornata != null) {

                    Type type = new TypeToken<Mappa>() {
                    }.getType();

                    Mappa mappa_aggiornata = new Gson().fromJson(dati_mappa_aggiornata, type);
                    mappa_aggiornata.salvataggioLocale(context);

                    userController = UserController.getInstance((Activity)context);
                    userController.setMappa(mappa_aggiornata);
                    MappaView m = userController.getMappaView();

                    try {
                        m.setNodi(mappa_aggiornata.getNodi());
                        m.postInvalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                handler.postDelayed(Aggiorna, Parametri.T_AGGIORNAMENTI);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    };

    private void setContext(Context contxt){

        context = contxt;
    }

    public static CommunicationServer getInstance(Context context) {
        if (instance == null)
            instance = new CommunicationServer(context);
        else
            instance.setContext(context);
        return instance;
    }
}
