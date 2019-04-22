package com.taku.safe.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BuildConfig;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespLogin;
import com.taku.safe.receiver.TouCoolReceiver;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.StringUtils;

import java.lang.ref.WeakReference;


/**
 * Created by Administrator on 2017/10/17.
 * 验证码对话框
 */

public class SmsCodeDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = SmsCodeDialog.class.getSimpleName();

    private static final int HANDLER_COUNT_SECOND = 0;
    private static final int DATA_MAX_TIME = 60; //最大时间 1min
    private int curTime;

    private TakuApp mTakuApp;
    private Context mContext;

    private EditText et_sms_code;
    private Button btnSmsCode;

    private String phoneNum;
    private String password;
    private String imei;
    private String phoneMac;

    private OnResult onResult;

    private TextView tv_confirm;
    private TextView tv_cancel;
    private TextView tv_pw_error;

    private SmsReceiver mSmsReceiver;
    private UIHandler mHandler;

    public interface OnResult {
        void success();
    }


    public static class Builder {
        private Context context;
        private String phoneNum;
        private String password;
        private String imei;
        private String phoneMac;
        private OnResult onResult;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder phoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder imei(String imei) {
            this.imei = imei;
            return this;
        }

        public Builder phoneMac(String phoneMac) {
            this.phoneMac = phoneMac;
            return this;
        }

        public Builder onResult(OnResult onResult) {
            this.onResult = onResult;
            return this;
        }

        public SmsCodeDialog builder() {
            return new SmsCodeDialog(this);
        }
    }

    private SmsCodeDialog(Builder builder) {
        super(builder.context, R.style.custom_dialog);
        mContext = builder.context;
        mTakuApp = (TakuApp) builder.context.getApplicationContext();

        phoneNum = builder.phoneNum;
        password = builder.password;
        imei = builder.imei;
        phoneMac = builder.phoneMac;

        onResult = builder.onResult;

        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sms_code);
        curTime = DATA_MAX_TIME;

        mHandler = new UIHandler(this);

        mSmsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TouCoolReceiver.SMS_RECEIVED_ACTION);
        mContext.registerReceiver(mSmsReceiver, filter);

        setDialogFeature();
        initView();
        setListener();
    }


    @Override
    public void dismiss() {
        if (mSmsReceiver != null) {
            mContext.unregisterReceiver(mSmsReceiver);
        }
        if (mHandler.hasMessages(HANDLER_COUNT_SECOND)) {
            mHandler.removeMessages(HANDLER_COUNT_SECOND);
        }
        super.dismiss();
    }

    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }


    private void initView() {
        et_sms_code = (EditText) findViewById(R.id.et_sms_code);
        btnSmsCode = (Button) findViewById(R.id.btn_sms_code);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_pw_error = (TextView) findViewById(R.id.tv_pw_error);
    }


    private void setListener() {
        btnSmsCode.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }


    /**
     * 初始化验证
     */
    private void reqSmsCode(String mobile) {
        btnSmsCode.setEnabled(false);  //获取验证码不可点击

        //字体变
        mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_SECOND, 1000);

        String url = AppConfig.GET_SMS_CODE;
        ContentValues params = new ContentValues();
        params.put("mobile", mobile);


        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isShowing()) { //网络回调需刷新UI,添加此判断
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
     * 处理验证.
     */
    private void checkSmsCode() {
        curTime--;
        if (curTime <= 0) {
            curTime = DATA_MAX_TIME;

            btnSmsCode.setEnabled(true);
            btnSmsCode.setText(R.string.req_sms_code);

        } else {
            btnSmsCode.setText(mContext.getString(R.string.sms_code_second, curTime));
            mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_SECOND, 1000);
        }
    }


    /**
     * 用户登录
     *
     * @param smsCode 验证码
     */
    private void loginBySmsCode(String smsCode) {
        String url = AppConfig.TEACHER_LOGIN;

        ContentValues params = new ContentValues();
        params.put("mobile", phoneNum);
        params.put("password", password);
        params.put("imei", imei);
        params.put("phoneMac", phoneMac);
        params.put("smscode", smsCode);

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }


        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在登录，请稍后...");
        dialog.show();

        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isShowing()) { //网络回调需刷新UI,添加此判断
                    RespLogin respLogin = GsonUtil.parse(response, RespLogin.class);
                    if (respLogin != null && respLogin.isSuccess()) {
                        mTakuApp.setToken(respLogin.getToken());
                        mTakuApp.setTs(respLogin.getTs());
                        mTakuApp.setExpire(respLogin.getExpire());
                        mTakuApp.setPhoneNum(phoneNum);
                        mTakuApp.setBind(respLogin.isBind());

                        if (onResult != null) {
                            onResult.success();
                        }
                        dismiss();

                    } else {
                        if (respLogin != null)
                            pwError(respLogin.getMsg());
                    }
                    dialog.dismiss();
                }

            }
        });
    }


    private void pwError(String error) {
        tv_pw_error.setText(error);
        tv_pw_error.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_sms_code:
                tv_pw_error.setVisibility(View.INVISIBLE);
                reqSmsCode(phoneNum);
                break;
            case R.id.tv_confirm:
                tv_pw_error.setVisibility(View.INVISIBLE);
                String smsCode = et_sms_code.getText().toString();
                if (TextUtils.isEmpty(smsCode)) {
                    Toast.makeText(getContext(), "请填写验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginBySmsCode(smsCode);
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<SmsCodeDialog> mTarget;

        UIHandler(SmsCodeDialog target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsCodeDialog dialog = mTarget.get();
            switch (msg.what) {
                case HANDLER_COUNT_SECOND:
                    dialog.checkSmsCode();
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

                tv_confirm.setEnabled(false);
                tv_cancel.setEnabled(false);

                loginBySmsCode(valid);
            }
        }
    }


}
