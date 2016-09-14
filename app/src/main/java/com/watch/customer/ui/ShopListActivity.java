package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import com.uacent.watchapp.R;
import com.watch.customer.adapter.SectionAdapter;
import com.watch.customer.adapter.ShopListAdapter;
import com.watch.customer.app.MyApplication;
import com.watch.customer.dao.TypeDao;
import com.watch.customer.model.Shop;
import com.watch.customer.model.Type;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;
import com.watch.customer.xlistview.XListView;
import com.watch.customer.xlistview.XListView.IXListViewListener;

public class ShopListActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener,IXListViewListener {
	private TextView text1;
	private TextView text2;
	private TextView text3;
	private ImageView trian1;
	private ImageView trian2;
	private ImageView trian3;
	private LinearLayout citybtn;
	private TextView citytext;
	private PopupWindow mPopWin;
	private LinearLayout layout;
	private ListView section_list;
	private GridView section_grid;
	private GridView area_grid;
	private ShopListAdapter mShopListAdapter;
	private AreaAdapter mAreaAdapter;
	private SectionAdapter secAdapter;
	private LinearLayout linLayout;

	private ArrayList<String> secArr1 = new ArrayList<String>();
	private ArrayList<String> secArr2 = new ArrayList<String>();
	private ArrayList<String> secArr3 = new ArrayList<String>();
	private ArrayList<String> areaArr = new ArrayList<String>();

	private int selectPos[] = new int[] { 0, 0, 0 };
	private int secindex = 0;
	private int area_selectid = 0;

