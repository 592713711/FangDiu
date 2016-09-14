package com.watch.customer.ui;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uacent.watchapp.R;
import com.watch.customer.app.MyApplication;
import com.watch.customer.model.LocationRecord;
import com.watch.customer.util.PermissionUtils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.Override;
import java.lang.String;

/**
 * Created by Administrator on 16-5-18.
 */
public class MyGoogleMapActivity extends BaseActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

        /**
         * Request code for location permission request.
         *
         * @see #onRequestPermissionsResult(int, String[], int[])
         */
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

        /**
         * Flag indicating whether a requested permission has been denied after returning in
         * {@link #onRequestPermissionsResult(int, String[], int[])}.
         */
        private boolean mPermissionDenied = false;

        private GoogleMap mMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.my_location_demo);

                SupportMapFragment mapFragment =
                        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);


                Button bt_lost_history = (Button) findViewById(R.id.bt_lost_history);
                bt_lost_history.setOnClickListener(this);

                Button bt_loc_history = (Button) findViewById(R.id.bt_location_history);
                bt_loc_history.setOnClickListener(this);
        }

        @Override
        public void onMapReady(GoogleMap map) {
                mMap = map;

                mMap.setOnMyLocationButtonClickListener(this);
                enableMyLocation();
                centerMapOnMyLocation();
        }

        private void centerMapOnMyLocation() {
                if (MyApplication.getInstance().islocation == 1) {
                        double lat = MyApplication.getInstance().latitude;
                        double lng = MyApplication.getInstance().longitude;

                        LatLng ll = new LatLng(lat, lng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14));
                }
        }

        /**
         * Enables the My Location layer if the fine location permission has been granted.
         */
        private void enableMyLocation() {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                        // Permission to access the location is missing.
                        PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                                Manifest.permission.ACCESS_FINE_LOCATION, true);
                } else if (mMap != null) {
                        // Access to the location has been granted to the app.
                        mMap.setMyLocationEnabled(true);
                }
        }

        @Override
        public boolean onMyLocationButtonClick() {
               // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
                // Return false so that we don't consume the event and the default behavior still occurs
                // (the camera animates to the user's current position).
                return false;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
                if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
                        return;
                }

                if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Enable the my location layer if the permission has been granted.
                        enableMyLocation();
                } else {
                        // Display the missing permission error dialog when the fragments resume.
                        mPermissionDenied = true;
                }
        }

        @Override
        protected void onResumeFragments() {
                super.onResumeFragments();
                if (mPermissionDenied) {
                        // Permission was not granted, display error dialog.
                        showMissingPermissionError();
                        mPermissionDenied = false;
                }
        }

        /**
         * Displays a dialog with error message explaining that the location permission is missing.
         */
        private void showMissingPermissionError() {
                PermissionUtils.PermissionDeniedDialog
                        .newInstance(true).show(getSupportFragmentManager(), "dialog");
        }


        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                        case R.id.bt_location_history: {
                                Intent intent = new Intent(MyGoogleMapActivity.this, LocationRecordList.class);
                                intent.putExtra("status", LocationRecord.FOUND);
                                startActivityForResult(intent, LocationActivity.LOCATION_GET_POSITION);
                                break;
                        }

                        case R.id.bt_lost_history: {
                                Intent intent = new Intent(MyGoogleMapActivity.this, LocationRecordList.class);
                                intent.putExtra("status", LocationRecord.LOST);
                                startActivityForResult(intent, LocationActivity.LOCATION_GET_POSITION);
                                break;
                        }

                        default:
                                break;
                }

                super.onClick(v);
        }

        void addMarker(double longitude, double latitude) {
                LatLng pos = new LatLng(latitude, longitude);
                Marker perth = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .draggable(true));
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (resultCode == RESULT_OK) {
                        if (requestCode == LocationActivity.LOCATION_GET_POSITION) {
                                float longitude = data.getFloatExtra("longitude", 0f);
                                float latitude = data.getFloatExtra("latitude", 0f);
                                Log.e("hjq", "long = " + longitude + " latitude =" + latitude);
                                addMarker(longitude, latitude);
                        }
                }

                super.onActivityResult(requestCode, resultCode, data);
        }

}
