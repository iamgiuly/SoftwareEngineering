package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ids.ids.ui.R;
import com.ids.ids.utils.DBHelper;

import java.util.ArrayList;

public class MappaDAO extends DAO<Mappa>{

    public static final String TABLE = "Mappa";

    public static final String KEY_ID = "id";
    public static final String KEY_piano = "piano";
    public static final String KEY_piantina = "piantina";

    private static MappaDAO instance = null;

    private NodoDAO nodoDAO;
    //TODO private ArcoDAO arcoDAO;

    public MappaDAO(Context context) {
        super(context);
        this.nodoDAO = NodoDAO.getInstance(context);
        //TODO this.arcoDAO = ArcoDAO.getInstance(context);
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
        ArrayList<Nodo> nodi = nodoDAO.findByMappa(idMappa);
        ArrayList<Arco> archi = null; //TODO nodoDAO.findByMappa(idMappa);

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
        //TODO for(Arco arco : mappa.getArchi())
        //TODO     arcoDAO.insert(arco);
    }

    @Override
    protected void cascadeUpdate(Mappa mappa) {
        for(Nodo nodo : mappa.getNodi())
            nodoDAO.update(nodo);
        //TODO for(Arco arco : mappa.getArchi())
        //TODO     arcoDAO.update(arco);
    }

    @Override
    protected void cascadeDelete(Mappa mappa) {
        for(Nodo nodo : mappa.getNodi())
            nodoDAO.delete(nodo.getId());
        //TODO for(Arco arco : mappa.getArchi())
        //TODO     arcoDAO.delete(arco.getIdColumn());
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

    public static MappaDAO getInstance(Context context){
        if(instance == null)
            instance = new MappaDAO(context);
        return instance;
    }
}
