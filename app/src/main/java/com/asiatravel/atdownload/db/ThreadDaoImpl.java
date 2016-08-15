package com.asiatravel.atdownload.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.asiatravel.atdownload.entity.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jsion on 16/8/11.
 */

public class ThreadDaoImpl implements ThreadDao {
    private static final String SQL_INSERT = "INSERT INTO thread_info" +
            "(thread_id,url,start,end,finished) " +
            "VALUES(?,?,?,?,?)";
    private static final String SQL_DELETE = "DELETE FROM thread_info " +
            "WHERE url = ? ";
    private static final String SQL_UPDATE = "UPDATE thread_info " +
            "SET finished = ? " +
            "WHERE url = ? AND thread_id = ?";
    private static final String SQL_QUERY = "SELECT * FROM " +
            "thread_info " +
            "WHERE url = ?";
    private static final String SQL_QUERY_EXISTES = "SELECT * FROM " +
            "thread_info " +
            "WHERE url = ? AND thread_id = ? ";

    private DBHelper dbHelper;
    private Context context;

    public ThreadDaoImpl(Context context) {
        this.context = context;
        dbHelper = DBHelper.getInstance(context);
    }

    @Override
    public synchronized void intsertThread(ThreadInfo threadInfo) {
        if (null == threadInfo) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(SQL_INSERT, new Object[]{threadInfo.getId(),
                threadInfo.getUrl(),
                threadInfo.getStart(),
                threadInfo.getEnd(),
                threadInfo.getFinished()});
        db.close();
    }

    @Override
    public synchronized void deleteThread(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE, new Object[]{url});
        db.close();
    }

    @Override
    public synchronized void updateTherad(String url, int thread_id, int finished) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(SQL_UPDATE, new Object[]{finished, url, thread_id});
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        List<ThreadInfo> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_QUERY, new String[]{url});
        if (null != cursor) {
            while (cursor.moveToNext()) {
                ThreadInfo threadInfo = new ThreadInfo();
                threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
                threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
                threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
                threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
                list.add(threadInfo);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    @Override
    public boolean isExistsTherad(String url, int thread_id) {
        boolean isExists = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_QUERY_EXISTES, new String[]{url, thread_id + ""});
        if (null != cursor) {
            isExists = cursor.moveToNext();
            cursor.close();
        }
        db.close();
        return isExists;
    }
}
