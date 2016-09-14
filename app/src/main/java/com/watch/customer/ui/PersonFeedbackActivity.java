package com.watch.customer.ui;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uacent.watchapp.R;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class PersonFeedbackActivity extends BaseActivity {
	private EditText contentEdit;
	private EditText relateEdit;
	private Button submit;
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			String result=msg.obj.toString();
			Log.e("hjq", result);
			closeLoadingDialog();
			try {
				JSONObject json=new JSONObject(result);
				if (json.getInt(JsonUtil.CODE)==1) {
					showLongToast("发送成功");
					finish();
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_feedback);
		contentEdit=(EditText)findViewById(R.id.feedback_content);
		relateEdit=(EditText)findViewById(R.id.feedback_relate);
		submit=(Button)findViewById(R.id.submit);
		findViewById(R.id.back).setOnClickListener(this);
		submit.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.submit:
			final String contentstr=contentEdit.getText().toString().trim();
			if (contentstr.equals("")) {
				showLongToast("反馈内容不能为空");
				return ;
			}
			showLoadingDialog();
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String result= null;
					try {
						result = HttpUtil.post(HttpUtil.URL_USERFEEDBACK,
								new BasicNameValuePair(JsonUtil.USER_ID, PreferenceUtil.getInstance(PersonFeedbackActivity.this).getUid()),
								new BasicNameValuePair(JsonUtil.CONTENT, contentstr));
					} catch (IOException e) {
						e.printStackTrace();
						result = e.getMessage();
					}
					Message msg=new Message();
					msg.obj=result;
					mHandler.sendMessage(msg);
				}
			});
			break;
		default:
			break;
		}
	}
}
