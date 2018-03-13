package com.ids.ids.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageView mappaImageView;
    private Map<Integer, ImageButton> nodi;         // da creare dinamicamente

    private int lunghezzaMappa, altezzaMappa;
    private boolean rendered = false;

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene caricata e visualizzata la mappa con i suoi nodi e settati i listener associati ad essi
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergenza);

        this.userController = UserController.getInstance(this.getApplicationContext());

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
        ViewTreeObserver viewTree = mappaImageView.getViewTreeObserver();
        viewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if(!rendered){
                    lunghezzaMappa = mappaImageView.getMeasuredWidth();
                    altezzaMappa = mappaImageView.getMeasuredHeight();
                    visualizzaMappa();
                    rendered = true;
                }
                return true;
            }
        });
    }

    /**
     * L'immagine associata alla mappa caricata viene visualizzata,
     * i nodi vengono visualizzati nelle coordinate opportune,
     * vengono associati i listener ai nodi che richiamano il metodo listenerNodoSelezionato()
     * TODO i nodi già sotto incendio (presi dal db) devono già risultare selezionati
     */
    private void visualizzaMappa(){
        this.mappaImageView.setImageResource(mappa.getPiantina());
        ConstraintLayout layout = findViewById(R.id.layout);
        this.nodi = new HashMap<>();
        for (Nodo nodo : this.mappa.getNodi()) {
            ImageButton bottoneNodo = this.visualizzaBottoneNodo(nodo);
            layout.addView(bottoneNodo);
            this.nodi.put(nodo.getId(), bottoneNodo);
            if(this.userController.getModalita() == this.userController.MODALITA_SEGNALAZIONE) {
                bottoneNodo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenerNodoSelezionato(v.getId());
                    }
                });
            }
        }

        if(this.userController.getModalita() == this.userController.MODALITA_EMERGENZA)
            this.visualizzaPercorso();
    }

    private ImageButton visualizzaBottoneNodo(Nodo nodo){
        ImageButton bottoneNodo = new ImageButton(this);
        bottoneNodo.setImageResource(Nodo.IMG_BASE);
        bottoneNodo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bottoneNodo.setBackgroundColor(Color.TRANSPARENT);
        bottoneNodo.setId(nodo.getId());
        bottoneNodo.setX(this.xNodoAssoluta(nodo.getX()));
        bottoneNodo.setY(this.yNodoAssoluta(nodo.getY()));
        bottoneNodo.setLayoutParams(new ConstraintLayout.LayoutParams(Nodo.dim, Nodo.dim));
        return bottoneNodo;
    }

    private void visualizzaPercorso(){

    }

    /**
     * Calcola la coordinata X assoluta di un nodo
     * @param xNodoRelativa valore da 0 a 100 con riferimento alla posizione relativa alla lunghezza della mappa
     * @return coordinata X espressa in pixel sullo schermo
     */
    private int xNodoAssoluta(int xNodoRelativa){
        float xMappa = this.mappaImageView.getX();
        return (int) (xMappa + (this.lunghezzaMappa * xNodoRelativa) / 100);
    }

    /**
     * Calcola la coordinata Y assoluta di un nodo
     * @param yNodoRelativa valore da 0 a 100 con riferimento alla posizione relativa all'altezza della mappa
     * @return coordinata Y espressa in pixel sullo schermo
     */
    private int yNodoAssoluta(int yNodoRelativa){
        float yMappa = this.mappaImageView.getY();
        return (int) (yMappa + (this.altezzaMappa * yNodoRelativa) / 100);
    }

    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    public void listenerNodoSelezionato(int idNodo){
        int icon = Nodo.IMG_INCENDIO;
        if(userController.nodoSelezionato(idNodo))          // deseleziona
            icon = Nodo.IMG_BASE;
        this.nodi.get(idNodo).setImageResource(icon);
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
