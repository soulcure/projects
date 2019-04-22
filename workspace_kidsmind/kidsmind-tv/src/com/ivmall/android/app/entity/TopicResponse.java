/**
 * Created by colin on 2015/11/11.
 */
package com.ivmall.android.app.entity;


public class TopicResponse {

    public int code; // 错误类型
    public String message; // 错误信息

    public TopicInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public String getMessage() {
        return message;
    }

    public TopicInfo getData() {
        return data;
    }
}
