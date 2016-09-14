package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.dao.UserDao;
import com.watch.customer.model.Dish;
import com.watch.customer.model.Menu;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class ShopMenuSumActivity extends BaseActivity  {
	private ListView mListView;
	private TextView summary;
	private TextView submit;
	private TextView nametv;
	private TextView phonetv;
	private int[] nums;
	private MenusumAdapter sumAdapter;
    private ArrayList<Dish> dishArr=new ArrayList<Dish>();
    private double sum = 0;
    private String type="";
    private String create_time="";
    private String personnum="";
    private String isroom="";
    private String name="";
    private String disheslist="";
    private Shop mShop;
    private boolean schedule=false;
    private ImageView orderlist;
    private Handler mHandler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		String result=msg.obj.toString();
    		closeLoadingDialog();
    		try {	
    		JSONObject object = new JSONObject(result);
    		 if (object.getString(JsonUtil.CODE).equals("1")) {
    			 if (schedule) {
    				 Intent shopIntent=new Intent(ShopMenuSumActivity.this,OrderResultActivity.class);
 					shopIntent.putExtra("object", mShop);
 					String order_id=object.getString("order_id");
 				    create_time=object.getString("create_time");
 					shopIntent.putExtra("create_time", create_time);
 					shopIntent.putExtra("order_id", order_id);
 					shopIntent.putExtra("personnum",personnum);
 					shopIntent.putExtra("type",type);
 					shopIntent.putExtra("disheslist",getMenuData());
 					startActivity(shopIntent);
 					finish();
				}
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
		setContentView(R.layout.activity_shop_menusum);
		Intent bef=getIntent();
		mShop=(Shop)bef.getSerializableExtra("object");
		schedule=bef.getBooleanExtra("schedule", false);
			if (schedule) {
				Log.e("hjq","schedule=true");
				schedule=true;
				create_time=bef.getStringExtra("create_time");
				personnum= bef.getStringExtra("personnum");
				isroom=bef.getStringExtra("isroom");
				name= bef.getStringExtra("name");
			}
		dishArr=(ArrayList<Dish>)getIntent().getSerializableExtra("list");
		mListView = (ListView) findViewById(R.id.menusum_lv);
		summary = (TextView) findViewById(R.id.summary);
		nametv = (TextView) findViewById(R.id.personname);
		phonetv = (TextView) findViewById(R.id.personphone);
		submit = (TextView) findViewById(R.id.submit);
		orderlist = (ImageView) findViewById(R.id.orderlist);
		if (name.equals("")) {
			nametv.setText(new UserDao(this).queryById(PreferenceUtil.getInstance(this).getUid()).getName());
		}else {
			nametv.setText(name);
		}
		phonetv.setText(new UserDao(this).queryById(PreferenceUtil.getInstance(this).getUid()).getPhone());
		((TextView)findViewById(R.id.title)).setText(mShop.getName());;
		findViewById(R.id.back).setOnClickListener(this);
		submit.setOnClickListener(this);
		orderlist.setOnClickListener(this);
		nums = new int[dishArr.size()];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = 1;
		}
		freshData();
		sumAdapter = new MenusumAdapter(this);
		mListView.setAdapter(sumAdapter);
	   
	}

	class MenusumAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context context;

		public MenusumAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mInflater = getLayoutInflater().from(ShopMenuSumActivity.this);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dishArr.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dishArr.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder mHolder = null;
			if (convertView == null) {
				mHolder = new Holder();
				convertView = mInflater.inflate(R.layout.menusum_item, null);
				mHolder.image = (ImageView) convertView
						.findViewById(R.id.menu_image);
				mHolder.name = (TextView) convertView
						.findViewById(R.id.menu_name);
				mHolder.price = (TextView) convertView
						.findViewById(R.id.menu_price);
				mHolder.discount = (ImageView) convertView
						.findViewById(R.id.menu_discount);
				mHolder.num = (TextView) convertView
						.findViewById(R.id.menu_num);
				mHolder.sub = (ImageView) convertView.findViewById(R.id.sub);
				mHolder.add = (ImageView) convertView.findViewById(R.id.add);
				convertView.setTag(mHolder);
			} else {
				mHolder = (Holder) convertView.getTag();
			}
			ImageLoaderUtil.displayImage(HttpUtil.SERVER+dishArr.get(position).getImage(),
					mHolder.image, context);
			mHolder.name.setText(dishArr.get(position).getDishes_name());
			mHolder.price.setText("¥" + dishArr.get(position).getPrice() + "/个");
			if (dishArr.get(position).getDiscount().equals("1")) {
				mHolder.discount.setVisibility(View.VISIBLE);
			} else {
				mHolder.discount.setVisibility(View.INVISIBLE);
			}
			mHolder.num.setText(String.valueOf(nums[position]));
			mHolder.sub.setTag(position);
			mHolder.sub.setOnClickListener(ShopMenuSumActivity.this);
			mHolder.add.setTag(position);
			mHolder.add.setOnClickListener(ShopMenuSumActivity.this);
			return convertView;
		}

		private class Holder {
			public ImageView image;
			public TextView name;
			public TextView price;
			public ImageView discount;
			public TextView num;
			public ImageView sub;
			public ImageView add;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.orderlist:
			Intent mIntent=new Intent(MainActivity.ACTION_TAB);
			mIntent.putExtra("index", 2);
			sendBroadcast(mIntent);
			startActivity(new Intent(this,MainActivity.class));
			finish();
			break;
		case R.id.sub:
			int subpos = (Integer) v.getTag();
			if (nums[subpos] > 1) {
				nums[subpos] = nums[subpos] - 1;
				sumAdapter.notifyDataSetChanged();
				freshData();
			}
			break;
		case R.id.add:
			int addpos = (Integer) v.getTag();
			nums[addpos] = nums[addpos] + 1;
			sumAdapter.notifyDataSetChanged();
			freshData();
			break;
		case R.id.submit:
			int num=0;
			for (int i = 0; i < nums.length; i++) {
				num=num+nums[i];
			}
		    if (num==0) {
				showLongToast("还没有选择任何菜");
				return  ;
			}
		    if (schedule) {
		    	showLoadingDialog();
		    	ThreadPoolManager.getInstance().addTask(new Runnable() {				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String uid=PreferenceUtil.getInstance(ShopMenuSumActivity.this)
								.getUid();
						String store_id=dishArr.get(0).getStore_id();
						String people=personnum;
						String phone=new UserDao(ShopMenuSumActivity.this).queryById(uid).getPhone();
						String order_time=create_time;
						String is_room=isroom;
						String total_price=sum+"";	
						Log.e("hjq","total_price="+total_price );
						String username=name;	
	                    String type=schedule?"schedule":"point";
						try {
						     disheslist=getDataByJson();
							Log.e("hjq", "disheslist="+disheslist);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String result= null;
						try {
							result = HttpUtil.post(HttpUtil.URL_SUBMITORDER,
									new BasicNameValuePair(JsonUtil.USER_ID, uid),
									new BasicNameValuePair(JsonUtil.STORE_ID, store_id),
									new BasicNameValuePair(JsonUtil.PEOPLE, people),
									new BasicNameValuePair(JsonUtil.PHONE, phone),
									new BasicNameValuePair(JsonUtil.ORDER_TIME, order_time),
									new BasicNameValuePair(JsonUtil.IS_ROOM, is_room),
									new BasicNameValuePair(JsonUtil.DISHESLIST, disheslist),
									new BasicNameValuePair(JsonUtil.TOTAL_PRICE, total_price),
									new BasicNameValuePair("userName", username),
									new BasicNameValuePair(JsonUtil.TYPE, type));
						} catch (IOException e) {
							e.printStackTrace();
							result = e.getMessage();
						}
						Log.e("hjq", "result="+result);
						Message msg=new Message();
						msg.obj=result;
						mHandler.sendMessage(msg);
					}
				});
			}else {
				try {
				     disheslist=getDataByJson();
					Log.e("hjq", "disheslist="+disheslist);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent mIntent1=new Intent(ShopMenuSumActivity.this,
						ShopBookActivity.class);
				mIntent1.putExtra("object", mShop);
				mIntent1.putExtra("point", true);
				    mIntent1.putExtra("total_price",sum+"");
				    mIntent1.putExtra("disheslist",disheslist);
				    mIntent1.putExtra("dishArr",getMenuData());
				startActivity(mIntent1);
			}
		    
//			startActivity(new Intent(this, OrderDetailActivity.class));
			break;
		}
	}
	private String getDataByJson() throws JSONException {
		JSONArray array=new JSONArray();
		for (int i = 0; i < dishArr.size(); i++) {
			JSONObject object=new JSONObject();
			object.put(JsonUtil.DISHES_ID, dishArr.get(i).getId());
			object.put(JsonUtil.COUNT, nums[i]);
			object.put(JsonUtil.PRICE, Double.parseDouble(dishArr.get(i).getPrice())*nums[i]);
			array.put(object);
		}
		return array.toString();
	}
	private ArrayList<Menu> getMenuData(){
		ArrayList<Menu> array=new ArrayList<Menu>();
		for (int i = 0; i < dishArr.size(); i++) {
			Dish mDish=dishArr.get(i);
			array.add(new Menu(mDish.getDishes_name(),Double.parseDouble(mDish.getPrice())*nums[i]));
		}
		return array;
	}
	private void freshData() {
		int num=0;
		sum=0;
		for (int i = 0; i < dishArr.size(); i++) {
			sum = sum + Double.parseDouble(dishArr.get(i).getPrice())* nums[i];
			num=num+nums[i];
		}
		summary.setText("共计" + num + "个菜，¥" + sum);
	}
}
