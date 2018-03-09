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

    public boolean controllaConnessione(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return true;
    }

    public boolean nodoSelezionato(int idNodo){
        for(Nodo nodo : this.nodiSelezionati)
            if(nodo.getId() == idNodo)
                return true;
        return false;
    }

    /**
     *
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

    public boolean inviaNodiSelezionati(){
        // TODO aggiornare nel db locale?
        boolean result = this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati);
        if(result)
            this.nodiSelezionati.clear();
        return result;
    }

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
