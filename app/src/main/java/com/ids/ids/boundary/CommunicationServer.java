package com.ids.ids.boundary;

import com.ids.ids.ui.R;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.NodoDAO;

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
        Mappa mappa = new Mappa(145, R.drawable.map145, null, null);
        mappa.setNodi(NodoDAO.findAll());
        return mappa;
    }

    public static CommunicationServer getInstance(){
        if(instance == null)
            instance = new CommunicationServer();
        return instance;
    }

}
