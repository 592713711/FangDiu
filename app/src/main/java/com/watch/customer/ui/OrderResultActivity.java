package com.watch.customer.ui;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.MenuAdapter;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.app.MyApplication;
import com.watch.customer.http.CustomHttpUtil;
import com.watch.customer.model.Menu;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class OrderResultActivity extends BaseActivity implements OnClickListener {
	private ArrayList<Menu> menulistdata = new ArrayList<Menu>();
	private ListView orderdetailList = null;
	private TextView orderidtv;
	private TextView createtimetv;
	private TextView pricesumtv;
//	private Button zfbbtn;
//	private Button shibibtn;
	private Button mylist_btn;
    private String order_id;
    private String create_time;
    private String price_sum;
    private String type;
    private double pricesum=0.0;
    private Shop mShop;
    public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AlipayUtil.RQF_PAY:
				Result result = new Result((String) msg.obj);
				if (result.isPaySuccess()) {
					Toast.makeText(OrderResultActivity.this, "支付成功!", 1).show();
					finish();
				} else {
					Toast.makeText(OrderResultActivity.this, "支付出现错误", 1)
							.show();
				}
				break;
			case 2:
				String result1 = msg.obj.toString();
				Log.e("hjq", result1);
				closeLoadingDialog();
				try {
					JSONObject json = new JSONObject(result1);
					if (json.getInt(JsonUtil.CODE) == 1) {
						showLongToast(json.getString(JsonUtil.MSG));
						finish();
					} else {
						showLongToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		Intent myIntent=getIntent();
		menulistdata=(ArrayList<Menu>)myIntent.getSerializableExtra("disheslist");
		mShop=(Shop)myIntent.getSerializableExtra("object");
		create_time=myIntent.getStringExtra("create_time");
		order_id=myIntent.getStringExtra("order_id");
		type=myIntent.getStringExtra("type");
		orderdetailList = (ListView) findViewById(R.id.orderdetailList);
//		zfbbtn = (Button) findViewById(R.id.zfb_btn);
//		shibibtn = (Button) findViewById(R.id.shibi_btn);
		mylist_btn = (Button) findViewById(R.id.orderlist_btn);
		orderidtv= (TextView) findViewById(R.id.orderidtv);
		createtimetv= (TextView) findViewById(R.id.createtimetv);
		pricesumtv= (TextView) findViewById(R.id.pricesumtv);
		findViewById(R.id.back).setOnClickListener(this);
		((TextView)findViewById(R.id.title)).setText(mShop.getName());;
//		zfbbtn.setOnClickListener(this);
//		shibibtn.setOnClickListener(this);	
		mylist_btn.setOnClickListener(this);	
		orderidtv.setText(order_id);	
		Log.e("hjq", "create_time="+create_time);
		createtimetv.setText(create_time.substring(0,create_time.length()-2));
		for (int i = 0; i < menulistdata.size(); i++) {
			pricesum=pricesum+menulistdata.get(i).getPrice();
		}
		DecimalFormat deformat=new DecimalFormat("0.0");
		pricesum=Double.parseDouble(deformat.format(pricesum));
		pricesumtv.setText(getString(R.string.system_rmb)+pricesum+"");
        MenuAdapter adapter = new MenuAdapter(this, menulistdata);
		orderdetailList.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.orderlist_btn:
			MyApplication.getInstance().type=2;
			if (type.equals("schedule")) {
				MyApplication.getInstance().orderindex=0;	
			}else if (type.equals("point")) {
				MyApplication.getInstance().orderindex=1;
			}
			Log.e("hjq", "orderindex111="+MyApplication.getInstance().orderindex);
			startActivity(new Intent(this, MainActivity.class));
			break;
		case R.id.zfb_btn:
			String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
			AlipayUtil alipayUtil = new AlipayUtil(OrderResultActivity.this, mHandler, "掌上餐厅菜品支付", "无", price_sum,order_id, url);
			alipayUtil.doAlipay();
//			Intent mIntent=new Intent(this, ShopMenuResultActivity.class);
//			mIntent.putExtra("object", getIntent().getSerializableExtra("object"));
//			mIntent.putExtra("create_time", create_time);
//			mIntent.putExtra("order_id", orderidtv.getText().toString().trim());
//			mIntent.putExtra("personnum",getIntent().getStringExtra("personnum") );
//			startActivity(mIntent);
			break;
		case R.id.shibi_btn:
			String cur = PreferenceUtil.getInstance(OrderResultActivity.this)
			.getString(JsonUtil.SHIBI, "0");
	Double mycoin = Double.parseDouble(cur);
	Double curcoin =pricesum;
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
					OrderResultActivity.this).getUid();
			String shibi = String.valueOf(pricesum);
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
	private void sendRequestForCoinPay() {
		String url = "http://114.215.180.179:8800/zsdc/main/scoreMainController/pay.do";
		JSONObject jsonObject = new JSONObject();
		StringEntity stringEntity = null;
		try {

			jsonObject.put("orderid", order_id);
			jsonObject.put("userid", PreferenceUtil.getInstance(OrderResultActivity.this).getUid());
			stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		CustomHttpUtil.post(OrderResultActivity.this, url, stringEntity, "application/json", new com.watch.customer.http.JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
//				loadUtil.loading();
				Log.e("hjq", "onStart");
			}

			@Override
			public void onFinish() {
				super.onFinish();
				Log.e("hjq", "onFinish");
//				loadUtil.closeLoad();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.e("hjq", "onSuccess");
				if (response != null) {
					int status = response.optInt("status");
					String message = response.optString("message");
					Log.e("hjq", "status="+status+",message="+message);
					if (status == 200) {
						mHandler.sendEmptyMessage(2);
					} else {
						mHandler.sendEmptyMessage(2);
					}
				} else {
					mHandler.sendEmptyMessage(4);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				Toast.makeText(OrderResultActivity.this, "请求失败!", 1).show();
			}
		});
	}

	
}
