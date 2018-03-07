package com.ids.ids.invioSegnalazioneEmergenza.control;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationServer;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

import java.util.ArrayList;

// TODO rendere singleton, lo stesso oggetto deve essere accessibile da tutte le activity con un riferimento ad essa
public class UserController extends Application {

    private static UserController instance = null;

    private CommunicationServer communicationServer;
    private ArrayList<Nodo> nodiSelezionati;

    public UserController(){
        this.communicationServer = new CommunicationServer();
    }

    public boolean controllaConnessione(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    public void gestisciTapNodiSottoIncendio(Nodo nodo){
        this.nodiSelezionati.add(nodo);
    }

    // TODO viene mostrata la view con la mappa su cui selezionare i nodi (creare altra Activity?)
    public void gestisciTapBottoneEmergenza(){
        // TODO ...
        this.communicationServer.richiediMappa();
    }

    // TODO i nodi selezionati vengono inviati al server attraverso una richiesta RESTful
    public void gestisciTapBottoneInvioNodi(){
        // TODO ...
        if(this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati)){
            // TODO success
        }
        else{
            // TODO error
        }
    }

    public Mappa richiediMappa() {
        return this.communicationServer.richiediMappa();
    }

    public static UserController getInstance(){
        if(instance == null)
            instance = new UserController();
        return instance;
    }
}
