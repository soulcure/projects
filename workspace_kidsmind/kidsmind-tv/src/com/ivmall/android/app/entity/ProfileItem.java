package com.ivmall.android.app.entity;

import com.ivmall.android.app.fragment.KidsInfoFragment;

/**
 * Created by colin on 2015/5/25.
 */
public class ProfileItem {


    private int id; //
    private String nickname; //
    private KidsInfoFragment.Gender gender; //
    private String birthday; //
    private String imgUrl; //
    private SevenValue preferences; //
    private FiveValue rates; //

    private int sessionTime;
    private SessionStatus sessionStatus; //
    private int autoInitial; //


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public KidsInfoFragment.Gender getGender() {
        return gender;
    }

    public void setGender(KidsInfoFragment.Gender gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public SevenValue getPreferences() {
        return preferences;
    }

    public void setPreferences(SevenValue preferences) {
        this.preferences = preferences;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(int sessionTime) {
        this.sessionTime = sessionTime;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public boolean isAutoInitial() {
        return autoInitial != 1 ? true : false;
    }


    public FiveValue getRates() {
        return rates;
    }

    public void setRates(FiveValue rates) {
        this.rates = rates;
    }

    public enum SessionStatus {
        UNKNOWN, OPEN, PAUSED, NONE
    }

}
