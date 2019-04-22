package com.applidium.nickelodeon.dialog;

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

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.MobileBindRequest;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.entity.SmsCodeRequest;
import com.applidium.nickelodeon.entity.UserInfo;
import com.applidium.nickelodeon.impl.OnLoginSuccessListener;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.OnEventId;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.CountDownClock;


/**
 * Created by koen on 2015/11/10.
 */
public class LoginDialog extends Dialog implements View.OnClickListener {

    private Context context;

    MNJApplication application;
    private LinearLayout linearLogin;
    private TextView tvLoginInfo;
    private ProgressDialog mProgressDialog;
    private EditText etPhoneNumber;
    private EditText etSmsCode;

    private Button btnLogin;
    private CountDownClock tvCountDown;

    private SmsCodeRequest.SmsParm smsParm;
    private OnLoginSuccessListener listener;


    public LoginDialog(Context context, MNJApplication kidsApp, OnLoginSuccessListener listener) {
        super(context, R.style.loginDialog);
        this.context = context;
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
        tvLoginInfo.setText(((MNJApplication) context.getApplicationContext())
                .getAppConfig("afterLoginPrompt"));
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);

        etSmsCode = (EditText) findViewById(R.id.et_sms_code);

        btnLogin = (Button) findViewById(R.id.tv_login);
        btnLogin.setOnClickListener(this);

        tvCountDown = (CountDownClock) findViewById(R.id.tv_sms_code);
        tvCountDown.setOnClickListener(this);
        tvCountDown.setTextInfo(context.getString(R.string.get_verify_code));
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
        /*String url = AppConfig.MOBILE_LOGIN;
        MobileBindRequest request = new MobileBindRequest();
        request.setMobile(phoneNum);
        request.setValidateCode(smsCode);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);
                if (resp.isSucess()) {
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                    etSmsCode.setText("");
                    //String token = resp.getToken();
                    //UserInfo.VipType vip = resp.getVipLevel();
                    //application.setToken(token);  //该句话已经更新了登录状态
                    application.setMoblieNum(phoneNum);
                    //application.setVipLevel(vip);
                    application.reqUserInfo();//刷新用户VIP信息
                    application.reqProfile(new OnCreateProfileResult());
                    dismiss();

                } else {
                    Toast.makeText(context, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

        });*/

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
        //request.setUsefor(parm);

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
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    etSmsCode.setText("");
                    //String token = resp.getToken();  //绑定成功不返回数据
                    String token = application.getToken();
                    application.setToken(token);
                    application.setMoblieNum(phoneNum);
                    application.reqUserInfo();//刷新用户VIP信息
                    application.reqProfile(new OnCreateProfileResult());
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
                Toast.makeText(context, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AppUtils.isMobileNum(num)) {
                Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            smsParm = SmsCodeRequest.SmsParm.bind;
            reqSmsCode(num, smsParm);
        } else if (id == R.id.tv_login) {

            String num = etPhoneNumber.getText().toString();
            String smsCode = etSmsCode.getText().toString();
            if (StringUtils.isEmpty(smsCode)) {
                Toast.makeText(context, "动态密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AppUtils.isSmsCode(smsCode)) {
                Toast.makeText(context, "请输入正确的动态密码", Toast.LENGTH_SHORT).show();
                return;
            }
            mProgressDialog = AppUtils.showProgress(context, null,
                    "正在为您登录", false, true);
            mobileBind(num, smsCode);
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
