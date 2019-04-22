package com.taku.safe.entity;

/**
 * Created by soulcure on 2017/8/12.
 */

public class VoiceItem {
    private boolean isAutoPlay;
    private String url;
    private int duration;

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
