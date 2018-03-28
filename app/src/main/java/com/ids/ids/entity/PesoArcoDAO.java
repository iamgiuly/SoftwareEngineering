package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class PesoArcoDAO extends DAO<PesoArco> {

    public static final String TABLE = "PesoArco";

    private static PesoArcoDAO instance = null;

    public static final String KEY_ID = "id";
    public static final String KEY_idPeso = "idPeso";
    public static final String KEY_idArco = "idArco";
    public static final String KEY_valore = "valore";

    private PesoDAO pesoDAO;

    public PesoArcoDAO(Context context) {
        super(context);
        this.pesoDAO = PesoDAO.getInstance(context);
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
    protected int getId(PesoArco pesoArco) {
        return pesoArco.getId();
    }

    @Override
    protected PesoArco getFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        int idArco = cursor.getInt(cursor.getColumnIndex(KEY_idArco));
        int idPeso = cursor.getInt(cursor.getColumnIndex(KEY_idPeso));
        int valore = cursor.getInt(cursor.getColumnIndex(KEY_valore));
        return new PesoArco(id, idArco, pesoDAO.find(idPeso), valore);
    }

    @Override
    protected void putValues(PesoArco pesoArco, ContentValues values) {
        values.put(KEY_ID, pesoArco.getId());
        values.put(KEY_idArco, pesoArco.getIdArco());
        values.put(KEY_idPeso, pesoArco.getPeso().getId());
        values.put(KEY_valore, pesoArco.getValore());
    }

    @Override
    protected void cascadeInsert(PesoArco pesoArco) {

    }

    @Override
    protected void cascadeUpdate(PesoArco pesoArco) {

    }

    @Override
    protected void cascadeDelete(PesoArco pesoArco) {

    }

    public static PesoArcoDAO getInstance(Context context){
        if(instance == null)
            instance = new PesoArcoDAO(context);
        return instance;
    }

}
