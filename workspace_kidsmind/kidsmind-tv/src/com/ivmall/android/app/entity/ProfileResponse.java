/**
 *
 */
package com.ivmall.android.app.entity;


import java.util.List;

/**
 * 支付 接口响应结果封装基础类
 *
 * @author Roseox Hu
 */
public class ProfileResponse {

    private int code; // 错误类型
    private String message; // 错误信息

    private ProfileInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public boolean isFail() {
        return code == 109 ? true : false;
    }


    public String getMessage() {
        return message;
    }

    public ProfileInfo getData() {
        return data;
    }
}
