package com.ids.ids.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.Key;
import java.util.ArrayList;

import com.ids.ids.DB.DBHelper;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.PesoArco;

public class ArcoDAO extends DAO<Arco> {

    public static final String TABLE = "Arco";

    public static final String KEY_ID = "id";
    public static final String KEY_nodoPartenzaId = "nodoPartenzaId";
    public static final String KEY_nodoArrivoId = "nodoArrivoId";
    public static final String KEY_mappaId = "mappaId";

    private static ArcoDAO instance = null;

    private NodoDAO nodoDAO;
    private PesoArcoDAO pesoArcoDAO;

    private ArcoDAO(Context context) {
        super(context);
        this.nodoDAO = NodoDAO.getInstance(context);
        this.pesoArcoDAO = PesoArcoDAO.getInstance(context);
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
        return arco.getId();
    }

    @Override
    protected Arco getFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        Nodo nodoPartenza = nodoDAO.find(cursor.getInt(cursor.getColumnIndex(KEY_nodoPartenzaId)));
        Nodo nodoArrivo = nodoDAO.find(cursor.getInt(cursor.getColumnIndex(KEY_nodoArrivoId)));

        ArrayList<PesoArco> pesi = pesoArcoDAO.findAllByColumnValue(PesoArcoDAO.KEY_idArco, String.valueOf(id));
      /*  for(PesoArco peso : pesi)
            Log.i("Peso", ""+peso.getPeso().getPeso());

        Arco arco = new Arco(id, nodoPartenza, nodoArrivo, pesi);
        return arco;*/

      return new Arco(id, nodoPartenza, nodoArrivo, pesi);
    }

    @Override
    protected void putValues(Arco arco, ContentValues values) {
        values.put(KEY_ID, arco.getId());
        values.put(KEY_nodoPartenzaId, arco.getNodoPartenza().getId());
        values.put(KEY_nodoArrivoId, arco.getNodoArrivo().getId());
        values.put(KEY_mappaId, arco.getMappaId());
    }

    @Override
    protected void cascadeInsert(Arco arco) {
        for(PesoArco peso : arco.getPesi())
            pesoArcoDAO.insert(peso);
    }

    @Override
    protected void cascadeUpdate(Arco arco) {
        for(PesoArco peso: arco.getPesi())
            pesoArcoDAO.update(peso);
    }

    @Override
    protected void cascadeDelete(Arco arco) {
        for(PesoArco peso: arco.getPesi())
            pesoArcoDAO.delete(peso.getId());
    }

    public static ArcoDAO getInstance(Context context){
        if(instance == null)
            instance = new ArcoDAO(context);
        return instance;
    }
}
