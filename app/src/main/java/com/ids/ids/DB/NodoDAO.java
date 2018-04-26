package com.ids.ids.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import com.ids.ids.entity.Nodo;

public class NodoDAO extends DAO<Nodo> {

    public static final String TABLE = "Nodo";

    public static final String KEY_ID = "id";
    public static final String KEY_beaconId = "beaconId";
    public static final String KEY_x = "x";
    public static final String KEY_y = "y";
    public static final String KEY_tipoUscita = "tipoUscita";
    public static final String KEY_tipoIncendio = "tipoIncendio";
    public static final String KEY_mappaId = "mappaId";

    private static NodoDAO instance = null;

    private NodoDAO(Context context) {

        super(context);
    }

    @Override
    protected String getTable() {
        return TABLE;
    }

    @Override
    protected String getIdColumn() {
        return KEY_ID;
    }

    @Override
    protected int getId(Nodo nodo) {
        return nodo.getId();
    }

    @Override
    protected Nodo getFromCursor(Cursor cursor) {
        Nodo nodo = new Nodo(   cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_beaconId)),
                cursor.getInt(cursor.getColumnIndex(KEY_x)),
                cursor.getInt(cursor.getColumnIndex(KEY_y)),
                cursor.getInt(cursor.getColumnIndex(KEY_tipoUscita)) != 0,
                cursor.getInt(cursor.getColumnIndex(KEY_tipoIncendio)) != 0,
                cursor.getInt(cursor.getColumnIndex(KEY_mappaId)));
        return nodo;
    }

    @Override
    protected void putValues(Nodo nodo, ContentValues values) {
        values.put(KEY_ID, nodo.getId());
        values.put(KEY_beaconId, nodo.getBeaconId());
        values.put(KEY_x, nodo.getX());
        values.put(KEY_y, nodo.getY());
        values.put(KEY_tipoUscita, nodo.isTipoUscita());
        values.put(KEY_tipoIncendio, nodo.isTipoIncendio());
        values.put(KEY_mappaId, nodo.getMappaId());
    }

    @Override
    protected void cascadeInsert(Nodo nodo) {
        return;
    }

    @Override
    protected void cascadeUpdate(Nodo nodo) {
        return;
    }

    @Override
    protected void cascadeDelete(Nodo nodo) {
        return;
    }

    public static NodoDAO getInstance(Context context){
        if(instance == null)
            instance = new NodoDAO(context);
        return instance;
    }
}
