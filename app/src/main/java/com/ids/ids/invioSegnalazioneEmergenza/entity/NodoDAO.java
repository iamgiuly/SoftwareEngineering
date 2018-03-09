package com.ids.ids.invioSegnalazioneEmergenza.entity;

import java.util.ArrayList;

// TODO singleton
public class NodoDAO {

    public static Nodo find(int id){
        //TODO dummy
        return new Nodo(id, ""+id, 10 * id, 10 * id, Nodo.TIPO_BASE);
    }

    public static ArrayList<Nodo> findAll(){
        //TODO per mappa
        ArrayList<Nodo> nodi = new ArrayList<>();
        nodi.add(new Nodo(1, "1", 10, 10, Nodo.TIPO_BASE));
        nodi.add(new Nodo(2, "2", 20, 20, Nodo.TIPO_BASE));
        nodi.add(new Nodo(3, "3", 30, 30, Nodo.TIPO_BASE));
        return nodi;
    }
}
