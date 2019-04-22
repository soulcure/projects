package com.ivmall.android.app.entity;

/**
 * Created by koen on 2015/11/12.
 */
public class BaiduPushInfoRequest extends ProtocolRequest {
    private String channelId;
    private int deviceType = 3;  //Android=3、ios=4


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
