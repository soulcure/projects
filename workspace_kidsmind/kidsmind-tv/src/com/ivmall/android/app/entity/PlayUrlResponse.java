/**
 *
 */
package com.ivmall.android.app.entity;


/**
 * 支付 接口响应结果封装基础类
 *
 * @author Roseox Hu
 */
public class PlayUrlResponse {

    private int code; // 错误类型
    private String message; // 错误信息

    private PlayUrlInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public boolean isCollect() {
        return code == 120 ? true : false;
    }


    public String getMessage() {
        return message;
    }

    public PlayUrlInfo getData() {
        return data;
    }
}
