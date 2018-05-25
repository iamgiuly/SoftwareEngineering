package com.ids.ids.beacon;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.ids.ids.User;
import com.ids.ids.toServer.CommunicationServer;
import com.ids.ids.utils.Parametri;

/**
 * Presenta i metodi per localizzare l utente grazie ai risultati (MAC address)
 * forniti dal BeaconScanner
 * <br>
 * Nota localizzazioni:
 * <br>
 * ALWAYS: mantiene sempre attivo il BeaconScanner
 *         mantiene sempre attivo il Runnable findMeAlways
 * <br>
 * ONE:    appena trovato il Beacon più vicino il BeaconScanner e il Runnable findMeONE vengono fermati
 */

public class Localizzatore implements IntLocalizzatore {

    private static Localizzatore instance = null;
    private static final String TAG = "Localizzatore";

    private Context context;
    private BeaconScanner scanner;
    private Handler finder;
    private ProgressDialog loading_localizzazione;
    private User user;
    private CommunicationServer communicationServer;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private Localizzatore(Context contxt) {

        context = contxt;
        scanner = BeaconScanner.getInstance(contxt);
        finder = new Handler();
        user = User.getInstance((Activity) contxt);
        communicationServer = CommunicationServer.getInstance(contxt);
    }


    /*
     * FindMeONE viene è un Runnable utilizzato al click del bottone segnala emergenza
     * Appena la posizione dell utente è stata trovata termina la scansione
     */
    private final Runnable findMeONE = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            Log.i(TAG, "Inizio Ricerca pos ONE");
            String macAdrs = scanner.BeaconVicino();

            if (macAdrs.equals("NN")) {
                // Non è stato ancora trovato nessun Beacon dallo scanner
                // Attendo nuovamente
                finder.postDelayed(findMeONE, Parametri.T_POSIZIONE);
            } else {
                // E' stato trovato il beacon dallo scanner
                loading_localizzazione.dismiss();               //  Tolgo il messaggio di localizzazione
                user.setMacAdrs(macAdrs);
                communicationServer.richiestaMappa(macAdrs);  //   Avvio l Activity passandogli il macAdrs
                stopFinderONE();                              //    Fermo questo Runnable
            }
        }
    };

    /*
     * FindMeALWAYS è un Runnable utilizzato dalla mappa
     */
    private final Runnable findMeALWAYS = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {

            Log.i(TAG, "Inizio Ricerca pos ALWAYS");
            String macAdrs = scanner.BeaconVicino();

            if (macAdrs.equals("NN")) {
                // Non è stato ancora trovato nessun Beacon dallo scanner
                // Attendo nuovamente
                finder.postDelayed(findMeALWAYS, Parametri.T_POSIZIONE);
            } else {
                System.out.println("MAC: " + macAdrs);     // E' stato trovato il beacon dallo scanner
                finder.postDelayed(findMeALWAYS, Parametri.T_POSIZIONE);

                user.setMacAdrs(macAdrs);

                if (user.getModalita() == User.MODALITA_EMERGENZA)
                    communicationServer.richiediPercorsoEmergenza(macAdrs,
                            user.getPianoUtente(),
                            user.getMappaView(),
                            user.getMappa());
                else if (user.getModalita() == User.MODALITA_NORMALEPERCORSO)
                    communicationServer.richiestaPercorsoNormale(macAdrs,
                            user.getPianoUtente(),
                            user.getMappaView(),
                            user.getMappa(),
                            user.getNodoDestinazione().getBeaconId()
                            , false);
            }
        }
    };

    /*
    ========================================================================================================
     AVVIO RUNNABLE
    =======================================================================================================
     */

    /**
     * Avvia la localizzazione ONE
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startFinderONE() {

        scanner.scansione(true);
        finder.postDelayed(findMeONE, Parametri.T_POSIZIONE);
        //visualizzazione messaggio di localizzazione
        loading_localizzazione = new ProgressDialog(context);
        loading_localizzazione.setIndeterminate(true);
        loading_localizzazione.setCancelable(false);
        loading_localizzazione.setCanceledOnTouchOutside(false);
        loading_localizzazione.setMessage("Non muoverti localizzazione in corso...");
        loading_localizzazione.show();

    }

    /**
     * Avvia la localizzazione ALWAYS
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startFinderALWAYS() {

        scanner.scansione(true);                                  //Avvio scansione BLE
        finder.postDelayed(findMeALWAYS, Parametri.T_POSIZIONE);
    }


     /*
    ========================================================================================================
     STOP RUNNABLE
     =======================================================================================================
     */

    /**
     * Ferma la localizzazione ALWAYS
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopFinderALWAYS() {

        scanner.scansione(false);                //  Fermo la scansione dello scanner
        finder.removeCallbacks(findMeALWAYS);
    }

    /**
     * Ferma la localizzazione ONE
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopFinderONE() {

        scanner.scansione(false);                //  Fermo la scansione dello scanner
        finder.removeCallbacks(findMeONE);
    }

    private void setContext(Context contxt) {

        context = contxt;
    }

    public static Localizzatore getInstance(Activity context) {
        if (instance == null)
            instance = new Localizzatore(context);
        else
            instance.setContext(context);
        return instance;
    }
}
