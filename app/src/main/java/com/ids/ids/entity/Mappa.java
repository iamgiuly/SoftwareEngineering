package com.ids.ids.entity;

import android.content.Context;

import com.ids.ids.DB.MappaDAO;

import java.util.ArrayList;

public class Mappa {

    private int Piano;
    private String Piantina;        //nome dell'immagine
    private ArrayList<Nodo> Nodi;   //TODO eliminare, avremo un getNodi() che recupera l'ArrayList dei nodi dagli archi
    private ArrayList<Arco> Archi;

    public Mappa(int piano, String piantina, ArrayList<Nodo> nodi, ArrayList<Arco> archi) {

        Piano = piano;
        Piantina = piantina;
        Nodi = nodi;
        Archi = archi;
    }

    public ArrayList<Arco> calcolaPercorso(){
        //TODO Dijkstra
        return null;
    }

    public int getPiano(){

        return Piano;
    }

    public void setPiano(int piano){

        Piano = piano;
    }

    public String getPiantina(){

        return Piantina;
    }
    public void setPiantina(String piantina){

        Piantina = piantina;
    }

    public ArrayList<Nodo> getNodi(){

        return Nodi;
    }

    public void setNodi(ArrayList<Nodo> nodi){

        Nodi = nodi;
    }

    //TODO sostituirà getNodi()
    public ArrayList<Nodo> getNodiFromArchi(){
        ArrayList<Nodo> nodi = new ArrayList<>();
        for(Arco arco : Archi){
            Nodo nodoPartenza = arco.getNodoPartenza();
            if(!isNodoInArrayList(nodoPartenza, nodi))
                nodi.add(nodoPartenza);
            Nodo nodoArrivo = arco.getNodoArrivo();
            if(!isNodoInArrayList(nodoArrivo, nodi))
                nodi.add(nodoArrivo);
            nodi.add(arco.getNodoArrivo());
        }
        return Nodi;
    }

    private boolean isNodoInArrayList(Nodo nodo, ArrayList<Nodo> nodi){
        for(Nodo n : nodi)
            if(n.getId() == nodo.getId())
                return true;
        return false;
    }

    public ArrayList<Nodo> getNodiUscita() {
        ArrayList<Nodo> uscite = new ArrayList<>();
        for(Nodo nodo : Nodi)
            if(nodo.isTipoUscita())
                uscite.add(nodo);
        return uscite;
    }

    public ArrayList<Arco> getArchi() {

        return Archi;
    }

    public void setArchi(ArrayList<Arco> archi) {

        Archi = archi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mappa mappa = (Mappa) o;

        return Piano == mappa.Piano;
    }

    public Nodo getPosUtente(String macAdrs){

        Nodo result = null;

        for(Nodo n: Nodi)
            if(n.getBeaconId().equals(macAdrs))
                result = n;

        return result;
    }

    public void salvataggioLocale(Context contxt){

        ArrayList<Mappa>  esito;

        MappaDAO m = MappaDAO.getInstance(contxt);
        esito = m.findAllByColumnValue("piano",String.valueOf(this.Piano));

        if(esito.size() == 0 ){
            System.out.println("Salvataggio");
            m.insert(this);
        }
        else {
            System.out.println("Update");
            m.update(this);
        }

    }
}
