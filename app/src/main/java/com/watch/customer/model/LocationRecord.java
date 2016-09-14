package com.watch.customer.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 16-3-22.
 */
public class LocationRecord implements Serializable {
    public LocationRecord(int id, String btaddress, String long_lat, String address, long datetime, int status) {
        this.id = id;
        this.btaddress = btaddress;
        this.long_lat = long_lat;
        this.address = address;
        this.datetime = new Date(datetime);
        this.status = status;
    }

    public String getBtaddress() {
        return btaddress;
    }

    public String getLong_lat() {
        return long_lat;
    }

    public String getAddress() {
        return address;
    }

    public Date getDt_time() {
        return datetime;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLongLat(String long_lat) {
        this.long_lat = long_lat;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    int id;

    public void setBtaddress(String btaddress) {
        this.btaddress = btaddress;
    }

    String btaddress;
    String long_lat;
    String address;
    Date datetime;

    // 定位或者丢失
    static final public int LOST = 1;
    static final public int FOUND = 0;
    int status;
}
