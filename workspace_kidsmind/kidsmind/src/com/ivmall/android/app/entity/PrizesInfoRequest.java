package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class PrizesInfoRequest extends ProtocolRequest {


    private String token;
    private String activityTitle;


    public void setToken(String token) {
        this.token = token;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
