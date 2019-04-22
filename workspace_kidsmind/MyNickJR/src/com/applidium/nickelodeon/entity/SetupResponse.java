
package com.applidium.nickelodeon.entity;

import java.util.Locale;

/**
 *
 */
public class SetupResponse {

    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息

    private SetupInfo data; // 详细信息


    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }

    /*public boolean isModelA() {
        boolean res = false;
        String model = data.getUiModel().toUpperCase(Locale.US);
        if (model.equals("A")) {
            res = true;
        }
        return res;
    }

    public boolean isModelB() {
        boolean res = false;
        String model = data.getUiModel().toUpperCase(Locale.US);
        if (model.equals("B")) {
            res = true;
        }
        return res;
    }*/

    public String getMessage() {
        return errorMessage;
    }

    public SetupInfo getData() {
        return data;
    }
}
