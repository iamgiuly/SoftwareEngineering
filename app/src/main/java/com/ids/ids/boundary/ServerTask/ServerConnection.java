package com.ids.ids.boundary.ServerTask;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 24/03/2018.
 */

public class ServerConnection extends AsyncTask<Void, Void, Boolean> {

    private HttpURLConnection connection;
    private final String PATH = "http://192.168.1.8:8080";
    //private final String PATH = "http://172.23.128.184:8080";

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        try {
            connection = (HttpURLConnection) new URL(PATH + "/").openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
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
