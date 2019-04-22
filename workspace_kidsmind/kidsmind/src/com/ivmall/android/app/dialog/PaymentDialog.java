package com.ivmall.android.app.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.pay.payment.AliPayment;
import com.ivmall.android.app.pay.payment.QrcodePayActivity;
import com.ivmall.android.app.wxapi.WXPayEntryActivity;


/**
 * @author chenqy
 *         <p>
 *         设置对话框
 */
public class PaymentDialog extends AlertDialog implements View.OnClickListener {


    /**
     * 有支付要求的特殊渠道
     */
    public static final String MITV_CHANNEL = "70054";  //小米TV
    public static final String YUNOS_TV_CHANNEL = "70050";  //阿里TV
    public static final String COOCAA_TV_CHANNEL = "70059";  //创维酷开TV
    public static final String DOMY_TV_CHANNEL = "70060";  //大麦TV
    public static final String HUAN_PAY_CHANNEL = "70047";  //TCL 欢网支付

    public static final String HUAWEI_CHANNEL = "80015";  //华为移动渠道


    /* Intent传参的Key */
    public static final String PRCIE = "Price";
    public static final String VIPGUID = "vipGuid";
    public static final String VIPTITLE = "vipTitle";
    public static final String QRCODE_NAME = "QrcodeName";
    public static final String ALI_PAY = "alipay";
    public static final String WECHAT_PAY = "wechat";

    public static final String DOMY_PAY = "partnerProductId";


    private Context mContext;

    private TextView mTitle;
    private TextView mPayMoney;

    private RelativeLayout mAlipay;
    private RelativeLayout mAlipayQrcode;
    private RelativeLayout mWechat;
    private RelativeLayout mWechatQrcode;
    //private ImageButton btnBack;


    private double mPrice;
    private String mVipGuid;
    private String mVipTitle;

    /*大麦支付 产品序列号*/
    private String mPartnerProductId;

    public PaymentDialog(Context context, double price, String vipGuid, String vipTitle) {
        super(context);
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


    private void initView() {
        mPayMoney = (TextView) findViewById(R.id.pay_money);
        String price = mPrice + mContext.getResources().getString(R.string.cny);
        mPayMoney.setText(price);

        mAlipay = (RelativeLayout) findViewById(R.id.alipay_pay);
        mAlipayQrcode = (RelativeLayout) findViewById(R.id.alipay_pay_qrcode);
        mWechat = (RelativeLayout) findViewById(R.id.wechat_pay);
        mWechatQrcode = (RelativeLayout) findViewById(R.id.wechat_pay_qrcode);
        // btnBack = (ImageButton) findViewById(R.id.btn_pany_return);


        showThirdPayment();
    }

    /**
     * 大麦支付 产品序列号
     */
    public void setPartnerProductId(String productId) {
        mPartnerProductId = productId;
    }

    /**
     * 初始化支付类型显示
     */
    private void showThirdPayment() {


    }


    /**
     * 设置按键监听
     */
    private void setOnListener() {
        mAlipay.setOnClickListener(this);
        mAlipayQrcode.setOnClickListener(this);
        mWechat.setOnClickListener(this);
        mWechatQrcode.setOnClickListener(this);
        //btnBack.setOnClickListener(this);
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
                // 微信订单请求参数
                Intent intent = new Intent(mContext, WXPayEntryActivity.class);
                intent.putExtra(PRCIE, mPrice);
                intent.putExtra(VIPGUID, mVipGuid);
                mContext.startActivity(intent);
                dismiss();
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
            /*case R.id.btn_pany_return:
                dismiss();
                break;*/
            default:
                break;

        }
        dismiss();
    }


}