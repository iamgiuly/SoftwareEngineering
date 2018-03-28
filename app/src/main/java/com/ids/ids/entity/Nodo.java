package com.ids.ids.entity;

import android.util.Log;

import com.ids.ids.ui.R;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;

public class Nodo {

    public static final int IMG_BASE = R.drawable.nodo_base;
    public static final int IMG_UTENTE = R.drawable.posizione;
    public static final int IMG_USCITA = R.drawable.nodo_uscita;
    public static final int IMG_INCENDIO = R.drawable.nodo_incendio;

    public static final int DIM = 50;

    private int id;
    private String beaconId;
    private int x, y;       // valori da 0 a 100 che indicano le coordinate relative alla mappa
    private boolean tipoUscita;
    private boolean tipoIncendio;
    private int mappaId;

    private boolean cambiato = false;       // se true significa che il valore di tipoIncendio è stato cambiato
                                            // (serve a decidere se mostrare il bottone "Invia Nodi")

    public Nodo(int id, String beaconId, int x, int y, int mappaId) {
        this(id, beaconId, x, y, false, false, mappaId);
    }

    public Nodo(int id, String beaconId, int x, int y, boolean tipoUscita, int mappaId) {
        this(id, beaconId, x, y, tipoUscita, false, mappaId);
    }

    public Nodo(int id, String beaconId, int x, int y, boolean tipoUscita, boolean tipoIncendio, int mappaId) {

        this.id = id;
        this.beaconId = beaconId;
        this.x = x;
        this.y = y;
        this.tipoUscita = tipoUscita;
        this.tipoIncendio = tipoIncendio;
        this.mappaId = mappaId;

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

    public int getMappaId() {
        return mappaId;
    }
    public void setMappaId(int mappaId){
        this.mappaId = mappaId;
    }

    public void setIncendio(){
        this.tipoIncendio = !this.tipoIncendio;
        this.cambiato = !this.cambiato;
    }

    public boolean isCambiato() {
        return cambiato;
    }

    public boolean isTipoUscita() {
        return tipoUscita;
    }
    public boolean isTipoIncendio() {
        return tipoIncendio;
    }

    // L'ordine in cui vengono associate le immagini è importante per le priorità
    // (più valori possono essere true contemporaneamente)
    // TODO gestire anche posizione utente
    public int getImage(){
        if(this.tipoIncendio) {
            System.out.println("Tipo incendio");
            return IMG_INCENDIO;
        }
        if(this.tipoUscita) {
            return IMG_USCITA;
        }
        return IMG_BASE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nodo nodo = (Nodo) o;

        return id == nodo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public ArrayList<Arco> getStella(ArrayList<Arco> archi) {
        ArrayList<Arco> stella = new ArrayList<>();
        for(Arco arco : archi)
            if (arco.getNodoArrivo().equals(this) || arco.getNodoPartenza().equals(this))
                stella.add(arco);
        return stella;
    }
}