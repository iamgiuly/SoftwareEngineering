package com.ids.ids.entity;

import java.util.ArrayList;

/**
 * Created by User on 25/05/2018.
 */

public interface IntNodo {

    int getId();

    void setId(int id);

    String getBeaconId();

    void setId(String beaconId);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getMappaId();

    void setMappaId(int mappaId);

    void setIncendio();

    boolean isCambiato();

    boolean isTipoUscita() ;

    boolean isTipoIncendio();

    int getImage();

    ArrayList<Arco> getStella(ArrayList<Arco> archi);
}
