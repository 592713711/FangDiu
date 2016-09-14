package com.watch.customer.ui;

import org.apache.http.message.BasicNameValuePair;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.app.MyApplication;
import com.watch.customer.model.Order;
import com.watch.customer.model.OrderItem;
import com.watch.customer.model.Shop;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class OrderLocalDetailActivity extends BaseActivity implements OnClickListener {
	private TextView order_id;
	private TextView order_status;
	private TextView create_time;
	private TextView order_time;
	private TextView people;
	private TextView is_room;
	private TextView name;
	private TextView phone;
	private EditText input;
    private Shop mShop;
    private OrderItem mOrderItem;
    private Order mOrder;
    private Button submitbtn;
	private Button zfbbtn;
	private Button shibibtn;
	private Button cashbtn;
	private Button commentbtn;
	private LinearLayout finishLin;
	private Button mylistbtn;
	private PopupWindow popupWindow;
	private int paytype=2;
	private String money;
	private String orderidstr;
    private Handler mHandler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
    		case 1:
				try {
					String result=msg.obj.toString();
					Log.e("hjq", result);
					getOrderData(result);
					create_time.setText(mOrder.getCreate_time().subSequence(0, mOrder.getCreate_time().length()-1));
					order_time.setText(mOrder.getOrder_time().subSequence(0, mOrder.getOrder_time().length()-1));
					people.setText(mOrder.getPeople()+"人");
					is_room.setText(mOrder.getIs_room().equals("1")?"包间":"大厅");
					name.setText(mOrder.getUserName());
					phone.setText(mOrder.getPhone());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

    		case AlipayUtil.RQF_PAY:
				Result result = new Result((String) msg.obj);
				if (result.isPaySuccess()) {
					Toast.makeText(OrderLocalDetailActivity.this, "支付成功!", 1).show();
					havePay("1");
				} else {
					Toast.makeText(OrderLocalDetailActivity.this, "支付出现错误", 1).show();
				}
				break;
			case 0:
				String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
				AlipayUtil alipayUtil = new AlipayUtil(OrderLocalDetailActivity.this, mHandler, "掌上餐厅菜品支付", "无",mOrder.getTotal_price(), mOrder.getOrder_id(), url);
				alipayUtil.doAlipay();
				break;
			case 2:
	    			String result1=msg.obj.toString();
	    			Log.e("hjq", result1);
	    			closeLoadingDialog();
				try {
					JSONObject json=new JSONObject(result1);
					if (json.getInt(JsonUtil.CODE)==1) {
						showLongToast(json.getString(JsonUtil.MSG));
						havePay("0");
					}else {
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
    			closeLoadingDialog();
    			if (result3.contains("success")) {
					showLongToast("支付完成");
					finish();
				}else {
					showLongToast("支付失败");
				}
				break;
			case 4:
				String result4=msg.obj.toString();
    			Log.e("hjq", "result4="+result4);
    			closeLoadingDialog();
    			if (result4.contains("success")) {
					showLongToast("数据已提交");
					finish();
				}else {
					showLongToast("数据提交失败");
				}
				break;
        	};				
			}	
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_local_detail);
		mShop=(Shop)getIntent().getSerializableExtra("object");
		order_id = (TextView) findViewById(R.id.order_id);
		order_status= (TextView) findViewById(R.id.orderdetailstatus);
		create_time = (TextView) findViewById(R.id.create_time);
		order_time = (TextView) findViewById(R.id.order_time);
		people = (TextView) findViewById(R.id.people);
		is_room = (TextView) findViewById(R.id.is_room);
		name = (TextView) findViewById(R.id.name);
		phone = (TextView) findViewById(R.id.phone);
		finishLin = (LinearLayout) findViewById(R.id.orderfinished);
		submitbtn = (Button) findViewById(R.id.submit);
		commentbtn = (Button) findViewById(R.id.comment_btn);
		mylistbtn = (Button) findViewById(R.id.orderlist_btn);
		zfbbtn = (Button) findViewById(R.id.zfb_btn);
		shibibtn = (Button) findViewById(R.id.shibi_btn);
		cashbtn = (Button) findViewById(R.id.cash_btn);
      if (mShop==null) {
    	  mOrderItem=(OrderItem)getIntent().getSerializableExtra("order");
    	  Log.e("hjq", mOrderItem.toString());
    	    order_id.setText(mOrderItem.getOrder_id());
    	    order_status.setText(CommonUtil.getOrderStatus(mOrderItem.getStatus()));
    	    create_time.setText(mOrderItem.getCreate_time().substring(0, mOrderItem.getCreate_time().length()-1));
    	    order_time.setText(mOrderItem.getOrder_time().substring(0, mOrderItem.getOrder_time().length()-1));
    	    people.setText(mOrderItem.getPeople()+"人");
    	    is_room.setText(mOrderItem.getIs_room().equals("1")?"包间":"大厅");
			name.setText(mOrderItem.getUserName());
			phone.setText(mOrderItem.getPhone());
    	    if (mOrderItem.getStatus().equals("ensure")) {

    			zfbbtn.setVisibility(View.VISIBLE);
    			shibibtn .setVisibility(View.VISIBLE);
			}else if (mOrderItem.getStatus().equals("pay")) {
				cashbtn.setVisibility(View.VISIBLE);
			}else if (mOrderItem.getStatus().equals("submit")) {
				submitbtn.setVisibility(View.VISIBLE);
			}else if (mOrderItem.getStatus().equals("consumption")) {
				commentbtn.setVisibility(View.VISIBLE);
			}
		}else {
			finishLin.setVisibility(View.VISIBLE);
			order_id.setText(getIntent().getStringExtra("order_id"));
			String mydate = getIntent().getStringExtra("create_time");
			mydate=CommonUtil.getTimestr(mydate);
			zfbbtn.setVisibility(View.GONE);
			shibibtn .setVisibility(View.GONE);
			finishLin.setVisibility(View.VISIBLE);
		}
		findViewById(R.id.back).setOnClickListener(this);
		commentbtn.setOnClickListener(this);		
		submitbtn.setOnClickListener(this);		
		mylistbtn.setOnClickListener(this);
		zfbbtn.setOnClickListener(this);
		shibibtn.setOnClickListener(this);
		cashbtn.setOnClickListener(this);
         ThreadPoolManager.getInstance().addTask(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_ORDERDETAIL,
							new BasicNameValuePair(JsonUtil.ORDER_ID, getIntent().getStringExtra("order_id")));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.what=1;
				msg.obj=result;
				mHandler.sendMessage(msg);
			}
		});
	}

	protected void havePay(final String string) {
		// TODO Auto-generated method stub
//		showLoadingDialog("正在提交数据");
//		Toast.makeText(OrderBookDetailActivity.this, "支付成功!", 1).show();
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
		      msg.what=4;
		      msg.obj=result;
		      mHandler.sendMessage(msg);
			}
		});
