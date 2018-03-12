package com.ids.ids.ui;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.control.UserController;


public class MainActivityA extends AppCompatActivity {


    private Button SegnalaEmergenzaButton;

    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BeaconScanner COMbeacon;



    private UserController userController = UserController.getInstance();

    private Button segnalazioneButton;
    private Button emergenzaButton;
    private TextView messaggioErroreTextView;       // invisibile all'inizio




    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        userController.init(getApplicationContext());


        COMbeacon = new BeaconScanner(this);

        segnalazioneButton = findViewById(R.id.segnalazioneButton);
        segnalazioneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                COMbeacon.Scansione(true);
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








        //BluethoothManager è utilizzata per ottenere una istanza di Adapter
        //Bluethooth adapter rappresenta l'adattatore Bluetooth del dispositivo locale.
        //BluetoothAdapter consente di eseguire attività Bluetooth fondamentali, come avviare il rilevamento dei dispositivi,
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE); //Ottengo BluethoothManager e lo salvo in locale
        btAdapter = btManager.getAdapter();                                        //richiamo l adapter e lo salvo in locale



        if(AbilitaBLE())
            AbilitaLocazione();



    }


    /**
     * Richiamato dal listener associato al bottone "Segnala Emergenza", viene controllata la connessione:
     *  - se attiva viene avviata l'activity EmergenzaActivity
     *  - altrimenti viene mostrato un messaggio di errore rimanendo in questa activity
     */
    public void listenerBottoneSegnalazione(){
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

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void AbilitaLocazione(){


        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Localizzazione");
            alert.setMessage("Accettare per attivare i servizi di localizzazione");
            alert.setPositiveButton(android.R.string.ok, null);
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);

                }
            });

            alert.show();

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        switch (requestCode) {

            case PERMISSION_REQUEST_COARSE_LOCATION: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED & AbilitaBLE() )
                    AbilitaLocazione();
                else
                     Log.i("Localizzazione", "Permessi di localizzazione negati");

            }break;

        }

    }




}









