/**
 *
 */
package com.ivmall.android.app.entity;


/**
 * 支付 接口响应结果封装基础类
 *
 * @author Roseox Hu
 */
public class PayResponse {

    private int code; // 错误类型
    private String message; // 错误信息

    private GoodsInfo data; // 商品信息


    public boolean isSuccess() {
        boolean res = false;
        if (code == 200) {
            res = true;
        }
        return res;
    }


    public String getMessage() {
        return message;
    }


    public String getSubject() {
        return data.getSubject();
    }

    public String getBody() {
        return data.getBody();
    }

    public String getOutTradeNo() {
        return data.getOutTradeNo();
    }

    public Double getTotalFee() {
        return data.getTotalFee();
    }

    public String getNotifyURL() {
        return data.getNotifyURL();
    }

    public boolean isTradeResult() {
        return data.isTradeResult();
    }

    public double getPoints() {
        return data.getPoints();
    }

    public String getQrcodeURL() {
        return data.getQrcodeURL();
    }

    public String getQrcodeImgURL() {
        return data.getQrcodeImgURL();
    }

    public String getCodeUrl() {
        return data.getCodeUrl();
    }

    public String getPrepayid() {
        return data.getPrepayid();
    }

    public String getNonceStr() {
        return data.getNonceStr();
    }

    public Integer getTimestamp() {
        return data.getTimestamp();
    }

    public String getPackageValue() {
        return data.getPackageValue();
    }

    public String getSign() {
        return data.getSign();
    }

    public String getAccessToken() {
        return data.getAccessToken();
    }


    public String getQrcode() {
        return data.getQrcode();
    }

    public String getQrcodeImgUrl() {
        return data.getQrcodeImgUrl();
    }

    public double getAmount() {
        return data.getAmount();
    }

    public String getVipName() {
        return data.getVipName();
    }


    public String getYunSign() {
        return data.getYunSign();
    }

    public String getYunOrder() {
        return data.getYunOrder();
    }

    /**
     * 大麦支付回调
     */
    public String getCallback() {
        return data.getCallback();
    }

    /**
     * 大麦支付参数
     */
    public String getSignToken() {
        return data.getSignToken();
    }

    /**
     * 大麦支付参数
     */
    public String getAppendAttr() {
        return data.getAppendAttr();
    }


    public String getParams() {
        return data.getParams();
    }

    public String getUnicomGoodsId() {
        return data.getUnicomGoodsId();
    }
}
