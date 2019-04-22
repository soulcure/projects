package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by Markry on 2016/1/8.
 */
public class ChangeUserNameRequest extends ProtocolRequest {

    public void setUsername(String username) {
        this.username = username;
    }

    String username;

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }

}
