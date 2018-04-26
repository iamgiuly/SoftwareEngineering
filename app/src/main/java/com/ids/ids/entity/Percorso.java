package com.ids.ids.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Percorso {

    private static Percorso instance = null;

    private Percorso (){

    }

    public ArrayList<Arco> calcolaPercorso(Mappa mappa, Nodo posUtente) {

        ArrayList<Nodo> uscite = mappa.getNodiUscita();
        ArrayList<Nodo> nodi = mappa.getNodi();
        ArrayList<Arco> archiGrafo = mappa.getArchi();
        ArrayList<Arco> archi = new ArrayList<>();

        // considera solo gli archi non incidenti a nodi sotto incendio
        for (Arco arco : archiGrafo)
            if(!arco.getNodoPartenza().isTipoIncendio() && !arco.getNodoArrivo().isTipoIncendio())
                archi.add(arco);
        Nodo migliorUscita = null;

        // algoritmo di Dijkstra: inizializza valori
        Map<Nodo, Integer> costi = new HashMap<>();
        Map<Nodo, Nodo> nodoPrev = new HashMap<>();
        Map<Nodo, Arco> arcoPrev = new HashMap<>();
        ArrayList<Nodo> nodiLocali = new ArrayList<>();

        // copia i nodi della mappa nella lista di nodi locali e setta la distanza a -1 (non ancora calcolata)
        for (Nodo nodo : nodi) {
            nodiLocali.add(nodo);
            costi.put(nodo, -1);
        }

        // il costo per andare dal nodo sorgente (posizione utente) e se stesso è zero per definizione
        costi.put(posUtente, 0);


        while (nodiLocali.size() > 0) {
            // restituisci e rimuovi il nodo con minor costo
            Nodo migliorNodo = getMigliorNodo(nodiLocali, costi);

            nodiLocali.remove(migliorNodo);

            // calcoliamo il costo per tutti i nodi adiacenti
            for (Arco arcoVicino : migliorNodo.getStella(archi)) {
                Nodo nodoVicino = null;
                if (arcoVicino.getNodoArrivo().equals(migliorNodo))
                    nodoVicino = arcoVicino.getNodoPartenza();
                else
                    nodoVicino = arcoVicino.getNodoArrivo();
                if (nodiLocali.contains(nodoVicino)) {
                    int costo = costi.get(migliorNodo) + arcoVicino.getCosto();
                    int costoVicino = costi.get(nodoVicino);

                    if (costoVicino == -1 || costo < costoVicino) {
                        costi.put(nodoVicino, costo);
                        nodoPrev.put(nodoVicino, migliorNodo);
                        arcoPrev.put(nodoVicino, arcoVicino);
                    }
                }
            }

            if (uscite.contains(migliorNodo) && ((migliorUscita == null || costi.get(migliorNodo) < costi.get(migliorUscita))))
                migliorUscita = migliorNodo;
        }

        ArrayList<Arco> percorso = new ArrayList<>();
        try {
            Nodo nodoProssimo = migliorUscita;
            while (nodoPrev.containsKey(nodoProssimo)) {
                percorso.add(arcoPrev.get(nodoProssimo));
                nodoProssimo = nodoPrev.get(nodoProssimo);
            }
        } catch (Exception e) {}

        return percorso;
    }

    private Nodo getMigliorNodo(ArrayList<Nodo> nodiLocali, Map<Nodo, Integer> costi){
        int costo = -1;
        Nodo migliorNodo = null;

        for(Nodo nodo : nodiLocali){
            int costoNodo = costi.get(nodo);
            if((costo == -1) || (costoNodo != -1 && costoNodo < costo)){
                migliorNodo = nodo;
                costo = costoNodo;
            }
        }
        return migliorNodo;
    }

    public static Percorso getInstance() {
        if (instance == null)
            instance = new Percorso();
        return instance;
    }
}
