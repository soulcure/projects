
package com.ivmall.android.app.entity;

import java.util.List;

/**
 *
 */
public class FirstRecommResponse {

    private int code; // 错误类型
    private String message; // 错误信息

    private List<FirstRecommItem> data; // 详细信息

    public boolean isSucess() {
        return code == 200 ? true : false;
    }


    public String getMessage() {
        return message;
    }

    public List<FirstRecommItem> getData() {
        return data;
    }
}
