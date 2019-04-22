/**
 * Created by colin on 2016/3/23.
 */
package com.ivmall.android.app.entity;


public class CategoryResponse {

    public int code; // 错误类型
    public String message; // 错误信息

    public CategoryInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public String getMessage() {
        return message;
    }

    public CategoryInfo getData() {
        return data;
    }
}
