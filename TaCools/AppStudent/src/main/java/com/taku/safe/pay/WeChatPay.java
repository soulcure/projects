package com.taku.safe.pay;

import android.content.Context;

import com.taku.safe.config.Constant;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatPay implements PayImpl {

    private Context mContext;

    private IWXAPI api;

    public WeChatPay(Context mContext) {
        this.mContext = mContext;
        api = WXAPIFactory.createWXAPI(mContext, Constant.WECHAT_APPID, true);
        api.registerApp(Constant.WECHAT_APPID);
    }

    /**
     * 打开微信
     */
    public void openWeChat() {
        api.openWXApp();
    }

    @Override
    public boolean isInstall() {
        return api.isWXAppInstalled();
    }

    @Override
    public void authorized() {
        SendAuth.Req req = new SendAuth.Req();
        //授权读取用户信息
        req.scope = "snsapi_userinfo";
        //自定义信息
        req.state = "wechat_sdk_demo_test";
        //向微信发送请求
        api.sendReq(req);
    }

    @Override
    public void pay(final String phone, int goodsId, String price, String amount) {
        new PayHttp(mContext).reqOrder(mContext, phone, 2, goodsId, price, amount);
    }


    private void pay() {
        PayReq req = new PayReq();
        req.appId = Constant.WECHAT_APPID;
        req.partnerId = Constant.WECHAT_MCH_ID;
        //req.prepayId = dBean.getPrepay_id();
        //req.packageValue = "Sign=WXPay";
        //req.nonceStr = dBean.getNonce_str();
        //req.timeStamp = dBean.getTimestamp();
        //req.sign = dBean.getSign();
        api.sendReq(req);
    }
}
