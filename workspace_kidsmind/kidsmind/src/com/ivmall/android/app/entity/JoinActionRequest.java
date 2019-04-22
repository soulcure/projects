package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class JoinActionRequest extends ProtocolRequest {


    private String token;
    private String title;
    private String mobile;
    private String name;
    private String address;
    private String coupon;


    public void setToken(String token) {
        this.token = token;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
