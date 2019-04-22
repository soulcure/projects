package com.ivmall.android.app.pay.payment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ivmall.android.app.KidsMindApplication;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ZXingUtil;

import java.lang.ref.WeakReference;

public class QrcodePayActivity extends Activity {

    private static final String TAG = QrcodePayActivity.class.getSimpleName();

    private static final int PAY_SERVER_RESULT = 1;

    public static final String ALI_PAY = "alipay";
    public static final String WECHAT_PAY = "wechat";

    private TextView mPayHintText;
    private ImageView mPayImage;
    private ImageView mPayQrcode;

    private Double mPrice;
    private String mVipGuid;

    private String mOutTradeNo; // 二维码支付订单号

    private String mQrcodeName;
    private String mVipTitle;

    private PayHandler mHandler;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.alipay_pay);
        mHandler = new PayHandler(this);

        mPrice = getIntent().getExtras().getDouble(
                PaymentDialog.PRCIE);
        mVipGuid = getIntent().getExtras().getString(
                PaymentDialog.VIPGUID);
        mVipTitle = getIntent().getExtras().getString(
                PaymentDialog.VIPTITLE);

        mQrcodeName = getIntent().getExtras().getString(
                PaymentDialog.QRCODE_NAME);

        initView();
        initData();
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


    private void initView() {
        mPayHintText = (TextView) findViewById(R.id.alipay_text);
        mPayImage = (ImageView) findViewById(R.id.pay_img);
        mPayQrcode = (ImageView) findViewById(R.id.alipay_qrcode);

    }

    private void initData() {
        if (mQrcodeName != null && !mQrcodeName.isEmpty()) {
            String url = "";
            if (mQrcodeName.equalsIgnoreCase(ALI_PAY)) {
                mPayHintText.setText(R.string.alipay_text_des);
                mPayImage.setBackgroundResource(R.drawable.phone_alipay);
                url += AppConfig.PAY_QR_ALIPAY_NOTIFY;


            } else if (mQrcodeName.equalsIgnoreCase(WECHAT_PAY)) {
                mPayHintText.setText(R.string.wechat_text_des);
                mPayImage.setBackgroundResource(R.drawable.phone_weixin);
                url += AppConfig.PAY_WX_PREPARE;
            }

            initPayQrCode(url, mQrcodeName);
        }
    }


    /**
     * 二维码扫描支付 支持微信"wechat" 和 支付宝"alipay"
     */
    private void initPayQrCode(String url, final String paySdk) {
        PayRequest payRequest = new PayRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        payRequest.setToken(token);
        payRequest.setPrice(mPrice);
        payRequest.setVipGuid(mVipGuid);

        String json = payRequest.toJsonString();

        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                PayResponse payResponse = GsonUtil.parse(response,
                        PayResponse.class);
                if (payResponse.isSuccess()) {
                    String codeUrl = "";
                    String resUrl = "";
                    if (paySdk.equalsIgnoreCase(ALI_PAY)) { // 支付宝
                        codeUrl = payResponse.getQrcodeURL();
                        resUrl += AppConfig.PAY_QR_ALIPAY_QUERY;

                    } else if (paySdk.equalsIgnoreCase(WECHAT_PAY)) {// 微信
                        codeUrl = payResponse.getCodeUrl();
                        resUrl += AppConfig.PAY_WX_QUERY;
                    }
                    try {
                        int width = getResources().getDimensionPixelSize(R.dimen.qrcode_width);
                        int height = getResources().getDimensionPixelSize(R.dimen.qrcode_height);

                        Bitmap nestImage = BitmapFactory.decodeResource(
                                getResources(), R.drawable.app_icon);
                        Bitmap bitmap = ZXingUtil.encode(codeUrl, width, height,
                                nestImage);

                        mPayQrcode.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    mOutTradeNo = payResponse.getOutTradeNo();
                    queryPayResult(resUrl);//交易结果查询
                } else {
                    String error = payResponse.getMessage();
                    Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    /**
     * 查询二维码支付结果
     */
    private void queryPayResult(final String url) {
        PayRequest payRequest = new PayRequest();
        // 入参
        String token = ((KidsMindApplication) getApplication()).getToken();

        payRequest.setToken(token);
        payRequest.setOutTradeNo(mOutTradeNo);
        String json = payRequest.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                PayResponse payResponse = GsonUtil.parse(response,
                        PayResponse.class);

                if (payResponse.isSuccess()) {
                    if (payResponse.isTradeResult()) { // 支付完成
                        ((KidsMindApplication) getApplication()).reqUserInfo((Activity) mContext, payResponse.getVipName());
                    } else { // 未查询到支付
                        if (mHandler != null) {
                            Message msg = mHandler.obtainMessage();
                            msg.obj = url;
                            msg.what = PAY_SERVER_RESULT;
                            mHandler.sendMessageDelayed(msg, 2000);
                        }

                    }

                }

            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(PAY_SERVER_RESULT);
        mHandler = null;
    }

    private class PayHandler extends Handler {
        private final WeakReference<QrcodePayActivity> mTarget;

        PayHandler(QrcodePayActivity target) {
            mTarget = new WeakReference<QrcodePayActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_SERVER_RESULT:
                    String url = msg.obj.toString();
                    queryPayResult(url);
                    break;
                default:
                    break;
            }
        }
    }

}
