package com.ids.ids.entity;

import android.content.Context;

import java.util.ArrayList;

/**
 * Interfaccia implementata dalla classe mappa
 */
public interface IntMappa {

    int getPiano();

    void setPiano(int piano);

    String getPiantina();

    void setPiantina(String piantina);

    ArrayList<Nodo> getNodi();

    void setNodi(ArrayList<Nodo> nodi);

    ArrayList<Nodo> getNodiUscita();

    ArrayList<Arco> getArchi();

    void setArchi(ArrayList<Arco> archi);

    Nodo getNodoSpecifico(String macAdrs);

    void salvataggioLocale(Context contxt);

    void deletemappa(Context contxt);
}
