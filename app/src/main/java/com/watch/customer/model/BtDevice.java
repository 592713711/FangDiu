package com.watch.customer.model;

import com.uacent.watchapp.R;

import java.io.Serializable;

/**
 * Created by Administrator on 16-3-7.
 */
public class BtDevice implements Serializable {
    // private String name;
    private String thumbnail;

    public BtDevice copy() {

        BtDevice d = new BtDevice();

        d.setAntiLostSwitch(this.isAntiLostSwitch());
        d.setLostAlertSwitch(this.isLostAlertSwitch());
        d.setAlertDistance(this.getAlertDistance());
        d.setAlertVolume(this.getAlertVolume());
        d.setAlertRingtone(this.getAlertRingtone());
        d.setFindAlertSwitch(this.isFindAlertSwitch());
        d.setFindAlertRingtone(this.getFindAlertRingtone());
        d.setFindAlertVolume(this.getFindAlertVolume());
        d.setLostAlert(this.isLostAlert());
        d.setStatus(this.getStatus());
        d.setThumbnail(this.getThumbnail());
        d.setAddress(this.getAddress());
        d.setName(this.getName());

        d.setAlertService(this.isAlertService());
        d.setReportAlert(this.isReportAlert());

        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BtDevice device = (BtDevice) o;

        if (isAntiLostSwitch() != device.isAntiLostSwitch()) return false;
        if (isLostAlertSwitch() != device.isLostAlertSwitch()) return false;
        if (getAlertDistance() != device.getAlertDistance()) return false;
        if (getAlertVolume() != device.getAlertVolume()) return false;
        if (getAlertRingtone() != device.getAlertRingtone()) return false;
        if (isFindAlertSwitch() != device.isFindAlertSwitch()) return false;
        if (getFindAlertVolume() != device.getFindAlertVolume()) return false;
        if (getFindAlertRingtone() != device.getFindAlertRingtone()) return false;
        if (isLostAlert() != device.isLostAlert()) return false;
        if (getThumbnail() != null ? !getThumbnail().equals(device.getThumbnail()) : device.getThumbnail() != null)
            return false;
        if (getName() != null ? !getName().equals(device.getName()) : device.getName() != null)
            return false;
        return !(getAddress() != null ? !getAddress().equals(device.getAddress()) : device.getAddress() != null);
    }

