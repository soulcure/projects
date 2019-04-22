package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class SmsCodeRequest extends ProtocolRequest {


    private String mobile;


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }

    public enum SmsParm {
        login, bind
    }

}
