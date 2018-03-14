package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ids.ids.utils.DBHelper;

import java.util.ArrayList;

public class MappaDAO {

    public static final String TABLE = "Mappa";

    public static final String KEY_ID = "id";
    public static final String KEY_piano = "piano";
    public static final String KEY_piantina = "piantina";

    private static MappaDAO instance = null;

    private DBHelper dbHelper;

    private NodoDAO nodoDAO;
    //TODO private ArcoDAO arcoDAO;

    public MappaDAO(Context context) {
        dbHelper = new DBHelper(context);
        this.nodoDAO = NodoDAO.getInstance(context);
        //TODO this.arcoDAO = ArcoDAO.getInstance(context);
        this.seed();
    }

    //TODO solo per testing
    public void seed(){

    }

    public Mappa find(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT *" +
                " FROM " + TABLE +
                " WHERE " + KEY_ID + " = ?";   // "?" è un parametro che verrà inserito alla chiamata di db.rawQuery()

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(id) } );
        cursor.moveToFirst();

        int idMappa = cursor.getColumnIndex(KEY_ID);
        ArrayList<Nodo> nodi = nodoDAO.findByMappa(idMappa);
        ArrayList<Arco> archi = null; //TODO nodoDAO.findByMappa(idMappa);

        Mappa mappa = new Mappa(cursor.getInt(cursor.getColumnIndex(KEY_piano)),
                                cursor.getInt(cursor.getColumnIndex(KEY_piantina)),
                                nodi, archi);

        cursor.close();
        db.close();
        return mappa;
    }

    public ArrayList<Mappa> findAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM " + TABLE;

        ArrayList<Mappa> mappe = new ArrayList();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idMappa = cursor.getColumnIndex(KEY_ID);
                ArrayList<Nodo> nodi = nodoDAO.findByMappa(idMappa);
                ArrayList<Arco> archi = null; //TODO nodoDAO.findByMappa(idMappa);

                mappe.add(new Mappa(cursor.getInt(cursor.getColumnIndex(KEY_piano)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_piantina)),
                                    nodi, archi));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return mappe;
    }

    public int insert(Mappa mappa){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_piano, mappa.getPiano());
        values.put(KEY_piantina, mappa.getPiantina());

        long mappaId = db.insert(TABLE, null, values);
        db.close();
        for(Nodo nodo : mappa.getNodi())
            nodoDAO.insert(nodo);
        //for(Arco arco : mappa.getArchi())
        //    arcoDAO.insert(arco);
        return (int) mappaId;
    }

    public void update(Mappa mappa) {
        //TODO
    }

    public void delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        // TODO delete nodi, archi
        db.close();
    }

    public static MappaDAO getInstance(Context context){
        if(instance == null)
            instance = new MappaDAO(context);
        return instance;
    }
}
