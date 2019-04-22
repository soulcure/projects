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
import com.applidium.nickelodeon.dialog.WebViewDialog;
import com.applidium.nickelodeon.entity.LoginResponse;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.entity.RegisterRequest;
import com.applidium.nickelodeon.entity.SmsCodeRequest;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.CountDownClock;
import com.applidium.nickelodeon.views.FoucsTextView;


public class RegisterFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = RegisterFragment.class.getSimpleName();

    private Activity mAct;
    public OnArticleSelectedListener mOnArticleKidsMind;


    private EditText etPhoneNumber;
    private EditText etPassword;
    private EditText etVerifyCode;
    private CountDownClock tvCountDown;
    private Button btnAgreeClause;
    private FoucsTextView tvUserClause;

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
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        initViews(view);
        return view;
    }


    private void initViews(View v) {

        etPhoneNumber = (EditText) v.findViewById(R.id.et_phone_number);
        etPassword = (EditText) v.findViewById(R.id.et_password);
        etVerifyCode = (EditText) v.findViewById(R.id.et_verify_code);

        tvUserClause = (FoucsTextView) v.findViewById(R.id.tv_user_clause);
        tvUserClause.setOnClickListener(this);

        tvCountDown = (CountDownClock) v.findViewById(R.id.tv_countdown);
        tvCountDown.setOnClickListener(this);

        btnAgreeClause = (Button) v.findViewById(R.id.btn_agree_clause);
        btnAgreeClause.setOnClickListener(this);
        btnAgreeClause.requestFocus();


        btnBack = (ImageButton) v.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
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
            case R.id.btn_agree_clause: {
                String mobile = etPhoneNumber.getText().toString();
                String password = etPassword.getText().toString();
                String verifycode = etVerifyCode.getText().toString();

                if (!AppUtils.isMobileNum(mobile)) {
                    Toast.makeText(getActivity(), R.string.phone_num_err, Toast.LENGTH_SHORT).show();
                } else if (StringUtils.isEmpty(password) || password.length() < 6) {
                    Toast.makeText(getActivity(), R.string.password_length_err, Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.checkPassWord(password)) {
                    Toast.makeText(getActivity(), R.string.password_err, Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isSmsCode(verifycode)) {
                    Toast.makeText(getActivity(), R.string.verifycode_err, Toast.LENGTH_SHORT).show();
                } else {
                    doRegister(mobile, password, verifycode, null);
                }
            }
            break;
            case R.id.tv_user_clause:
                String url = AppConfig.USER_CLAUSE;
                WebViewDialog dialog = new WebViewDialog(getActivity(), url);
                dialog.show();
                break;
            case R.id.btn_back:
                mOnArticleKidsMind.skipToFragment(LoginIvmallFragment.TAG, null);
                break;
            default:
                break;
        }

    }


    public void isMobileRegistered(String mobile) {

        String url = AppConfig.MOBILE_IS_REGISTERED;
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

                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
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
                    String token = resp.getData().getToken();
                    String userName = resp.getData().getUsername();
                    String mobileNum = resp.getData().getMobile();
                    String pwd = resp.getData().getPassword();

                    ((MNJApplication) getActivity().getApplication()).setToken(token);
                    ((MNJApplication) getActivity().getApplication()).setUserName(userName);
                    ((MNJApplication) getActivity().getApplication()).setMoblieNum(mobileNum);
                    ((MNJApplication) getActivity().getApplication()).setPassWord(pwd);

                    mOnArticleKidsMind.skipToFragment(AutoLoginFragment.TAG, null);
                    Toast.makeText(getActivity(), R.string.register_sucess, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


}