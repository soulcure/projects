package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ContentListRequest extends ProtocolRequest {


    private String token;
    private int subSeriesId;

    public void setToken(String token) {
        this.token = token;
    }

    public void setSubSeriesId(int subSeriesId) {
        this.subSeriesId = subSeriesId;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
