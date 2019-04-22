package com.ivmall.android.app.pay.payment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.entity.PayStatusRequest;
import com.ivmall.android.app.entity.PayStatusResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.Log;
import com.sdk.commplatform.Commplatform;
import com.sdk.commplatform.entry.AppInfo;
import com.sdk.commplatform.entry.AuthResult;
import com.sdk.commplatform.entry.CyclePayment;
import com.sdk.commplatform.entry.ErrorCode;
import com.sdk.commplatform.entry.PayResult;
import com.sdk.commplatform.entry.ProductInfos;
import com.sdk.commplatform.listener.CallbackListener;
import com.smit.android.ivmall.stb.R;

import java.lang.ref.WeakReference;

public class UnicomPayActivity extends Activity {

    private static final String TAG = UnicomPayActivity.class.getSimpleName();
    private static final String productType = "2";

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;
    private static final int PAY_SDK_DESTORY = 3;

    private static final int PAY_INIT_RESULT = 4;
    private static final int PAY_AUTH_RESULT = 5;

    private Activity mAct;
    private int mQueryCount;
    private String mProductId;
    private PayRequest payRequest = new PayRequest();

    private PayHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAct = this;
        mHandler = new PayHandler(this);

        String token = ((KidsMindApplication) getApplicationContext()).getToken();

        double price = getIntent().getExtras().getDouble(
                PaymentDialog.PRCIE);
        String vipGuid = getIntent().getExtras().getString(
                PaymentDialog.VIPGUID);

        payRequest.setToken(token);
        payRequest.setPrice(price);
        payRequest.setVipGuid(vipGuid);

        init(this);
    }


    public void init(final Activity activity) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(AppConfig.UNICOM_APPID);// 应用ID
        appInfo.setAppKey(AppConfig.UNICOM_APPKEY);// 应用Key
        appInfo.setCtx(activity);
        appInfo.setVersionCheckStatus(AppInfo.VERSION_CHECK_LEVEL_STRICT);
        Commplatform.getInstance().Init(0, appInfo,
                new CallbackListener<Integer>() {
                    @Override
                    public void callback(int paramInt, Integer paramT) {
                        Message msg = mHandler.obtainMessage(PAY_INIT_RESULT);
                        msg.arg1 = paramInt;
                        mHandler.sendMessage(msg);
                    }
                });
    }


    /**
     * 联通用户鉴权
     *
     * @param productId
     * @param productType
     * @param contentId
     */
    private void authNew(String productId, String productType, String contentId) {
        Commplatform.getInstance().authPermission(productId,
                productType,
                contentId,
                new CallbackListener<AuthResult>() {
                    @Override
                    public void callback(int code, AuthResult result) {
                        Message msg = mHandler.obtainMessage(PAY_AUTH_RESULT);
                        msg.arg1 = code;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    /**
     * 创建kidsmind支付订单 1.32 小米TV支付预支付
     *
     * @param payRequest
     */
    public void pay(PayRequest payRequest) {

        String url = AppConfig.PAY_UNICOM_TV_PREPARE;

        String json = payRequest.toJsonString();

        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                PayResponse payResponse = GsonUtil.parse(response,
                        PayResponse.class);
                if (payResponse != null && payResponse.isSuccess()) {
                    callPaySDK(payResponse);
                } else {
                    Toast.makeText(mAct, payResponse.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }

        });

    }

    private boolean callPaySDK(final PayResponse payResponse) {
        CyclePayment cyclePayment = new CyclePayment();
        cyclePayment.setTradeNo(payResponse.getOutTradeNo());//订单号
        cyclePayment.setProductId(mProductId);  //联通SDK5.0 ,mProductId由鉴权产生
        //cyclePayment.setProductId(payResponse.getUnicomGoodsId());//商品ID
        //cyclePayment.setNote(note);  //透传字段

        cyclePayment.setThirdAppId(AppConfig.UNICOM_APPID);
        cyclePayment.setThirdAppName(mAct.getResources()
                .getString(R.string.app_name));
        cyclePayment.setThirdAppPkgname(payResponse.getPackageValue());
        cyclePayment.setNotifyURL(payResponse.getNotifyURL());


        int ret = Commplatform.getInstance().subsPay(cyclePayment, mAct,
                new CallbackListener<PayResult>() {
                    @Override
                    public void callback(final int arg0, final PayResult arg1) {
                        mAct.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (arg0 == ErrorCode.COM_PLATFORM_SUCCESS) {
                                    Toast.makeText(mAct, "pay_success",
                                            Toast.LENGTH_SHORT).show();

                                    Message msg = mHandler.obtainMessage();
                                    msg.what = PAY_SDK_RESULT;
                                    msg.obj = arg1;
                                    mHandler.sendMessage(msg);

                                } else if (arg0 == ErrorCode.COM_PLATFORM_ERROR_PAY_FAILURE) {
                                    Toast.makeText(mAct, "purchase_canceled",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mAct, "Purchase failed. Error code:" + arg0,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        if (ret == 0) {
            return true;
        } else {
            // 返回错误，即支付过程结束
            return false;
        }
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
                        ((KidsMindApplication) mAct.getApplication())
                                .reqUserInfo(mAct, statusResponse.getVipName());

                        mHandler.sendEmptyMessageDelayed(PAY_SDK_DESTORY, 1500); // 销毁支付SDK
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

    private static class PayHandler extends Handler {
        private final WeakReference<UnicomPayActivity> mTarget;

        PayHandler(UnicomPayActivity target) {
            mTarget = new WeakReference<UnicomPayActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            UnicomPayActivity activity = mTarget.get();
            switch (msg.what) {
                case PAY_SDK_RESULT: {
                    PayResult result = (PayResult) msg.obj;
                    // 执行交易状态查询
                    PayStatusRequest req = new PayStatusRequest();
                    req.setToken(((KidsMindApplication) activity.getApplication())
                            .getToken());
                    req.setOutTradeNo(result.getConsumeStreamId());
                    // req.setTotalFee(result.getTotalFee());

                    activity.mQueryCount = 9;
                    Message message = activity.mHandler.obtainMessage();
                    message.what = PAY_SERVER_RESULT;
                    message.obj = req;
                    activity.mHandler.sendMessage(message);
                }
                break;
                case PAY_SERVER_RESULT: {
                    // 执行交易状态查询
                    PayStatusRequest req = (PayStatusRequest) msg.obj;
                    String url = AppConfig.PAY_UNICOM_TV_QUERY;
                    activity.queryPayStatus(url, req);
                }
                break;
                case PAY_SDK_DESTORY:
                    Commplatform.getInstance().destroy();
                    break;
                case PAY_INIT_RESULT:
                    int paramInt = msg.arg1;
                    if (paramInt == ErrorCode.COM_PLATFORM_SUCCESS) {
                        Log.v(TAG, "init_success");
                        activity.authNew(AppConfig.UNICOM_PRODUCTID, productType, AppConfig.UNICOM_CONTENTID);
                    } else {
                        Log.v(TAG, "init_failed");
                    }

                    break;
                case PAY_AUTH_RESULT:
                    int code = msg.arg1;
                    AuthResult result = (AuthResult) msg.obj;
                    if (code == ErrorCode.COM_PLATFORM_SUCCESS) {
                        ProductInfos[] productInfos = result.productInfos;
                        if (productInfos != null && productInfos.length > 0) {
                            activity.mProductId = productInfos[0].productId;
                            activity.pay(activity.payRequest);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

    }

}
