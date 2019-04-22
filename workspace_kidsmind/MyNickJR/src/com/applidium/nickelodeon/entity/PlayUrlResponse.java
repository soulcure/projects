/**
 *
 */
package com.applidium.nickelodeon.entity;


/**
 * 支付 接口响应结果封装基础类
 *
 * @author Roseox Hu
 */
public class PlayUrlResponse {

    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息

    private PlayUrlInfo data; // 详细信息


    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }

    public boolean isCollect() {
        return errorCode == 120 ? true : false;
    }


    public String getMessage() {
        return errorMessage;
    }

    public PlayUrlInfo getData() {
        return data;
    }
}
