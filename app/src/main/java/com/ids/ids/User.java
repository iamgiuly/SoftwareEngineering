package com.ids.ids;

import android.app.Activity;
import android.content.Context;

import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.ui.MappaView;

public class User implements IntUser{

    private static User instance = null;
    private static final String TAG = "User";

    private MappaView mappaView;
    private Activity context;
    private Mappa mappa;
    private String macAdrs;
    private Nodo nodoDestinazione;
    private int PianoUtente;
    private int modalita;

    public User(Activity contxt) {

        context = contxt;
    }

    /**
     * Permette di cancellare le info presenti nel DB locale
     */
    public void DropDB() {

        if (modalita == User.MODALITA_EMERGENZA || modalita == User.MODALITA_NORMALEPERCORSO)
            mappa.deletemappa(context);
    }


    //GETTERS
    public MappaView getMappaView() {

        return mappaView;
    }

    public int getModalita() {

        return modalita;
    }

    public Mappa getMappa() {

        return mappa;
    }

    public String getMacAdrs(){

        return macAdrs;
    }

    public int getPianoUtente() {

        return PianoUtente;
    }

    public Nodo getPosUtente() {

        return mappa.getNodoSpecifico(macAdrs);
    }

    public Nodo getNodoDestinazione(){

        return nodoDestinazione;
    }

    public Context getContext(){

        return this.context;
    }

    //SETTERS
    public void setModalita(int mod) {

        modalita = mod;
    }

    public void setNodoDestinazione(Nodo destinazione) {

        nodoDestinazione = destinazione;
    }

    public void setMappa(Mappa map) {

        mappa = map;
    }

    public void setMappaView(MappaView mV) {

        mappaView = mV;
    }

    public void setPianoUtente(int piano) {

        PianoUtente = piano;
    }

    public void setMacAdrs(String mac) {

        macAdrs = mac;
    }

    private void setContext(Context contxt) {

        context = (Activity) contxt;
    }

    public static User getInstance(Activity context) {
        if (instance == null)
            instance = new User(context);
        else
            instance.setContext(context);
        return instance;
    }
}