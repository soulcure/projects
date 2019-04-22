package com.applidium.nickelodeon.entity;

/**
 * Created by Markry on 2015/11/9.
 */
public class InviteResponse {
    private int code; // 错误类型

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message; // 错误信息

    public boolean isSucess() {
        return code == 200 ? true : false;
    }
}
