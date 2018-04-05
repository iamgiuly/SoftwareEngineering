package com.ids.ids.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.control.Localizzatore;
import com.ids.ids.control.UserController;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.DebugSettings;


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
    private BeaconScanner scanner;

    private Button inviaNodiButton;                 // invisibile all'inizio
    private MappaView mappaView;

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
        userController.clearNodiSelezionati();

        inviaNodiButton = findViewById(R.id.inviaNodiButton);
        inviaNodiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerBottoneInvioNodi();
            }
        });

        scanner = new BeaconScanner(this);

        // inizializza la View della mappa
        mappaView = findViewById(R.id.mappaView);

        try {
            if (userController.getModalita() == userController.MODALITA_SEGNALAZIONE) {

                localizzatore = new Localizzatore(this, scanner);
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
            } else {  //CASO EMERGENZA

                localizzatore = new Localizzatore(mappaView, this, scanner);
                mappaView.setMappa(userController.getMappa(), true);
                scanner.scansione(true);
                localizzatore.startFinderALWAYS();
                //Avvia aggiornamento db locale
                userController.richiestaAggiornamento(true);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanner.scansione(false);
        localizzatore.stopFinderALWAYS();
        userController.richiestaAggiornamento(false);
       // userController.DropDB();
    }

   /* public void Ricalcolo(ArrayList<Arco> percorso, String macAdrs){

        Nodo posUtente = userController.getMappa().getPosUtente(macAdrs);
        this.mappaView.setPosUtente(posUtente);
        this.mappaView.setPercorso(percorso);
        try {
            this.mappaView.postInvalidate();
        } catch (Exception e) { }
    }*/

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
     */
    public void listenerBottoneInvioNodi() {

        userController.inviaNodiSelezionati(this);
    }
}
