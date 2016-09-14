package com.watch.customer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.watch.customer.model.BtDevice;
import com.watch.customer.model.LocationRecord;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 16-3-22.
 */
public class LocationDao {
    private DatabaseHelper dbHelper;
    private static final String TABLE_NAME = "loc_history";

    public LocationDao(Context context) {
        super();

        dbHelper = new DatabaseHelper(context);
    }

    public boolean isExist(LocationRecord d) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id = ?",
                new String[]{Integer.toString(d.getId())}, null, null, null);
        if (cursor.getCount() > 0) {
            db.close();
            cursor.close();
            return true;
        }
        db.close();
        cursor.close();
        return false;
    }

    public int insert(LocationRecord r) {
        LocationRecord bef = queryById(r.getId());
        if (bef != null) {
            deleteById(bef.getId());
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("long_lat", r.getLong_lat());
        values.put("location", r.getAddress());
        values.put("btaddress", r.getBtaddress());
        values.put("status", r.getStatus());
        values.put("loc_datetime", r.getDt_time().getTime());

        int id = (int) db.insert(TABLE_NAME, null, values);
        db.close();
        Log.e("hjq", "insert id = " + id);
        return id;
    }

    public ArrayList<LocationRecord> queryAll(int lostOrFound) {
        ArrayList<LocationRecord> list = new ArrayList<LocationRecord>(10);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, "status = ?", new String[]{ Integer.toString(lostOrFound) }, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String long_lat = cursor.getString(cursor.getColumnIndex("long_lat"));
            String address = cursor.getString(cursor.getColumnIndex("location"));
            String btaddress = cursor.getString(cursor.getColumnIndex("btaddress"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            long datetime = cursor.getLong(cursor.getColumnIndex("loc_datetime"));
            list.add(new LocationRecord(id, btaddress, long_lat, address, datetime, status));
        }
        cursor.close();
        db.close();

        Log.d("hjq", "list size from dao is " + list.size());

        return list;
    }

    public ArrayList<LocationRecord> queryAll() {
        ArrayList<LocationRecord> list = new ArrayList<LocationRecord>(10);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String long_lat = cursor.getString(cursor.getColumnIndex("long_lat"));
            String address = cursor.getString(cursor.getColumnIndex("location"));
            String btaddress = cursor.getString(cursor.getColumnIndex("btaddress"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            long datetime = cursor.getLong(cursor.getColumnIndex("loc_datetime"));
            list.add(new LocationRecord(id, btaddress, long_lat, address, datetime, status));
        }
        cursor.close();
        db.close();

        Log.d("hjq", "list size from dao is " + list.size());

        return list;
    }

    public void deleteById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{new Integer(id).toString()});
        db.close();
    }

    public LocationRecord queryById(int id) {
        LocationRecord r = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id=?",
                new String[]{Integer.toString(id)}, null, null, null);
        while (cursor.moveToNext()) {
            String long_lat = cursor.getString(cursor.getColumnIndex("long_lat"));
            String address = cursor.getString(cursor.getColumnIndex("location"));
            String btaddress = cursor.getString(cursor.getColumnIndex("btaddress"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            long datetime = cursor.getLong(cursor.getColumnIndex("loc_datetime"));

            r = new LocationRecord(id, btaddress, long_lat, address, datetime, status);
        }
        cursor.close();
        db.close();

        return r;
    }

    public int getcount() {
        return queryAll().size();
    }
}
