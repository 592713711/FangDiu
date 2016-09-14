package com.watch.customer.device;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.watch.customer.util.Utils;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 16-3-9.
 */
public class BluetoothAntiLostDevice extends BluetoothLeClass {
    public static final UUID POWER_SERVICE_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID POWER_FUNC_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");

    public static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_FUNC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    public static final UUID KEY_FUNC_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static final UUID KEY_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

    public static final UUID ALERT_SERVICE_UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    public static final UUID ALERT_FUNC_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");

    public static final UUID LOSS_SERVICE_UUID = UUID.fromString("00001803-0000-1000-8000-00805f9b34fb");
    public static final UUID LOSS_FUNC_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");

    public static final int ALERT_ON = 2;
    public static final int ALERT_OFF = 0;

    private final String TAG = "hjq";

    public BluetoothAntiLostDevice(Context c) {
        super(c);
    }

    public boolean checkBleStatus(){
        Log.e(TAG, "check ble status = " + mBleStatus);
        return mBleStatus == BLE_STATE_CONNECTED;
    }

    public int getBleStatus() {
        return mBleStatus;
    }

    public void enableKeyReport(boolean on) {
        Log.e(TAG, "enable notification");

        if (!checkBleStatus()) {
            return;
        }

        //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
        setCharacteristicNotification(KEY_SERVICE_UUID, KEY_FUNC_UUID, on);

        BluetoothGattService service = mBluetoothGatt.getService(KEY_SERVICE_UUID);
        if (service == null) {
            return;
        }

        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(KEY_SERVICE_UUID).getCharacteristic(KEY_FUNC_UUID);

        if (characteristic != null) {
            characteristic.setValue("send data->");
            //往蓝牙模块写入数据
            writeCharacteristic(characteristic);
        }
    }

    public void getLinkLossSetting() {
        Log.e(TAG, "getLinkLossSetting()");
        if (!checkBleStatus()) {
            return;
        }
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(LOSS_SERVICE_UUID).getCharacteristic(LOSS_FUNC_UUID);
        if (characteristic == null) {
            Log.e(TAG, "not support the loss service?");
            return;
        }

        readCharacteristic(characteristic);
    }

    public void setLinkLossSetting(byte val) {
        Log.e(TAG, "setLinkLossSetting()");
        if (!checkBleStatus()) {
            return;
        }
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(LOSS_SERVICE_UUID).getCharacteristic(LOSS_FUNC_UUID);
        if (characteristic == null) {
            Log.e(TAG, "not support the loss service?");
            return;
        }

        if (characteristic != null) {
            characteristic.setValue(new byte[] { val });
            //往蓝牙模块写入数据
            writeCharacteristic(characteristic);
        }
    }

    public void getBatteryLevel() {
        Log.e(TAG, "getBatteryLevel()");
        if (!checkBleStatus()) {
            return;
        }
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(BATTERY_SERVICE_UUID).getCharacteristic(BATTERY_FUNC_UUID);
        if (characteristic == null) {
            Log.e(TAG, "not support the loss service?");
            return;
        }

        readCharacteristic(characteristic);
    }

    public void setImmediateAlert(int val) {
        Log.e(TAG, "setImmediateAlert() value = " + val);
        if (!checkBleStatus()) {
            return;
        }

        BluetoothGattService service = mBluetoothGatt.getService(ALERT_SERVICE_UUID);

        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(ALERT_FUNC_UUID);
            if (characteristic == null) {
                Log.e(TAG, "not support the immediate alert funcion?");
                return;
            }

            if (characteristic != null) {
                characteristic.setValue(new byte[] { (byte)val });
                //往蓝牙模块写入数据
                writeCharacteristic(characteristic);
            }
        } else {
            Log.e(TAG, "not support the immediate alert service?");
        }

    }

    public void turnOnImmediateAlert() {
        setImmediateAlert(ALERT_ON);
    }

    public void turnOffImmediateAlert() {
        setImmediateAlert(ALERT_OFF);
    }
}

