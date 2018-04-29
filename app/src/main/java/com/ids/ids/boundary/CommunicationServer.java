package com.ids.ids.boundary;

import android.support.annotation.RequiresApi;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.ids.ids.boundary.ServerTask.AggiornaDatiMappaTask;
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
    private final Handler handler = new Handler();
    private Context context;
    private int piano;

    private CommunicationServer(Context context) {

        this.context = context;
    }

    /**
     * ottiene come parametri gli ID dei nodi da inviare nella segnalazione,
     * invia una richiesta RESTful con questi ID,
     * riceve come risposta l'eventuale successo dell'operazione
     */
    public void inviaNodiSottoIncendio(ArrayList<Nodo> nodi, Context contxt) {

        new InvioNodiTask(nodi, contxt).execute();
    }

    /**
     * Avvia il task per la richiesta del percorso e una volta ricevuto, se non è null,
     * lo mappa lato App
     *
     * @param mac , piano
     * @return percorso --> null se la connessione è caduta
     */
    public ArrayList<Arco> richiediPercorso(String mac, int piano) {

        ArrayList<Arco> percorso = null;

        try {

            String dati_percorso = new DownloadPercorsoTask(mac, piano).execute().get();

            if (dati_percorso != null) {

                System.out.println("Percorso esterno " + dati_percorso.toString());

                Type type = new TypeToken<ArrayList<Arco>>() {
                }.getType();
                // Estrazione dell ArrayList inviato dall app
                percorso = new Gson().fromJson(dati_percorso, type);

                System.out.println("percorso " + percorso.size());
            }

        } catch (ExecutionException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return percorso;
    }

    /**
     * Avvia il task per la richiesta della mappa
     *
     * Recupera la mappa del piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     *
     * @param contxt, posizioneU
     */
    public void richiestaMappa(Context contxt, String posizioneU) {

        new DownloadInfoMappaTask(contxt, posizioneU).execute();
    }

    /**
     * In base all enable avvia o ferma il runnable relativo alla richiesta di aggiornamenti al server
     *
     * @param enable
     */
    public void richiestaAggiornamenti(Boolean enable, int PianoUtente) {

        piano = PianoUtente;
        if (enable)
            handler.postDelayed(Aggiorna, Parametri.T_AGGIORNAMENTI);
        else
            handler.removeCallbacks(Aggiorna);
    }

    public void aggiornaDbLocale(){

    }

    // Runnable per l aggiornamento
    private final Runnable Aggiorna = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            System.out.println("Richiesta Aggiornamenti");
            String dati_mappa_aggiornata = null;
            try {
                dati_mappa_aggiornata = new AggiornaDatiMappaTask(piano).execute().get();

                if (dati_mappa_aggiornata != null) {

                    Type type = new TypeToken<Mappa>() {
                    }.getType();

                    Mappa mappa_aggiornata = new Gson().fromJson(dati_mappa_aggiornata, type);
                    mappa_aggiornata.salvataggioLocale(context);

                    UserController u = UserController.getInstance(null);
                    u.setMappa(mappa_aggiornata);
                    MappaView m = u.getMappaView();

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

    public static CommunicationServer getInstance(Context context) {
        if (instance == null)
            instance = new CommunicationServer(context);
        return instance;
    }
}