//		ThreadPoolManager.getInstance().addTask(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				String result=HttpUtil.post(HttpUtil.URL_SUBMITLOCALORDERPAY,
//						new BasicNameValuePair(JsonUtil.PAY_TYPE, string),
//						new BasicNameValuePair(JsonUtil.SCHEDULE_MONEY, money),
//						new BasicNameValuePair(JsonUtil.ORDER_ID,orderidstr));
//				Message msg=new Message();
//				msg.obj=result;
//				msg.what=4;
//				mHandler.sendMessage(msg);
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.submit:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setMessage(R.string.submit_msg).setNegativeButton("确定", null).create().show();
			break;
		case R.id.sure:
			Log.e("hjq", "onclick sure,paytype="+paytype);
			money=input.getText().toString().trim();
			orderidstr=order_id.getText().toString();
			if (money.equals("")) {
				showShortToast("输入的金额不能为空");
				return ;
			}
				if (paytype==1) {
					String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
					AlipayUtil alipayUtil = new AlipayUtil(OrderLocalDetailActivity.this, mHandler, "掌上餐厅菜品支付", "无", money,orderidstr, url);
					alipayUtil.doAlipay();
				}else if(paytype==0){
					String cur=PreferenceUtil.getInstance(OrderLocalDetailActivity.this).getString(JsonUtil.SHIBI, "0");
		    		Double mycoin=Double.parseDouble(cur);
		    		Double curcoin=Double.parseDouble(money);
		    		if (mycoin<curcoin) {
						showLongToast("食币余额不足，请先充值");
						return ;
					}
		    	showLoadingDialog();
				ThreadPoolManager.getInstance().addTask(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String user_id=PreferenceUtil.getInstance(OrderLocalDetailActivity.this).getUid();
						String shibi=money;
						String order_id=orderidstr;
						String result= null;
						try {
							result = HttpUtil.post(HttpUtil.URL_USESHIBIPAY,
									new BasicNameValuePair(JsonUtil.USER_ID, user_id),
									new BasicNameValuePair(JsonUtil.SHIBI, shibi),
									new BasicNameValuePair(JsonUtil.ORDER_ID, order_id));
						} catch (IOException e) {
							e.printStackTrace();
							result = e.getMessage();
						}
						Message msg=new Message();
						msg.obj=result;
						msg.what=2;
						mHandler.sendMessage(msg);
					}
				});
				}else {
					showLoadingDialog();
					ThreadPoolManager.getInstance().addTask(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String result= null;
							try {
								result = HttpUtil.post(HttpUtil.URL_LOCALPAYORDER,
										new BasicNameValuePair(JsonUtil.LOCALPAY, money),
										new BasicNameValuePair(JsonUtil.PAY_TYPE, paytype + ""),
										new BasicNameValuePair(JsonUtil.ORDER_ID, orderidstr));
							} catch (IOException e) {
								e.printStackTrace();
								result = e.getMessage();
							}
							Message msg=new Message();
							msg.obj=result;
							msg.what=3;
							mHandler.sendMessage(msg);
						}
					});
			}
