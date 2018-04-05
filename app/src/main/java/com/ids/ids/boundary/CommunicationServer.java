package com.ids.ids.boundary;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.DB.MappaDAO;
import com.ids.ids.boundary.ServerTask.DownloadPercorsoTask;
import com.ids.ids.boundary.ServerTask.DownloadInfoMappaTask;
import com.ids.ids.boundary.ServerTask.InvioNodiTask;
import com.ids.ids.control.Localizzatore;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.DB.NodoDAO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CommunicationServer {

    private static CommunicationServer instance = null;

    private Context context;

    final Handler handler = new Handler();

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

    public void richiestaMappa(Context contxt, String posizioneU) {

        new DownloadInfoMappaTask(contxt, posizioneU).execute();
    }

    public void richiestaAggiornamenti(Boolean enable) {

        if(enable)
            handler.postDelayed(Aggiorna, 2000);
        else
            handler.removeCallbacks(Aggiorna);

    }

    // FindMeALWAYS è un Runnable utilizzato dalla mappa
    private final Runnable Aggiorna = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            System.out.println("RichiestaAggiornamenti");
            // new aggiornaDatiMappaTask().execute();
            handler.postDelayed(Aggiorna, 2000);

        }
    };

    public static CommunicationServer getInstance(Context context) {
        if (instance == null)
            instance = new CommunicationServer(context);
        return instance;
    }
}
