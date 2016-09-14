package com.watch.customer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * 记录用户名，密码之类的首选项
 * 
 */
public class PreferenceUtil {
	private static PreferenceUtil preference = null;
	private SharedPreferences sharedPreference;
	private String packageName = "";
	public static final String USERNAME = "username"; // 登录名
	public static final String PASSWORD = "password"; // 密码
	public static final String REMINDWORD = "remindword"; // 是否保留密码
	public static final String AUTOLOGIN = "autologin";
	public static final String TIMES = "times";
	public static final String LOCATION = "location";
	public static final String UID = "uid";
	public static final String CITY = "city";
	public static final String CITYID = "cityid";
	public static final String LON = "lon";
	public static final String LAT = "lat";
	public static final String SHIBI = "shibi";
	public static final String PHONE = "phone";
	public static final String TOKEN = "token";

	public static synchronized PreferenceUtil getInstance(Context context) {
		if (preference == null)
			preference = new PreferenceUtil(context);
		return preference;
	}

	public PreferenceUtil(Context context) {
		packageName = context.getPackageName() + "_preferences";
		sharedPreference = context.getSharedPreferences(packageName,
				Context.MODE_PRIVATE);
	}

	public String getUid() {
		String value = sharedPreference.getString(UID, "");
		return value;
	}
	public void setUid(String value) {
		Editor edit = sharedPreference.edit();
		edit.putString(UID, value);
		edit.commit();
	}

	public String getToken() {
		String value = sharedPreference.getString(TOKEN, "");
		return value;
	}
	public void setToken(String value) {
		Editor edit = sharedPreference.edit();
		edit.putString(TOKEN, value);
		edit.commit();
	}

	public String getString(String name,String defValue) {
		String value = sharedPreference.getString(name, defValue);
		return value;
	}
	public void setString(String name,String value) {
		Editor edit = sharedPreference.edit();
		edit.putString(name, value);
		edit.commit();
	}
	public int getInt(String name,int defValue) {
		int value = sharedPreference.getInt(name,defValue );
		return value;
	}
	public void setInt(String name,int value) {
		Editor edit = sharedPreference.edit();
		edit.putInt(name, value);
		edit.commit();
	}
	public float getFloat(String name,float defValue) {
		float value = sharedPreference.getFloat(name, defValue);
		return value;
	}
	public void setFloat(String name,float value) {
		Editor edit = sharedPreference.edit();
		edit.putFloat(name, value);
		edit.commit();
	}

}
