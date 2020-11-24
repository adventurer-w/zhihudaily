package com.example.zhihudaily;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MydataBaseHelper extends SQLiteOpenHelper {

    public MydataBaseHelper(Context context) {
        super(context, "database", null, 4);
        Log.i("TAG:", "使用成功！");
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("TAG:", "创建数据库表！");

        String TABLE_USER = "create table user(account_id INTEGER PRIMARY KEY autoincrement,account text" + ",cipher text,name text,picture text);";
        sqLiteDatabase.execSQL(TABLE_USER);

        String TABLE_COLLECT = "create table collect(collect_id INTEGER PRIMARY KEY autoincrement,account_id text,id text,url text,image text,title text,hint text)";
        sqLiteDatabase.execSQL(TABLE_COLLECT);
// "create table collect(collect_id INTEGER PRIMARY KEY autoincrement,news_id text,url text,image_url text,title text);";
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists user");
        onCreate(sqLiteDatabase);
    }
}
