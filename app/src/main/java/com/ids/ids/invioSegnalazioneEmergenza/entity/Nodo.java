package com.ids.ids.invioSegnalazioneEmergenza.entity;

public class Nodo {

    public static final int TIPO_BASE = 0;
    public static final int TIPO_UTENTE = 1;
    public static final int TIPO_USCITA = 2;
    public static final int TIPO_INCENDIO = 3;

    private int id;
    private String beaconId;
    private int x, y;       // valori da 0 a 100 che indicano le coordinate relative alla mappa
    private int tipo;

    public Nodo(int id, String beaconId, int x, int y, int tipo) {
        this.id = id;
        this.beaconId = beaconId;
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getBeaconId(){
        return this.beaconId;
    }
    public void setId(String beaconId){
        this.beaconId = beaconId;
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