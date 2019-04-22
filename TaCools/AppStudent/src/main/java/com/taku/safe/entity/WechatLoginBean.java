package com.taku.safe.entity;

/**
 * Created by colin on 2017/5/26.
 */

public class WechatLoginBean {

    /**
     * openid : oUTQB0-KoVJjk7vnVCJC4KPGnsI4
     * access_token : XgjxkRsk0YkcTAEXXiJut5DeD-MuCmYb6DcqhONqbkkGtWxaYYpnPkP2n180yUqOMiI2RucH6OhnnbZM3DqqkzBMLBATh9AhU3xaRb-Qutw
     * expires_in : 7200
     * refresh_token : cdGq_9CY4G1FPHfkCOdtfXt08y-kT4T3e-2lfwrRmZGMOgMhK_5EY-7d2lSIdwQk22DGmwx7bPizcrVGk0pn6_kTf9kXIQrTCtoBTc5DAww
     * scope : snsapi_base,snsapi_userinfo,
     */

    private String openid;
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String scope;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
