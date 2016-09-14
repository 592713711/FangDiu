package com.watch.customer.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.BMapManager;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;
import com.watch.customer.model.LocationRecord;
import com.watch.customer.util.DialogUtil;
import com.watch.customer.util.PermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 16-3-7.
 */
public class LocationActivity  extends BaseActivity {
    public static final int LOCATION_GET_POSITION = 1;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 2;
    // 定位相关
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAABDE3DA;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    public static final String ACTION_UPDATE_POSITION = "com.watch.customer.LocationActivity.UPDATE_POSITION_ACTION";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    MapView mMapView;
    BaiduMap mBaiduMap;

    boolean mCustomPos = false;

    private final String TAG = "hjq";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "location activity onCreate");

        setContentView(R.layout.activity_location);

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        hideZoomControl();
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化

        Button bt_lost_history = (Button) findViewById(R.id.bt_lost_history);
        bt_lost_history.setOnClickListener(this);

        Button bt_loc_history = (Button) findViewById(R.id.bt_location_history);
        bt_loc_history.setOnClickListener(this);

        Button curBtn = (Button) findViewById(R.id.button1);
        curBtn.setOnClickListener(this);

        registerReceiver(mLocationReceiver, new IntentFilter(ACTION_UPDATE_POSITION));

        gotoCurrentPosition();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("hjq", "not granted");
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    void hideZoomControl() {
        int childCount = mMapView.getChildCount();
        View zoom = null;

        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);

            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }

        if (zoom != null) {
            zoom.setVisibility(View.GONE);
        }
    }

    void addMarker(double longitude, double latitude) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        mCustomPos = true;

        LatLng ll = new LatLng(latitude, longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "location activity onpause");
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "location activity onResume");
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();

        super.onResume();
    }

    void gotoCurrentPosition() {
        if (MyApplication.getInstance().islocation == 1) {
            mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_geo);
            mBaiduMap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            mCurrentMode, true, mCurrentMarker,
                            accuracyCircleFillColor, accuracyCircleStrokeColor));

//            mCurrentMarker = null;
//            mBaiduMap
//                    .setMyLocationConfigeration(new MyLocationConfiguration(
//                            mCurrentMode, true, null));

            double latitude = MyApplication.getInstance().latitude;
            double longitude = MyApplication.getInstance().longitude;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(MyApplication.getInstance().radius)
                    .direction(100).latitude(latitude)
                    .longitude(longitude).build();
            mBaiduMap.setMyLocationData(locData);

            LatLng ll = new LatLng(latitude, longitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_location_history: {
                Intent intent = new Intent(LocationActivity.this, LocationRecordList.class);
                intent.putExtra("status", LocationRecord.FOUND);
                startActivityForResult(intent, LOCATION_GET_POSITION);
                break;
            }

            case R.id.bt_lost_history: {
                Intent intent = new Intent(LocationActivity.this, LocationRecordList.class);
                intent.putExtra("status", LocationRecord.LOST);
                startActivityForResult(intent, LOCATION_GET_POSITION);
                break;
            }

            case R.id.button1: {
                mCustomPos = false;
                gotoCurrentPosition();
                break;
            }

            default:
                break;
        }

        super.onClick(v);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "location activity onDestroy");
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        unregisterReceiver(mLocationReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == LOCATION_GET_POSITION) {
                float longitude = data.getFloatExtra("longitude", 0f);
                float latitude = data.getFloatExtra("latitude", 0f);
                Log.e("hjq", "long = " + longitude + " latitude =" + latitude);
                addMarker(longitude, latitude);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private final BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ACTION_UPDATE_POSITION)) {
                if (!mCustomPos) {
                    gotoCurrentPosition();
                }
            }
        }
    };
}
