package com.applidium.nickelodeon.entity;


public class LoginInfo {

    /*注册和登录共同返回字段*/
    private String token;
    private String accountId;
    private String vipLevel;
    private String CDNDomain;
    private String CDNLockdown;
    private String IP;
    private String city;
    private String ISP;
    private String fileType;
    private String proxyEnable;

    /*登录多出的返回字段*/
    private String vipExpiryTime;
    private String vipExpiryTip;
    private String username;
    private String password;
    private String currentTime;
    private String firstModifiedTime;

    /*匿名登录多出返回字段*/
    private String mobile;




    public String getToken() {
        return token;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public String getCDNDomain() {
        return CDNDomain;
    }

    public String getCDNLockdown() {
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

    public String getFileType() {
        return fileType;
    }

    public String getProxyEnable() {
        return proxyEnable;
    }

    public String getVipExpiryTime() {
        return vipExpiryTime;
    }

    public String getVipExpiryTip() {
        return vipExpiryTip;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getFirstModifiedTime() {
        return firstModifiedTime;
    }

    public String getMobile() {
        return mobile;
    }
}
