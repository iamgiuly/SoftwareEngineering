package com.ids.ids.entity;

import java.util.ArrayList;

/**
 * Un arco è formato da un id, da un nodo di partenza e arrivo, da una lista di pesi e
 * dal piano relativo alla mappa a cui è associato
 */
public class Arco implements IntArco{

    private int id;
    private Nodo nodoPartenza;
    private Nodo nodoArrivo;
    private ArrayList<PesoArco> pesi;
    private int mappaId;

    public Arco(int id, Nodo nodoPartenza, Nodo nodoArrivo, ArrayList<PesoArco> pesi) {
        this.id = id;
        this.nodoPartenza = nodoPartenza;
        this.nodoArrivo = nodoArrivo;
        this.pesi = pesi;
        this.mappaId = nodoPartenza.getMappaId();
    }

    //Getters
    public int getId(){

        return id;
    }

    public Nodo getNodoPartenza() {

        return nodoPartenza;
    }

    public Nodo getNodoArrivo() {

        return nodoArrivo;
    }

    public ArrayList<PesoArco> getPesi() {

        return pesi;
    }

    /**
	 * Permette di calcolare il costo relativo all arco considerando tutti i pesi ad esso associati
	 */
    public int getCosto(){
        int costo = 0;
        for (PesoArco pesoArco : this.pesi)
            costo += pesoArco.getPeso().getPeso() * pesoArco.getValore();
        return costo;
    }

    public int getMappaId() {

        return mappaId;
    }

    //setters
    public void setId(int id){

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arco arco = (Arco) o;

        return id == arco.id;
    }

    @Override
    public int hashCode() {

        return id;
    }
}
