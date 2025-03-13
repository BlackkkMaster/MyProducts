package com.example.yomixteam_myproducts.DB_Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class PropertiesDataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DataBaseName = "PropertiesDB";
    private static final int DataBaseVersion =  1;
    private static final String tableName = "Properties", columnKey = "keyid",
            columnValue = "value";

    public PropertiesDataBaseHelper(@Nullable Context context){
        super(context,DataBaseName,null,DataBaseVersion);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + tableName + " (" +
                columnKey + " TEXT PRIMARY KEY, " +
                columnValue + " TEXT);";
        db.execSQL(query);
        ContentValues values = new ContentValues();

        values.put(columnKey, "warnForDays");
        values.put(columnValue, "3");
        db.insert(tableName, null, values);
        values.clear();

        values.put(columnKey, "timeVerify");
        values.put(columnValue, "8:00");
        db.insert(tableName, null, values);
        values.clear();

        values.put(columnKey, "removeAfter");
        values.put(columnValue, "1");
        db.insert(tableName, null, values);
        values.clear();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }


    public Cursor readProperties(){
        String query = "SELECT * FROM " + tableName;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query,null);
        }
        return cursor;
    }


    public boolean editProperties(String keyId, String value){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnValue, value);
        long result = database.update(tableName, cv, "keyid=?", new String[]{keyId});
        return result != -1;
    }








}
