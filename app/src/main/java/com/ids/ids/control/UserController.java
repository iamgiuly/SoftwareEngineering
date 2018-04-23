package com.ids.ids.control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.ids.ids.DB.MappaDAO;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.Percorso;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.ui.MainActivity;
import com.ids.ids.ui.MappaView;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;

public class UserController extends Application {

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;

    private static UserController instance = null;

    private Activity context;
    private int modalita;

    private CommunicationServer communicationServer;
    private ArrayList<Nodo> nodiSelezionati; // nodi di cui bisogna cambiare il flag "sotto incendio"
    private Mappa mappa;
    private int PianoUtente;

    private UserController(Activity contxt) {
        context = contxt;
        communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        nodiSelezionati = new ArrayList<>();
        modalita = MODALITA_SEGNALAZIONE;
    }

    public void DropDB() {


    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     */
    public void richiestaMappa(Context context, String macAddress) {

        communicationServer.richiestaMappa(context, macAddress);
    }

    public void richiediPercorso(String mac, MappaView mappaView) {

        ArrayList<Arco> percorso;

        //TODO:MIGLIORARE PERCHè IN MAPPA IO HO QUELLA SCARICATA AL PRIMO ACCESSO
        percorso = communicationServer.richiediPercorso(mac, PianoUtente);

        if (percorso == null) {

            //TODO: MIGLIORARE ANCHE PERCHE mappa è QUELLA SCARICATA A PRIMO ACCESSO
            System.out.println("Connessione caduta --> Bisogna prendere il locale");
            Mappa mappaAggiornata = MappaDAO.getInstance(context).find(PianoUtente);

            Percorso p = Percorso.getInstance();
            percorso = p.calcolaPercorso(mappaAggiornata, mappaAggiornata.getPosUtente(mac));
            System.out.println("percorso preso in locale size: " + percorso.size());
        }

        Nodo posUtente = mappa.getPosUtente(mac);
        mappaView.setPosUtente(posUtente);
        mappaView.setPercorso(percorso);

        try {
            mappaView.postInvalidate();
        } catch (Exception e) {
        }
    }

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


    public void MandaEmergenzaActivity() {

        Intent intent = new Intent(context, EmergenzaActivity.class);
        context.startActivity(intent);
    }

    public void MandaMainActivity() {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("AvviaTastoEmergenza",true);
        context.startActivity(intent);

    }

    public void clearNodiSelezionati() {

        this.nodiSelezionati.clear();
    }

    public ArrayList<Nodo> getNodiSelezionati() {
        return nodiSelezionati;
    }

    public int getModalita() {

        return modalita;
    }

    public void setModalita(int modalita) {

        this.modalita = modalita;
    }

    public Mappa getMappa() {

        return mappa;
    }

    public void setMappa(Mappa mappa) {

        this.mappa = mappa;
    }

    public void setPianoUtente(int piano) {

        this.PianoUtente = piano;
    }

    public int getPianoUtente() {

        return this.PianoUtente;
    }


    public static UserController getInstance(Activity context) {
        if (instance == null)
            instance = new UserController(context);
        return instance;
    }
}