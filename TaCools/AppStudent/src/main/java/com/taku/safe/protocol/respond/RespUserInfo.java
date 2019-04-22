package com.taku.safe.protocol.respond;


import android.text.TextUtils;

/**
 * Created by colin on 2017/6/6.
 */

public class RespUserInfo extends RespBaseBean {

    /**
     * birthday : null
     * avataUrl : null
     * gender : 1
     * name : 琼瑶
     * phoneNo : null
     */

    private String birthday;
    private String avataUrl;
    private int gender;  //1 男  2女
    private String name;
    private String phoneNo;

    public String getBirthday() {
        if (TextUtils.isEmpty(birthday)) {
            return "";
        }
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAvataUrl() {
        if (TextUtils.isEmpty(avataUrl)) {
            return "";
        }
        return avataUrl;
    }

    public void setAvataUrl(String avataUrl) {
        this.avataUrl = avataUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        if (TextUtils.isEmpty(phoneNo)) {
            return "";
        }
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
