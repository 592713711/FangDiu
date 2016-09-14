package com.watch.customer.ui;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class StaticPageActivity extends BaseActivity {
   private TextView titletv;
   private TextView contenttv;
   private String code="";
   private Handler mHandler=new Handler(){
	   public void handleMessage(android.os.Message msg) {
		   String result=msg.obj.toString();
		   closeLoadingDialog();
		   Log.e("hjq", result);
		try {
			JSONArray array = new JSONArray(result);
		   for (int i = 0; i < array.length(); i++) {
			JSONObject object=array.getJSONObject(i);
				titletv.setText(object.getString(JsonUtil.TITLE));
				contenttv.setText(object.getString(JsonUtil.CONTENT));

			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	   };
   };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staticpage);
        String title=getIntent().getStringExtra(JsonUtil.TITLE);
		titletv=(TextView)findViewById(R.id.title);
		titletv.setText(title);
		contenttv=(TextView)findViewById(R.id.content);
		findViewById(R.id.back).setOnClickListener(this);
		showLoadingDialog();
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_STATICPAGE,
							new BasicNameValuePair(JsonUtil.CODE, JsonUtil.PROTOCOL));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg = new Message();
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}
@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}
}
