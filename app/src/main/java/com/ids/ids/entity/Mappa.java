package com.ids.ids.entity;

import java.util.ArrayList;

public class Mappa {

    private int piano;
    private int piantina;           //è un'immagine, è intero perché fa riferimento al codice del drawable associato
    private ArrayList<Nodo> nodi;

    public Mappa(int piano, int piantina, ArrayList<Nodo> nodi) {
        this.piano = piano;
        this.piantina = piantina;
        this.nodi = nodi;
    }

    public int getPiano(){
        return this.piano;
    }
    public void setPiano(int piano){
        this.piano = piano;
    }

    public int getPiantina(){
        return this.piantina;
    }
    public void setPiantina(int piantina){
        this.piantina = piantina;
    }

    public ArrayList<Nodo> getNodi(){
        return this.nodi;
    }
    public void setNodi(ArrayList<Nodo> nodi){
        this.nodi = nodi;
    }
}
