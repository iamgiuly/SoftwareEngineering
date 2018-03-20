package com.ids.ids.control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.NodoDAO;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;

public class UserController extends Application{

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;

    private static UserController instance = null;

    private Activity context;

    private CommunicationServer communicationServer;
    private NodoDAO nodoDAO;
    private ArrayList<Nodo> nodiSelezionati;

    private int modalita;

    public UserController(Activity context){
        this.context = context;
        this.communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        this.nodoDAO = NodoDAO.getInstance(context.getApplicationContext());
        this.nodiSelezionati = new ArrayList<>();
        this.modalita = MODALITA_SEGNALAZIONE;
    }

    /**
     * Verifica che l'utente sia connesso alla rete Wi-Fi
     * @return true se l'utente è connesso al Wi-Fi
     */
    public boolean controllaConnessione(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return !DebugSettings.CHECK_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
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
    public boolean selezionaNodo(Nodo nodo){
        nodo.setIncendio();
        if(nodo.isCambiato()) {
            if (!this.nodiSelezionati.contains(nodo))
                this.nodiSelezionati.add(nodo);
        }
        else {
            if (this.nodiSelezionati.contains(nodo))
                this.nodiSelezionati.remove(nodo);
        }
        return !this.nodiSelezionati.isEmpty();
/*
            this.nodiSelezionati.get
        for(Nodo nodo : this.nodiSelezionati){
            if(nodo.getId() == nodoSel.getId()){
                nodo = nodoSel;
                return true;
                //this.nodiSelezionati.remove(nodo);
                //return !this.nodiSelezionati.isEmpty();
            }
        }
        //Nodo nodo = nodoDAO.find(idNodo);
        this.nodiSelezionati.add(nodoSel);
        return true;*/
    }

    /**
     * I nodi selezionati vengono settati nel db locale come sotto incendio,
     * viene fatto lo stesso nel db remoto inviando una richiesta RESTful al server,
     * quindi la lista dei nodi selezionati viene svuotata
     * @return true se l'operazione ha successo
     */
    public boolean inviaNodiSelezionati(){
        //TODO inviare TUTTI i nodi
        for(Nodo nodo : this.nodiSelezionati) {
            //TODO togliere? nodo.setTipo(Nodo.TIPO_INCENDIO);
            nodoDAO.update(nodo);                   //TODO da inserire nella mappa
        }
        //TODO cambiare anche incendio -> base
        boolean result = this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati);
        if(result)
            this.clearNodiSelezionati();
        return result;
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente in base alla sua posizione rilevata dal beacon
     * @return mappa del piano in cui si trova l'utente
     */
    public Mappa richiediMappa() {
        int piano = 145;// TODO this.beaconScanner.getPianoUtente();
        return this.communicationServer.richiediMappa(piano);
    }

    public int getModalita() {
        return modalita;
    }
    public void setModalita(int modalita) {
        this.modalita = modalita;
    }

    public void clearNodiSelezionati() {
        this.nodiSelezionati.clear();
    }

    public static UserController getInstance(Activity context){
        if(instance == null)
            instance = new UserController(context);
        return instance;
    }
}
