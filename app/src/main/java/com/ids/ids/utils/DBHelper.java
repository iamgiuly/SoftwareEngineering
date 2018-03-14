package com.ids.ids.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ids.ids.entity.NodoDAO;

public class DBHelper extends SQLiteOpenHelper{

    // TODO incrementare ogni volta che si modifica la struttura del db (tabelle)
    private static final int DATABASE_VERSION = 1;
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
        String CREATE_TABLE_NODO = "CREATE TABLE " + NodoDAO.TABLE + "(" +
                NodoDAO.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NodoDAO.KEY_beaconId + " TEXT, " +
                NodoDAO.KEY_x + " INTEGER, " +
                NodoDAO.KEY_y + " INTEGER, " +
                NodoDAO.KEY_tipo + " INTEGER)";

        // TODO seeding (per testing)

        db.execSQL(CREATE_TABLE_NODO);
    }

    /**
     * Qui vengono distrutte le tabelle e ricreato il database
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NodoDAO.TABLE);
        onCreate(db);
    }

}