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
import com.watch.customer.dao.UserDao;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import java.io.IOException;

public class PersonUpdatePasswordActivity extends BaseActivity {
	private EditText passwordEdit;
	private EditText repasswordEdit;
	private Button savebtn;
	private String uid ="";
	private String oldpassword = "";
	private String passwordstr ;
	private String repasswordstr ;
    private Handler mHandler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		String result = msg.obj.toString();
    		Log.e("hjq", result);
			try {
				JSONObject json = new JSONObject(result);
				if (JsonUtil.getInt(json, JsonUtil.CODE) == 1) {
					showLongToast("修改密码成功");
					new UserDao(PersonUpdatePasswordActivity.this).updatePassWordById(passwordstr, uid);
					finish();
				} else {
					showLongToast(JsonUtil.getStr(json, JsonUtil.MSG));
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
		setContentView(R.layout.activity_person_updatepassword);
		passwordEdit = (EditText) findViewById(R.id.password);
		repasswordEdit = (EditText) findViewById(R.id.repassword);
		savebtn = (Button) findViewById(R.id.save);
		savebtn.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		uid = PreferenceUtil.getInstance(this).getUid();
		oldpassword = new UserDao(this).queryById(uid).getPassword();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.save:
			 passwordstr = passwordEdit.getText().toString().trim();
			repasswordstr = repasswordEdit.getText().toString().trim();
			if (passwordstr.equals("")) {
				showLongToast("密码不能为空");
				return;
			}
			if (!passwordstr.equals(repasswordstr)) {
				showLongToast("两次输入的密码必须一致");
				return;
			}
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String result= null;
					try {
						result = HttpUtil.post(HttpUtil.URL_RESETPASSWORD,
								new BasicNameValuePair(JsonUtil.USER_ID, uid),
								new BasicNameValuePair("oldpass", oldpassword),
								new BasicNameValuePair("newpass", passwordstr),
								new BasicNameValuePair("repassword", repasswordstr));
					} catch (IOException e) {
						e.printStackTrace();
						result = e.getMessage();
					}
					Message msg = new Message();
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			});
			break;

		default:
			break;
		}
	}
}
