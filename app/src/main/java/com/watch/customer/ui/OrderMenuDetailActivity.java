package com.watch.customer.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.MenuAdapter;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.model.Menu;
import com.watch.customer.model.Order;
import com.watch.customer.model.OrderItem;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class OrderMenuDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView order_id;
	private TextView order_status;
	private TextView addmenu;
	private TextView create_time;
	private TextView order_time;
	private TextView people;
	private TextView is_room;
	private TextView name;
	private TextView phone;
	private TextView total_price;
	private ListView orderdetailList;
	private LinearLayout paybar;
	private LinearLayout menu_lin;
	private Button submitbtn;
	private Button hasconbtn;
    private Button commentbtn;
	private Button cashbtn;
	private Button zfbbtn;
	private Button shibibtn;
	private OrderItem mOrderItem;
	private Order mOrder;
	private ArrayList<Menu> menulistdata = new ArrayList<Menu>();
	private ArrayList<OrderDetailItem> items = new ArrayList<OrderDetailItem>();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				String result1 = msg.obj.toString();
				Log.e("hjq", result1);
				getOrderData(result1);
				create_time.setText(mOrder.getCreate_time().subSequence(0,
						mOrder.getCreate_time().length() - 1));
				order_time.setText(mOrder.getOrder_time().subSequence(0,
						mOrder.getOrder_time().length() - 1));
				people.setText(mOrder.getPeople() + "人");
				is_room.setText(mOrder.getIs_room().equals("1") ? "包间":"大厅");
				name.setText(mOrder.getUserName());
				phone.setText(mOrder.getPhone());
				total_price.setText(getString(R.string.system_rmb)
						+ CommonUtil.DouToStr1(Double.parseDouble(mOrder
								.getTotal_price())));
				MenuAdapter adapter = new MenuAdapter(
						OrderMenuDetailActivity.this, menulistdata);
				orderdetailList.setAdapter(adapter);
				setListViewHeightBasedOnChildren(orderdetailList);   
				menu_lin.setVisibility(View.VISIBLE);
				break;
			case AlipayUtil.RQF_PAY:
				Result result = new Result((String) msg.obj);
				if (result.isPaySuccess()) {
					Toast.makeText(OrderMenuDetailActivity.this, "支付成功!", 1)
							.show();
					showLoadingDialog("正在改变订单状态");
					ThreadPoolManager.getInstance().addTask(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String result= null;
							try {
								result = HttpUtil.post(HttpUtil.URL_UPDATEORDERSTATUS,
										new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem.getOrder_id()),
										new BasicNameValuePair(JsonUtil.STATUS, "pay"),
										new BasicNameValuePair(JsonUtil.PAY_TYPE, "1"));
							} catch (IOException e) {
								e.printStackTrace();
								result = e.getMessage();
							}
							Message msg=new Message();
					      msg.what=3;
					      msg.obj=result;
					      mHandler.sendMessage(msg);
						}
					});
				} else {
					Toast.makeText(OrderMenuDetailActivity.this, "支付出现错误", 1)
							.show();
				}
				break;
			case 0:
				String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
				AlipayUtil alipayUtil = new AlipayUtil(
						OrderMenuDetailActivity.this, mHandler, "掌上餐厅菜品支付",
						"无", mOrder.getTotal_price(), mOrder.getOrder_id(), url);
				alipayUtil.doAlipay();
				break;
			case 2:
				String result2 = msg.obj.toString();
				Log.e("hjq", result2);
				closeLoadingDialog();
				try {
					JSONObject json = new JSONObject(result2);
					if (json.getInt(JsonUtil.CODE) == 1) {
						showLongToast(json.getString(JsonUtil.MSG));
						showLoadingDialog("正在改变订单状态");
						ThreadPoolManager.getInstance().addTask(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								String result= null;
								try {
									result = HttpUtil.post(HttpUtil.URL_UPDATEORDERSTATUS,
											new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem.getOrder_id()),
											new BasicNameValuePair(JsonUtil.STATUS, "pay"),
											new BasicNameValuePair(JsonUtil.PAY_TYPE, "0"));
								} catch (IOException e) {
									e.printStackTrace();
									result = e.getMessage();
								}
								Message msg=new Message();
						      msg.what=3;
						      msg.obj=result;
						      mHandler.sendMessage(msg);
							}
						});
					} else {
						showLongToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case 3:
				String result3=msg.obj.toString();
    			Log.e("hjq", "result3="+result3);
//    			closeLoadingDialog();
    			try {
					JSONObject json=new JSONObject(result3);
					if (json.getInt(JsonUtil.CODE)==1) {
						showLongToast(getString(R.string.operate_success));
						finish();
					}else {
						showLongToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 4:
				String result4=msg.obj.toString();
    			Log.e("hjq", "result3="+result4);
    			closeLoadingDialog();
    			if (result4.contains("success")) {
					showLongToast("支付完成");
					finish();
				}else {
					showLongToast("支付失败");
				}
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_menu_detail);
		mOrderItem = (OrderItem) getIntent().getSerializableExtra(
				JsonUtil.ORDER);
		order_id = (TextView) findViewById(R.id.order_id);
		order_status = (TextView) findViewById(R.id.orderdetailstatus);
		paybar = (LinearLayout) findViewById(R.id.orderdetailPayBar);
		menu_lin = (LinearLayout) findViewById(R.id.menu_lin);
		create_time = (TextView) findViewById(R.id.create_time);
		order_time = (TextView) findViewById(R.id.order_time);
		people = (TextView) findViewById(R.id.people);
		is_room = (TextView) findViewById(R.id.is_room);
		name = (TextView) findViewById(R.id.name);
		phone = (TextView) findViewById(R.id.phone);
		total_price = (TextView) findViewById(R.id.total_price);
		addmenu= (TextView) findViewById(R.id.addmenu);
		orderdetailList = (ListView) findViewById(R.id.orderdetailList);
		submitbtn = (Button) findViewById(R.id.submit);
		hasconbtn= (Button) findViewById(R.id.hascon_btn);
		commentbtn= (Button) findViewById(R.id.comment_btn);
		cashbtn = (Button) findViewById(R.id.cash_btn);
		zfbbtn = (Button) findViewById(R.id.zfb_btn);
		shibibtn = (Button) findViewById(R.id.shibi_btn);
		if (mOrderItem.getStatus().equals("ensure")) {
			paybar.setVisibility(View.VISIBLE);
		} else if (mOrderItem.getStatus().equals("submit")) {
			submitbtn.setVisibility(View.VISIBLE);
		}else if (mOrderItem.getStatus().equals("consumption")) {
			commentbtn.setVisibility(View.VISIBLE);
		}else if (mOrderItem.getStatus().equals("localpay")||mOrderItem.getStatus().equals("pay")) {
			hasconbtn.setVisibility(View.VISIBLE);
		}
		findViewById(R.id.back).setOnClickListener(this);
		order_id.setText(mOrderItem.getOrder_id());
		order_status.setText(CommonUtil.getOrderStatus(mOrderItem.getStatus()));
		create_time.setText(mOrderItem.getCreate_time().substring(0,
				mOrderItem.getCreate_time().length() - 1));
		order_time.setText(mOrderItem.getCreate_time().substring(0,
				mOrderItem.getOrder_time().length() - 1));
		people.setText(mOrderItem.getPeople() + "人");
		is_room.setText(mOrderItem.getIs_room().equals("1") ? "包间":"大厅");
		name.setText(mOrderItem.getUserName());
		phone.setText(mOrderItem.getPhone());
		submitbtn.setOnClickListener(this);
		hasconbtn.setOnClickListener(this);
		commentbtn.setOnClickListener(this);
		cashbtn.setOnClickListener(this);
		zfbbtn.setOnClickListener(this);
		shibibtn.setOnClickListener(this);
		if (mOrderItem.getAdd_food().equals("0")&&mOrderItem.getStatus().equals("submit")) {
    		addmenu.setVisibility(View.VISIBLE);
    		addmenu.setOnClickListener(this);
		}
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try {
					result = HttpUtil.post(
							HttpUtil.URL_ORDERDETAIL,
							new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem
									.getOrder_id()));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg = new Message();
				msg.what = 1;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	protected void getOrderData(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject json = new JSONObject(result).getJSONArray(
					JsonUtil.ORDER).getJSONObject(0);
			String id = json.getString(JsonUtil.ID);
			String order_id = json.getString(JsonUtil.ORDER_ID);
			String user_id = json.getString(JsonUtil.USER_ID);
			String store_id = json.getString(JsonUtil.STORE_ID);
			String create_time = json.getString(JsonUtil.CREATE_TIME);
			String phone = json.getString(JsonUtil.PHONE);
			String people = json.getString(JsonUtil.PEOPLE);
			String is_room = json.getString(JsonUtil.IS_ROOM);
			String order_time = json.getString(JsonUtil.ORDER_TIME);
			String type = json.getString(JsonUtil.TYPE);
			String status = json.getString(JsonUtil.STATUS);
			String checkgroup = json.getString(JsonUtil.CHECKGROUP);
			String group_count = json.getString(JsonUtil.GROUP_COUNT);
			String group_id = json.getString(JsonUtil.GROUP_ID);
			String userName = json.getString("userName");
			String total_price = json.getString(JsonUtil.TOTAL_PRICE);
			mOrder = new Order(id, order_id, user_id, store_id, create_time,
					phone, people, is_room, order_time, type, status,
					checkgroup, group_count, group_id, userName, total_price);
			JSONArray array = new JSONObject(result)
					.getJSONArray(JsonUtil.DETAIL);
			for (int i = 0; i < array.length(); i++) {
				String order_id1 = array.getJSONObject(i).getString(
						JsonUtil.ORDER_ID);
				String count = array.getJSONObject(i).getString(JsonUtil.COUNT);
				String dishes_id = array.getJSONObject(i).getString(
						JsonUtil.DISHES_ID);
				String price = array.getJSONObject(i).getString(JsonUtil.PRICE);
				String dishes_name = array.getJSONObject(i).getString(
						JsonUtil.DISHES_NAME);
				items.add(new OrderDetailItem(order_id1, count, dishes_id,
						price, dishes_name));
				menulistdata.add(new Menu(dishes_name, Double
						.parseDouble(price)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.submit:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage(R.string.submit_msg)
					.setNegativeButton("确定", null).create().show();
			break;
	      case R.id.sure:
	  		showLoadingDialog();
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String result= null;
					try {
						result = HttpUtil.post(HttpUtil.URL_POINTORSCHEDULELOCALPAYORDER,
								new BasicNameValuePair(JsonUtil.LOCALPAY, mOrder.getTotal_price()),
								new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem.getOrder_id()));
					} catch (IOException e) {
						e.printStackTrace();
						result = e.getMessage();
					}
					Message msg=new Message();
					msg.obj=result;
					msg.what=4;
					mHandler.sendMessage(msg);
				}
			}); 
	      	  break;
		case R.id.cash_btn:
	
	    	  showLoadingDialog();
			showAfterInputDialog();
			break;
		case R.id.zfb_btn:
			String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
			AlipayUtil alipayUtil = new AlipayUtil(this, mHandler, "掌上餐厅菜品支付",
					"无", mOrder.getTotal_price(), mOrderItem.getOrder_id(), url);
			alipayUtil.doAlipay();
			break;
		case R.id.hascon_btn:
			   showLoadingDialog();
				ThreadPoolManager.getInstance().addTask(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
					Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_UPDATEORDERSTATUS, 
							new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem.getOrder_id()),
							new BasicNameValuePair(JsonUtil.STATUS, "consumption"),
							new BasicNameValuePair(JsonUtil.PAY_TYPE, "2")));
						String result= null;
						try {
							result = HttpUtil.post(HttpUtil.URL_UPDATEORDERSTATUS,
									new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem.getOrder_id()),
									new BasicNameValuePair(JsonUtil.STATUS, "consumption"),
									new BasicNameValuePair(JsonUtil.PAY_TYPE, "2"));
						} catch (IOException e) {
							e.printStackTrace();
							result = e.getMessage();
						}
						Message msg=new Message();
				      msg.what=3;
				      msg.obj=result;
				      mHandler.sendMessage(msg);
					}
				});
				break;
		case R.id.shibi_btn:
			String cur = PreferenceUtil.getInstance(
					OrderMenuDetailActivity.this)
					.getString(JsonUtil.SHIBI, "0");
			Double mycoin = Double.parseDouble(cur);
			Double curcoin = Double.parseDouble(mOrder.getTotal_price());
			if (mycoin < curcoin) {
				showLongToast("食币余额不足，请先充值");
				return;
			}
			showLoadingDialog();
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String user_id = PreferenceUtil.getInstance(
							OrderMenuDetailActivity.this).getUid();
					String shibi = mOrder.getTotal_price();
					String order_id = mOrderItem.getOrder_id();
					String result = null;
					try {
						result = HttpUtil
                                .post(HttpUtil.URL_USESHIBIPAY,
										new BasicNameValuePair(JsonUtil.USER_ID,
												user_id), new BasicNameValuePair(
												JsonUtil.SHIBI, shibi),
										new BasicNameValuePair(JsonUtil.ORDER_ID,
												order_id));
					} catch (IOException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.obj = result;
					msg.what = 2;
					mHandler.sendMessage(msg);
				}
			});
			break;
		case R.id.comment_btn:
			Intent mIntent=new Intent(this, ShopMenuCommentActivity.class);
			mIntent.putExtra("orderitem", mOrderItem);
            startActivity(mIntent);
			break;
		case R.id.addmenu:
			if (items.size()>0) {
				Intent mIntent2=new Intent(OrderMenuDetailActivity.this, ShopMenuAddActivity.class);
				mIntent2.putExtra("item", items);
				mIntent2.putExtra("addmenu", true);
				mIntent2.putExtra("store_id",mOrderItem.getStore_id());
				mIntent2.putExtra("store_name", mOrderItem.getStoreName());
				mIntent2.putExtra("order", mOrder);
				startActivity(mIntent2);
			}
			break;
		default:
			break;
		}
	}
	private void showAfterInputDialog(){	
		Dialog aDialog=new Dialog(this, R.style.loading_dialog);
		View parent=getLayoutInflater().inflate(R.layout.dialog_afterinputmoney, null);
	    EditText input=(EditText)parent.findViewById(R.id.dialog_input);
	    input.setEnabled(false);
		Button sure=(Button)parent.findViewById(R.id.sure);
		input.setText(mOrder.getTotal_price());
		sure.setOnClickListener(this);
		aDialog.setContentView(parent, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		aDialog.show();	
	}
	public void setListViewHeightBasedOnChildren(ListView listView) {   
        // 获取ListView对应的Adapter   
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {   
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   
            // listAdapter.getCount()返回数据项的数目   
            View listItem = listAdapter.getView(i, null, listView);   
            // 计算子项View 的宽高   
            listItem.measure(0, 0);    
            // 统计所有子项的总高度   
            totalHeight += listItem.getMeasuredHeight();    
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        // listView.getDividerHeight()获取子项间分隔符占用的高度   
        // params.height最后得到整个ListView完整显示需要的高度   
        listView.setLayoutParams(params);   
    }   
}


class OrderDetailItem implements Serializable{
	private String order_id;
	private String count;
	private String dishes_id;
	private String price;
	private String dishes_name;

	public OrderDetailItem(String order_id, String count, String dishes_id,
			String price, String dishes_name) {
		super();
		this.order_id = order_id;
		this.count = count;
		this.dishes_id = dishes_id;
		this.price = price;
		this.dishes_name = dishes_name;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getDishes_id() {
		return dishes_id;
	}

	public void setDishes_id(String dishes_id) {
		this.dishes_id = dishes_id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDishes_name() {
		return dishes_name;
	}

	public void setDishes_name(String dishes_name) {
		this.dishes_name = dishes_name;
	}

	@Override
	public String toString() {
		return "OrderDetailItem [order_id=" + order_id + ", count=" + count
				+ ", dishes_id=" + dishes_id + ", price=" + price
				+ ", dishes_name=" + dishes_name + "]";
	}

}
