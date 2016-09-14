package com.watch.customer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uacent.watchapp.R;

/**
 * Created by Administrator on 16-3-21.
 */
public class MapSelectActivity extends BaseActivity {
    SharedPreferences mSharedPreferences;
    ImageView iv_baidu;
    ImageView iv_google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_select_activity);
        iv_baidu = (ImageView) findViewById(R.id.iv_baidu_check);
        iv_google = (ImageView) findViewById(R.id.iv_google_check);

        mSharedPreferences = getSharedPreferences("watch_app_preference", 0);
        String map = mSharedPreferences.getString("map", "baidu");
       if ("baidu".equals(map)) {
           iv_baidu.setVisibility(View.VISIBLE);
           iv_google.setVisibility(View.INVISIBLE);
       } else if ("google".equals(map)) {
           iv_baidu.setVisibility(View.INVISIBLE);
           iv_google.setVisibility(View.VISIBLE);
       }

        RelativeLayout rl_baidu = (RelativeLayout) findViewById(R.id.rl_bd_map);
        RelativeLayout rl_google = (RelativeLayout) findViewById(R.id.rl_google_map);

        rl_baidu.setOnClickListener(this);
        rl_google.setOnClickListener(this);

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_bd_map: {
                iv_baidu.setVisibility(View.VISIBLE);
                iv_google.setVisibility(View.INVISIBLE);

                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString("map", "baidu");
                mEditor.apply();

                Intent i = new Intent();
                i.setAction(MainActivity.MAP_SWITCH_ACTION);
                i.putExtra("map", "baidu");
                sendBroadcast(i);

                break;
            }

            case R.id.rl_google_map: {
                iv_baidu.setVisibility(View.INVISIBLE);
                iv_google.setVisibility(View.VISIBLE);

                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString("map", "google");
                mEditor.apply();

                Intent i = new Intent();
                i.setAction(MainActivity.MAP_SWITCH_ACTION);
                i.putExtra("map", "google");
                sendBroadcast(i);
                break;
            }

            case R.id.iv_back: {
                finish();
                break;
            }
        }

        super.onClick(v);
    }
}
