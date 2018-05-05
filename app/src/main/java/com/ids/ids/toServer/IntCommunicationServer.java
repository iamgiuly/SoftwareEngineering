package com.ids.ids.toServer;

import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.ui.MappaView;

import java.util.ArrayList;

/**
 * Interfaccia implementata da parte della classe CommunicationServer
 */
public interface IntCommunicationServer {

    void inviaNodiSottoIncendio(ArrayList<Nodo> nodi);

    void richiestaPercorsoNormale(String macPosU, int piano, MappaView mv, Mappa map, String macDest, boolean enable);

    void richiediPercorsoEmergenza(String macPosU, int piano, MappaView mV, Mappa map);

    void richiestaMappa(String macPosU);

    void richiestaAggiornamenti(Boolean enable, int piano);

}
