package com.ids.ids.control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ids.ids.DB.MappaDAO;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.DB.NodoDAO;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;

public class UserController extends Application {

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;

    private static UserController instance = null;

    private Activity context;
    private int modalita;

    private CommunicationServer communicationServer;
    private NodoDAO nodoDAO;
    private ArrayList<Nodo> nodiSelezionati; // nodi di cui bisogna cambiare il flag "sotto incendio"
    private Mappa mappa;

    public UserController(Activity contxt) {
        context = contxt;
        communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        // nodoDAO = NodoDAO.getInstance(context.getApplicationContext());
        nodiSelezionati = new ArrayList<>();
        modalita = MODALITA_SEGNALAZIONE;
    }

    public ArrayList<Arco> richiediPercorso(String mac, Localizzatore l) {

        return communicationServer.richiediPercorso(mac, mappa.getPiano(), l);
    }

    public void salvataggioDB(Mappa mappa_scaricata) {

        System.out.println("Salvataggio");
        MappaDAO.getInstance(context).insert(mappa_scaricata);
    }


/*
    /**
     * Verifica che l'utente sia connesso alla rete Wi-Fi
     *
     * @return true se l'utente è connesso al Wi-Fi

    public boolean controllaConnessione() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return !DebugSettings.CHECK_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }*/

    /**
     * Aggiunge o rimuove dalla lista dei nodi selezionati il nodo con l'id passato come parametro
     *
     * @param nodo nodo da selezionare o deselezionare
     * @return true se c'è almeno un nodo selezionato
     */
    public boolean selezionaNodo(Nodo nodo) {
        nodo.setIncendio();

        if (nodo.isCambiato()) {
            System.out.println("cambiato");
            if (!this.nodiSelezionati.contains(nodo)) {
                this.nodiSelezionati.add(nodo);
            }
        } else {
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
     *
     * @return true se l'operazione ha successo
     */
    public void inviaNodiSelezionati(Context contx) {

        communicationServer.inviaNodiSottoIncendio(nodiSelezionati, contx);
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     */
    public void caricaMappa(Context context, String macAddress) {

        communicationServer.richiestaMappa(context, macAddress);
    }

    public void MandaEmergenzaActivity() {

        Intent intent = new Intent(context, EmergenzaActivity.class);
        context.startActivity(intent);
    }

 /*   public Nodo getPosizioneUtente() {
        // TODO dummy
        ArrayList<Nodo> nodi = nodoDAO.findAll();
        Random random = new Random();
        return nodoDAO.findAll().get(random.nextInt(nodi.size()));
    }
*/

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

    public static UserController getInstance(Activity context) {
        if (instance == null)
            instance = new UserController(context);
        return instance;
    }
}
