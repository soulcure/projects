package com.ivmall.android.app.entity;


/**
 * Created by colin on 2015/5/25.
 */
public class FavoriteItem {

    private int episodeId;
    private String episodeName;
    private int serieId;

    private String serieName;
    private boolean langSwitch;
    private String imgUrl;
    private boolean trial;


    public int getEpisodeId() {
        return episodeId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public int getSerieId() {
        return serieId;
    }

    public String getSerieName() {
        return serieName;
    }

    public boolean isLangSwitch() {
        return langSwitch;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public boolean isTrial() {
        return trial;
    }
}
