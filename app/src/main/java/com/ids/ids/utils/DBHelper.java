package com.ids.ids.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ids.ids.entity.ArcoDAO;
import com.ids.ids.entity.MappaDAO;
import com.ids.ids.entity.NodoDAO;
import com.ids.ids.entity.PesoArcoDAO;
import com.ids.ids.entity.PesoDAO;

public class DBHelper extends SQLiteOpenHelper{

    // TODO incrementare ogni volta che si modifica la struttura del db (tabelle)
    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "ids.db";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Qui vengono create le tabelle
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MAPPA = "CREATE TABLE " + MappaDAO.TABLE + "(" +
                MappaDAO.KEY_piano + " INTEGER PRIMARY KEY, " +
                MappaDAO.KEY_piantina + " INTEGER)";

        String CREATE_TABLE_NODO = "CREATE TABLE " + NodoDAO.TABLE + "(" +
                NodoDAO.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NodoDAO.KEY_beaconId + " TEXT, " +
                NodoDAO.KEY_x + " INTEGER, " +
                NodoDAO.KEY_y + " INTEGER, " +
                NodoDAO.KEY_tipoUscita + " INTEGER, " +
                NodoDAO.KEY_tipoIncendio + " INTEGER, " +
                NodoDAO.KEY_mappaId + " INTEGER)";

        String CREATE_TABLE_ARCO = "CREATE TABLE " + ArcoDAO.TABLE + "(" +
                ArcoDAO.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArcoDAO.KEY_nodoPartenzaId + " INTEGER, " +
                ArcoDAO.KEY_nodoArrivoId + " INTEGER, " +
                ArcoDAO.KEY_mappaId + " INTEGER)";

        String CREATE_TABLE_PESO = "CREATE TABLE " + PesoDAO.TABLE + "(" +
                PesoDAO.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PesoDAO.KEY_descrizione + " STRING, " +
                PesoDAO.KEY_peso + " INTEGER)";

        String CREATE_TABLE_PESO_ARCO = "CREATE TABLE " + PesoArcoDAO.TABLE + "(" +
                PesoArcoDAO.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PesoArcoDAO.KEY_idArco + " INTEGER, " +
                PesoArcoDAO.KEY_idPeso + " INTEGER, " +
                PesoArcoDAO.KEY_valore + " INTEGER)";

        db.execSQL(CREATE_TABLE_MAPPA);
        db.execSQL(CREATE_TABLE_NODO);
        db.execSQL(CREATE_TABLE_ARCO);
        db.execSQL(CREATE_TABLE_PESO);
        db.execSQL(CREATE_TABLE_PESO_ARCO);
    }

    /**
     * Qui vengono distrutte le tabelle e ricreato il database
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MappaDAO.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NodoDAO.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ArcoDAO.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PesoDAO.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PesoArcoDAO.TABLE);
        onCreate(db);
    }

}