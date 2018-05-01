package com.ids.ids.control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;

import com.ids.ids.DB.MappaDAO;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.Percorso;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.ui.MainActivity;
import com.ids.ids.ui.MappaView;
import com.ids.ids.ui.NormaleActivity;

public class UserController extends Application {

    private static UserController instance = null;
    private static final String TAG = "UserController";

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;
    public static final int MODALITA_NORMALE = 2;

    private Activity context;
    private MappaView mappaView;
    private CommunicationServer communicationServer;
    private Localizzatore localizzatore;
    private Mappa mappa;
    private String macAdrs;
    private ArrayList<Nodo> nodiSelezionati; // nodi di cui bisogna cambiare il flag "sotto incendio"
    private int PianoUtente;
    private int modalita;

    public UserController(Activity contxt) {
        context = contxt;
        communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        nodiSelezionati = new ArrayList<>();
    }

    public void DropDB() {

        if(modalita == UserController.MODALITA_EMERGENZA)
           mappa.deletemappa(context);
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente inviando una richiesta al server,
     * passando a questo la posizione dell'utente raffigurata dall'id (MACaddress) del beacon
     */
    public void richiestaMappa(Context context, String macAddress) {

        macAdrs = macAddress;
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

            Log.i(TAG, "Percorso in locale");
            Mappa mappaAggiornata = MappaDAO.getInstance(context).find(PianoUtente);

            Percorso p = Percorso.getInstance();
            percorso = p.calcolaPercorso(mappaAggiornata, mappaAggiornata.getPosUtente(mac));
        }

        mappaView.setPosUtente(mappa.getPosUtente(mac));
        mappaView.setPercorso(percorso);

        //Nel caso in cui il percorso sia zero significa che
        //l utente ha raggiunto l uscita
        //per questo lo avvisiamo attraverso un messaggio
        if (percorso.size() == 0) {
            localizzatore.stopFinderALWAYS();
            richiestaAggiornamento(false);
            mappaView.messaggio("Sei al sicuro", "Hai raggiunto l uscita", false);
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
            if (!nodiSelezionati.contains(nodo))
                nodiSelezionati.add(nodo);
        } else if (nodiSelezionati.contains(nodo))
            nodiSelezionati.remove(nodo);

        return !nodiSelezionati.isEmpty();
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

    public void MandaNormaleActivity() {

        Intent intent = new Intent(context, NormaleActivity.class);
        context.startActivity(intent);
    }

    public void clearNodiSelezionati() {

        nodiSelezionati.clear();
    }

    public int getModalita() {

        return modalita;
    }

    public void setModalita(int mod) {

        modalita = mod;
    }

    public Mappa getMappa() {

        return mappa;
    }

    public void setMappa(Mappa map) {

        mappa = map;
    }

    public void setMappaView(MappaView mV) {

        mappaView = mV;
    }

    public MappaView getMappaView() {

        return mappaView;
    }

    public void setPianoUtente(int piano) {

        PianoUtente = piano;
    }

    public void setLocalizzatore(Localizzatore loc) {

        localizzatore = loc;
    }

    public String getMacAdrs(){

        return macAdrs;
    }

    public static UserController getInstance(Activity context) {
        if (instance == null)
            instance = new UserController(context);
        return instance;
    }
}