package com.ids.ids.entity;

public class Peso {

    private String descrizione;
    private int peso;

    public Peso(String descrizione, int peso) {
        this.descrizione = descrizione;
        this.peso = peso;
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

        return descrizione != null ? descrizione.equals(peso.descrizione) : peso.descrizione == null;
    }

}