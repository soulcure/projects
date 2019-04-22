package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class RegisterRequest extends ProtocolRequest {


    private String mobile; //手机号码
    private String password; //密码
    private String validateCode; //短信验证码
    private String promoCode;


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
