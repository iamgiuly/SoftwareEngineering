package com.ids.ids.boundary.ServerTask;

import android.os.AsyncTask;
import com.ids.ids.utils.Parametri;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Classe che setta i valori per la connessione con il server
 */
public class ServerConnection extends AsyncTask<Void, Void, Boolean> {

    private HttpURLConnection connection;
    private final String PATH = Parametri.PATH;

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        try {
            connection = (HttpURLConnection) new URL(PATH + "/FireExit").openConnection();
            connection.setConnectTimeout(100000); //lancia una eccezione qualora la connessione non viene stabilita entro i tre secondi
            connection.setReadTimeout(40000);    //lancia un eccezione se la lettura dal server non termina entro i 3 secondi
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }

    }

    @Override
    protected void onProgressUpdate(Void... arg0) {

    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
