package com.taku.safe.pay;

import android.content.Context;

public class PayHttp {

    private Context mContext;

    public PayHttp(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取支付宝授权需要的orderInfo
     */
    public void getAuthInfo() {

        /*String url = AppConfig.PAY_AUTH_INFO;

        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid + "");
        contentValues.put("msisdn", phoneNum);
        contentValues.put("payWay", "1");
        contentValues.put("sid", sid);
        contentValues.put("termid", imei);
        contentValues.put("sign", AppConfig.appSign(phoneNum, imei));
        contentValues.put("v", AppConfig.V);
        contentValues.put("ps", HuxinSdkManager.instance().getCommonParam());
        HttpConnector.httpPost(url, contentValues, new IPostListener() {
            @Override
            public void httpReqResult(String response) {

            }

        });*/

    }

    /**
     * 获取支付宝账号信息
     */
    public void getAccountInfo(String authCode, String payWay) {
        /*String url = AppConfig.PAY_ACCOUNT_INFO;

        int uid = HuxinSdkManager.instance().getUserId();
        String sid = HuxinSdkManager.instance().getSession();
        String phoneNum = HuxinSdkManager.instance().getPhoneNum();
        String imei = DeviceUtils.getIMEI(mContext.getApplicationContext());

        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid + "");
        contentValues.put("msisdn", phoneNum);
        contentValues.put("payWay", payWay);//支付方式  0：余额；1：支付宝；2：微信；3：银行卡; 4:呼币
        contentValues.put("authCode", authCode);//授权码
        contentValues.put("sid", sid);
        contentValues.put("termid", imei);
        contentValues.put("sign", AppConfig.appSign(phoneNum, imei));
        contentValues.put("v", AppConfig.V);
        contentValues.put("ps", HuxinSdkManager.instance().getCommonParam());
        HttpConnector.httpPost(url, contentValues, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (callBackPay != null) {
                    callBackPay.success(response);
                }
            }
        });*/
    }


    /**
     * 用户下订单接口
     */
    public void reqOrder(Context context, String phone, int payWay, int goodsId, String price, String amount) {

        /*String url = AppConfig.WECHAT_ORDER;

        int uid = HuxinSdkManager.instance().getUserId();
        String sid = HuxinSdkManager.instance().getSession();
        String phoneNum = HuxinSdkManager.instance().getPhoneNum();
        String imei = DeviceUtils.getIMEI(context.getApplicationContext());

        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid + "");
        contentValues.put("msisdn", phoneNum);
        contentValues.put("payWay", payWay);
        contentValues.put("sid", sid);
        contentValues.put("termid", imei);
        contentValues.put("sign", AppConfig.appSign(phoneNum, imei));
        contentValues.put("phone", phone);
        contentValues.put("goodsId", goodsId);//购买的商品id
        contentValues.put("price", price);//商品单价
        contentValues.put("amount", amount);//购买数量
        HttpConnector.httpPost(url, contentValues, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespWeChatOrder data = GsonUtil.parse(response, RespWeChatOrder.class);
            }
        });*/

    }


}
