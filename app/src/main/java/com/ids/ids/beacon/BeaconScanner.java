package com.ids.ids.beacon;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import com.ids.ids.utils.Parametri;


/**
 * La classe BeaconScanner presenta i metodi per effettuare la scansione periodica dei dispositivi
 * BLE (Bluetooth Low Energy) che si trovano nelle vicinanze
 */
public class BeaconScanner implements IntBeaconScanner{

    private static BeaconScanner instance = null;
    private static final String TAG = "BeaconScanner";

    private Handler scanH = new Handler();  //Utilizzato per la pianificazione del task di avio e stop
    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private BluetoothLeScanner btScanner;  // il Bluetooth deve essere on altrimenti restituisce un null l adapter
    private ScanCallback leScanCallback;
    private ArrayList<ScanResult> RaccogliDevice = new ArrayList<>();
    private int refresh = 0;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BeaconScanner(Context context) {

        //BluethoothManager è utilizzata per ottenere una istanza di Adapter
        //Bluethooth adapter rappresenta l'adattatore Bluetooth del dispositivo locale. BluetoothAdapter
        //consente di eseguire attività Bluetooth fondamentali, come avviare il rilevamento dei dispositivi,

        btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);//Ottengo BluethoothManager e lo salvo in locale
        btAdapter = btManager.getAdapter();    //richiamo l adapter e lo salvo in locale,

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
                Log.e(TAG, "Scansione fallita! Error Code: " + errorCode);
            }
        };
    }

    /**
     * Questo metodo avvia e ferma la scansione periodica, in base al booleano in ingresso
     *
     * @param enable per attivare e disattivare la scansione
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scansione(Boolean enable) {

        // Lo scanner è invocato solo prima della scansione per attendere che il ble sia attivo
        if (btScanner == null)
            btScanner = btAdapter.getBluetoothLeScanner();

        if (enable)
            scanH.post(start);
        else {
            scanH.removeCallbacks(start);
            scanH.removeCallbacks(stop);
        }
    }

    /*
     * Aggiunge ciclicamente i nuovi disp. BLE senza ripetizioni
     * Si è posto un filtro per considerare solamente i Beacon
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addDevice(ScanResult result) {

        boolean presente = false;
        BluetoothDevice device = result.getDevice();
        String nomeDevice;

        if (device.getName() != null)
            nomeDevice = device.getName();
        else
            nomeDevice = "NN";

        // - Vengono considerati solo i dispositivi bluetooth con nome definito nel filtro
        if (Parametri.FILTRO_BLE_DEVICE.equals(nomeDevice)) {
            // - Confronto il Device appena trovato (Device) con i Device appena raccolti fino ad ora (DeviceRac)
            // - il confronto avviene grazie al MACaddress che identifica univocamente il Device
            for (ScanResult DeviceRaccolto : RaccogliDevice)
                if (device.getAddress().equals(DeviceRaccolto.getDevice().getAddress()))
                    presente = true;

            //se non è presente lo aggiungo
            if (!presente)
                RaccogliDevice.add(result);
        }
    }

    ///AVVIO E STOP SCANNER////

    /*
     * Codice task avvia scanner
     */
    private final Runnable start = new Runnable() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            // ogni 2 scansioni si rinnova la lista
            if (refresh == 2) {

                refresh = 0;
                RaccogliDevice.clear();
            }
            refresh++;

            btScanner.startScan(leScanCallback);

            Log.i(TAG, "Start");
            scanH.postDelayed(stop, Parametri.T_SCAN_PERIOD); //pianificazione messaggio di stop

        }
    };

    /*
     * Codice task ferma scanner
     */
    private final Runnable stop = new Runnable() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            if (btAdapter.isEnabled()) {
                btScanner.stopScan(leScanCallback);
                for (ScanResult device : RaccogliDevice)
                    Log.i(TAG,"Device: \n Nome ="+device.getDevice().getName()+"\n MacAdrs ="+device.getDevice().getAddress()+
                    " \n Rssi = "+device.getRssi());
            }

            Log.i(TAG, "Stop");
            scanH.postDelayed(start, Parametri.T_SCAN_PERIOD);
        }
    };

    /////////RICERCA BEACON VICINO//////

    /**
     * Metodo per la ricerca del Beacon più vicino
     * Per ogni Device viene considerato il valore RSSI e , tra tutti , viene preso il Device con il
     * valore RSSI più basso (cioè più vicino all utente)
     *
     * @return  indirizzo MAC del Beacon più vicino all utente
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String BeaconVicino() {

        ScanResult vicino = null;

        if (RaccogliDevice.size() > 0)
            vicino = RaccogliDevice.get(0);

        if (RaccogliDevice.size() > 1)
            for (ScanResult device : RaccogliDevice)
                if (vicino != null)
                    if (device.getRssi() > vicino.getRssi())
                        vicino = device;

        String macAdrs = "NN";
        if (vicino != null)
            macAdrs = vicino.getDevice().getAddress();   //ID del beacon

        return macAdrs;  //ID del beacon
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BeaconScanner getInstance(Context context) {
        if (instance == null)
            instance = new BeaconScanner(context);
        return instance;
    }
}
