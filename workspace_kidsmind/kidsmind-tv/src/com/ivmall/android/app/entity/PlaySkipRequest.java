package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class PlaySkipRequest extends ProtocolRequest {


    private String token;
    private boolean isSkip;

    private boolean isEffective;
    private int playDuration;

    private boolean close;
    private boolean confirm;


    private int startIndex;
    private int offset;

    public void setToken(String token) {
        this.token = token;
    }

    public void setSkip(boolean skip) {
        isSkip = skip;
    }

    public void setEffective(boolean effective) {
        isEffective = effective;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }


    public void setClose(boolean close) {
        this.close = close;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }


    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
