package com.ivmall.android.app.entity;

/**
 * Created by smit on 2016/3/4.
 */
public class OOSRequest extends ProtocolRequest {

    private String token;
    private String aliyunApi;
    private String type;


    public void setToken(String token) {
        this.token = token;
    }

    public void setAliyunApi(String aliyunApi) {
        this.aliyunApi = aliyunApi;
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum ImageType {
        bbs_img, head_img
    }

    public enum ApiType {
        putObject, postObject
    }


}
