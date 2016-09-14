package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.model.Comment;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.ImageLoaderUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

public class ShopDetailActivity extends BaseActivity implements OnClickListener {
	private LinearLayout orderbtn;
	private LinearLayout menubtn;
	private LinearLayout dealbtn;
	private LinearLayout vipcardbtn;
	private LinearLayout paybtn;
	private TextView intro;
	private TextView comment_count;
	private TextView name;
	private TextView type;
	private TextView notice;
	private TextView address;
	private TextView phone;
	private ImageButton collect;
	private ImageView logo;
	private LinearLayout shopdetailmain;
	private LinearLayout phonebtn;
	private LinearLayout commentbtn;
	private LinearLayout mapbtn;
	private Shop mShop;
	private boolean ishouse=false;
	private static final int ishouse_what=0;
	private static final int addordel_what=1;
	private static final int comment_what=2;
	private ArrayList<Comment> commentData = new ArrayList<Comment>();
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		String result=msg.obj.toString();
    		switch (msg.what) {
			case ishouse_what:
				Log.e("hjq","ishouse_what="+result);
				try {
					JSONObject json=new JSONObject(result);
					if (json.getInt(JsonUtil.CODE)==1) {
						Log.e("hjq","CODE="+1);
						collect.setImageResource(R.drawable.collect_head_select);
						ishouse=true;
					}else {
						Log.e("hjq","CODE="+0);
						collect.setImageResource(R.drawable.collect_head);	
					    ishouse=false;
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			case addordel_what:
				Log.e("hjq","addordel_what="+result);
				try {
					JSONObject json=new JSONObject(result);
					if (json.getInt(JsonUtil.CODE)==1) {
						ishouse=!ishouse;
						if (ishouse) {
							showLongToast(json.getString(JsonUtil.MSG));
							collect.setImageResource(R.drawable.collect_head_select);
						}else {
							showLongToast(json.getString(JsonUtil.MSG));
							collect.setImageResource(R.drawable.collect_head);
						}
						
					}else {
						showLongToast(json.getString(JsonUtil.MSG));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;

			case comment_what:
				try {
					if (!result.trim().equals("null")) {
					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						JSONObject json = array.getJSONObject(i);
						String id = json.getString(JsonUtil.ID);
						String order_id = json.getString(JsonUtil.ORDER_ID);
						String user_id = json.getString(JsonUtil.USER_ID);
						String content = json.getString(JsonUtil.CONTENT);
						String sorce = json.getString(JsonUtil.SORCE);
						String store_id = json.getString(JsonUtil.STORE_ID);
						String comment_time = json.getString(JsonUtil.COMMENT_TIME);
						String people = json.getString(JsonUtil.PEOPLE);
						String is_room = json.getString(JsonUtil.IS_ROOM);
						String order_time = json.getString(JsonUtil.ORDER_TIME);
						;
						String order_type = json.getString(JsonUtil.ORDER_TYPE);
						;
						String phone = json.getString(JsonUtil.PHONE);
						String status = json.getString(JsonUtil.STATUS);
						String total_price = json.getString(JsonUtil.TOTAL_PRICE);
						String user_name = json.getString(JsonUtil.USER_NAME);
						commentData.add(new Comment(id, order_id, user_id, content,
								sorce, store_id, comment_time, people, is_room,
								order_time, order_type, phone, status, total_price,
								user_name));
					}
					}	
					comment_count.setText("共"+commentData.size()+"条评价");
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
		setContentView(R.layout.activity_shop_detail);
		initView();
		mShop = (Shop) getIntent().getSerializableExtra("object");
		name.setText(mShop.getName());
		type.setText(mShop.getAverage_buy()+"元/人");
		notice.setText("点餐"+mShop.getDiscount() + "折扣");
		address.setText(mShop.getAddress());
		phone.setText(mShop.getPhone().equals("")?"无":mShop.getPhone());
		ImageLoaderUtil.displayImage(
				HttpUtil.SERVER+ mShop.getImage_thumb(),
				logo, this);
		intro.setText(mShop.getIntro());
		if (mShop.getIs_schedule().equals("0")) {
			((ImageView) findViewById(R.id.orderimage))
					.setImageResource(R.drawable.order_unenable);
			orderbtn.setEnabled(false);
		}
		if (mShop.getIs_point().equals("0")) {
			((ImageView) findViewById(R.id.menuimage))
					.setImageResource(R.drawable.menu_unenable);
			menubtn.setEnabled(false);
		}
		if (mShop.getIs_group().equals("0")) {
			((ImageView) findViewById(R.id.dealimage))
					.setImageResource(R.drawable.deal_unenable);
			dealbtn.setEnabled(false);
		}
		if (mShop.getIs_card().equals("0")) {
			((ImageView) findViewById(R.id.vipimage))
					.setImageResource(R.drawable.vip_unenable);
			vipcardbtn.setEnabled(false);
		}
		if (mShop.getIs_pay().equals("0")) {
			((ImageView) findViewById(R.id.payimage))
					.setImageResource(R.drawable.pay_unenable);
			paybtn.setEnabled(false);
		}
		collect.setOnClickListener(this);
		if (!PreferenceUtil.getInstance(this).getUid().equals("")) {
			ThreadPoolManager.getInstance().addTask(new Runnable() {		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String store_id=mShop.getId();
					String user_id=PreferenceUtil.getInstance(ShopDetailActivity.this).getUid();
					Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_ISHOUSE, 
					new BasicNameValuePair(JsonUtil.STORE_ID,store_id),
					new BasicNameValuePair(JsonUtil.USER_ID,user_id)));
					String result= null;
					try {
						result = HttpUtil.post(HttpUtil.URL_ISHOUSE,
								new BasicNameValuePair(JsonUtil.STORE_ID, store_id),
								new BasicNameValuePair(JsonUtil.USER_ID, user_id));
					} catch (IOException e) {
						e.printStackTrace();
						result = e.getMessage();
					}
					Message msg=new Message();
					msg.obj=result;
					msg.what=ishouse_what;
					mHandler.sendMessage(msg);
				}
			});
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String result = null;
					try {
						result = HttpUtil
                                .post(HttpUtil.URL_STORECOMMENT,
										new BasicNameValuePair(JsonUtil.STORE_ID, mShop
												.getId()));
					} catch (IOException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.obj = result;
					msg.what=comment_what;
					mHandler.sendMessage(msg);

				}
			});
		}
	}

	private void initView() {
		orderbtn = (LinearLayout) findViewById(R.id.orderbtn);
		menubtn = (LinearLayout) findViewById(R.id.menubtn);
		dealbtn = (LinearLayout) findViewById(R.id.dealbtn);
		vipcardbtn = (LinearLayout) findViewById(R.id.vipbtn);
		paybtn = (LinearLayout) findViewById(R.id.paybtn);
		intro = (TextView) findViewById(R.id.intro);
		name = (TextView) findViewById(R.id.name);
		type = (TextView) findViewById(R.id.type);
		notice = (TextView) findViewById(R.id.notice);
		address = (TextView) findViewById(R.id.address);
		phone = (TextView) findViewById(R.id.phone);
		comment_count= (TextView) findViewById(R.id.comment_count);
		collect= (ImageButton) findViewById(R.id.collect);
		logo = (ImageView) findViewById(R.id.logo);
		shopdetailmain = (LinearLayout) findViewById(R.id.shopdetailmain);
		phonebtn = (LinearLayout) findViewById(R.id.shopdetailButtonPhone);
		commentbtn = (LinearLayout) findViewById(R.id.shopdetailButtonComment);
		mapbtn = (LinearLayout) findViewById(R.id.shopdetailButtonMap);
		findViewById(R.id.back).setOnClickListener(this);
		collect.setOnClickListener(this);
		orderbtn.setOnClickListener(this);
		menubtn.setOnClickListener(this);
		dealbtn.setOnClickListener(this);
		vipcardbtn.setOnClickListener(this);
		paybtn.setOnClickListener(this);
		shopdetailmain.setOnClickListener(this);
		phonebtn.setOnClickListener(this);
		commentbtn.setOnClickListener(this);
		mapbtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.collect:
			if (PreferenceUtil.getInstance(this).getUid().equals("")) {
				startActivity(new Intent(this,AuthLoginActivity.class));
				return ;
			}
			ThreadPoolManager.getInstance().addTask(new Runnable() {		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String store_id=mShop.getId();
					String user_id=PreferenceUtil.getInstance(ShopDetailActivity.this).getUid();
					Log.e("hjq", ""+ishouse);
					String type=ishouse?"delete":"add";
					Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_ADDORDELUSERHOUSE, 
					new BasicNameValuePair(JsonUtil.STORE_ID,store_id),
					new BasicNameValuePair(JsonUtil.USER_ID,user_id),
					new BasicNameValuePair(JsonUtil.TYPE,type)));
					String result= null;
					try {
						result = HttpUtil.post(HttpUtil.URL_ADDORDELUSERHOUSE,
								new BasicNameValuePair(JsonUtil.STORE_ID, store_id),
								new BasicNameValuePair(JsonUtil.USER_ID, user_id),
								new BasicNameValuePair(JsonUtil.TYPE, type));
					} catch (IOException e) {
						e.printStackTrace();
						result = e.getMessage();
					}
					Message msg=new Message();
					msg.obj=result;
					msg.what=addordel_what;
					mHandler.sendMessage(msg);
				}
			});
			break;
		case R.id.orderbtn:
				if (PreferenceUtil.getInstance(this).getUid().equals("")) {
				startActivity(new Intent(this,AuthLoginActivity.class));
				return ;
			}
			gotoActivity(ShopBookActivity.class);
			break;
		case R.id.menubtn:		
			if (PreferenceUtil.getInstance(this).getUid().equals("")) {
				startActivity(new Intent(this,AuthLoginActivity.class));
				return ;
			}
			gotoActivity(ShopMenuActivity.class);
			break;
		case R.id.dealbtn:	
			if (PreferenceUtil.getInstance(this).getUid().equals("")) {
				startActivity(new Intent(this,AuthLoginActivity.class));
				return ;
			}
			gotoActivity(ShopDealActivity.class);
			break;
		case R.id.vipbtn:
			gotoActivity(VipCardActivity.class);
			break;
		case R.id.paybtn:
//			gotoActivity(VipCardActivity.class);
			break;
		case R.id.shopdetailmain:
			gotoActivity(ShopDetailMsgActivity.class);
			break;
		case R.id.shopdetailButtonPhone:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String phonestr = ((TextView) v.findViewById(R.id.phone)).getText().toString();
			if (phonestr.equals("")) {
				return ;
			}
			builder.setTitle("电话").setMessage("拨号到"+phonestr)
			  .setPositiveButton(R.string.system_sure, new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setAction("android.intent.action.CALL");
					intent.setData(Uri.parse("tel:"+phonestr));
					startActivity(intent);
				}
			}).setNegativeButton(R.string.system_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			}).create().show();
			
			break;
		case R.id.shopdetailButtonComment:
			gotoActivity(ShopCommentActivity.class);
			break;
		case R.id.shopdetailButtonMap:
			gotoActivity(ShopMapActivity.class);
			break;

		default:
			break;
		}
	}

	private void gotoActivity(Class<?> cls) {
		Intent mIntent=new Intent(ShopDetailActivity.this,cls);
		mIntent.putExtra("object", mShop);
		mIntent.putExtra("ishouse", ishouse);
		startActivity(mIntent);
	}

}
