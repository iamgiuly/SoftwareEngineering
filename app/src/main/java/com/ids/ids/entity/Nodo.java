package com.ids.ids.entity;

import com.ids.ids.R;

import java.util.ArrayList;

/**
 * Un nodo è formato da un id, dal suo MACAdress, da posizioni x e y sulla piantina.
 * Inoltre e caratterizzato dal suo tipo: Incendio o Base
 */
public class Nodo implements IntNodo {

    public static final int IMG_BASE = R.drawable.nodo_base;
    public static final int IMG_UTENTE = R.drawable.posizione;
    public static final int IMG_USCITA = R.drawable.nodo_uscita;
    public static final int IMG_INCENDIO = R.drawable.nodo_incendio;

    public static final int DIM = 50;

    private int Id;
    private String BeaconId;
    private int X, Y;       // valori da 0 a 100 che indicano le coordinate relative alla mappa
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

        this.Id = id;
        this.BeaconId = beaconId;
        this.X = x;
        this.Y = y;
        this.tipoUscita = tipoUscita;
        this.tipoIncendio = tipoIncendio;
        this.mappaId = mappaId;

    }

    public int getId(){

        return this.Id;
    }

    public void setId(int id){

        this.Id = id;
    }

    public String getBeaconId(){

        return this.BeaconId;
    }

    public void setId(String beaconId){

        this.BeaconId = beaconId;
    }

    public int getX(){

        return this.X;
    }

    public void setX(int x){

        this.X = x;
    }

    public int getY(){

        return this.Y;
    }

    public void setY(int y){

        this.Y = y;
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

    public int getImage(){

        if(this.tipoIncendio)
            return IMG_INCENDIO;

        if(this.tipoUscita)
            return IMG_USCITA;

        return IMG_BASE;
    }

    /**
	 * Pemette di ottenere la stella (archi associati al nodo)
	 *
	 * @param archi lista degli archi in cui bisogna cercare
	 */
    public ArrayList<Arco> getStella(ArrayList<Arco> archi) {
        ArrayList<Arco> stella = new ArrayList<>();
        for(Arco arco : archi)
            if (arco.getNodoArrivo().equals(this) || arco.getNodoPartenza().equals(this))
                stella.add(arco);
        return stella;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nodo nodo = (Nodo) o;

        return Id == nodo.getId();
    }

    @Override
    public int hashCode() {
        return Id;
    }
}