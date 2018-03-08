package com.ids.ids.invioSegnalazioneEmergenza.boundary;

import com.ids.ids.R;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

import java.util.ArrayList;

public class CommunicationServer{

    private static CommunicationServer instance = null;

    /**
     * ottiene come parametri gli ID dei nodi da inviare nella segnalazione,
     * invia una richiesta RESTful con questi ID,
     * riceve come risposta l'eventuale successo dell'operazione
     */
    public boolean inviaNodiSottoIncendio(ArrayList<Nodo> nodi){
        // TODO dummy
        return true;
    }

    public Mappa richiediMappa(int piano){
        // TODO dummy
        return new Mappa(145, R.drawable.map145, null);
    }

    public static CommunicationServer getInstance(){
        if(instance == null)
            instance = new CommunicationServer();
        return instance;
    }

}
