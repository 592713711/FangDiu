package com.watch.customer.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.uacent.watchapp.BuildConfig;
import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;
import com.watch.customer.dao.UserDao;
import com.watch.customer.model.User;
import com.watch.customer.util.DialogUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import cn.jpush.android.api.JPushInterface;

public class FirstActivity extends BaseActivity {
	private String phone;
	private String password;
	private final String TAG = "hjq";
    private final int MSG_LOGIN = 0;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = msg.obj.toString();
			closeLoadingDialog();
			Log.e(TAG, result);

			if (result.matches("Connection to .* refused") || result.matches("Connect to.*timed out")) {
				showLongToast(getString(R.string.network_error));
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.i(TAG, "postDelayed2");
						//finish();

                        DialogUtil.showDialog(FirstActivity.this, getString(R.string.str_network_error),
                               getString(R.string.str_network_prompt),
                                getString(R.string.system_sure),
                                null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                       finish();
                                        startActivity(new Intent(FirstActivity.this, MainActivity.class));
                                    }
                                }, null, true);
					}
				}, 1000);
				return;
			}

			switch (msg.what) {
				case MSG_LOGIN: {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (JsonUtil.getInt(json, JsonUtil.CODE) != 1) {
                            showLongToast(JsonUtil.getStr(json, JsonUtil.MSG));
                            startActivity(new Intent(FirstActivity.this, AuthLoginActivity.class));
                        } else {
                            JSONObject msgobj = json.getJSONObject("msg");
                            String token = msgobj.getString("token");

                            JSONObject userobj = json.getJSONObject("user");
                            String id = userobj.getString(JsonUtil.ID);
                            String name = userobj.getString(JsonUtil.NAME);
                            String phone = userobj.getString(JsonUtil.PHONE);
                            String sex = userobj.getString(JsonUtil.SEX);
                         //   String password = userobj.getString(JsonUtil.PASSWORD);
                            String create_time = userobj.getString(JsonUtil.CREATE_TIME);

							String image_thumb = null;
							String image = null;
							try {
								image_thumb = userobj.getString(JsonUtil.IMAGE_THUMB);
								image = userobj.getString(JsonUtil.IMAGE);
							} catch (Exception e)
							{
								e.printStackTrace();
							}

                            User user = new User(id, name, phone, sex, password, create_time, image_thumb, image, token);
                            new UserDao(FirstActivity.this).insert(user);
                            showLongToast(getString(R.string.login_success));
                            PreferenceUtil.getInstance(FirstActivity.this).setUid(user.getId());
                            PreferenceUtil.getInstance(FirstActivity.this).getString(PreferenceUtil.PHONE, user.getPhone());
                            PreferenceUtil.getInstance(FirstActivity.this).setToken(user.getToken());
                            MyApplication.getInstance().mToken = user.getToken();
                            startActivity(new Intent(FirstActivity.this, MainActivity.class));
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
    public void onStart() {
        super.onStart();

    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Log.e("hjq", "all permissions are granted.");
                    if (!btAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else {
                        launchMainActivity();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(FirstActivity.this, R.string.str_permission_denied, Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }

                break;
            }

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private boolean checkMultiPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add(getString(R.string.str_gps));
        }

        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add(getString(R.string.str_camera));
        }

        if (!addPermission(permissionsList, Manifest.permission.BLUETOOTH)) {
            permissionsNeeded.add(getString(R.string.str_bluetooth));
        }

        if (permissionsList.size() == 0) {
            return false;
        }

        // Need Rationale
        String message = getString(R.string.str_permission_prompt) + permissionsNeeded.get(0);
        for (int i = 1; i < permissionsNeeded.size(); i++) {
            message = message + ", " + permissionsNeeded.get(i);
        }

        DialogUtil.showNoTitleDialog(FirstActivity.this, message, getString(R.string.system_sure), getString(R.string.system_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(FirstActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }, false);

        return true;
    }

    void launchMainActivity() {
        if (BuildConfig.oversea.equals("1")) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                   startActivity(new Intent(FirstActivity.this, MainActivity.class /*FlashActivity.class*/));
                //    startActivity(new Intent(FirstActivity.this, FlashActivity.class));
                }
            }, 3000);
        } else {
            ArrayList<User> list = new UserDao(this).queryAll();
            User user = null;

            if (list != null && !list.isEmpty()) {
                user = list.get(0);
            }

            Log.e("hjq", "user " + user);
            if (user != null) {
                phone = user.getPhone();
                password = user.getPassword();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //     showLoadingDialog();
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Log.e(TAG, "begin post");
                                String result = null;
                                try {
                                    result = HttpUtil.post(HttpUtil.URL_LOGIN,
                                            new BasicNameValuePair(JsonUtil.PHONE, phone),
                                            new BasicNameValuePair(JsonUtil.PASSWORD,
                                                    password));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    result = e.getMessage();
                                }
                                //       Log.e(TAG, "my result " + result);

                                Message msg = new Message();
                                msg.obj = result;
                                msg.what = MSG_LOGIN;
                                mHandler.sendMessage(msg);
                            }
                        });
                    }
                }, 3000);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "postDelayed");
                        finish();
                        startActivity(new Intent(FirstActivity.this, AuthLoginActivity.class));
                    }
                }, 3000);
            }
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_first);

        if (checkMultiPermissions()) {
            return;
        }

        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            launchMainActivity();
        }
	}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    launchMainActivity();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }

            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            // 具体的操作代码
            Log.e("hjq", "onBackPressed");

            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
