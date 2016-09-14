package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.CoinListAdapter;
import com.watch.customer.alipay.AlipayUtil;
import com.watch.customer.alipay.Result;
import com.watch.customer.dao.UserDao;
import com.watch.customer.model.Coin;
import com.watch.customer.model.User;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class PersonCoinActivity extends BaseActivity {
	private TextView total;
	private Button recharge;
	private ImageView head_image;
	private ListView listview;
	private ArrayList<Coin> mCoins=new ArrayList<Coin>();
	private CoinListAdapter mListAdapter;
	private final int getcoindetail_what=0;
	private final int getmycoin_what=1;
	private final int addcoin_what=2;
	private String money="0";
	private Runnable getmycoinRun,getcoindetailRun,addcoinRun;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			String result=msg.obj.toString();
			Log.e("hjq", result);
			switch (msg.what) {
			case getcoindetail_what:
				try {
					JSONArray array=new JSONArray(result);
					mCoins.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject json=array.getJSONObject(i);
						String id=json.getString(JsonUtil.ID);
						String shibi=json.getString(JsonUtil.SHIBI);
						String user_id=json.getString(JsonUtil.USER_ID);
						String create_time=json.getString(JsonUtil.CREATE_TIME);
						String type=json.getString(JsonUtil.TYPE);
						mCoins.add(new Coin(id, shibi, user_id, create_time, type));
					}
					mListAdapter=new CoinListAdapter(PersonCoinActivity.this, mCoins);
				    listview.setAdapter(mListAdapter);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				break;
            case getmycoin_what:
            	if (result.trim().equals("null")) {
					return ;
				}
				try {
					JSONObject mycoinobj=new JSONObject(result);
					String shibi=mycoinobj.getString(JsonUtil.SHIBI);
					total.setText(shibi);
					PreferenceUtil.getInstance(PersonCoinActivity.this).setString(PreferenceUtil.SHIBI, shibi);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				break;
            case addcoin_what:
            	closeLoadingDialog();
            	if (result.trim().equals("null")) {
					return ;
				}
				try {
					JSONObject json=new JSONObject(result);
					if (json.getInt(JsonUtil.CODE)==1) {
						ThreadPoolManager.getInstance().addTask(getmycoinRun);
						ThreadPoolManager.getInstance().addTask(getcoindetailRun);
						showShortToast("提交数据成功");
						
					}else {
						showShortToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;

    		case AlipayUtil.RQF_PAY:
				Result result1 = new Result((String) msg.obj);
				if (result1.isPaySuccess()) {
					Toast.makeText(PersonCoinActivity.this, "支付成功!", 1).show();
					showLoadingDialog("正在提交数据...");
					ThreadPoolManager.getInstance().addTask(addcoinRun);
				} else {
					Toast.makeText(PersonCoinActivity.this, "支付出现错误", 1).show();
				}
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_mycoin);
		total=(TextView)findViewById(R.id.personmycoinTextCoin);
		recharge=(Button)findViewById(R.id.personmycoinButtonDeposit);
		head_image=(ImageView)findViewById(R.id.head_image);
		listview=(ListView)findViewById(R.id.personmycoinList);
		recharge.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		initRun();
		User mUser=new UserDao(this).queryById(PreferenceUtil.getInstance(this).getUid());
		if (mUser.getImage().equals("")) {
			head_image.setImageResource(R.drawable.null_user);
		}else {
			ImageLoaderUtil.displayImage(HttpUtil.SERVER+mUser.getImage_thumb(), head_image, this);
		}
		
	}
	private void initRun() {
		// TODO Auto-generated method stub
		getcoindetailRun=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_GETSHIBIBYUSERID,
							new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(PersonCoinActivity.this).getUid()));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
			msg.obj=result;
			msg.what=getcoindetail_what;
			mHandler.sendMessage(msg);
			}
		};
		getmycoinRun=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_TOTALSHIBIBYUSERID,
							new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(PersonCoinActivity.this).getUid()));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
			msg.obj=result;
			msg.what=getmycoin_what;
			mHandler.sendMessage(msg);
			}
		};
		addcoinRun=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result= null;
				try {
					result = HttpUtil.post(HttpUtil.URL_ADDSHIBIBYUSERID,
							new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(PersonCoinActivity.this).getUid()),
							new BasicNameValuePair(JsonUtil.SHIBI, money));
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				}
				Message msg=new Message();
			msg.obj=result;
			msg.what=addcoin_what;
			mHandler.sendMessage(msg);
			}
		};
		ThreadPoolManager.getInstance().addTask(getcoindetailRun);
		ThreadPoolManager.getInstance().addTask(getmycoinRun);
//		ThreadPoolManager.getInstance().addTask(addcoinRun);
//		ThreadPoolManager.getInstance().addTask(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//			String result=HttpUtil.post(HttpUtil.URL_ADDSHIBIBYUSERID,
//						new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(PersonCoinActivity.this).getUid()),
//						new BasicNameValuePair(JsonUtil.SHIBI,"1000"));
//			Message msg=new Message();
//			msg.obj=result;
//			msg.what=addcoin_what;
//			mHandler.sendMessage(msg);
//			}
//		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.personmycoinButtonDeposit:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle("食币充值");
			View parentView=getLayoutInflater().inflate(R.layout.coin_edittext, null);
            final EditText editText=(EditText)parentView.findViewById(R.id.coinedit);
            builder.setView(parentView);
            builder.setNegativeButton(R.string.system_sure, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					 money=editText.getText().toString().trim();
					if (money.equals("")) {
						showShortToast("充值金额不能为空");
						return;
					}else if (Integer.parseInt(money)==0) {
						showShortToast("充值金额不能为零");
						return;
					}else {
						String url = "http://114.215.180.179:8800/zsdc/main/payMainController/shibiPay.do";
						AlipayUtil alipayUtil = new AlipayUtil(PersonCoinActivity.this, mHandler,"食币充值", "无",money, PreferenceUtil.getInstance(PersonCoinActivity.this).getUid(), url);
						alipayUtil.doAlipay();
					}
					
				}
			}).setPositiveButton(R.string.system_cancel, null).create().show();
			break;

		default:
			break;
		}
	}
}
