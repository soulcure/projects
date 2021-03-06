/**
 *
 */
package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;


/**
 * API 接口请求参数封装基础类
 */
public class PayStatusRequest extends ProtocolRequest {

    // 必传参数
    private String token; // 用户标识

    private String outTradeNo; // 系统交易号


    private String partnerOrderId; //for domy支付添加

//    * @param out_trade_no 商户网站已经付款完成的商户网站订单号
//    46 	 * @param trade_no 已经付款完成的支付宝交易号，与商户网站订单号out_trade_no相对应


    public void setToken(String token) {
        this.token = token;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public void setPartnerOrderId(String id) {
        this.partnerOrderId = id;
    }

    public String toJsonString() {
        return GsonUtil.format(this);
    }
}
