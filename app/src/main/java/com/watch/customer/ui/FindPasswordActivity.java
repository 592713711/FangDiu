package com.watch.customer.ui;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uacent.watchapp.R;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.ThreadPoolManager;

public class FindPasswordActivity extends BaseActivity {
    private EditText phone;

    //	private EditText code;
//	private EditText password;
//	private EditText repassword;
    private Button getcode;
//	private Button save;

    private Timer mTimer;
    private String phonestr = "";
    private String codestr = "";
    private String passwordstr = "";
    private String repasswordstr = "";
    private final int save_what = 0;
    private final int getcode_what = 1;
    private final String TAG = FindPasswordActivity.class.getName();

    private Handler timerHandler = new Handler() {
        public void handleMessage(Message msg) {
            int num = msg.what;
            getcode.setText(num
                    + "秒");
            if (num == -1) {
                mTimer.cancel();
                getcode.setEnabled(true);
                getcode.setText(R.string.str_get_code);
            }
        }

        ;
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            closeLoadingDialog();
            String result = msg.obj.toString();
            Log.e(TAG, result);
            switch (msg.what) {
                case save_what: {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (JsonUtil.getInt(json, JsonUtil.CODE) == 0) {
                            showLongToast(JsonUtil.getStr(json, JsonUtil.MSG));
                        } else {
                            showLongToast(JsonUtil.getStr(json, JsonUtil.MSG));
                            finish();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

                case getcode_what: {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (!JsonUtil.getStr(json, JsonUtil.MSG).equals("success")) {
                            showLongToast(JsonUtil.getStr(json, JsonUtil.MSG));
                            getcode.setEnabled(true);
                            getcode.setText(R.string.register_button_code);
                        } else {
                            codestr = String.valueOf(JsonUtil.getInt(json, JsonUtil.CODE));
                            showLongToast("密码已发送，注意查收");
                            mTimer = new Timer();
                            mTimer.schedule(new TimerTask() {
                                int num = 90;

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Message msg = new Message();
                                    msg.what = num;
                                    timerHandler.sendMessage(msg);
                                    num--;
                                }
                            }, new Date(), 1000);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);

        phone = (EditText) findViewById(R.id.phone);
//		code = (EditText)findViewById(R.id.code);
//		password = (EditText)findViewById(R.id.password);
//		repassword = (EditText)findViewById(R.id.repassword);
        getcode = (Button) findViewById(R.id.getcode);
//		save = (Button)findViewById(R.id.save);
        findViewById(R.id.back).setOnClickListener(this);
        getcode.setOnClickListener(this);
//		save.setOnClickListener(this);
        phone.setText(getLocalPhoneNumber());
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back: {
                onBackPressed();

                break;
            }
            case R.id.getcode: {

//            phonestr = phone.getText().toString().trim();
//            if (phone.equals("")) {
//                showShortToast("手机号码不能为空");
//                return;
//            }
                if (checkdata()) {
                    getcode.setText("正在发送");
                    getcode.setEnabled(false);
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            String result = null;
                            try {
                                result = HttpUtil.get(HttpUtil.URL_FORGETPASSWORD
                                        + "?mobile=" + phonestr);
                            } catch (IOException e) {
                                e.printStackTrace();
                                result = e.getMessage();
                            }

                            Message msg = new Message();
                            msg.what = getcode_what;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    });
                }
                break;
            }

            case R.id.save: {
                if (checkdata()) {
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            String result = null;
                            try {
                                result = HttpUtil.post(HttpUtil.URL_FORGETPASSWORD,
                                        new BasicNameValuePair(JsonUtil.PHONE, phonestr),
                                        new BasicNameValuePair(JsonUtil.PASSWORD, passwordstr));
                            } catch (IOException e) {
                                e.printStackTrace();
                                result = e.getMessage();
                            }
                            Message msg = new Message();
                            msg.what = save_what;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    });
                    showLoadingDialog();
                }

                break;
            }

            default:
                break;
        }
    }

    private boolean checkdata() {
        phonestr = phone.getText().toString().trim();
//		String codestr1 = code.getText().toString().trim();
//		passwordstr = password.getText().toString().trim();
//		repasswordstr = repassword.getText().toString().trim();

        // +86
        if (phonestr.length() == 14) {
            phonestr = phonestr.substring(3);
        }

        if (phonestr.equals("")) {
            showLongToast("电话号码不能为空");
            return false;
        } else if (phonestr.length() != 11) {
            showLongToast("手机号码必须为11位");
            return false;
        }

        return true;

//		else if (codestr1.equals("")) {
//			showLongToast("验证码不能为空");
//			return false;
//		}
//        else if (passwordstr.equals("")) {
//            showLongToast("密码不能为空");
//            return false;
//        } else if (repasswordstr.equals("")) {
//            showLongToast("确认密码不能为空");
//            return false;
//        } else if (!passwordstr.equals(repasswordstr)) {
//            showLongToast("两次输入的密码必须一致");
//            return false;
//        } else if (passwordstr.length() < 6 && passwordstr.length() > 16) {
//            showLongToast("密码长度为6到16位");
//            return false;
//        }
//		else if (!codestr.equals(codestr1)) {
//			showLongToast("验证码不正确");
//			return false;
//		}

 //       return true;
    }

    private String getLocalPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneId = tm.getLine1Number();
        return phoneId;
    }
}
