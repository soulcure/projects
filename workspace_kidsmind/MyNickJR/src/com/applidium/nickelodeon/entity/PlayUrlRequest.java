package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class PlayUrlRequest extends ProtocolRequest {

    private String token;
    private int contentId;


    public void setToken(String token) {
        this.token = token;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
