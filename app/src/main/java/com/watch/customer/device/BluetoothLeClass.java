package com.watch.customer.device;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeClass {
    private final static String TAG = "BluetoothLeClass";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    protected BluetoothGatt mBluetoothGatt;

    static public final int BLE_STATE_INIT = 0;
    static public final int BLE_STATE_CONNECTED = 1;
    static public final int BLE_STATE_CONNECTING = 2;
    static public final int BLE_STATE_ERROR = 3;
    static public final int BLE_STATE_ALERTING = 4;
    static public final int BLE_STATE_BREAK = 5;

    protected int mBleStatus = BLE_STATE_INIT;

    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public interface OnConnectListener {
        public void onConnect(BluetoothGatt gatt);
    }
    public interface OnDisconnectListener {
        public void onDisconnect(BluetoothGatt gatt);
    }
    public interface OnServiceDiscoverListener {
        public void onServiceDiscover(BluetoothGatt gatt);
    }
    public interface OnDataAvailableListener {
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status);
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic);
    }

    public interface  OnReadRemoteRssiListener {
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
    }

    private OnConnectListener mOnConnectListener;
    private OnDisconnectListener mOnDisconnectListener;
    private OnServiceDiscoverListener mOnServiceDiscoverListener;
    private OnDataAvailableListener mOnDataAvailableListener;
    private OnReadRemoteRssiListener mOnReadRemoteRssiListener;
    private Context mContext;
    public void setOnConnectListener(OnConnectListener l){
        mOnConnectListener = l;
    }
    public void setOnDisconnectListener(OnDisconnectListener l){
        mOnDisconnectListener = l;
    }
    public void setOnServiceDiscoverListener(OnServiceDiscoverListener l){
        mOnServiceDiscoverListener = l;
    }
    public void setOnDataAvailableListener(OnDataAvailableListener l){
        mOnDataAvailableListener = l;
    }
    public void setOnReadRemoteRssiListener(OnReadRemoteRssiListener l) {
        mOnReadRemoteRssiListener = l;
    }

    public BluetoothLeClass(Context c){
        mContext = c;
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (mOnConnectListener != null)
                    mOnConnectListener.onConnect(gatt);
                mBleStatus = BLE_STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                boolean ret = mBluetoothGatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery:" + ret);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mOnDisconnectListener != null)
                    mOnDisconnectListener.onDisconnect(gatt);
                Log.i(TAG, "Disconnected from GATT server.");
                mBleStatus = BLE_STATE_INIT;
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                }
                mBluetoothGatt = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && mOnServiceDiscoverListener!=null) {
                mOnServiceDiscoverListener.onServiceDiscover(gatt);
                mBleStatus = BLE_STATE_CONNECTED;
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
                mBleStatus = BLE_STATE_ERROR;
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (mOnDataAvailableListener!=null)
                mOnDataAvailableListener.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if (mOnDataAvailableListener!=null)
                mOnDataAvailableListener.onCharacteristicWrite(gatt, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (mOnReadRemoteRssiListener != null) {
                mOnReadRemoteRssiListener.onReadRemoteRssi(gatt, rssi, status);
            }
        }
    };

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
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
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        //boolean来表明当BLE设备可用时是否主动去连接它
        mBluetoothGatt = device.connectGatt(mContext, true , mGattCallback);

        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
   //     mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readRemoteRssi() {
        mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     */
    public void setCharacteristicNotification(UUID serviceUuid, UUID characteristicUuid,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        BluetoothGattService gattService = mBluetoothGatt.getService(serviceUuid);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(characteristicUuid);

            Log.e("hjq", "char = " + characteristic);

            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
            descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[]{0x00, 0x00});
            mBluetoothGatt.writeDescriptor(descriptor);
        } else {
            Log.e("hjq", "service id = " + serviceUuid + " is not support!");
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic){
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }

}

