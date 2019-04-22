package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ConfigRequest extends ProtocolRequest {


    private String uniqueKey;


    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
