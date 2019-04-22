/**
 *
 */
package com.ivmall.android.app.entity;


public class BehaviorResponse {

    public int code; // 错误类型
    public String message; // 错误信息

    public BehaviorInfo data; // 详细信息

    public boolean isSucess() {
        return code == 200 ? true : false;
    }


    public String getMessage() {
        return message;
    }

    public BehaviorInfo getData() {
        return data;
    }
}
