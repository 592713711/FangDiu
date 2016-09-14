package com.watch.customer.ui;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.app.MyApplication;
import com.watch.customer.dao.UserDao;
import com.watch.customer.model.Deal;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class ShopDealResultActivity extends BaseActivity implements OnClickListener {
    private TextView pricetv;
    private ImageView sub;
    private TextView sum;
    private ImageView add;
    private TextView sumpricetv;
    private TextView phonetv;
    private LinearLayout phonelin;
    private Button zfbbtn;
    private ImageView orderlist;
    private Button shibibtn;
    private Button surebtn;
    private Button cancelbtn;
    private int num=1;
    private double price;
    private Deal mDeal;
    private int orderid;
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		closeLoadingDialog();
    		switch (msg.what) {
			case 1:
	    		try {
	    			String result=msg.obj.toString();
	    			Log.e("hjq", result);
					JSONObject json=new JSONObject(result);
					closeLoadingDialog();
					if (json.getString(JsonUtil.CODE).equals("1")) {
						orderid=json.getInt(JsonUtil.ORDER_ID);
						showLongToast("成功提交订单");
						Intent mIntent=new Intent(ShopDealResultActivity.this, ShopDealPayActivity.class);
						mIntent.putExtra("Deal", mDeal);
						mIntent.putExtra(JsonUtil.ORDER_ID, orderid);
						mIntent.putExtra(JsonUtil.GROUP_PRICE, price+"");
						mIntent.putExtra(JsonUtil.GROUP_COUNT, num+"");
						mIntent.putExtra(JsonUtil.TOTAL_PRICE,json.getString(JsonUtil.TOTAL_PRICE));
						startActivity(mIntent);
					}else {
						showLongToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			case AlipayUtil.RQF_PAY:
				Result result = new Result((String) msg.obj);
				if (result.isPaySuccess()) {
					Toast.makeText(ShopDealResultActivity.this, "支付成功!", 1).show();
					finish();
				} else {
					Toast.makeText(ShopDealResultActivity.this, "支付出现错误,请到订单列表重新支付", 1).show();
				}
				break;
			}

    		
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_result);
		mDeal=(Deal)getIntent().getSerializableExtra("deal");
		price=Double.parseDouble(mDeal.getGroup_price());
		pricetv=(TextView)findViewById(R.id.price);
		sub=(ImageView)findViewById(R.id.sub);
		sum=(TextView)findViewById(R.id.menu_num);
		add=(ImageView)findViewById(R.id.add);
		sumpricetv=(TextView)findViewById(R.id.pricesum);
		phonetv=(TextView)findViewById(R.id.phone);
		phonelin=(LinearLayout)findViewById(R.id.phonelin);
		zfbbtn=(Button)findViewById(R.id.zfb_btn);
		orderlist=(ImageView)findViewById(R.id.orderlist);
		shibibtn=(Button)findViewById(R.id.shibi_btn);
		surebtn=(Button)findViewById(R.id.sure);
		cancelbtn=(Button)findViewById(R.id.cancel);
		((TextView)findViewById(R.id.title)).setText(mDeal.getTitle());
		pricetv.setText(mDeal.getGroup_price()+"元");
		sumpricetv.setText(mDeal.getGroup_price()+"元");
		phonetv.setText(new UserDao(this).queryAll().get(0).getPhone());
		sub.setOnClickListener(this);
		add.setOnClickListener(this);
		phonelin.setOnClickListener(this);
		zfbbtn.setOnClickListener(this);
		shibibtn.setOnClickListener(this);
		surebtn.setOnClickListener(this);
		cancelbtn.setOnClickListener(this);
		orderlist.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
        case R.id.back:
        	onBackPressed();
        	break;
        case R.id.sub:
        	if (num!=1) {
				num--;
				sum.setText(String.valueOf(num));
				sumpricetv.setText(CommonUtil.DouToStr1(num*price)+"元");
			}
        	break;
        case R.id.add:
			num++;
			sum.setText(String.valueOf(num));
			sumpricetv.setText(CommonUtil.DouToStr1(num*price)+"元");
        	break;
        case R.id.cancel:
        	findViewById(R.id.linepop).setVisibility(View.GONE);
        	surebtn.setVisibility(View.VISIBLE);
        	break;
        case R.id.sure:
        	if (num>0) {
        		if (phonetv.getText().toString().trim().equals("")) {
					showShortToast("联系电话不能为空");
        			return;
				}
        		showLoadingDialog("正在提交订单");
				ThreadPoolManager.getInstance().addTask(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String store_id=mDeal.getStore_id();
						String count=String.valueOf(num);
						String group_id=mDeal.getId();
						String total_price=String.valueOf(num*price);
						String phone=phonetv.getText().toString();
						String user_id=PreferenceUtil.getInstance(ShopDealResultActivity.this).getUid();
						Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_SUBMITGROUPORDER,
								new BasicNameValuePair(JsonUtil.STORE_ID, store_id),
								new BasicNameValuePair(JsonUtil.COUNT, count),
								new BasicNameValuePair(JsonUtil.GROUP_ID,group_id),
								new BasicNameValuePair(JsonUtil.PHONE,phone),
								new BasicNameValuePair(JsonUtil.TOTAL_PRICE,total_price),
								new BasicNameValuePair(JsonUtil.USER_ID,user_id)));
						String result= null;
						try {
							result = HttpUtil.post(HttpUtil.URL_SUBMITGROUPORDER,
									new BasicNameValuePair(JsonUtil.STORE_ID, store_id),
									new BasicNameValuePair(JsonUtil.COUNT, count),
									new BasicNameValuePair(JsonUtil.GROUP_ID, group_id),
									new BasicNameValuePair(JsonUtil.PHONE, phone),
									new BasicNameValuePair(JsonUtil.TOTAL_PRICE, total_price),
									new BasicNameValuePair(JsonUtil.USER_ID, user_id));
						} catch (IOException e) {
							e.printStackTrace();
							result = e.getMessage();
						}
						Message msg=new Message();
						msg.obj=result;
						msg.what=1;
						mHandler.sendMessage(msg);
					}
				});
			}else {
				showShortToast("请先选择数量");
			}
          
        	break;
        case R.id.zfb_btn:
        	
			String url = "http://114.215.180.179:8800/zsdc/main/payMainController/alipayPay.do";
			AlipayUtil alipayUtil = new AlipayUtil(ShopDealResultActivity.this, mHandler, "掌上餐厅菜品支付", "无",CommonUtil.DouToStr1(Double.parseDouble(mDeal.getGroup_price())*num),orderid+"", url);
			alipayUtil.doAlipay();
        	break;
        case R.id.shibi_btn:
        	String cur = PreferenceUtil.getInstance(this)
			.getString(JsonUtil.SHIBI, "0");
	Double mycoin = Double.parseDouble(cur);
	final Double curcoin =num*price;
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
					ShopDealResultActivity.this).getUid();
			String shibi = String.valueOf(curcoin);
			String result = null;
			try {
				result = HttpUtil
						.post(HttpUtil.URL_USESHIBIPAY,
								new BasicNameValuePair(JsonUtil.USER_ID,
										user_id), new BasicNameValuePair(
										JsonUtil.SHIBI, shibi),
								new BasicNameValuePair(JsonUtil.ORDER_ID,
										orderid + ""));
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
        case R.id.phonelin:
        	   final EditText inputServer = new EditText(this);
        	   inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
               AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
               builder1.setTitle("修改订单联系电话").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                       .setNegativeButton(R.string.system_cancel, null);
               builder1.setPositiveButton(R.string.system_sure, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                      phonetv.setText(inputServer.getText().toString());
                    }
               });
               builder1.show();
        	break;
        case R.id.orderlist:
        	Intent mIntent=new Intent(MainActivity.ACTION_TAB);
			mIntent.putExtra("index", 2);
			MyApplication.getInstance().orderindex=2;
			sendBroadcast(mIntent);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        	break;
		default:
			break;
		}
	}


}
