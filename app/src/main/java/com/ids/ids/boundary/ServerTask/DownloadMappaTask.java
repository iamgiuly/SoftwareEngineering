package com.ids.ids.boundary.ServerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.ids.ids.boundary.MappaServer;
import com.ids.ids.boundary.NodoServer;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.ids.ids.ui.MainActivity;
import com.ids.ids.ui.R;


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


// AsyncTask consente di effettuare operazioni in background
// in thread separati e poi restituire il risultato al thread dell'interfaccia utente.
// Per richiamare questa classe basta creare un'istanza della stessa e chiamare il suo metodo execute.
// < parametri pasati al doBack, progresso , result passato al post >


public class DownloadMappaTask extends AsyncTask<Void, Void, String> {


    private HttpURLConnection connection;
    private final String PATH = "http://192.168.1.8:8080";
    //private final String PATH = "http://172.23.128.184:8080";
    private String PosizioneU;
    private Context context;
    private ProgressDialog download_mappa_in_corso;
    private AsyncTask<Void, Void, Boolean> execute;


    public DownloadMappaTask(Context contxt, String posizioneU) {

        context = contxt;
        PosizioneU = posizioneU;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        execute = new ServerConnection().execute();
        download_mappa_in_corso = new ProgressDialog(context);
        download_mappa_in_corso.setIndeterminate(true);
        download_mappa_in_corso.setCancelable(false);
        download_mappa_in_corso.setCanceledOnTouchOutside(false);
        download_mappa_in_corso.setMessage("Download info mappa in corso..");
        download_mappa_in_corso.show();

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

                //creo il JSON as a key value pair.
                JSONObject Data = new JSONObject();
                Data.put("mac_beacon", PosizioneU);
                System.out.println("Ciao");
                System.out.println("   " + Data.toString());

                //Create the request
                //TODO: URL

                URL url = new URL(PATH + "/FireExit/services/maps/getMappa");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
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
    protected void onPostExecute(String result) {

        super.onPostExecute(result);

        if (result == null) {

            download_mappa_in_corso.dismiss();
            AlertDialog download_mappa_impossibile = new AlertDialog.Builder(context).create();
            download_mappa_impossibile.setTitle("Errore");
            download_mappa_impossibile.setMessage("Controllare connessione WiFi e riprovare");
            download_mappa_impossibile.setCanceledOnTouchOutside(false);
            download_mappa_impossibile.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
            download_mappa_impossibile.show();
            MainActivity m = (MainActivity) context;
            m.findViewById(R.id.segnalazioneButton).setVisibility(View.VISIBLE);

        } else {

            System.out.println("ciao: " + result.toString());

            Type type = new TypeToken<MappaServer>() {
            }.getType();

            MappaServer dati_mappa = new Gson().fromJson(result, type);

            int piano = dati_mappa.getPiano();
            String nome_piantina = dati_mappa.getPiantina();

            ArrayList<NodoServer> nodiServer = dati_mappa.getNodi();
            ArrayList<Nodo> nodi = new ArrayList<Nodo>();

            for (NodoServer n : nodiServer) {

                int p = n.getPiano();
                String beaconID = n.getBeaconId();
                int x = n.getX();
                int y = n.getY();
                int id = 0;

                boolean tipoIncendio;
                boolean tipoUscita;
                Nodo nodo;

                switch (n.getTipo()) {

                    case 1:
                        nodo = new Nodo(id, beaconID, x, y, p);
                        nodi.add(nodo);
                        break;

                    case 2:
                        tipoUscita = false;
                        tipoIncendio = true;
                        System.out.println("s2");
                        nodo = new Nodo(id, beaconID, x, y, tipoUscita, tipoIncendio, p);
                        nodi.add(nodo);
                        break;

                    case 3:
                        tipoUscita = true;
                        tipoIncendio = false;
                        System.out.println("s3");
                        nodo = new Nodo(id, beaconID, x, y, tipoUscita, tipoIncendio, p);
                        nodi.add(nodo);
                        break;
                }

                //   nodo = new Nodo(id , beaconID , x , y , p );
                //   nodi.add(nodo);
            }


            System.out.println("ecco: " + nodi.size());

            Mappa mappa_scaricata = new Mappa(piano, nome_piantina, nodi, null);
            download_mappa_in_corso.dismiss();
            new DownloadPiantinaTask(context, mappa_scaricata).execute();

        }
    }
}

