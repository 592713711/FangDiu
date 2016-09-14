package com.watch.customer.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.service.BleComService;
import com.watch.customer.ui.CameraInterface.CamOpenOverCallback;
import com.watch.customer.util.DisplayUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 16-3-7.
 */
public class CameraActivity  extends BaseActivity implements CamOpenOverCallback, CameraInterface.OnFinishCallback {
    private static final String TAG = "CameraActivity";
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    ImageButton switchCameraBtn;
    ImageButton settingBtn;
    ImageButton albumBtn;
    ImageButton flashBtn;
    Handler mHandler;
    Handler myHandler;
    HandlerThread mHandlerThread;

    int mDegree;

    List<String> mSupportedFlashMode;

    int mCount;
    int mInterval;

    private IService mService;
    SharedPreferences mSharedPreferences;

    float previewRate = -1f;
    private PowerManager.WakeLock wakeLock;
    private boolean iswakeLock = true;

    boolean mCapturing = false;
    final Object mSync = new Object();

    boolean mStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "cameraactivity oncreate");
        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();

        shutterBtn.setOnClickListener(new BtnListeners());
        switchCameraBtn.setOnClickListener(new BtnListeners());
        albumBtn.setOnClickListener(new BtnListeners());
        settingBtn.setOnClickListener(new BtnListeners());
        flashBtn.setOnClickListener(new BtnListeners());

        mHandler = new Handler();
        mSharedPreferences = getSharedPreferences("watch_app_preference", 0);

        mHandlerThread = new HandlerThread("takephoto");
        mHandlerThread.start();
        myHandler = new Handler(mHandlerThread.getLooper());

        Intent i = new Intent(CameraActivity.this, BleComService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private void initUI(){
        surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);

        switchCameraBtn = (ImageButton) findViewById(R.id.btn_camera_switch);
        albumBtn = (ImageButton) findViewById(R.id.btn_album);
        settingBtn = (ImageButton) findViewById(R.id.btn_setting);
        flashBtn = (ImageButton) findViewById(R.id.ib_flash);
    }

    private void initViewParams(){
        LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        surfaceView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);;
        shutterBtn.setLayoutParams(p2);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "cameraactivity onresume");

        // TODO Auto-generated method stub
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, "DPA");

        if (iswakeLock) {
            wakeLock.acquire();
        }

        surfaceView.setVisibility(View.VISIBLE);
        mDegree = getCameraDisplayOrientation(this, CameraInterface.getInstance().getCameraId());
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
            }
        });

        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "cameraactivity onstop");

        super.onStop();
    }

    void showDialog() {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.camera_setting_dialog,
                (ViewGroup) findViewById(R.id.camera_setting_dialog));

        mCount = mSharedPreferences.getInt("continous", 0);
        mInterval = mSharedPreferences.getInt("interval", 0);   // mInterval 单位为0.1s

        final SeekBar sb_continous = (SeekBar) layout.findViewById(R.id.sb_continue);
        final SeekBar sb_interval = (SeekBar) layout.findViewById(R.id.sb_interval);
        final TextView tv_continous = (TextView) layout.findViewById(R.id.tv_continue);
        final TextView tv_interval = (TextView) layout.findViewById(R.id.tv_interval);
        View btn_ok = layout.findViewById(R.id.rl_ok);

        sb_continous.setProgress(mCount);
        sb_interval.setProgress(mInterval);
        tv_interval.setText((float)mInterval / 10.0f + " " + getString(R.string.str_second));
        tv_continous.setText(mCount + " " + getString(R.string.str_stretch));

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(layout).show();
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = sb_continous.getProgress();
                int interval = sb_interval.getProgress();

                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putInt("continous", count);
                mEditor.putInt("interval", interval);
                mEditor.apply();

                dialog.dismiss();
            }
        });

        sb_interval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float interval = progress / 10.0f;
                tv_interval.setText(interval + " " + getString(R.string.str_second));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_continous.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_continous.setText(progress + " " + getString(R.string.str_stretch));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "cameraactivity onPause");

        myHandler.post(new Runnable() {
            @Override
            public void run() {
                CameraInterface.getInstance().doStopCamera();
                Log.d(TAG, "run in thread");
            }
        });
        surfaceView.setVisibility(View.INVISIBLE);
        if (wakeLock != null) {
            wakeLock.release();
        }

        super.onPause();
    }

    //6.0 需要手动判断程序是否有写入外部存储权限
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }else{
            takePhoto();
        }
    }

    void takePhoto() {

        mCount = mSharedPreferences.getInt("continous", 0);
        mInterval = mSharedPreferences.getInt("interval", 0);   // mInterval 单位为0.1s
        mStopped = false;

        synchronized (mSync) {
            if (mCapturing) {
                Log.e("hjq", "camera is busying");
                return;
            }
            mCapturing = true;
        }

        myHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "take photo mcount = " + mCount);
                CameraInterface.getInstance().doTakePicture(CameraActivity.this, CameraActivity.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                //takePhoto();
            } else {
                showLongToast(getString(R.string.str_takephone_fail));
            }
        }
    }

    @Override
    public void onFinish() {
        if (mCount != 0) {
            if (mStopped) {
                mCapturing = false;
                mCount = 0;
                return;
            }

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CameraInterface.getInstance().doTakePicture(CameraActivity.this, CameraActivity.this);
                    mCount--;
                    Log.e(TAG, "take photo mcount = " + mCount);
                }
            }, mInterval * 100);
        } else {
            synchronized (mSync) {
                mCapturing = false;
            }
        }

        Log.e(TAG, "mcapturing = " + mCapturing);
    }

    private class BtnListeners implements OnClickListener{
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            synchronized (mSync) {
                if (mCapturing) {
                    Toast.makeText(CameraActivity.this, R.string.str_camera_busy, Toast.LENGTH_SHORT).show();
                    int id = v.getId();

                   if (id ==  R.id.btn_shutter ||  id == R.id.btn_album || id == R.id.btn_camera_switch ||
                           id == R.id.btn_setting || id == R.id.ib_flash || id == R.id.ib_flash_off ||
                           id == R.id.ib_flash_auto || id == R.id.ib_flash_on) {
                       mStopped = true;
                   }
                    return;
                }
            }

            switch(v.getId()){
                case R.id.btn_shutter: {
                    checkPermission();
                    break;
                }

                case R.id.btn_album: {
                    Intent intent = new Intent(Intent.ACTION_VIEW, /*android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI*/Uri.parse(
                            "content://media/internal/images/media"));
                   // intent.setType("image/*");
                    startActivity(intent);
                    break;
                }

                case R.id.btn_camera_switch: {
                    CameraInterface.getInstance().switchCamera();
                    mDegree = getCameraDisplayOrientation(CameraActivity.this, CameraInterface.getInstance().getCameraId());
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "here1");
                            CameraInterface.getInstance().doStopCamera();
                            CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
                        }
                    });
                    break;
                }

                case R.id.btn_setting: {
                    showDialog();
                    break;
                }

                case R.id.ib_flash:{
                    if (mSupportedFlashMode == null) {
                        break;
                    }

                    LinearLayout ll = (LinearLayout) findViewById(R.id.ll_flash_second);
                    if (ll.getVisibility() == View.VISIBLE) {
                        ll.setVisibility(View.GONE);
                        break;
                    }

                    ll.setVisibility(View.VISIBLE);
                    Map<String, Boolean> map = new HashMap<>();

                    for (String mode : mSupportedFlashMode) {
                        Log.e("hjq", "mode = " + mode);
                        map.put(mode, true);
                    }

                    ImageButton ib_flash_auto = (ImageButton) ll.findViewById(R.id.ib_flash_auto);
                    if (map.get("auto") != null) {
                        ib_flash_auto.setOnClickListener(new BtnListeners());
                        ib_flash_auto.setVisibility(View.VISIBLE);
                    } else {
                        ib_flash_auto.setVisibility(View.GONE);
                    }

                    ImageButton ib_flash_on = (ImageButton) ll.findViewById(R.id.ib_flash_on);
                    if (map.get("on") != null) {
                        ib_flash_on.setOnClickListener(new BtnListeners());
                        ib_flash_on.setVisibility(View.VISIBLE);
                    } else {
                        ib_flash_on.setVisibility(View.GONE);
                    }

                    ImageButton ib_flash_off = (ImageButton) ll.findViewById(R.id.ib_flash_off);
                    if (map.get("off") != null) {
                        ib_flash_off.setOnClickListener(new BtnListeners());
                        ib_flash_off.setVisibility(View.VISIBLE);
                    } else {
                        ib_flash_off.setVisibility(View.GONE);
                    }

                    break;
                }

                case R.id.ib_flash_off:
                case R.id.ib_flash_auto:
                case R.id.ib_flash_on: {
                    changeFlashMode(v.getId());
                    LinearLayout ll = (LinearLayout) findViewById(R.id.ll_flash_second);
                    ll.setVisibility(View.INVISIBLE);

                    break;
                }

                default:
                    break;
            }
        }
    }

    void changeFlashMode(int resid) {
        String mode = "off";
        int drawid = R.drawable.flash_off;;
        if (resid ==  R.id.ib_flash_off) {
            mode = "off";
            drawid = R.drawable.flash_off;
        } else if (resid == R.id.ib_flash_auto) {
            mode = "auto";
            drawid = R.drawable.flash_auto;
        }  else if (resid == R.id.ib_flash_on) {
            mode = "on";
            drawid = R.drawable.flash_on;
        }

        flashBtn.setBackgroundResource(drawid);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("flash_mode", mode);
        mEditor.apply();

        CameraInterface.getInstance().setFlashMode(mode);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "camera activity onDestroy");
        if (mConnection != null) {
            try {
                Log.i(TAG, "onDestroy->>unregisterCallback");
                mService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        Log.i(TAG, "onDestroy->>unbindService");
        super.onDestroy();
    }

    public static int getCameraDisplayOrientation(Activity activity,
                                                   int cameraId
                                                   ) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate, mDegree);

        Log.e(TAG, "here 3");

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSupportedFlashMode = CameraInterface.getInstance().getSupportedFlashModes();
                if (mSupportedFlashMode == null) {
                    Log.e(TAG, " supported flash mode is null?");
                    return;
                } else {
                    Log.e(TAG, "list size = " + mSupportedFlashMode.size());
                }

                if (mSupportedFlashMode.size() > 0) {
                    String flash_mode = mSharedPreferences.getString("flash_mode", mSupportedFlashMode.get(0));
                    if (!mSupportedFlashMode.contains(flash_mode)) {
                        flash_mode = mSupportedFlashMode.get(0);
                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                        mEditor.putString("flash_mode", flash_mode);
                        mEditor.apply();
                    }

                    if ("auto".equals(flash_mode)) {
                        flashBtn.setBackgroundResource(R.drawable.flash_auto);
                    } else if ("on".equals(flash_mode)) {
                        flashBtn.setBackgroundResource(R.drawable.flash_on);
                    } else if ("off".equals(flash_mode)) {
                        flashBtn.setBackgroundResource(R.drawable.flash_off);
                    }
                } else {
                    flashBtn.setVisibility(View.INVISIBLE);
                }
                Log.e(TAG, "here 4");
                flashBtn.invalidate();

                surfaceView.invalidate();
            }
        });
    }
    private ICallback.Stub mCallback = new ICallback.Stub() {
        @Override
        public void onConnect(String address) throws RemoteException {
                Log.d(TAG, "onConnect address = " + address);
        }

        @Override
        public void onDisconnect(String address) throws RemoteException {
                Log.d(TAG, "onDisconnect called");
        }

        @Override
        public boolean onRead(String address, byte[] val) throws RemoteException {
            Log.e(TAG, "onRead called in camera");
            return false;
        }

        @Override
        public boolean onWrite(String address, byte[] val) throws RemoteException {

            String active = getTopActivity();
            Log.e(TAG, "onWrite called in camera act = " + active);

            if (active != null && !active.contains("CameraActivity")){
                return false;
            }

            int key = val[0];
            if (key == 1) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkPermission();
                    }
                });
            }

            return true;
        }

        @Override
        public void onSignalChanged(String address, int rssi) throws RemoteException {
                Log.d(TAG, "onSignalChanged called address = " + address + " rssi = " + rssi);
        }

        public void onPositionChanged(String address, int position) throws RemoteException {
                Log.d(TAG, "onPositionChanged called address = " + address + " newpos = " + position);
        }

        @Override
        public void onAlertServiceDiscovery(String address, boolean support) throws RemoteException {

        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected in camera");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, " onServiceConnected in camera");
            mService = IService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "", e);
            }
        }
    };


}