	private XListView shoplist;
	private ArrayList<Shop> shoplistdata = new ArrayList<Shop>();
	private ImageView seachbtn;
	private TextView locText;
	public static final int typelist_what = 1;
	public static final int shoplist_what = 2;
	private Runnable typelistRun; // 店铺类型
	private TypeDao mTypeDao;
	private int pagerindex=0;
	public static final String REFRESH_CITY="refresh_city";
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = msg.obj.toString();
			switch (msg.what) {
			case typelist_what:
				try {
					Log.i("hjq", "typelist_what="+result);
					JSONArray jsonarr = new JSONArray(result);
					mTypeDao.deleteAll();
					for (int i = 0; i < jsonarr.length(); i++) {
						JSONObject ob = jsonarr.getJSONObject(i);
						secArr2.add(ob.getString(JsonUtil.NAME));
						Log.i("hjq", new Type(ob.getString(JsonUtil.ID),
								secArr2.get(i)).toString());
						mTypeDao.insert(new Type(ob.getString(JsonUtil.ID),
								secArr2.get(i)));
					}
					secArr2.add(0,"默认");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case shoplist_what:
				Log.i("hjq", "shoplist_what="+result);
				try {
					if (pagerindex>0) {
						if (result.trim().equals("null")) {
							Log.i("hjq", "result==null");
							showShortToast("没有数据了");
							shoplist.stopLoadMore();
							shoplist.hideFootView();
							return ;
						}
						shoplistdata.addAll(getShopListData(result));
						mShopListAdapter.notifyDataSetChanged();
						return ;
					}else {
						shoplistdata=getShopListData(result);
						if (shoplistdata.size()==0) {
							shoplist.hideFootView();
						}
					}
					mShopListAdapter = new ShopListAdapter(
							ShopListActivity.this, shoplistdata);
					shoplist.setAdapter(mShopListAdapter);
					shoplist.setOnItemClickListener(ShopListActivity.this);
					shoplist.stopLoadMore();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showShortToast("没有数据了");
					if (pagerindex==0) {
						shoplistdata.clear();
						if (mShopListAdapter!=null) {
							mShopListAdapter.notifyDataSetChanged();
							shoplist.setAdapter(mShopListAdapter);
						}
						
					}
					closeLoadingDialog();
					shoplist.stopLoadMore();
					shoplist.hideFootView();
				}
				closeLoadingDialog();
				break;
//			case findby_what:
//				Log.i("hjq", "findby_what="+result);
//				if (result.trim().equals("null")) {
//					shoplistdata.clear();
//					if (mShopListAdapter!=null) {
//						mShopListAdapter.notifyDataSetChanged();
//						mShopListAdapter = new ShopListAdapter(
//								ShopListActivity.this, shoplistdata);
//						shoplist.setAdapter(mShopListAdapter);
//						shoplist.setOnItemClickListener(ShopListActivity.this);
//					}
//					showShortToast("没有找到店铺信息");
//					shoplist.stopLoadMore();
//					shoplist.hideFootView();
//				} else {
//					try {
//						shoplistdata = getShopListData(result);
//						mShopListAdapter.notifyDataSetChanged();
//						mShopListAdapter = new ShopListAdapter(
//								ShopListActivity.this, shoplistdata);
//						shoplist.setAdapter(mShopListAdapter);
//						shoplist.setOnItemClickListener(ShopListActivity.this);
//						shoplist.stopLoadMore();
//						shoplist.hideFootView();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				closeLoadingDialog();
//				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoplist);
		mTypeDao = new TypeDao(this);
		shoplist = (XListView) findViewById(R.id.shoplist);
		shoplist.setPullLoadEnable(true);
		shoplist.setPullRefreshEnable(false);
		shoplist.setXListViewListener(this);
		seachbtn = (ImageView) findViewById(R.id.iv_search);
		citybtn = (LinearLayout) findViewById(R.id.citybtn);
		citytext = (TextView) findViewById(R.id.city_text);
		seachbtn.setOnClickListener(this);
		citybtn.setOnClickListener(this);
		citytext.setText("芜湖");
		initPopupWindow();
		initRun();
		showLoadingDialog();

	}

	private void initRun() {
		// TODO Auto-generated method stub
		typelistRun = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.get(HttpUtil.URL_TYPELIST);
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg = new Message();
				msg.what = typelist_what;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		ThreadPoolManager.getInstance().addTask(typelistRun);
		SearchBySec();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(REFRESH_CITY);
		registerReceiver(MyReceiver, intentFilter);
	} 

	private void initPopupWindow() {
		String[] discounts = getResources()
				.getStringArray(R.array.discount_arr);
		secArr1.add("默认");
		for (int i = 0; i < discounts.length; i++) {
			secArr1.add(discounts[i]);
		}
		String[] servers = getResources().getStringArray(R.array.server_arr);
		secArr3.add("默认");
		for (int i = 0; i < servers.length; i++) {
			secArr3.add(servers[i]);
		}
//		String[] blocks = getResources().getStringArray(R.array.block_arr);
//		for (int i = 0; i < blocks.length; i++) {
//			areaArr.add(blocks[i]);
//		}
		text1 = (TextView) findViewById(R.id.section1);
		text2 = (TextView) findViewById(R.id.section2);
		text3 = (TextView) findViewById(R.id.section3);
		trian1 = (ImageView) findViewById(R.id.mark1);
		trian2 = (ImageView) findViewById(R.id.mark2);
		trian3 = (ImageView) findViewById(R.id.mark3);
		linLayout = (LinearLayout) findViewById(R.id.second);
		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text3.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void showSectionPop(int width, int height, final int secindex) {
		layout = (LinearLayout) LayoutInflater.from(ShopListActivity.this)
				.inflate(R.layout.popup_category, null);
		section_list = (ListView) layout.findViewById(R.id.section_list);
		section_grid = (GridView) layout.findViewById(R.id.section_grid);
		section_list.setOnItemClickListener(this);
		section_grid.setOnItemClickListener(this);
		if (secindex == 1) {// 2是grid
			section_grid.setVisibility(View.VISIBLE);
			section_list.setVisibility(View.GONE);
			secAdapter = new SectionAdapter(ShopListActivity.this, secArr2,
					selectPos[secindex], secindex);
			section_grid.setAdapter(secAdapter);
		} else {
			section_grid.setVisibility(View.GONE);
			section_list.setVisibility(View.VISIBLE);
			if (secindex == 0) {
				secAdapter = new SectionAdapter(ShopListActivity.this, secArr1,
						selectPos[secindex], secindex);
			} else {
				secAdapter = new SectionAdapter(ShopListActivity.this, secArr3,
						selectPos[secindex], secindex);
			}
			section_list.setAdapter(secAdapter);
		}
		mPopWin = new PopupWindow(layout, width, LayoutParams.WRAP_CONTENT,
				true);
		mPopWin.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				selectSecCheck(4);
			}
		});
		mPopWin.setBackgroundDrawable(new BitmapDrawable());
		mPopWin.showAsDropDown(text1, 0, 0);
		mPopWin.update();
	}

	@SuppressWarnings("deprecation")
	private void showAreaPop(int width, int height) {
		layout = (LinearLayout) LayoutInflater.from(ShopListActivity.this)
				.inflate(R.layout.popup_area, null);
		area_grid = (GridView) layout.findViewById(R.id.area_grid);
		area_grid.setOnItemClickListener(this);
		mAreaAdapter = new AreaAdapter();
		area_grid.setAdapter(mAreaAdapter);
		mPopWin = new PopupWindow(layout, width, LayoutParams.WRAP_CONTENT,
				true);
		View area_loc = (LinearLayout) layout.findViewById(R.id.area_loc);
		area_loc.setOnClickListener(this);
		mPopWin.setBackgroundDrawable(new BitmapDrawable());
		mPopWin.showAsDropDown(findViewById(R.id.head), 0, 0);
		mPopWin.update();
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.section1:
			secindex = 0;
			selectSecCheck(secindex);
			showSectionPop(linLayout.getWidth(), linLayout.getHeight(), 0);
			break;
		case R.id.section2:
			secindex = 1;
			selectSecCheck(secindex);
			showSectionPop(linLayout.getWidth(), linLayout.getHeight(), 1);
			break;
		case R.id.section3:
			secindex = 2;
			selectSecCheck(secindex);
			showSectionPop(linLayout.getWidth(), linLayout.getHeight(), 2);
			break;
		case R.id.iv_search:
			startActivity(new Intent(ShopListActivity.this,
					ShopSearchActivity.class));
			break;
		case R.id.citybtn:
//			secindex = 3;
//			showAreaPop(linLayout.getWidth(), linLayout.getHeight());
			Intent mIntent=new Intent(ShopListActivity.this,
					SelectCityActivity.class);
			mIntent.putExtra("city", citytext.getText().toString());
			startActivityForResult(mIntent, 1);
			break;
		case R.id.area_loc:
			if (mPopWin != null && mPopWin.isShowing()) {
				mPopWin.dismiss();
				Toast.makeText(this, "点击了切换城市", 1).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (R.id.shoplist == parent.getId()) {
			Intent mIntent = new Intent(this, ShopDetailActivity.class);
			mIntent.putExtra("object", shoplistdata.get(position-1));
			startActivity(mIntent);
			return;
		}
		if (R.id.area_grid == parent.getId()) {
			return;
		}
		selectPos[secindex] = position;
		secAdapter.notifyDataSetChanged();
		switch (secindex) {
		case 0:
			secAdapter = new SectionAdapter(ShopListActivity.this, secArr1,
					position, secindex);
			section_list.setAdapter(secAdapter);
			if (position==0) {
				text1.setText(R.string.shop_list_section_discount);
			}else {
				text1.setText(secArr1.get(position));
			}
			pagerindex=0;
			SearchBySec();
			break;
		case 1:
			secAdapter = new SectionAdapter(ShopListActivity.this, secArr2,
					position, secindex);
			section_grid.setAdapter(secAdapter);
			if (position==0) {
				text2.setText(R.string.shop_list_section_type);
			}else {
				text2.setText(secArr2.get(position));
			}
			pagerindex=0;
			SearchBySec();
			break;
		case 2:
			secAdapter = new SectionAdapter(ShopListActivity.this, secArr3,
					position, secindex);
			section_list.setAdapter(secAdapter);
			text3.setText(secArr3.get(position));
			if (position==0) {
				text3.setText(R.string.shop_list_section_service);
			}else {
				text3.setText(secArr3.get(position));
			}
			pagerindex=0;
			SearchBySec();
			break;
		}
		mPopWin.dismiss();
	}

//需要重新写	
	private void SearchBySec() {
		Log.e("hjq", "SearchBySec");
		final String citystr=citytext.getText().toString().trim();
		Runnable listRun = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<BasicNameValuePair> mPair=new ArrayList<BasicNameValuePair>();
				if (selectPos[0] != 0) {
					String para1 = secArr1.get(selectPos[0]);
					mPair.add(new BasicNameValuePair(JsonUtil.DISCOUNT, para1));
				}
				if (selectPos[1] != 0) {
					String para2 = mTypeDao.queryByName(secArr2.get(selectPos[1]))
							.getId();
					mPair.add(new BasicNameValuePair(JsonUtil.ID, para2));
				}
				if (selectPos[2] != 0) {
					String para3 = selectPos[2]+"";
					mPair.add(new BasicNameValuePair(JsonUtil.SERVICEID, para3));
				}
				if (PreferenceUtil.getInstance(ShopListActivity.this).getString(PreferenceUtil.CITYID, "0").equals("0")) {
					mPair.add(new BasicNameValuePair(JsonUtil.CITY, "58"));
				}else {
					mPair.add(new BasicNameValuePair(JsonUtil.CITY,PreferenceUtil.getInstance(ShopListActivity.this).getString(PreferenceUtil.CITYID, "0")));
				}
				String lat=PreferenceUtil.getInstance(ShopListActivity.this).getString(PreferenceUtil.LAT, "");
				String lon=PreferenceUtil.getInstance(ShopListActivity.this).getString(PreferenceUtil.LON, "");
				if (!lat.equals("")&&!lon.equals("")) {
					mPair.add(new BasicNameValuePair(JsonUtil.LAT,lat));
					mPair.add(new BasicNameValuePair(JsonUtil.LONG,lon));
				}
				mPair.add(new BasicNameValuePair(JsonUtil.PAGE,String.valueOf(pagerindex)));
				BasicNameValuePair[] pairs=new BasicNameValuePair[mPair.size()];
				for (int i = 0; i < pairs.length; i++) {
					pairs[i]=mPair.get(i);
				}
				Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_FINDSTOREBYALL,
						pairs));
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_FINDSTOREBYALL,
							pairs);
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}

				Message msg = new Message();
				msg.what = shoplist_what;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		ThreadPoolManager.getInstance().addTask(listRun);
		
	}

	private ArrayList<Shop> getShopListData(String result) throws JSONException {
		ArrayList<Shop> shops = new ArrayList<Shop>();
		JSONArray jsonarr = new JSONObject(result).getJSONArray("list");
		Log.e("hjq", "getShopListData="+jsonarr.length());
		for (int i = 0; i < jsonarr.length(); i++) {
			JSONObject jsonobj = (JSONObject) jsonarr.get(i);
			String id = jsonobj.getString(JsonUtil.ID);
			String name = jsonobj.getString(JsonUtil.NAME);
			String type_name = mTypeDao.queryById(
					jsonobj.getString(JsonUtil.TYPE_ID)).getName();
			String city = jsonobj.getString(JsonUtil.CITY);
			String phone = jsonobj.getString(JsonUtil.PHONE);
			String average_buy = jsonobj.getString(JsonUtil.AVERAGE_BUY);
			String start_hours = jsonobj.getString(JsonUtil.START_HOURS);
			String end_hours = jsonobj.getString(JsonUtil.END_HOURS);
			String routes = jsonobj.getString(JsonUtil.ROUTES);
			String address = jsonobj.getString(JsonUtil.ADDRESS);
			String is_rooms = jsonobj.getString(JsonUtil.IS_ROOMS);
			String lon = jsonobj.getString(JsonUtil.LONG);
			String lat = jsonobj.getString(JsonUtil.LAT);
			String license = "";
//			String license = jsonobj.getString(JsonUtil.LICENSE);
			String permit = jsonobj.getString(JsonUtil.PERMIT);
			String short_message = jsonobj.getString(JsonUtil.SHORT_MESSAGE);
			String short_message_remark = jsonobj
					.getString(JsonUtil.SHORT_MESSAGE_REMARK);
			String bank_name = jsonobj.getString(JsonUtil.BANK_NAME);
			String bank_number = jsonobj.getString(JsonUtil.BANK_NUMBER);
			String bane_username = jsonobj.getString(JsonUtil.BANE_USERNAME);
			String zhifubao = jsonobj.getString(JsonUtil.ZHIFUBAO);
			String discount = jsonobj.getString(JsonUtil.DISCOUNT);
			String create_time = jsonobj.getString(JsonUtil.CREATE_TIME);
			String image = jsonobj.getString(JsonUtil.IMAGE);
			String image_thumb = jsonobj.getString(JsonUtil.IMAGE_THUMB);
			String is_schedule = jsonobj.getString(JsonUtil.IS_SCHEDULE);
			String is_point = jsonobj.getString(JsonUtil.IS_POINT);
			String is_group = jsonobj.getString(JsonUtil.IS_GROUP);
			String is_card = jsonobj.getString(JsonUtil.IS_CARD);
			String is_pay = jsonobj.getString(JsonUtil.IS_PAY);
			String intro = jsonobj.getString(JsonUtil.INTRO);
			String username = jsonobj.getString(JsonUtil.USERNAME);
			String password = jsonobj.getString(JsonUtil.PASSWORD);
			String temp_distance = jsonobj.getString(JsonUtil.TEMP_DISTANCE);
			shops.add(new Shop(id, name, type_name, city, phone, average_buy,
					start_hours, end_hours, routes, address, is_rooms, lon,
					lat, license, permit, short_message, short_message_remark,
					bank_name, bank_number, bane_username, zhifubao, discount,
					create_time, image, image_thumb, is_schedule, is_point,
					is_group, is_card, is_pay, intro, username, password,temp_distance));
		}
		return shops;
	}
	class AreaAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return areaArr.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return areaArr.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			// TODO Auto-generated method stub;
			TextView tv;
			if (convertView == null) {
				convertView = getLayoutInflater().from(ShopListActivity.this)
						.inflate(R.layout.area_item, null);
				tv = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(tv);
			} else {
				tv = (TextView) convertView.getTag();
			}
			tv.setText(areaArr.get(position));
			if (area_selectid == position) {
				tv.setTextColor(0xff1398a7);
				tv.setBackgroundResource(R.drawable.area_item_selected);
			} else {
				tv.setTextColor(Color.BLACK);
				convertView.setBackgroundResource(R.drawable.white_btn_text);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selected(position);
					citytext.setText(areaArr.get(position));
					mPopWin.dismiss();
				}
			});
			return convertView;
		}

		public void selected(int id) {
			area_selectid = id;
			this.notifyDataSetChanged();
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		Log.e("hjq", "onLoadMore");
			pagerindex++;
			SearchBySec();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { 
		case RESULT_OK:
			String cityStr=data.getStringExtra("city");
			citytext.setText(cityStr);
			SearchBySec();
		  break;
		default:
		          break;
		}
		}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(MyReceiver);
        PreferenceUtil.getInstance(this).setString(PreferenceUtil.CITYID, "0");
	}
	BroadcastReceiver MyReceiver = new BroadcastReceiver() {
		 
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.e("hjq","MyReceiver.action=" + action);
			if (action.equals(REFRESH_CITY)) {
//				citytext.setText(PreferenceUtil.getInstance(ShopListActivity.this).getString(PreferenceUtil.CITY, getString(R.string.city_unknown)));
			    if (mShopListAdapter!=null) {
		            Log.e("hjq", "islocation="+MyApplication.getInstance().islocation);
			    	SearchBySec();
			    	mShopListAdapter.chanageDistance();
			    	pagerindex=0;
			    	SearchBySec();
				}
			} 
		}
	};
	
}
