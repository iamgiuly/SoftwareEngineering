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
import com.ids.ids.boundary.ServerTask.DownloadPercorsoEmergenzaTask;
import com.ids.ids.boundary.ServerTask.DownloadInfoMappaTask;
import com.ids.ids.boundary.ServerTask.InvioNodiTask;

import com.ids.ids.control.User;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.ui.MappaView;
import com.ids.ids.utils.Parametri;

/**
 * Espone i metodi che permettono di avviare Task specifici per effettuare le richieste al server
 */
public class CommunicationServer implements IntCommunicationServer{

    private static CommunicationServer instance = null;
    private static final String TAG = "CommunicationServer";

    private final Handler handler = new Handler();
    private User user;
    private Context context;
    private int Piano;

    private CommunicationServer(Context contxt) {

        context = contxt;
    }

    /**
     * Richiama il task specifico per l'invio dei nodi selezionati al server
     *
     * @param nodi
     * @return void
     */
    public void inviaNodiSottoIncendio(ArrayList<Nodo> nodi) {

        new InvioNodiTask(nodi, context).execute();
    }

    /**
     * Richiama il task specifico per il download del percorso normale
     *
     * @param macPosU , piano , mv , map , macDest , enable
     * @return void
     */
    public void richiestaPercorsoNormale(String macPosU, int piano, MappaView mv, Mappa map, String macDest, boolean enable) {

        new DownloadPercorsoNormaleTask(context, macPosU, piano, mv, map, macDest, enable).execute();
    }

    /**
     * Richiama il task specifico per l'invio dei nodi selezionati al server
     *
     * @param macPosU , piano , mv , map
     * @return void
     */
    public void richiediPercorsoEmergenza(String macPosU, int piano, MappaView mV, Mappa map) {

        new DownloadPercorsoEmergenzaTask(context, macPosU, piano, mV, map).execute();
    }

    /**
     * Avvia il task per la richiesta della mappa
     * <p>
     * Recupera la mappa del Piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     *
     * @param macPosU
     * @return void
     */
    public void richiestaMappa(String macPosU) {

        new DownloadInfoMappaTask(context, macPosU).execute();
    }

    /**
     * Avvia o ferma il runnable aggiorna in base al paramentro enable passato come parametro
     *
     * @param enable, piano
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

            Log.i(TAG, "Richiesta Aggiornamenti");
            String dati_mappa_aggiornata = null;
            try {
                dati_mappa_aggiornata = new AggiornaDatiMappaTask(Piano).execute().get();

                if (dati_mappa_aggiornata != null) {

                    Type type = new TypeToken<Mappa>() {
                    }.getType();

                    Mappa mappa_aggiornata = new Gson().fromJson(dati_mappa_aggiornata, type);
                    mappa_aggiornata.salvataggioLocale(context);

                    user = User.getInstance((Activity) context);
                    user.setMappa(mappa_aggiornata);
                    MappaView m = user.getMappaView();

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

    private void setContext(Context contxt) {

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
