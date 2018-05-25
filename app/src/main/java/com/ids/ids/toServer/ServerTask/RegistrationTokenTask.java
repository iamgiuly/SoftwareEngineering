package com.ids.ids.toServer.ServerTask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.entity.Mappa;
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
import java.util.concurrent.ExecutionException;

/**
 * task per la registrazione del token
 */
public class RegistrationTokenTask {

    private String token;

    public RegistrationTokenTask(String recent_Token){

        token = recent_Token;

    }

    public boolean execute() throws IOException {

        Log.d("il token è: ", token);
        // parte code for send
        // CommunicationServer.getInstance(this).registrationToken(token);

        HttpURLConnection connection = null;
        //creo il JSON as a key value pair.
        try {
            JSONObject Data = new JSONObject();
            Data.put("token", token);

            //Create the request
            URL url = new URL(Parametri.PATH + "/FireExit/services/user/registrationToken");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //lancia una eccezione qualora la connessione non viene stabilita entro i tre secondi
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
           // connection.getInputStream();

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine + "\n");
            }

            br.close();

            Type type = new TypeToken<Boolean>() {
            }.getType();

            return new Gson().fromJson(sb.toString(), type);

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

        return false;
    }
}
