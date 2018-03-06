package com.ids.ids;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserActivity extends AppCompatActivity {

    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.userController = (UserController) getApplicationContext();
        boolean stato = userController.controllaConnessione();
        if(stato){
            this.visualizzazioneBottoneInvio();
        }
        else{
            this.visualizzazioneMessaggioRiconnetti();
        }
    }



    public void visualizzazioneBottoneInvio(){

    }

    public void visualizzazioneMessaggioRiconnetti(){


    }

    public void visualizzazioneMappa(){

    }

    // TODO modificare nome in modo da estendere metodo listener
    public void tapNodiSottoIncendio(){
        this.userController.gestisciTapNodiSottoIncendio();
    }

    // TODO modificare nome in modo da estendere metodo listener
    public void tapBottoneEmergenza(){
        this.userController.gestisciTapBottoneEmergenza();
    }

    // TODO modificare nome in modo da estendere metodo listener
    public void tapBottoneInvioNodi(){
        this.userController.gestisciTapBottoneInvioNodi();
    }

}


