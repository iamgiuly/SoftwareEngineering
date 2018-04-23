package com.ids.ids.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ids.ids.entity.Peso;

public class PesoDAO extends DAO<Peso> {

    public static final String TABLE = "Peso";

    public static final String KEY_ID = "id";
    public static final String KEY_descrizione = "descrizione";
    public static final String KEY_peso = "peso";

    private static PesoDAO instance = null;

    private PesoDAO(Context context) {
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
    protected int getId(Peso peso) {
        return peso.getId();
    }

    @Override
    protected Peso getFromCursor(Cursor cursor) {
        Peso peso = new Peso(   cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_descrizione)),
                cursor.getInt(cursor.getColumnIndex(KEY_peso)));
        return peso;
    }

    @Override
    protected void putValues(Peso peso, ContentValues values) {
        values.put(KEY_ID, peso.getId());
        values.put(KEY_descrizione, peso.getDescrizione());
        values.put(KEY_peso, peso.getPeso());
    }

    @Override
    protected void cascadeInsert(Peso peso) {

    }

    @Override
    protected void cascadeUpdate(Peso peso) {

    }

    @Override
    protected void cascadeDelete(Peso peso) {

    }

    public static PesoDAO getInstance(Context context){
        if(instance == null)
            instance = new PesoDAO(context);
        return instance;
    }

}