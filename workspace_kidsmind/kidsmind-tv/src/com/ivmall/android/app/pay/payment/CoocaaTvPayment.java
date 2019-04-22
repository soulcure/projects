package com.ivmall.android.app.pay.payment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.coocaa.ccapi.CcApi;
import com.coocaa.ccapi.OrderData;
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

public class CoocaaTvPayment {
    private static final String TAG = CoocaaTvPayment.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;


    private static CoocaaTvPayment instance = null;

    private Activity mAct;
    private int mQueryCount;


    private PayHandler mHandler;

    /**
     * 小米TV支付构造函数 单例
     */
    private CoocaaTvPayment() {
        mHandler = new PayHandler(this);

    }

    /**
     * 小米TV支付单例
     *
     * @param act
     * @return
     */
    public static CoocaaTvPayment getInstance(Activity act) {
        if (instance == null) {
            instance = new CoocaaTvPayment();
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

        String url = AppConfig.PAY_COOCAA_TV_PREPARE;

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
        CcApi api = new CcApi(mAct);

        String appcode = AppConfig.COOCAA_APPID;       //商户编号ID,由酷开发布给第三方
        String productname = payResponse.getSubject();     //商品名称，例如“影视包年”
        String producttype = AppConfig.COOCAA_TYPE;     //商品类型，在“实体”和“虚拟”中选择
        String tradeid = payResponse.getOutTradeNo();         //订单编号ID
        //必填，通知支付结果给第三方开发者服务器URL，必须以http://开头，目前支持80端口 ，参数内容为，json格式字符串 例如：{"notify_url":"http://tv.coocaa.com/notify_url.html"}

        Special special = new Special();
        special.notify_url = payResponse.getNotifyURL();

        //String specialtype = "{\"notify_url\":\"http://42.121.113.121:8090/aqiyiOrder/viewMain.html\"}";
        String specialtype = GsonUtil.format(special);
        double amount = payResponse.getTotalFee();       //商品价格，以“元”为单位

        OrderData order = new OrderData();

        order.setappcode(appcode);
        order.setProductName(productname);
        order.setProductType(producttype);
        order.setSpecialType(specialtype);
        order.setTradeId(tradeid);
        order.setamount(amount);

        CcApi.PurchaseCallBack pB = new CcApi.PurchaseCallBack() {

            @Override
            public void pBack(int resultstatus, String tradeid, String uslever,
                              String resultmsg, double balance, String purchWay, String order_Adress) {
                String ss = null;
                if (resultstatus == 0) {
                    ss = "成功";
                    // 第三方应用完成后
                    Message msg = mHandler.obtainMessage();
                    msg.what = PAY_SDK_RESULT;
                    msg.obj = payResponse;
                    mHandler.sendMessage(msg);
                } else if (resultstatus == 1) {
                    ss = "失败";
                } else {
                    ss = "异常";
                }

                if (AppConfig.TEST_HOST) {
                    Toast.makeText(mAct,
                            "支付：" + ss + "\n订单号：" + tradeid + "\n返回信息：" + resultmsg + "\n用户等级："
                                    + uslever + "\n账户余额：" + balance + "\n支付方式：" + purchWay,
                            Toast.LENGTH_SHORT).show();
                }


            }
        };
        api.purchase(order, pB);

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
        private final WeakReference<CoocaaTvPayment> mTarget;

        PayHandler(CoocaaTvPayment target) {
            mTarget = new WeakReference<CoocaaTvPayment>(target);
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
                    String url = AppConfig.PAY_COOCAA_TV_QUERY;
                    queryPayStatus(url, req);
                }
                break;
                default:
                    break;
            }
        }

    }

    /**
     * 创维酷开TV支付回掉地址
     * for class to json
     */
    private class Special {
        String notify_url;
    }

}
