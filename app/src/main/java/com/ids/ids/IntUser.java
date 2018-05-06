package com.ids.ids;

import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.ui.MappaView;

/**
 * Interfaccia implementata dalla classe User
 */

public interface IntUser {

    //COSTANTI
    int MODALITA_SEGNALAZIONE = 0;
    int MODALITA_EMERGENZA = 1;
    int MODALITA_NORMALE = 2;
    int MODALITA_NORMALEPERCORSO = 3;

    //GETTERS
    MappaView getMappaView();

    int getModalita();

    Mappa getMappa();

    String getMacAdrs();

    int getPianoUtente();

    Nodo getPosUtente();

    Nodo getNodoDestinazione();

    //SETTERS
    void setModalita(int mod);

    void setNodoDestinazione(Nodo destinazione);

    void setMappa(Mappa map);

    void setMappaView(MappaView mV);

    void setPianoUtente(int piano);

    void setMacAdrs(String mac);

}
