package com.ids.ids.control;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ids.ids.boundary.BeaconScanner;

public class BluetoothController {

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static BluetoothController instance = null;

    private Activity context;

    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private BeaconScanner COMbeacon;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothController(Context context){
        this.context = (Activity) context;

        COMbeacon = new BeaconScanner(this.context);
        // Ottengo BluethoothManager e lo salvo in locale (classe utilizzata per ottenere una istanza di Adapter)
        btManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        // Richiamo l'Adapter e lo salvo in locale (rappresenta l'adattatore Bluetooth del dispositivo locale,
        // consente di eseguire attività Bluetooth fondamentali, come avviare il rilevamento dei dispositivi)
        btAdapter = btManager.getAdapter();



        if(abilitaBLE())
            abilitaLocazione();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void avviaScansione(){
        COMbeacon.Scansione(true);
    }


    private boolean abilitaBLE(){
        boolean statoBLE = btAdapter.isEnabled();
        if (btAdapter != null && !statoBLE) {
            // Viene mostrata all'utente una dialogWindow con la richiesta di attivazione,
            // La particolare richiesta è definita da REQUEST_ENABLE_BT
            Intent enableIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
            context.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        return statoBLE;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void abilitaLocazione(){


        if (this.context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
            alert.setTitle("Localizzazione");
            alert.setMessage("Accettare per attivare i servizi di localizzazione");
            alert.setPositiveButton(android.R.string.ok, null);
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {

                    context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);

                }
            });

            alert.show();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void fornisciPermessi(int permesso){
        if (permesso == PackageManager.PERMISSION_GRANTED & abilitaBLE() )
            this.abilitaLocazione();
        else
            Log.i("Localizzazione", "Permessi di localizzazione negati");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothController getInstance(Context context){
        if(instance == null)
            instance = new BluetoothController(context);
        return instance;
    }

}
