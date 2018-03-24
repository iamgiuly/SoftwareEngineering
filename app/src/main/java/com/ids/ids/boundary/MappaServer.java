package com.ids.ids.boundary;

import com.ids.ids.entity.Nodo;

import java.util.ArrayList;

public class MappaServer {

    private int Piano;
    private String Piantina;           //è un'immagine, è intero perché fa riferimento al codice del drawable associato
    private ArrayList<NodoServer> Nodi;   //TODO eliminare, avremo un getNodi() che recupera l'ArrayList dei nodi dagli archi



    public int getPiano() {

        return Piano;
    }

    public String getPiantina() {

        return Piantina;
    }

    public ArrayList<NodoServer> getNodi() {

        return Nodi;
    }

    public void setNodi(ArrayList<NodoServer> nodi) {

        Nodi = nodi;
    }

}

