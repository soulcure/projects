/**
 *
 */
package com.ivmall.android.app.entity;


/**
 * 支付交易状态查询接口响应结果封装基础类
 */
public class PlayTimeResponse {


    private int code; // 错误类型
    private String message; // 错误信息

    private PlayTimeInfo data;


    public boolean isSuccess() {
        boolean res = false;
        if (code == 200) {
            res = true;
        }
        return res;
    }

    public boolean isTimeOut() {
        boolean res = false;
        if (code == 158) {
            res = true;
        }
        return res;
    }

    public PlayTimeInfo getData() {
        return data;
    }


}
