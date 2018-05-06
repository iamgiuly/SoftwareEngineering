package com.ids.ids.utils;

import android.content.Context;
import android.content.Intent;

import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.ui.MainActivity;
import com.ids.ids.ui.NormaleActivity;

/**
 * Pone a disposizione i metodi per lanciare le diverse activity di FireExit
 */
public class GestoreUI implements IntGestoreUI{

    private static GestoreUI instance;

    private GestoreUI() {

    }

    /**
     * Lancia l activity Emergenza
     *
     * @param context dell Activity che si sta utilizzando quando questo metodo è richiamato
     */
    public void MandaEmergenzaActivity(Context context) {

        Intent intent = new Intent(context, EmergenzaActivity.class);
        context.startActivity(intent);
    }

    /**
     * Lancia l activity Main
     *
     * @param context dell Activity che si sta utilizzando quando questo metodo è richiamato
     */
    public void MandaMainActivity(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("AvviaTastoEmergenza", true);
        context.startActivity(intent);
    }

    /**
     * Lancia l activity Normale
     *
     * @param context dell Activity che si sta utilizzando quando questo metodo è richiamato
     */
    public void MandaNormaleActivity(Context context) {

        Intent intent = new Intent(context, NormaleActivity.class);
        context.startActivity(intent);
    }

    public static GestoreUI getInstance() {
        if (instance == null)
            instance = new GestoreUI();
        return instance;
    }
}
