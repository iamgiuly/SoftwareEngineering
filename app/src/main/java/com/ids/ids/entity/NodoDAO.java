package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import com.ids.ids.utils.DBHelper;

//TODO insertInMappa, idMappa
public class NodoDAO {

    public static final String TABLE = "Nodo";

    public static final String KEY_ID = "id";
    public static final String KEY_beaconId = "beaconId";
    public static final String KEY_x = "x";
    public static final String KEY_y = "y";
    public static final String KEY_tipo = "tipo";

    private static NodoDAO instance = null;

    private DBHelper dbHelper;

    public NodoDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public Nodo find(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT *" +
                " FROM " + TABLE +
                " WHERE " + KEY_ID + " = ?";   // "?" è un parametro che verrà inserito alla chiamata di db.rawQuery()

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(id) } );
        cursor.moveToFirst();
        Nodo nodo = new Nodo(   cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                                cursor.getString(cursor.getColumnIndex(KEY_beaconId)),
                                cursor.getInt(cursor.getColumnIndex(KEY_x)),
                                cursor.getInt(cursor.getColumnIndex(KEY_y)),
                                cursor.getInt(cursor.getColumnIndex(KEY_tipo)));

        cursor.close();
        db.close();
        return nodo;
    }

    public ArrayList<Nodo> findByMappa(int idMappa){
        //TODO
        return null;
    }

    public ArrayList<Nodo> findAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM " + TABLE;

        ArrayList<Nodo> nodi = new ArrayList();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                nodi.add(new Nodo(  cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                                    cursor.getString(cursor.getColumnIndex(KEY_beaconId)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_x)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_y)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_tipo))));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return nodi;
    }

    public int insert(Nodo nodo){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_beaconId, nodo.getBeaconId());
        values.put(KEY_x, nodo.getX());
        values.put(KEY_y, nodo.getY());
        values.put(KEY_tipo, nodo.getTipo());

        long nodoId = db.insert(TABLE, null, values);
        db.close();
        return (int) nodoId;
    }

    public void update(Nodo nodo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_beaconId, nodo.getBeaconId());
        values.put(KEY_x, nodo.getX());
        values.put(KEY_y, nodo.getY());
        values.put(KEY_tipo, nodo.getTipo());

        db.update(TABLE, values, KEY_ID + " = ?", new String[] { String.valueOf(nodo.getId()) });
        db.close();
    }

    public void delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    //TODO se esiste
    public void clear(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE, "1=1", null);
        db.close();
    }


    public static NodoDAO getInstance(Context context){
        if(instance == null)
            instance = new NodoDAO(context);
        return instance;
    }
}
