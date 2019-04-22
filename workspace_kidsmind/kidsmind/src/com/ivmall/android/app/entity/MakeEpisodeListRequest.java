package com.ivmall.android.app.entity;

/**
 * Created by smit on 2016/3/4.
 */
public class MakeEpisodeListRequest extends ProtocolRequest {

    private String token;
    private String episodeList;


    public void setToken(String token) {
        this.token = token;
    }

    public void setEpisodeList(String episodeList) {
        this.episodeList = episodeList;
    }
}
