package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class PlayNoticeRequest extends ProtocolRequest {


    private String token;
    private int contentId;
    private int profileId;

    public void setToken(String token) {
        this.token = token;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
