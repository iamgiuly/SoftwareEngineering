package com.ids.ids.toServer.ServerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ids.ids.User;
import com.ids.ids.entity.Nodo;
import com.ids.ids.utils.GestoreUI;
import com.ids.ids.utils.Parametri;


/**
 * Task per l invio dei nodi sotto incendio segnalati dall utente
 */
public class InvioNodiTask extends AsyncTask<Void, Void, String> {

    private HttpURLConnection connection;
    private final String PATH = Parametri.PATH;
    private AsyncTask<Void, Void, Boolean> execute;

    private ArrayList<Nodo> NodiSottoIncendio;
    private ProgressDialog loading_segnalazione;
    private Context context;
    private User user;
    private GestoreUI gestoreUI;

    public InvioNodiTask(ArrayList<Nodo> nodi, Context contxt) {

        NodiSottoIncendio = nodi;
        context = contxt;
        user = User.getInstance((Activity)context);
        gestoreUI = GestoreUI.getInstance();
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        execute = new ServerConnection().execute();
        loading_segnalazione = new ProgressDialog(context);
        loading_segnalazione.setIndeterminate(true);
        loading_segnalazione.setCancelable(false);
        loading_segnalazione.setCanceledOnTouchOutside(false);
        loading_segnalazione.setMessage("Segnalazione emergenza in corso...");
        loading_segnalazione.show();

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

                Log.i("InvioNodiTask","Connesso al server");

                //creo il JSON as a key value pair
                Gson gson = new Gson();
                String Data = gson.toJson(NodiSottoIncendio);

                // Create the request
                URL url = new URL(PATH + "/FireExit/services/maps/segnalazione");
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
            loading_segnalazione.dismiss();

            AlertDialog segnalazione_impossibile = new AlertDialog.Builder(context).create();
            segnalazione_impossibile.setTitle("Errore");
            segnalazione_impossibile.setMessage("Controllare connessione WiFi e riprovare");
            segnalazione_impossibile.setCanceledOnTouchOutside(false);
            segnalazione_impossibile.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
            segnalazione_impossibile.show();

        } else {
            JsonObject jobj = new Gson().fromJson(result, JsonObject.class);
            String esito = jobj.get("esito").getAsString();

            if (esito.equals("true")) {

                loading_segnalazione.dismiss();

                AlertDialog segnalazione_avvenuta = new AlertDialog.Builder(context).create();
                segnalazione_avvenuta.setTitle("Grazie");
                segnalazione_avvenuta.setMessage("Segnalazione avvenuta con successo! Clicca OK per metterti al sicuro");
                segnalazione_avvenuta.setCanceledOnTouchOutside(false);
                segnalazione_avvenuta.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {


                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                gestoreUI.MandaMainActivity(context);
                            }

                        });
                segnalazione_avvenuta.show();
            }
        }
    }
}