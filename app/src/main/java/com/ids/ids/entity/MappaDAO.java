package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class MappaDAO extends DAO<Mappa>{

    public static final String TABLE = "Mappa";

    public static final String KEY_ID = "id";
    public static final String KEY_piano = "piano";
    public static final String KEY_piantina = "piantina";

    private static MappaDAO instance = null;

    private NodoDAO nodoDAO;
    private ArcoDAO arcoDAO;

    public MappaDAO(Context context) {
        super(context);
        this.nodoDAO = NodoDAO.getInstance(context);
        this.arcoDAO = ArcoDAO.getInstance(context);
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
    protected int getId(Mappa mappa) {
        return mappa.getPiano();
    }

    @Override
    protected Mappa getFromCursor(Cursor cursor) {
        int idMappa = cursor.getColumnIndex(KEY_ID);            //TODO inutile? non è uguale a id?
        ArrayList<Nodo> nodi = nodoDAO.findAllByColumnValue(NodoDAO.KEY_mappaId, String.valueOf(idMappa));
        ArrayList<Arco> archi = arcoDAO.findAllByColumnValue(NodoDAO.KEY_mappaId, String.valueOf(idMappa));

        Mappa mappa = new Mappa(cursor.getInt(cursor.getColumnIndex(KEY_piano)),
                cursor.getInt(cursor.getColumnIndex(KEY_piantina)),
                nodi, archi);
        return mappa;
    }

    @Override
    protected void putValues(Mappa mappa, ContentValues values) {
        values.put(KEY_piano, mappa.getPiano());
        values.put(KEY_piantina, mappa.getPiantina());
    }

    @Override
    protected void cascadeInsert(Mappa mappa) {
        for(Nodo nodo : mappa.getNodi())
            nodoDAO.insert(nodo);
        for(Arco arco : mappa.getArchi())
            arcoDAO.insert(arco);
    }

    @Override
    protected void cascadeUpdate(Mappa mappa) {
        for(Nodo nodo : mappa.getNodi())
            nodoDAO.update(nodo);
        for(Arco arco : mappa.getArchi())
            arcoDAO.update(arco);
    }

    @Override
    protected void cascadeDelete(Mappa mappa) {
        for(Nodo nodo : mappa.getNodi())
            nodoDAO.delete(nodo.getId());
        //TODO for(Arco arco : mappa.getArchi())
        //TODO     arcoDAO.delete(arco.getId());
    }

    public static MappaDAO getInstance(Context context){
        if(instance == null)
            instance = new MappaDAO(context);
        return instance;
    }
}
