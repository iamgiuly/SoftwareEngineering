package com.ids.ids.ui;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ids.ids.control.BluetoothController;
import com.ids.ids.control.UserController;
import com.ids.ids.utils.DebugSettings;

import static com.ids.ids.control.BluetoothController.PERMISSION_REQUEST_COARSE_LOCATION;

/**
 * Questa activity viene mostrata all'apertura dell'applicazione, visualizza il bottone "Segnala Emergenza",
 * a tale bottone viene associato un listener, che al tap su di esso richiama il metodo listenerBottoneSegnalazione() il quale:
 *  - rimanda l'utente online alla EmergenzaActivity
 *  - mostra un messaggio di errore all'utente offline
 */
public class MainActivity extends AppCompatActivity {

    public static boolean DB_SEEDED = false;        // solo per debug

    private UserController userController;
    private BluetoothController bluetoothController;

    private Button segnalazioneButton;
    private Button emergenzaButton;
    private TextView messaggioErroreTextView;       // invisibile all'inizio

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene inizializzato il Controller dell'utente
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userController = UserController.getInstance(this);
        if(DebugSettings.SCAN_BLUETOOTH)
            bluetoothController = BluetoothController.getInstance(this);

        segnalazioneButton = findViewById(R.id.segnalazioneButton);
        segnalazioneButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v){
                listenerBottoneSegnalazione();
            }
        });

        emergenzaButton = findViewById(R.id.emergenzaButton);
        emergenzaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerBottoneEmergenza();
            }
        });

        messaggioErroreTextView = findViewById(R.id.messaggioErroreTextView);

        if(DebugSettings.SEED_DB && !MainActivity.DB_SEEDED) {
            DebugSettings.seedDb(this);
            MainActivity.DB_SEEDED = true;
        }
    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     *  - se attiva viene avviata l'activity EmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void listenerBottoneSegnalazione(){
        if(DebugSettings.SCAN_BLUETOOTH)
            this.bluetoothController.avviaScansione();
        if(this.userController.controllaConnessione()){
            this.userController.setModalita(this.userController.MODALITA_SEGNALAZIONE);
            Intent intent = new Intent(this, EmergenzaActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     *  - se attiva viene avviata l'activity EmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneEmergenza(){
        if(this.userController.controllaConnessione()){
            this.userController.setModalita(this.userController.MODALITA_EMERGENZA);
            Intent intent = new Intent(this, EmergenzaActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_COARSE_LOCATION)
            bluetoothController.fornisciPermessi(grantResults[0]);
    }

}