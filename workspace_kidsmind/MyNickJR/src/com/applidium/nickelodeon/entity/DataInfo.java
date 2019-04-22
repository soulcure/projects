package com.applidium.nickelodeon.entity;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by colin on 2015/5/25.
 */
public class DataInfo {

    private String token; // 用户token
    private String username; //用户名
    private String password; // 密码
    private String accountId; // 账号
    private String mobile; // 手机号
    //private UserInfo.VipType vipLevel; // 用户等级
    private String CDNDomain;
    private boolean CDNLockdown;
    private String IP;
    private String city;
    private String ISP;
    private String filetype;
    private boolean proxyEnable;


    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getMobile() {
        return mobile;
    }

    /*public UserInfo.VipType getVipLevel() {
        return vipLevel;
    }*/

    public String getCDNDomain() {
        return CDNDomain;
    }

    public boolean isCDNLockdown() {
        return CDNLockdown;
    }

    public String getIP() {
        return IP;
    }

    public String getCity() {
        return city;
    }

    public String getISP() {
        return ISP;
    }

    public String getFiletype() {
        return filetype;
    }

    public boolean isProxyEnable() {
        return proxyEnable;
    }
}
