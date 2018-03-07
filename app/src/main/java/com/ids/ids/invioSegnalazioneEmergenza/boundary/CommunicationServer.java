package com.ids.ids.invioSegnalazioneEmergenza.boundary;

import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

import java.util.ArrayList;

public class CommunicationServer{

    /**
     * ottiene come parametri gli ID dei nodi da inviare nella segnalazione,
     * invia una richiesta RESTful con questi ID,
     * riceve come risposta l'eventuale successo dell'operazione
     */
    public boolean inviaNodiSottoIncendio(ArrayList<Nodo> nodi){
        return true;
    }

    public Mappa richiediMappa(){
        return null;
    }

}
