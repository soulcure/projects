/**
 *
 */
package com.ivmall.android.app.entity;


public class TvReportResponse {


    private int code; // 错误类型
    private String message; // 错误信息
    private TvReportInfo data;

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

    public TvReportInfo getData() {
        return data;
    }
}
