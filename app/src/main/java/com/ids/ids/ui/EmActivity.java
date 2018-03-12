package com.ids.ids.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ids.ids.control.UserController;
import com.ids.ids.entity.Mappa;

import java.util.Map;

/**
 * Questa activity viene mostrata all'apertura dell'applicazione, visualizza il bottone "Segnala Emergenza",
 * a tale bottone viene associato un listener, che al tap su di esso richiama il metodo listenerBottoneSegnalazione() il quale:
 *  - rimanda l'utente online alla EmergenzaActivity
 *  - mostra un messaggio di errore all'utente offline
 */
public class EmActivity extends AppCompatActivity {

    private UserController userController = UserController.getInstance();

    private Mappa mappa;

    private Button segnalaEmergenzaButton;
    private TextView messaggioErroreTextView;       // invisibile all'inizio
    private ImageView mappaImageView;
    private Map<Integer, ImageButton> nodi;         // da creare dinamicamente

    private int lunghezzaMappa, altezzaMappa;
    private boolean rendered = false;

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene inizializzato il Controller dell'utente
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userController.init(getApplicationContext());

        segnalaEmergenzaButton = findViewById(R.id.segnalazioneButton);
        segnalaEmergenzaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerBottoneEmergenza();
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
                    //visualizzaMappa();
                    rendered = true;
                }
                return true;
            }
        });
    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     *  - se attiva viene avviata l'activity EmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneEmergenza(){
        if(this.userController.controllaConnessione()){
            Intent intent = new Intent(this, EmergenzaActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

}