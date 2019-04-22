package com.ivmall.android.app.entity;

/**
 * Created by smit on 2016/3/4.
 */
public class AdReportRequest extends ProtocolRequest {

    public void setToken(String token) {
        this.token = token;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    private String token;
    private int episodeId;


}
