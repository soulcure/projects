/**
 *
 */
package com.ivmall.android.app.entity;


public class JoinActionResponse {

    private int code; // 错误类型
    private String message; // 错误信息


    public Object data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }


    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
