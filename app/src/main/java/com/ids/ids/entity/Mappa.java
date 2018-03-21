package com.ids.ids.entity;

import java.util.ArrayList;

public class Mappa {

    private int piano;
    private int piantina;           //è un'immagine, è intero perché fa riferimento al codice del drawable associato
    private ArrayList<Nodo> nodi;   //TODO eliminare, avremo un getNodi() che recupera l'ArrayList dei nodi dagli archi
    private ArrayList<Arco> archi;

    public Mappa(int piano, int piantina, ArrayList<Nodo> nodi, ArrayList<Arco> archi) {
        this.piano = piano;
        this.piantina = piantina;
        this.nodi = nodi;
        this.archi = archi;
    }

    public ArrayList<Arco> calcolaPercorso(){
        //TODO Dijkstra
        return null;
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

    //TODO sostituirà getNodi()
    public ArrayList<Nodo> getNodiFromArchi(){
        ArrayList<Nodo> nodi = new ArrayList<>();
        for(Arco arco : this.archi){
            Nodo nodoPartenza = arco.getNodoPartenza();
            if(!isNodoInArrayList(nodoPartenza, nodi))
                nodi.add(nodoPartenza);
            Nodo nodoArrivo = arco.getNodoArrivo();
            if(!isNodoInArrayList(nodoArrivo, nodi))
                nodi.add(nodoArrivo);
            nodi.add(arco.getNodoArrivo());
        }
        return this.nodi;
    }
    private boolean isNodoInArrayList(Nodo nodo, ArrayList<Nodo> nodi){
        for(Nodo n : nodi)
            if(n.getId() == nodo.getId())
                return true;
        return false;
    }

    public ArrayList<Arco> getArchi() {
        return archi;
    }
    public void setArchi(ArrayList<Arco> archi) {
        this.archi = archi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mappa mappa = (Mappa) o;

        return piano == mappa.piano;
    }

}
