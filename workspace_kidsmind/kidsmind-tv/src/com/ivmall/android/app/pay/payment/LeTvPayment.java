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
import com.letv.tvos.paysdk.LetvOnPayListener;
import com.letv.tvos.paysdk.LetvPay;
import com.letv.tvos.paysdk.appmodule.pay.model.LetvOrder;

import java.lang.ref.WeakReference;

public class LeTvPayment {

    private static final String TAG = LeTvPayment.class.getSimpleName();

    private static final int PAY_SDK_RESULT = 1;
    private static final int PAY_SERVER_RESULT = 2;


    private static LeTvPayment instance = null;

    private Activity mAct;
    private int mQueryCount;

    private PayHandler mHandler;

    /**
     * Letv 支付构造函数 单例
     */
    private LeTvPayment() {
        mHandler = new PayHandler(this);
    }

    /**
     * 小米TV支付单例
     *
     * @param act
     * @return
     */
    public static LeTvPayment getInstance(Activity act) {
        if (instance == null) {
            instance = new LeTvPayment();
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

        String url = AppConfig.PAY_LE_TV_PREPARE;

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
        String username = LetvPay.getUniqueId(mAct); // 单机app接入

        String productId = payResponse.getOutTradeNo();   //商品ID
        String productName = payResponse.getSubject();  //商品名称
        String productPrice = payResponse.getTotalFee() + ""; // 商品单价
        int count = 1;//    购买数量

        /*商品的Url,用于支付SDK调出支付二维码时展示，商品图片格式要求420*234示例：
          http://i3.letvimg.com/iptv/201409/09/ea9338f4-cc7a-47e6-9395-f88d882c5324.png
          如果不填写productImgUrl，则在商品支付页面看不到商品图片*/
        String productImgUrl = null;



        /*我不是回调地址，我是回调地址所带一个参数的value值，我可以有也可以没有，主人随你便哦*/
        //String serverMessage = null;

        /*自定义参数,在订单支付成功或失败回调客户端时返回 或者查询历史订单时返回在param1字段中
        （需自行解析param1信息）协助功能*/

        //Map<String, String> customParams = new HashMap<String, String>();
        //customParams.put("kidsmind", payResponse.getParams());


        LetvOrder letvOrder = new LetvOrder(username, productId, productName, productPrice, count, productImgUrl);
        //letvOrder.setServerMessage(serverMessage);
        //letvOrder.setCustomParams(customParams);

        LetvPay.pay(mAct, letvOrder, new LetvOnPayListener() {
            @Override
            public void onPaySuccess(LetvOrder order) {
                Message msg = mHandler.obtainMessage();
                msg.what = PAY_SDK_RESULT;
                msg.obj = order;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onPayFailed(LetvOrder order, int stateCode) {
                Toast.makeText(mAct, "支付失败", Toast.LENGTH_SHORT).show();
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
        private final WeakReference<LeTvPayment> mTarget;

        PayHandler(LeTvPayment target) {
            mTarget = new WeakReference<LeTvPayment>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_SDK_RESULT: {
                    LetvOrder result = (LetvOrder) msg.obj;
                    // 执行交易状态查询
                    PayStatusRequest req = new PayStatusRequest();
                    req.setToken(((KidsMindApplication) mAct.getApplication())
                            .getToken());
                    req.setOutTradeNo(result.getExternalProductId());
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
                    String url = AppConfig.PAY_LE_TV_QUERY;
                    queryPayStatus(url, req);
                }
                break;
                default:
                    break;
            }
        }

    }

}
