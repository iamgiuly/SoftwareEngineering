package com.ids.ids.ui;

/**
 * Created by User on 01/05/2018.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;

import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.control.Localizzatore;
import com.ids.ids.control.UserController;
import com.ids.ids.entity.Nodo;


/**
 * Questa activity viene mostrata al tap sul bottone "Segnala Emergenza",
 * al suo avvio carica la mappa in cui si trova l'utente e la visualizza con i suoi nodi opportunamente contrassegnati,
 * ad essi vengono associati dei listener, che al tap richiamano il metodo listenerNodoSelezionato() il quale
 * chiede al Controller di aggiungere / rimuovere il nodo premuto alla / dalla lista dei nodi selezionati.
 * Al tap sul bottone "Invia Nodi" (visibile se almeno un nodo è selezionato),
 * viene chiesto al Controller di inviare al server i nodi selezionati
 */
public class NormaleActivity extends AppCompatActivity {

    private MappaView mappaView;
    private UserController userController;
    private Localizzatore localizzatore;
    private Button inviaDestinazioneButton;                 // invisibile all'inizio
    private Nodo nodoDestinazione;
    private Nodo posUtente;


    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene caricata e visualizzata la mappa con i suoi nodi e settati i listener associati ad essi
     *
     * @param savedInstanceState
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normale);

        userController = UserController.getInstance(this);
        userController.clearNodiSelezionati();
        localizzatore = Localizzatore.getInstance(this);
        CommunicationServer.getInstance(this);

        inviaDestinazioneButton = findViewById(R.id.inviaDestinazioneButton);
        inviaDestinazioneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerInviaDestinazione();
            }
        });

        // inizializza la View della mappa
        mappaView = findViewById(R.id.mappaViewNormale);
        userController.setMappaView(mappaView);

        try {

            mappaView.setMappa(userController.getMappa());
            posUtente = userController.getMappa().getPosUtente(userController.getMacAdrs());
            mappaView.setPosUtente(posUtente);
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //per finish()
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {

        super.onStop();
    }

    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    public void listenerNodoSelezionato(NodoView nodoView) {

        Nodo nodo = nodoView.getNodo();

        if(nodo.getBeaconId() == posUtente.getBeaconId())
            mappaView.messaggio("Attenzione!", "Si trova già nel posto segnalato", false);
        else if(nodoDestinazione == null) {
            nodoDestinazione = nodo;
            nodoView.setImage(R.drawable.destinazione);
            inviaDestinazioneButton.setVisibility(View.VISIBLE);
        }else if(nodo.getBeaconId() == nodoDestinazione.getBeaconId()) {  //deselezione
            nodoView.setImage(nodo.getImage());
            nodoDestinazione = null;
            inviaDestinazioneButton.setVisibility(View.INVISIBLE);
        } else if (nodoDestinazione != null)
            mappaView.messaggio("Attenzione!","E' gia segnata una destinazione.\nDeselezionarla per cambiare", true);
    }

    public void listenerInviaDestinazione() {

        //userController.inviaDestinazione(this);
    }
}
