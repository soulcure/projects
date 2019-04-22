package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class LoginIvmallRequest extends ProtocolRequest {


    private String mobile;
    private String password;
    private String type;


    public void setMobile(String username) {
        this.mobile = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String username) {
        if (AppUtils.isMobileNum(username)) {
            this.type = "mobile";
        } else {
            this.type = "username";
        }
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
