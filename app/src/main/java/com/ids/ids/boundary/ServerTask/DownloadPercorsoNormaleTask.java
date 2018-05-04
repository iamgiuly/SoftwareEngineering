package com.ids.ids.boundary.ServerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.DB.MappaDAO;
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
 * Task per l invio della richiesta di download del percorso in modalità normale
 */
public class DownloadPercorsoNormaleTask extends AsyncTask<Void, Void, String> {

    private HttpURLConnection connection;
    private final String PATH = Parametri.PATH;
    private AsyncTask<Void, Void, Boolean> execute;

    private String MacPosU;
    private String MacDest;
    private int Piano;
    private MappaView mappaView;
    private Mappa mappa;
    private Context context;
    private boolean Enable;
    private ProgressDialog download_percorso_in_corso;

    public DownloadPercorsoNormaleTask(Context contxt, String macPU , int piano , MappaView mv, Mappa map, String macD ,boolean enable)  {

        context = contxt;
        Piano = piano;
        MacPosU = macPU;
        mappaView = mv;
        mappa = map;
        MacDest = macD;
        Enable = enable;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        if(Enable){

            execute = new ServerConnection().execute();
            download_percorso_in_corso = new ProgressDialog(context);
            download_percorso_in_corso.setIndeterminate(true);
            download_percorso_in_corso.setCancelable(false);
            download_percorso_in_corso.setCanceledOnTouchOutside(false);
            download_percorso_in_corso.setMessage("Download percorso in corso..");
            download_percorso_in_corso.show();
        }
        execute = new ServerConnection().execute();
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

                Log.i("DownloadNormaleTask","Connesso al server");

                //Create the request
                JSONObject Data = new JSONObject();
                Data.put("posUtente", MacPosU);
                Data.put("destinazione", MacDest);
                Data.put("piano", Piano);

                URL url = new URL(PATH + "/FireExit/services/percorso/getPercorsoMinimoNormale");
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPostExecute(String dati_percorso) {

        ArrayList<Arco> percorso;

        if(Enable)
            download_percorso_in_corso.dismiss();

        if (dati_percorso == null){

            Log.i("DownloadNormaleTask", "Percorso normale in locale");

            Percorso p = Percorso.getInstance();
            percorso = p.calcolaPercorsoNormale(mappa, mappa.getNodoSpecifico(MacPosU), mappa.getNodoSpecifico(MacDest));

        }else{

            Type type = new TypeToken<ArrayList<Arco>>() {
            }.getType();
            // Estrazione dell ArrayList inviato dall app
            percorso = new Gson().fromJson(dati_percorso, type);
        }

        if (percorso.size() == 0) {
            Localizzatore.getInstance((Activity) context).stopFinderALWAYS();
            mappaView.messaggio("Good!", "Hai raggiunto la destinazione indicata", false);
        }

        mappaView.setPosUtente(mappa.getNodoSpecifico(MacPosU));
        mappaView.setPercorso(percorso);

        mappaView.postInvalidate();
    }
}
