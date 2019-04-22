package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ActionRequest extends ProtocolRequest {


    private String token;
    private String location;


    public void setToken(String token) {
        this.token = token;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
