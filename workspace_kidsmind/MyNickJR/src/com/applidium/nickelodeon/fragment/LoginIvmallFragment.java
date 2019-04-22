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
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.LoginIvmallRequest;
import com.applidium.nickelodeon.entity.LoginResponse;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;


public class LoginIvmallFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = LoginIvmallFragment.class.getSimpleName();

    public static final String MOBILE_NUM = "mobile_num";
    public static final String USER_NAME = "user_name";


    public OnArticleSelectedListener mOnArticleKidsMind;
    private Activity mAct;

    private EditText etPhoneNumber;
    private EditText etPassword;

    private Button btnLogin;
    private Button btnRegister;
    private Button btnForget;

    private ImageButton btnBack;
    private TextView tvDefaultLogin;

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
        return inflater.inflate(R.layout.login_activity, container, false);
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

        etPassword = (EditText) v.findViewById(R.id.et_password);

        btnLogin = (Button) v.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        btnRegister = (Button) v.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);

        btnForget = (Button) v.findViewById(R.id.btn_forget);
        btnForget.setOnClickListener(this);

        btnBack = (ImageButton) v.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        tvDefaultLogin = (TextView) v.findViewById(R.id.tv_default_login);
        tvDefaultLogin.setOnClickListener(this);

    }


    private void initData(Bundle bundle) {

        String mobileNum = bundle.getString(MOBILE_NUM);
        String useName = bundle.getString(USER_NAME);
        if (!StringUtils.isEmpty(mobileNum) && AppUtils.isMobileNum(mobileNum)) {
            etPhoneNumber.setText(mobileNum);
        } else {
            etPhoneNumber.setText(useName);
        }
    }


    @Override
    public void onClick(View v) {

        MediaPlayerService.playSound(mAct, MediaPlayerService.ONCLICK);
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                String mobile = etPhoneNumber.getText().toString();
                String password = etPassword.getText().toString();


                if (StringUtils.isEmpty(mobile)) {
                    Toast.makeText(mAct, R.string.phone_num_empty, Toast.LENGTH_SHORT).show();
                }
                /*else if (!AppUtils.isMobileNum(mobile)) {
                    Toast.makeText(mAct, R.string.phone_num_err, Toast.LENGTH_SHORT).show();
                } */
                else if (!AppUtils.checkPassWord(password)) {
                    Toast.makeText(mAct, R.string.password_err, Toast.LENGTH_SHORT).show();
                } else {
                    loginIvmall(mobile, password);
                }
                break;
            case R.id.btn_register:
                mOnArticleKidsMind.skipToFragment(RegisterFragment.TAG, null);
                break;
            case R.id.btn_forget:
                Bundle bundle = new Bundle();
                String mobileNum = etPhoneNumber.getText().toString();
                bundle.putString(LoginIvmallFragment.MOBILE_NUM, mobileNum);

                mOnArticleKidsMind.skipToFragment(ForgetPasswordFragment.TAG, bundle);
                break;
            case R.id.tv_default_login:
                mOnArticleKidsMind.skipToFragment(AutoLoginFragment.TAG, null);
                break;
            case R.id.btn_back:
                mAct.finish();
                break;
            default:
                break;
        }


    }


    /**
     * 账号密码登录ivmall
     *
     * @param nameStr
     * @param pwStr
     */
    private void loginIvmall(final String nameStr, final String pwStr) {
        String url = AppConfig.USER_LOGIN_IVMALL;
        LoginIvmallRequest request = new LoginIvmallRequest();
        request.setMobile(nameStr);
        request.setPassword(pwStr);
        request.setType(nameStr);

        AppUtils.showLoadingDialog(mAct);
        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                LoginResponse resp = GsonUtil.parse(response,
                        LoginResponse.class);
                if (resp != null) {
                    if (resp.isSucess()) {
                        String userId = resp.getData().getAccountId();
                        String token = resp.getData().getToken();
                        String userName = resp.getData().getUsername();
                        String vipExpiresTime = resp.getData().getVipExpiryTime();
                        String firstMtime = resp.getData().getFirstModifiedTime();

                        ((MNJApplication) getActivity().getApplication()).setUserId(userId);
                        ((MNJApplication) getActivity().getApplication()).setToken(token);
                        ((MNJApplication) getActivity().getApplication()).setUserName(userName);
                        ((MNJApplication) getActivity().getApplication()).setVipExpiresTime(vipExpiresTime);
                        ((MNJApplication) getActivity().getApplication()).setFirstModifiedTime(firstMtime);
                        ((MNJApplication) getActivity().getApplication()).setPassWord(pwStr);

                        if (AppUtils.isMobileNum(nameStr)) {
                            ((MNJApplication) getActivity().getApplication()).setMoblieNum(nameStr);
                        }
                        ((MNJApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());

                    } else {
                        Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    AppUtils.hideLoadingDialog();
                }
            }

        });

    }

    private class OnCreateProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            //登录法国服务器
            /*String userId = ((MNJApplication) mAct.getApplication()).getUserId();
            String token = ((MNJApplication) mAct.getApplication()).getToken();
            ((MNJApplication) mAct.getApplication()).loginMnj(userId, token);*/

            ((MNJApplication) mAct.getApplication()).reqUserInfo(); //请求用户信息
            mOnArticleKidsMind.skipToFragment(MetroFragment.TAG, null);
        }

        @Override
        public void create() {
            mOnArticleKidsMind.skipToFragment(KidsInfoFragment.TAG, null);// profile
        }

        @Override
        public void fail() {
            ((MNJApplication) mAct.getApplication()).clearToken();
            mOnArticleKidsMind.skipToFragment(LoginIvmallFragment.TAG, null);
        }
    }

}