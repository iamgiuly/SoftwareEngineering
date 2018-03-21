package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PesoDAO extends DAO<Peso> {

    public static final String TABLE = "Nodo";

    public static final String KEY_descrizione = "descrizione";
    public static final String KEY_peso = "peso";

    private static PesoDAO instance = null;

    public PesoDAO(Context context) {
        super(context);
    }

    @Override
    protected String getTable() {
        return TABLE;
    }

    @Override
    protected String getIdColumn() {
        return KEY_descrizione;
    }

    @Override
    protected int getId(Peso peso) {
        return peso.getDescrizione().hashCode();
    }           //TODO ??

    @Override
    protected Peso getFromCursor(Cursor cursor) {
        Peso peso = new Peso(   cursor.getString(cursor.getColumnIndex(KEY_descrizione)),
                                cursor.getInt(cursor.getColumnIndex(KEY_peso)));
        return peso;
    }

    @Override
    protected void putValues(Peso peso, ContentValues values) {
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