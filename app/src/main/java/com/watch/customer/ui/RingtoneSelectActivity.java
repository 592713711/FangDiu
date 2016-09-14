package com.watch.customer.ui;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.uacent.watchapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 16-3-11.
 */
public class RingtoneSelectActivity extends BaseActivity {
    private ListView mListView;
    private int lastPos;
    static final int NR_RINGTONE = 11;
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ringtone_select);

        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(new SimpleAdapter(this, getData(), R.layout.list_item3,
                new String[]{"text", "icon"},
                new int[]{R.id.list_text, R.id.img_icon}));


        Intent i = getIntent();
        lastPos = i.getIntExtra("index", 0);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {

                Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(pos);
                int wantedChild;
                ImageView iv;

                if (lastPos != pos) {
                    wantedChild = getActualPosition(lastPos);
                    View wantedView = mListView.getChildAt(wantedChild);
                    iv = (ImageView) wantedView.findViewById(R.id.img_icon);
                    iv.setVisibility(View.INVISIBLE);
                }

                wantedChild = getActualPosition(pos);
                View wantedView = mListView.getChildAt(wantedChild);
                iv = (ImageView) wantedView.findViewById(R.id.img_icon);
                iv.setVisibility(View.VISIBLE);
                lastPos = pos;

                playSound((int) item.get("audio_id"));
            }
        });

        mListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int wantedChild;
                ImageView iv;

                wantedChild = getActualPosition(lastPos);
                View wantedView = mListView.getChildAt(wantedChild);
                iv = (ImageView) wantedView.findViewById(R.id.img_icon);
                iv.setVisibility(View.VISIBLE);

                return true;
            }
        });
    }


    int getActualPosition(int pos) {
        int wantedPosition = pos; // Whatever position you're looking for
        int firstPosition = mListView.getFirstVisiblePosition() - mListView.getHeaderViewsCount(); // This is the same as child #0
        int wantedChild = wantedPosition - firstPosition;
        // Say, first visible position is 8, you want position 10, wantedChild will now be 2
        // So that means your view is child #2 in the ViewGroup:
        if (wantedChild < 0 || wantedChild >= mListView.getChildCount()) {
            Log.w("hjq", "Unable to get view for desired position, because it's not being displayed on screen.");
            return  -1;
        }

        return wantedChild;
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
        Map<String, Object> map = (Map<String, Object>) mListView.getAdapter().getItem(lastPos);
        intent.putExtra("audio_id", (int) map.get("audio_id"));
        RingtoneSelectActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                goBack();
                break;
            }

        }

        super.onClick(v);
    }

    static final int audio_mp3_res[] = { R.raw.alarm, R.raw.alarm_bird, R.raw.alarm_car, R.raw.alarm_cat,
                                R.raw.alarm_chatcall, R.raw.alarm_dog, R.raw.alarm_fire, R.raw.alarm_music,
                                R.raw.alarm_radar, R.raw.alarm_trumpet, R.raw.alarm_whistle
                            };

    static public int getIndexFromResid(int resid) {
        int i;

        for (i = 0; i < audio_mp3_res.length; i++) {
            if (resid == audio_mp3_res[i]) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < NR_RINGTONE; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            int index = i + 1;
            map.put("text", "Alarmtone " + index);
            map.put("audio_id", audio_mp3_res[i]);
            map.put("icon", R.drawable.select_icon);
            list.add(map);
        }

        return list;
    }

    private void playSound(int audio_id) {
        Log.d("hjq", "id = " + audio_id);

        if (mPlayer != null) {
            mPlayer.release();
        }

        mPlayer = MediaPlayer.create(RingtoneSelectActivity.this, audio_id);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mPlayer.release();
                mPlayer = null;
            }
        });
        mPlayer.setVolume(1.0f, 1.0f);
        mPlayer.start();
    }
}
