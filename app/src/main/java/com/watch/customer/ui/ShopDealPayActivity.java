package com.watch.customer.ui;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.model.Deal;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class ShopDealPayActivity extends BaseActivity implements OnClickListener {
	private TextView orderidtv;
	private TextView pricetv;
    private TextView sum;
    private TextView sumpricetv;
    private Button zfbbtn;
    private Button shibibtn;
    private String group_price;
    private String group_count;
    private String total_price;
    private String order_id;
    private String pay_type;
    private Deal mDeal;
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		closeLoadingDialog();
    		switch (msg.what) {
			case 2:
				String result1 = msg.obj.toString();
				Log.e("hjq", result1);
				closeLoadingDialog();
				try {
					JSONObject json = new JSONObject(result1);
					if (json.getInt(JsonUtil.CODE) == 1) {
						showLongToast(json.getString(JsonUtil.MSG));
						pay_type="0";
						this.sendEmptyMessage(3);
					} else {
						showLongToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case AlipayUtil.RQF_PAY:
				Result result = new Result((String) msg.obj);
				if (result.isPaySuccess()) {
					Toast.makeText(ShopDealPayActivity.this, "支付成功!", 1).show();
					pay_type="1";
					this.sendEmptyMessage(3);
				} else {
					Toast.makeText(ShopDealPayActivity.this, "支付出现错误,请到订单列表重新支付", 1).show();
				}
				break;
			case 3:
				showLoadingDialog();
				ThreadPoolManager.getInstance().addTask(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String result= null;
						try {
							result = HttpUtil.post(HttpUtil.URL_UPDATEORDERSTATUS,
									new BasicNameValuePair(JsonUtil.ORDER_ID, order_id),
									new BasicNameValuePair(JsonUtil.STATUS, "pay"),
									new BasicNameValuePair(JsonUtil.PAY_TYPE, pay_type));
						} catch (IOException e) {
							e.printStackTrace();
							result = e.getMessage();
						}
						Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_UPDATEORDERSTATUS,
							new BasicNameValuePair(JsonUtil.ORDER_ID,order_id),
							new BasicNameValuePair(JsonUtil.STATUS, "pay"),
							new BasicNameValuePair(JsonUtil.PAY_TYPE, pay_type)));	
				      Message msg=new Message();
				      msg.what=4;
				      msg.obj=result;
				      mHandler.sendMessage(msg);
					}
				});
			break;
			case 4:
				String result3=msg.obj.toString();
    			Log.e("hjq", "result3="+result3);
    			closeLoadingDialog();
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
			}

    		
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_pay);
		mDeal=(Deal)getIntent().getSerializableExtra("Deal");
		order_id=getIntent().getIntExtra(JsonUtil.ORDER_ID,0)+"";
		group_price=getIntent().getStringExtra(JsonUtil.GROUP_PRICE);
		group_count=getIntent().getStringExtra(JsonUtil.GROUP_COUNT);
		total_price=getIntent().getStringExtra(JsonUtil.TOTAL_PRICE);
		orderidtv=(TextView)findViewById(R.id.order_id);
		pricetv=(TextView)findViewById(R.id.price);
		sum=(TextView)findViewById(R.id.group_count);
		sumpricetv=(TextView)findViewById(R.id.pricesum);
		zfbbtn=(Button)findViewById(R.id.zfb_btn);
		shibibtn=(Button)findViewById(R.id.shibi_btn);
		((TextView)findViewById(R.id.title)).setText(mDeal.getTitle());
		orderidtv.setText(order_id);
		pricetv.setText(group_price+"元");
		sum.setText(group_count+"份");
		sumpricetv.setText(total_price+"元");
		zfbbtn.setOnClickListener(this);
		shibibtn.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
        case R.id.back:
        	onBackPressed();
        	break;
        case R.id.zfb_btn:
        	
			String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
			AlipayUtil alipayUtil = new AlipayUtil(ShopDealPayActivity.this, mHandler, "掌上餐厅菜品支付", "无",total_price,order_id, url);
			alipayUtil.doAlipay();
        	break;
        case R.id.shibi_btn:
        	String cur = PreferenceUtil.getInstance(this)
			.getString(JsonUtil.SHIBI, "0");
	Double mycoin = Double.parseDouble(cur);
	final Double curcoin =Double.parseDouble(total_price);
	Log.e("hjq", "mycoin="+mycoin+",curcoin="+curcoin);
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
					ShopDealPayActivity.this).getUid();
			String shibi = String.valueOf(curcoin);
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
		default:
			break;
		}
	}


}
