package com.ivmall.android.app.entity;


/**
 * Created by colin on 2015/5/25.
 */
public class RecommendItem {

    private int episodeId; //
    private String episodeName; //
    private int serieId;
    private String serieName;
    private boolean favorite;
    private String serieImgUrl;
    private boolean end;


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


    public boolean isFavorite() {
        return favorite;
    }

    public String getSerieImgUrl() {
        return serieImgUrl;
    }

    public void setEpisodeName(String s) {
        episodeName = s;
    }

    public void setSerieName(String s) {
        serieName = s;
    }

    public boolean isEnd() {
        return end;
    }
}
