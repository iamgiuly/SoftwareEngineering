package com.ids.ids.invioSegnalazioneEmergenza.entity;

public class Nodo {

    public static final int TIPO_BASE = 0;
    public static final int TIPO_UTENTE = 1;
    public static final int TIPO_USCITA = 2;
    public static final int TIPO_INCENDIO = 3;

    private String id;
    private int x, y;       // valori da 0 a 100 che indicano le coordinate relative alla mappa
    private int tipo;

    public Nodo(String id, int x, int y, int tipo) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }

    public String getId(){
        return this.id;
    }
    public int getIntId(){
        return this.id.hashCode();
    }
    public void setId(String id){
        this.id = id;
    }

    public int getX(){
        return this.x;
    }
    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }
    public void setY(int y){
        this.y = y;
    }

    public int getTipo(){
        return this.tipo;
    }
    public void setTipo(int tipo){
        this.tipo = tipo;
    }
}