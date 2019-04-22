package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ChangePasswordRequest extends ProtocolRequest {


    private String oldPassword;
    private String newPassword;
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
