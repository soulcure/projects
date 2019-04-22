package com.ivmall.android.app.entity;


/**
 * Created by john on 2015/5/28.
 */
public class RecordItem {

    private int episodeId;  //子剧集ID
    private String episodeName;   //子剧集名称
    private int serieId; //所属剧集ID
    private String serieName; //所属剧集名称
    private String imgUrl;  //剧集图片下载地址
    private String playTime;  //剧集播放的时间
    private int sequence;
    private String[] preferences;  //剧集七项能力
    private String[] rates;  //新五项能力
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

    public String getImgUrl() {
        return imgUrl;
    }

    public String getPlayTime() {
        return playTime;
    }

    public String getSequence() {
        String result;
        if (sequence < 10) {
            result = "0" + sequence;
        } else {
            result = "" + sequence;
        }
        return result;
    }


    public String[] getPreferences() {
        return preferences;
    }

    public String[] getRates() {
        return rates;
    }

    public boolean isTrial() {
        return trial;
    }
}
