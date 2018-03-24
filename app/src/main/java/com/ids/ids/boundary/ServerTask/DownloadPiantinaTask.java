package com.ids.ids.boundary.ServerTask;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ids.ids.control.UserController;
import com.ids.ids.entity.Mappa;
import com.ids.ids.ui.EmergenzaActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPiantinaTask extends AsyncTask<Void, String, Void> {

    private HttpURLConnection connection;
    private final String PATH = "http://192.168.1.8:8080";
   // private final String PATH = "http://172.23.128.184:8080";

    private Context context;
    private Mappa mappa_scaricata;
    private ProgressDialog download_immagini_in_corso;

    private UserController usercontroller;

    public DownloadPiantinaTask(Context ctx, Mappa mappa) {

        context = ctx;
        mappa_scaricata = mappa;
        usercontroller= UserController.getInstance((Activity)context);

    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        download_immagini_in_corso = new ProgressDialog(context);
        download_immagini_in_corso.setMessage("Download della piantina in corso...");
        download_immagini_in_corso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        download_immagini_in_corso.setCancelable(false);
        download_immagini_in_corso.setCanceledOnTouchOutside(false);
        download_immagini_in_corso.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {


        try {
            Thread.sleep(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {


            System.out.println(mappa_scaricata.getPiantina());
            URL url = new URL(PATH+"/FireExit/services/maps/downloadPiantina/"+mappa_scaricata.getPiantina());
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int lunghezza_file = connection.getContentLength();

            InputStream input = connection.getInputStream();
            FileOutputStream output = context.openFileOutput(mappa_scaricata.getPiantina()+".png" ,Context.MODE_PRIVATE);

            int read;
            long total = 0;
            byte[] data = new byte[1024];

            while ((read = input.read(data)) != -1) {
                total += read;
                publishProgress("" + (int) ((total * 100) / lunghezza_file));
                output.write(data, 0, read);
            }

            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {

        super.onProgressUpdate(progress);
        download_immagini_in_corso.setProgress(Integer.parseInt(progress[0]));

    }

    @Override
    protected void onPostExecute(Void arg0) {

        super.onPostExecute(arg0);
        download_immagini_in_corso.dismiss();

        System.out.println("Piantina scaricata");
        usercontroller.setMappa(mappa_scaricata);
        usercontroller.MandaEmergenzaActivity();

    }
}
