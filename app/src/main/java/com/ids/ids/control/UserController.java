package com.ids.ids.control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.DB.MappaDAO;
import com.ids.ids.entity.Nodo;
import com.ids.ids.DB.NodoDAO;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class UserController extends Application{

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;

    private static UserController instance = null;

    private Activity context;

    private CommunicationServer communicationServer;
    private NodoDAO nodoDAO;
    private ArrayList<Nodo> nodiSelezionati;                // nodi di cui bisogna cambiare il flag "sotto incendio"

    private int modalita;

    private Mappa mappa;

    public UserController(Activity context){
        this.context = context;
        this.communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        this.nodoDAO = NodoDAO.getInstance(context.getApplicationContext());
        this.nodiSelezionati = new ArrayList<>();
        this.modalita = MODALITA_SEGNALAZIONE;
    }

    /**
     * Verifica che l'utente sia connesso alla rete Wi-Fi
     * @return true se l'utente è connesso al Wi-Fi
     */
    public boolean controllaConnessione(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return !DebugSettings.CHECK_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Aggiunge o rimuove dalla lista dei nodi selezionati il nodo con l'id passato come parametro
     * @param nodo nodo da selezionare o deselezionare
     * @return true se c'è almeno un nodo selezionato
     */
    public boolean selezionaNodo(Nodo nodo){
        nodo.setIncendio();

        if(nodo.isCambiato()) {
            System.out.println("cambiato");
            if (!this.nodiSelezionati.contains(nodo)) {
                this.nodiSelezionati.add(nodo);
            }
        }
        else {
            System.out.println(" non Cambiato");
            if (this.nodiSelezionati.contains(nodo)) {
                this.nodiSelezionati.remove(nodo);
            }
        }

        return !this.nodiSelezionati.isEmpty();
    }

    /**
     * I nodi selezionati vengono settati nel db locale come sotto incendio,
     * viene fatto lo stesso nel db remoto inviando una richiesta RESTful al server,
     * quindi la lista dei nodi selezionati viene svuotata
     * @return true se l'operazione ha successo
     */
    public void inviaNodiSelezionati(Context contx){
        for(Nodo nodo : this.nodiSelezionati)
            nodoDAO.update(nodo);    //salvataggio in locale
        this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati, contx);
        //TODO: RESULT
        // if(result)
           // this.clearNodiSelezionati();
        //return result;
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     */
    public Mappa caricaMappa(Context context, String macAddress) {
        // TODO dovrebbe prima leggere la posizione dell'utente tramite bluetooth,
        // TODO poi verificare la connessione:
        // TODO - se attiva la mappa viene richiesta al server inviando la posizione
        // TODO - altrimenti viene presa dal db locale sempre in base alla posizione
        if(DebugSettings.SCAN_BLUETOOTH)
            // TODO evitare riferimenti circolari
            communicationServer.richiestaMappa(context, macAddress);
        else
            mappa = MappaDAO.getInstance(context).find(DebugSettings.PIANO_DEFAULT);
        return mappa;
    }

    public void MandaEmergenzaActivity(){

        Intent intent = new Intent(context , EmergenzaActivity.class);
        context.startActivity(intent);

    }

    public Nodo getPosizioneUtente() {
        // TODO dummy
        ArrayList<Nodo> nodi = nodoDAO.findAll();
        Random random = new Random();
        return nodoDAO.findAll().get(random.nextInt(nodi.size()));
    }

    // TODO spostare nel model
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

    public int getModalita() {
        return modalita;
    }
    public void setModalita(int modalita) {
        this.modalita = modalita;
    }

    public void clearNodiSelezionati() {
        this.nodiSelezionati.clear();
    }

    public Mappa getMappa() {
        return mappa;
    }

    public void setMappa(Mappa mappa) {
        this.mappa = mappa;
    }

    public static UserController getInstance(Activity context){
        if(instance == null)
            instance = new UserController(context);
        return instance;
    }
}
