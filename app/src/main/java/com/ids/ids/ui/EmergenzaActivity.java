package com.ids.ids.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.ids.control.UserController;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.DebugSettings;

import java.util.HashMap;
import java.util.Map;

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

    private Mappa mappa;

    private Button inviaNodiButton;                 // invisibile all'inizio
    private TextView messaggioErroreTextView;       // invisibile all'inizio
    private MappaView mappaView;

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

        this.mappa = userController.richiediMappa();
        this.mappaView = findViewById(R.id.mappaView);
        if(this.userController.getModalita() == this.userController.MODALITA_SEGNALAZIONE) {
            this.mappaView.setMappa(this.mappa);
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
        }
        else{
            this.mappaView.setMappa(this.mappa, true);
            this.threadRunning = true;
            (new Thread(this)).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.threadRunning = false;
    }

    // TODO thread (ogni 2 secondi richiama un metodo del controller per il ricalcolo di posizione, percorso e nodi)
    public void richiediRicalcolo(){
        this.mappa = userController.richiediMappa();        // TODO serve per prendere i nodi sotto incendio aggiornati
                                                            // TODO e se la mappa cambia? (non dovrebbe succedere)
        this.mappaView.setPosUtente(userController.getPosizioneUtente());

        //this.mappaView.setMappa(this.mappa, true);
        // TODO ************ LE SEGUENTI OPERAZIONI VANNO FATTE SU this.mappa,
        // TODO ************ LA VIEW SI AGGIORNA IN AUTOMATICO CON invalidate()
        // TODO calcolo percorso

        try {
            this.mappaView.invalidate();
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
        if(this.userController.controllaConnessione() && userController.inviaNodiSelezionati()){
            this.finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);
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
}
