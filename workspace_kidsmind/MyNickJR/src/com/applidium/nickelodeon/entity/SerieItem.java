package com.applidium.nickelodeon.entity;

/**
 * Created by colin on 2015/5/25.
 */
public class SerieItem {

    private int episodeId;
    private String title;
    private String description;
    private int sequence;
    private String imgUrl;
    private boolean langSwitch;
    private boolean favorite;
    private boolean trial;


    public int getEpisodeId() {
        return episodeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public boolean isLangSwitch() {
        return langSwitch;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isTrial() {
        return trial;
    }
}
