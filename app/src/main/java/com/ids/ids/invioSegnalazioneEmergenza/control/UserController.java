package com.ids.ids.invioSegnalazioneEmergenza.control;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.ids.ids.invioSegnalazioneEmergenza.boundary.CommunicationServer;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Mappa;
import com.ids.ids.invioSegnalazioneEmergenza.entity.Nodo;

import java.util.ArrayList;

// TODO rendere singleton, lo stesso oggetto deve essere accessibile da tutte le activity con un riferimento ad essa
public class UserController extends Application{

    private static UserController instance = null;

    private Context context;

    private CommunicationServer communicationServer;
    private ArrayList<Nodo> nodiSelezionati;

    public UserController(){
        this.communicationServer = new CommunicationServer();
    }

    public void init(Context context){
        this.context = context;
    }

    public boolean controllaConnessione(){
        /*ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();*/


        //getSystemService andrebbe richiamato da un'activity... TODO risolvere
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        /*WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifiMgr.isWifiEnabled()){
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if(wifiInfo.getNetworkId() == -1)
                return false;                   //not connected to an access point
            return true;                        //connected to an access point
        }
        return false;                           //Wi-Fi adapter is OFF*/
    }

    public void gestisciTapNodiSottoIncendio(Nodo nodo){
        this.nodiSelezionati.add(nodo);
    }

    // TODO viene mostrata la view con la mappa su cui selezionare i nodi (creare altra Activity?)
    public void gestisciTapBottoneEmergenza(){
        // TODO ...
        this.communicationServer.richiediMappa();
    }

    // TODO i nodi selezionati vengono inviati al server attraverso una richiesta RESTful
    public void gestisciTapBottoneInvioNodi(){
        // TODO ...
        if(this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati)){
            // TODO success
        }
        else{
            // TODO error
        }
    }

    public Mappa richiediMappa() {
        return this.communicationServer.richiediMappa();
    }

    public static UserController getInstance(){
        if(instance == null)
            instance = new UserController();
        return instance;
    }
}
