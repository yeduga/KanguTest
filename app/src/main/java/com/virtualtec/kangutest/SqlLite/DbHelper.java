package com.virtualtec.kangutest.SqlLite;

/**
 * Created by Android on 9/03/18.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String BD_NAME = "kanguSQL.db";
    public static final int DB_SCHEME_VERSION = 1;

    public DbHelper(Context context) {
        super(context, BD_NAME, null, DB_SCHEME_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseManager.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}