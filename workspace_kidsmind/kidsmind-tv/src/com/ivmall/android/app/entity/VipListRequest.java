package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class VipListRequest extends ProtocolRequest {


    private String token;


    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
