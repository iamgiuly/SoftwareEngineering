package com.ids.ids.utils;

import android.content.Context;
import android.content.Intent;

import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.ui.MainActivity;
import com.ids.ids.ui.NormaleActivity;

/**
 * Created by User on 05/05/2018.
 */

public class GestoreUI implements IntGestoreUI{

    private static GestoreUI instance;

    private GestoreUI() {

    }

    public void MandaEmergenzaActivity(Context context) {

        Intent intent = new Intent(context, EmergenzaActivity.class);
        context.startActivity(intent);
    }

    public void MandaMainActivity(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("AvviaTastoEmergenza", true);
        context.startActivity(intent);
    }

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
