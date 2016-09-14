package com.watch.customer.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.dao.BtDeviceDao;
import com.watch.customer.model.BtDevice;
import com.watch.customer.util.SwitchButton;

/**
 * Created by Administrator on 16-3-11.
 */
public class AntiLostSettingActivity extends BaseActivity {
    Button mBtnear;
    Button mBtmiddle;
    Button mBtfar;
    final static int SELECT_RINGTONE = 1;
    BtDevice mDevice;
    BtDeviceDao mDao;
    SwitchButton antiLostSwitch;
    SwitchButton lightSwitch;
    SeekBar mVolumeBar;
    AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_antilost_setting);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        mDevice  = (BtDevice) b.getSerializable("device");
        Log.d("hjq", "mDevice = " + mDevice.toString());

        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        mBtnear = (Button) findViewById(R.id.btn_near);
        mBtnear.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           mBtmiddle.setBackgroundColor(getResources().getColor(R.color.text_white));
                                           mBtfar.setBackgroundColor(getResources().getColor(R.color.text_white));
                                           mBtnear.setBackgroundColor(getResources().getColor(R.color.btnbg_orange));
                                           mDevice.setAlertDistance(BtDevice.ALERT_DISTANCE_NEAR);
                                       }
                                   }
        );

        mBtmiddle = (Button) findViewById(R.id.btn_middle);
        mBtmiddle.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             mBtnear.setBackgroundColor(getResources().getColor(R.color.text_white));
                                             mBtfar.setBackgroundColor(getResources().getColor(R.color.text_white));
                                             mBtmiddle.setBackgroundColor(getResources().getColor(R.color.btnbg_orange));
                                             mDevice.setAlertDistance(BtDevice.ALERT_DISTANCE_MIDDLE);
                                         }
                                     }
        );
        mBtfar = (Button) findViewById(R.id.btn_far);
        mBtfar.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          mBtnear.setBackgroundColor(getResources().getColor(R.color.text_white));
                                          mBtmiddle.setBackgroundColor(getResources().getColor(R.color.text_white));
                                          mBtfar.setBackgroundColor(getResources().getColor(R.color.btnbg_orange));

                                          mDevice.setAlertDistance(BtDevice.ALERT_DISTANCE_FAR);
                                      }
                                  }
        );

        if (mDevice.getAlertDistance() == BtDevice.ALERT_DISTANCE_FAR) {
            mBtfar.setBackgroundColor(getResources().getColor(R.color.btnbg_orange));
        } else if (mDevice.getAlertDistance() == BtDevice.ALERT_DISTANCE_MIDDLE) {
            mBtmiddle.setBackgroundColor(getResources().getColor(R.color.btnbg_orange));
        } else if (mDevice.getAlertDistance() == BtDevice.ALERT_DISTANCE_NEAR) {
            mBtnear.setBackgroundColor(getResources().getColor(R.color.btnbg_orange));
        }

        antiLostSwitch = (SwitchButton) findViewById(R.id.switchAntiLost);
        if (mDevice.isAntiLostSwitch()) {
            antiLostSwitch.setChecked(true);
        } else {
            antiLostSwitch.setChecked(false);
        }

        antiLostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("hjq", "is checked = " + isChecked);
                mDevice.setAntiLostSwitch(isChecked);
            }
        });

        lightSwitch = (SwitchButton) findViewById(R.id.switchLight);
        if (mDevice.isLostAlertSwitch()) {
            lightSwitch.setChecked(true);
        } else {
            lightSwitch.setChecked(false);
        }

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("hjq", "lightswitch is checked = " + isChecked);
                mDevice.setLostAlertSwitch(isChecked);
            }
        });

        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.d("hjq", "max volume = " + maxVolume);
        mVolumeBar = (SeekBar) findViewById(R.id.seekBarVolume);
        mVolumeBar.setMax(maxVolume);
        mVolumeBar.setProgress(mDevice.getAlertVolume());
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seekBar == mVolumeBar) {
                    Log.d("hjq", "value in seekbar = " + i);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                    mDevice.setAlertVolume(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        View ll_ringtone = findViewById(R.id.ll_ringtone);
        ll_ringtone.setOnClickListener(this);

        mDao = new BtDeviceDao(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            Log.e("hjq", "onBackPressed");

            goBack();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    private void goBack() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("device", mDevice);
        intent.putExtras(b);

        ContentValues cv = new ContentValues();
        cv.put("alertRingtone", mDevice.getAlertRingtone());
        cv.put("anitiLostSwitch", mDevice.isAntiLostSwitch() ? 1 : 0);
        cv.put("lostAlertSwitch", mDevice.isLostAlertSwitch() ? 1 : 0);
        cv.put("alertVolume", mDevice.getAlertVolume());

        int index = mDao.update(mDevice, cv);
        Log.d("hjq", "index = " + index + " d = " + mDevice);

        //设置返回数据
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
      public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                goBack();
                break;
            }

            case R.id.ll_ringtone: {
                Intent i = new Intent(AntiLostSettingActivity.this, RingtoneSelectActivity.class);
                int index = -1;
                if (mDevice.getAlertRingtone() > 0) {
                    index = RingtoneSelectActivity.getIndexFromResid(mDevice.getAlertRingtone());
                }

                if (index < 0) {
                    index = 0;
                }

                Log.d("hjq", "index = " + index);
                i.putExtra("index", index);

                startActivityForResult(i, SELECT_RINGTONE);
                break;
            }
        }

        super.onClick(v);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_RINGTONE: {
                    Bundle bundle = data.getExtras();
                    int id = bundle.getInt("audio_id");

                    //Toast.makeText(this, "audio id = " + id, Toast.LENGTH_SHORT).show();
                    mDevice.setAlertRingtone(id);

                    break;
                }

                default:
                    break;
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
