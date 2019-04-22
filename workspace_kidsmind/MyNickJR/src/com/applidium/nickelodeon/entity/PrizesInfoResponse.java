package com.applidium.nickelodeon.entity;

/**
 * Created by colin on 2015/5/25.
 */
public class PrizesInfoResponse {


    public int errorCode; // 错误类型
    public String errorMessage; // 错误信息



    public boolean isSucess() {
        return errorCode == 200 ? true : false;
    }

    public String getMessage() {
        return errorMessage;
    }


}
