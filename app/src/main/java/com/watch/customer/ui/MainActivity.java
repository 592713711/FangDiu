package com.watch.customer.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.DialogUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.util.ThreadPoolManager;
import com.watch.customer.util.UpdateManager;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements LocationListener {
    public static final String TAG = "MainActivity";

    private TabHost mTabHost;
    private RadioGroup mTabButtonGroup;
//    public static final String TAB_MAIN = "SHOPLIST_ACTIVITY";
//    public static final String TAB_BOOK = "ORDER_ACTIVITY";
//    public static final String TAB_CATEGORY = "PERSON_ACTIVITY";

    public static final String TAB_DEVICE = "DEVICELIST_ACTIVITY";
    public static final String TAB_CAMERA = "CAMERA_ACTIVITY";
    public static final String TAB_LOCATION = "LOCATION_ACTIVITY";
    public static final String TAB_SETTING = "SETTING_ACTIVITY";
    public static final String TAB_INFO = "INFO_ACTIVITY";


    public static final String ACTION_TAB = "tabaction";
    public static final String MAP_SWITCH_ACTION = "MapSwtichAction";

    private RadioButton rButton1, rButton2, rButton3;
    private RadioButton rButtonDevice;
    private RadioButton rButtonCamera;
    private RadioButton rButtonLocation;
    private RadioButton rButtonSetting;
    private RadioButton rButtonInfo;

    public LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    public static boolean isForeground = false;

    Intent mLocIntent;
    TabHost.TabSpec mTabSpec;

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    private int mCurrentIndex = -1;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                String result = msg.obj.toString();
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt(JsonUtil.CODE) == 1) {
                        Log.i(TAG, "msg is = " + json.getString(JsonUtil.MSG));
                    } else {
                        final String path = json.getString(JsonUtil.PATH);
                        final String updatemsg = json.getString(JsonUtil.MSG);

                        DialogUtil.showDialog(MainActivity.this, "发现新版本！",
                                json.getString(JsonUtil.MSG) + "是否要更新？",
                                getString(R.string.system_sure),
                                getString(R.string.system_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        UpdateManager mUpdateManager = new UpdateManager(
                                                MainActivity.this,
                                                updatemsg,
                                                HttpUtil.SERVER + path);
                                        mUpdateManager.showDownloadDialog();
                                    }
                                }, null, true);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (msg.what == 1) {
                Bundle b = msg.getData();

                PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.LOCATION, b.getString("address"));
                PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.LAT, "" + b.getDouble("latitude"));
                PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.LON, "" + b.getDouble("longitude"));
                MyApplication.getInstance().islocation = 1;
                MyApplication.getInstance().latitude = b.getDouble("latitude");
                MyApplication.getInstance().longitude = b.getDouble("longitude");

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
        initView();

        MyApplication.getInstance().addActivity(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TAB);
        intentFilter.addAction(MAP_SWITCH_ACTION);
        registerReceiver(TabReceiver, intentFilter);
        registerReceiver(mBtReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        // map check
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        SharedPreferences mSharedPreferences = getSharedPreferences("watch_app_preference", 0);
        String map = mSharedPreferences.getString("map", "baidu");

        if ("google".equals(map)) {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        } else {
            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(myListener);

            LocationClientOption option = new LocationClientOption();
            // option.setLocationMode(LocationMode.Hight_Accuracy);

            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(50);
            mLocClient.setLocOption(option);
            mLocClient.start();

//            option.setCoorType("bd09ll");
//            option.setScanSpan(0);
//            option.setNeedDeviceDirect(true);
//            option.setIsNeedAddress(true);
//            mLocClient.setLocOption(option);
//            mLocClient.start();
        }


        //     confirmBluetooth();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ThreadPoolManager.getInstance().addTask(new Runnable() {

                    @Override
                    public void run() {
                        Log.i(TAG, "version=" + CommonUtil.getVersionName(MainActivity.this));
                        String result = null;
                        try {
                            result = HttpUtil.post(HttpUtil.URL_ANDROIDUPDATE,
                                    new BasicNameValuePair(JsonUtil.VERSION, CommonUtil.getVersionName(MainActivity.this)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            result = e.getMessage();
                        }
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                    }
                });
            }
        }, 1000);


