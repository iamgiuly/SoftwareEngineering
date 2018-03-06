package com.ids.ids;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final UserController userController = (UserController) getApplicationContext();
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

    public void tapNodiSottoIncendio(){

    }

    public void tapBottoneInvioNodi(){

    }

}


