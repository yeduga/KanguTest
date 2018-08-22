package com.virtualtec.kangutest.SqlLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.virtualtec.kangutest.Datas.DataProducts;

import java.util.ArrayList;

/**
 * Created by Android on 9/03/18.
 */

public class DataBaseManager {

    public static final String TABLE_NAME = "products";
    public static final String CN_ID = "id";
    public static final String CN_ID_PRODUCT = "id_product";
    public static final String CN_REFERENCE = "reference";
    public static final String CN_NAME = "name";
    public static final String CN_DESCR = "descr";
    public static final String CN_PRICE = "price";
    public static final String CN_UNITY = "unity";
    public static final String CN_PERCENTAGE = "percentage";
    public static final String CN_QTY = "qty";
    public static final String CN_IMAGE = "image";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + CN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CN_ID_PRODUCT + " TEXT NOT NULL,"
            + CN_REFERENCE + " TEXT NOT NULL,"
            + CN_NAME + " TEXT NOT NULL,"
            + CN_DESCR + " TEXT NOT NULL,"
            + CN_PRICE + " TEXT NOT NULL,"
            + CN_UNITY + " TEXT NOT NULL,"
            + CN_PERCENTAGE + " TEXT NOT NULL,"
            + CN_QTY + " TEXT NOT NULL,"
            + CN_IMAGE + " TEXT NOT NULL)";

    private DbHelper helper;
    private SQLiteDatabase db;

    public DataBaseManager(Context context) {
        helper = new DbHelper(context);
    }
    private void openReadableDB() {
        db = helper.getReadableDatabase();
    }
    private void openWriteableDB() {
        db = helper.getWritableDatabase();
    }
    private void closeDB() {
        if(db!=null){
            db.close();
        }
    }

    public ContentValues generateContentValues(String id_product, String reference, String name, String descr, String price, String unity, String percentage_iva, String qty, String image){
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_PRODUCT, id_product);
        valores.put(CN_REFERENCE, reference);
        valores.put(CN_NAME, name);
        valores.put(CN_DESCR, descr);
        valores.put(CN_PRICE, price);
        valores.put(CN_UNITY, unity);
        valores.put(CN_PERCENTAGE, percentage_iva);
        valores.put(CN_QTY, qty);
        valores.put(CN_IMAGE, image);
        return  valores;
    }

    public String[] selectProductxID(String id_product, String qty){
        this.openReadableDB();
        String where = CN_ID_PRODUCT + "= ?";
        String[] whereArgs = {id_product};
        Cursor cursor = db.query(TABLE_NAME, null, where, whereArgs, null, null, null);

        String[] mStrings = new String[2];
        mStrings[0] = "0"; // false
        mStrings[1] = "0";
        if (cursor.moveToFirst()) {
            do {
                mStrings[0] = "1"; // true
                mStrings[1] = cursor.getString(8); // Position column qty
            } while (cursor.moveToNext());
        }
        this.closeDB();
        return mStrings;
    }

    public String selectProductxIDQty(String id_product){
        this.openReadableDB();
        String where = CN_ID_PRODUCT + "= ?";
        String[] whereArgs = {id_product};
        Cursor cursor = db.query(TABLE_NAME, null, where, whereArgs, null, null, null);

        String qty = "0";
        if (cursor.moveToFirst()) {
            do {
                qty = cursor.getString(8); // Position column qty
            } while (cursor.moveToNext());
        }
        this.closeDB();
        return qty;
    }

    public void insertProduct(String id_product, String reference, String name, String descr, String price, String unity, String percentage_iva, String qty, String image){
        this.openWriteableDB();
        db.insert(TABLE_NAME, null, generateContentValues(id_product, reference, name, descr, price, unity, percentage_iva, qty, image));
        this.closeDB();
    }

    public void updateProduct(String id_product, String reference, String name, String descr, String price, String unity, String percentage_iva, String qty, String image) {
        this.openWriteableDB();
        String where = CN_ID_PRODUCT + "= ?";
        db.update(TABLE_NAME, generateContentValues(id_product, reference, name, descr, price, unity, percentage_iva, qty, image), where, new String[]{String.valueOf(id_product)});
        this.closeDB();
    }

    public void deleteProduct(String id_product) {
        this.openWriteableDB();
        String where = CN_ID_PRODUCT + "= ?";
        db.delete(TABLE_NAME, where, new String[]{String.valueOf(id_product)});
        this.closeDB();
    }

    public void deleteAll(){
        db.delete(TABLE_NAME,null,null);
    }

    public Cursor cursorKangu(){
        this.openWriteableDB();
        String [] columnas = new String[]{CN_ID, CN_ID_PRODUCT, CN_REFERENCE, CN_NAME, CN_DESCR, CN_PRICE, CN_UNITY, CN_PERCENTAGE, CN_QTY, CN_IMAGE};
        return db.query(TABLE_NAME,columnas,null,null,null,null,null);
    }

}
