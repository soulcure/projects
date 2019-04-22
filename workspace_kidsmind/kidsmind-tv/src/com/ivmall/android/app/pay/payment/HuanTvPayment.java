package com.ivmall.android.app.pay.payment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.entity.PayStatusRequest;
import com.ivmall.android.app.entity.PayStatusResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import java.lang.ref.WeakReference;

import tv.huan.sdk.pay2.jar.HuanCallback;
import tv.huan.sdk.pay2.jar.HuanPayManager;

public class HuanTvPayment {
    private static final String TAG = HuanTvPayment.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;


    private static HuanTvPayment instance = null;

    private Activity mAct;
    private int mQueryCount;


    private PayHandler mHandler;

    private HuanPayManager mHuanPayManager;

    /**
     * 小米TV支付构造函数 单例
     */
    private HuanTvPayment(Activity act) {
        mHandler = new PayHandler(this);
        mHuanPayManager = HuanPayManager.getInstance(act, mHandler);


    }

    /**
     * 小米TV支付单例
     *
     * @param act
     * @return
     */
    public static HuanTvPayment getInstance(Activity act) {
        if (instance == null) {
            instance = new HuanTvPayment(act);
        }
        instance.mAct = act;
        return instance;
    }

    /**
     * 创建kidsmind支付订单 1.32 小米TV支付预支付
     *
     * @param payRequest
     */
    public void pay(PayRequest payRequest) {

        String url = AppConfig.PAY_HUANPAY_PREPARE;

        String json = payRequest.toJsonString();

        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                PayResponse payResponse = GsonUtil.parse(response,
                        PayResponse.class);
                if (payResponse.isSuccess()) {
                    callPaySDK(payResponse);
                }

            }

        });

    }

    /**
     * 调起小米TV支付应用
     *
     * @param payResponse
     * @return
     */
    private boolean callPaySDK(final PayResponse payResponse) {
        String appSerialNo = payResponse.getOutTradeNo();  //商户订单号
        String productName = payResponse.getSubject();  //产品名称
        int productPrice = (int) (payResponse.getTotalFee() * 100);//产品价格
        String noticeUrl = payResponse.getCallback();  //后台通知url
        String productDescribe = payResponse.getBody(); //产品描述

        StringBuffer sb = new StringBuffer();
        sb.append("appSerialNo").append('=').append(appSerialNo);
        sb.append('&').append("appPayKey").append('=').append(AppConfig.HUAN_PAYKEY);
        sb.append('&').append("productName").append('=').append(productName);
        sb.append('&').append("productCount").append('=').append(1);  //产品数量,固定值 1个
        sb.append('&').append("productPrice").append('=').append(productPrice);
        sb.append('&').append("orderType").append('=').append("rmb"); //订单类型,固定值“rmb”
        sb.append('&').append("noticeUrl").append('=').append(noticeUrl);
        sb.append('&').append("productDescribe").append('=').append(productDescribe);

        String parm = sb.toString();
        try {
            mHuanPayManager.pay(parm, new HuanCallback() {
                @Override
                public void callback(int state, String log) {
                    if (state == 1) {  //状态为1 支付成功
                        Message msg = mHandler.obtainMessage();
                        msg.what = PAY_SDK_RESULT;
                        msg.obj = payResponse;
                        mHandler.sendMessage(msg);
                    } else if (state == 2) {  //状态为2 支付失败， 这时log会返回支付失败的错误信息
                        if (AppConfig.TEST_HOST) {
                            Toast.makeText(mAct, log, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        } catch (Exception e) {

        }
        return true;
    }

    private void queryPayStatus(String url, final PayStatusRequest req) {

        String json = req.toJsonString();

        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                PayStatusResponse statusResponse = GsonUtil.parse(response,
                        PayStatusResponse.class);
                if (statusResponse.isSuccess()) {
                    if (statusResponse.isTradeResult()) {
                        ((KidsMindApplication) mAct.getApplication()).reqUserInfo(mAct, statusResponse.getVipName());
                    } else {
                        if (mQueryCount > 0) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = PAY_SERVER_RESULT;
                            msg.obj = req;
                            mHandler.sendMessageDelayed(msg, 1500);
                            mQueryCount--;
                        }

                    }
                }

            }
        });
    }


    private class PayHandler extends Handler {
        private final WeakReference<HuanTvPayment> mTarget;

        PayHandler(HuanTvPayment target) {
            mTarget = new WeakReference<HuanTvPayment>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_SDK_RESULT: {
                    PayResponse result = (PayResponse) msg.obj;
                    // 执行交易状态查询
                    PayStatusRequest req = new PayStatusRequest();
                    req.setToken(((KidsMindApplication) mAct.getApplication())
                            .getToken());
                    req.setOutTradeNo(result.getOutTradeNo());
                    //req.setTotalFee(result.getTotalFee());

                    mQueryCount = 9;
                    Message message = mHandler.obtainMessage();
                    message.what = PAY_SERVER_RESULT;
                    message.obj = req;
                    mHandler.sendMessage(message);
                }
                break;
                case PAY_SERVER_RESULT: {
                    // 执行交易状态查询
                    PayStatusRequest req = (PayStatusRequest) msg.obj;
                    String url = AppConfig.PAY_HUANPAY_QUERY;
                    queryPayStatus(url, req);
                }
                break;
                default:
                    break;
            }
        }

    }

}
