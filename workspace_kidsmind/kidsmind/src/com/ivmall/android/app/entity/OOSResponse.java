/**
 *
 */
package com.ivmall.android.app.entity;


public class OOSResponse {

    private int code; // 错误类型
    private String message; // 错误信息

    private OOSInfo data;


    public boolean isSucess() {
        return code == 200 ? true : false;
    }


    public String getMessage() {
        return message;
    }

    public OOSInfo getData() {
        return data;
    }
}
