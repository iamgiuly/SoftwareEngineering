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
    public static final int MODALITA_NORMALEPERCORSO = 3;

    private CommunicationServer communicationServer;
    private Localizzatore localizzatore;
    private MappaView mappaView;
    private Activity context;
    private Mappa mappa;
    private String macAdrs;
    private Nodo nodoDestinazione;
    private int PianoUtente;
    private int modalita;

    public UserController(Activity contxt) {

        context = contxt;
        communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
    }

    public void DropDB() {

        if (modalita == UserController.MODALITA_EMERGENZA || modalita == UserController.MODALITA_NORMALEPERCORSO)
            mappa.deletemappa(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void richiediPercorsoEmergenza(String mac) {

        ArrayList<Arco> percorso;

        percorso = communicationServer.richiediPercorso(mac, PianoUtente);

        if (percorso == null) {

            Log.i(TAG, "Percorso emergenza in locale");
            Mappa mappaAggiornata = MappaDAO.getInstance(context).find(PianoUtente);

            Percorso p = Percorso.getInstance();
            percorso = p.calcolaPercorso(mappaAggiornata, mappaAggiornata.getNodoSpecifico(mac));
        }

        mappaView.setPosUtente(mappa.getNodoSpecifico(mac));
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

    //GETTERS
    public MappaView getMappaView() {

        return mappaView;
    }

    public int getModalita() {

        return modalita;
    }

    public Mappa getMappa() {

        return mappa;
    }

    public String getMacAdrs(){

        return macAdrs;
    }

    public int getPianoUtente() {

        return PianoUtente;
    }

    public Nodo getPosUtente() {

        return mappa.getNodoSpecifico(macAdrs);
    }

    public Nodo getNodoDestinazione(){

        return nodoDestinazione;
    }

    //SETTERS
    public void setModalita(int mod) {

        modalita = mod;
    }

    public void setNodoDestinazione(Nodo destinazione) {

        nodoDestinazione = destinazione;
    }

    public void setMappa(Mappa map) {

        mappa = map;
    }

    public void setMappaView(MappaView mV) {

        mappaView = mV;
    }

    public void setPianoUtente(int piano) {

        PianoUtente = piano;
    }

    public void setLocalizzatore(Localizzatore loc) {

        localizzatore = loc;
    }

    public void setMacAdrs(String mac) {

        macAdrs = mac;
    }

    private void setContext(Context contxt) {

        context = (Activity) contxt;
    }

    public static UserController getInstance(Activity context) {
        if (instance == null)
            instance = new UserController(context);
        else
            instance.setContext(context);
        return instance;
    }
}