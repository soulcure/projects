package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class PlayUrlRequest extends ProtocolRequest {

    private String token;
    private int profileId;
    private int episodeId;
    private int behaviorId;//用户行为ID

    public void setToken(String token) {
        this.token = token;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public void setBehaviorId(int behaviorId) {
        this.behaviorId = behaviorId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
