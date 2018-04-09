package com.ids.ids.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ids.ids.DB.DBHelper;
import com.ids.ids.entity.Arco;
import com.ids.ids.DB.ArcoDAO;
import com.ids.ids.entity.Mappa;
import com.ids.ids.DB.MappaDAO;
import com.ids.ids.entity.Nodo;
import com.ids.ids.DB.NodoDAO;
import com.ids.ids.entity.Peso;
import com.ids.ids.entity.PesoArco;
import com.ids.ids.DB.PesoArcoDAO;
import com.ids.ids.DB.PesoDAO;
import com.ids.ids.ui.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DebugSettings {

    public static boolean SEED_DB = true;
    public static boolean SCAN_BLUETOOTH = false;
    public static boolean CHECK_WIFI = false;

    public static int PIANO_DEFAULT = 145;
    public static int PIANO_DRAWABLE_DEFAULT = R.drawable.map145;

    private static boolean DB_SEEDED = false;

    public static void seedDb(Context context){
        if(DB_SEEDED) return;

        int idMappa = PIANO_DEFAULT;

        MappaDAO.getInstance(context).clear();
        NodoDAO.getInstance(context).clear();
        PesoArcoDAO.getInstance(context).clear();
        ArcoDAO.getInstance(context).clear();
        PesoDAO.getInstance(context).clear();

        ArrayList<Peso> pesi = new ArrayList<>();
        Peso peso1 = new Peso(1, "Lunghezza", 3);
        Peso peso2 = new Peso(2, "Viabilità", 1);
        pesi.add(peso1);
        pesi.add(peso2);
        PesoDAO.getInstance(context).insert(peso1);
        PesoDAO.getInstance(context).insert(peso2);

        ArrayList<Nodo> nodi = new ArrayList<>();
        Nodo nodo1 = new Nodo(1, "1", 10, 20, idMappa);
        Nodo nodo2 = new Nodo(2, "2", 20, 10, idMappa);
        Nodo nodo3 = new Nodo(3, "3", 50, 60, false, true, idMappa);
        Nodo nodo4 = new Nodo(4, "4", 60, 50, true, idMappa);
        Nodo nodo5 = new Nodo(5, "5", 10, 80, idMappa);
        Nodo nodo6 = new Nodo(6, "6", 80, 10, idMappa);
        Nodo nodo7 = new Nodo(7, "7", 50, 80, idMappa);
        Nodo nodo8 = new Nodo(8, "8", 80, 50, true, idMappa);
        nodi.add(nodo1);
        nodi.add(nodo2);
        nodi.add(nodo3);
        nodi.add(nodo4);
        nodi.add(nodo5);
        nodi.add(nodo6);
        nodi.add(nodo7);
        nodi.add(nodo8);

        ArrayList<Arco> archi = new ArrayList<>();
        archi.add(new Arco(1, nodo1, nodo4, getPesi(1, pesi, 10)));
        archi.add(new Arco(2, nodo1, nodo2, getPesi(2, pesi, 10)));
        archi.add(new Arco(3, nodo2, nodo3, getPesi(3, pesi, 10)));
        archi.add(new Arco(4, nodo3, nodo4, getPesi(4, pesi, 10)));
        archi.add(new Arco(5, nodo3, nodo5, getPesi(5, pesi, 10)));
        archi.add(new Arco(6, nodo5, nodo4, getPesi(6, pesi, 10)));
        archi.add(new Arco(7, nodo4, nodo6, getPesi(7, pesi, 10)));
        archi.add(new Arco(8, nodo6, nodo7, getPesi(8, pesi, 10)));
        archi.add(new Arco(9, nodo7, nodo8, getPesi(9, pesi, 10)));
        archi.add(new Arco(10, nodo2, nodo8, getPesi(10, pesi, 10)));
        archi.add(new Arco(11, nodo3, nodo8, getPesi(11, pesi, 10)));


        MappaDAO.getInstance(context).insert(new Mappa(idMappa, "map145", nodi, archi));

        DB_SEEDED = true;
    }

    private static ArrayList<PesoArco> getPesi(int id, ArrayList<Peso> pesi, int seed){
        ArrayList<PesoArco> pesiArco = new ArrayList<>();
        int i = 1;
        for(Peso peso : pesi) {
            pesiArco.add(new PesoArco(id * pesi.size() + i - 1, id, peso, seed * i));
            i++;
        }
        return pesiArco;
    }

    public static String getTableAsString(Context context, String tableName) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }
}
