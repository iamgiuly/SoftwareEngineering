package com.ids.ids.boundary;

import android.content.Context;

import com.ids.ids.boundary.ServerTask.DownloadMappaTask;
import com.ids.ids.boundary.ServerTask.InvioNodiTask;
import com.ids.ids.entity.MappaDAO;
import com.ids.ids.ui.R;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.NodoDAO;

import java.util.ArrayList;

public class CommunicationServer{

    private static CommunicationServer instance = null;

    private Context context;

    private NodoDAO nodoDAO;

    public CommunicationServer(Context context){
        this.context = context;
        nodoDAO = NodoDAO.getInstance(context);
    }

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
        return MappaDAO.getInstance(this.context).find(piano);
    }

    public void inviaNodiSottoIncendio(ArrayList<Nodo> nodi, Context contxt){

        new InvioNodiTask(nodi,contxt).execute();

    }


    public void richiestaMappa(Context contxt,String posizioneU){

        new DownloadMappaTask(contxt,posizioneU).execute();

    }


    public static CommunicationServer getInstance(Context context){
        if(instance == null)
            instance = new CommunicationServer(context);
        return instance;
    }

}
