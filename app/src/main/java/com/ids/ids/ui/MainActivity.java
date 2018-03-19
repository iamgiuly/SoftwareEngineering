package com.ids.ids.ui;

import android.app.Activity;
import android.app.Application;
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
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.ArcoDAO;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.MappaDAO;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.NodoDAO;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;

import static com.ids.ids.control.BluetoothController.PERMISSION_REQUEST_COARSE_LOCATION;

/**
 * Questa activity viene mostrata all'apertura dell'applicazione, visualizza il bottone "Segnala Emergenza",
 * a tale bottone viene associato un listener, che al tap su di esso richiama il metodo listenerBottoneSegnalazione() il quale:
 *  - rimanda l'utente online alla EmergenzaActivity
 *  - mostra un messaggio di errore all'utente offline
 */
public class MainActivity extends AppCompatActivity {

    public static boolean DB_SEEDED = false;            //TODO solo per debug

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

        //TODO solo per debug
        if(DebugSettings.SEED_DB && !MainActivity.DB_SEEDED) {
            this.seedDb();
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

    //TODO solo per debug
    public void seedDb(){
        ArrayList<Nodo> nodi = new ArrayList<>();
        ArrayList<Arco> archi = new ArrayList<>();
        int idMappa = 145;
        Nodo nodo1 = new Nodo(1, "1", 20, 20, Nodo.TIPO_BASE, idMappa);
        Nodo nodo2 = new Nodo(2, "2", 20, 40, Nodo.TIPO_BASE, idMappa);
        Nodo nodo3 = new Nodo(3, "3", 20, 60, Nodo.TIPO_BASE, idMappa);
        Nodo nodo4 = new Nodo(4, "4", 20, 80, Nodo.TIPO_BASE, idMappa);
        Nodo nodo5 = new Nodo(5, "5", 60, 20, Nodo.TIPO_BASE, idMappa);
        Nodo nodo6 = new Nodo(6, "6", 60, 40, Nodo.TIPO_BASE, idMappa);
        Nodo nodo7 = new Nodo(7, "7", 60, 60, Nodo.TIPO_BASE, idMappa);
        Nodo nodo8 = new Nodo(8, "8", 60, 80, Nodo.TIPO_BASE, idMappa);
        nodi.add(nodo1);
        nodi.add(nodo2);
        nodi.add(nodo3);
        nodi.add(nodo4);
        nodi.add(nodo5);
        nodi.add(nodo6);
        nodi.add(nodo7);
        nodi.add(nodo8);
        archi.add(new Arco(nodo1, nodo2, null));
        archi.add(new Arco(nodo3, nodo4, null));
        archi.add(new Arco(nodo5, nodo6, null));
        archi.add(new Arco(nodo7, nodo8, null));
        archi.add(new Arco(nodo1, nodo4, null));
        archi.add(new Arco(nodo2, nodo8, null));

        MappaDAO.getInstance(this).clear();
        NodoDAO.getInstance(this).clear();
        ArcoDAO.getInstance(this).clear();
        MappaDAO.getInstance(this).insert(new Mappa(idMappa, R.drawable.map145, nodi, archi));
    }

}