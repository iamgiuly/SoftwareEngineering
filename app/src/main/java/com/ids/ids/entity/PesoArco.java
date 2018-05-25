package com.ids.ids.entity;

/**
 * Un pesoArco è caratterizzato dall id dell arco a cui è associato, un peso, e il valore di quanto quest ultimo
 * incide sull arco
 */
public class PesoArco {

    private int id;
    private int idArco;
    private Peso peso;
    private int valore;

    public PesoArco(int id, int idArco, Peso peso, int valore) {
        this.id = id;
        this.idArco = idArco;
        this.peso = peso;
        this.valore = valore;
    }

    public int getId(){

        return this.id;
    }

    public void setId(int id){

        this.id = id;
    }

    public int getIdArco() {

        return idArco;
    }

    public void setIdArco(int idArco) {

        this.idArco = idArco;
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
