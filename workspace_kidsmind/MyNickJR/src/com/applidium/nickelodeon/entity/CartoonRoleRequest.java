package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class CartoonRoleRequest extends ProtocolRequest {


    private String token;
    private int startIndex; //
    private int offset;   //


    public void setToken(String token) {
        this.token = token;
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
