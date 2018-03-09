package com.ids.ids.invioSegnalazioneEmergenza.control;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationBeacon;
import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationServer;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;
import com.ids.ids.invioSegnalazioneEmergenza.entity.NodoDAO;

import java.util.ArrayList;

public class UserController extends Application{

    private static UserController instance = null;

    private Context context;

    private CommunicationServer communicationServer = CommunicationServer.getInstance();
    private CommunicationBeacon communicationBeacon = CommunicationBeacon.getInstance();
    private ArrayList<Nodo> nodiSelezionati = new ArrayList<>();

    public void init(Context context){
        this.context = context;
    }

    /**
     * Verifica che l'utente sia connesso alla rete Wi-Fi
     * @return true se l'utente è connesso al Wi-Fi
     */
    public boolean controllaConnessione(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return true;
    }

    /**
     * Verifica che il nodo con un certo id sia selezionato
     * @param idNodo id del nodo da controllare
     * @return true se il nodo con l'id passato è selezionato
     */
    public boolean nodoSelezionato(int idNodo){
        for(Nodo nodo : this.nodiSelezionati)
            if(nodo.getId() == idNodo)
                return true;
        return false;
    }

    /**
     * Aggiunge o rimuove dalla lista dei nodi selezionati il nodo con l'id passato come parametro
     * @param idNodo id del nodo da selezionare o deselezionare
     * @return true se c'è almeno un nodo selezionato
     */
    public boolean selezionaNodo(int idNodo){
        for(Nodo nodo : this.nodiSelezionati){
            if(nodo.getId() == idNodo){
                this.nodiSelezionati.remove(nodo);
                return !this.nodiSelezionati.isEmpty();
            }
        }
        Nodo nodo = NodoDAO.find(idNodo);
        this.nodiSelezionati.add(nodo);
        return true;
    }

    /**
     * I nodi selezionati vengono settati nel db locale come sotto incendio,
     * viene fatto lo stesso nel db remoto inviando una richiesta RESTful al server,
     * quindi la lista dei nodi selezionati viene svuotata
     * @return true se l'operazione ha successo
     */
    public boolean inviaNodiSelezionati(){
        // TODO aggiornare nel db locale?
        boolean result = this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati);
        if(result)
            this.nodiSelezionati.clear();
        return result;
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente in base alla sua posizione rilevata dal beacon
     * @return mappa del piano in cui si trova l'utente
     */
    public Mappa richiediMappa() {
        int piano = this.communicationBeacon.getPianoUtente();
        return this.communicationServer.richiediMappa(piano);
    }

    public static UserController getInstance(){
        if(instance == null)
            instance = new UserController();
        return instance;
    }
}
