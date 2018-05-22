package com.ids.ids.entity;

import java.util.ArrayList;

/**
 * Created by User on 18/05/2018.
 */

public interface IntPercorso {

    ArrayList<Arco> calcolaPercorsoEmergenza(Mappa mappa, Nodo posUtente);

    ArrayList<Arco> calcolaPercorsoNormale(Mappa mappa, Nodo posUtente, Nodo destinazione);
}
