/**
 *
 */
package com.ivmall.android.app.entity;


/**
 *
 */
public class NodeInfoResponse {

    private int code; // 错误类型
    private String message; // 错误信息


    public NodeInfo data; // 详细信息


    public boolean isSucess() {
        return code == 200 ? true : false;
    }

    public NodeInfo getData() {
        return data;
    }
}
