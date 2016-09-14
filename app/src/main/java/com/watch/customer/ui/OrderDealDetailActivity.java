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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.model.OrderDeal;
import com.watch.customer.model.OrderItem;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class OrderDealDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView order_id;
	private TextView price;
	private TextView num;
	private TextView total_price;
	private TextView status;
	private TextView dealcode;
	private TextView time;
	private LinearLayout codeLin;
	private Button refund_btn;
	private Button comment_btn;
	private Button zfb_btn;
	private Button shibi_btn;
	private ArrayList<OrderDetailItem> items=new ArrayList<OrderDetailItem>();
    private OrderDeal mOrderdeal;
    private OrderItem mOrderItem;
    private Handler mHandler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case 0:
				try {
					String result=msg.obj.toString();
					Log.e("hjq", result);
					    mOrderdeal=getOrderData(result);
						price.setText(mOrderdeal.getGroup_price());
						num.setText(mOrderdeal.getGroup_count());
						double gprice=Double.parseDouble(mOrderdeal.getGroup_price());
						int gcount=Integer.parseInt(mOrderdeal.getGroup_count());
						Log.e("hjq", "gprice="+gprice+"gcount="+gcount);
						total_price.setText(CommonUtil.DouToStr1(gcount*gprice));
						order_id.setText(mOrderdeal.getOrder_id());
						status.setText(CommonUtil.getOrderStatus(mOrderdeal.getStatus()));
						dealcode.setText(mOrderdeal.getCheckgroup());
						time.setText(mOrderdeal.getStart_time().substring(0, 10)+"——"+mOrderdeal.getEnd_time().substring(0, 10));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case AlipayUtil.RQF_PAY:
				Result result = new Result((String) msg.obj);
				if (result.isPaySuccess()) {
					Toast.makeText(OrderDealDetailActivity.this, "支付成功!", 1).show();
					finish();
				} else {
					Toast.makeText(OrderDealDetailActivity.this, "支付出现错误,请到订单列表重新支付", 1).show();
					finish();
				}
				break;
			case 2:
    			String result1=msg.obj.toString();
    			Log.e("hjq", result1);
    			closeLoadingDialog();
			try {
				JSONObject json=new JSONObject(result1);
				if (json.getInt(JsonUtil.CODE)==1) {
					showLongToast(json.getString(JsonUtil.MSG));
					finish();
				}else {
					showLongToast(json.getString(JsonUtil.MSG));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    			
			break;
			}
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_deal_detail);
		mOrderItem=(OrderItem)getIntent().getSerializableExtra(JsonUtil.ORDER);
		comment_btn = (Button) findViewById(R.id.comment_btn);
		refund_btn = (Button) findViewById(R.id.refund_btn);
		zfb_btn = (Button) findViewById(R.id.zfb_btn);
		shibi_btn = (Button) findViewById(R.id.shibi_btn);
		order_id = (TextView)findViewById(R.id.order_id);
		price = (TextView)findViewById(R.id.price);
		num = (TextView)findViewById(R.id.num);
		total_price = (TextView)findViewById(R.id.total_price);
		status = (TextView)findViewById(R.id.status);
		dealcode = (TextView)findViewById(R.id.dealcode);
		time = (TextView)findViewById(R.id.time);
		codeLin=(LinearLayout)findViewById(R.id.codeLin);
		findViewById(R.id.back).setOnClickListener(this);
		comment_btn.setOnClickListener(this);
		refund_btn.setOnClickListener(this);
		zfb_btn.setOnClickListener(this);
		shibi_btn.setOnClickListener(this);
//		price.setText(mOrderItem.getGroup_price());
		num.setText(mOrderItem.getGroup_count());
		order_id.setText(mOrderItem.getOrder_id());
		status.setText(CommonUtil.getDealOrderStatus(mOrderItem.getStatus()));
		dealcode.setText(mOrderItem.getCheck_group());
//		time.setText(mOrderItem.getStart_time().substring(0, 10)+"——"+mOrderdeal.getEnd_time().substring(0, 10));
         ThreadPoolManager.getInstance().addTask(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_GETGROUPBYORDERID, 
	                    new BasicNameValuePair(JsonUtil.ORDER_ID,mOrderItem.getOrder_id())));
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_GETGROUPBYORDERID,
							new BasicNameValuePair(JsonUtil.ORDER_ID, mOrderItem.getOrder_id()));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
				msg.what=0;
				msg.obj=result;
				mHandler.sendMessage(msg);
			}
		});
         if (mOrderItem.getStatus().equals("submit")) {
        	 zfb_btn.setVisibility(View.VISIBLE);
        	 shibi_btn.setVisibility(View.VISIBLE);
        	 codeLin.setVisibility(View.GONE);
        	 comment_btn.setVisibility(View.GONE);
		}else if(mOrderItem.getStatus().equals("finish")){
			 refund_btn.setVisibility(View.VISIBLE);
			 codeLin.setVisibility(View.VISIBLE);
			 comment_btn.setVisibility(View.VISIBLE);
		}else if(mOrderItem.getStatus().equals("pay")){
			 refund_btn.setVisibility(View.VISIBLE);
			 codeLin.setVisibility(View.VISIBLE);}
		else {
			 codeLin.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.comment_btn:
			Intent mIntent=new Intent(this, ShopMenuCommentActivity.class);
			mIntent.putExtra("orderdeal", mOrderdeal);
			startActivity(mIntent);
			break;
		case R.id.refund_btn:
			Intent cIntent=new Intent(this, ShopDealRefundActivity.class);
			cIntent.putExtra("orderdeal", mOrderdeal);
			startActivity(cIntent);
			break;
		 case R.id.zfb_btn:
				String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
				AlipayUtil alipayUtil = new AlipayUtil(OrderDealDetailActivity.this, mHandler, "掌上餐厅菜品支付", "无",
						CommonUtil.DouToStr1(Double.parseDouble(mOrderdeal.getGroup_price())*Double.parseDouble(mOrderItem.getGroup_count())),
						mOrderItem.getOrder_id()+"", url);
				alipayUtil.doAlipay();
	        	break;
		 case R.id.shibi_btn:
				String cur=PreferenceUtil.getInstance(OrderDealDetailActivity.this).getString(JsonUtil.SHIBI, "0");
	    		Double mycoin=Double.parseDouble(cur);
	    		Double curcoin=Double.parseDouble(mOrderdeal.getTotal_price());
	    		if (mycoin<curcoin) {
					showLongToast("食币余额不足，请先充值");
					return ;
				}
	    	showLoadingDialog();
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String user_id=PreferenceUtil.getInstance(OrderDealDetailActivity.this).getUid();
					String shibi=mOrderdeal.getTotal_price();
					String order_id=mOrderItem.getOrder_id();
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
				break;
		default:
			break;
		}
	}
	protected OrderDeal getOrderData(String result) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject json=new JSONArray(result).getJSONObject(0);
		 String id=json.getString(JsonUtil.ID);
		 String order_id=json.getString(JsonUtil.ORDER_ID);
		 String user_id=json.getString(JsonUtil.USER_ID);
		 String phone=json.getString(JsonUtil.PHONE);
		 String store_id=json.getString(JsonUtil.STORE_ID);
		 String create_time=json.getString(JsonUtil.CREATE_TIME);
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
		 String pay_type=json.getString(JsonUtil.PAY_TYPE);
		 String pay_time=json.getString(JsonUtil.PAY_TIME);
		 String title=json.getString(JsonUtil.TITLE);
		 String group_price=json.getString(JsonUtil.GROUP_PRICE);
		 String start_time=json.getString(JsonUtil.START_TIME);
		 String end_time=json.getString(JsonUtil.END_TIME);
		 return new OrderDeal(id, order_id, user_id, phone, store_id, create_time, people, is_room, order_time, type, status, checkgroup, group_count, group_id, userName, total_price, pay_type, pay_time, title, group_price, start_time, end_time);
	}


}
