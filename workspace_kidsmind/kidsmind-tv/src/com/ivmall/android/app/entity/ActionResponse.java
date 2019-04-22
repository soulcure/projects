/**
 *
 */
package com.ivmall.android.app.entity;


public class ActionResponse {

    private int code; // 错误类型
    private String message; // 错误信息


    public ActionInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }



    public String getTitle() {
        if (data != null) {
            return data.getTitle();
        }
        return null;
    }

    public String getImgUrl() {
        if (data != null) {
            return data.getImgUrl();
        }
        return null;
    }

    public String getDescription() {
        if (data != null) {
            return data.getDescription();
        }
        return null;
    }

    public String getUrl() {
        if (data != null) {
            return data.getUrl();
        }
        return null;
    }

    public boolean isShowMobileInput() {
        if (data != null) {
            return data.isShowMobileInput();
        }
        return false;
    }

}
