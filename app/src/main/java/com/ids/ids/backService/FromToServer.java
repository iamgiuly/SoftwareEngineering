package com.ids.ids.backService;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ids.ids.utils.Parametri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/*Classe che comunica con il Server utilizzando la libreria Volley */

public class FromToServer {

    private RequestQueue queue;
    private boolean check_emerg = false;

    public FromToServer() {
        this.queue = null;

    }

    //todo da modificare andando a leggere la colonna StatoEmergenza della tabella maps
    /*Invia la get al server per controllare lo stato dei nodi - al momento è stata implementata controllando lo stato di tutti i nodi della
    tabella nodi, non appena trova un nodo con il tipoIncendio contrassegnato a true segnala lo stato di emergenza, ritornando true al chiamante */
    public boolean StatoEmergenza() {
        String urlNodi = Parametri.PATH.concat("/FireExit/segnalazione/maps"); //todo sistemare path
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, urlNodi, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(this.toString(), "ricezioneNodi: ricezione stato nodi");
                        try {
                            for (int i = 0; ((i < response.length()) || (check_emerg = false)); i++) {

                                JSONObject element = response.getJSONObject(i);
                                if (element.getBoolean("tipoIncendio")) ;
                                check_emerg = true;
                            }
                        } catch (Exception e) {
                            Log.i(this.toString(), "ricezioneNodi EXCEPTION: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        {
                            errorHandler("Ricezione emergenza", error);
                        }
                    }
                });

        queue.add(request);
        return check_emerg;
    }



    public void inviaPos(){
        //todo?
    }



    private static void errorHandler(String chiamata, VolleyError error) {
        // Salva il messaggio e la causa dell'errore
        String msgError = error.getMessage() + " " + error.getCause();
        if (msgError.equals("null null")) {
            msgError = "SERVER DOWN";
        }
        Log.i("Error Handler", chiamata + " Error Response: " + msgError);
        // Visualizza anche il codice errore data, soltanto nel caso in cui networkResponse non sia nullo
        if (error.networkResponse != null) {
            Log.i("POST Response Error", String.valueOf(error.networkResponse.statusCode) + " "
                    + Arrays.toString(error.networkResponse.data) + " !");
        }
    }



}