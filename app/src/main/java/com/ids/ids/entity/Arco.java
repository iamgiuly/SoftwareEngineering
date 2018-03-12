package com.ids.ids.entity;

import java.util.Map;

public class Arco {

    private Nodo nodoPartenza;
    private Nodo nodoArrivo;
    private Map<String, Integer> pesi;

    public Arco(Nodo nodoPartenza, Nodo nodoArrivo, Map<String, Integer> pesi) {
        this.nodoPartenza = nodoPartenza;
        this.nodoArrivo = nodoArrivo;
        this.pesi = pesi;
    }

    public Nodo getNodoPartenza() {
        return nodoPartenza;
    }
    public void setNodoPartenza(Nodo nodoPartenza) {
        this.nodoPartenza = nodoPartenza;
    }

    public Nodo getNodoArrivo() {
        return nodoArrivo;
    }
    public void setNodoArrivo(Nodo nodoArrivo) {
        this.nodoArrivo = nodoArrivo;
    }

    public Map<String, Integer> getPesi() {
        return pesi;
    }
    public void setPesi(Map<String, Integer> pesi) {
        this.pesi = pesi;
    }

}
