package com.watch.customer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.watch.customer.model.BtDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 16-3-11.
 */
public class BtDeviceDao {
    private DatabaseHelper dbHelper;
    private static final String TABLE_NAME = "btdevices";

    public BtDeviceDao(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
    }

    public boolean isExist(BtDevice d) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id = ?",
                new String[]{d.getId()}, null, null, null);
        if (cursor.getCount() > 0) {
            db.close();
            cursor.close();
            return true;
        }
        db.close();
        cursor.close();
        return false;
    }

    public int insert(BtDevice device) {
        BtDevice bef = queryById(device.getId());
        if (bef != null) {
            deleteById(bef.getId());
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", device.getId());
        values.put("name", device.getName());
        values.put("thumbnail", device.getThumbnail());
        values.put("anitiLostSwitch", device.isAntiLostSwitch() ? 1 : 0);
        values.put("lostAlertSwitch", device.isLostAlertSwitch() ? 1 : 0);
        values.put("alertDistance", device.getAlertDistance());
        values.put("alertVolume", device.getAlertVolume());
        values.put("alertRingtone", device.getAlertRingtone());
        values.put("alertFindSwitch", device.isFindAlertSwitch() ? 1 : 0);
        values.put("findAlertVolume", device.getFindAlertVolume());
        values.put("findAlertRingtone", device.getFindAlertRingtone());
        values.put("rssi", device.getRssi());
        int id = (int) db.insert(TABLE_NAME, null, values);
        db.close();
        Log.e("hjq", "insert id = " + id);
        return id;
    }

    public int update(BtDevice device, ContentValues cv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int index = (int) db.update(TABLE_NAME, cv, "id = ?", new String[]{device.getId()});
        db.close();
        return index;
    }

    public int update(BtDevice device) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", device.getName());
        values.put("anitiLostSwitch", device.isAntiLostSwitch() ? 1 : 0);
        values.put("lostAlertSwitch", device.isLostAlertSwitch() ? 1 : 0);
        values.put("alertDistance", device.getAlertDistance());
        values.put("thumbnail", device.getThumbnail());
        values.put("alertVolume", device.getAlertVolume());
        values.put("alertRingtone", device.getAlertRingtone());
        values.put("alertFindSwitch", device.isFindAlertSwitch() ? 1 : 0);
        values.put("findAlertVolume", device.getFindAlertVolume());
        values.put("findAlertRingtone", device.getFindAlertRingtone());
        values.put("rssi", device.getRssi());
        int index = (int) db.update(TABLE_NAME, values, "id = ?", new String[]{device.getId()});
        db.close();
        return index;
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void deleteById(String address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{address});
        db.close();
    }

    public ArrayList<BtDevice> queryAll() {
        ArrayList<BtDevice> list = new ArrayList<BtDevice>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            boolean anitiLostSwitch = cursor.getInt(cursor.getColumnIndex("anitiLostSwitch")) == 1;
            int distance = cursor.getInt(cursor.getColumnIndex("alertDistance"));
            boolean lostAlertSwitch = cursor.getInt(cursor.getColumnIndex("lostAlertSwitch")) == 1;
            int alertVolume = cursor.getInt(cursor.getColumnIndex("alertVolume"));
            int alertRingtone = cursor.getInt(cursor.getColumnIndex("alertRingtone"));
            boolean alertFindSwitch = cursor.getInt(cursor.getColumnIndex("alertFindSwitch")) == 1;
            int findAlertVolume = cursor.getInt(cursor.getColumnIndex("findAlertVolume"));
            int findAlertRingtone = cursor.getInt(cursor.getColumnIndex("findAlertRingtone"));
            String image_thumb = cursor.getString(cursor.getColumnIndex("thumbnail"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int rssi = cursor.getInt(cursor.getColumnIndex("rssi"));
            list.add(new BtDevice(image_thumb, name, id, anitiLostSwitch, lostAlertSwitch, distance, alertVolume,
                    alertRingtone, alertFindSwitch, findAlertVolume, findAlertRingtone, rssi));
        }
        cursor.close();
        db.close();
        Log.i("hjq", "list size from dao is " + list.size());
        // sort the list by name.
        Collections.sort(list, comparator);
        return list;
    }

    public BtDevice queryById(String id) {
        BtDevice device = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id=?",
                new String[]{id}, null, null, null);
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("id"));
            boolean anitiLostSwitch = cursor.getInt(cursor.getColumnIndex("anitiLostSwitch")) == 1;
            int distance = cursor.getInt(cursor.getColumnIndex("alertDistance"));
            boolean lostAlertSwitch = cursor.getInt(cursor.getColumnIndex("lostAlertSwitch")) == 1;
            int alertVolume = cursor.getInt(cursor.getColumnIndex("alertVolume"));
            int alertRingtone = cursor.getInt(cursor.getColumnIndex("alertRingtone"));
            boolean alertFindSwitch = cursor.getInt(cursor.getColumnIndex("alertFindSwitch")) == 1;
            int findAlertVolume = cursor.getInt(cursor.getColumnIndex("findAlertVolume"));
            int findAlertRingtone = cursor.getInt(cursor.getColumnIndex("findAlertRingtone"));
            String image_thumb = cursor.getString(cursor.getColumnIndex("thumbnail"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int rssi = cursor.getInt(cursor.getColumnIndex("rssi"));
            device = new BtDevice(image_thumb, name, address, anitiLostSwitch, lostAlertSwitch, distance,
                    alertVolume, alertRingtone, alertFindSwitch, findAlertVolume, findAlertRingtone, rssi);
        }
        cursor.close();
        db.close();

        return device;
    }

    public int getcount() {
        return queryAll().size();
    }

    Comparator<BtDevice> comparator = new Comparator<BtDevice>() {
        public int compare(BtDevice s1, BtDevice s2) {
            if (!s1.getName().equals(s2.getName())) {
                return s1.getName().compareTo(s2.getName());
            } else {
                return s1.getRssi() - s2.getRssi();
            }
        }
    };
}

