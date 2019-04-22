package com.ivmall.android.app.entity;

import com.ivmall.android.app.fragment.KidsInfoFragment;
import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ProfileRequest extends ProtocolRequest {


    private String token;

    private String profileId;   //profileId为空时查全部


    private String nickname; //
    private String birthday; //  yyyy-MM-01

    private KidsInfoFragment.Gender gender; //

    private String headimg; //

    private SevenValue preferences; //

    private FiveValue rates; //

    public void setToken(String token) {
        this.token = token;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(KidsInfoFragment.Gender gender) {
        this.gender = gender;
    }


    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public void setPreferences(SevenValue preferences) {
        this.preferences = preferences;
    }

    public void setRates(FiveValue rates) {
        this.rates = rates;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }




}
