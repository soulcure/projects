package com.applidium.nickelodeon.entity;


/**
 * Created by john on 2015/5/28.
 */
public class UserInfo {

    private String mobile;
    private String nickname;
    private String email;
    private String lang;
    private String birthday;
    private String gender;
    private String address;
    private String balance;
    private String userImg;
    private String vipExpiryTime;
    private boolean vipExpiryTip;

    private int vipLevel;   // 0=注册用户，1=会员
    private String vipRenew;
    private String username;
    private String password;
    private String currentTime;
    private String firstModifiedTime;

    public String getMobile() {
        return mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getLang() {
        return lang;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getBalance() {
        return balance;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getVipExpiryTime() {
        return vipExpiryTime;
    }

    public boolean isVipExpiryTip() {
        return vipExpiryTip;
    }

    public boolean isVip() {
        boolean isVip = false;
        if (vipLevel == 1) {
            isVip = true;
        }
        return isVip;
    }

    public String getVipRenew() {
        return vipRenew;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getFirstModifiedTime() {
        return firstModifiedTime;
    }


}
