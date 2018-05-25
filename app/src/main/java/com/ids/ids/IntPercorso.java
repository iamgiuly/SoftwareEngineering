package com.ids.ids;

import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;

import java.util.ArrayList;

/**
 * Created by User on 18/05/2018.
 */

public interface IntPercorso {

    ArrayList<Arco> calcolaPercorsoEmergenza(Mappa mappa, Nodo posUtente);

    ArrayList<Arco> calcolaPercorsoNormale(Mappa mappa, Nodo posUtente, Nodo destinazione);
}
