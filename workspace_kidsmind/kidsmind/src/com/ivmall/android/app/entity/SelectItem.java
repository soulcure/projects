package com.ivmall.android.app.entity;

/**
 * Created by koen on 2016/4/14.
 * 用于记录已选项，用于发送电视
 */
public class SelectItem {

    private int episodeId;
    private String name;
    private int serieId;

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeName() {
        return name;
    }

    public void setEpisodeName(String episodeName) {
        this.name = episodeName;
    }

    public int getSerieId() {
        return serieId;
    }

    public void setSerieId(int serieId) {
        this.serieId = serieId;
    }
}
