package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.uitls.LoginUtils;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.MobileBindRequest;
import com.ivmall.android.app.entity.ProtocolResponse;
import com.ivmall.android.app.entity.SmsCodeRequest;
import com.ivmall.android.app.entity.UserInfo;
import com.ivmall.android.app.impl.OnLoginSuccessListener;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.CountDownClock;

/**
 * Created by koen on 2015/11/10.
 */
public class LoginDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    KidsMindApplication application;
    private LinearLayout linearLogin;
    private TextView tvLoginInfo;
    private ProgressDialog mProgressDialog;
    private EditText etPhoneNumber;
    private EditText etSmsCode;

    private Button btnLogin;
    private CountDownClock tvCountDown;

    private SmsCodeRequest.SmsParm smsParm;
    private OnLoginSuccessListener listener;


    public LoginDialog(Context context, KidsMindApplication kidsApp, OnLoginSuccessListener listener) {
        super(context, R.style.loginDialog);
        mContext = context;
        application = kidsApp;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        initView();
    }

    private void initView() {
        linearLogin = (LinearLayout) findViewById(R.id.linear_login);
        tvLoginInfo = (TextView) findViewById(R.id.tvLoginInfo);
        tvLoginInfo.setText(((KidsMindApplication) mContext.getApplicationContext())
                .getAppConfig("afterLoginPrompt"));
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    BaiduUtils.onEvent(mContext, OnEventId.LOGIN_MOBIM, mContext.getString(R.string.login_mobim));
            }
        });

        etSmsCode = (EditText) findViewById(R.id.et_sms_code);
        etSmsCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    BaiduUtils.onEvent(mContext, OnEventId.LOGIN_PASSWORD, mContext.getString(R.string.login_password));
            }
        });

        btnLogin = (Button) findViewById(R.id.tv_login);
        btnLogin.setOnClickListener(this);

        tvCountDown = (CountDownClock) findViewById(R.id.tv_sms_code);
        tvCountDown.setOnClickListener(this);
        tvCountDown.setTextInfo(mContext.getString(R.string.get_password));
        String mobleNum = application.getMoblieNum();
        if (!StringUtils.isEmpty(mobleNum)) {
            etPhoneNumber.setText(mobleNum);
        }
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

                mProgressDialog.dismiss();
                dismiss();
            }

            @Override
            public void onFailed(String message) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
        loginUtils.mobileLogin(phoneNum, smsCode);

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
                    // 设置动态码倒计时
                    String smsCountdown = application.getAppConfig("smsCountdown");
                    tvCountDown.setCountMillSecond(Integer.parseInt(smsCountdown) * 1000);
                    tvCountDown.start();
                    etSmsCode.requestFocus();
                }

            }

        });

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

        String token = application.getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    etSmsCode.setText("");
                    //String token = resp.getToken();  //绑定成功不返回数据
                    String token = application.getToken();
                    application.setToken(token);
                    application.setMoblieNum(phoneNum);
                    application.reqUserInfo();//刷新用户VIP信息
                    application.reqProfile(new OnCreateProfileResult());

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
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.tv_sms_code) {
            String num = etPhoneNumber.getText().toString();
            if (StringUtils.isEmpty(num)) {
                Toast.makeText(mContext, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AppUtils.isMobileNum(num)) {
                Toast.makeText(mContext, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            smsParm = SmsCodeRequest.SmsParm.bind;
            reqSmsCode(num, smsParm);
            BaiduUtils.onEvent(mContext, OnEventId.LOGIN_GET_PASSWORD,
                    mContext.getString(R.string.login_get_password));
        } else if (id == R.id.tv_login) {

            String num = etPhoneNumber.getText().toString();
            String smsCode = etSmsCode.getText().toString();
            if (StringUtils.isEmpty(smsCode)) {
                Toast.makeText(mContext, "动态密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AppUtils.isSmsCode(smsCode)) {
                Toast.makeText(mContext, "请输入正确的动态密码", Toast.LENGTH_SHORT).show();
                return;
            }
            mProgressDialog = AppUtils.showProgress(mContext, null,
                    "正在为您登录", false, true);
            mobileBind(num, smsCode);
            BaiduUtils.onEvent(mContext, OnEventId.CLICK_LOGIN,
                    mContext.getString(R.string.click_login));
        }
    }

    private class OnCreateProfileResult implements OnSucessListener {

        @Override
        public void sucess() {
            listener.onSuccess();
        }

        @Override
        public void create() {
            listener.onSuccess();
        }

        @Override
        public void fail() {

        }
    }
}
