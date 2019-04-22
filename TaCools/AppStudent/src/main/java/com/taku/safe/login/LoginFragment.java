package com.taku.safe.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.AccountInfoActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespLogin;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.DeviceUtils;
import com.taku.safe.utils.GsonUtil;


public class LoginFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = LoginFragment.class.getSimpleName();

    private EditText et_phone;
    private EditText et_password;

    private TextView tv_pw_error;

    private TextView tv_register;
    private String mobile;


    TextWatcher mAccoutWatcher = new TextWatcher() {

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


    TextWatcher mPassWordWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Drawable drawablePw = ContextCompat.getDrawable(mContext, R.drawable.ic_pw_selector);
            et_password.setCompoundDrawablesWithIntrinsicBounds(drawablePw, null, null, null);

            int color = ContextCompat.getColor(mContext, R.color.colorAccent);
            et_password.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            tv_pw_error.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            mobile = bundle.getString("mobile");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initTitle(View view) {
        view.findViewById(R.id.content_title)
                .setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));

        TextView tv_back = (TextView) view.findViewById(R.id.tv_back);
        tv_back.setVisibility(View.GONE);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_title));
        tv_title.setText(R.string.login);

        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_confirm.setBackgroundResource(R.drawable.bg_white_selector);
        Drawable drawableCorrect = ContextCompat.getDrawable(mContext, R.mipmap.ic_close_black);

        tv_confirm.setCompoundDrawablesWithIntrinsicBounds(drawableCorrect, null, null, null);
        tv_confirm.setOnClickListener(this);

    }

    private void initView(View view) {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_phone.addTextChangedListener(mAccoutWatcher);
        et_phone.requestFocus();

        String phone = mTakuApp.getPhoneNum();

        if (!TextUtils.isEmpty(mobile)) {
            et_phone.setText(mobile);
        } else if (!TextUtils.isEmpty(phone)) {
            et_phone.setText(phone);
        }

        et_password = (EditText) view.findViewById(R.id.et_password);
        et_password.addTextChangedListener(mPassWordWatcher);

        tv_pw_error = (TextView) view.findViewById(R.id.tv_pw_error);

        tv_register = (TextView) view.findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);

        view.findViewById(R.id.tv_forget).setOnClickListener(this);
        view.findViewById(R.id.btn_login).setOnClickListener(this);

    }


    private void pwError(String error) {
        Drawable drawableError = ContextCompat.getDrawable(mContext, R.mipmap.ic_warning);
        Drawable drawablePw = ContextCompat.getDrawable(mContext, R.drawable.ic_pw_selector);
        et_password.setCompoundDrawablesWithIntrinsicBounds(drawablePw, null, drawableError, null);

        int color = ContextCompat.getColor(mContext, R.color.color_red);
        et_password.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        tv_pw_error.setText(error);
        tv_pw_error.setVisibility(View.VISIBLE);
    }


    /**
     * 用户登录
     *
     * @param mobile   手机号
     * @param password 密码
     * @param imei     设备编码
     */
    private void login(final String mobile,
                       final String password, final String imei, final String phoneMac) {
        String url = AppConfig.STUD_LOGIN;

        ContentValues params = new ContentValues();
        params.put("mobile", mobile);
        params.put("password", password);
        params.put("imei", imei);
        params.put("phoneMac", phoneMac);

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
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    RespLogin respLogin = GsonUtil.parse(response, RespLogin.class);
                    if (respLogin != null && respLogin.isSuccess()) {
                        mTakuApp.setToken(respLogin.getToken());
                        mTakuApp.setTs(respLogin.getTs());
                        mTakuApp.setExpire(respLogin.getExpire());
                        mTakuApp.setPhoneNum(mobile);
                        mTakuApp.setBind(respLogin.isBind());

                        if (respLogin.isBind()) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } else {
                            ((AccountInfoActivity) getActivity()).skipToFragment(BindFragment.TAG, null);
                        }

                    } else if (respLogin != null && respLogin.isReBindDevice()) {
                        choiceReBindDialog(mobile);
                    } else {
                        if (respLogin != null)
                            pwError(respLogin.getMsg());
                    }
                    dialog.dismiss();
                }

            }
        });
    }


    private void choiceReBindDialog(final String mobile) {
        String format = getString(R.string.change_device_msg);
        new AlertDialog.Builder(getContext())
                .setMessage(String.format(format, mobile))
                .setPositiveButton(R.string.change_device_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = new Bundle();
                                bundle.putString("mobile", mobile);
                                ((AccountInfoActivity) getActivity()).skipToFragment(ChangeDeviceFragment.TAG, bundle);
                            }
                        })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_forget: {
                String phone = et_phone.getText().toString();  //"18664923439"
                Bundle bundle = new Bundle();
                bundle.putString("mobile", phone);

                ((AccountInfoActivity) getActivity()).skipToFragment(ForgetFragment.TAG, bundle);
            }
            break;
            case R.id.tv_register:
                ((AccountInfoActivity) getActivity()).skipToFragment(RegisterFragment.TAG, null);
                break;
            case R.id.btn_login: {
                String phone = et_phone.getText().toString();  //"18664923439"
                String password = et_password.getText().toString();  //"123456"
                String imei = DeviceUtils.getIMEI(getContext());
                String phoneMac = DeviceUtils.getMacAddress(getContext());

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "请填写手机号码", Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isMobileNum(phone)) {
                    Toast.makeText(getContext(), "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                } else {
                    login(phone, password, imei, phoneMac);
                }
            }
            break;
            case R.id.tv_confirm:
                //((AccountInfoActivity) getActivity()).skipToFragment(ChoiceFragment.TAG, null);
                getActivity().finish();
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //thirdLogin.onActivityResult(requestCode, resultCode, data); //第三方登录
    }


}
