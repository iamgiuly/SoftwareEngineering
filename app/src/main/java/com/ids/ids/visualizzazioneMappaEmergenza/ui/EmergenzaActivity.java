package com.ids.ids.visualizzazioneMappaEmergenza.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ids.ids.R;
import com.ids.ids.invioSegnalazioneEmergenza.control.UserController;
import com.ids.ids.invioSegnalazioneEmergenza.ui.SegnalazioneEmergenzaActivity;

/**
 * Questa activity viene mostrata all'apertura dell'applicazione, visualizza il bottone "Segnala Emergenza",
 * a tale bottone viene associato un listener, che al tap su di esso richiama il metodo listenerBottoneEmergenza() il quale:
 *  - rimanda l'utente online alla SegnalazioneEmergenzaActivity
 *  - mostra un messaggio di errore all'utente offline
 */
public class EmergenzaActivity extends AppCompatActivity {

    private UserController userController = UserController.getInstance();

    private Button segnalaEmergenzaButton;
    private TextView messaggioErroreTextView;       // invisibile all'inizio

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

        segnalaEmergenzaButton = findViewById(R.id.segnalaEmergenzaButton);
        segnalaEmergenzaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerBottoneEmergenza();
            }
        });

        messaggioErroreTextView = findViewById(R.id.messaggioErroreTextView);
    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     *  - se attiva viene avviata l'activity SegnalazioneEmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneEmergenza(){
        if(this.userController.controllaConnessione()){
            Intent intent = new Intent(this, SegnalazioneEmergenzaActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

}