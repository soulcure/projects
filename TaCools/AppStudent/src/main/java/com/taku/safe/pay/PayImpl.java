package com.taku.safe.pay;


public interface PayImpl {

    /**
     * 是否已经安装客户端
     */
    boolean isInstall();

    /**
     * 授权操作
     */
    void authorized();

    /**
     * 支付操作
     */
    void pay(String phone, int goodsId, String price, String amount);

}
