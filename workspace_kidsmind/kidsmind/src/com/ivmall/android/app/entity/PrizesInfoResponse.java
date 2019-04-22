package com.ivmall.android.app.entity;

/**
 * Created by colin on 2015/5/25.
 */
public class PrizesInfoResponse {


    private int code; // 错误类型
    private String message;  //错误信息
    private PrizesInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public String getMessage() {
        return message;
    }

    public PrizesInfo getData() {
        return data;
    }



}
