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

    public String getId(){
        return this.id;
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

    public String getTipo(){
        return this.tipo;
    }
    public void setTipo(String tipo){
        this.tipo = tipo;
    }
}