package com.applidium.nickelodeon.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.MobileBindRequest;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.entity.SmsCodeRequest;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.CountDownClock;

/**
 *
 */
public class BindPhoneDialog extends Dialog implements OnClickListener {

    private Context mContext;


    private EditText etPhoneNumber;
    private EditText etVerifyCode;
    private CountDownClock tvCountDown;
    private Button btnConfirm;
    private Button btnCancel;
    private MNJApplication application;
    private ChangeUsernameDialog.OnChangeSuccessListener listener;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */

    public BindPhoneDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
        application = (MNJApplication) mContext.getApplicationContext();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_activity);
        initView();
    }

    private void initView() {

        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etVerifyCode = (EditText) findViewById(R.id.et_verify_code);

        tvCountDown = (CountDownClock) findViewById(R.id.tv_countdown);
        tvCountDown.setOnClickListener(this);
        tvCountDown.setTextInfo(mContext.getString(R.string.get_verify_code));

        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

    }

    private void reqSmsCode(String mobile) {
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
                    /*String smsCountdown = ((MNJApplication) getActivity().getApplication())
                            .getAppConfig("smsCountdown");*/
                    tvCountDown.setCountMillSecond(60 * 2 * 1000);
                    tvCountDown.start();
                    etVerifyCode.requestFocus();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
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

        String token = ((MNJApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    application.setMoblieNum(phoneNum);
                    dismiss();
                    ChangePasswordDialog dialog = new ChangePasswordDialog(mContext, true);
                    dialog.setChangeSuccessListener(listener);
                    dialog.show();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void setChangeSuccessListener(ChangeUsernameDialog.OnChangeSuccessListener listener) {
        this.listener = listener;
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
        if (id == R.id.tv_countdown) {
            String phoneNumber = etPhoneNumber.getText().toString();
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
            reqSmsCode(phoneNumber);
        }
        if (id == R.id.btn_confirm) {
            String phoneNumber = etPhoneNumber.getText().toString();
            String phoneCode = etVerifyCode.getText().toString();
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
        if (id == R.id.btn_cancel) {
            dismiss();
        }
    }


}
