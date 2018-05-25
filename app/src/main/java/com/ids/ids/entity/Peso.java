package com.ids.ids.entity;

/**
 * 	Un peso è caratterizzato da una descrizione e dal valore di quel peso.
 *  Il valore è espresso da un nmumro intero
 */
public class Peso {

    private int id;
    private String descrizione;
    private int peso;

    public Peso(int id, String descrizione, int peso) {
        this.id = id;
        this.descrizione = descrizione;
        this.peso = peso;
    }

    public int getId(){

        return this.id;
    }

    public void setId(int id){

        this.id = id;
    }

    public String getDescrizione() {

        return descrizione;
    }

    public void setDescrizione(String descrizione) {

        this.descrizione = descrizione;
    }

    public int getPeso() {

        return peso;
    }

    public void setPeso(int peso) {

        this.peso = peso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peso peso = (Peso) o;

        return id == peso.id;
    }

    @Override
    public int hashCode() {

        return id;
    }
}