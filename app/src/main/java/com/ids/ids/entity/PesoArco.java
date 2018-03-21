package com.ids.ids.entity;

//TODO diverso dalla tabella, manca l'arco (Arco ha PesoArco), aggiungere id
public class PesoArco {

    private Peso peso;
    private int valore;

    public PesoArco(Peso peso, int valore) {
        this.peso = peso;
        this.valore = valore;
    }

    public Peso getPeso() {
        return peso;
    }
    public void setPeso(Peso peso) {
        this.peso = peso;
    }

    public int getValore() {
        return valore;
    }
    public void setValore(int valore) {
        this.valore = valore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PesoArco pesoArco = (PesoArco) o;

        if (valore != pesoArco.valore) return false;
        return peso.equals(pesoArco.peso);
    }

}
