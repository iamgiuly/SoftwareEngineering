package com.ids.ids.control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;

import com.ids.ids.DB.DBHelper;
import com.ids.ids.DB.MappaDAO;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.Percorso;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.ui.MainActivity;
import com.ids.ids.ui.MappaView;

public class UserController extends Application {

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;

    private Activity context;

    private static UserController instance = null;
    private CommunicationServer communicationServer;
    private ArrayList<Nodo> nodiSelezionati; // nodi di cui bisogna cambiare il flag "sotto incendio"
    private Mappa mappa;
    private Localizzatore localizzatore;
    private DBHelper db;
    private MappaView mappaView;
    private int PianoUtente;
    private int modalita;

    public UserController(Activity contxt) {
        context = contxt;
        communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        nodiSelezionati = new ArrayList<>();
        modalita = MODALITA_SEGNALAZIONE;
    }

    public void DropDB() {

        mappa.deletemappa(context);
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     */
    public void richiestaMappa(Context context, String macAddress) {

        communicationServer.richiestaMappa(context, macAddress);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void richiediPercorso(String mac) {

        ArrayList<Arco> percorso;

        percorso = communicationServer.richiediPercorso(mac, PianoUtente);

        if (percorso == null) {

            System.out.println("Connessione caduta --> Bisogna prendere il locale");
            Mappa mappaAggiornata = MappaDAO.getInstance(context).find(PianoUtente);

            Percorso p = Percorso.getInstance();
            percorso = p.calcolaPercorso(mappaAggiornata, mappaAggiornata.getPosUtente(mac));
            System.out.println("percorso preso in locale size: " + percorso.size());
        }

        mappaView.setPosUtente(mappa.getPosUtente(mac));
        mappaView.setPercorso(percorso);

        //Nel caso in cui il percorso sia zero significa che
        //l utente ha raggiunto l uscita
        //per questo lo avvisiamo attraverso un messaggio
        if (percorso.size() == 0) {
            localizzatore.stopFinderALWAYS();
            richiestaAggiornamento(false);
            mappaView.messaggio("Sei sicuro", "Hai raggiunto l uscita");
        }

        try {

            mappaView.postInvalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void richiestaAggiornamento(Boolean enable) {

        communicationServer.richiestaAggiornamenti(enable, PianoUtente);
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
            if (!this.nodiSelezionati.contains(nodo))
                this.nodiSelezionati.add(nodo);
        } else if (this.nodiSelezionati.contains(nodo))
            this.nodiSelezionati.remove(nodo);

        return !this.nodiSelezionati.isEmpty();
    }


    public void MandaEmergenzaActivity() {

        Intent intent = new Intent(context, EmergenzaActivity.class);
        context.startActivity(intent);
    }

    public void MandaMainActivity() {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("AvviaTastoEmergenza", true);
        context.startActivity(intent);
    }

    public void clearNodiSelezionati() {

        this.nodiSelezionati.clear();
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

    public void setMappaView(MappaView mappaView) {

        this.mappaView = mappaView;
    }

    public MappaView getMappaView() {

      return mappaView;
    }

    public void setPianoUtente(int piano) {

        PianoUtente = piano;
    }

    public void setLocalizzatore(Localizzatore loc){

        localizzatore = loc;
    }

    public static UserController getInstance(Activity context) {
        if (instance == null)
            instance = new UserController(context);
        return instance;
    }
}