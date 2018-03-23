package com.ids.ids.boundary.ServerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Nodo;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;



// AsyncTask consente di effettuare operazioni in background
// in thread separati e poi restituire il risultato al thread dell'interfaccia utente.
// Per richiamare questa classe basta creare un'istanza della stessa e chiamare il suo metodo execute.
// < parametri, progresso , result >


public class InvioNodiTask extends AsyncTask<Void, Void, String> {


    private HttpURLConnection connection;
    private ArrayList<Nodo> NodiSottoIncendio;
    private ProgressDialog loading_segnalazione;
    private Context context;
    private AsyncTask<Void, Void, Boolean> execute;

    public InvioNodiTask(ArrayList<Nodo> nodi, Context contxt) {

        NodiSottoIncendio = nodi;
        context = contxt;

    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

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

                //creo il JSON as a key value pair.
                JSONObject Data = new JSONObject();
                Data.put("nodi_sotto_incendio", NodiSottoIncendio );
                System.out.println("   "+Data.toString());

                // Create the request
                //TODO: URL
                //  --> URL url = new URL(/*URL*/);
                // --> connection = (HttpURLConnection) url.openConnection();
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
            //CODE
        }

    }
}