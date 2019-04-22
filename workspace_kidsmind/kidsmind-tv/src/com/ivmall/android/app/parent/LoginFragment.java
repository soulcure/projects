package com.ivmall.android.app.parent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.ParentFragmentActivity;
import com.ivmall.android.app.uitls.LoginUtils;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.MobileBindRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.entity.ProtocolResponse;
import com.ivmall.android.app.entity.SmsCodeRequest;
import com.ivmall.android.app.entity.UserInfo;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.uitls.ZXingUtil;
import com.ivmall.android.app.views.CountDownClock;

import java.lang.ref.WeakReference;


public class LoginFragment extends Fragment implements OnClickListener {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private static final int REQ_SERVER_RESULT = 1;

    private Activity mAct;

    private LoginHandler mHandler;

    private LinearLayout linearLogin;
    private TextView tvLoginInfo;

    private ImageView qrcodeLogin;
    private ProgressDialog mProgressDialog;

    private EditText etPhoneNumber;
    private EditText etSmsCode;

    private Button btnLogin;
    private CountDownClock tvCountDown;

    private SmsCodeRequest.SmsParm smsParm;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new LoginHandler(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.login_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        linearLogin = (LinearLayout) view.findViewById(R.id.linear_login);
        tvLoginInfo = (TextView) view.findViewById(R.id.tvLoginInfo);
        tvLoginInfo.setText(((KidsMindApplication) mAct.getApplication())
                .getAppConfig("afterLoginPrompt"));

        qrcodeLogin = (ImageView) view.findViewById(R.id.img_qrcode_login);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
        etPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    BaiduUtils.onEvent(mAct, OnEventId.LOGIN_MOBIM, getString(R.string.login_mobim));
            }
        });


        etSmsCode = (EditText) view.findViewById(R.id.et_sms_code);
        etSmsCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    BaiduUtils.onEvent(mAct, OnEventId.LOGIN_PASSWORD, getString(R.string.login_password));
            }
        });


        btnLogin = (Button) view.findViewById(R.id.tv_login);
        btnLogin.setOnClickListener(this);

        tvCountDown = (CountDownClock) view.findViewById(R.id.tv_sms_code);
        tvCountDown.setOnClickListener(this);
        tvCountDown.setTextInfo(mAct.getString(R.string.get_password));

        String mobleNum = ((KidsMindApplication) mAct.getApplication())
                .getMoblieNum();

        if (!StringUtils.isEmpty(mobleNum)) {
            etPhoneNumber.setText(mobleNum);
        }


        KidsMindApplication.LoginType type = ((KidsMindApplication) mAct.getApplication())
                .getLoginType();


        if (type == KidsMindApplication.LoginType.mobileLogin
                && !StringUtils.isEmpty(mobleNum)) {
            showIsLoginedView(mobleNum);
        } else {
            etPhoneNumber.requestFocus();
        }


        // 设置二维码扫码登录
        String url = ((KidsMindApplication) mAct.getApplication())
                .getAppConfig("webControlURL");
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        String deviceDRMId = AppConfig.getDeviceDRMId(mAct);
        StringBuilder sb = new StringBuilder();
        sb.append(url).append('?');
        sb.append('&').append("token").append('=').append(token);
        sb.append('&').append("deviceDRMId").append('=').append(deviceDRMId);
        initQrcodeLogin(sb.toString());
    }


    @Override
    public void onResume() {
        super.onResume();
        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }


    private void showIsLoginingView() {
        ((ParentFragmentActivity) mAct).setLoginText(false);
        linearLogin.setVisibility(View.VISIBLE);
        tvLoginInfo.setText(((KidsMindApplication) mAct.getApplication())
                .getAppConfig("afterLoginPrompt"));
        btnLogin.setText(R.string.login_in);
    }

    private void showIsLoginedView(String mobleNum) {
        ((ParentFragmentActivity) mAct).setLoginText(true);
        linearLogin.setVisibility(View.GONE);
        String text = getString(R.string.login_out_info) + mobleNum;

        int color = mAct.getResources().getColor(R.color.yellow);
        int start = getString(R.string.login_out_info).length();
        int end = text.length();
        CharSequence cs = AppUtils.setHighLightText(text, color, start, end);
        tvLoginInfo.setText(cs);
        etPhoneNumber.setText(mobleNum);
        btnLogin.setText(R.string.login_out);
    }


    /**
     * 1.2 手机号登录
     *
     * @param phoneNum
     * @param smsCode
     */
    private void mobileLogin(final String phoneNum, String smsCode) {
        LoginUtils loginUtils = new LoginUtils(mAct);

        loginUtils.setLoginInSuccess(new LoginUtils.LoginInListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mAct, "登录成功", Toast.LENGTH_SHORT).show();
                etSmsCode.setText("");
                showIsLoginedView(phoneNum);
                ((KidsMindApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailed(String message) {
                mProgressDialog.dismiss();
                Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
            }
        });
        loginUtils.mobileLogin(phoneNum, smsCode);
    }

    /**
     * 1.3 手机号是否已注册
     *
     * @param phoneNum
     */
    @Deprecated
    private void mobileRegistered(String phoneNum) {
        {
            String url = AppConfig.MOBILE_NUM_REGISTERED;
            SmsCodeRequest request = new SmsCodeRequest();
            request.setMobile(phoneNum);

            String json = request.toJsonString();
            HttpConnector.httpPost(url, json, new IPostListener() {

                @Override
                public void httpReqResult(String response) {
                    // TODO Auto-generated method stub
                    ProtocolResponse resp = GsonUtil.parse(response,
                            ProtocolResponse.class);
                    String num = etPhoneNumber.getText().toString();
                    if (resp.isSucess()) {
                        smsParm = SmsCodeRequest.SmsParm.bind;
                    } else {
                        smsParm = SmsCodeRequest.SmsParm.login;
                    }
                    reqSmsCode(num, smsParm);
                }

            });

        }
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
                    String smsCountdown = ((KidsMindApplication) mAct.getApplication())
                            .getAppConfig("smsCountdown");
                    tvCountDown.setCountMillSecond(Integer.parseInt(smsCountdown) * 1000);
                    tvCountDown.start();
                    etSmsCode.requestFocus();
                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
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

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    Toast.makeText(mAct, "登录成功", Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();
                    etSmsCode.setText("");

                    showIsLoginedView(phoneNum);

                    //String token = resp.getToken();  //绑定成功不返回数据

                    String token = ((KidsMindApplication) mAct.getApplication()).getToken();
                    ((KidsMindApplication) mAct.getApplication()).setToken(token);

                    ((KidsMindApplication) mAct.getApplication()).setMoblieNum(phoneNum);

                    ((KidsMindApplication) mAct.getApplication()).reqUserInfo();//刷新用户VIP信息
                    ((KidsMindApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());

                    MainFragmentActivity.openApp(mAct);
                } else {
                    //绑定不成功，做登录处理
                    mobileLogin(phoneNum, smsCode);
                }


            }

        });

    }


    private class OnCreateProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            ((ParentFragmentActivity) mAct).goToBuyVip();
        }

        @Override
        public void create() {

        }

        @Override
        public void fail() {

        }
    }


    /**
     * 1.6 注销登录
     */
    private void loginOut() {
        LoginUtils loginUtils = new LoginUtils(mAct);

        loginUtils.setLoginOutSuccess(new LoginUtils.LoginOutListener() {
            @Override
            public void onSuccess() {
                showIsLoginingView();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
            }
        });
        loginUtils.loginOut();
    }

    /**
     * 设置二维码扫码登录
     *
     * @param url
     */
    private void initQrcodeLogin(String url) {
        try {
            Bitmap nestImage = BitmapFactory.decodeResource(getResources(),
                    R.drawable.app_icon);

            int size = (int) ScreenUtils.dpToPx(mAct, 200);

            Bitmap bitmap = ZXingUtil.encode(url, size, size, nestImage);

            qrcodeLogin.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询二维码登录结果
     *
     * @param url
     */
    private void queryLoginResult(final String url) {
        MobileBindRequest request = new MobileBindRequest();
        // 入参
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();

        request.setToken(token);

        HttpConnector.httpPost(url, request.toJsonString(),
                new IPostListener() {
                    @Override
                    public void httpReqResult(String response) {
                        PayResponse payResponse = GsonUtil.parse(response,
                                PayResponse.class);

                        if (payResponse.isSuccess()) {
                            if (payResponse.isTradeResult()) {
                                Toast.makeText(mAct, "登录成功", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Message msg = mHandler.obtainMessage();
                                msg.obj = url;
                                msg.what = REQ_SERVER_RESULT;
                                mHandler.sendMessageDelayed(msg, 5000);
                            }

                        }

                    }

                });
    }


    @Override
    public void onClick(View v) {

        KidsMindApplication.LoginType type = ((KidsMindApplication) mAct.getApplication())
                .getLoginType();

        boolean isLogin = type == KidsMindApplication.LoginType.mobileLogin ? true : false;

        int id = v.getId();
        if (id == R.id.tv_sms_code) {
            String num = etPhoneNumber.getText().toString();
            if (StringUtils.isEmpty(num)) {
                Toast.makeText(mAct, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AppUtils.isMobileNum(num)) {
                Toast.makeText(mAct, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }


            if (isLogin) {
                smsParm = SmsCodeRequest.SmsParm.login;
            } else {
                smsParm = SmsCodeRequest.SmsParm.bind;
            }

            reqSmsCode(num, smsParm);

            BaiduUtils.onEvent(mAct, OnEventId.LOGIN_GET_PASSWORD,
                    getString(R.string.login_get_password));
        } else if (id == R.id.tv_login) {

            if (isLogin) {
                loginOut(); //注销登录
                BaiduUtils.onEvent(mAct, OnEventId.CLICK_LOGOUT,
                        getString(R.string.click_logout));
            } else { //登录绑定
                String num = etPhoneNumber.getText().toString();
                String smsCode = etSmsCode.getText().toString();
                if (StringUtils.isEmpty(smsCode)) {
                    Toast.makeText(mAct, "动态密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AppUtils.isSmsCode(smsCode)) {
                    Toast.makeText(mAct, "请输入正确的动态密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressDialog = AppUtils.showProgress(mAct, null,
                        "正在为您登录", false, true);
                mobileBind(num, smsCode);
                BaiduUtils.onEvent(mAct, OnEventId.CLICK_LOGIN,
                        getString(R.string.click_login));
            }
        }

    }

    private class LoginHandler extends Handler {
        private final WeakReference<LoginFragment> mTarget;

        LoginHandler(LoginFragment target) {
            mTarget = new WeakReference<LoginFragment>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQ_SERVER_RESULT:
                    String url = msg.obj.toString();
                    queryLoginResult(url);
                    break;
                default:
                    break;
            }
        }
    }

}
