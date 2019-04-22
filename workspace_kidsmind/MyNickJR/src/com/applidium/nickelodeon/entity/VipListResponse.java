package com.applidium.nickelodeon.entity;

/**
 * Created by john on 2015/5/29.
 */
public class VipListResponse {

    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息

    private VipListInfo data; // 详细信息


    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }


    public String getMessage() {
        return errorMessage;
    }

    public VipListInfo getData() {
        return data;
    }
}
