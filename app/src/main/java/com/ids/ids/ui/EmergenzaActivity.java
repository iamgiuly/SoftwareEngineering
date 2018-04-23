package com.ids.ids.ui;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.control.Localizzatore;
import com.ids.ids.control.UserController;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.DebugSettings;
import com.ids.ids.utils.Parametri;


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
    private Localizzatore localizzatore;
    private CommunicationServer communicationServer;

    private Button inviaNodiButton;                 // invisibile all'inizio
    private Button cambiapianoButton;
    private MappaView mappaView;

    private Thread update;
    private boolean updating = true;

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
        System.out.println("ONDESTROY");

        userController = UserController.getInstance(this);
        userController.clearNodiSelezionati();
        localizzatore = Localizzatore.getInstance(this);
        communicationServer = CommunicationServer.getInstance(this);

        inviaNodiButton = findViewById(R.id.inviaNodiButton);
        inviaNodiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerBottoneInvioNodi();
            }
        });

        // inizializza la View della mappa
        mappaView = findViewById(R.id.mappaView);

        try {

            // CASO SEGNALAZIONE
            if (userController.getModalita() == UserController.MODALITA_SEGNALAZIONE) {
                mappaView.setMappa(userController.getMappa());
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

                localizzatore.setMappaView(mappaView);      //TODO temporaneo (vedere in Localizzatore perché)
                mappaView.setMappa(userController.getMappa(), true);
                update();

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

    private void update(){
        updating = true;
        update = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void run(){
                //TODO possibilmente un unico thread nell'activity che sostituisce gli altri
                //TODO (richiamando gli appositi metodi delle classi che attualmente usano threads)

                localizzatore.getScanner().avviaScansione();
                int pianoUtente = userController.getPianoUtente();              // TODO temporaneo
                communicationServer.richiestaAggiornamenti(true, pianoUtente);  //Avvio richiesta aggiornamento

                Log.i("Localizzatore", "Inizio Ricerca pos ALWAYS");

                while(updating){
                    String pos = localizzatore.calcolaPosizione();
                    if (!pos.equals("NN")) {
                        System.out.println("MAC: " + pos);     // E' stato trovato il beacon dallo scanner
                        userController.richiediPercorso(pos, mappaView);
                    }

                    try {
                        Thread.sleep(Parametri.T_POSIZIONE_EMERGENZA);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                localizzatore.getScanner().fermaScansione();
                communicationServer.richiestaAggiornamenti(false, pianoUtente);
            }
        });
        update.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        updating = false;
        // userController.DropDB();
    }

    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    public void listenerNodoSelezionato(NodoView nodoView) {

        Nodo nodo = nodoView.getNodo();

        if (userController.selezionaNodo(nodo))
            this.inviaNodiButton.setVisibility(View.VISIBLE);
        else
            this.inviaNodiButton.setVisibility(View.INVISIBLE);

        nodoView.setImage(nodo.getImage());
    }

    /**
     * Richiamato dal listener associato al bottone "Invia Nodi", viene controllata la connessione:
     * - se attiva viene chiesto al Controller di inviare al server i nodi selezionati
     * e viene avviata l'activity MainActivity
     * - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     *
     *
     * I nodi selezionati vengono settati nel db locale come sotto incendio,
     * viene fatto lo stesso nel db remoto inviando una richiesta RESTful al server,
     * quindi la lista dei nodi selezionati viene svuotata
     */
    public void listenerBottoneInvioNodi() {
        ArrayList<Nodo> nodi = userController.getNodiSelezionati();
        communicationServer.inviaNodiSottoIncendio(nodi, this);
    }

    private void listenerBottoneCambiaPiano() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(DialogInterface dialog, int response) {
            switch (response) {
                case DialogInterface.BUTTON_POSITIVE: {
                    dialog.cancel();
                    updating = false;
                    finish();
                    userController.MandaMainActivity();
                    System.out.println("CLICK");
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
