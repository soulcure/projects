package com.taku.safe.protocol.respond;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespSignDetail extends RespBaseBean {


    /**
     * signDate : 2017-01-01 11:22:30
     * location : 广东省深圳市南山区xxx
     * signValid : 0
     * longitude : 212.123123
     * latitude : 19.123123
     * image1 : http://imagecdn/aaa.jpg
     * image2 : http://imagecdn/bbb.jpg
     * image3 : http://imagecdn/ccc.jpg
     * image4 : http://imagecdn/ddd.jpg
     * note :
     * approveNote :
     */

    private String signDate;
    private String location;
    private int signValid;
    private double longitude;
    private double latitude;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String note;
    private String approveNote;
    private int isChanged;
    private String changedTime;


    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSignValid() {
        return signValid;
    }

    public void setSignValid(int signValid) {
        this.signValid = signValid;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<String> getImageList() {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(image1)) {
            list.add(image1);
        }
        if (!TextUtils.isEmpty(image2)) {
            list.add(image2);
        }
        if (!TextUtils.isEmpty(image3)) {
            list.add(image3);
        }
        if (!TextUtils.isEmpty(image4)) {
            list.add(image4);
        }

        return list;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getApproveNote() {
        return approveNote;
    }

    public void setApproveNote(String approveNote) {
        this.approveNote = approveNote;
    }

    public boolean isChanged() {
        return isChanged == 1;
    }

    public void setChanged(int changed) {
        isChanged = changed;
    }

    public String getChangedTime() {
        return changedTime;
    }

    public void setChangedTime(String changedTime) {
        this.changedTime = changedTime;
    }
}
