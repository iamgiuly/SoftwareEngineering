package com.ids.ids.ui;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;

import com.ids.ids.R;
import com.ids.ids.toServer.CommunicationServer;
import com.ids.ids.beacon.Localizzatore;
import com.ids.ids.User;
import com.ids.ids.entity.Nodo;

/**
 * Questa Activity consente all utente
 * 1) di scegliele la destinazione
 * 2) una volta scelta permette la visualizzazione del percorso per raggiungere il nodo scelto
 */
public class NormaleActivity extends AppCompatActivity {

    private User user;
    private CommunicationServer communicationServer;
    private Localizzatore localizzatore;
    private MappaView mappaView;
    private Nodo nodoDestinazione;
    private Button RichiediPercorsoButton;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normale);

        user = User.getInstance(this);
        localizzatore = Localizzatore.getInstance(this);
        communicationServer = CommunicationServer.getInstance(this);

        RichiediPercorsoButton = findViewById(R.id.RichiediPercorsoButton);
        RichiediPercorsoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richiediPercorsoDestinazione();
            }
        });

        // inizializza la View della mappa
        mappaView = findViewById(R.id.mappaViewNormale);
        user.setMappaView(mappaView);

        try {

            //CASO SCELTA DESTINAZIONE
            if (user.getModalita() == User.MODALITA_NORMALE) {

                mappaView.setMappa(user.getMappa(), true);
                mappaView.setPosUtente(user.getPosUtente());

                mappaView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
                            return true;
                        NodoView nodoView = mappaView.getNodoPremuto((int) motionEvent.getX(), (int) motionEvent.getY());
                        if (nodoView != null)
                            listenerNodoSelezionato(nodoView);
                        mappaView.invalidate();
                        return true;

                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {

        super.onDestroy();
        localizzatore.stopFinderALWAYS();
        user.DropDB();
        mappaView.deleteImagePiantina();
    }

    //per finish()
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {

        super.onStop();
    }

    /*
     * Listener per i nodi selezionati.
     * Dispone dei vari controlli
     * 1) vede se la destinazione coincide con la posizione attuale dell utente
     * 2) se si seleziona una sola destinazione
     * 3) se è stata selezionata una sola destinazione
     */
    private void listenerNodoSelezionato(NodoView nodoView) {

        Nodo nodo = nodoView.getNodo();

        if (nodo.getBeaconId() == user.getPosUtente().getBeaconId())
            mappaView.messaggio("Attenzione!", "Si trova già nel posto segnalato", false);
        else if (nodoDestinazione == null) {
            nodoDestinazione = nodo;
            nodoView.setImage(R.drawable.destinazione);
            RichiediPercorsoButton.setVisibility(View.VISIBLE);
        } else if (nodo.getBeaconId() == nodoDestinazione.getBeaconId()) {  //deselezione
            nodoView.setImage(nodo.getImage());
            nodoDestinazione = null;
            RichiediPercorsoButton.setVisibility(View.INVISIBLE);
        } else if (nodoDestinazione != null)
            mappaView.messaggio("Attenzione!", "E' gia segnata una destinazione.\nDeselezionarla per cambiare", true);
    }

    /*
     * Listener del bottone richiedi percorso.
     * Si occupa di richiamare il metodo adatto del CommunicationServer per inviare la richiesta al server
     * Attiva inoltre la ricerca Always del Localizzatore
     */
    private void richiediPercorsoDestinazione() {

        user.setModalita(User.MODALITA_NORMALEPERCORSO);
        user.setNodoDestinazione(nodoDestinazione);
        communicationServer.richiestaPercorsoNormale(user.getMacAdrs(),
                user.getPianoUtente(),
                mappaView,
                user.getMappa(),
                nodoDestinazione.getBeaconId(),
                true);
        localizzatore.startFinderALWAYS();
        RichiediPercorsoButton.setVisibility(View.INVISIBLE);
    }
}
