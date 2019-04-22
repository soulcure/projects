package com.ivmall.android.app.pay.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.DomyInfo;
import com.ivmall.android.app.entity.DomyPayResult;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.entity.PayStatusRequest;
import com.ivmall.android.app.entity.PayStatusResponse;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.StringUtils;

import java.lang.ref.WeakReference;

public class DomyPayActivity extends Activity {

    private static final String TAG = DomyPayActivity.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;

    /* 第三方应用 支付 activity request code */
    private static final int REQUEST_CODE_DOMY_PAY = 5;

    private PayHandler mHandler;
    private Context mContext;
    private int mQueryCount;

    /*大麦支付 产品序列号*/
    private String partnerProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_domy_pay);

        mContext = this;
        mHandler = new PayHandler(this);

        String token = ((KidsMindApplication) getApplicationContext()).getToken();

        double price = getIntent().getExtras().getDouble(
                PaymentDialog.PRCIE);
        String vipGuid = getIntent().getExtras().getString(
                PaymentDialog.VIPGUID);

        partnerProductId = getIntent().getExtras().getString(
                PaymentDialog.DOMY_PAY);

        PayRequest payRequest = new PayRequest();

        payRequest.setToken(token);
        payRequest.setPrice(price);
        payRequest.setVipGuid(vipGuid);

        pay(payRequest);

    }


    @Override
    public void onResume() {
        super.onResume();

        BaiduUtils.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        BaiduUtils.onPause(this);
    }


    /**
     * 创建kidsmind支付订单 1.32 大麦TV支付预支付
     *
     * @param payRequest
     */
    private void pay(PayRequest payRequest) {

        String url = AppConfig.PAY_DOMY_TV_PREPARE;

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DOMY_PAY) {
            String result = data.getStringExtra("payPackageResult");

            if (!StringUtils.isEmpty(result)) {
                DomyPayResult payResult = GsonUtil.parse(result, DomyPayResult.class);
                // 未下单,支付失败
                if (payResult.isSucess()) {
                    DomyInfo info = GsonUtil.parse(payResult.getAppendAttr(), DomyInfo.class);
                    // 第三方应用完成后，查询支付状态
                    Message msg = mHandler.obtainMessage();
                    msg.what = PAY_SDK_RESULT;
                    msg.obj = info;
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(mContext, payResult.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


    private boolean callPaySDK(final PayResponse payResponse) {
        // 调用大麦支付apk
        Intent intent = new Intent();
        intent.setAction("com.hiveview.pay.package");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("productSerial", partnerProductId);// 产品序列号
        intent.putExtra("appendAttr", payResponse.getAppendAttr());// 业务参数 json格式
        intent.putExtra("token", payResponse.getSignToken()); // 参数签名
        startActivityForResult(intent, REQUEST_CODE_DOMY_PAY);


        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(PAY_SERVER_RESULT);
        mHandler = null;
    }


    /**
     * 查询大麦TV支付交易结果
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
                        ((KidsMindApplication) getApplication()).reqUserInfo(DomyPayActivity.this, statusResponse.getVipName());
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
        private final WeakReference<DomyPayActivity> mTarget;

        PayHandler(DomyPayActivity target) {
            mTarget = new WeakReference<DomyPayActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_SDK_RESULT: {
                    DomyInfo result = (DomyInfo) msg.obj;
                    // 执行交易状态查询
                    PayStatusRequest req = new PayStatusRequest();
                    req.setToken(((KidsMindApplication) getApplication())
                            .getToken());
                    req.setOutTradeNo(result.getOutTradeNo());
                    req.setPartnerOrderId(partnerProductId);

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
                    String url = AppConfig.PAY_DOMY_TV_QUERY;
                    queryPayStatus(url, req);
                }
                break;
                default:
                    break;
            }
        }

    }
}
