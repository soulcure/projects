package com.applidium.nickelodeon.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.ForgetPasswordRequest;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.entity.SmsCodeRequest;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.CountDownClock;


public class ForgetPasswordFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = ForgetPasswordFragment.class.getSimpleName();

    private Activity mAct;
    public OnArticleSelectedListener mOnArticleKidsMind;

    private EditText etPhoneNumber;
    private CountDownClock btnVerifyCode;

    private EditText etVerifyCode;
    private EditText etPassword;

    private Button btnConfirm;
    private ImageButton btnBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
        try {
            mOnArticleKidsMind = (OnArticleSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAct.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forget_password_activity, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        initViews(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            initData(bundle);
        }
    }


    private void initViews(View v) {
        etPhoneNumber = (EditText) v.findViewById(R.id.et_phone_number);
        etPhoneNumber.requestFocus();

        btnVerifyCode = (CountDownClock) v.findViewById(R.id.tv_countdown);
        btnVerifyCode.setOnClickListener(this);

        etVerifyCode = (EditText) v.findViewById(R.id.et_verify_code);
        etPassword = (EditText) v.findViewById(R.id.et_password);

        btnConfirm = (Button) v.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);

        btnBack = (ImageButton) v.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }

    private void initData(Bundle bundle) {

        String mobileNum = bundle.getString(LoginIvmallFragment.MOBILE_NUM);

        if (!StringUtils.isEmpty(mobileNum) && AppUtils.isMobileNum(mobileNum)) {
            etPhoneNumber.setText(mobileNum);
        }
    }

    @Override
    public void onClick(View v) {

        MediaPlayerService.playSound(mAct, MediaPlayerService.ONCLICK);
        int id = v.getId();
        switch (id) {
            case R.id.tv_countdown: {
                String mobile = etPhoneNumber.getText().toString();

                if (!AppUtils.isMobileNum(mobile)) {
                    Toast.makeText(getActivity(), R.string.phone_num_err, Toast.LENGTH_SHORT).show();
                } else {
                    reqSmsCode(mobile);
                }
            }
            break;
            case R.id.btn_confirm: {
                String mobile = etPhoneNumber.getText().toString();
                String password = etPassword.getText().toString();
                String verifycode = etVerifyCode.getText().toString();

                if (!AppUtils.isMobileNum(mobile)) {
                    Toast.makeText(getActivity(), R.string.phone_num_err, Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.checkPassWord(password)) {
                    Toast.makeText(getActivity(), R.string.password_err, Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isSmsCode(verifycode)) {
                    Toast.makeText(getActivity(), R.string.verifycode_err, Toast.LENGTH_SHORT).show();
                } else {
                    forgetPassword(mobile, password, verifycode);
                }
            }
            break;
            case R.id.btn_back:
                mOnArticleKidsMind.skipToFragment(LoginIvmallFragment.TAG, null);
                break;
            default:
                break;
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
                    /*String smsCountdown = ((MNJApplication) getActivity().getApplication())
                            .getAppConfig("smsCountdown");*/
                    btnVerifyCode.setCountMillSecond(60 * 2 * 1000);
                    btnVerifyCode.start();
                    etVerifyCode.requestFocus();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
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
     */
    private void forgetPassword(String mobile, String password, String validateCode) {


        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setMobile(mobile);
        request.setPassword(password);
        request.setValidateCode(validateCode);

        String json = request.toJsonString();

        String url = AppConfig.FORGET_PASSWORD;

        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);
                if (resp.isSucess()) {
                    mOnArticleKidsMind.skipToFragment(LoginIvmallFragment.TAG, null);
                    Toast.makeText(getActivity(), R.string.change_pw_sucess, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


}