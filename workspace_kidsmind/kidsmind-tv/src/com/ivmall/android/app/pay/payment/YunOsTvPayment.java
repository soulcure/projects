package com.ivmall.android.app.pay.payment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.aliyun.pay.client.PayClient;
import com.aliyun.pay.client.YunOSPayResult;
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

public class YunOsTvPayment {
    private static final String TAG = YunOsTvPayment.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;

    private static YunOsTvPayment instance = null;

    private Activity mAct;
    private int mQueryCount;

    private PayHandler mHandler;


    /**
     * 支付宝支付构造函数 单例
     */
    private YunOsTvPayment() {
        mHandler = new PayHandler(this);
    }

    /**
     * 支付宝支付单例
     *
     * @param act
     * @return
     */
    public static YunOsTvPayment getInstance(Activity act) {
        if (instance == null) {
            instance = new YunOsTvPayment();
        }
        instance.mAct = act;
        return instance;
    }

    /**
     * 创建kidsmind支付订单 1.32 支付宝预支付
     *
     * @param payRequest
     */
    public void pay(PayRequest payRequest) {

        String url = AppConfig.PAY_ALIYUN_TV_PREPARE;

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
     * 调起支付宝应用
     *
     * @param payResponse
     * @return
     */
    private boolean callPaySDK(final PayResponse payResponse) {

        // 另起一个线程
        new Thread() {
            public void run() {
                String order = payResponse.getYunOrder(); //由订单api生成的订单
                String sign = payResponse.getYunSign();   //由订单api生成的签名

                Bundle bundle = new Bundle();
                //传入固定的参数，用作标识支付宝支付
                bundle.putString("provider", "alipay");
                try {
                    PayClient payClient = new PayClient();
                    YunOSPayResult result = payClient.YunPay(mAct,
                            order, sign, bundle);
                    if (result.getPayResult()) {
                        // 第三方应用完成后
                        Message msg = mHandler.obtainMessage();
                        msg.what = PAY_SDK_RESULT;
                        msg.obj = payResponse;
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e) {

                }

            }
        }.start();

        return true;
    }

    /**
     * 查询支付宝交易结果
     *
     * @param url
     * @param req
     */
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
        private final WeakReference<YunOsTvPayment> mTarget;

        PayHandler(YunOsTvPayment target) {
            mTarget = new WeakReference<YunOsTvPayment>(target);
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
                    String url = AppConfig.PAY_ALIYUN_TV_QUERY;
                    queryPayStatus(url, req);
                }
                break;
                default:
                    break;
            }
        }

    }

}
