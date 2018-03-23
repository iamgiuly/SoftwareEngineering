package com.ids.ids.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.control.BluetoothController;
import com.ids.ids.control.Localizzatore;
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

    private UserController userController;
    private BluetoothController bluetoothController;

    private Button segnalazioneButton;
    private Button emergenzaButton;
    private TextView messaggioErroreTextView;       // invisibile all'inizio


    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconScanner Scanner;
    private Localizzatore Localiz;

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
        if(DebugSettings.SCAN_BLUETOOTH) {
          //  bluetoothController = BluetoothController.getInstance(this);

            //BluethoothManager è utilizzata per ottenere una istanza di Adapter
            //Bluethooth adapter rappresenta l'adattatore Bluetooth del dispositivo locale.
            //BluetoothAdapter consente di eseguire attività Bluetooth fondamentali, come avviare il rilevamento dei dispositivi,
            btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE); //Ottengo BluethoothManager e lo salvo in locale
            btAdapter = btManager.getAdapter();                                        //richiamo l adapter e lo salvo in locale


            Scanner = new BeaconScanner(this);
            Localiz = new Localizzatore(this,Scanner);

            segnalazioneButton = findViewById(R.id.segnalazioneButton);
            segnalazioneButton.setOnClickListener(new View.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v){

                    if(AbilitaBLE())
                        listenerBottoneSegnalazione();
                }
            });


            // Richiesta dei permessi di localizzazione approssimata
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }
            }


            // Registra il ricevitore per le notifiche di stato
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
        }



        emergenzaButton = findViewById(R.id.emergenzaButton);
        emergenzaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerBottoneEmergenza();
            }
        });

        messaggioErroreTextView = findViewById(R.id.messaggioErroreTextView);

        if(DebugSettings.SEED_DB)
            DebugSettings.seedDb(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancella il ricevitore dalle notifiche di stato
        unregisterReceiver(mReceiver);
        //mDriverServer.mToServer.startAmb(false);
    }

    // Permette di ricevere notifice sullo stato del dispositivo
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);

                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        listenerBottoneSegnalazione();

                        break;
                    case BluetoothAdapter.STATE_OFF:
                        finish();
                        // Segalare all'utente che l'app non funziona senza ble
                        break;
                }
            }
        }
    };

    private boolean AbilitaBLE(){

        boolean statoBLE = btAdapter.isEnabled();

        if (btAdapter != null && !statoBLE) {

            Intent enableIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);  //Messaggio
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT); //Metodo dell activity che permette di lanciare una dialogWindow,
            // REQUEST_ENABLE_BT è il codice sopra definito che permette alla dialog
            // di capire che la finestra da lancioare è quella per l attivazione del bluethooth

        }

        return statoBLE;

    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     *  - se attiva viene avviata l'activity EmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void listenerBottoneSegnalazione(){

        Scanner.Scansione(true);
        Localiz.startFinderONE();
        userController.setModalita(0);
        segnalazioneButton.setVisibility(Button.INVISIBLE);
        /*if(DebugSettings.SCAN_BLUETOOTH)
            this.bluetoothController.avviaScansione();
        if(this.userController.controllaConnessione()){
            this.userController.setModalita(this.userController.MODALITA_SEGNALAZIONE);
            Intent intent = new Intent(this, EmergenzaActivity.class);
            startActivity(intent);
        }
        else
            this.messaggioErroreTextView.setVisibility(View.VISIBLE);*/
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

}