    @Override
    public int hashCode() {
        int result = getThumbnail() != null ? getThumbnail().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (isAntiLostSwitch() ? 1 : 0);
        result = 31 * result + (isLostAlertSwitch() ? 1 : 0);
        result = 31 * result + getAlertDistance();
        result = 31 * result + getAlertVolume();
        result = 31 * result + getAlertRingtone();
        result = 31 * result + (isFindAlertSwitch() ? 1 : 0);
        result = 31 * result + getFindAlertVolume();
        result = 31 * result + getFindAlertRingtone();
        result = 31 * result + (isLostAlert() ? 1 : 0);
        result = 31 * result + getStatus();
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAntiLostSwitch(boolean antiLostSwitch) {
        this.antiLostSwitch = antiLostSwitch;
    }

    public void setLostAlertSwitch(boolean lostAlertSwitch) {
        this.lostAlertSwitch = lostAlertSwitch;
    }

    public void setAlertDistance(int alertDistance) {
        this.alertDistance = alertDistance;
    }

    public void setAlertVolume(int alertVolume) {
        this.alertVolume = alertVolume;
    }

    public void setAlertRingtone(int alertRingtone) {
        this.alertRingtone = alertRingtone;
    }

    public void setFindAlertSwitch(boolean findAlertSwitch) {
        this.findAlertSwitch = findAlertSwitch;
    }

    public void setFindAlertVolume(int findAlertVolume) {
        this.findAlertVolume = findAlertVolume;
    }

    public void setFindAlertRingtone(int findAlertRingtone) {
        this.findAlertRingtone = findAlertRingtone;
    }

    private String name;
    private String address;

    public String getAddress() {
        return address;
    }

    public boolean isAntiLostSwitch() {
        return antiLostSwitch;
    }

    public boolean isLostAlertSwitch() {
        return lostAlertSwitch;
    }

    public int getAlertDistance() {
        return alertDistance;
    }

    public int getAlertVolume() {
        return alertVolume;
    }

    public int getAlertRingtone() {
        return alertRingtone;
    }

    public boolean isFindAlertSwitch() {
        return findAlertSwitch;
    }

    public int getFindAlertVolume() {
        return findAlertVolume;
    }

    public int getFindAlertRingtone() {
        return findAlertRingtone;
    }

    // Anti lost setting
    private boolean antiLostSwitch = true;
    private boolean lostAlertSwitch = true;
    private int alertDistance;

    public final static int ALERT_DISTANCE_NEAR = 1;
    public final static int ALERT_DISTANCE_MIDDLE = 2;
    public final static int ALERT_DISTANCE_FAR = 3;

    private int alertVolume = 8;
    private int alertRingtone;

    //
    private boolean findAlertSwitch = true;
    private int findAlertVolume = 8;
    private int findAlertRingtone;
    private int rssi;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    static public final int LOST = 0;
    static public final int FAR = 1;
    static public final int MIDDLE = 2;
    static public final int NEAR = 3;
    static public final int OK = 4;

    public boolean isLostAlert() {
        return lostAlert;
    }

    public void setLostAlert(boolean lostAlert) {
        this.lostAlert = lostAlert;
    }

    private boolean lostAlert;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public int getRssi() {
        return rssi;
    }

    public void setThumbnail(String image) {
        thumbnail = image;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setRssi(int s) {
        rssi = s;
    }

    private boolean alertService;       // 是否支持报警服务
    private boolean reportAlert;        // 是否主动由防丢器发起的报警

    @Override
    public String toString() {
        return "BtDevice{" +
                "thumbnail='" + thumbnail + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", antiLostSwitch=" + antiLostSwitch +
                ", lostAlertSwitch=" + lostAlertSwitch +
                ", alertDistance=" + alertDistance +
                ", alertVolume=" + alertVolume +
                ", alertRingtone='" + alertRingtone + '\'' +
                ", findAlertSwitch=" + findAlertSwitch +
                ", findAlertVolume=" + findAlertVolume +
                ", findAlertRingtone='" + findAlertRingtone + '\'' +
                ", rssi=" + rssi +
                ", status=" + status +
                ", alertService = " + alertService +
                ", lostAlert = " + lostAlert +
                '}';
    }

    public String getId() {
        return address;
    }

    public BtDevice(String thumbnail, String name, String address, boolean antiLostSwitch,
                    boolean lostAlertSwitch, int alertDistance, int alertVolume,
                    int alertRingtone, boolean findAlertSwitch, int findAlertVolume, int findAlertRingtone, int rssi) {
        this.thumbnail = thumbnail;
        this.name = name;
        this.address = address;
        this.antiLostSwitch = antiLostSwitch;
        this.lostAlertSwitch = lostAlertSwitch;
        this.alertDistance = alertDistance;
        this.alertVolume = alertVolume;
        this.alertRingtone = alertRingtone;
        this.findAlertSwitch = findAlertSwitch;
        this.findAlertVolume = findAlertVolume;
        this.findAlertRingtone = findAlertRingtone;
        this.rssi = rssi;
        status = -1;
        lostAlert = false;
        position = -1;
        alertService = false;
        if (name == null) {
            this.name = "unkown";
        }
        if (this.findAlertRingtone == 0) {
            this.findAlertRingtone = R.raw.alarm;
        }
        if (this.alertRingtone == 0) {
            this.alertRingtone = R.raw.alarm;
        }
    }

    public BtDevice() {
        thumbnail = "";
        name = "";
        address = "";
        antiLostSwitch = true;
        lostAlertSwitch = true;
        alertDistance = ALERT_DISTANCE_FAR;
        alertVolume = 8;
        alertRingtone = R.raw.alarm;
        findAlertSwitch = true;
        findAlertVolume = 8;
        findAlertRingtone = R.raw.alarm;
        position = -1;
        lostAlert = false;
        reportAlert = false;
        alertService = false;
    }

    public boolean isAlertService() {
        return alertService;
    }

    public void setAlertService(boolean alertService) {
        this.alertService = alertService;
    }

    public boolean isReportAlert() {
        return reportAlert;
    }

    public void setReportAlert(boolean reportAlert) {
        this.reportAlert = reportAlert;
    }

    /**
     * 设备是否断开连接
     */
    private boolean isDisconnect = true;

    public boolean isDisconnect() {
        return isDisconnect;
    }

    public void setDisconnect(boolean disconnect) {
        isDisconnect = disconnect;
    }

    private boolean isStopReportPolice = true;

    public boolean isStopReportPolice() {
        return isStopReportPolice;
    }

    public void setStopReportPolice(boolean stopReportPolice) {
        isStopReportPolice = stopReportPolice;
    }

    private boolean isAlertingPolice = false;

    public boolean isAlertingPolice() {
        return isAlertingPolice;
    }

    public void setAlertingPolice(boolean alertingPolice) {
        isAlertingPolice = alertingPolice;
    }
}
