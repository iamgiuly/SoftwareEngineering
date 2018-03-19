package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.Key;
import java.util.ArrayList;

import com.ids.ids.utils.DBHelper;

//TODO insertInMappa, idMappa
public class ArcoDAO extends DAO<Arco> {

    public static final String TABLE = "Arco";

    public static final String KEY_ID = "id";
    public static final String KEY_nodoPartenzaId = "nodoPartenzaId";
    public static final String KEY_nodoArrivoId = "nodoArrivoId";

    private static ArcoDAO instance = null;

    private NodoDAO nodoDAO;

    public ArcoDAO(Context context) {
        super(context);
        this.nodoDAO = NodoDAO.getInstance(context);
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
    protected int getId(Arco arco) {
        return 0;           //TODO
    }

    @Override
    protected Arco getFromCursor(Cursor cursor) {
        Nodo nodoPartenza = nodoDAO.find(cursor.getInt(cursor.getColumnIndex(KEY_nodoPartenzaId)));
        Nodo nodoArrivo = nodoDAO.find(cursor.getInt(cursor.getColumnIndex(KEY_nodoArrivoId)));
        Arco arco = new Arco(nodoPartenza, nodoArrivo, null);       //TODO
        return arco;
    }

    @Override
    protected void putValues(Arco arco, ContentValues values) {
        values.put(KEY_nodoPartenzaId, arco.getNodoPartenza().getId());
        values.put(KEY_nodoArrivoId, arco.getNodoArrivo().getId());
    }

    @Override
    protected void cascadeInsert(Arco arco) {
        //TODO inserire pesi
    }

    @Override
    protected void cascadeUpdate(Arco arco) {
        //TODO aggiornare pesi
    }

    @Override
    protected void cascadeDelete(Arco arco) {
        //TODO eliminare pesi
    }

    public ArrayList<Arco> findByMappa(int idMappa){
        //TODO
        return null;
    }

    public static ArcoDAO getInstance(Context context){
        if(instance == null)
            instance = new ArcoDAO(context);
        return instance;
    }
}
