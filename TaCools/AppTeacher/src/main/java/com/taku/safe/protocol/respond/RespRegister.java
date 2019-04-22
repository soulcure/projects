package com.taku.safe.protocol.respond;

/**
 * Created by colin on 2017/6/6.
 */

public class RespRegister extends RespBaseBean {
    private int expire;
    private String token;
    private int bindStatus;

    public int getExpire() {
        return expire;
    }

    public String getToken() {
        return token;
    }

    public boolean isBindStatus() {
        return bindStatus == 1;
    }
}
