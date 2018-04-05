package com.ids.ids.control;

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
 * forniti dallo Scanner
 */

public class Localizzatore {

    private Context context;
    private BeaconScanner Scanner;
    private Handler finder;
    private ProgressDialog loading_localizzazione;
    private UserController userController;
    private MappaView mappaView;


    public Localizzatore(Context contxt, BeaconScanner scanner) {

        context = contxt;
        Scanner = scanner;
        finder = new Handler();
        userController = UserController.getInstance((Activity) context);

    }

    public Localizzatore(MappaView mView, Context contxt, BeaconScanner scanner) {

        context = contxt;
        Scanner = scanner;
        finder = new Handler();
        userController = UserController.getInstance((Activity) context);
        mappaView = mView;
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

    // FindMeONE viene è un Runnable utilizzato al click del bottone segnala emergenza
    // Appena la posizione dell utente è stata trovata termina la scansione
    private final Runnable findMeONE = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            Log.i("Localizzatore", "Inizio Ricerca pos ONE");
            String macAdrs = Scanner.BeaconVicino();

            if (macAdrs.equals("NN")) {
                // Non è stato ancora trovato nessun Beacon dallo scanner
                // Attendo nuovamente
                finder.postDelayed(findMeONE, Parametri.T_POSIZIONE_SEGNALAZIONE);
            } else {
                // E' stato trovato il beacon dallo scanner
                System.out.println("MAC: " + macAdrs);
                Scanner.scansione(false);                //  Fermo la scansione dello scanner
                loading_localizzazione.dismiss();               //  Tolgo il messaggio di localizzazione
                userController.richiestaMappa(context, macAdrs);  //   Avvio l Activity passandogli il macAdrs
                stopFinderONE();                              //    Fermo questo Runnable
            }
        }
    };

    // FindMeALWAYS è un Runnable utilizzato dalla mappa
    private final Runnable findMeALWAYS = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            Log.i("Localizzatore", "Inizio Ricerca pos ALWAYS");
            String macAdrs = Scanner.BeaconVicino();

            if (macAdrs.equals("NN")) {
                // Non è stato ancora trovato nessun Beacon dallo scanner
                // Attendo nuovamente
                finder.postDelayed(findMeALWAYS, Parametri.T_POSIZIONE_EMERGENZA);
            } else {
                System.out.println("MAC: " + macAdrs);     // E' stato trovato il beacon dallo scanner
                userController.richiediPercorso(macAdrs, mappaView);
                finder.postDelayed(findMeALWAYS, Parametri.T_POSIZIONE_EMERGENZA);
            }
        }
    };

    /*
    ========================================================================================================
     AVVIO RUNNABLE
     =======================================================================================================
     */


    // Avvia la localizzazione ONE
    public void startFinderONE() {

        finder.postDelayed(findMeONE, Parametri.T_POSIZIONE_EMERGENZA);
        //visualizzazione messaggio di localizzazione
        loading_localizzazione = new ProgressDialog(context);
        loading_localizzazione.setIndeterminate(true);
        loading_localizzazione.setCancelable(false);
        loading_localizzazione.setCanceledOnTouchOutside(false);
        loading_localizzazione.setMessage("Non muoverti localizzazione in corso...");
        loading_localizzazione.show();

    }

    // Avvia la localizzazione ALWAYS
    public void startFinderALWAYS() {

        finder.postDelayed(findMeALWAYS, Parametri.T_POSIZIONE_EMERGENZA);
    }


     /*
    ========================================================================================================
     STOP RUNNABLE
     =======================================================================================================
     */


    // Ferma la localizzazione ALWAYS
    public void stopFinderALWAYS() {

        finder.removeCallbacks(findMeALWAYS);
    }

    // Ferma la localizzazione ONE
    private void stopFinderONE() {

        finder.removeCallbacks(findMeONE);
    }
}
