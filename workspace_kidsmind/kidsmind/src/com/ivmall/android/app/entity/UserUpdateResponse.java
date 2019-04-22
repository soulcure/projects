package com.ivmall.android.app.entity;

/**
 * Created by koen on 2016/4/27.
 */
public class UserUpdateResponse {

    private int code;
    private String message;

    public boolean isSuccess() {
        boolean res = false;
        if (code == 200) {
            res = true;
        }
        return res;
    }

    public String getMessage() {
        return message;
    }

}
