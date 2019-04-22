/**
 *
 */
package com.applidium.nickelodeon.entity;


import java.util.ArrayList;
import java.util.List;

public class ContentListResponse {

    public int errorCode; // 错误类型
    public String errorMessage; // 错误信息

    public ContentInfo data; // 详细信息

    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }


    public String getMessage() {
        return errorMessage;
    }


    public ArrayList<ContentItem> getList() {
        return data.getList();
    }
}
