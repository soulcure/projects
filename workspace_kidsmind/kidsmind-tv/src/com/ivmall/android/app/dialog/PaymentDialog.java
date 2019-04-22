package com.ivmall.android.app.dialog;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.pay.payment.CoocaaTvPayment;
import com.ivmall.android.app.pay.payment.DomyPayActivity;
import com.ivmall.android.app.pay.payment.HuanTvPayment;
import com.ivmall.android.app.pay.payment.LeTvPayment;
import com.ivmall.android.app.pay.payment.MiTvPayment;
import com.ivmall.android.app.pay.payment.UnicomPayActivity;
import com.ivmall.android.app.pay.payment.YunOsTvPayment;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.pay.payment.AliPayment;
import com.ivmall.android.app.pay.payment.QrcodePayActivity;
import com.ivmall.android.app.pay.payment.WeChatPayment;
import com.xiaomi.mitv.osspay.sdk.proxy.ThirdPayProxy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author chenqy
 *         <p/>
 *         设置对话框
 */
public class PaymentDialog extends Dialog implements View.OnClickListener {


    /**
     * 有支付要求的特殊渠道
     */
    public static final String MITV_CHANNEL = "70054";  //小米TV
    public static final String YUNOS_TV_CHANNEL = "70050";  //阿里TV
    public static final String COOCAA_TV_CHANNEL = "70059";  //创维酷开TV
    public static final String DOMY_TV_CHANNEL = "70060";  //大麦TV
    public static final String HUAN_PAY_CHANNEL = "70047";  //TCL 欢网支付
    public static final String LETV_PAY_CHANNEL = "70035";  //Letv 乐视TV支付
    public static final String UNICOM_PAY_CHANNEL = "70088";  //联通支付

    /* Intent传参的Key */
    public static final String PRCIE = "Price";
    public static final String VIPGUID = "vipGuid";
    public static final String VIPTITLE = "vipTitle";
    public static final String QRCODE_NAME = "QrcodeName";
    public static final String ALI_PAY = "alipay";
    public static final String WECHAT_PAY = "wechat";

    public static final String DOMY_PAY = "partnerProductId";

    private Context mContext;

    private TextView mPayMoney;

    private RelativeLayout mAlipay;
    private RelativeLayout mAlipayQrcode;
    private RelativeLayout mWechat;
    private RelativeLayout mWechatQrcode;


    private double mPrice;
    private String mVipGuid;
    private String mVipTitle;

    /**
     * 支付入口
     *
     * @param context
     * @param price
     * @param vipGuid
     * @param vipTitle
     * @param productId
     */
    public static void payment(Context context, double price, String vipGuid, String vipTitle, String productId) {
        String promoter = ((KidsMindApplication) context.getApplicationContext()).getPromoter();
        String token = ((KidsMindApplication) context.getApplicationContext()).getToken();

        //小米支付
        boolean isSupport = ThirdPayProxy.instance(AppConfig.AppContext).isSupportFeature(); //是否支持最新的小米SDK
        if (isSupport && promoter.equals(MITV_CHANNEL)) {
            PayRequest payRequest = new PayRequest();
            payRequest.setToken(token);
            payRequest.setPrice(price);
            payRequest.setVipGuid(vipGuid);

            MiTvPayment.getInstance((Activity) context).pay(payRequest);
        }
        //YunOs TV支付
        else if (promoter.equals(YUNOS_TV_CHANNEL)) {
            PayRequest payRequest = new PayRequest();

            payRequest.setToken(token);
            payRequest.setPrice(price);
            payRequest.setVipGuid(vipGuid);

            YunOsTvPayment.getInstance((Activity) context).pay(payRequest);
        }
        //创维酷开 TV支付
        else if (promoter.equals(COOCAA_TV_CHANNEL)) {
            PayRequest payRequest = new PayRequest();

            payRequest.setToken(token);
            payRequest.setPrice(price);
            payRequest.setVipGuid(vipGuid);

            CoocaaTvPayment.getInstance((Activity) context).pay(payRequest);
        }
        //大麦TV支付
        else if (promoter.equals(DOMY_TV_CHANNEL)
                && !StringUtils.isEmpty(productId)) {
            Intent intent = new Intent(context, DomyPayActivity.class);
            intent.putExtra(PRCIE, price);
            intent.putExtra(VIPTITLE, vipTitle);
            intent.putExtra(VIPGUID, vipGuid);

            intent.putExtra(DOMY_PAY, productId);

            context.startActivity(intent);
        }
        //欢网支付
        else if (promoter.equals(HUAN_PAY_CHANNEL)) {
            PayRequest payRequest = new PayRequest();
            payRequest.setToken(token);
            payRequest.setPrice(price);
            payRequest.setVipGuid(vipGuid);

            HuanTvPayment.getInstance((Activity) context).pay(payRequest);
        }
        //Letv支付
        else if (promoter.equals(LETV_PAY_CHANNEL)) {
            PayRequest payRequest = new PayRequest();
            payRequest.setToken(token);
            payRequest.setPrice(price);
            payRequest.setVipGuid(vipGuid);

            LeTvPayment.getInstance((Activity) context).pay(payRequest);
        }
        //联通支付
        else if (promoter.equals(UNICOM_PAY_CHANNEL)) {

            Intent intent = new Intent(context, UnicomPayActivity.class);
            intent.putExtra(PRCIE, price);
            intent.putExtra(VIPTITLE, vipTitle);
            intent.putExtra(VIPGUID, vipGuid);

            intent.putExtra(DOMY_PAY, productId);

            context.startActivity(intent);
        }
        //默认支付宝和微信扫描支付
        else {
            PaymentDialog dialog = new PaymentDialog(context, price, vipGuid, vipTitle);
            dialog.show();
        }
    }


