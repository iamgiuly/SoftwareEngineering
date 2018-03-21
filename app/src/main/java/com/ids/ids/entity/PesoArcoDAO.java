package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

//TODO completare
public class PesoArcoDAO extends DAO {

    //TODO tra le chiavi inserire anche l'arco, anche se non è nel model
    
    private static PesoArcoDAO instance = null;

    public PesoArcoDAO(Context context) {
        super(context);
    }

    @Override
    protected String getTable() {
        return null;
    }

    @Override
    protected String getIdColumn() {
        return null;
    }

    @Override
    protected int getId(Object o) {
        return 0;
    }

    @Override
    protected Object getFromCursor(Cursor cursor) {
        return null;
    }

    @Override
    protected void putValues(Object o, ContentValues values) {

    }

    @Override
    protected void cascadeInsert(Object o) {

    }

    @Override
    protected void cascadeUpdate(Object o) {

    }

    @Override
    protected void cascadeDelete(Object o) {

    }

    public static PesoArcoDAO getInstance(Context context){
        if(instance == null)
            instance = new PesoArcoDAO(context);
        return instance;
    }

}
