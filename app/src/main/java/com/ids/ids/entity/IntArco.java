package com.ids.ids.entity;

import java.util.ArrayList;

/**
 * Interfaccia implementata dalla classe arco
 */
public interface IntArco {

    int getId();

    Nodo getNodoPartenza();

    Nodo getNodoArrivo();

    ArrayList<PesoArco> getPesi();

    int getCosto();

    int getMappaId() ;

    void setId(int id);
}