    /**
     * 支付是否需要另起activity
     *
     * @param context
     * @return
     */
    public static boolean isPaymentOtherActivity(Context context) {
        boolean res = false;
        String promoter = ((KidsMindApplication) context.getApplicationContext()).getPromoter();
        if (promoter.equals(PaymentDialog.DOMY_TV_CHANNEL)
                || promoter.equals(PaymentDialog.COOCAA_TV_CHANNEL)) {
            res = true;
        }
        return res;
    }


    /**
     * 私有构造函数
     *
     * @param context
     * @param price
     * @param vipGuid
     * @param vipTitle
     */
    private PaymentDialog(Context context, double price, String vipGuid, String vipTitle) {
        super(context, R.style.full_dialog);
        mContext = context;

        mPrice = price;
        mVipGuid = vipGuid;
        mVipTitle = vipTitle;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.payment_method);
        initView();
        setOnListener();
    }


    /**
     * 初始化UI
     */
    private void initView() {
        mPayMoney = (TextView) findViewById(R.id.pay_money);
        mPayMoney.setText(mPrice + "元");

        mAlipay = (RelativeLayout) findViewById(R.id.alipay_pay);
        mAlipayQrcode = (RelativeLayout) findViewById(R.id.alipay_pay_qrcode);
        mWechat = (RelativeLayout) findViewById(R.id.wechat_pay);
        mWechatQrcode = (RelativeLayout) findViewById(R.id.wechat_pay_qrcode);


    }


    /**
     * 设置按键监听
     */
    private void setOnListener() {
        mAlipay.setOnClickListener(this);
        mAlipayQrcode.setOnClickListener(this);
        mWechat.setOnClickListener(this);
        mWechatQrcode.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();

        switch (v.getId()) {
            case R.id.alipay_pay: {
                // 支付宝订单请求参数
                PayRequest payRequest = new PayRequest();

                payRequest.setToken(token);
                payRequest.setPrice(mPrice);
                payRequest.setVipGuid(mVipGuid);

                AliPayment.getInstance((Activity) mContext).pay(payRequest);
            }
            break;
            case R.id.alipay_pay_qrcode: {
                // 传参： 支付方式，金额，套餐，
                Intent intent = new Intent(mContext, QrcodePayActivity.class);
                intent.putExtra(QRCODE_NAME, ALI_PAY);
                intent.putExtra(PRCIE, mPrice);
                intent.putExtra(VIPTITLE, mVipTitle);
                intent.putExtra(VIPGUID, mVipGuid);

                mContext.startActivity(intent);
            }
            break;
            case R.id.wechat_pay: {
                // 检查 应用是否安装
                if (!WeChatPayment.getInstance((Activity) mContext).isWXAppInstalled()) {
                    Toast.makeText(mContext, mContext.getString(R.string.not_install_wxapp), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!WeChatPayment.getInstance((Activity) mContext).getWXAppSupportAPI()) {
                    Toast.makeText(mContext, mContext.getString(R.string.unsupported_pay_wxapp), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 微信订单请求参数
                PayRequest payRequest = new PayRequest();
                payRequest.setToken(token);
                payRequest.setPrice(mPrice);
                payRequest.setVipGuid(mVipGuid);

                WeChatPayment.getInstance((Activity) mContext).pay(payRequest);
            }
            break;
            case R.id.wechat_pay_qrcode: {
                // 传参： 支付方式，金额，套餐，
                Intent intent = new Intent(mContext, QrcodePayActivity.class);
                intent.putExtra(QRCODE_NAME, WECHAT_PAY);
                intent.putExtra(PRCIE, mPrice);
                intent.putExtra(VIPTITLE, mVipTitle);
                intent.putExtra(VIPGUID, mVipGuid);

                mContext.startActivity(intent);
            }
            break;
            default:
                break;

        }
        dismiss();
    }


}