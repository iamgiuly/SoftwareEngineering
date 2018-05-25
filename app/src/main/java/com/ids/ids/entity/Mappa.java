package com.ids.ids.entity;

import android.content.Context;

import com.ids.ids.DB.MappaDAO;

import java.util.ArrayList;

/**
 * Una mappa è formata da una serie di Nodi e archi, e viene caratterizzata da una piantina e un piano.
 */
public class Mappa implements IntMappa {

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

    /**
     * Ottiene i nodi uscita relativi alla mappa
     * @return
     */
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

    /**
	 * Permette di ottenere un determinato nodo dalla lista dei nodi associata alla mappa
     * @param macAdrs MACAdress del nodo che si intende avere
	 */
    public Nodo getNodoSpecifico(String macAdrs){

        Nodo result = null;

        for(Nodo n: Nodi)
            if(n.getBeaconId().equals(macAdrs))
                result = n;

        return result;
    }

    /**
     * Permette di salvare le informazioni relative alla mappa sul DB
     * @param contxt
     */
    public void salvataggioLocale(Context contxt){

        ArrayList<Mappa>  esito;

        MappaDAO m = MappaDAO.getInstance(contxt);
        esito = m.findAllByColumnValue("piano", String.valueOf(Piano));

        if(esito.size() == 0 ){
            System.out.println("Salvataggio");
            m.insert(this);
        }
        else {
            System.out.println("Update");
            m.update(this);
        }
    }

    /**
     * Pemette di eleminare le info della mappa dal DB
     * @param contxt
     */
    public void deletemappa(Context contxt){

        MappaDAO m = MappaDAO.getInstance(contxt);
        m.delete(Piano);
    }
}
