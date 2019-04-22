/**
 *
 */
package com.applidium.nickelodeon.entity;


/**
 * 支付交易状态查询接口响应结果封装基础类
 */
public class UserInfoResponse {


    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息


    private UserInfo data;


    /**
     * 118表示需要支付
     *
     * @return
     */
    public boolean success() {
        boolean res = false;
        if (errorCode == 0) {
            res = true;
        }
        return res;
    }

    public String getMessage() {
        return errorMessage;
    }


    public UserInfo getData() {
        return data;
    }
}
