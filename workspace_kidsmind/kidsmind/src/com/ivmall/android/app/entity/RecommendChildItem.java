package com.ivmall.android.app.entity;

/**
 * Created by koen on 2015/11/9.
 */
public class RecommendChildItem {

    private int episodeId; //
    private String episodeName; //
    private String episodeDesc;

    private String serieName;
    private String serieDesc; //

    private boolean favorite;
    private String imgUrl;

    public int getEpisodeId() {
        return episodeId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public String getEpisodeDesc() {
        return episodeDesc;
    }

    public String getSerieName() {
        return serieName;
    }

    public String getSerieDesc() {
        return serieDesc;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
