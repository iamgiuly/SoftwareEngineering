package com.ids.ids.boundary;

import com.ids.ids.entity.Nodo;

import java.util.ArrayList;

public class MappaServer {

    private int Piano;
    private String Piantina;           //è un'immagine, è intero perché fa riferimento al codice del drawable associato
    private ArrayList<NodoServer> Nodi;   //TODO eliminare, avremo un getNodi() che recupera l'ArrayList dei nodi dagli archi


    public MappaServer(int piano, String piantina, ArrayList<NodoServer> nodi) {

        Piano = piano;
        Piantina = piantina;
        Nodi = nodi;


    }


    public int getPiano() {
        return Piano;
    }


    public void setPiano(int piano) {
        Piano = piano;
    }


    public String getPiantina() {
        return Piantina;
    }


    public void setPiantina(String piantina) {
        Piantina = piantina;
    }


    public ArrayList<NodoServer> getNodi() {
        return Nodi;
    }


    public void setNodi(ArrayList<NodoServer> nodi) {
        Nodi = nodi;
    }

}

