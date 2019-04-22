package com.taku.safe.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.AccountInfoActivity;
import com.taku.safe.BuildConfig;
import com.taku.safe.R;
import com.taku.safe.activity.UserProtoActivity;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespRegister;
import com.taku.safe.receiver.TouCoolReceiver;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.DeviceUtils;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.StringUtils;

import java.lang.ref.WeakReference;


public class RegisterFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = RegisterFragment.class.getSimpleName();

    private static final int HANDLER_COUNT_SECOND = 0;

    private static final int DATA_MAX_TIME = 60; //最大时间 1min

    private int curTime;

    private SmsReceiver mSmsReceiver;
    private UIHandler mHandler;

    private EditText et_phone;
    private EditText et_password;
    private EditText et_sms_code;

    private Button btnSmsCode;
    private CheckBox cb_agree;

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String phoneNum = s.toString();
            Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.drawable.ic_account_selector);
            Drawable drawableCorrect = ContextCompat.getDrawable(mContext, R.mipmap.ic_correct);
            if (AppUtils.isMobileNum(phoneNum)) {
                et_phone.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableCorrect, null);
            } else {
                et_phone.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
        mSmsReceiver = new SmsReceiver();
        curTime = DATA_MAX_TIME;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
        initData();
    }


    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(TouCoolReceiver.SMS_RECEIVED_ACTION);
        getActivity().registerReceiver(mSmsReceiver, filter);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mSmsReceiver != null) {
            getActivity().unregisterReceiver(mSmsReceiver);
        }

    }


    private void initTitle(View view) {
        view.findViewById(R.id.content_title)
                .setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

        TextView tv_back = (TextView) view.findViewById(R.id.tv_back);
        tv_back.setVisibility(View.GONE);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_title));
        tv_title.setText(R.string.register);

        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_confirm.setBackgroundResource(R.drawable.bg_white_selector);
        Drawable drawableCorrect = ContextCompat.getDrawable(mContext, R.mipmap.ic_close_black);
        tv_confirm.setCompoundDrawablesWithIntrinsicBounds(drawableCorrect, null, null, null);
        tv_confirm.setOnClickListener(this);
    }


    private void initView(View view) {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_phone.addTextChangedListener(mTextWatcher);

        et_password = (EditText) view.findViewById(R.id.et_password);
        et_sms_code = (EditText) view.findViewById(R.id.et_sms_code);

        btnSmsCode = (Button) view.findViewById(R.id.btn_sms_code);
        btnSmsCode.setOnClickListener(this);

        cb_agree = (CheckBox) view.findViewById(R.id.cb_agree);


        view.findViewById(R.id.btn_commit).setOnClickListener(this);
        view.findViewById(R.id.tv_proto).setOnClickListener(this);
    }


    private void initData() {
        String phoneNum = DeviceUtils.getPhoneNumber(getContext());
        if (!TextUtils.isEmpty(phoneNum)) {
            et_phone.setText(phoneNum);
        }
    }


    /**
     * 初始化验证
     */
    private void reqSmsCode(String mobile) {
        btnSmsCode.setEnabled(false);  //获取验证码不可点击
        et_phone.setEnabled(false);

        //字体变
        mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_SECOND, 1000);

        String url = AppConfig.GET_SMS_CODE;
        ContentValues params = new ContentValues();
        params.put("mobile", mobile);


        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                    if (baseBean != null && baseBean.isSuccess()) {
                        String msg = baseBean.getMsg();
                        if (!TextUtils.isEmpty(msg) && BuildConfig.DEBUG) {
                            et_sms_code.setText(msg);
                        }
                    } else {
                        if (baseBean != null) {
                            Toast.makeText(getContext(), baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
    }

    /**
     * 用户注册
     *
     * @param mobile   手机号
     * @param password 密码
     */
    private void register(final String mobile, String password, String smscode) {
        String url = AppConfig.STUD_REGISTER;

        String imei = DeviceUtils.getIMEI(getContext());

        ContentValues params = new ContentValues();
        params.put("mobile", mobile);
        params.put("password", password);
        params.put("smscode", smscode);
        params.put("imei", imei);

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在注册，请稍后...");
        dialog.show();

        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    RespRegister bean = GsonUtil.parse(response, RespRegister.class);
                    if (bean != null) {
                        if (bean.isSuccess()) {
                            mTakuApp.setToken(bean.getToken());
                            mTakuApp.setTs(bean.getTs());
                            mTakuApp.setExpire(bean.getExpire());
                            mTakuApp.setPhoneNum(mobile);

                            //只有学生才需要注册，新注册账号没有进行过绑定
                            if (bean.isBindStatus()) {
                                mTakuApp.setBind(true);
                                mTakuApp.reqUserInfo(null);
                                getActivity().setResult(Activity.RESULT_OK);
                                getActivity().finish();
                            } else {
                                ((AccountInfoActivity) getActivity()).skipToFragment(BindFragment.TAG, null);
                            }

                        } else {
                            Toast.makeText(getContext(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }
            }
        });
    }


    /**
     * 处理验证.
     */
    private void checkSmsCode() {
        curTime--;
        if (curTime <= 0) {
            curTime = DATA_MAX_TIME;

            btnSmsCode.setEnabled(true);
            btnSmsCode.setText(R.string.req_sms_code);
            et_phone.setEnabled(true);

        } else {
            btnSmsCode.setText(getString(R.string.sms_code_second, curTime));
            mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_SECOND, 1000);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHandler.hasMessages(HANDLER_COUNT_SECOND)) {
            mHandler.removeMessages(HANDLER_COUNT_SECOND);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_confirm:
                ((AccountInfoActivity) getActivity()).skipToFragment(LoginFragment.TAG, null);
                break;
            case R.id.btn_sms_code:
                String phoneNum = et_phone.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getContext(), "请填写手机号码", Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isMobileNum(phoneNum)) {
                    Toast.makeText(getContext(), "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                } else {
                    reqSmsCode(phoneNum);
                }


                break;
            case R.id.btn_commit:
                String mobile = et_phone.getText().toString();
                String password = et_password.getText().toString();
                String smsCode = et_sms_code.getText().toString();


                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(getContext(), "请填写手机号码", Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isMobileNum(mobile)) {
                    Toast.makeText(getContext(), "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isPassWord(password)) {
                    Toast.makeText(getContext(), "密码必须为6-20个字母或数字组成", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(smsCode)) {
                    Toast.makeText(getContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                } else if (!cb_agree.isChecked()) {
                    Toast.makeText(getContext(), "请同意塔库用户许可协议", Toast.LENGTH_SHORT).show();
                } else {
                    register(mobile, password, smsCode);
                }

                break;
            case R.id.tv_proto:
                startActivity(new Intent(mContext, UserProtoActivity.class));
                break;
        }

    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<RegisterFragment> mTarget;

        UIHandler(RegisterFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            RegisterFragment fragment = mTarget.get();
            switch (msg.what) {
                case HANDLER_COUNT_SECOND:
                    fragment.checkSmsCode();
                    break;
                default:
                    break;
            }
        }
    }


    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String valid = intent.getStringExtra(TouCoolReceiver.SMS_CODE);
            if (!StringUtils.isEmpty(valid)) {
                et_sms_code.setText(valid);
            }
        }
    }

}
