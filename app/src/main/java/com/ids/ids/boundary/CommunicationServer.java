package com.ids.ids.boundary;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.boundary.ServerTask.AggiornaDatiMappaTask;
import com.ids.ids.boundary.ServerTask.DownloadPercorsoTask;
import com.ids.ids.boundary.ServerTask.DownloadInfoMappaTask;
import com.ids.ids.boundary.ServerTask.InvioNodiTask;
import com.ids.ids.control.UserController;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.Parametri;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CommunicationServer {

    private static CommunicationServer instance = null;

    private Context context;
    private final Handler handler = new Handler();

    private int piano;

    public CommunicationServer(Context context) {

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

                System.out.println("esterno " + dati_percorso.toString());

                Type type = new TypeToken<ArrayList<Arco>>() {
                }.getType();
                // Estrazione dell ArrayList inviato dall app
                percorso = new Gson().fromJson(dati_percorso, type);

                System.out.println("percorso " + percorso.size());
            }

        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }

        return percorso;
    }

    /**
     * Avvia il task per la richiesta della mappa
     *
     * @param contxt, posizioneU
     *
     */
    public void richiestaMappa(Context contxt, String posizioneU) {

        new DownloadInfoMappaTask(contxt, posizioneU).execute();
    }

    /**
     * In base all enable avvia o ferma il runnable relativo alla richiesta di aggiornamenti al server
     *
     * @param enable
     *
     */
    public void richiestaAggiornamenti(Boolean enable, int PianoUtente) {

        piano = PianoUtente;
        if(enable)
            handler.postDelayed(Aggiorna, Parametri.T_AGGIORNAMENTI );
        else
            handler.removeCallbacks(Aggiorna);

    }

    // Runnable per l aggiornamento
    private final Runnable Aggiorna = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            System.out.println("RichiestaAggiornamenti");
            String dati_mappa_aggiornata = null;
            try {
                dati_mappa_aggiornata = new AggiornaDatiMappaTask(piano).execute().get();

                if(dati_mappa_aggiornata != null){

                    //  System.out.println("ciao: " + dati_mappa_aggiornata.toString());

                    Type type = new TypeToken<Mappa>() {
                    }.getType();

                    Mappa mappa_aggiornata = new Gson().fromJson(dati_mappa_aggiornata, type);
                    mappa_aggiornata.salvataggioLocale(context);
                    //TODO:RISOLVERE cast non funziona
                    //   UserController.getInstance((Activity)context).setMappa(mappa_aggiornata);

                }

                handler.postDelayed(Aggiorna, Parametri.T_AGGIORNAMENTI );

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
