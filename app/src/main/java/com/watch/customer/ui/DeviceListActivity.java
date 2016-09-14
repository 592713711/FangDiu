package com.watch.customer.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.adapter.DeviceListAdapter;
import com.watch.customer.app.MyApplication;
import com.watch.customer.dao.BtDeviceDao;
import com.watch.customer.dao.LocationDao;
import com.watch.customer.device.BluetoothAntiLostDevice;
import com.watch.customer.device.BluetoothLeClass;
import com.watch.customer.model.BtDevice;
import com.watch.customer.model.LocationRecord;
import com.watch.customer.service.BleComService;
import com.watch.customer.util.PreferenceUtil;
import com.watch.customer.xlistview.ItemMainLayout;
import com.watch.customer.xlistview.Menu;
import com.watch.customer.xlistview.MenuItem;
import com.watch.customer.xlistview.SlideAndDragListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 16-3-7.
 */
public class DeviceListActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, DeviceListAdapter.OnItemClickCallback, SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {
    private static final String TAG = "DeviceListActivity";
    private static final int CHANGE_BLE_DEVICE_SETTING = 1;
    private SlideAndDragListView mDeviceList;
    private DeviceListAdapter mDeviceListAdapter;
    private ArrayList<BtDevice> mListData;
    private Handler mHandler;
    private BtDeviceDao mDeviceDao;
    private IService mService;
    boolean mScanningStopped;
    LocationDao mLocationDao;
    SharedPreferences mSharedPreferences;

    //    private final String TAG = "hjq";
    private Menu mMenu;
    private Handler myHandler;
    HandlerThread mHandlerThread;

    Map<String, Runnable> mTimer = new HashMap<>(20);
    Object mLock = new Object();

    Map<String, Runnable> mKeyChecker = new HashMap<>(10);

    final static int DISCOVER_SERVICE_TIMEOUT = 20 * 1000; // 20 S
    private SurfaceTexture mPreviewTexture;
    private boolean restart = false;

