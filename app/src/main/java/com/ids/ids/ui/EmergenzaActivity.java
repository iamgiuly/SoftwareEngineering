package com.ids.ids.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ids.ids.control.UserController;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.DebugSettings;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Questa activity viene mostrata al tap sul bottone "Segnala Emergenza",
 * al suo avvio carica la mappa in cui si trova l'utente e la visualizza con i suoi nodi opportunamente contrassegnati,
 * ad essi vengono associati dei listener, che al tap richiamano il metodo listenerNodoSelezionato() il quale
 * chiede al Controller di aggiungere / rimuovere il nodo premuto alla / dalla lista dei nodi selezionati.
 * Al tap sul bottone "Invia Nodi" (visibile se almeno un nodo è selezionato),
 * viene chiesto al Controller di inviare al server i nodi selezionati
 */
public class EmergenzaActivity extends AppCompatActivity implements Runnable {

    private UserController userController;

    private Button inviaNodiButton;                 // invisibile all'inizio
    private TextView messaggioErroreTextView;       // invisibile all'inizio
    private MappaView mappaView;

    private Mappa mappa;

    private boolean threadRunning;

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene caricata e visualizzata la mappa con i suoi nodi e settati i listener associati ad essi
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergenza);

        this.userController = UserController.getInstance(this);
        this.userController.clearNodiSelezionati();

        inviaNodiButton = findViewById(R.id.inviaNodiButton);
        inviaNodiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerBottoneInvioNodi();
            }
        });

        messaggioErroreTextView = findViewById(R.id.messaggioErroreTextView);

        //TODO connessione attiva: viene ottenuta la posizione tramite bluetooth, inviata al server che invia la mappa
        //TODO connessione non attiva: viene ottenuta la posizione tramite bluetooth e caricata la mappa dal db locale
        mappa = userController.caricaMappa(this, null);
        // mappa vale null se manca la connessione e non esiste una copia nel db locale
        if(mappa == null)
            visualizzaMessaggioErrore("La mappa non può essere caricata, verificare la connessione");
            // TODO avviare coroutine che ricontrolla periodicamente lo stato della connessione
            // TODO oppure visualizzare bottone "Riconnetti"

        // inizializza la View della mappa
        this.mappaView = findViewById(R.id.mappaView);
        try {
            if (this.userController.getModalita() == this.userController.MODALITA_SEGNALAZIONE) {
                this.mappaView.setMappa(mappa);
                this.mappaView.setOnTouchListener(new View.OnTouchListener() {
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
                this.mappaView.setMappa(userController.getMappa(), true);
                this.threadRunning = true;
                (new Thread(this)).start();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.threadRunning = false;
    }

    public void richiediRicalcolo(){
        //this.mappa = userController.richiediMappa();        // TODO serve per prendere i nodi sotto incendio aggiornati
                                                            // TODO e se la mappa cambia? (non dovrebbe succedere)
        // TODO this.mappa = userController.richiediNodi();

        // TODO PROVA: aggiornare (dummy) nodi sotto incendio
        Nodo posUtente = userController.getPosizioneUtente();
        this.mappaView.setPosUtente(posUtente);
        ArrayList<Arco> percorso = userController.calcolaPercorso(userController.getMappa(), posUtente);
        this.mappaView.setPercorso(percorso);
        try {
            this.mappaView.postInvalidate();
        } catch (Exception e) { }
    }

    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    public void listenerNodoSelezionato(NodoView nodoView){
        Nodo nodo = nodoView.getNodo();
        if(this.userController.selezionaNodo(nodo))
            this.inviaNodiButton.setVisibility(View.VISIBLE);
        else
            this.inviaNodiButton.setVisibility(View.INVISIBLE);
        nodoView.setImage(nodo.getImage());
    }

    /**
     * Richiamato dal listener associato al bottone "Invia Nodi", viene controllata la connessione:
     *  - se attiva viene chiesto al Controller di inviare al server i nodi selezionati
     *      e viene avviata l'activity MainActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneInvioNodi(){

       // if(this.userController.controllaConnessione()){

            userController.inviaNodiSelezionati(this);

            //this.finish();
       // }
       // else
         //   this.messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

    public boolean isThreadRunning() {
        return threadRunning;
    }

    @Override
    public void run() {
        while(this.isThreadRunning()) {
            this.richiediRicalcolo();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void visualizzaMessaggioErrore(String messaggio){
        messaggioErroreTextView.setText(messaggio);
        messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

    private void nascondiMessaggioErrore(){
        messaggioErroreTextView.setVisibility(View.INVISIBLE);
    }
}
