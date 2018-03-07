package com.ids.ids.invioSegnalazioneEmergenza.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ids.ids.R;
import com.ids.ids.invioSegnalazioneEmergenza.control.UserController;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

/**
 * visualizzata all'apertura dell'applicazione,
 * mostra il bottone "Segnala Emergenza" o il bottone "Riconnetti" con un messaggio di errore
 * TODO togliamo il bottone "Riconnetti"? Basta un messaggio "Riprovare" e lasciamo "Segnala Emergenza"
 */
public class MainActivity extends AppCompatActivity {

    private UserController userController = UserController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * richiamato quando l'utente fa tap sul bottone "Segnala Emergenza",
     * viene controllata la connessione:
     *  - se attiva viene avviata l'activity SegnalazioneEmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore
     *      e l'utente può premere nuovamente il bottone quando si riconnette
     * TODO modificare nome in modo da estendere metodo listener
     */
    public void tapBottoneEmergenza(){
        if(userController.controllaConnessione()){
            Intent intent = new Intent(this, SegnalazioneEmergenzaActivity.class);
            startActivity(intent);
        }
        else
            this.visualizzaMessaggioRiconnetti();
    }

    private void visualizzaMessaggioRiconnetti(){
        // TODO
    }

}