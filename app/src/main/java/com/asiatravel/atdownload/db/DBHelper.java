package com.asiatravel.atdownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jsion on 16/8/11.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "download.db";
    private static final int DB_VERSION = 1;
    private static final String SQL_CREAT = "CREATE TABLE thread_info(_id integer primary key autoincrement," +
            "thread_id integer," +
            "url text," +
            "start integer ," +
            "end integer ," +
            "finished integer" + ")";
    private static final String SQL_DROP = "DROP TABLE IF EXISTES " +
            "thread_info";

    private static DBHelper dbHelper;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP);
        sqLiteDatabase.execSQL(SQL_CREAT);
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }
}
