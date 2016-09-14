package com.watch.customer.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.watch.customer.device.BluetoothAntiLostDevice;
import com.watch.customer.device.BluetoothLeClass;
import com.watch.customer.device.BluetoothLeClass.OnReadRemoteRssiListener;
import com.watch.customer.model.BtDevice;
import com.watch.customer.ui.ICallback;
import com.watch.customer.ui.IService;
import com.watch.customer.util.PreventAntiLostCore;
import com.watch.customer.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 16-3-15.
 */
public class BleComService extends Service {
    private static final String  TAG = "testBleComService";
    private static final int SCAN_PERIOD = 10000;
    private static final long LIVE_PERIOD = 1000 * 7;       //点击开始扫描后的10秒停止扫描

    private Map<String, BluetoothAntiLostDevice> mActiveDevices = new HashMap<String, BluetoothAntiLostDevice>() ;
    private Map<String, Integer> mScaningRssi = new HashMap<String, Integer>();
    private Map<String,List<Integer>> mlivingRssiData = new HashMap<String,List<Integer>>();

    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
    private boolean antiLostEnabled = false;
    private final Object mSync = new Object();

    public class LocalBinder extends Binder {
        public BleComService getService() {
            return BleComService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
       Log.i(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mCallbacks.kill();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    private IService.Stub mBinder = new IService.Stub() {
        @Override
        public boolean initialize() throws RemoteException {
            return false;
        }

        @Override
        public boolean connect(String addr) throws RemoteException {
            return connectBtDevice(addr);
        }

        @Override
        public void disconnect(String addr) throws RemoteException {
            disconnectBtDevice(addr);
        }

        public void unregisterCallback(ICallback cb){
            if (cb != null) {
                mCallbacks.unregister(cb);
            }
        }

        public void registerCallback(ICallback cb){
            if (cb != null) {
                mCallbacks.register(cb);
            }
        }

        public void turnOffImmediateAlert(String addr) {
            bleTurnOffImmediateAlert(addr);
        }

        public void turnOnImmediateAlert(String addr) {
            bleTurnOnImmediateAlert(addr);
        }

        public void setAntiLost(boolean enable) {
            setBleAntiLost(enable);
        }
    };

    private void bleTurnOnImmediateAlert(String addr) {
        BluetoothAntiLostDevice device = mActiveDevices.get(addr);
        if (device != null) {
            device.turnOnImmediateAlert();
        } else {
            Log.e("hjq", "the device is null?");
        }
    }

    private void bleTurnOffImmediateAlert(String addr) {
        BluetoothAntiLostDevice device = mActiveDevices.get(addr);
        if (device != null) {
            device.turnOffImmediateAlert();
        } else {
            Log.e("hjq", "the device is null?");
        }
    }

    Thread fnAntiLost = null;
    // 15秒获取一次连接的rssi值并进行判断，是否掉线了。
    void setBleAntiLost(boolean enable) {

       Log.i("hjq", "set antilost enable = " + enable);

        if (fnAntiLost != null && fnAntiLost.isAlive()) {
            return;
        }

        fnAntiLost = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        mScaningRssi.clear();
                        boolean bleok = false;
                        for (String k : mActiveDevices.keySet()) {
                            BluetoothAntiLostDevice d = mActiveDevices.get(k);

                            bleok = (d != null && d.getBleStatus() == BluetoothLeClass.BLE_STATE_CONNECTED);
                            if (bleok) {
                                d.readRemoteRssi();
                                // 必须在此处同步回调函数，否则蓝牙协议栈会出错
                                synchronized (mScaningRssi) {
                                    try {
                                        mScaningRssi.wait(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        Log.e("hjq", "ble status = " + bleok);
                        if (!bleok) {
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        // 判断蓝牙设备是否丢失
                        for (String k : mlivingRssiData.keySet()) {
                            int val = -130;    // 蓝牙设备已经丢失的信号值
                            if (mScaningRssi.get(k) != null) {
                                val = mScaningRssi.get(k);
                                mScaningRssi.remove(k);
                            }
                            List<Integer> list = mlivingRssiData.get(k);
                            if (list.size() > 20) {
                                list.remove(0);
                            }
                            list.add(val);
                        }

                        for (String key : mScaningRssi.keySet()) {
                            List<Integer> list = new ArrayList<Integer>();
                            list.add(mScaningRssi.get(key));

                            mlivingRssiData.put(key, list);
                        }

                        Map<String, Double> rssidata = PreventAntiLostCore.getDeviceState(mlivingRssiData);
                        for (String key : rssidata.keySet()) {
                            int rssi = rssidata.get(key).intValue();
                            int pos;

                            Log.e("hjq", "rssi = " + rssi);
                            if (rssi < -100) {
                                pos = BtDevice.LOST;
                            } else {
                                pos = BtDevice.OK;
                            }

                            synchronized (mActiveDevices) {
                                if (mActiveDevices.get(key) == null) {
                                    continue;
                                }
                            }

                            synchronized (mCallbacks) {
                                int n = mCallbacks.beginBroadcast();
                                try {
                                    int i;
                                    for (i = 0; i < n; i++) {
                                        mCallbacks.getBroadcastItem(i).onPositionChanged(key, pos);
                                        mCallbacks.getBroadcastItem(i).onSignalChanged(key, rssi);
                                    }
                                } catch (RemoteException e) {
                                    Log.e(TAG, "remote call exception", e);
                                }
                                mCallbacks.finishBroadcast();
                            }
                        }

                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        fnAntiLost.start();
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    boolean connectBtDevice(String address) {
        boolean ret;

        BluetoothAntiLostDevice d = mActiveDevices.get(address);
        if (d != null) {
            Log.e("hjq", "warning the address: " + address + " is not disconnected");
            d.disconnect();
            d.close();
            mActiveDevices.remove(address);
        }

        BluetoothAntiLostDevice device = new BluetoothAntiLostDevice(this);
        device.initialize();
        device.setOnServiceDiscoverListener(mOnServiceDiscover);
        //收到BLE终端数据交互的事件
        device.setOnDataAvailableListener(mOnDataAvailable);
        device.setOnConnectListener(mOnConnectListener);
        device.setOnDisconnectListener(mOnDisconnectListener);
        device.setOnReadRemoteRssiListener(mOnReadRemoteRssiListener);

        mActiveDevices.put(address, device);

        ret = device.connect(address);
        if (ret)  {
           Log.i(TAG, "connect to " + address + " success");
        } else {
           Log.i(TAG, "connect to " + address + " failed");
        }

        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    void disconnectBtDevice(String address) {
        BluetoothAntiLostDevice device = mActiveDevices.get(address);
        if (device != null) {
            mActiveDevices.remove(address);
            device.disconnect();
        }
    }

    private BluetoothLeClass.OnDisconnectListener mOnDisconnectListener = new BluetoothLeClass.OnDisconnectListener() {
        @Override
        public void onDisconnect(BluetoothGatt gatt) {
            BluetoothDevice device = gatt.getDevice();
            BluetoothAntiLostDevice leDevice = mActiveDevices.get(device.getAddress());
            if (leDevice != null) {
                mActiveDevices.remove(device.getAddress());
            } else {
                Log.e("hjq", "remove address = " + device.getAddress() + " is null");
            }

            synchronized (mCallbacks) {
                int n = mCallbacks.beginBroadcast();
                try {
                    int i;
                    for (i = 0; i < n; i++) {
                        mCallbacks.getBroadcastItem(i).onDisconnect(gatt.getDevice().getAddress());
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "remote call exception", e);
                }
                mCallbacks.finishBroadcast();
            }
        }
    };

    private OnReadRemoteRssiListener mOnReadRemoteRssiListener = new OnReadRemoteRssiListener() {
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BluetoothDevice device = gatt.getDevice();
           Log.i("hjq", "read remote " + device.getAddress() + " rssi = " + rssi + " status = " + status);
            synchronized (mScaningRssi) {
                mScaningRssi.put(gatt.getDevice().getAddress(), rssi);
                mScaningRssi.notify();
            }
        }
    };

    private BluetoothLeClass.OnConnectListener mOnConnectListener = new BluetoothLeClass.OnConnectListener() {
        @Override
        public void onConnect(BluetoothGatt gatt) {
            synchronized (mCallbacks) {
                int n = mCallbacks.beginBroadcast();
                try {
                    int i;
                    for (i = 0; i < n; i++) {
                        mCallbacks.getBroadcastItem(i).onConnect(gatt.getDevice().getAddress());
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "remote call exception", e);
                }
                mCallbacks.finishBroadcast();
            }
        }

    };

    /**
     * 搜索到BLE终端服务的事件
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new BluetoothLeClass.OnServiceDiscoverListener(){
        @Override
        public void onServiceDiscover(BluetoothGatt gatt) {
            BluetoothDevice device = gatt.getDevice();
            BluetoothAntiLostDevice leDevice = mActiveDevices.get(device.getAddress());
            if (leDevice != null) {
                displayGattServices(leDevice.getSupportedGattServices());
                leDevice.enableKeyReport(true);     // 打开上报按键信息
                boolean alertSupport = checkAlertService(leDevice.getSupportedGattServices());
                synchronized (mCallbacks) {
                    int n = mCallbacks.beginBroadcast();
                    try {
                        int i;
                        for (i = 0; i < n; i++) {
                            mCallbacks.getBroadcastItem(i).onAlertServiceDiscovery(device.getAddress(), alertSupport);
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "remote call exception", e);
                    }
                    mCallbacks.finishBroadcast();
                }
            } else {
                Log.e("hjq", "address = " + device.getAddress() + " is null");
            }
        }
    };

    boolean checkAlertService(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return false;
        }

        for (BluetoothGattService gattService : gattServices) {
            final UUID serviceUUID = gattService.getUuid();
            Log.e(TAG,"-->service uuid:" + gattService.getUuid());

            if (!serviceUUID.equals(BluetoothAntiLostDevice.ALERT_SERVICE_UUID)) {
                continue;
            }

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());
                if (gattCharacteristic.getUuid().equals(BluetoothAntiLostDevice.ALERT_FUNC_UUID)) {
                    Log.e(TAG, "support alert service");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 收到BLE终端数据交互的事件
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new BluetoothLeClass.OnDataAvailableListener(){

        /**
         * BLE终端数据被读的事件
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onCharRead " + gatt.getDevice().getName()
                        + " read "
                        + characteristic.getUuid().toString()
                        + " -> "
                        + Utils.bytesToHexString(characteristic.getValue()));

                synchronized (mCallbacks) {
                    int n = mCallbacks.beginBroadcast();
                    try {
                        int i;
                        for (i = 0; i < n; i++) {
                            mCallbacks.getBroadcastItem(i).onRead(gatt.getDevice().getAddress(), characteristic.getValue());
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "remote call exception", e);
                    }
                    mCallbacks.finishBroadcast();
                }
            }
        }

        /**
         * 收到BLE终端写入数据回调
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();

            Log.e(TAG, "onCharWrite " + gatt.getDevice().getName()
                    + " write "
                    + characteristic.getUuid().toString()
                    + " -> "
                    + Utils.bytesToHexString(value));

            synchronized (mCallbacks) {
                int n = mCallbacks.beginBroadcast();
                try {
                    int i;
                    for (i = 0; i < n; i++) {
                        mCallbacks.getBroadcastItem(i).onWrite(gatt.getDevice().getAddress(), characteristic.getValue());
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "remote call exception", e);
                }
                mCallbacks.finishBroadcast();
            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }

        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            int type = gattService.getType();
            final UUID serviceUUID = gattService.getUuid();
            Log.e(TAG,"-->service type:"+Utils.getServiceType(type));
            Log.e(TAG,"-->includedServices size:"+gattService.getIncludedServices().size());
            Log.e(TAG,"-->service uuid:"+gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
                Log.e(TAG,"---->char uuid:"+gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG,"---->char permission:"+Utils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG,"---->char property:"+Utils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG,"---->char value:"+new String(data));
                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG,"-------->desc permission:"+ Utils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "-------->desc value:"+ new String(desData));
                    }
                }
            }
        }
    }
}