//		if (!PreferenceUtil.getInstance(this).getUid().equals("")) {
//			JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
//		    JPushInterface.init(this);
//		}
    }


    void confirmBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bt_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    private void selectTab(int index) {
        // TODO Auto-generated method stub
        switch (index) {
            case 1:
                mTabHost.setCurrentTabByTag(TAB_DEVICE);
                mTabButtonGroup.check(R.id.home_tab_device);
                break;

            case 2:
                //       mTabHost.setCurrentTabByTag(TAB_CAMERA);
                mTabButtonGroup.check(R.id.home_tab_camera);
                break;

            case 3:
                mTabHost.setCurrentTabByTag(TAB_LOCATION);
                mTabButtonGroup.check(R.id.home_tab_location);
                break;

            case 4:
                mTabHost.setCurrentTabByTag(TAB_SETTING);
                mTabButtonGroup.check(R.id.home_tab_setting);
                break;

            case 5:
                mTabHost.setCurrentTabByTag(TAB_INFO);
                mTabButtonGroup.check(R.id.home_tab_info);
                break;

            default:
                break;
        }

        changeTextColor(index);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Log.i(TAG, "onResume," + "index=" + mCurrentIndex);
        // selectTab(mCurrentIndex);
        // 恢复点击camera之前的状态
        if (mCurrentIndex != -1) {
            selectTab(mCurrentIndex + 1);
        }
        MyApplication.getInstance().type = 0;
        isForeground = true;
    }

    @Override
    protected void onPause() {

        // 启动camera之前点击了其他按钮，交给TabActivity处理
        if (mCurrentIndex != getTabHost().getCurrentTab()) {
            mCurrentIndex = -1;
        }


        Log.i(TAG, "onPause," + "index=" + mCurrentIndex);
        super.onPause();
        isForeground = false;
    }

    private void findViewById() {
        mTabButtonGroup = (RadioGroup) findViewById(R.id.home_radio_button_group);

        rButtonDevice = (RadioButton) findViewById(R.id.home_tab_device);
        rButtonCamera = (RadioButton) findViewById(R.id.home_tab_camera);
        rButtonLocation = (RadioButton) findViewById(R.id.home_tab_location);
        rButtonSetting = (RadioButton) findViewById(R.id.home_tab_setting);
        rButtonInfo = (RadioButton) findViewById(R.id.home_tab_info);

        if (PreferenceUtil.getInstance(MainActivity.this).getUid().equals("")) {
            mTabButtonGroup.check(R.id.home_tab_device);
        }
    }

    private void initView() {
        mTabHost = getTabHost();

        Intent i_device = new Intent(this, DeviceListActivity.class);
        final Intent i_camera = new Intent(this, CameraActivity.class);
        //Intent i_location = new Intent(this, LocationActivity.class);
        Intent i_location = new Intent(this, MyGoogleMapActivity.class);
        Intent i_setting = new Intent(this, SettingActivity.class);
        Intent i_info = new Intent(this, InfoActivity.class);

        SharedPreferences mSharedPreferences = getSharedPreferences("watch_app_preference", 0);
        String map = mSharedPreferences.getString("map", "baidu");
        if ("baidu".equals(map)) {
            i_location = new Intent(this, LocationActivity.class);
        }

        mLocIntent = i_location;

        mTabHost.addTab(mTabHost.newTabSpec(TAB_DEVICE).setIndicator(TAB_DEVICE)
                .setContent(i_device));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_CAMERA).setIndicator(TAB_CAMERA)
                .setContent(i_info));

        mTabSpec = mTabHost.newTabSpec(TAB_LOCATION).setIndicator(TAB_LOCATION)
                .setContent(i_location);
        mTabHost.addTab(mTabSpec);

        mTabHost.addTab(mTabHost.newTabSpec(TAB_SETTING).setIndicator(TAB_SETTING)
                .setContent(i_setting));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_INFO).setIndicator(TAB_INFO)
                .setContent(i_info));

        mTabButtonGroup
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Log.i(TAG, "checked id = " + checkedId);
                        switch (checkedId) {
                            case R.id.home_tab_device:
                                mTabHost.setCurrentTabByTag(TAB_DEVICE);
                                changeTextColor(1);
                                break;

                            case R.id.home_tab_camera: {
                                RadioButton bt = (RadioButton) group.findViewById(checkedId);

                                if (bt.isChecked()) {
                                    mCurrentIndex = mTabHost.getCurrentTab();
                                    Log.i(TAG, " mCurrentIndex = " + mCurrentIndex);
                                    //    mTabHost.setCurrentTabByTag(TAB_CAMERA);
                                    changeTextColor(2);
                                    startActivity(i_camera);
                                }
                                break;
                            }

                            case R.id.home_tab_location:
                                mTabHost.setCurrentTabByTag(TAB_LOCATION);
                                changeTextColor(3);
                                break;

                            case R.id.home_tab_setting:
                                mTabHost.setCurrentTabByTag(TAB_SETTING);
                                changeTextColor(4);
                                break;

                            case R.id.home_tab_info:
                                mTabHost.setCurrentTabByTag(TAB_INFO);
                                changeTextColor(5);
                                break;

                            default:
                                break;
                        }
                    }
                });
    }

    private void changeTextColor(int index) {
        switch (index) {
            case 1:
                rButtonDevice.setTextColor(getResources().getColor(
                        R.color.textcolor_select));
                rButtonCamera.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonLocation.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonSetting.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonInfo.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                break;

            case 2:
                rButtonDevice.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonCamera.setTextColor(getResources().getColor(
                        R.color.textcolor_select));
                rButtonLocation.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonSetting.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonInfo.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                break;

            case 3:
                rButtonDevice.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonCamera.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonLocation.setTextColor(getResources().getColor(
                        R.color.textcolor_select));
                rButtonSetting.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonInfo.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                break;

            case 4:
                rButtonDevice.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonCamera.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonLocation.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonSetting.setTextColor(getResources().getColor(
                        R.color.textcolor_select));
                rButtonInfo.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                break;


            case 5:
                rButtonDevice.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonCamera.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonLocation.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonSetting.setTextColor(getResources().getColor(
                        R.color.textcolor_normal));
                rButtonInfo.setTextColor(getResources().getColor(
                        R.color.textcolor_select));
                break;

            default:
                break;
        }
    }


    /**
     * 含有标题、内容、两个按钮的对话框
     **/
    protected void showAlertDialog(String title, String message,
                                   String positiveText,
                                   DialogInterface.OnClickListener onPositiveClickListener,
                                   String negativeText,
                                   DialogInterface.OnClickListener onNegativeClickListener) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton(positiveText, onPositiveClickListener)
                .setNegativeButton(negativeText, onNegativeClickListener)
                .show();
    }

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        Log.i(TAG, "onBackPressed");
////        Intent intent = new Intent(Intent.ACTION_MAIN);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
////        intent.addCategory(Intent.CATEGORY_HOME);
////        startActivity(intent);
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "dispatchKeyEvent");
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            // 具体的操作代码
            Log.i(TAG, "dispatchKeyEvent_onBackPressed");

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);

