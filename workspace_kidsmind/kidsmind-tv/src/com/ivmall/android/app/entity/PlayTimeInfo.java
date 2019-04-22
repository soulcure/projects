package com.ivmall.android.app.entity;


/**
 * Created by colin on 2016/4/5.
 */
public class PlayTimeInfo {


    private boolean isSkip;
    private boolean isEffective;
    private int playDuration;
    private int leftPlayDuration;

    public boolean isSkip() {
        return isSkip;
    }

    public boolean isEffective() {
        return isEffective;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public int getLeftPlayDuration() {
        return leftPlayDuration;
    }
}
