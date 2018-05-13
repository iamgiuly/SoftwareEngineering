package com.ids.ids.toServer.ServerTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.utils.Parametri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 09/05/2018.
 */

public class OttieniTokens extends AsyncTask<Void, Void, String> {

    private HttpURLConnection connection;
    private final String PATH = Parametri.PATH;
    private AsyncTask<Void, Void, Boolean> execute;
    private ArrayList<String> listToken;

    public OttieniTokens(ArrayList<String> ListToken) {

        listToken = ListToken;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
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

            Log.i("DownInfoMappaTask","Connesso al server");

            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                //creo il JSON as a key value pair.

                //Create the request
                URL url = new URL(PATH + "/FireExit/services/user/getTokens");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                // FACOLTATIVO - Imposta un'intestazione di autorizzazione
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

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
            }finally {

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

        ArrayList<String> listServer;
        if(result != null){

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            // Estrazione dell ArrayList inviato dall app
            listServer = new Gson().fromJson(result, type);



            if(listServer.size() == 0)
                listToken = null;
            else
                for(String token: listServer)
                    listToken.add(token);

            System.out.println("ListToken token:"+listServer.size());
        }
    }
}
