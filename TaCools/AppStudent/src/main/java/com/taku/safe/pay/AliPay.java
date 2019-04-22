package com.taku.safe.pay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by fylder on 2017/8/2.
 */

public class AliPay implements PayImpl {

    WeakReference<Activity> activityRef;
    Handler aliPayHandler;

    public AliPay(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    public void setAliPayHandler(Handler aliPayHandler) {
        this.aliPayHandler = aliPayHandler;
    }

    @Override
    public boolean isInstall() {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(activityRef.get().getApplicationContext().getPackageManager());
        return componentName != null;
    }

    /**
     * 支付宝授权
     * 需要拿到authInfo
     */
    @Override
    public void authorized() {
//        if (aliPayHandler == null) {
//            Log.d("pay", "AliPay need to set aliPayHandler");
//            return;
//        }
//        Message msg = new Message();
//        msg.what = PurseLoginAuthorizeFragment.ALIPAY_INFO_START;
//        aliPayHandler.sendMessage(msg);
//
//        PayHttp payHttp = new PayHttp(activityRef.get());
//        payHttp.getAuthInfo(new CallBackPay() {
//            @Override
//            public void success(String msg) {
//                Message msgInfo = new Message();
//                msgInfo.what = PurseLoginAuthorizeFragment.ALIPAY_INFO_END;
//                aliPayHandler.sendMessage(msgInfo);
//                //        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//进入沙箱模式
//                Gson gson = new Gson();
//                AuthInfoEntity data = gson.fromJson(msg, AuthInfoEntity.class);
//                if (!data.isSuccess()) {
//                    return;
//                }
//
//                final String authInfo = data.getD().getAuthInfo();
//                Runnable authRunnable = new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // 构造AuthTask 对象
//                        AuthTask authTask = new AuthTask(activityRef.get());
//                        // 调用授权接口，获取授权结果
//                        Map<String, String> result = authTask.authV2(authInfo, false);
//
//                        AuthResult authResult = new AuthResult(result, true);
//                        String authCode = authResult.getAuthCode();
//                        String userId = authResult.getUserId();
//                        String resultStatus = authResult.getResultStatus();
//                        // 判断resultStatus 为“9000”且result_code
//                        // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                        Message msg = new Message();
//                        if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//
//                            msg.what = PurseLoginAuthorizeFragment.ALIPAY_SUCCESS;
//                            msg.obj = authResult;
//                        } else {
//                            msg.what = PurseLoginAuthorizeFragment.ALIPAY_ERROR;
//                            msg.obj = authResult;
//                        }
//                        aliPayHandler.sendMessage(msg);
//                    }
//                };
//
//                Thread authThread = new Thread(authRunnable);
//                authThread.start();
//            }
//
//            @Override
//            public void error(String error) {
//                Log.d("test", "获取authInfo异常");
//                Message msg = new Message();
//                msg.what = PurseLoginAuthorizeFragment.ALIPAY_ERROR;
//                aliPayHandler.sendMessage(msg);
//            }
//        });
    }

    /**
     * 支付宝支付
     *
     * @param goodsId
     * @param price
     * @param amount
     */
    @Override
    public void pay(String phone, int goodsId, String price, String amount) {
//        if (aliPayHandler == null) {
//            Log.d("pay", "AliPay need to set aliPayHandler");
//            return;
//        }
//        PayHttp.getOrder(activityRef.get().getBaseContext(), phone, 1, goodsId, price, amount, new PayHttp.CallbackData<WeChatOrderEntity.DBean>() {
//            @Override
//            public void response(final WeChatOrderEntity.DBean dBean) {
//                Runnable payRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//
//                        try {
//                            String orderParam = dBean.getOrderParam();
//                            String sign = dBean.getSign();
//                            String orderInfo = orderParam + "&" + sign;
//                            PayTask alipay = new PayTask(activityRef.get());
//                            Map<String, String> result = alipay.payV2(orderInfo, true);
//                            PayResult payResult = new PayResult(result);
//                            PayResultJson payResultJson = new Gson().fromJson(payResult.getResult(), PayResultJson.class);
//                            String resultStatus = payResult.getResultStatus();
//                            Message msg = new Message();
//                            if (TextUtils.equals(resultStatus, "9000")) {
//                                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                                msg.what = 200;
//                                msg.obj = payResultJson.getAlipay_trade_app_pay_response().getOut_trade_no();
//                            } else {
//                                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                                msg.what = 201;
//                                msg.obj = payResult;
//                            }
//                            aliPayHandler.sendMessage(msg);
//                        } catch (Exception e) {
//                            Log.e("pay", "pay error:" + e.getMessage());
//                            Toast.makeText(activityRef.get(), "支付过程异常", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                };
//
//                Thread payThread = new Thread(payRunnable);
//                payThread.start();
//            }
//
//            @Override
//            public void error(String msg) {
//                Log.e("pay", msg);
//                Toast.makeText(activityRef.get().getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });


    }


}
