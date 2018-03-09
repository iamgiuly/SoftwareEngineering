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
        Mappa mappa = new Mappa(145, R.drawable.map145, null);
        ArrayList<Nodo> nodi = new ArrayList<>();
        nodi.add(new Nodo("1", 10, 10, Nodo.TIPO_BASE));
        nodi.add(new Nodo("2", 20, 20, Nodo.TIPO_BASE));
        nodi.add(new Nodo("3", 30, 30, Nodo.TIPO_BASE));
        mappa.setNodi(nodi);
        return mappa;
    }

    public static CommunicationServer getInstance(){
        if(instance == null)
            instance = new CommunicationServer();
        return instance;
    }

}
