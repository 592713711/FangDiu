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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.ShopListAdapter;
import com.watch.customer.dao.TypeDao;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;
import com.watch.customer.xlistview.XListView;
import com.watch.customer.xlistview.XListView.IXListViewListener;

public class PersonShopActivity extends BaseActivity implements IXListViewListener{
	private XListView shoplist;
	private ArrayList<Shop> shoplistdata = new ArrayList<Shop>();
	private ShopListAdapter mListAdapter;
	private TypeDao mTypeDao;
	private Runnable myHouseRun;
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		String result=msg.obj.toString().trim();
    		Log.e("hjq", "result="+result);
    		try {
				if (result.equals(JsonUtil.CODE)) {
					JSONObject json=new JSONObject(result);
					showLongToast(json.getString(JsonUtil.MSG));
				}else {
					shoplistdata=getShopListData(result);
					mListAdapter=new ShopListAdapter(PersonShopActivity.this, shoplistdata);
				    shoplist.setAdapter(mListAdapter); 
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		shoplist.stopRefresh();
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_shop);
		findViewById(R.id.back).setOnClickListener(this);
		shoplist = (XListView) findViewById(R.id.personshopList);
		shoplist.setPullLoadEnable(false);
		shoplist.setPullRefreshEnable(true);
		shoplist.setXListViewListener(this);
		mTypeDao=new TypeDao(this);
	    shoplist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(PersonShopActivity.this, ShopDetailActivity.class);
				mIntent.putExtra("object", shoplistdata.get(position-1));
				startActivity(mIntent);
			}
		});
	     myHouseRun=new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String user_id=PreferenceUtil.getInstance(PersonShopActivity.this).getUid();
				Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_MYHOUSE, 
						new BasicNameValuePair(JsonUtil.USER_ID, user_id)));
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_MYHOUSE,
							new BasicNameValuePair(JsonUtil.USER_ID, user_id));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.obj=result;
				mHandler.sendMessage(msg);
			}
		};
		
	} 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ThreadPoolManager.getInstance().addTask(myHouseRun);
	}

	private ArrayList<Shop> getShopListData(String result) throws JSONException {
		ArrayList<Shop> shops = new ArrayList<Shop>();
		Log.e("hjq", result);
		JSONArray jsonarr = new JSONArray(result);
		Log.e("hjq", "getShopListData="+jsonarr.length());
		for (int i = 0; i < jsonarr.length(); i++) {
			JSONObject jsonobj = (JSONObject) jsonarr.get(i);
			String id = jsonobj.getString(JsonUtil.STORE_ID);
			String name = jsonobj.getString(JsonUtil.NAME);
			String type_name = mTypeDao.queryById(
					jsonobj.getString(JsonUtil.TYPE_ID)).getName();
			String city =jsonobj.getString(JsonUtil.CITY);
			String phone = jsonobj.getString(JsonUtil.PHONE);
			String average_buy = "";
			String start_hours = jsonobj.getString(JsonUtil.START_HOURS);
			String end_hours = jsonobj.getString(JsonUtil.END_HOURS);
			String routes = jsonobj.getString(JsonUtil.ROUTES);
			String address = jsonobj.getString(JsonUtil.ADDRESS);
			String is_rooms = jsonobj.getString(JsonUtil.IS_ROOMS);
			String lon = jsonobj.getString("lon");
			String lat = jsonobj.getString(JsonUtil.LAT);
			String license = "";
//			String license = jsonobj.getString(JsonUtil.LICENSE);
			String permit = jsonobj.getString(JsonUtil.PERMIT);
			String short_message = jsonobj.getString(JsonUtil.SHORT_MESSAGE);
			String short_message_remark =jsonobj.getString(JsonUtil.SHORT_MESSAGE_REMARK);
			String bank_name =jsonobj.getString(JsonUtil.NAME);
			String bank_number =jsonobj.getString(JsonUtil.BANK_NUMBER);
			String bane_username = jsonobj.getString(JsonUtil.BANE_USERNAME);
			String zhifubao = jsonobj.getString(JsonUtil.ZHIFUBAO);
			String discount = jsonobj.getString(JsonUtil.DISCOUNT);
			String create_time = jsonobj.getString(JsonUtil.CREATE_TIME);
			String image = jsonobj.getString(JsonUtil.IMAGE);
			String image_thumb = jsonobj.getString(JsonUtil.IMAGE_THUMB);
			String is_schedule = jsonobj.getString(JsonUtil.IS_SCHEDULE);
			String is_point = jsonobj.getString(JsonUtil.IS_POINT);
			String is_group =jsonobj.getString(JsonUtil.IS_GROUP);
			String is_card = jsonobj.getString(JsonUtil.IS_CARD);
			String is_pay = jsonobj.getString(JsonUtil.IS_PAY);
			String intro = jsonobj.getString(JsonUtil.INTRO);
			String username = jsonobj.getString(JsonUtil.USERNAME);
			String password = jsonobj.getString(JsonUtil.PASSWORD);
			String temp_distance = "";
			shops.add(new Shop(id, name, type_name, city, phone, average_buy,
					start_hours, end_hours, routes, address, is_rooms, lon,
					lat, license, permit, short_message, short_message_remark,
					bank_name, bank_number, bane_username, zhifubao, discount,
					create_time, image, image_thumb, is_schedule, is_point,
					is_group, is_card, is_pay, intro, username, password,temp_distance));
		}
		return shops;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;

		default:
			break;
		}
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		ThreadPoolManager.getInstance().addTask(myHouseRun);
	}
}
