package com.taku.safe.protocol.respond;

/**
 * Created by colin on 2017/6/6.
 */

public class RespLogin extends RespBaseBean {
    private int expire;
    private String token;
    private int bindStatus;
    private int firstLogin;

    public int getExpire() {
        return expire;
    }

    public String getToken() {
        return token;
    }

    public boolean isBind() {
        return bindStatus == 1;
    }

    public boolean isFirstLogin() {
        return firstLogin == 1;
    }

}
