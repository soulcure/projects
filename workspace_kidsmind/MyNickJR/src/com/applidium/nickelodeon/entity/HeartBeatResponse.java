/**
 *
 */
package com.applidium.nickelodeon.entity;


/**
 * 支付交易状态查询接口响应结果封装基础类
 */
public class HeartBeatResponse {


    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息


    private UserInfo data;


    /**
     * 118表示需要支付
     *
     * @return
     */
    public boolean isNeedPayVip() {
        boolean res = false;
        if (errorCode == 402) {
            res = true;
        }
        return res;
    }

    /**
     * 200表示需要支付
     *
     * @return
     */
    public boolean success() {
        boolean res = false;
        if (errorCode == 200) {
            res = true;
        }
        return res;
    }


    public String getMessage() {
        return errorMessage;
    }



}