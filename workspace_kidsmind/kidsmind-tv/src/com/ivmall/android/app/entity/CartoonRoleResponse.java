/**
 *
 */
package com.ivmall.android.app.entity;



public class CartoonRoleResponse {

    private int code; // 错误类型
    private String message; // 错误信息


    public CartoonInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public CartoonInfo getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
