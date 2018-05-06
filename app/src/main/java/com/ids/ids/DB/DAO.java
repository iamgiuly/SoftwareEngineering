package com.ids.ids.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public abstract class DAO<Table> {

    protected DBHelper dbHelper;

    public DAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public Table find(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT *" +
                " FROM " + this.getTable() +
                " WHERE " + this.getIdColumn() + " = ?";   // "?" è un parametro che verrà inserito alla chiamata di db.rawQuery()

        Cursor cursor = db.rawQuery(selectQuery,  new String[] { String.valueOf(id) } );
        cursor.moveToFirst();
        System.out.println(cursor.toString());

        Table elem = this.getFromCursor(cursor);

        cursor.close();
        db.close();
        return elem;
    }

    public ArrayList<Table> findAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + this.getTable();

        ArrayList<Table> elementi = new ArrayList();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                elementi.add(this.getFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return elementi;
    }

    public ArrayList<Table> findAllByColumnValue(String column, String value){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + this.getTable() + " WHERE " + column + " = " + value;

        ArrayList<Table> elementi = new ArrayList();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                elementi.add(this.getFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return elementi;
    }

    public int insert(Table table){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        this.putValues(table, values);
        long id = db.insert(this.getTable(), null, values);
        System.out.println("inserimento"+this.getTable()+" con valori "+values);
        db.close();
        this.cascadeInsert(table);
        return (int) id;
    }

    public void update(Table table){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        this.putValues(table, values);
        db.update(this.getTable(), values, this.getIdColumn() + " = ?", new String[] { String.valueOf(this.getId(table)) });
        this.cascadeUpdate(table);
        db.close();
    }

    public void delete(int id){
        this.cascadeDelete(this.find(id));
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(this.getTable(), this.getIdColumn() + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    public void clear(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(this.getTable(), "", null);
        db.close();
    }

    protected abstract String getTable();
    protected abstract String getIdColumn();
    protected abstract int getId(Table table);

    protected abstract Table getFromCursor(Cursor cursor);
    protected abstract void putValues(Table table, ContentValues values);
    protected abstract void cascadeInsert(Table table);
    protected abstract void cascadeUpdate(Table table);
    protected abstract void cascadeDelete(Table table);

}
