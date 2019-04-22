/**
 *
 */
package com.ivmall.android.app.entity;


/**
 * 大麦TV支付 接口响应结果封装类
 */
public class DomyPayResult {

    private String message;
    private String appendAttr;
    private String code;
    private long chargingDuration;
    private String orderId;


    public boolean isSucess() {
        boolean res = false;
        if (code.equals("N000000")) {
            res = true;
        }
        return res;
    }


    public String getCode() {
        return code;
    }

    public long getChargingDuration() {
        return chargingDuration;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }


    public String getAppendAttr() {
        return appendAttr;
    }
}
