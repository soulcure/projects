package com.ivmall.android.app.entity;

/**
 * Created by smit on 2016/4/27.
 */
public class UserUpdateRequest extends ProtocolRequest{

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String token;
    private String name;
}
