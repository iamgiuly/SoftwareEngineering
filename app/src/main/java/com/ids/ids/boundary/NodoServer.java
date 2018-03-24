package com.ids.ids.boundary;


public class NodoServer {

    private String BeaconId;
    private int Piano;
    private int X, Y;       // valori da 0 a 100 che indicano le coordinate relative alla mappa
    private int Tipo;

    public String getBeaconId() {

        return BeaconId;
    }

    public int getPiano() {

        return Piano;
    }

    public int getX() {

        return X;
    }

    public int getY() {

        return Y;
    }

    public int getTipo() {

        return Tipo;
    }
}
