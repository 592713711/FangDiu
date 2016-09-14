package com.watch.customer.ui;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;

import com.uacent.watchapp.R;
import com.watch.customer.model.Shop;
import com.watch.customer.util.PreferenceUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShopMapActivity extends BaseActivity implements
		BaiduMap.OnMapClickListener {
	Button showlinepop;
	MapView mMapView = null; // 地图View
	BaiduMap mBaidumap = null;
	String cityname = "";
	LatLng currentLocation = null;
	boolean isFirstLoc = true;// 是否首次定位
	private Shop mShop;
	Double latitude;
	Double longitude;
	BitmapDescriptor mCurrentMarker;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_map);
		Intent it = getIntent();
		mShop = (Shop) it.getSerializableExtra("object");
		Log.e("hjq",mShop.toString());
		latitude = Double.valueOf(mShop.getLat());
		Log.e("hjq",latitude+"");
		longitude = Double.valueOf(mShop.getLon());
		Log.e("hjq",longitude+"");
		((TextView) findViewById(R.id.title)).setText(mShop.getName());
		mMapView = (MapView) findViewById(R.id.map);
		showlinepop = (Button) findViewById(R.id.showlinepop);
		findViewById(R.id.back).setOnClickListener(this);
		showlinepop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Double mLat1=Double.parseDouble(PreferenceUtil.getInstance(ShopMapActivity.this).getString(PreferenceUtil.LAT, ""));
				Double mLon1=Double.parseDouble(PreferenceUtil.getInstance(ShopMapActivity.this).getString(PreferenceUtil.LON, ""));
				LatLng pt1 = new LatLng(mLat1, mLon1);
				LatLng pt2 = new LatLng(latitude, longitude);
				// 构建 导航参数

			}
		});
		mBaidumap = mMapView.getMap();
		mBaidumap.setMyLocationEnabled(true);
		mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(
						com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL,
						false, null));
		// 地图点击事件处理
		mBaidumap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);
		mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(
				com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));
		mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(
						com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL,
						true, mCurrentMarker));
		MyLocationData locData = new MyLocationData.Builder()
		.latitude(latitude)
		.longitude(longitude).build();
		
		mBaidumap.setMyLocationData(locData);
		LatLng ll = new LatLng(latitude,
				longitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaidumap.animateMapStatus(u);
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

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}
	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
