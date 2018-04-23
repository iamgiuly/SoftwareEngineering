package com.ids.ids.control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.ui.MappaView;
import com.ids.ids.utils.Parametri;

/**
 * La classe Localizzatore presenta i metodi per localizzare l utente grazie ai risultati (MAC address)
 * forniti dallo scanner
 */

public class Localizzatore {

    private static Localizzatore instance = null;

    private Context context;
    private BeaconScanner scanner;
    private Handler finder;
    private ProgressDialog loading_localizzazione;
    private UserController userController;
    private MappaView mappaView;

    private boolean nuovoPercorso;      // per richiedere il ricalcolo del percorso


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private Localizzatore(Context contxt) {
        context = contxt;
        scanner = BeaconScanner.getInstance(contxt);
        finder = new Handler();
        userController = UserController.getInstance((Activity) context);

        nuovoPercorso = false;
    }

    public void setMappaView(MappaView mappaView) {
        this.mappaView = mappaView;
    }
/*
    =========================================================================================================

      Nota localizzazioni:

      ALWAYS: Utilizzata dalla mappa
              tiene sempre attivo il BeaconScanner
              tiene sempre attivo il Runnable findMeAlways

      ONE:    Utilizzato al click del bottone SegnalaEmergenza
              Appena trovato il Beacon più vicino il BeaconScanner e il Runnable findMeONE vengono fermati

     ========================================================================================================
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String calcolaPosizione(){
        return scanner.BeaconVicino();
    }

    // FindMeONE viene è un Runnable utilizzato al click del bottone segnala emergenza
    // Appena la posizione dell utente è stata trovata termina la scansione
    private final Runnable findMeONE = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            Log.i("Localizzatore", "Inizio Ricerca pos ONE");
            String macAdrs = scanner.BeaconVicino();

            if (macAdrs.equals("NN")) {
                // Non è stato ancora trovato nessun Beacon dallo scanner
                // Attendo nuovamente
                finder.postDelayed(findMeONE, Parametri.T_POSIZIONE_SEGNALAZIONE);
            } else {
                // E' stato trovato il beacon dallo scanner
                System.out.println("MAC: " + macAdrs);
                loading_localizzazione.dismiss();               //  Tolgo il messaggio di localizzazione
                userController.richiestaMappa(context, macAdrs);  //   Avvio l Activity passandogli il macAdrs
                stopFinderONE();                              //    Fermo questo Runnable
            }
        }
    };


    public BeaconScanner getScanner() {
        return scanner;
    }

    // Avvia la localizzazione ONE
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startFinderONE() {
        scanner.avviaScansione();

        finder.postDelayed(findMeONE, Parametri.T_POSIZIONE_EMERGENZA);
        //visualizzazione messaggio di localizzazione
        loading_localizzazione = new ProgressDialog(context);
        loading_localizzazione.setIndeterminate(true);
        loading_localizzazione.setCancelable(false);
        loading_localizzazione.setCanceledOnTouchOutside(false);
        loading_localizzazione.setMessage("Non muoverti localizzazione in corso...");
        loading_localizzazione.show();

    }

     /*
    ========================================================================================================
     STOP RUNNABLE
     =======================================================================================================
     */

    // Ferma la localizzazione ONE
    private void stopFinderONE() {
        scanner.fermaScansione();
        finder.removeCallbacks(findMeONE);
    }


    public boolean isNuovoPercorso() {
        return nuovoPercorso;
    }
    public void setNuovoPercorso(boolean nuovoPercorso) {
        this.nuovoPercorso = nuovoPercorso;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Localizzatore getInstance(Context context) {
        if (instance == null)
            instance = new Localizzatore(context);
        return instance;
    }
}
