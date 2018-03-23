package com.ids.ids.boundary;

/**
 * Created by User on 23/03/2018.
 */

public class NodoServer {


    private String BeaconId;
    private int Piano;
    private int X, Y;       // valori da 0 a 100 che indicano le coordinate relative alla mappa
    private int Tipo;



    public NodoServer(String beaconId, int piano, int x, int y, int tipo) {


        BeaconId = beaconId;
        Piano = piano;
        X = x;
        Y = y;
        Tipo = tipo;

    }



    public String getBeaconId() {
        return BeaconId;
    }



    public void setBeaconId(String beaconId) {
        BeaconId = beaconId;
    }



    public int getPiano() {
        return Piano;
    }



    public void setPiano(int piano) {
        Piano = piano;
    }



    public int getX() {
        return X;
    }



    public void setX(int x) {
        X = x;
    }



    public int getY() {
        return Y;
    }



    public void setY(int y) {
        Y = y;
    }



    public int getTipo() {
        return Tipo;
    }



    public void setTipo(int tipo) {
        Tipo = tipo;
    }





}
