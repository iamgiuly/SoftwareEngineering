package com.ids.ids.boundary.ServerTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.DB.MappaDAO;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.control.Localizzatore;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Percorso;
import com.ids.ids.ui.MappaView;
import com.ids.ids.utils.Parametri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Task che si preoccupa dell effetuare il download degli aggiornamenti tra DB Locale(App) e DB Server
 */
public class DownloadPercorsoTask extends AsyncTask<Void, Void, String> {

    private HttpURLConnection connection;
    private final String PATH = Parametri.PATH;
    private Context context;
    private String Mac;
    private Mappa mappa;
    private MappaView mappaView;
    private int piano;
    private AsyncTask<Void, Void, Boolean> execute;

    public DownloadPercorsoTask(Context contxt, String mac, int Piano, MappaView mV, Mappa map) {

        context = contxt;
        piano = Piano;
        Mac = mac;
        mappaView = mV;
        mappa = map;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        execute = new ServerConnection().execute();

        System.out.println("onPreExecute");
    }

    // tutto il codice da eseguire in modo asincrono deve essere inserito nel metodo doInBackground
    // Quando l'operazione termina il risultato viene restituito tramite il metodo onPostExecute.
    @Override
    protected String doInBackground(Void... arg0) {

        boolean connesso = false;

        try {
            connesso = execute.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!connesso)
            return null;
        else {

            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                //Create the request
                JSONObject Data = new JSONObject();
                Data.put("posUtente", Mac);
                Data.put("piano", piano);

                URL url = new URL(PATH + "/FireExit/services/percorso/getPercorsoMinimo");
                connection = (HttpURLConnection) url.openConnection();
                // connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                // FACOLTATIVO - Imposta un'intestazione di autorizzazione
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                // Invia il corpo del post
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                writer.write(Data.toString()); //codifica in UTF-8
                writer.flush();
                writer.close();

                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String inputLine;

                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine + "\n");
                }

                br.close();
                return sb.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();

            } finally {

                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... arg0) {

    }

    @Override
    protected void onPostExecute(String dati_percorso) {

        ArrayList<Arco> percorso = null;

        if(dati_percorso == null){

            Log.i("DownloadPercorsoEmer", "Percorso emergenza in locale");
            Mappa mappaAggiornata = MappaDAO.getInstance(context).find(piano);

            Percorso p = Percorso.getInstance();
            percorso = p.calcolaPercorso(mappaAggiornata, mappaAggiornata.getNodoSpecifico(Mac));


        }else{

            Type type = new TypeToken<ArrayList<Arco>>() {
            }.getType();
            // Estrazione dell ArrayList inviato dall app
            percorso = new Gson().fromJson(dati_percorso, type);
        }

        mappaView.setPosUtente(mappa.getNodoSpecifico(Mac));
        mappaView.setPercorso(percorso);

        //Nel caso in cui il percorso sia zero significa che
        //l utente ha raggiunto l uscita
        //per questo lo avvisiamo attraverso un messaggio
        if (percorso.size() == 0) {
            Localizzatore.getInstance((Activity) context);
            CommunicationServer.getInstance(context).richiestaAggiornamenti(false , piano);
            mappaView.messaggio("Sei al sicuro", "Hai raggiunto l uscita", false);
        }

        mappaView.postInvalidate();
    }
}