    /**
     * 重连时间间隔
     */
    private final long RELINKTIME = 3;
    /**
     * 快速重连时间间隔
     */
    private final long QUICKRELINKTIME = 3;
    private Timer timer;
    private boolean isFristScanner = false;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_devicelist);
        initView();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        initMenu();
        initUiAndListener();
        fillListData();
        Intent i = new Intent(this, BleComService.class);
        getApplicationContext().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        showLoadingDialog(getResources().getString(R.string.waiting));
        mHandlerThread = new HandlerThread("torchThread");
        mHandlerThread.start();
        myHandler = new Handler(mHandlerThread.getLooper());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initView() {
        ImageView seachbtn;
        ImageView testbtn;
        seachbtn = (ImageView) findViewById(R.id.search);
        testbtn = (ImageView) findViewById(R.id.testkey);
        seachbtn.setOnClickListener(this);
        testbtn.setOnClickListener(this);
        mHandler = new Handler();
        mDeviceDao = new BtDeviceDao(this);
        mLocationDao = new LocationDao(this);
        mSharedPreferences = getSharedPreferences("watch_app_preference", 0);
        mDeviceList = (SlideAndDragListView) findViewById(R.id.devicelist);
        mDeviceList.setOnItemClickListener(DeviceListActivity.this);
        mDeviceList.setLayoutAnimation(getAnimationController());
    }

    public void initMenu() {
        mMenu = new Menu(new ColorDrawable(Color.WHITE), true);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(new ColorDrawable(Color.parseColor("#c20f0f")))
                .setText(getString(R.string.system_delete))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.WHITE)
                .setTextSize(14)
                .build());
    }

    public void initUiAndListener() {
        mDeviceList.setMenu(mMenu);
        mDeviceList.setOnListItemLongClickListener(this);
        mDeviceList.setOnDragListener(this, mListData);
        mDeviceList.setOnListItemClickListener(this);
        mDeviceList.setOnSlideListener(this);
        mDeviceList.setOnMenuItemClickListener(this);
        mDeviceList.setOnItemDeleteListener(this);
    }


    @Override
    protected void onDialogCancel() {
        super.onDialogCancel();

        Log.i(TAG, "mScanningStopped = " + mScanningStopped);
        if (!mScanningStopped) {
            scanLeDevice(false);
        }
//        checkAntiLost();
    }

    protected LayoutAnimationController getAnimationController() {
        int duration = 300;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    void checkUI(final String address) {
        Log.i(TAG, "checkUI");
        final Runnable fnCheck = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "fnCheck");
                restart = false;
                boolean bStopTorch = true;
                for (int i = 0; i < mListData.size(); i++) {
                    BtDevice d = mListData.get(i);
                    Log.i(TAG, "address:" + d.getAddress() + "    d.isLostAlert()->>>" + d.isLostAlert() + "   d.isAntiLostSwitch()->>" + d.isAntiLostSwitch());
                    if (address.equals(d.getAddress())) {
                        if (d.isLostAlert() && d.isAntiLostSwitch()) {
                            Log.i(TAG, "d.isLostAlert()");
                            //lzg edit
                            d.setStatus(BluetoothLeClass.BLE_STATE_BREAK);
                            Log.i(TAG, "updata");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mDeviceListAdapter.notifyDataSetChanged();
                                }
                            });

                            int disturb = mSharedPreferences.getInt("disturb_status", 0);
                            if (disturb == 0) {     // 免打扰模式没有打开，播放声音
                                playAlertRingtone(d);
                            }
                            startAnimation(i);
                            restart = true;
                            d.setAlertingPolice(true);

                            if (d.isLostAlertSwitch()) {
                                Log.i(TAG, "d.isLostAlertSwitch()");
                                flashTorch();
                                bStopTorch = false;
                            }
                            //有用
//                            if (d.isLostAlert()) {
//                                flashTorch();
//                                bStopTorch = false;
//                            }
                        } else {
                            Log.i(TAG, "!  d.isLostAlert()");
                            stopAnimation(i);
                            stopAlertRingtone(d);
                        }
                        break;
                    }
                }

                if (bStopTorch) {
                    ensureStopTorch();
                }
                if (restart) {
                    mHandler.postDelayed(this, 3000);
                }
            }
        };
        mHandler.postDelayed(fnCheck, 1000);
    }

    private void showItemViewAnimation(final View v, final int index) {
        if (v.getAnimation() != null) {
            Log.i(TAG, "animation is running");
            return;
        }

        final Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        myAnimation.setAnimationListener(new Animation.AnimationListener() {
                                             @Override
                                             public void onAnimationStart(Animation animation) {

                                             }

                                             @Override
                                             public void onAnimationEnd(Animation animation) {
                                                 if (v.getAnimation() != null) {
                                                     v.startAnimation(myAnimation);
                                                 }
                                             }

                                             @Override
                                             public void onAnimationRepeat(Animation animation) {

                                             }
                                         }
        );
        v.startAnimation(myAnimation);
    }

    int getActualPosition(int pos) {
        int firstPosition = mDeviceList.getFirstVisiblePosition() - mDeviceList.getHeaderViewsCount(); // This is the same as child #0
        int wantedChild = pos - firstPosition;
        // Say, first visible position is 8, you want position 10, wantedChild will now be 2
        // So that means your view is child #2 in the ViewGroup:
        if (wantedChild < 0 || wantedChild >= mDeviceList.getChildCount()) {
            Log.w("hjq", "Unable to get view for desired position, because it's not being displayed on screen.");
            return -1;
        }
        return wantedChild;
    }

    void stopAnimation(final int position) {
        int wantedChild;
        vibrator.cancel();
        wantedChild = getActualPosition(position);
        View wantedView = mDeviceList.getChildAt(wantedChild);
        if (wantedView != null) {
            ItemMainLayout layout = (ItemMainLayout) wantedView;
            View v = layout.getItemCustomLayout().getCustomView();
            v.setBackgroundColor(getResources().getColor(R.color.text_white));
            wantedView.clearAnimation();
        }
    }

    void startAnimation(final int position) {
        Log.i(TAG, "警报i:" + position);
        int wantedChild;
        wantedChild = getActualPosition(position);
        View wantedView = mDeviceList.getChildAt(wantedChild);
        Log.i(TAG, "view = " + wantedView);
        if (wantedView != null) {
            ItemMainLayout layout = (ItemMainLayout) wantedView;
            View v = layout.getItemCustomLayout().getCustomView();
            v.setBackgroundColor(getResources().getColor(R.color.textbg_red));
            showItemViewAnimation(wantedView, position);
        }


        long[] pattern = {400, 400, 400, 400}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 2); //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    Map<String, MediaPlayer> mPlayer = new HashMap<String, MediaPlayer>();

    private void stopAlertRingtone(final BtDevice d) {
        MediaPlayer player = mPlayer.get(d.getAddress());
        if (player == null) {
            Log.i(TAG, "warning: mediaplayer some thing error!");
            return;
        }

        player.stop();
        player.release();

        mPlayer.remove(d.getAddress());
    }

    private void playAlertRingtone(final BtDevice d) {
        Log.i(TAG, "playAlertRingtone_" + d.getName());
        MediaPlayer player = mPlayer.get(d.getAddress());
        if (player != null) {
            Log.i(TAG, "media player is ringing");
            return;
        }

        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 优先防丢报警
        if (d.isLostAlert() && d.isAntiLostSwitch()) {
            Log.i(TAG, "防丢报警");
            player = MediaPlayer.create(this, d.getAlertRingtone());
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, d.getAlertVolume(), 0);
        } else /* if ( d.isReportAlert()) */ {
            Log.i(TAG, "双击报警");
            player = MediaPlayer.create(this, d.getFindAlertRingtone());
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, d.getFindAlertVolume(), 0);
            //vibrator.vibrate(3000);
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if ((d.isLostAlert() && d.isAntiLostSwitch()) || d.isReportAlert()) {
                    Log.i(TAG, "media player is ringing d = " + d);
                    int disturb = mSharedPreferences.getInt("disturb_status", 0);
                    if (disturb == 0) {     // 免打扰模式没有打开，播放声音
                        mediaPlayer.start();
                    }
                } else {
                    mediaPlayer.release();
                    mPlayer.remove(d.getAddress());
                }
            }
        });

        mPlayer.put(d.getAddress(), player);
        player.setVolume(1.0f, 1.0f);
        player.start();
    }

    private void fillListData() {
        mListData = new ArrayList<BtDevice>();
        mDeviceListAdapter = new DeviceListAdapter(
                DeviceListActivity.this, mListData, this);
        mDeviceList.setAdapter(mDeviceListAdapter);
//        mListData = mDeviceDao.queryAll();
//        if (mListData != null && mListData.size() > 0) {
//            Log.i(TAG, "mListData size:" + mListData.size());
//            mDeviceListAdapter.notifyDataSetChanged();
//            String mac = mSharedPreferences.getString("mac", "");
//            for (int i = 0; i < mListData.size(); i++) {
//                if (mac.equals(mListData.get(i))) {
//                    connectBLE((mListData.get(i).getAddress()));
//                    break;
//                }
//            }
//        } else {
//            Log.i(TAG, "mListData null");
//            mListData = new ArrayList<BtDevice>();
//        }
//        Log.i(TAG, "fillListData_after");
    }


    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (mConnection != null) {
            try {
                Log.i(TAG, "onDestroy->>unregisterCallback");
                mService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        getApplicationContext().unbindService(mConnection);
        Log.i(TAG, "onDestroy->>unbindService");
        mHandlerThread.quit();
        super.onDestroy();
    }

    private void scanLeDevice(final boolean enable) {
        BluetoothManager mBluetoothManager;
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            Log.e(TAG, "Unable to initialize BluetoothManager.");
            return;
        }
        final BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Log.i(TAG, "stop scanning");
                    if (isFristScanner) {
                        Log.i(TAG, "设备自动连接");
                        //10s后判断上次连接的设备有没有扫描到  扫描到就直接连接

                        //String mac = mSharedPreferences.getString("mac", "");
                        //Log.i(TAG, "mac:" + mac);
                        for (int i = 0; i < mListData.size(); i++) {
                            //以前连接过  则自动连接
                            if (mDeviceDao.queryById(mListData.get(i).getAddress()) != null) {
                                Log.i(TAG, "real connnet mac:" + mListData.get(i).getAddress());
                                Log.i(TAG, "getStatus:" + mListData.get(i).getStatus());
                                if (mListData.get(i).getStatus() == BluetoothLeClass.BLE_STATE_INIT ||
                                        mListData.get(i).getStatus() == -1) {
                                    connectBLE(mListData.get(i).getAddress());
                                    mListData.get(i).setStatus(BluetoothLeClass.BLE_STATE_CONNECTING);
                                    mDeviceListAdapter.notifyDataSetChanged();
                                }

                            }
                        }
                        isFristScanner = false;
                    }
                    mScanningStopped = true;
                    closeLoadingDialog();
                }
            }, 10 * 1000);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mScanningStopped = false;
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanningStopped = true;
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

                    addDevice(device.getAddress(), device.getName(), rssi);
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
        ensureStopTorch();
    }

    public void turnOnImmediateAlert(String addr) {
        try {
            mService.turnOnImmediateAlert(addr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void turnOffImmediateAlert(String addr) {
        try {
            mService.turnOffImmediateAlert(addr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                showLoadingDialog(getResources().getString(R.string.waiting));
                isFristScanner = false;
                scanLeDevice(true);
                break;

            case R.id.testkey: {
                Toast.makeText(this, R.string.prompt, Toast.LENGTH_SHORT).show();
                break;
            }

            default:
                break;
        }
    }

    public void addDevice(final String address, final String name, final int rssi) {
        Log.i(TAG, "addDevice called----" + "address:" + address + "  name:" + name);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //列表中已经存在就更改信号状态
                for (int i = 0; i < mListData.size(); i++) {
                    BtDevice d = mListData.get(i);
                    if (d.getAddress().equals(address)) {
                        d.setRssi(rssi);
                        return;
                    }
                }
                BtDevice device = mDeviceDao.queryById(address);
                if (device != null) {
                    device.setRssi(rssi);
                    mDeviceDao.update(device);
                } else if (device == null && name != null && name.equals("HAITUN")) {
                    device = new BtDevice();
                    device.setName(name);
                    device.setAddress(address);
                    device.setRssi(rssi);
                } else
                    return;
                mListData.add(device);
                mDeviceListAdapter.notifyDataSetChanged();
            }
        });
    }

    //private boolean mKeydownFlag;
    private final Object mKeyLock = new Object();

    void registerDatabase(BtDevice device) {
        Log.i(TAG, "registerDatabase");
        BtDevice d = mDeviceDao.queryById(device.getAddress());
        if (d == null) {
            mDeviceDao.insert(device);
        }
    }

    private ICallback.Stub mCallback = new ICallback.Stub() {
        @Override
        public void onConnect(final String address) throws RemoteException {
            Log.i(TAG, "onConnect  address：" + address);
            for (int i = 0; i < mListData.size(); i++) {
                final BtDevice d = mListData.get(i);
                if (address.equals(d.getAddress())) {
                    //停止动画
                    final int finalI = i;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopAnimation(finalI);
                            d.setReportAlert(false);
                            stopAlertRingtone(d);
                        }
                    });

                }
            }

