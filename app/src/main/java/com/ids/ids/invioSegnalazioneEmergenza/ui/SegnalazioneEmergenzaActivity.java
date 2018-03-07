package com.ids.ids.invioSegnalazioneEmergenza.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ids.ids.R;
import com.ids.ids.invioSegnalazioneEmergenza.control.UserController;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

public class SegnalazioneEmergenzaActivity extends AppCompatActivity {

    private UserController userController = UserController.getInstance();
    private Mappa mappa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnalazione_emergenza);
        this.mappa = userController.richiediMappa();
        this.visualizzaMappa();
    }

    private void visualizzaMappa(){

    }

    /**
     * richiamato quando l'utente fa tap su un nodo,
     * tale nodo deve essere opportunamente contrassegnato,
     * oltre ad essere aggiunto ai / rimosso dalla lista dei nodi selezionati
     * TODO modificare nome in modo da estendere metodo listener
     */
    public void tapNodoSottoIncendio(Nodo nodo){
        this.userController.gestisciTapNodiSottoIncendio(nodo);
    }

    // TODO modificare nome in modo da estendere metodo listener
    public void tapBottoneInvioNodi(){
        this.userController.gestisciTapBottoneInvioNodi();
    }

}
