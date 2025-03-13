package com.example.yomixteam_myproducts.DB_Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ProductsDataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DataBaseName = "ProductsDB";
    private static final int DataBaseVersion =  1;
    private static final String tableName = "Products", columnId = "id",
        columnProductName = "productName", columnExpiredDate = "expiredDate";

    public ProductsDataBaseHelper(@Nullable Context context){
        super(context,DataBaseName,null,DataBaseVersion);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + tableName + " (" + columnId +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + columnProductName + " TEXT, " +
                columnExpiredDate + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public boolean addProduct(String name, String expiredDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnProductName, name);
        cv.put(columnExpiredDate, expiredDate);
        long resultValue = db.insert(tableName,null, cv);
        return resultValue != -1;
    }
    public Cursor readProducts(){
        String query = "SELECT * FROM " + tableName;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query,null);
        }
       return cursor;
    }
    public void deleteAllProducts(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DELETE FROM " + tableName;
        database.execSQL(query);
    }
    public boolean editProduct(String name, String expireDate, String id){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnProductName, name);
        cv.put(columnExpiredDate, expireDate);
        long result = database.update(tableName, cv, "id=?", new String[]{id});
        return result != -1;
    }
    public boolean deleteSingleProduct(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(tableName, "id=?", new String[]{id});
        return result != -1;
    }







}
