package com.ids.ids.boundary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;


public class BeaconScanner {

    private Handler scanH = new Handler();
    private int refresh = 0;
    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private BluetoothLeScanner btScanner;
    private ScanCallback leScanCallback;

    private ArrayList<ScanResult> RaccogliDevice = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BeaconScanner(Activity context) {

        //BluethoothManager è utilizzata per ottenere una istanza di Adapter
        //Bluethooth adapter rappresenta l'adattatore Bluetooth del dispositivo locale. BluetoothAdapter
        // consente di eseguire attività Bluetooth fondamentali, come avviare il rilevamento dei dispositivi,
        btManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE); //Ottengo BluethoothManager e lo salvo in locale
        btAdapter = btManager.getAdapter();                                        //richiamo l adapter e lo salvo in locale,
        btScanner = btAdapter.getBluetoothLeScanner();


        leScanCallback = new ScanCallback() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                //Aggiungo al raccoglitore il device trovato
                addDevice(result);

            }
            // Se fallisce la scansione
            @Override
            public void onScanFailed(int errorCode) {
                Log.e("Scan Failed", "Error Code: " + errorCode);
            }
        };


    }


    // Questo metodo avvia e ferma la scansione periodica, in base al booleano in ingresso
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Scansione(Boolean enable) {


        if (enable)
            scanH.post(start);
        // else {

        //     scanH.removeCallbacks(start);
        //     scanH.removeCallbacks(stop);

        // }

    }

    // Aggiungi ciclicamente i nuovi disp. BLE
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addDevice(ScanResult result) {

        boolean presente = false;
        BluetoothDevice Device = result.getDevice();
        //     String nomeDev;

      /*  try {
            nomeDev = Device.getName();
        } catch (Exception ex) {

            Log.i("BeaconListener","Errore getName");
            nomeDev = "NN";
        }*/


        // Vengono considerati solo i dispositivi bluetooth con nome definito nel filtro
        //ATTENZIONE ANDROID BT SOLO PER PROVA
        // if ("ANDROID BT".equals(nomeDev)) {

        //Confronto il Device appena trovato (Device)
        //con i Device appena raccolti fino ad ora (DeviceRac)
        //il confronto avviene grazie al MACaddress che identifica univocamente il Device
        for (ScanResult DeviceRac : RaccogliDevice)
            if (Device.getAddress().equals(DeviceRac.getDevice().getAddress()))
                presente = true;

        //se non è presente lo aggiungo
        if (!presente)
            RaccogliDevice.add(result);

        //  }
    }





    ///AVVIO E STOP SCANNER
    ///////////////////////////////////////////////////////////////////


    // Codice task avvia scanner
    private final Runnable start = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            // ogni una scansioni si rinnova la lista
            if (refresh == 1) {

                refresh = 0;
                RaccogliDevice.clear();

            }
            refresh++;



            btScanner.startScan(leScanCallback);


            Log.i("Scanning", "Start");
            scanH.postDelayed(stop, 6000);
        }
    };

    // Codice task ferma scanner
    private final Runnable stop = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            System.out.println("-Stampo risultati");

            if (btAdapter.isEnabled()) {
                btScanner.stopScan(leScanCallback);

                for (ScanResult device : RaccogliDevice) {

                    System.out.println("OGGETTO:");
                    System.out.println(device.getDevice().getName());
                    System.out.println(device.getDevice().getAddress());
                    System.out.println(device.getRssi());
                }
            }


            Log.i("Scanning", "Stop");
            scanH.postDelayed(start, 6000);
        }
    };


}
