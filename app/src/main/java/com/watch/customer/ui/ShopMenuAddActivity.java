package com.watch.customer.ui;

import java.io.IOException;
import java.text.DecimalFormat;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.CategoryAdapter;
import com.watch.customer.adapter.DishListAdapter;
import com.watch.customer.model.Category;
import com.watch.customer.model.Dish;
import com.watch.customer.model.Order;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class ShopMenuAddActivity extends BaseActivity {
	private ListView categorylv;
	private ListView dishlv;
	private LinearLayout searchtext;
	private TextView categoryText;
	private TextView count_num;
	private TextView count_sum;
	private TextView submit;
	private CategoryAdapter categoryAdapter;
	private DishListAdapter dishAdapter;
	private ArrayList<Category> categories = new ArrayList<Category>();
	private ArrayList<Dish> dishes = new ArrayList<Dish>();
	private final int find_what = 0;
	private final int search_what = 1;
	private final int getdiscount_what = 2;
	private String create_time="";
	private String personnum="";
	private String isroom="";
	private String name="";
	private boolean schedule=false;
	private String store_id="";
	private String store_name="";
	private String store_discount="";
 private Order mOrder;
	 private ArrayList<OrderDetailItem> items=new ArrayList<OrderDetailItem>();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result=msg.obj.toString();
			switch (msg.what) {
			case find_what:
//				Log.e("hjq", "find_what="+result);
               if (result.trim().equals("null")) {
			    showShortToast("没有找到数据");
            	   return ;	
			 }
				try {
					JSONObject json=new JSONObject(result);
					JSONArray cataArray=json.getJSONArray("dishestype");
					for (int i = 0; i < cataArray.length(); i++) {
						JSONObject object=cataArray.getJSONObject(i);
						String id=object.getString(JsonUtil.ID);
						String name=object.getString(JsonUtil.NAME);
						String store_id=object.getString(JsonUtil.STORE_ID);
						String order=object.getString(JsonUtil.ORDER);
						categories.add(new Category(id, name, store_id, order,0));
//						Log.e("hjq", "cataArray="+new Category(id, name, store_id, order,0).toString());
					}
					
					JSONArray dishArray=json.getJSONArray("disheslist");
					for (int i = 0; i < dishArray.length(); i++) {
						JSONObject object=dishArray.getJSONObject(i);
						String id=object.getString(JsonUtil.ID);
						String store_id=object.getString(JsonUtil.STORE_ID);
						String dishes_name=object.getString(JsonUtil.DISHES_NAME);
						String dishes_type=object.getString(JsonUtil.DISHES_TYPE);
						String image=object.getString(JsonUtil.IMAGE);
						String image_thumb=object.getString(JsonUtil.IMAGE_THUMB);
						String price=object.getString(JsonUtil.PRICE);
						String discount=object.getString(JsonUtil.DISCOUNT);
						String create_time=object.getString(JsonUtil.CREATE_TIME);
						dishes.add(new Dish(id, store_id, dishes_name, dishes_type, image, image_thumb, price, discount, create_time,false));
//						Log.e("hjq", "dishArray="+new Dish(id, store_id, dishes_name, dishes_type, image, image_thumb, price, discount, create_time,false).toString());
					}
					for (int j = 0; j < dishes.size(); j++) {
						for (int j2 = 0; j2 < items.size(); j2++) {
						if (items.get(j2).getDishes_id().equals(dishes.get(j).getId())) {
							dishes.get(j).setIsselect(true);
						}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                categoryAdapter = new CategoryAdapter(ShopMenuAddActivity.this, categories);
				categorylv.setAdapter(categoryAdapter);
				dishAdapter = new DishListAdapter(ShopMenuAddActivity.this, dishes);
				dishlv.setAdapter(dishAdapter);
				break;
			case search_what:

				break;
			case getdiscount_what:
               Log.e("hjq", "getdiscount_what="+result);
				try {
					store_discount=new JSONObject(result).getString("discount");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_menu);
		categorylv = (ListView) findViewById(R.id.category_list);
		dishlv = (ListView) findViewById(R.id.menu_list);
		searchtext = (LinearLayout) findViewById(R.id.category_search);
		categoryText = (TextView) findViewById(R.id.category_title);
		count_num = (TextView) findViewById(R.id.count_num);
		count_sum = (TextView) findViewById(R.id.count_sum);
		submit = (TextView) findViewById(R.id.submit);
		findViewById(R.id.back).setOnClickListener(this);
		searchtext.setOnClickListener(this);
		submit.setOnClickListener(this);
		Intent bef=getIntent();
		schedule=bef.getBooleanExtra("schedule", false);
		store_id=bef.getStringExtra("store_id");
		store_name=bef.getStringExtra("store_name");
		mOrder=(Order)bef.getSerializableExtra("order");
		((TextView)findViewById(R.id.title)).setText(store_name);
		items=(ArrayList<OrderDetailItem>)bef.getSerializableExtra("item");
		if(schedule ) {
			Log.e("hjq","schedule=true");
			schedule=true;
			create_time=bef.getStringExtra("create_time");
			Log.e("hjq", bef.getStringExtra("create_time"));
			personnum=bef.getStringExtra("personnum");
			isroom=bef.getStringExtra("isroom");
			name=bef.getStringExtra("name");
		}
		categorylv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.e("hjq",categories.get(position).toString());
				categoryText.setText(categories.get(position).getName());
				dishAdapter.showCatalist(categories.get(position).getId());
			}
		});
		dishlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Dish mDish=(Dish)dishAdapter.getItem(position);
					if (isOrderMenu(mDish)) {
						showShortToast("已经被选了,无法继续加菜");
						return ;	
				}
			
				boolean select=mDish.isIsselect();
				select=!select;
				mDish.setIsselect(select);
				dishAdapter.refresh();
			    categoryAdapter.setNumber(mDish.getDishes_type(), select);
			    countSum();
			}
		});

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_FINDDISHESBYPOINT,
							new BasicNameValuePair(JsonUtil.STOREID, store_id));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.what=find_what;
				msg.obj=result;
				mHandler.sendMessage(msg);
			}
		});
		Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_GETSTOREDISCOUNTBYSTOREID,
						new BasicNameValuePair(JsonUtil.STORE_ID, store_id)));
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(HttpUtil.URL_GETSTOREDISCOUNTBYSTOREID,
							new BasicNameValuePair(JsonUtil.STORE_ID, store_id));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.what=getdiscount_what;
				msg.obj=result;
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
		case R.id.category_search:
			Intent mIntent=new Intent(ShopMenuAddActivity.this,
					ShopMenuSearchActivity.class);
			mIntent.putExtra("object", store_id);
			startActivityForResult(mIntent, 1);
			break;
		case R.id.submit:
			ArrayList<Dish> sendList=new ArrayList<Dish>();
			for (int i = 0; i < dishes.size(); i++) {
				if (dishes.get(i).isIsselect()&&!isOrderMenu(dishes.get(i))) {
					if (dishes.get(i).getDiscount().equals("1")) {
						Double discount=Double.valueOf(store_discount)/100;
						Dish mDish=dishes.get(i);
						DecimalFormat deformat=new DecimalFormat("0.0");
						mDish.setPrice(Double.parseDouble(deformat.format(Double.parseDouble(dishes.get(i).getPrice())*discount))+"");
						sendList.add(mDish);
					}else {
						sendList.add(dishes.get(i));
					}
					
				}
			}
			if (sendList.size()==0) {
				showShortToast("没有选中任何菜单哦");
				return ;
			}
			if (PreferenceUtil.getInstance(this).getUid().equals("")) {
				Intent loginintent=new Intent(this,AuthLoginActivity.class);
				startActivity(loginintent);
				return ;
			}
			Intent sendIntent=new Intent(ShopMenuAddActivity.this,ShopMenuAddSumActivity.class);
			sendIntent.putExtra("schedule", schedule);
			sendIntent.putExtra("create_time",create_time);
			sendIntent.putExtra("personnum", personnum);
			sendIntent.putExtra("isroom",isroom);
			sendIntent.putExtra("name", name);
			sendIntent.putExtra("list", sendList);
			sendIntent.putExtra("store_name",store_name);
			sendIntent.putExtra("order",mOrder);
			startActivity(sendIntent);
			break;
		}
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { 
		case RESULT_OK:
		ArrayList<Dish> searchlist=(ArrayList<Dish>)data.getSerializableExtra("data");
		for (int i = 0; i < searchlist.size(); i++) {
			for (int j = 0; j < dishes.size(); j++) {
				if (searchlist.get(i).getId().equals(dishes.get(j).getId())) {
					if (!dishes.get(j).isIsselect()) {
						dishes.get(j).setIsselect(true);
						dishAdapter.refresh();
					    categoryAdapter.setNumber(dishes.get(j).getDishes_type(), true);
					}
				}
			}
		}
		countSum();
		  break;
		default:
		          break;
		}
		}

	private void countSum() {
		// TODO Auto-generated method stub
		int num=0;
		double sum=0.0;
		for (int i = 0; i < dishes.size(); i++) {
			if (dishes.get(i).isIsselect()&&!isOrderMenu(dishes.get(i))) {
				if (dishes.get(i).getDiscount().equals("1")) {
					Double discount=Double.valueOf(store_discount)/100;
					sum=sum+Double.parseDouble(dishes.get(i).getPrice())*discount;
				}else {
					sum=sum+Double.parseDouble(dishes.get(i).getPrice());
				}
				num++;
			}
		}
		count_num.setText("共"+num+"道菜");
		DecimalFormat deformat=new DecimalFormat("0.0");
		sum=Double.parseDouble(deformat.format(sum));
		count_sum.setText(getResources().getString(R.string.system_rmb)+sum);
	}
	public boolean isOrderMenu(Dish mdish){
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getDishes_id().equals(mdish.getId())) {
				return true;
			}
		}
		return false;
	}
}
