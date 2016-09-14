// ICallback.aidl
package com.watch.customer.ui;

// Declare any non-default types here with import statements
interface ICallback {
       void onConnect(String address);
       void onDisconnect(String address);
       boolean onRead(String address, in byte[] val);
       boolean onWrite(String address, out byte[] val);
       void onSignalChanged(String address, int rssi);
       void onPositionChanged(String address, int position);
       void onAlertServiceDiscovery(String address, boolean support);
}
