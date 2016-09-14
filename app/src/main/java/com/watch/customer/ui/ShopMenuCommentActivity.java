package com.watch.customer.ui;

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
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;
import com.watch.customer.model.OrderDeal;
import com.watch.customer.model.OrderItem;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class ShopMenuCommentActivity extends BaseActivity implements
		OnClickListener {
    private Button submitbtn;
    private TextView codetv;
    private EditText contented;
    private RatingBar mRating;
    private String contentstr=""; 
    private String order_id=""; 
    private String store_id=""; 
    private OrderDeal mdeal;
    private OrderItem mItem;
    private String rating;
    private Handler mHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		String result=msg.obj.toString();
    		closeLoadingDialog();
    		Log.e("hjq", result);
    		try {
				JSONObject json=new JSONObject(result);
				if (json.getInt(JsonUtil.CODE)==1) {
					showLongToast("评价成功");
					finish();
					MyApplication.getInstance().type=1;
					startActivity(new Intent(ShopMenuCommentActivity.this, MainActivity.class));
				}else {
					showLongToast(json.getString(JsonUtil.MSG));
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
		setContentView(R.layout.activity_shop_menu_comment);
		mdeal=(OrderDeal)getIntent().getSerializableExtra("orderdeal");
		submitbtn=(Button)findViewById(R.id.submit);
		codetv=(TextView)findViewById(R.id.code);
		contented=(EditText)findViewById(R.id.feedback_content);
		mRating=(RatingBar)findViewById(R.id.feedback_rating);
		if (mdeal!=null) {
			order_id=mdeal.getOrder_id();
			store_id=mdeal.getStore_id();
		}else {
			mItem=(OrderItem)getIntent().getSerializableExtra("orderitem");
			order_id=mItem.getOrder_id();
			store_id=mItem.getStore_id();
		}
		codetv.setText(order_id);
		findViewById(R.id.back).setOnClickListener(this);
		submitbtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		  case R.id.back:
			  onBackPressed();
			  break;
          case R.id.submit:
        	 if (checkData()) {
        		 showLoadingDialog();
				ThreadPoolManager.getInstance().addTask(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String user_id=PreferenceUtil.getInstance(ShopMenuCommentActivity.this).getUid();
						String content=contentstr;
						String sorce=rating;
						Log.e("hjq", HttpUtil.getURlStr(HttpUtil.URL_USERCOMMENT,
						new BasicNameValuePair(JsonUtil.ORDER_ID,order_id),
						new BasicNameValuePair(JsonUtil.USER_ID, user_id),
						new BasicNameValuePair(JsonUtil.CONTENT, content),
						new BasicNameValuePair(JsonUtil.SORCE, sorce),
						new BasicNameValuePair(JsonUtil.STORE_ID, store_id)));
						String result= null;
						try {
							result = HttpUtil.post(HttpUtil.URL_USERCOMMENT,
									new BasicNameValuePair(JsonUtil.ORDER_ID, order_id),
									new BasicNameValuePair(JsonUtil.USER_ID, user_id),
									new BasicNameValuePair(JsonUtil.CONTENT, content),
									new BasicNameValuePair(JsonUtil.SORCE, sorce),
									new BasicNameValuePair(JsonUtil.STORE_ID, store_id));
						} catch (IOException e) {
							e.printStackTrace();
							result = e.getMessage();
						}
						Message msg=new Message();
						msg.obj=result;
						mHandler.sendMessage(msg);
					}
				});
        		 
			}
        	  break;
 
		}
	}

	private boolean checkData() {
		// TODO Auto-generated method stub
		contentstr=contented.getText().toString().trim();
//		String relate=relateed.getText().toString().trim();
		rating=String.valueOf(mRating.getRating());
		if (contentstr.equals("")) {
			showShortToast("内容不能为空");
			return false;
		}
		if (rating.equals("")) {
			showShortToast("联系方式不能为空");
			return false;
		}
		if (order_id.equals("")) {
			showShortToast("没有发现订单号");
			return false;
		}
		return true;
	}

}
