/**
 *
 */
package com.applidium.nickelodeon.entity;

/**
 * 支付交易状态查询接口响应结果封装基础类
 */
public class PayStatusResponse {


    private int errorCode; // 错误类型
    private String errorMessage; // 错误信息


    private OrderInfo data;  //订单信息


    public boolean isSuccess() {
        boolean res = false;
        if (errorCode == 0) {
            res = true;
        }
        return res;
    }


    public boolean isTradeResult() {
        return data.isTradeResult();
    }
    
    public String getVipName() {
        return data.getVipName();
    }

}
