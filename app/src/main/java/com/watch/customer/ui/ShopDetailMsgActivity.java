package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Comment;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class ShopDetailMsgActivity extends BaseActivity implements OnClickListener {
	private ImageView collect;
	private TextView name;
	private ImageView logo;
	private TextView time;
	private TextView route;
	private TextView intro;
	private Shop mShop;
	private ListView mListView;
	private boolean ishouse=false;
	private static final int addordel_what=1;
	private static final int storecomment_what=2;
	private ArrayList<Comment> commentData = new ArrayList<Comment>();
	private ArrayList<String> commentarr = new ArrayList<String>();
	 private Handler mHandler=new Handler(){
	    	public void handleMessage(Message msg) {
	    		String result=msg.obj.toString();
	    		switch (msg.what) {
				case addordel_what:
					Log.e("hjq","addordel_what="+result);
					try {
						JSONObject json=new JSONObject(result);
						if (json.getInt(JsonUtil.CODE)==1) {
							ishouse=!ishouse;
							if (ishouse) {
								showLongToast(json.getString(JsonUtil.MSG));
								collect.setImageResource(R.drawable.collect_head_select);
							}else {
								showLongToast(json.getString(JsonUtil.MSG));
								collect.setImageResource(R.drawable.collect_head);
							}
							
						}else {
							showLongToast(json.getString(JsonUtil.MSG));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case storecomment_what:
					Log.e("hjq","storecomment_what="+result);
					if (result.trim().equals("null")) {
						
					}else {
						try {
						JSONArray array = new JSONArray(result);
						for (int i = 0; i < array.length(); i++) {
							JSONObject json = array.getJSONObject(i);
							String id = json.getString(JsonUtil.ID);
							String order_id = json.getString(JsonUtil.ORDER_ID);
							String user_id = json.getString(JsonUtil.USER_ID);
							String content = json.getString(JsonUtil.CONTENT);
							String sorce = json.getString(JsonUtil.SORCE);
							String store_id = json.getString(JsonUtil.STORE_ID);
							String comment_time = json.getString(JsonUtil.COMMENT_TIME);
							String people = json.getString(JsonUtil.PEOPLE);
							String is_room = json.getString(JsonUtil.IS_ROOM);
							String order_time = json.getString(JsonUtil.ORDER_TIME);
							;
							String order_type = json.getString(JsonUtil.ORDER_TYPE);
							;
							String phone = json.getString(JsonUtil.PHONE);
							String status = json.getString(JsonUtil.STATUS);
							String total_price = json.getString(JsonUtil.TOTAL_PRICE);
							String user_name = json.getString(JsonUtil.USER_NAME);
							commentData.add(new Comment(id, order_id, user_id, content,
									sorce, store_id, comment_time, people, is_room,
									order_time, order_type, phone, status, total_price,
									user_name));
							commentarr.add(content);
						}	
						
						} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
						ArrayAdapter<String> adapter=new ArrayAdapter<String>(ShopDetailMsgActivity.this, android.R.layout.simple_list_item_1, commentarr);
						mListView.setAdapter(adapter);
					}
					
					break;
				}
	    	};
	    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detailmsg);
		mShop=(Shop)getIntent().getSerializableExtra("object");
		ishouse=getIntent().getBooleanExtra("ishouse",false);
		collect= (ImageView) findViewById(R.id.collect);
		name=(TextView)findViewById(R.id.name);
		logo=(ImageView)findViewById(R.id.logo);
		time=(TextView)findViewById(R.id.time);
		intro=(TextView)findViewById(R.id.intro);
		route=(TextView)findViewById(R.id.route);
		mListView=(ListView)findViewById(R.id.comment_list);
		collect.setOnClickListener(this);
		if (ishouse) {
			collect.setImageResource(R.drawable.collect_head_select);
		}	
		findViewById(R.id.back).setOnClickListener(this);
		name.setText(mShop.getName());
		ImageLoaderUtil.displayImage(HttpUtil.SERVER + mShop.getImage_thumb(),logo, this);
		time.setText(mShop.getStart_hours()+"-"+mShop.getEnd_hours());
		intro.setText(mShop.getIntro());
		route.setText(mShop.getRoutes());
		ThreadPoolManager.getInstance().addTask(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String store_id=mShop.getId();
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_STORECOMMENT,
							new BasicNameValuePair(JsonUtil.STORE_ID, store_id));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.obj=result;
				msg.what=storecomment_what;
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
		onBackPressed();
		break;
	case R.id.collect:
		if (PreferenceUtil.getInstance(this).getUid().equals("")) {
			startActivity(new Intent(this,AuthLoginActivity.class));
			return ;
		}
		ThreadPoolManager.getInstance().addTask(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String store_id=mShop.getId();
				String user_id=PreferenceUtil.getInstance(ShopDetailMsgActivity.this).getUid();
				Log.e("hjq", ""+ishouse);
				String type=ishouse?"delete":"add";
				Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_ADDORDELUSERHOUSE, 
				new BasicNameValuePair(JsonUtil.STORE_ID,store_id),
				new BasicNameValuePair(JsonUtil.USER_ID,user_id),
				new BasicNameValuePair(JsonUtil.TYPE,type)));
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_ADDORDELUSERHOUSE,
							new BasicNameValuePair(JsonUtil.STORE_ID, store_id),
							new BasicNameValuePair(JsonUtil.USER_ID, user_id),
							new BasicNameValuePair(JsonUtil.TYPE, type));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.obj=result;
				msg.what=addordel_what;
				mHandler.sendMessage(msg);
			}
		});
		break;
	case R.id.comment_list:

		ThreadPoolManager.getInstance().addTask(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String store_id=mShop.getId();
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_STORECOMMENT,
							new BasicNameValuePair(JsonUtil.STORE_ID, store_id));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.obj=result;
				msg.what=storecomment_what;
				mHandler.sendMessage(msg);
			}
		});
		break;
	default:
		break;
	}
}

}
