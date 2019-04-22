package com.applidium.nickelodeon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.WebViewDialog;
import com.applidium.nickelodeon.entity.LoginResponse;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.entity.RegisterRequest;
import com.applidium.nickelodeon.entity.SmsCodeRequest;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.views.CountDownClock;


public class RegisterActivity extends Activity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();


    private Context mContext;

    private EditText etPhoneNumber;
    private EditText etPassword;
    private EditText etVerifyCode;
    private CountDownClock tvCountDown;
    private Button btnAgreeClause;
    private TextView tvUserClause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_fragment);

        mContext = this;
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etPassword = (EditText) findViewById(R.id.et_password);
        etVerifyCode = (EditText) findViewById(R.id.et_verify_code);
        tvUserClause = (TextView) findViewById(R.id.tv_user_clause);
        tvUserClause.setOnClickListener(this);

        tvCountDown = (CountDownClock) findViewById(R.id.tv_countdown);
        tvCountDown.setOnClickListener(this);

        btnAgreeClause = (Button) findViewById(R.id.btn_agree_clause);
        btnAgreeClause.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.et_phone_number) {

        } else if (id == R.id.et_phone_number) {

        } else if (id == R.id.et_password) {

        } else if (id == R.id.et_verify_code) {

        } else if (id == R.id.tv_countdown) {
            String mobile = etPhoneNumber.getText().toString();

            if (!AppUtils.isMobileNum(mobile)) {
                Toast.makeText(this, R.string.phone_num_err, Toast.LENGTH_SHORT).show();
            } else {
                reqSmsCode(mobile);
            }
        } else if (id == R.id.btn_agree_clause) {
            String mobile = etPhoneNumber.getText().toString();
            String password = etPassword.getText().toString();
            String verifycode = etVerifyCode.getText().toString();

            if (!AppUtils.isMobileNum(mobile)) {
                Toast.makeText(this, R.string.phone_num_err, Toast.LENGTH_SHORT).show();
            } else if (!AppUtils.checkPassWord(password)) {
                Toast.makeText(this, R.string.password_err, Toast.LENGTH_SHORT).show();
            } else if (!AppUtils.isSmsCode(verifycode)) {
                Toast.makeText(this, R.string.verifycode_err, Toast.LENGTH_SHORT).show();
            } else {
                doRegister(mobile, password, verifycode, null);
            }
        } else if (id == R.id.tv_user_clause) {
            String url = AppConfig.USER_CLAUSE;
            WebViewDialog dialog = new WebViewDialog(mContext, url);
            dialog.show();
        }
    }


    public void reqSmsCode(String mobile) {
        String url = AppConfig.SMS_CODE;
        SmsCodeRequest request = new SmsCodeRequest();
        request.setMobile(mobile);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);
                if (resp.isSucess()) {
                    // 设置动态码倒计时
                    String smsCountdown = ((MNJApplication) mContext.getApplicationContext())
                            .getAppConfig("smsCountdown");
                    tvCountDown.setCountMillSecond(Integer.parseInt(smsCountdown) * 1000);
                    tvCountDown.start();
                    etVerifyCode.requestFocus();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });


    }


    /**
     * 注册
     *
     * @param mobile       手机号码
     * @param password     密码
     * @param validateCode 短信验证码
     * @param promoCode
     */
    private void doRegister(String mobile, String password, String validateCode, String promoCode) {

        RegisterRequest request = new RegisterRequest();
        request.setMobile(mobile);
        request.setPassword(password);
        request.setValidateCode(validateCode);
        request.setPromoCode(promoCode);
        String json = request.toJsonString();

        String url = AppConfig.USER_REGISTER;

        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                LoginResponse resp = GsonUtil.parse(response,
                        LoginResponse.class);
                if (resp.isSucess()) {

                }

            }
        });


    }


}