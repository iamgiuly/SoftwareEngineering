package com.ids.ids.ui;

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
import java.util.ArrayList;

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
public class EmergenzaActivity extends AppCompatActivity {

    private UserController userController;
    private CommunicationServer communicationServer;
    private Localizzatore localizzatore;
    private MappaView mappaView;
    private ArrayList<Nodo> nodiSelezionati; // nodi di cui bisogna cambiare il flag "sotto incendio"

    private Button inviaNodiButton;                 // invisibile all'inizio
    private Button cambiapianoButton;

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
        setContentView(R.layout.activity_emergenza);

        userController = UserController.getInstance(this);
        localizzatore = Localizzatore.getInstance(this);
        communicationServer = CommunicationServer.getInstance(this);

        nodiSelezionati = new ArrayList<>();

        inviaNodiButton = findViewById(R.id.inviaNodiButton);
        inviaNodiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerBottoneInvioNodi();
            }
        });

        // inizializza la View della mappa
        mappaView = findViewById(R.id.mappaView);
        userController.setMappaView(mappaView);

        try {

            // CASO SEGNALAZIONE
            if (userController.getModalita() == userController.MODALITA_SEGNALAZIONE) {

                mappaView.setMappa(userController.getMappa());
               // mappaView.setPosUtente(userController.getMappa().getNodoSpecifico(userController.getMacAdrs()));
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

            } else {
                //CASO EMERGENZA

                mappaView.setMappa(userController.getMappa(), true);
                localizzatore.startFinderALWAYS();                               //Avvio localizzazione
                //Avvia aggiornamento db locale
                communicationServer.richiestaAggiornamenti(true,userController.getPianoUtente());
                userController.setLocalizzatore(localizzatore);

                cambiapianoButton = findViewById(R.id.CambiaPianoButton);
                cambiapianoButton.setVisibility(View.VISIBLE);
                cambiapianoButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        listenerBottoneCambiaPiano();
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
        if (localizzatore != null)
            localizzatore.stopFinderALWAYS();
        communicationServer.richiestaAggiornamenti(false, userController.getPianoUtente());
        userController.DropDB();
        mappaView.deleteImagePiantina();
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

        if (selezionaNodo(nodo))
            inviaNodiButton.setVisibility(View.VISIBLE);
        else
            inviaNodiButton.setVisibility(View.INVISIBLE);

        nodoView.setImage(nodo.getImage());
    }


    /**
     * Aggiunge o rimuove dalla lista dei nodi selezionati il nodo con l'id passato come parametro
     *
     * @param nodo nodo da selezionare o deselezionare
     * @return true se c'è almeno un nodo selezionato
     */

    private boolean selezionaNodo(Nodo nodo) {
        nodo.setIncendio();

        if (nodo.isCambiato()) {
            if (!nodiSelezionati.contains(nodo))
                nodiSelezionati.add(nodo);
        } else if (nodiSelezionati.contains(nodo))
            nodiSelezionati.remove(nodo);

        return !nodiSelezionati.isEmpty();
    }

    /**
     * Richiamato dal listener associato al bottone "Invia Nodi", viene controllata la connessione:
     * - se attiva viene chiesto al Controller di inviare al server i nodi selezionati
     * e viene avviata l'activity MainActivity
     * - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneInvioNodi() {

        communicationServer.inviaNodiSottoIncendio(nodiSelezionati/*, this*/);
    }

    private void listenerBottoneCambiaPiano() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sei sicuro di voler cambiare piano?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(DialogInterface dialog, int response) {

            switch (response) {

                case DialogInterface.BUTTON_POSITIVE: {
                    dialog.cancel();
                    localizzatore.stopFinderALWAYS();
                    communicationServer.richiestaAggiornamenti(false,userController.getPianoUtente());
                    finish();
                    userController.MandaMainActivity();
                    break;
                }

                case DialogInterface.BUTTON_NEGATIVE: {
                    dialog.cancel();
                    break;
                }
            }
        }
    };
}
