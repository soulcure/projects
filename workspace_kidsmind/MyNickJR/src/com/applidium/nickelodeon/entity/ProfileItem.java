package com.applidium.nickelodeon.entity;

import android.content.Context;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.fragment.KidsInfoFragment;
import com.applidium.nickelodeon.uitls.TimeUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by colin on 2015/5/25.
 */
public class ProfileItem {

    private int id; //
    private String nickname; //
    private KidsInfoFragment.Gender gender;
    private String birthday; //
    private int autoInitial; //
    private int sessionTime;
    private FiveValue rates; //


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

    public int getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(int sessionTime) {
        this.sessionTime = sessionTime;
    }

    public FiveValue getRates() {
        return rates;
    }

    public void setRates(FiveValue rates) {
        this.rates = rates;
    }

    public boolean isAutoInitial() {
        return autoInitial != 1 ? true : false;
    }

    public String getAge(Context context) {
        String res = "";
        String cur = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_MONTH);
        String[] cur_day = cur.split("-");
        String[] birth = birthday.split("-");

        if (cur_day.length > 1
                && birth.length > 1) {
            int cur_year = Integer.parseInt(cur_day[0]);
            int cur_month = Integer.parseInt(cur_day[1]);

            int birth_year = Integer.parseInt(birth[0]);
            int birth_month = Integer.parseInt(birth[1]);

            int months = (cur_year - birth_year) * 12 + cur_month - birth_month;
            int year = months / 12;
            int month = months % 12;

            if (year < 0) {
                year = 0;
            }

            if (month > 5) {
                res = (year + 1) + context.getResources().getString(R.string.year_old);
            } else {
                res = year + context.getResources().getString(R.string.year_old);
            }

        }

        return res;
    }
}
