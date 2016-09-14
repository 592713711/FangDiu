package com.watch.customer.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.ShopListAdapter;
import com.watch.customer.dao.SearchDao;
import com.watch.customer.dao.TypeDao;
import com.watch.customer.model.Search;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.ThreadPoolManager;

public class ShopSearchActivity extends BaseActivity implements OnClickListener {
	private EditText searchEdit;
	private ImageButton seachbtn;
	private ListView recordList;
	private LinearLayout cleanLin;
	private ArrayList<Search> searchlist = new ArrayList<Search>();
	private SearchDao sdao;
	private TypeDao mTypeDao;
	private String editstr = "";
	private Runnable searchRun;
	private RecordAdapter mAdapter;
	private ShopListAdapter mShopAdapter;
	private ArrayList<Shop> shopArr = new ArrayList<Shop>();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			cleanLin.setVisibility(View.GONE);
			closeLoadingDialog();
			String result = msg.obj.toString();
			Log.i("hjq", "result=" + result);
			if (result.trim().equals("null")) {
				searchlist.clear();
				mShopAdapter = new ShopListAdapter(ShopSearchActivity.this,
						shopArr);
				recordList.setAdapter(mShopAdapter);
				showShortToast("没有找到相关店铺");
				return;
			}
			try {
				JSONArray jsonarr = new JSONArray(result);
				Log.i("hjq", "shopArr=" + shopArr.size());
				for (int i = 0; i < jsonarr.length(); i++) {
					JSONObject jsonobj = (JSONObject) jsonarr.get(i);
					String id = jsonobj.getString(JsonUtil.ID);
					String name = jsonobj.getString(JsonUtil.NAME);
					String type_name = mTypeDao.queryById(
							jsonobj.getString(JsonUtil.TYPE_ID)).getName();
					String city = jsonobj.getString(JsonUtil.CITY);
					String phone = jsonobj.getString(JsonUtil.PHONE);
					String average_buy = jsonobj
							.getString(JsonUtil.AVERAGE_BUY);
					String start_hours = jsonobj
							.getString(JsonUtil.START_HOURS);
					String end_hours = jsonobj.getString(JsonUtil.END_HOURS);
					String routes = jsonobj.getString(JsonUtil.ROUTES);
					String address = jsonobj.getString(JsonUtil.ADDRESS);
					String is_rooms = jsonobj.getString(JsonUtil.IS_ROOMS);
					String lon = jsonobj.getString(JsonUtil.LONG);
					String lat = jsonobj.getString(JsonUtil.LAT);
					String license ="";
					String permit = jsonobj.getString(JsonUtil.PERMIT);
					String short_message = jsonobj
							.getString(JsonUtil.SHORT_MESSAGE);
					String short_message_remark = jsonobj
							.getString(JsonUtil.SHORT_MESSAGE_REMARK);
					String bank_name = jsonobj.getString(JsonUtil.BANK_NAME);
					String bank_number = jsonobj
							.getString(JsonUtil.BANK_NUMBER);
					String bane_username = jsonobj
							.getString(JsonUtil.BANE_USERNAME);
					String zhifubao = jsonobj.getString(JsonUtil.ZHIFUBAO);
					String discount = jsonobj.getString(JsonUtil.DISCOUNT);
					String create_time = jsonobj
							.getString(JsonUtil.CREATE_TIME);
					String image = jsonobj.getString(JsonUtil.IMAGE);
					String image_thumb = jsonobj
							.getString(JsonUtil.IMAGE_THUMB);
					String is_schedule = jsonobj
							.getString(JsonUtil.IS_SCHEDULE);
					String is_point = jsonobj.getString(JsonUtil.IS_POINT);
					String is_group = jsonobj.getString(JsonUtil.IS_GROUP);
					String is_card = jsonobj.getString(JsonUtil.IS_CARD);
					String is_pay = jsonobj.getString(JsonUtil.IS_PAY);
					String intro = jsonobj.getString(JsonUtil.INTRO);
					String username = jsonobj.getString(JsonUtil.USERNAME);
					String password = jsonobj.getString(JsonUtil.PASSWORD);
					String temp_distance = jsonobj.getString(JsonUtil.TEMP_DISTANCE);
					shopArr.add(new Shop(id, name, type_name, city, phone,
							average_buy, start_hours, end_hours, routes,
							address, is_rooms, lon, lat, license, permit,
							short_message, short_message_remark, bank_name,
							bank_number, bane_username, zhifubao, discount,
							create_time, image, image_thumb, is_schedule,
							is_point, is_group, is_card, is_pay, intro,
							username, password,temp_distance));
					Log.i("hjq", "shopArr=" + shopArr.get(i).toString());
				}
				mShopAdapter = new ShopListAdapter(ShopSearchActivity.this,
						shopArr);
				recordList.setAdapter(mShopAdapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_search);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		searchEdit = (EditText) findViewById(R.id.search_edit);
		seachbtn = (ImageButton) findViewById(R.id.seachbtn);
		recordList = (ListView) findViewById(R.id.search_record_list);
		cleanLin = (LinearLayout) findViewById(R.id.cleanrecord);
		seachbtn.setOnClickListener(this);
		cleanLin.setOnClickListener(this);
		sdao = new SearchDao(this);
		mTypeDao = new TypeDao(this);
		searchlist = sdao.queryAll();
		mAdapter = new RecordAdapter(this);
		recordList.setAdapter(mAdapter);
		recordList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (cleanLin.getVisibility() == View.VISIBLE) {
					searchEdit.setText(searchlist.get(position).getContent());
					onSearch();
				} else {
					Intent mIntent=new Intent(ShopSearchActivity.this,
							ShopDetailActivity.class);
					mIntent.putExtra("object", shopArr.get(position));
					startActivity(mIntent);
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.seachbtn:
			onSearch();
			break;
		case R.id.cleanrecord:
			searchlist.clear();
			sdao.deleteAll();
			mAdapter.notifyDataSetChanged();
			break;
		}

	}

	private void onSearch() {
		editstr = searchEdit.getText().toString().trim();
		if (editstr.equals("") || editstr.length() == 0) {
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-HH");
		Date curDate = new Date(System.currentTimeMillis());
		String timestr = formatter.format(curDate);
		sdao.insert(new Search(0, editstr, timestr));
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_FINDSTOREBYNAME,
							new BasicNameValuePair(JsonUtil.NAME, editstr));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_FINDSTOREBYNAME,
						new BasicNameValuePair(JsonUtil.NAME, editstr)));
				Log.e("hjq","result="+result);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});

	}

	class RecordAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public RecordAdapter(Context context) {
			super();
			mInflater = getLayoutInflater().from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return searchlist.get(position);
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(final int position, View convertview,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder mHolder = null;
			if (convertview == null) {
				mHolder = new Holder();
				convertview = mInflater.inflate(R.layout.record_item, null);
				mHolder.tv = (TextView) convertview
						.findViewById(R.id.content_txt);
				mHolder.iv = (ImageView) convertview
						.findViewById(R.id.open_img);
				convertview.setTag(mHolder);
			} else {
				mHolder = (Holder) convertview.getTag();
			}
			mHolder.tv.setText(searchlist.get(position).getContent());
			return convertview;
		}

		class Holder {
			TextView tv;
			ImageView iv;
		}

	}
}
