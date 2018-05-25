package com.ids.ids.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.ids.ids.R;
import com.ids.ids.entity.Mappa;
import com.ids.ids.toServer.CommunicationServer;
import com.ids.ids.beacon.Localizzatore;
import com.ids.ids.User;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.GestoreUI;


/**
 * Questa activity si comporta dinamicamente in base alla modalità dell applicazione.
 * E' in grado di gestire le seguenti modalità:
 *
 * SEGNALAZIONE:  visualizza la piantina e i nodi, relativi asl piano in cui l utente si trova, in modo
 *                tale che l utente può segnalare i nodi sottoIncendio.
 *
 * EMERGENZA:     visualizza la piantina i nodi e il percorso che l utente deve segiuire per raggiungere
 *                l uscita di emergenza più vicina.
 *                Inoltre è data all utente la possibilità di effettuare un cambio piano.
 */
public class EmergenzaActivity extends AppCompatActivity {

    private User user;
    private CommunicationServer communicationServer;
    private Localizzatore localizzatore;
    private MappaView mappaView;
    private ArrayList<Nodo> nodiSelezionati; // nodi di cui bisogna cambiare il flag "sotto incendio"

    private Button inviaNodiButton;                 // invisibile all'inizio
    private Button cambiapianoButton;
    private GestoreUI gestoreUI;
    private Context context;

    /**
     * Vengono visualizzati gli elementi della UI e settati i listener,
     *
     * @param savedInstanceState
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergenza);
        context = this;

        user = User.getInstance(this);
        localizzatore = Localizzatore.getInstance(this);
        communicationServer = CommunicationServer.getInstance(this);
        gestoreUI = GestoreUI.getInstance();

        nodiSelezionati = new ArrayList<>();

        inviaNodiButton = findViewById(R.id.inviaNodiButton);
        inviaNodiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerBottoneInvioNodi();
            }
        });

        // inizializza la View della mappa
        mappaView = findViewById(R.id.mappaView);
        user.setMappaView(mappaView);

        try {

            // CASO SEGNALAZIONE
            if (user.getModalita() == user.MODALITA_SEGNALAZIONE) {

                //vengono tolti i nodi uscita
                Mappa mappa = user.getMappa();
                ArrayList nodi = new ArrayList<Nodo>();
                for(Nodo nodo : mappa.getNodi())
                    if(!nodo.isTipoUscita())
                        nodi.add(nodo);
                mappa.setNodi(nodi);

                mappaView.setMappa(mappa);
                // mappaView.setPosUtente(user.getMappa().getNodoSpecifico(user.getMacAdrs()));
                mappaView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
                            return true;
                        NodoView nodoView = mappaView.getNodoPremuto((int) motionEvent.getX(), (int) motionEvent.getY());
                        if (nodoView != null)
                            listenerNodoSelezionato(nodoView);
                        mappaView.invalidate();
                        return true;

                    }
                });

            } else {
                //CASO EMERGENZA

                mappaView.setMappa(user.getMappa(), true);
                localizzatore.startFinderALWAYS();                               //Avvio localizzazione
                //Avvia aggiornamento db locale
                communicationServer.richiestaAggiornamenti(true, user.getPianoUtente());

                cambiapianoButton = findViewById(R.id.CambiaPianoButton);
                cambiapianoButton.setVisibility(View.VISIBLE);
                cambiapianoButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        listenerBottoneCambiaPiano();
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localizzatore != null)
            localizzatore.stopFinderALWAYS();
        communicationServer.richiestaAggiornamenti(false, user.getPianoUtente());
        user.DropDB();
        mappaView.deleteImagePiantina();
    }

    //per finish()
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {

        super.onStop();
    }

    /**
     * Richiamato dal listener associato ad un nodo, tale nodo deve essere opportunamente contrassegnato
     * oltre ad essere aggiunto alla / rimosso dalla lista dei nodi selezionati,
     * viene quindi controllato se c'è almeno un nodo selezionato in modo tale da
     * rendere visibile o invisibile il bottone "Invia Nodi"
     */
    private void listenerNodoSelezionato(NodoView nodoView) {

        Nodo nodo = nodoView.getNodo();

        if (selezionaNodo(nodo))
            inviaNodiButton.setVisibility(View.VISIBLE);
        else
            inviaNodiButton.setVisibility(View.INVISIBLE);

        nodoView.setImage(nodo.getImage());
    }


    /**
     * Aggiunge o rimuove dalla lista dei nodi selezionati il nodo con l'id passato come parametro
     *
     * @param nodo nodo da selezionare o deselezionare
     * @return true se c'è almeno un nodo selezionato
     */

    private boolean selezionaNodo(Nodo nodo) {
        nodo.setIncendio();

        if (nodo.isCambiato()) {
            if (!nodiSelezionati.contains(nodo))
                nodiSelezionati.add(nodo);
        } else if (nodiSelezionati.contains(nodo))
            nodiSelezionati.remove(nodo);

        return !nodiSelezionati.isEmpty();
    }

    /**
     * Listener bottone inviaNodi.
     * Permette di richiamare il metodo opportuno (InviaNodiSottoIncendio) del communicationServer
     * per l invio della segnalazione.
     */
    private void listenerBottoneInvioNodi() {

        communicationServer.inviaNodiSottoIncendio(nodiSelezionati/*, this*/);
    }

    /**
     * Listener bottone CambioPiano.
     * Permette di visualizzare un popup con pulsanti SI/NO in cui si chiede all utente se è sicuro o meno di cambiare piano.
     * Al click sul si avvia nuovamernte la localizzazione
     */
    private void listenerBottoneCambiaPiano() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sei sicuro di voler cambiare piano?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(DialogInterface dialog, int response) {

            switch (response) {

                case DialogInterface.BUTTON_POSITIVE: {
                    dialog.cancel();
                    localizzatore.stopFinderALWAYS();
                    communicationServer.richiestaAggiornamenti(false, user.getPianoUtente());
                    finish();
                    gestoreUI.MandaMainActivity(context);
                    break;
                }

                case DialogInterface.BUTTON_NEGATIVE: {
                    dialog.cancel();
                    break;
                }
            }
        }
    };
}
