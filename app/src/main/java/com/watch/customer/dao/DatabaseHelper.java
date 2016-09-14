package com.watch.customer.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper是一个辅助类，用来管理数据库的创建和版本他，它提供两个方面的功能
 * 第一，getReadableDatabase()、getWritableDatabase
 * ()可以获得SQLiteDatabase对象，通过该对象可以对数据库进行操作
 * 第二，提供了onCreate()、onUpgrade()两个回调函数，允许我们再创建和升级数据库时，进行自己的操作
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 5;
    private String DROP_SEARCH = "drop table if exists search ";

    /**
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数
     *
     * @param context 上下文对象
     * @param name    数据库名称
     * @param factory
     * @param version 当前数据库的版本，值必须是整数并且是递增的状态
     */
    public DatabaseHelper(Context context) {
        super(context, "cache.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table if not exists search("
                + "id integer PRIMARY KEY AUTOINCREMENT,"
                + "content varchar(20)," + "time varchar(20))");

        db.execSQL("create table if not exists type("
                + "id varchar(10) PRIMARY KEY," + "name varchar(20))");

        db.execSQL("create table if not exists shop("
                + "id varchar(10) PRIMARY KEY," + "name varchar(30),"
                + "type_name varchar(10)," + "city varchar(10),"
                + "phone varchar(20)," + "average_buy varchar(10),"
                + "start_hours varchar(10)," + "end_hours varchar(10),"
                + "routes varchar(100)," + "address varchar(50),"
                + "is_rooms varchar(10)," + "long varchar(10),"
                + "lat varchar(10)," + "license varchar(20),"
                + "permit varchar(50)," + "short_message varchar(20),"
                + "short_message_remark varchar(20),"
                + "bank_name varchar(20)," + "bank_number varchar(20),"
                + "bane_username varchar(20)," + "zhifubao varchar(20),"
                + "discount varchar(50)," + "create_time varchar(20),"
                + "image varchar(50)," + "image_thumb varchar(50),"
                + "is_schedule varchar(5)," + "is_point varchar(5),"
                + "is_group varchar(5)," + "is_card varchar(5),"
                + "is_pay varchar(5)," + "intro varchar(100),"
                + "username varchar(20)," + "password varchar(20))");

        db.execSQL("create table if not exists user("
                + "id varchar(25) PRIMARY KEY," + "name varchar(20),"
                + "phone varchar(15)," + "sex varchar(10),"
                + "password varchar(15)," + "create_time varchar(20),"
                + "image_thumb varchar(30)," + "image varchar(30)" + ", token varchar(24))");

        db.execSQL("create table if not exists btdevices("
                + "id varchar(25) PRIMARY KEY," + "name varchar(20),"
                + "anitiLostSwitch integer," + "alertDistance integer,"
                + "lostAlertSwitch integer," + "alertVolume integer,"
                + "thumbnail varchar(30)," + "alertRingtone integer,"
                + "alertFindSwitch integer," + "findAlertVolume integer,"
                + "findAlertRingtone integer," + "rssi integer)");

        db.execSQL("create table if not exists loc_history("
                + "id integer PRIMARY KEY AUTOINCREMENT," + "long_lat varchar(20),"
                + "location varchar(100)," + "loc_datetime long,"
                + "btaddress varchar(25)," + "status integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL(DROP_SEARCH);
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists btdevices");
        db.execSQL("drop table if exists loc_history");
        onCreate(db);
    }
}
