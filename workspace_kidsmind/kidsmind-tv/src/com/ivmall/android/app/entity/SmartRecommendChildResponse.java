package com.ivmall.android.app.entity;

/**
 * Created by smit on 2015/11/9.
 */
public class SmartRecommendChildResponse {

    public int code; // 错误类型
    public String message; // 错误信息

    public RecommendChildInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public RecommendChildInfo getData() {
        return data;
    }
}
