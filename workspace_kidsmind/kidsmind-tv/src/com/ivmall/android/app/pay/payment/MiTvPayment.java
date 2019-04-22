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
import com.ivmall.android.app.uitls.Log;
import com.xiaomi.mitv.osspay.sdk.data.PayOrder;
import com.xiaomi.mitv.osspay.sdk.proxy.PayCallback;
import com.xiaomi.mitv.osspay.sdk.proxy.ThirdPayProxy;

import java.lang.ref.WeakReference;

public class MiTvPayment {
    private static final String TAG = MiTvPayment.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;


    private static MiTvPayment instance = null;

    private Activity mAct;
    private int mQueryCount;


    private PayHandler mHandler;
    private ThirdPayProxy thirdPayProxy;

    /**
     * 小米TV支付构造函数 单例
     */
    private MiTvPayment(Activity act) {
        mHandler = new PayHandler(this);
        thirdPayProxy = ThirdPayProxy.instance(AppConfig.AppContext);

        //设置是否是测试环境
        if (AppConfig.TEST_HOST) {
            thirdPayProxy.setUsePreview(true); //  true：测试环境
        }

    }

    /**
     * 小米TV支付单例
     *
     * @param act
     * @return
     */
    public static MiTvPayment getInstance(Activity act) {
        if (instance == null) {
            instance = new MiTvPayment(act);
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

        String url = AppConfig.PAY_MI_TV_PREPARE;

        String json = payRequest.toJsonString();

        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                PayResponse payResponse = GsonUtil.parse(response,
                        PayResponse.class);
                if (payResponse.isSuccess()) {
                    callPaySDK(payResponse);
                } else {
                    Toast.makeText(mAct, payResponse.getMessage(), Toast.LENGTH_LONG).show();
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
        long appId = Long.valueOf(AppConfig.XIAOMI_APPID);
        String custOrderId = payResponse.getOutTradeNo();
        String productName = payResponse.getSubject();
        long price = payResponse.getTotalFee().longValue();
        String orderDesc = payResponse.getBody();
        String extra = "extra";  //payResponse.getNotifyURL();


        thirdPayProxy.createOrderAndPay(appId, custOrderId,
                productName, price, orderDesc,
                extra, new PayCallback() {
                    @Override
                    public void onSuccess(PayOrder payOrder) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = PAY_SDK_RESULT;
                        msg.obj = payOrder;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (AppConfig.TEST_HOST) {
                            Toast.makeText(mAct, "小米支付错误码:" + code + "错误信息:" + message, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "code:" + code + ",message:" + message);
                        }

                    }
                });
        return true;

    }


    /**
     * 查询小米TV支付交易结果
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
        private final WeakReference<MiTvPayment> mTarget;

        PayHandler(MiTvPayment target) {
            mTarget = new WeakReference<MiTvPayment>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_SDK_RESULT: {
                    PayOrder result = (PayOrder) msg.obj;
                    // 执行交易状态查询
                    PayStatusRequest req = new PayStatusRequest();
                    req.setToken(((KidsMindApplication) mAct.getApplication())
                            .getToken());
                    req.setOutTradeNo(result.getCustomerOrderId());
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
                    String url = AppConfig.PAY_MI_TV_QUERY;
                    queryPayStatus(url, req);
                }
                break;
                default:
                    break;
            }
        }

    }

}
