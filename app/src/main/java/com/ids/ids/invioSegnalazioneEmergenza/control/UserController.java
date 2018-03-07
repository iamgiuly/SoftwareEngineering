package com.ids.ids.invioSegnalazioneEmergenza.control;

import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationServer;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

import java.util.ArrayList;

// TODO rendere singleton, lo stesso oggetto deve essere accessibile da tutte le activity con un riferimento ad essa
public class UserController {

    private static UserController instance = null;

    private CommunicationServer communicationServer;
    private ArrayList<Nodo> nodiSelezionati;

    public UserController(){
        this.communicationServer = new CommunicationServer();
    }

    public boolean controllaConnessione(){
        return true;
    }

    public void gestisciTapNodiSottoIncendio(Nodo nodo){

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

    public static UserController getInstance(){
        if(instance == null)
            instance = new UserController();
        return instance;
    }
}
