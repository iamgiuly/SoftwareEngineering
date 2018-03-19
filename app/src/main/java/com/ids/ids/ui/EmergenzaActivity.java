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
public class EmergenzaActivity extends AppCompatActivity {

    private UserController userController;

    private Mappa mappa;

    private Button inviaNodiButton;                 // invisibile all'inizio
    private TextView messaggioErroreTextView;       // invisibile all'inizio
    private MappaView mappaView;

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
        this.mappaView.setMappa(this.mappa);
        if(this.userController.getModalita() == this.userController.MODALITA_SEGNALAZIONE) {
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
        else
            this.mappaView.setDisegnaPercorso(true);
    }

    //TODO creiamo i listener relativi a MappaView qui
    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    public void listenerNodoSelezionato(NodoView nodoView){
        int idNodo = nodoView.getId();
        int image = Nodo.IMG_INCENDIO;
        if(userController.nodoSelezionato(idNodo))          // deseleziona
            image = Nodo.IMG_BASE;
        nodoView.setImage(image);
        // TODO: cambiare il seguito (vengono anche deselezionati nodi selezionati prima dell'avvio)
        // il bottone "Invia Nodi" viene reso visibile o invisibile a seconda che ci siano nodi selezionati
        if(this.userController.selezionaNodo(idNodo))
            this.inviaNodiButton.setVisibility(View.VISIBLE);
        else
            this.inviaNodiButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Richiamato dal listener associato al bottone "Invia Nodi", viene controllata la connessione:
     *  - se attiva viene chiesto al Controller di inviare al server i nodi selezionati
     *      e viene avviata l'activity MainActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneInvioNodi(){
        if(this.userController.controllaConnessione() && userController.inviaNodiSelezionati()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

}
