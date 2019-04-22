package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ActionInfoRequest extends ProtocolRequest {


    private String token;
    private int behaviorId;
    private long activityTime;

    public void setToken(String token) {
        this.token = token;
    }

    public void setBehaviorId(int behaviorId) {
        this.behaviorId = behaviorId;
    }

    public void setActivityTime(long activityTime) {
        this.activityTime = activityTime;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
