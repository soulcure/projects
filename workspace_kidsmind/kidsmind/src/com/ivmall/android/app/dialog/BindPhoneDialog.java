package com.ivmall.android.app.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.MobileBindRequest;
import com.ivmall.android.app.entity.ProtocolResponse;
import com.ivmall.android.app.entity.SmsCodeRequest;
import com.ivmall.android.app.entity.SmsCodeRequest.SmsParm;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.LoginUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.CountDownClock;

/**
 *
 */
public class BindPhoneDialog extends Dialog implements OnClickListener {


    private EditText bindPhone;

    private EditText bindPhoneCode;

    private TextView bind;

    private SmsParm smsParm;

    private CountDownClock tvCountDown;

    private Context mContext;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */

    public BindPhoneDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_vip);
        initView();
    }

    private void initView() {
        bindPhone = (EditText) findViewById(R.id.bind_phone);
        bindPhoneCode = (EditText) findViewById(R.id.bind_phone_code);
        bind = (TextView) findViewById(R.id.bind);

        tvCountDown = (CountDownClock) findViewById(R.id.tv_time);
        tvCountDown.setOnClickListener(this);
        tvCountDown.setTextInfo(mContext.getString(R.string.get_verify_code));

        smsParm = SmsCodeRequest.SmsParm.bind;

        bind.setOnClickListener(this);

    }

    /**
     * 1.4 获取短信验证码
     *
     * @param phoneNum
     */
    private void reqSmsCode(String phoneNum, SmsCodeRequest.SmsParm parm) {
        String url = AppConfig.SMS_CODE;
        SmsCodeRequest request = new SmsCodeRequest();
        request.setMobile(phoneNum);
        request.setUsefor(parm);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);
                if (resp.isSucess()) {
                    String smsCountdown = ((KidsMindApplication) getContext().getApplicationContext())
                            .getAppConfig("smsCountdown");
                    tvCountDown.setCountMillSecond(Integer.parseInt(smsCountdown) * 1000);
                    tvCountDown.start();
                    bindPhoneCode.requestFocus();
                }

            }

        });

    }

    /**
     * 1.2 手机号登录
     *
     * @param phoneNum
     * @param smsCode
     */
    private void mobileLogin(final String phoneNum, String smsCode) {

        LoginUtils loginUtils = new LoginUtils(mContext);
        loginUtils.setLoginInSuccess(new LoginUtils.LoginInListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                ((KidsMindApplication) mContext.getApplicationContext()).reqProfile(new OnCreateProfileResult());

            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
        loginUtils.mobileLogin(phoneNum, smsCode);
        dismiss();
    }


    /**
     * 1.5 绑定手机号
     *
     * @param phoneNum
     * @param smsCode
     */
    private void mobileBind(final String phoneNum, final String smsCode) {
        String url = AppConfig.MOBILE_BIND;
        MobileBindRequest request = new MobileBindRequest();
        request.setMobile(phoneNum);
        request.setValidateCode(smsCode);

        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    Toast.makeText(mContext, "绑定并登录成功", Toast.LENGTH_SHORT).show();

                    //String token = resp.getToken();  //绑定成功不返回数据

                    String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();
                    ((KidsMindApplication) mContext.getApplicationContext()).setToken(token);

                    ((KidsMindApplication) mContext.getApplicationContext()).setMoblieNum(phoneNum);

                    /*if (mContext instanceof ParentFragmentActivity) {
                        ((ParentFragmentActivity) mContext).setLoginText(true);
                    }*/
                    MainFragmentActivity.openApp(mContext);

                    dismiss();

                } else {
                    //绑定不成功，做登录处理
                    mobileLogin(phoneNum, smsCode);
                }


            }

        });

    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                try {
                    dismiss();
                } catch (Exception e) {

                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.tv_time) {
            String phoneNumber = bindPhone.getText().toString();
            if (StringUtils.isEmpty(phoneNumber)) {
                Toast.makeText(getContext(), "手机号码不能为空", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (!AppUtils.isMobileNum(phoneNumber)) {
                Toast.makeText(getContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            reqSmsCode(phoneNumber, smsParm);
        }
        if (id == R.id.bind) {
            String phoneNumber = bindPhone.getText().toString();
            String phoneCode = bindPhoneCode.getText().toString();
            if (StringUtils.isEmpty(phoneCode)) {
                Toast.makeText(getContext(), "验证码不能为空", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (!AppUtils.isSmsCode(phoneCode)) {
                Toast.makeText(getContext(), "请输入正确的验证码", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            mobileBind(phoneNumber, phoneCode);
        }
    }


    private class OnCreateProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            KidsMindApplication.insertProvider(mContext);
        }

        @Override
        public void create() {

        }

        @Override
        public void fail() {

        }
    }
}
