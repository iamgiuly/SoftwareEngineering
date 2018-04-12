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
    public static boolean SCAN_BLUETOOTH = true;
    public static boolean CHECK_WIFI = false;

    public static int PIANO_DEFAULT = 145;
    public static int PIANO_DRAWABLE_DEFAULT = R.drawable.map145;

    private static boolean DB_SEEDED = false;



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
