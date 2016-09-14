package com.watch.customer.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.CommentAdapter;
import com.watch.customer.model.Comment;
import com.watch.customer.model.Shop;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.ThreadPoolManager;

public class ShopCommentActivity extends BaseActivity {
	private Shop mShop;
	private ListView commentlist;
	private CommentAdapter mAdapter;
	private ArrayList<Comment> listData = new ArrayList<Comment>();
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = msg.obj.toString();
			closeLoadingDialog();
			Log.e("hjq", result);
			if (result.trim().equals("null")) {
				showShortToast("没有发现评价数据");
				return;
			}
			try {
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
					listData.add(new Comment(id, order_id, user_id, content,
							sorce, store_id, comment_time, people, is_room,
							order_time, order_type, phone, status, total_price,
							user_name));
				}
				mAdapter = new CommentAdapter(ShopCommentActivity.this,
						listData);
				commentlist.setAdapter(mAdapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_comment);
		mShop = (Shop) getIntent().getSerializableExtra("object");
		findViewById(R.id.back).setOnClickListener(this);
		commentlist = (ListView) findViewById(R.id.commentlist);
		showLoadingDialog();
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

		default:
			break;
		}
	}
}
