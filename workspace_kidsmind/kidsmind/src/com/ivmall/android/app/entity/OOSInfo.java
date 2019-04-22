package com.ivmall.android.app.entity;


/**
 * Created by colin on 2016/4/5.
 */
public class OOSInfo {

    private String expiration;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String endPoint;
    private String policyBase64;
    private String signature;
    private String bucket;
    private String subObject;


    public String getExpiration() {
        return expiration;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getPolicyBase64() {
        return policyBase64;
    }

    public String getSignature() {
        return signature;
    }

    public String getBucket() {
        return bucket;
    }

    public String getSubObject() {
        return subObject;
    }
}