//            DialogUtil.showNoTitleDialog(MainActivity.this,
//                    R.string.system_sureifexit, R.string.system_sure, R.string.system_cancel,
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            MyApplication.getInstance().exit();
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }, true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        MyApplication.getInstance().removeActivity(this);
        unregisterReceiver(TabReceiver);
        unregisterReceiver(mBtReceiver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i(TAG, "onConfigurationChanged");
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.i(TAG, "GOOGLE latitude = " + latitude + " longitude = " + longitude);

        getAddressFromLocation(latitude, longitude, MainActivity.this, mHandler);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            Log.i(TAG, "onReceiveLocation");
            if (location == null) {
                return;
            }

            String addr = location.getAddrStr();
            Log.i(TAG, "addr=" + addr);
            Log.i(TAG, "lon=" + location.getLongitude());
            Log.i(TAG, "lat=" + location.getLatitude());

            if (addr != null) {
                String[] adds = getLocalMsg(addr);
                if (adds == null) {
                    PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.CITY, addr);
                } else {
                    PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.CITY, adds[1]);
                }
                PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.LOCATION, addr);
            }

            PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.LAT, "" + location.getLatitude());
            PreferenceUtil.getInstance(MainActivity.this).setString(PreferenceUtil.LON, "" + location.getLongitude());
            MyApplication.getInstance().islocation = 1;
            MyApplication.getInstance().radius = location.getRadius();
            MyApplication.getInstance().latitude = location.getLatitude();
            MyApplication.getInstance().longitude = location.getLongitude();
            sendBroadcast(new Intent(LocationActivity.ACTION_UPDATE_POSITION));
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public String[] getLocalMsg(String address) {
        int first, second;
        String[] str = new String[2];
        first = address.indexOf("省") + 1;
        if (first == 0) {
            first = address.indexOf("市") + 1;
            if (first == 0) {
                return null;
            }
            str[0] = address.substring(0, first - 1);
            str[1] = str[0];
        } else {
            second = address.indexOf("市") + 1;
            str[0] = address.substring(0, first);
            str[1] = address.substring(first, second - 1);
        }

        return str;
    }

    BroadcastReceiver TabReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            MyApplication.getInstance().type = 2;

            if (intent.getAction().equals(MAP_SWITCH_ACTION)) {
                String map = intent.getStringExtra("map");

                Intent i_location = new Intent(MainActivity.this, MyGoogleMapActivity.class);
                if ("baidu".equals(map)) {
                    i_location = new Intent(MainActivity.this, LocationActivity.class);
                }

                mLocIntent = i_location;
                mTabSpec.setContent(i_location);
            }
        }
    };

    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (btAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    // The user bluetooth is turning off yet, but it is not disabled yet.
                    return;
                }

                if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    // The user bluetooth is already disabled.
                    MyApplication.getInstance().exit();
                    return;
                }
            }
        }
    };


    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();

//                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                            sb.append(address.getAddressLine(i)).append("\n");
//                        }
                        sb.append(address.getAddressLine(0)).append("\t").append(address.getLocality()).append(address.getCountryName());
                        result = sb.toString();
                        Log.i(TAG, "result address =" + result);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putDouble("longitude", longitude);
                        bundle.putDouble("latitude", latitude);
                        bundle.putString("address", result);
//                        result = "Latitude: " + latitude + " Longitude: " + longitude +
//                                "\n\nAddress:\n" + result;
//                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Unable to get address for this lat-long.";
//                        bundle.putString("address", result);
                        bundle.putDouble("longitude", longitude);
                        bundle.putDouble("latitude", latitude);
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };

        thread.start();
    }
}