//			input.getText().toString();
			break;
		case R.id.orderlist_btn:
			MyApplication.getInstance().type=2;
			startActivity(new Intent(this, MainActivity.class));
			
			break;
		case R.id.zfb_btn:
			paytype=1;
			showInputDialog();
			

			break;
		case R.id.shibi_btn:
			paytype=0;
			showInputDialog();
			
			break;
		case R.id.cash_btn:		
			if (mOrderItem.getStatus().equals("pay")) {
				paytype=2;
				showAfterInputDialog();
			}else {
				paytype=2;
				showInputDialog();
			}
			
			break;
		case R.id.comment_btn:
			Intent mIntent=new Intent(this, ShopMenuCommentActivity.class);
			mIntent.putExtra("orderitem", mOrderItem);
            startActivity(mIntent);
			break;
	
		default:
			break;
		}
	}
	protected void getOrderData(String result) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject(result).getJSONArray(JsonUtil.ORDER).getJSONObject(0);
		 String id=json.getString(JsonUtil.ID);
		 String order_id=json.getString(JsonUtil.ORDER_ID);
		 String user_id=json.getString(JsonUtil.USER_ID);
		 String store_id=json.getString(JsonUtil.STORE_ID);
		 String create_time=json.getString(JsonUtil.CREATE_TIME);
		 String phone=json.getString(JsonUtil.PHONE);
		 String people=json.getString(JsonUtil.PEOPLE);
		 String is_room=json.getString(JsonUtil.IS_ROOM);
		 String order_time=json.getString(JsonUtil.ORDER_TIME);
		 String type=json.getString(JsonUtil.TYPE);
		 String status=json.getString(JsonUtil.STATUS);
		 String checkgroup=json.getString(JsonUtil.CHECKGROUP);
		 String group_count=json.getString(JsonUtil.GROUP_COUNT);
		 String group_id=json.getString(JsonUtil.GROUP_ID);
		 String userName=json.getString("userName");
		 String total_price=json.getString(JsonUtil.TOTAL_PRICE);
		 mOrder= new Order(id, order_id, user_id, store_id, create_time, phone, people,
				 is_room, order_time, type, status, checkgroup, group_count, group_id,userName, total_price);

	}
	private void showInputDialog(){	
		Dialog aDialog=new Dialog(this, R.style.loading_dialog);
		
		View parent=getLayoutInflater().inflate(R.layout.dialog_inputmoney, null);
	    input=(EditText)parent.findViewById(R.id.dialog_input);
		Button sure=(Button)parent.findViewById(R.id.sure);
		sure.setOnClickListener(this);
		aDialog.setContentView(parent, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		aDialog.show();	
	}
	private void showAfterInputDialog(){	
		Dialog aDialog=new Dialog(this, R.style.loading_dialog);
		
		View parent=getLayoutInflater().inflate(R.layout.dialog_afterinputmoney, null);
	    input=(EditText)parent.findViewById(R.id.dialog_input);
		Button sure=(Button)parent.findViewById(R.id.sure);
		sure.setOnClickListener(this);
		aDialog.setContentView(parent, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		aDialog.show();	
	}
}
