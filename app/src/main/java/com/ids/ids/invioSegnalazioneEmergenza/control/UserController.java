package com.ids.ids.invioSegnalazioneEmergenza.control;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationBeacon;
import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationServer;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;
import com.ids.ids.invioSegnalazioneEmergenza.entity.NodoDAO;

import java.util.ArrayList;

// TODO rendere singleton, lo stesso oggetto deve essere accessibile da tutte le activity con un riferimento ad essa
public class UserController extends Application{

    private static UserController instance = null;

    private Context context;

    private CommunicationServer communicationServer = CommunicationServer.getInstance();
    private CommunicationBeacon communicationBeacon = CommunicationBeacon.getInstance();
    private ArrayList<Nodo> nodiSelezionati;

    public void init(Context context){
        this.context = context;
    }

    public boolean controllaConnessione(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public void selezionaNodo(String idNodo){
        Nodo nodo = NodoDAO.find(idNodo);
        this.nodiSelezionati.add(nodo);
    }

    // TODO viene mostrata la view con la mappa su cui selezionare i nodi (creare altra Activity?)
    public void gestisciTapBottoneEmergenza(){
        // TODO ...
        //this.communicationServer.richiediMappa();
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
        int piano = this.communicationBeacon.getPianoUtente();
        return this.communicationServer.richiediMappa(piano);
    }

    public static UserController getInstance(){
        if(instance == null)
            instance = new UserController();
        return instance;
    }
}
