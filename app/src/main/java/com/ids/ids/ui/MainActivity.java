package com.ids.ids.ui;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.control.Localizzatore;
import com.ids.ids.control.User;


/**
 * Questa activity viene mostrata all'apertura dell'applicazione.
 * Visualizza i bottoni:
 *     -
 * a tale bottone viene associato un listener, che al tap su di esso richiama il metodo listenerBottoneSegnalazione() il quale:
 * - rimanda l'utente online alla EmergenzaActivity
 * - mostra un messaggio di errore all'utente offline
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1; // il popup da mostrare è quello per l'attivazione del bluetooth
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private User user;
    private Localizzatore localizzatore;

    private BluetoothManager btManager;             // utilizzata per ottenere una istanza di Adapter
    private BluetoothAdapter btAdapter;             // adattatore Bluetooth del dispositivo locale,
    // consente di eseguire attività Bluetooth fondamental
    // (es. avviare il rilevamento dei dispositivi)
    private BroadcastReceiver receiver;             // permette di ricevere notifice sullo stato del dispositivo

    private Button normaleButton;
    private Button segnalazioneButton;
    private Button emergenzaButton;

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     * viene inizializzato il Controller dell'utente
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();

        normaleButton = findViewById(R.id.normaleButton);
        normaleButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                listenerBottoneNormale();
            }
        });

        segnalazioneButton = findViewById(R.id.segnalazioneButton);
        segnalazioneButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                listenerBottoneSegnalazione();
            }
        });

        emergenzaButton = findViewById(R.id.emergenzaButton);
        emergenzaButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                listenerBottoneEmergenza();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancella il ricevitore dalle notifiche di stato
        unregisterReceiver(receiver);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBluetooth() {

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        receiver = new BroadcastReceiver() {
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
                            // segnalare all'utente che l'app non funziona senza BLE
                            break;
                    }
                }
            }
        };

        // richiesta dei permessi di localizzazione approssimata
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        // registra il ricevitore per le notifiche di stato
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);

        Bundle datipassati = getIntent().getExtras();
        if (datipassati != null)
            listenerBottoneEmergenza();
    }

    private void initSingleton(){
        user = User.getInstance(this);
        localizzatore = Localizzatore.getInstance(this);
        CommunicationServer.getInstance(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void listenerBottoneNormale() {
        initSingleton();
        user.setModalita(user.MODALITA_NORMALE);
        if (abilitaBLE())
            localizzatore.startFinderONE();
    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     * - se attiva viene avviata l'activity EmergenzaActivity
     * - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void listenerBottoneSegnalazione() {
        initSingleton();
        user.setModalita(user.MODALITA_SEGNALAZIONE);
        if (abilitaBLE())
            localizzatore.startFinderONE();
    }

    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     * - se attiva viene avviata l'activity EmergenzaActivity
     * - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void listenerBottoneEmergenza() {
        initSingleton();
        user.setModalita(user.MODALITA_EMERGENZA);
        if (abilitaBLE())
            localizzatore.startFinderONE();
    }

    /**
     * Prova ad abilitare l'adapter del bluetooth
     *
     * @return true se l'adapter è stato abilitato
     */
    private boolean abilitaBLE() {

        boolean statoBLE = btAdapter.isEnabled();
        if (btAdapter != null && !statoBLE) {
            Intent enableIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        return statoBLE;
    }
}