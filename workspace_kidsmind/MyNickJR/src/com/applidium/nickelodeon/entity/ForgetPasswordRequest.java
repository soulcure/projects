package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ForgetPasswordRequest extends ProtocolRequest {


    private String mobile;
    private String validateCode;
    private String password;


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
