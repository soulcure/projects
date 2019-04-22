package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class FirstRecommRequest extends ProtocolRequest {

    private String token;
    private int serieId;


    public void setToken(String token) {
        this.token = token;
    }

    public void setSerieId(int serieId) {
        this.serieId = serieId;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