//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mDeviceListAdapter.notifyDataSetChanged();
//                    for (BtDevice d : mListData) {
//                        if (d.getAddress().equals(address)) {
//                            d.setLostAlert(false);
////                            d.setDisconnect(false);
//                        }
//                    }
//                }
//            });
        }

        @Override
        public void onDisconnect(String address) throws RemoteException {
            handleDisconnect(address);
        }

        @Override
        public boolean onRead(String address, byte[] val) throws RemoteException {
            Log.i(TAG, "onRead called");
            return false;
        }

        /**
         * 双击报警
         * @param address
         */
        void startAlert(String address) {
            Log.i(TAG, "startAlert-->>>" + address);
            for (int i = 0; i < mListData.size(); i++) {
                BtDevice d = mListData.get(i);
                if (d.getAddress().equals(address)) {
                    int disturb = mSharedPreferences.getInt("disturb_status", 0);
                    if (disturb == 0) {     // 免打扰模式没有打开，播放声音
                        playAlertRingtone(d);
                        d.setReportAlert(true);
                    }
                    startAnimation(i);
                    if (d.isFindAlertSwitch()) {
                        flashTorch();
                    }
                }
            }
        }

        @Override
        public boolean onWrite(final String address, byte[] val) throws RemoteException {
            Log.i(TAG, "onWrite called");
            String active = getTopActivity();
            if (active != null && active.contains("CameraActivity")) {
                return false;
            }
            Log.i(TAG, "testonWrite called");
            byte v = val[0];
            Runnable r = mKeyChecker.get(address);

            if (r != null && v == 1) {
                synchronized (mKeyLock) {
                    mHandler.removeCallbacks(r);
                    mKeyChecker.remove(address);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    /* turn on alert */
                        Log.i(TAG, "double key down detect!");
                        startAlert(address);
                    }
                });

                return true;
            }

            if (r == null && v == 1) {
                //先判断是否处于报警状态  是则更新界面 取消报警  不是则定位
                for (BtDevice device : mListData) {
                    if (device.getAddress().equals(address) &&
                            device.getStatus() == BluetoothLeClass.BLE_STATE_ALERTING) {
                        //取消报警
                        Log.i(TAG, "取消报警");
                        device.setStatus(BluetoothLeClass.BLE_STATE_CONNECTED);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (mKeyLock) {
                                    mKeyChecker.remove(address);
                                }
                                mDeviceListAdapter.notifyDataSetChanged();
                            }
                        });

                        return true;
                    }
                }


                r = new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "one key down detect!");
                        synchronized (mKeyLock) {
                            mKeyChecker.remove(address);
                        }
                        //记录地理位置
                        recordLocHistory(address);
                    }
                };

                synchronized (mKeyLock) {
                    mKeyChecker.put(address, r);
                }
                //一秒后执行
                mHandler.postDelayed(r, 1000);
            }

            return true;
        }

        @Override
        public void onSignalChanged(String address, int rssi) throws RemoteException {
            synchronized (mListData) {
                Log.i(TAG, "onSignalChanged called address = " + address + " rssi = " + rssi);

                for (int i = 0; i < mListData.size(); i++) {
                    BtDevice d = mListData.get(i);
                    if (d.getAddress().equals(address)) {
                        d.setRssi(rssi);
                    }
                }
            }
        }

        public void onPositionChanged(String address, int position) throws RemoteException {
            synchronized (mListData) {
                Log.i(TAG, "onPositionChanged called address = " + address + " newpos = " + position);

                for (int i = 0; i < mListData.size(); i++) {
                    BtDevice d = mListData.get(i);
                    if (d.getAddress().equals(address)) {
                        d.setPosition(position);
                    }
                }
            }

//            checkAntiLost();
        }

        //保证 服务发现回调
        @Override
        public void onAlertServiceDiscovery(final String btaddr, boolean support) throws RemoteException {
            Log.d(TAG, "onAlertServiceDiscovery");
            synchronized (mLock) {
                Runnable r = mTimer.get(btaddr);
                if (r != null) {
                    mTimer.remove(btaddr);
                    mHandler.removeCallbacks(r);
                }
                realConnect(btaddr, support);
            }
        }
    };

    /**
     * 打算报警
     */
    private void startReportPolice(final String address) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDeviceListAdapter.notifyDataSetChanged();
                checkAntiLost(address);
            }
        });
    }

    /**
     * 断开处理
     *
     * @param address
     */
    private void handleDisconnect(final String address) {
        synchronized (mListData) {
            Log.e(TAG, "onDisconnect called  address：" + address);
            for (int i = 0; i < mListData.size(); i++) {
                final BtDevice d = mListData.get(i);
                if (d.getAddress().equals(address)) {
                    if (!d.isDisconnect()) {
                        Log.i(TAG, "断开报警");
                        d.setDisconnect(true);
                        d.setPosition(BtDevice.LOST);
                        if (!d.isStopReportPolice()) {
                            if (timer != null) {
                                timer.cancel();
                            }

                            //设置定时器 到达时间后还是断开状态 则报警
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (d.isDisconnect()) {
                                        d.setStatus(BluetoothAntiLostDevice.BLE_STATE_INIT);
                                        d.setPosition(BtDevice.LOST);
                                        //报警
                                        startReportPolice(address);
                                    }
                                }
                            }, RELINKTIME * 1000);  //只会执行一次
                            reConnect(address);
                        }
                    } else {
                        Log.i(TAG, "断开不报警");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!d.isAlertingPolice()) {
                                    d.setStatus(BluetoothAntiLostDevice.BLE_STATE_INIT);
                                    d.setLostAlert(false);
                                    d.setPosition(BtDevice.OK);
                                    mDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        }

    }

    /**
     * 检查是否真的连接上Ble设备
     *
     * @param mac
     * @param support
     * @throws RemoteException
     */
    private void realConnect(String mac, boolean support) throws RemoteException {
        Log.i(TAG, "realConnect");
        for (int i = 0; i < mListData.size(); i++) {
            final BtDevice d = mListData.get(i);
            if (d.getAddress().equals(mac)) {
                if (support) {
                    Log.i(TAG, "realConnect:" + support);
                    if (timer != null) {
                        timer.cancel();
                    }
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("mac", mac);
                    editor.commit();
                    d.setDisconnect(false);
                    d.setLostAlert(false);
                    d.setAlertService(support);
                    d.setStatus(BluetoothAntiLostDevice.BLE_STATE_CONNECTED);
                    d.setPosition(BtDevice.OK);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            registerDatabase(d);
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                    d.setStopReportPolice(false);
                    d.setAlertingPolice(false);
                } else {
                    mService.disconnect(mac);
                }
            }
        }
    }

    /**
     * 先连接，后考虑是否报警
     *
     * @param address
     */
    private void reConnect(String address) {
        Log.i(TAG, "reConnect");
        connectBLE(address);
//        if (!connectBLE(address)) {
////            Log.i(TAG, "!connectBLE(address)");
////            try {
////                Thread.sleep(QUICKRELINKTIME * 1000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////            connectBLE(address);
//        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mService = IService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    isFristScanner = true;
                    scanLeDevice(true);
                }
            });
        }
    };

    /**
     * 防丢
     *
     * @param address
     * @return
     */
    boolean checkAntiLost(String address) {
        Log.i(TAG, "checkAntiLost");
        boolean ret = false;
        boolean oldstatus;

        for (BtDevice d : mListData) {
            if (address.equals(d.getAddress())) {
                if (d.isAntiLostSwitch()) {
                    oldstatus = d.isLostAlert();

                    switch (d.getPosition()) {
                        case BtDevice.LOST:
                        case BtDevice.FAR: {
                            Log.i(TAG, "checkAntiLost_lost_d.setLostAlert(true)");
                            d.setLostAlert(true);
                            break;
                        }

                        case BtDevice.OK: {
                            Log.i(TAG, "checkAntiLost_lost_d.setLostAlert(false)");
                            d.setLostAlert(false);
                            break;
                        }

                        default:
                            break;
                    }
                    Log.i(TAG, "oldstatus = " + oldstatus + " lostalert =" + d.isLostAlert());
                    // 丢失状态变化了，记录这个变化
                    if (oldstatus ^ d.isLostAlert()) {
                        recordLostHistory(d);
                    }
                    ret = true;
                }
                break;
            }
        }

        try {
            mService.setAntiLost(true);
            Log.i(TAG, "mService.setAntiLost(true)");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        checkUI(address);
        return ret;
    }

    private void recordLocHistory(String btaddr) {
        if (MyApplication.getInstance().islocation == 0) {
            showShortToast(getString(R.string.str_wait_for_position));
            return;
        }

        String address = PreferenceUtil.getInstance(DeviceListActivity.this).getString(PreferenceUtil.LOCATION, "广东省深圳市");
        String longitude = PreferenceUtil.getInstance(DeviceListActivity.this).getString(PreferenceUtil.LON, "22");
        String latitude = PreferenceUtil.getInstance(DeviceListActivity.this).getString(PreferenceUtil.LAT, "105");
        long datetime = new Date().getTime();
        int status = LocationRecord.FOUND;

        LocationRecord r = new LocationRecord(-1, btaddr, longitude + "," + latitude, address, datetime, status);
        int id = mLocationDao.insert(r);
        r.setId(id);

        showShortToast(getString(R.string.str_position_success));
    }

    private void recordLostHistory(BtDevice d) {
        if (MyApplication.getInstance().islocation == 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showShortToast("waiting for positioning!");
                }
            });
            return;
        }

        String address = PreferenceUtil.getInstance(DeviceListActivity.this).getString(PreferenceUtil.LOCATION, "广东省深圳市");
        String longitude = PreferenceUtil.getInstance(DeviceListActivity.this).getString(PreferenceUtil.LON, "22");
        String latitude = PreferenceUtil.getInstance(DeviceListActivity.this).getString(PreferenceUtil.LAT, "105");
        long datetime = new Date().getTime();
        int status;

        if (d.isLostAlert()) {
            status = LocationRecord.LOST;
        } else {
            status = LocationRecord.FOUND;
        }

        LocationRecord r = new LocationRecord(-1, d.getAddress(), longitude + "," + latitude, address, datetime, status);
        int id = mLocationDao.insert(r);
        r.setId(id);
    }

    public boolean connectBLE(final String address) {
        boolean ret = false;
        try {
            ret = mService.connect(address);
            if (ret) {
                Log.e(TAG, "connect to " + address + " success");
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock) {
                            mTimer.remove(address);
                            for (int i = 0; i < mListData.size(); i++) {
                                BtDevice d = mListData.get(i);
                                if (d.getAddress().equals(address)) {
                                    Log.d(TAG, "get service list form " + address + " time out");
                                    d.setStatus(BluetoothAntiLostDevice.BLE_STATE_INIT);
                                    d.setPosition(BtDevice.LOST);
                                }
                            }
                        }
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                };
                if (mTimer.containsKey(address))
                    mHandler.removeCallbacks(mTimer.get(address));
                mTimer.put(address, r);
                mHandler.postDelayed(r, DISCOVER_SERVICE_TIMEOUT);
            } else {
                Log.e(TAG, "connect to " + address + " failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(TAG, "xxx id = " + id);
    }

    /**
     * 点击报警 停止报警 连接按钮
     *
     * @param view
     * @param position
     */
    @Override
    public void onButtonClick(View view, int position) {
        Button v = (Button) view;
        if (mListData.get(position) != null) {
            int status = mListData.get(position).getStatus();
            Log.i(TAG, "status = " + status);
            switch (status) {
                case BluetoothLeClass.BLE_STATE_CONNECTED: {
                    turnOnImmediateAlert((mListData.get(position).getAddress()));
                    v.setText(R.string.stop_alert);
                    mListData.get(position).setStatus(BluetoothLeClass.BLE_STATE_ALERTING);
                    break;
                }

                case BluetoothLeClass.BLE_STATE_ALERTING: {
                    turnOffImmediateAlert((mListData.get(position).getAddress()));
                    mListData.get(position).setStatus(BluetoothLeClass.BLE_STATE_CONNECTED);
                    v.setText(R.string.alert);
                    break;
                }
                case BluetoothLeClass.BLE_STATE_BREAK:
                    mListData.get(position).setStopReportPolice(true);
                    mListData.get(position).setLostAlert(false);
                    mListData.get(position).setStatus(BluetoothLeClass.BLE_STATE_INIT);
                    v.setText(R.string.connect);
                    break;

                case BluetoothLeClass.BLE_STATE_CONNECTING: {
                    break;
                }

                default:
                case BluetoothLeClass.BLE_STATE_INIT: {
                    synchronized (mListData) {
                        if (connectBLE((mListData.get(position).getAddress()))) {
                            v.setText(R.string.disconnect);
                            mListData.get(position).setStatus(BluetoothLeClass.BLE_STATE_CONNECTING);
                        }
                    }
                    break;
                }
            }
            mDeviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRightArrowClick(int position) {
        BtDevice d = mListData.get(position);
        if (d.getStatus() == BluetoothLeClass.BLE_STATE_CONNECTED || d.getStatus() == BluetoothLeClass.BLE_STATE_ALERTING) {
            Intent i = new Intent(this, BtDeviceSettingActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("device", d);
            i.putExtras(b);
            startActivityForResult(i, CHANGE_BLE_DEVICE_SETTING);
        } else {
            Toast.makeText(this, R.string.str_connect_first, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CHANGE_BLE_DEVICE_SETTING) {
                Bundle b = data.getExtras();
                int changed = b.getInt("ret", 0);

                Log.i(TAG, "changed = " + changed);

                if (changed == 1) {
                    int i;
                    BtDevice d = (BtDevice) b.getSerializable("device");
                    for (i = 0; i < mListData.size(); i++) {
                        if (mListData.get(i).getAddress().equals(d.getAddress())) {
                            break;
                        }
                    }
                    Log.i(TAG, "i = " + i + " ,d = " + d);
                    if (i != mListData.size()) {
                        mListData.remove(i);
                        mListData.add(i, d);
                        mDeviceListAdapter.notifyDataSetChanged();
                    }

//                    checkAntiLost();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListItemLongClick(View view, int position) {
        // Toast.makeText(DeviceListActivity.this, "onItemLongClick   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onListItemLongClick   " + position);
    }

    @Override
    public void onDragViewStart(int position) {
        // Toast.makeText(DeviceListActivity.this, "onDragViewStart   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewStart   " + position);
    }

    @Override
    public void onDragViewMoving(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewMoving   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i("yuyidong", "onDragViewMoving   " + position);
    }

    @Override
    public void onDragViewDown(int position) {
        //Toast.makeText(DeviceListActivity.this, "onDragViewDown   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewDown   " + position);
    }

    @Override
    public void onListItemClick(View v, int position) {
        // Toast.makeText(DeviceListActivity.this, "onItemClick   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onListItemClick   " + position);
        if (position < 0) {
            return;
        }


        stopAnimation(position);
        BtDevice d = mListData.get(position);
        d.setReportAlert(false);
        stopAlertRingtone(d);
        ensureStopTorch();
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        //   Toast.makeText(DeviceListActivity.this, "onSlideOpen   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onSlideOpen   " + position);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        //     Toast.makeText(DeviceListActivity.this, "onSlideClose   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onSlideClose   " + position);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        Log.i(TAG, "onMenuItemClick   " + itemPosition + "   " + buttonPosition + "   " + direction);
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_NOTHING;
                    case 1:
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;

            //右划  删除
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0: {
                        BtDevice d = mListData.get(itemPosition);
                        mDeviceListAdapter.updateDataSet(itemPosition - mDeviceList.getHeaderViewsCount());
                        Log.e(TAG, "mListData:" + mListData.size() + "  mDeviceListAdapter:" + mDeviceListAdapter.getCount());
                        try {
                            mService.disconnect(d.getAddress());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        mDeviceDao.deleteById(d.getAddress());
                        stopAnimation(itemPosition);
                        stopAlertRingtone(d);
                        ensureStopTorch();


                        return Menu.ITEM_SCROLL_BACK;
                    }

                    case 1: {
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                    }
                }
        }

        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDelete(View view, int position) {

    }

    boolean mOn = false;
    final Object mSync = new Object();
    boolean mStop = false;
    Camera mCamera;

    Runnable flashRun = null;

    void ensureStopTorch() {
        mStop = true;
        myHandler.removeCallbacks(flashRun);
        synchronized (mSync) {
            if (mOn) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        turnOffTorch();
                    }
                });
            }
        }
    }

    void flashTorch() {
        Log.i(TAG, "start flashing, mStop = " + mStop);
        if (!mStop) {
            return;
        }

        flashRun = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "mstop = " + mStop + ", mOn = " + mOn);
                if (mStop && mOn) {
                    turnOffTorch();
                    return;
                }
                if (!mOn) {
                    if (!turnOnTorch()) {
                        return;
                    }
                } else {
                    turnOffTorch();
                }
                myHandler.postDelayed(this, 1000);
            }
        };

        mStop = false;
        boolean ret = myHandler.postDelayed(flashRun, 3000);
        Log.i(TAG, "ret = " + ret + " isAlive = " + mHandlerThread.isAlive());
    }

    boolean turnOnTorch() {
        Log.i(TAG, "turn on");

        int sdkVersion = Build.VERSION.SDK_INT;
        Log.i(TAG, "SDK Version = " + sdkVersion);

        synchronized (mSync) {
            if (mOn) {
                return true;
            }

            try {
                mCamera = Camera.open(0);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "open camera error!");
                return false;
            }

            try {
                Camera.Parameters p = mCamera.getParameters();

                List<String> pList = mCamera.getParameters().getSupportedFlashModes();

                if (pList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    Log.i(TAG, "support torch mode");
                } else {
                    Log.i(TAG, "NOT support torch mode");
                }
                mCamera.setParameters(p);

                if (sdkVersion > Build.VERSION_CODES.KITKAT) {
                    mPreviewTexture = new SurfaceTexture(0);
                    try {
                        mCamera.setPreviewTexture(mPreviewTexture);
                    } catch (IOException ex) {
                        // Ignore
                    }
                }

                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                mCamera.release();
                mCamera = null;
                return false;
            }

            mOn = true;
        }

        return true;
    }

    boolean turnOffTorch() {
        Log.i(TAG, "turn off");

        synchronized (mSync) {
            if (!mOn) {
                return false;
            }

            if (mCamera == null) {
                return false;
            }

            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(p);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mOn = false;

            return true;
        }
    }
}
