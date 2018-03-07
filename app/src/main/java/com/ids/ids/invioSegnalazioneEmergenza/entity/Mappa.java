package com.ids.ids.invioSegnalazioneEmergenza.entity;

import android.media.Image;

import java.util.ArrayList;

public class Mappa {

    private Image piantina;
    private ArrayList<Nodo> nodi;

    public Mappa(Image piantina, ArrayList<Nodo> nodi) {
        this.piantina = piantina;
        this.nodi = nodi;
    }

    public Image getPiantina(){
        return this.piantina;
    }
    public void setPiantina(Image piantina){
        this.piantina = piantina;
    }

    public ArrayList<Nodo> getNodi(){
        return this.nodi;
    }
    public void setNodi(ArrayList<Nodo> nodi){
        this.nodi = nodi;
    }
}
