package com.ids.ids.utils;

import android.content.Context;

import com.ids.ids.entity.Arco;
import com.ids.ids.entity.ArcoDAO;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.MappaDAO;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.NodoDAO;
import com.ids.ids.ui.R;

import java.util.ArrayList;

public class DebugSettings {
    public static boolean SEED_DB = false;
    public static boolean SCAN_BLUETOOTH = false;
    public static boolean CHECK_WIFI = false;

    public static void seedDb(Context context){
        ArrayList<Nodo> nodi = new ArrayList<>();
        ArrayList<Arco> archi = new ArrayList<>();
        int idMappa = 145;
        Nodo nodo1 = new Nodo(1, "1", 20, 20, idMappa);
        Nodo nodo2 = new Nodo(2, "2", 20, 40, idMappa);
        Nodo nodo3 = new Nodo(3, "3", 20, 60, idMappa);
        Nodo nodo4 = new Nodo(4, "4", 20, 80, true, idMappa);
        Nodo nodo5 = new Nodo(5, "5", 60, 20, idMappa);
        Nodo nodo6 = new Nodo(6, "6", 60, 40, idMappa);
        Nodo nodo7 = new Nodo(7, "7", 60, 60, idMappa);
        Nodo nodo8 = new Nodo(8, "8", 60, 80, true, idMappa);
        nodi.add(nodo1);
        nodi.add(nodo2);
        nodi.add(nodo3);
        nodi.add(nodo4);
        nodi.add(nodo5);
        nodi.add(nodo6);
        nodi.add(nodo7);
        nodi.add(nodo8);
        archi.add(new Arco(1, nodo1, nodo2, null));
        archi.add(new Arco(2, nodo3, nodo4, null));
        archi.add(new Arco(3, nodo5, nodo6, null));
        archi.add(new Arco(4, nodo7, nodo8, null));
        archi.add(new Arco(5, nodo1, nodo4, null));
        archi.add(new Arco(6, nodo2, nodo8, null));

        MappaDAO.getInstance(context).clear();
        NodoDAO.getInstance(context).clear();
        ArcoDAO.getInstance(context).clear();
        MappaDAO.getInstance(context).insert(new Mappa(idMappa, R.drawable.map145, nodi, archi));
    }
}
