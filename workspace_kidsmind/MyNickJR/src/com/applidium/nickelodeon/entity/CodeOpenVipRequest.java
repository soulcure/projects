package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by Markry on 2016/1/8.
 */
public class CodeOpenVipRequest extends ProtocolRequest {


    public void setToken(String token) {
        this.token = token;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String vipCode;
    private String password;

    private String token;

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }

}
