package com.ids.ids.invioSegnalazioneEmergenza.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.ids.R;
import com.ids.ids.invioSegnalazioneEmergenza.control.UserController;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

import java.util.ArrayList;

/**
 * Questa activity viene mostrata al tap sul bottone "Segnala Emergenza",
 * al suo avvio carica la mappa in cui si trova l'utente e la visualizza con i suoi nodi opportunamente contrassegnati,
 * ad essi vengono associati dei listener, che al tap richiamano il metodo listenerNodoSelezionato() il quale
 * chiede al Controller di aggiungere / rimuovere il nodo premuto alla / dalla lista dei nodi selezionati.
 * Al tap sul bottone "Invia Nodi" (visibile se almeno un nodo è selezionato),
 * viene chiesto al Controller di inviare al server i nodi selezionati
 */
public class SegnalazioneEmergenzaActivity extends AppCompatActivity {

    private UserController userController = UserController.getInstance();

    private Mappa mappa;

    private Button inviaNodiButton;                 // invisibile all'inizio
    private TextView messaggioErroreTextView;       // invisibile all'inizio
    private ImageView mappaImageView;
    private ArrayList<Button> nodi;                 // da creare dinamicamente  TODO dictionary id -> button

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene caricata e visualizzata la mappa con i suoi nodi e settati i listener associati ad essi
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnalazione_emergenza);

        inviaNodiButton = findViewById(R.id.inviaNodiButton);
        inviaNodiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerBottoneInvioNodi();
            }
        });

        messaggioErroreTextView = findViewById(R.id.messaggioErroreTextView);

        this.mappa = userController.richiediMappa();
        mappaImageView = findViewById(R.id.mappaImageView);
        this.visualizzaMappa();
    }

    /**
     * L'immagine associata alla mappa caricata viene visualizzata,
     * i nodi vengono visualizzati nelle coordinate opportune,
     * vengono associati i listener ai nodi che richiamano il metodo listenerNodoSelezionato()
     */
    private void visualizzaMappa(){
        // TODO settare immagine mappa con la mappa caricata
        this.mappaImageView.setImageResource(mappa.getPiantina());
        // TODO visualizzare nodi nelle coordinate opportune
        ConstraintLayout layout = findViewById(R.id.layout);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        for (Nodo nodo : this.mappa.getNodi()) {
            Button bottoneNodo = new Button(this);
            bottoneNodo.setId(nodo.getIntId());  // TODO ID NODO
            //bottoneNodo.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT));
            bottoneNodo.setX(this.xNodoAssoluta(nodo.getX()));
            bottoneNodo.setY(this.yNodoAssoluta(nodo.getY()));
            bottoneNodo.setBackgroundColor(Color.BLUE);
            layout.addView(bottoneNodo);

            set.connect(bottoneNodo.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            set.connect(bottoneNodo.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,0);
            set.connect(bottoneNodo.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);
            set.applyTo(layout);

            // TODO aggiungere a lista nodi

        }

        // TODO associare listener ai nodi
    }

    private int xNodoAssoluta(int xNodoRelativa){
        float xMappa = mappaImageView.getX();
        float lunghezzaMappa = 100;//mappaImageView.getWidth();
        return (int) (xMappa + (lunghezzaMappa * xNodoRelativa) / 100);
    }

    private int yNodoAssoluta(int yNodoRelativa){
        float yMappa = mappaImageView.getY();
        float altezzaMappa = 100;//mappaImageView.getHeight();
        return (int) (yMappa + (altezzaMappa * yNodoRelativa) / 100);
    }

    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    public void listenerNodoSelezionato(String idNodo){
        // il bottone associato al nodo selezionato viene contrassegnato graficamente come tale
        for (Button nodo : this.nodi) {
            if(nodo.getId() == Integer.parseInt(idNodo)){
                nodo.setBackgroundColor(Color.RED);
                break;
            }
        }

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
