package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class SeriesListRequest extends ProtocolRequest {


    private String token;

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
