package com.watch.customer.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.watch.customer.model.Search;

/**
 * 任务列表的数据库操作类
 * 
 * @author 黄家强
 */
public class SearchDao {
	private DatabaseHelper dbHelper;
	private static final String TABLE_NAME = "search";

	public SearchDao(Context context) {
		super();
		dbHelper = new DatabaseHelper(context);
	}

	public boolean isExist(Search search) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "content = ?",
				new String[] { search.getContent() + "" }, null, null, null);
		if (cursor.getCount() > 0) {
			db.close();
			cursor.close();
			return true;
		}
		db.close();
		cursor.close();
		return false;
	}

	public int insert(Search search) {
		Search bef = queryByConent(search.getContent());
		if (bef != null) {
			deleteById(bef.getId());
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("content", search.getContent());
		values.put("time", search.getTime());
		int id = (int) db.insert(TABLE_NAME, null, values);
		db.close();
		return id;
	}

	public void deleteAll() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}

	public void deleteById(int id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });
		db.close();
	}

	public ArrayList<Search> queryAll() {
		ArrayList<Search> ss = new ArrayList<Search>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			ss.add(new Search(id, content, time));
		}
		cursor.close();
		db.close();
		return ss;
	}

	public Search queryByConent(String content) {
		Search mSearch = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "content=?",
				new String[] { content }, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			content = cursor.getString(cursor.getColumnIndex("content"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			mSearch = new Search(id, content, time);
		}
		cursor.close();
		db.close();
		return mSearch;
	}

	public int getcount() {
		return queryAll().size();
	}
}
