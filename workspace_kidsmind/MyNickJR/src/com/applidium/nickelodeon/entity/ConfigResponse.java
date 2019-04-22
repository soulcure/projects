/**
 *
 */
package com.applidium.nickelodeon.entity;


import java.util.List;

public class ConfigResponse {

    public int errorCode; // 错误类型
    public String errorMessage; // 错误信息

    public ConfigInfo data; // 详细信息

    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }
    

    public String getMessage() {
        return errorMessage;
    }


    public List<ConfigItem> getList() {
        return data.getList();
    }
}
