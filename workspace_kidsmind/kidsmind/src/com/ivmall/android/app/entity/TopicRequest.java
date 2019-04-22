package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class TopicRequest extends ProtocolRequest {


    private String token;
    private int rowCount = 1; //返回记录数，默认1


    public void setToken(String token) {
        this.token = token;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
