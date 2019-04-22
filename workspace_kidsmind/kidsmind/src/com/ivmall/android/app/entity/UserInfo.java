package com.ivmall.android.app.entity;


/**
 * Created by john on 2015/5/28.
 */
public class UserInfo {

    private int userId;  //用户ID

    private VipType vipLevel;    //用户等级 device=匿名,register=注册,paid=付费
    private String vipExpiresTime;  //Vip过期时间  yyyy-MM-dd HH:mm:ss

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    private boolean invited;
    private String name;
    private String mobile;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }




    public int getUserId() {
        return userId;
    }

    public VipType getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(VipType vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getVipExpiresTime() {
        return vipExpiresTime;
    }

    public void setVipExpiresTime(String vipExpiresTime) {
        this.vipExpiresTime = vipExpiresTime;
    }

    public enum VipType {
        device, register, paid
    }
}
