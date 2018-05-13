package com.ids.ids.toServer.ServerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ids.ids.R;
import com.ids.ids.entity.Mappa;
import com.ids.ids.ui.MainActivity;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 09/05/2018.
 */

public class inviaAlert  extends AsyncTask<Void, Void, String> {

    private HttpURLConnection connection;
    private final String PATH = Parametri.PATH;
    private ArrayList<String> ListTokens;
    private AsyncTask<Void, Void, Boolean> execute;


    public inviaAlert(ArrayList<String> listTokens) {

        ListTokens = listTokens;
        System.out.println("ListToken Alert: "+ ListTokens.size());
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

    }

    // tutto il codice da eseguire in modo asincrono deve essere inserito nel metodo doInBackground
    // Quando l'operazione termina il risultato viene restituito tramite il metodo onPostExecute.
    @Override
    protected String doInBackground(Void... arg0) {


            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "key=AIzaSyD7a0N56L8RoWSobOSQxvQ6GAnKT5aAkuE" );
                connection.connect();

                String json = "";
                Iterator<String> it = ListTokens.iterator();

                if(it.hasNext())
                    json = "{\"registration_ids\": [\""+it.next()+"\"";
                while(it.hasNext())
                    json = json+", \"" + it.next() + "\"";

                json = json + "], \"data\": {\"title\": \"FireExit - EMERGENZA INCENDIO\" , " +
                                             "\"message\": \"Clicca per metterti al sicuro\"}}";


              //  String T1 = "eqK8YWzqLsI:APA91bF2RPxkM_sgeZQd0eTTEPSZLyYe8FKjq0lxxfhfWErKqkqh6vgYt0fzbaJcFuHHRriyRdQZfr-5zxE5KlwlcNwG-T6LC3ljuzW2gjGROxl_DfHhFntcagnErRaij0xqgt_0mTMA";
              //  String T2 = "d79SucuaBio:APA91bGuh0ZPNtqVCg_EkTdGyP0AgcYaxVPhYz-visJPTC2EKhYxcgDqE2b-P8RSQ8_IV76mLEkQOEo9lTEJ8UE43-GChGLc1QdBG0nSC7SIlAWcfom08VvUtHxHR1bSr5_Mk_H5YJo2";

/*
                json = "{\"registration_ids\": [\""+T1+"\"";
                json = json+", \"" +T2+ "\"";
                json = json + "], \"data\": {\"message\": \"Attenzione! Stato di emergenza\"}}";   */


                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
                wr.write(json.toString());
                wr.flush();
                connection.getInputStream();


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    }

                    catch (Exception e) {
                        e.printStackTrace();
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

    }
}
