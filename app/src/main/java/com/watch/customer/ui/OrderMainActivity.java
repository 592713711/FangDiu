package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.OrderListAdapter;
import com.watch.customer.app.MyApplication;
import com.watch.customer.model.OrderItem;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;
import com.watch.customer.xlistview.XListView;
import com.watch.customer.xlistview.XListView.IXListViewListener;

public class OrderMainActivity extends Activity implements OnClickListener,IXListViewListener  {
	private ArrayList<OrderItem> orderlistdata = new ArrayList<OrderItem>();
	private ArrayList<OrderItem> bookdata = new ArrayList<OrderItem>();
	private ArrayList<OrderItem> pointdata = new ArrayList<OrderItem>();
	private ArrayList<OrderItem> dealdata = new ArrayList<OrderItem>();
	private XListView ordermainList;
	private OrderListAdapter adapter;
	private TextView text1;
	private TextView text2;
	private TextView text3;
	private ImageView trian1;
	private ImageView trian2;
	private ImageView trian3;
	private int sectionindex = 0;
	private static final int book_what = 1;
	private static final int point_what = 2;
	private static final int deal_what = 3;
	private String uid = "";
	private Runnable bookRun, pointRun, dealRun;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = msg.obj.toString();
//			Log.e("hjq", result);
			switch (msg.what) {
			case book_what:
				try {
					bookdata = getListData(result);
					if (sectionindex == 0) {
						selectSecCheck(sectionindex);
						orderlistdata = bookdata;
						Log.e("hjq", "size=" + orderlistdata.size());
						adapter = new OrderListAdapter(OrderMainActivity.this,
								orderlistdata);
						adapter.notifyDataSetChanged();
						ordermainList.setAdapter(adapter);
						ordermainList.stopRefresh();
						MyApplication.getInstance().orderindex=0;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case point_what:
				try {
					pointdata = getListData(result);
					if (sectionindex == 1) {
						selectSecCheck(sectionindex);
						orderlistdata = pointdata;
						Log.e("hjq", "size=" + orderlistdata.size());
						adapter = new OrderListAdapter(OrderMainActivity.this,
								orderlistdata);
						adapter.notifyDataSetChanged();
						ordermainList.setAdapter(adapter);
						ordermainList.stopRefresh();
						MyApplication.getInstance().orderindex=0;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case deal_what:
				try {
					dealdata = getListData(result);
					if (sectionindex == 2) {
						selectSecCheck(sectionindex);
						orderlistdata = dealdata;
						Log.e("hjq", "size=" + orderlistdata.size());
						adapter = new OrderListAdapter(OrderMainActivity.this,
								orderlistdata);
						adapter.notifyDataSetChanged();
						ordermainList.setAdapter(adapter);
						ordermainList.stopRefresh();
						MyApplication.getInstance().orderindex=0;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
			selectSecCheck(sectionindex);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_main);
		text1 = (TextView) findViewById(R.id.section1);
		text2 = (TextView) findViewById(R.id.section2);
		text3 = (TextView) findViewById(R.id.section3);
		trian1 = (ImageView) findViewById(R.id.mark1);
		trian2 = (ImageView) findViewById(R.id.mark2);
		trian3 = (ImageView) findViewById(R.id.mark3);
		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text3.setOnClickListener(this);
		ordermainList = (XListView) findViewById(R.id.ordermainList);
		adapter = new OrderListAdapter(this, orderlistdata);
		ordermainList.setAdapter(adapter);
		ordermainList.setPullLoadEnable(false);
		ordermainList.setPullRefreshEnable(true);
		ordermainList.setXListViewListener(this);
		ordermainList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (sectionindex == 0) {
					Intent detailin;
					if (orderlistdata.get(position-1).getIs_local().equals("1")) {
						detailin = new Intent(OrderMainActivity.this,
								OrderLocalDetailActivity.class);
						detailin.putExtra(JsonUtil.ORDER,
								orderlistdata.get(position-1));
						startActivity(detailin);
					}else {
						detailin = new Intent(OrderMainActivity.this,
								OrderBookDetailActivity.class);
						detailin.putExtra(JsonUtil.ORDER,
								orderlistdata.get(position-1));
						startActivity(detailin);
					}
					MyApplication.getInstance().orderindex=0;
					
				} else if (sectionindex == 1) {
					Intent detailin = new Intent(OrderMainActivity.this,
							OrderMenuDetailActivity.class);
					detailin.putExtra(JsonUtil.ORDER,
							orderlistdata.get(position-1));
					startActivity(detailin);
					MyApplication.getInstance().orderindex=1;
				} else {
					Intent dealin = new Intent(OrderMainActivity.this,
							OrderDealDetailActivity.class);
					dealin.putExtra(JsonUtil.ORDER, orderlistdata.get(position-1));
					startActivity(dealin);
					MyApplication.getInstance().orderindex=2;
				}
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("hjq", "orderindex="+MyApplication.getInstance().orderindex);
	sectionindex = MyApplication.getInstance().orderindex;
	selectSecCheck(sectionindex);
	initRun();
	
	}

	protected ArrayList<OrderItem> getListData(String jsonstr)
			throws JSONException {
		// TODO Auto-generated method stub
		ArrayList<OrderItem> orders = new ArrayList<OrderItem>();
		if (jsonstr.trim().equals("null")) {
			Log.e("hjq", "jsonstr is null");
			return orders;
		}
		JSONArray arr = new JSONArray(jsonstr);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject object = arr.getJSONObject(i);
			String order_id = object.getString(JsonUtil.ORDER_ID);
			String storeName = object.getString(JsonUtil.STORENAME);
			String type = object.getString(JsonUtil.TYPE);
			String user_id = object.getString(JsonUtil.USER_ID);
			String create_time = object.getString(JsonUtil.CREATE_TIME);
			String order_time = object.getString(JsonUtil.ORDER_TIME);
			String status = object.getString(JsonUtil.STATUS);
			String store_id = object.getString(JsonUtil.STORE_ID);
			String group_id = object.getString(JsonUtil.GROUP_ID);
			String group_count = object.getString(JsonUtil.GROUP_COUNT);
			String check_group = object.getString(JsonUtil.CHECKGROUP);
			String is_local = object.getString(JsonUtil.IS_LOCAL);			
			String people = object.getString(JsonUtil.PEOPLE);
			String is_room = object.getString(JsonUtil.IS_ROOM);
			String userName = object.getString("userName");
			String phone = object.getString(JsonUtil.PHONE);
			String add_food = object.getString("add_food");
			orders.add(new OrderItem(order_id, type, user_id, create_time, order_time,
					status, store_id, group_id, group_count, check_group, storeName, 
					is_local, people, is_room, userName,phone,add_food));
		}
		return orders;
	}

	private void initRun() {
		// TODO Auto-generated method stub
		uid = PreferenceUtil.getInstance(OrderMainActivity.this).getUid();
		bookRun = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_ORDERLIST,
							new BasicNameValuePair(JsonUtil.USER_ID, uid),
							new BasicNameValuePair(JsonUtil.TYPE, "schedule"));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg = new Message();
				msg.what = book_what;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		pointRun = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_ORDERLIST,
							new BasicNameValuePair(JsonUtil.USER_ID, uid),
							new BasicNameValuePair(JsonUtil.TYPE, "point"));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg = new Message();
				msg.what = point_what;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		dealRun = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_ORDERLIST,
							new BasicNameValuePair(JsonUtil.USER_ID, uid),
							new BasicNameValuePair(JsonUtil.TYPE, "group"));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg = new Message();
				msg.what = deal_what;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		ThreadPoolManager.getInstance().addTask(bookRun);
		ThreadPoolManager.getInstance().addTask(pointRun);
		ThreadPoolManager.getInstance().addTask(dealRun);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.section1:
			sectionindex = 0;
			selectSecCheck(sectionindex);
			orderlistdata = bookdata;
			adapter = new OrderListAdapter(this, orderlistdata);
			adapter.notifyDataSetChanged();
			ordermainList.setAdapter(adapter);
			break;
		case R.id.section2:
			sectionindex = 1;
			selectSecCheck(sectionindex);
			orderlistdata = pointdata;
			adapter = new OrderListAdapter(this, orderlistdata);
			adapter.notifyDataSetChanged();
			ordermainList.setAdapter(adapter);
			break;
		case R.id.section3:
			sectionindex = 2;
			selectSecCheck(sectionindex);
			orderlistdata = dealdata;
			adapter = new OrderListAdapter(this, orderlistdata);
			adapter.notifyDataSetChanged();
			ordermainList.setAdapter(adapter);
			break;

		default:
			break;
		}
	}

	protected void selectSecCheck(int index) {
		// TODO Auto-generated method stub
		text1.setTextColor(0xff696969);
		text2.setTextColor(0xff696969);
		text3.setTextColor(0xff696969);
		trian1.setImageResource(R.drawable.section_bg_normal);
		trian2.setImageResource(R.drawable.section_bg_normal);
		trian3.setImageResource(R.drawable.section_bg_normal);
		switch (index) {
		case 0:
			text1.setTextColor(0xff14a19c);
			trian1.setImageResource(R.drawable.section_bg_selected);
			break;
		case 1:
			text2.setTextColor(0xff14a19c);
			trian2.setImageResource(R.drawable.section_bg_selected);
			break;
		case 2:
			text3.setTextColor(0xff14a19c);
			trian3.setImageResource(R.drawable.section_bg_selected);
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
		initRun();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("hjq", "onStop");
		
	}
}
