package com.watch.customer.ui;

import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//import cn.jpush.android.api.JPushInterface;

import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "hjq";
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		Log.e("hjq", msg.obj.toString());
    	};
    };
	@Override
	public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " );
//
//        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//            final String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
//            //send the Registration Id to your server...
//            JPushInterface.setAlias(context,PreferenceUtil.getInstance(context).getUid(), null);
//            ThreadPoolManager.getInstance().addTask(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//				Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_GETREGIDANDUSERID,
//						new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(context).getUid()),
//						new BasicNameValuePair(JsonUtil.ALIAS, PreferenceUtil.getInstance(context).getUid()),
//						new BasicNameValuePair(JsonUtil.REG_ID, regId)));
//				String result=HttpUtil.post(HttpUtil.URL_GETREGIDANDUSERID,
//							new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(context).getUid()),
//							new BasicNameValuePair(JsonUtil.ALIAS, PreferenceUtil.getInstance(context).getUid()),
//							new BasicNameValuePair(JsonUtil.REG_ID, regId));
//				Message msg=new Message();
//				msg.obj=result;
//				mHandler.sendMessage(msg);
//				}
//			});
//        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
////        	processCustomMessage(context, bundle);
//
//        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
//            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//
//        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
//
//        	//打开自定义的Activity
//        	Intent i = new Intent(context, MainActivity.class);
//        	i.putExtras(bundle);
//        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        	context.startActivity(i);
//
//        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
//            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
//
//        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
//        } else {
//        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
//        }
//	}
//
//	// 打印所有的 intent extra 数据
//	private static String printBundle(Bundle bundle) {
//		StringBuilder sb = new StringBuilder();
//		for (String key : bundle.keySet()) {
//			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
//				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//			}
//			else {
//				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
//			}
//		}
//		return sb.toString();
//	}
//
//	//send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
	}
}
