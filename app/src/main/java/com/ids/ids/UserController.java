package com.ids.ids;

import android.app.Application;


public class UserController extends Application {

    private CommunicationServer communicationServer;

    public UserController(){
        this.communicationServer = (CommunicationServer) getApplicationContext();
    }

    public boolean controllaConnessione(){
        return true;
    }

    public void gestisciTapNodiSottoIncendio(){

    }

    // TODO viene mostrata la view con la mappa su cui selezionare i nodi (creare altra Activity?)
    public void gestisciTapBottoneEmergenza(){
        // TODO ...
        this.communicationServer.richiediMappa();
    }

    // TODO i nodi selezionati vengono inviati al server attraverso una richiesta RESTful
    public void gestisciTapBottoneInvioNodi(){
        // TODO ...
        this.communicationServer.inviaNodiSottoIncendio();
    }
}
