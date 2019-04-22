package com.ivmall.android.app.pay.payment;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.app.sdk.AliPay;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.entity.PayStatusRequest;
import com.ivmall.android.app.entity.PayStatusResponse;
import com.ivmall.android.app.pay.alipay.Keys;
import com.ivmall.android.app.pay.alipay.Result;
import com.ivmall.android.app.pay.alipay.Rsa;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.Log;
import com.ivmall.android.app.uitls.StringUtils;

public class AliPayment {
    private static final String TAG = AliPayment.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;

    private static AliPayment instance = null;

    private Activity mAct;
    private int mQueryCount;

    private PayHandler mHandler;

    private String mOutTradeNo;

    /**
     * 支付宝支付构造函数 单例
     */
    private AliPayment() {
        mHandler = new PayHandler(this);
    }

    /**
     * 支付宝支付单例
     *
     * @param act
     * @return
     */
    public static AliPayment getInstance(Activity act) {
        if (instance == null) {
            instance = new AliPayment();
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

        String url = AppConfig.PAY_ALIPAY_PREPARE;

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
    private boolean callPaySDK(PayResponse payResponse) {
        mOutTradeNo = payResponse.getOutTradeNo();

        final String orderInfo = getOrderInfo(payResponse.getOutTradeNo(),
                payResponse.getSubject(), payResponse.getBody(),
                payResponse.getTotalFee(), payResponse.getNotifyURL());

        // 另起一个线程
        new Thread() {
            public void run() {
                // 获取Alipay对象，构造参数为当前Activity和Handler实例对象
                AliPay alipay = new AliPay(mAct, mHandler);
                // 调用pay方法，将订单信息传入
                String result = alipay.pay(orderInfo);
                // 处理返回结果
                Message msg = new Message();
                msg.what = PAY_SDK_RESULT;
                msg.obj = result;
                mHandler.sendMessage(msg);
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


    /**
     * 拼接支付宝支付参数
     *
     * @param orderNumber
     * @param orderSubject
     * @param orderBody
     * @param totalFee
     * @param notifyURL
     * @return
     */
    private String getOrderInfo(String orderNumber, String orderSubject,
                                String orderBody, double totalFee, String notifyURL) {
        StringBuilder sb = new StringBuilder();

        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        /* getOutTradeNo() */
        sb.append(orderNumber);
        sb.append("\"&subject=\"");
        sb.append(orderSubject);
        sb.append("\"&body=\"");
        sb.append(orderBody);
        sb.append("\"&total_fee=\"");
        // mPrice
        sb.append(totalFee);
        sb.append("\"&notify_url=\"");
        // 网址需要做URL编码
        String notify_url = "";
        if (notifyURL.startsWith("/")) {
            notify_url = AppConfig.MAIN_HOST + notifyURL;
        } else {
            notify_url = notifyURL;
        }
        try {
            notify_url = URLEncoder.encode(notify_url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sb.append(notify_url);

        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");

        String return_url = "";
        try {
            return_url = URLEncoder.encode("http://m.alipay.com", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sb.append(return_url);

        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);
        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");
        Log.d(TAG, "unsigned: " + sb.toString());
        // 参数签名处理
        String sign = Rsa.sign(sb.toString(), Keys.PRIVATE); // TODO
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sb.append("&sign=\"").append(sign);
        sb.append("\"&sign_type=\"RSA\""); // 签名类型，目前仅支持 RSA。
        Log.d(TAG, "signed: " + sb.toString());
        return sb.toString();
    }

    private class PayHandler extends Handler {
        private final WeakReference<AliPayment> mTarget;

        PayHandler(AliPayment target) {
            mTarget = new WeakReference<AliPayment>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PAY_SDK_RESULT:
                    Result result = new Result((String) msg.obj);
                    String status = result.getResultStatus();
                    if (!StringUtils.isEmpty(status)
                            && status.equals("9000")) {

                        // 执行交易状态查询
                        PayStatusRequest req = new PayStatusRequest();
                        req.setToken(((KidsMindApplication) mAct.getApplication())
                                .getToken());
                        String outTradeNo = result.getOutTradeNo();
                        if (StringUtils.isEmpty(outTradeNo)) {
                            outTradeNo = mOutTradeNo;
                        }
                        req.setOutTradeNo(outTradeNo);

                        mQueryCount = 9;
                        Message message = mHandler.obtainMessage();
                        message.what = PAY_SERVER_RESULT;
                        message.obj = req;
                        mHandler.sendMessage(message);
                    }

                    break;
                case PAY_SERVER_RESULT:
                    // 执行交易状态查询
                    PayStatusRequest req = (PayStatusRequest) msg.obj;
                    String url = AppConfig.PAY_ALIPAY_QUERY;
                    queryPayStatus(url, req);
                    break;
                default:
                    break;
            }
        }

    }

}
