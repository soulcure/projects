/**
 *
 */
package com.applidium.nickelodeon.entity;


import java.util.List;

/**
 * 支付 接口响应结果封装基础类
 *
 * @author Roseox Hu
 */
public class ProfileResponse {

    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息

    private ProfileInfo data; // 详细信息


    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }

    public boolean isFail() {
        return errorCode == 109 ? true : false;
    }


    public String getMessage() {
        return errorMessage;
    }

    public ProfileInfo getData() {
        return data;
    }
}
