package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class SerieInfoRequest extends ProtocolRequest {


    private String token;
    private int serieId; // 剧集ID
    private int topicId; // 专题ID
    private int startIndex;
    private int offset;   //默认 10


    public void setToken(String token) {
        this.token = token;
    }

    public void setSerieId(int serieId) {
        this.serieId = serieId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
