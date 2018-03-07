package com.ids.ids.invioSegnalazioneEmergenza.entity;

public class Nodo {

    private String id;
    private int x, y;
    private String tipo;    //TODO enum "SottoIncendio", "PosizioneUtente", "Uscita"...

    public Nodo(String id, int x, int y, String tipo) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }
}