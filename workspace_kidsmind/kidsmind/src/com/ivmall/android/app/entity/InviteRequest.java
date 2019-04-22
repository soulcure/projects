package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by Markry on 2015/11/9.
 */
public class InviteRequest extends ProtocolRequest {
    private String token;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }
}
