package com.watch.customer.app;

import java.util.LinkedList;

import android.app.Activity;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.watch.customer.passlock.AbstractAppLock;
import com.watch.customer.passlock.AppLockManager;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.PreferenceUtil;

public class MyApplication extends Application {

	public int time = 0;
	public int type = 1;
	public int orderindex = 0;
	public int islocation = 0;
	private LinkedList<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;
	public static String mToken = "";
	public double latitude;
	public double longitude;
	public float radius;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
		AppLockManager.getInstance().enableDefaultAppLockIfAvailable(this);
        AbstractAppLock appLock = AppLockManager.getInstance().getCurrentAppLock();
        appLock.setDisabledActivities(new String[] {"com.watch.customer.ui.FirstActivity", "com.watch.customer.ui.MainActivity"});
	}

	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		PreferenceUtil.getInstance(this).setString(PreferenceUtil.CITYID, "0");
		System.exit(0);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		ImageLoaderUtil.stopload(instance);
		super.onTerminate();
	}

}
