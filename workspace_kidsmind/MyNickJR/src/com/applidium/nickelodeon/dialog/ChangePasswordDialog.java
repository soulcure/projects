package com.applidium.nickelodeon.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.ChangePasswordRequest;
import com.applidium.nickelodeon.entity.LoginIvmallRequest;
import com.applidium.nickelodeon.entity.LoginResponse;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;

/**
 *
 */
public class ChangePasswordDialog extends Dialog implements OnClickListener {

    private Context mContext;

    private MNJApplication application;
    private EditText etoldpwd;
    private EditText etnewpwd;
    private EditText etnewpwdcf;
    private Button btnconfirm;
    private boolean isBindPhone = false;
    private TextView textTitle;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */

    public ChangePasswordDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
    }

    public ChangePasswordDialog(Context context, boolean isBindPhone) {
        super(context, R.style.full_dialog);
        mContext = context;
        this.isBindPhone = isBindPhone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pw);

        initView();
    }

    private void initView() {

        this.btnconfirm = (Button) findViewById(R.id.btn_confirm);
        this.etnewpwdcf = (EditText) findViewById(R.id.et_new_pwd_cf);
        this.etnewpwd = (EditText) findViewById(R.id.et_new_pwd);
        this.etoldpwd = (EditText) findViewById(R.id.et_old_pwd);
        this.textTitle = (TextView) findViewById(R.id.text_title);
        btnconfirm.setOnClickListener(this);

        application = (MNJApplication) mContext.getApplicationContext();

        if (isBindPhone) {
            textTitle.setText("为了安全，建议您修改原始密码（初始密码000000）");
            etoldpwd.setText("000000");
            etoldpwd.setEnabled(false);
        }

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && isBindPhone) {
                    return true;
                } else {
                    return false; //默认返回 false
                }
            }
        });
    }

    /**
     * 监听修改成功
     */
    private ChangeUsernameDialog.OnChangeSuccessListener listener;

    public void setChangeSuccessListener(ChangeUsernameDialog.OnChangeSuccessListener listener) {
        this.listener = listener;
    }

    private void changePassword(String old_pw, final String new_pw) {
        String url = AppConfig.UPDATE_PASSWORD;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setToken(application.getToken());
        request.setOldPassword(old_pw);
        request.setNewPassword(new_pw);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    application.setPassWord(new_pw);
                    //绑定手机号之后修改密码
                    if (isBindPhone) {
                        loginIvmall();
                    } else {
                        excuteOnchange();
                    }

                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }

            }

        });

    }

    /**
     * 账号密码登录ivmall
     */
    private void loginIvmall() {
        String url = AppConfig.USER_LOGIN_IVMALL;
        LoginIvmallRequest request = new LoginIvmallRequest();
        request.setMobile(application.getMoblieNum());
        request.setPassword(application.getPassWord());
        request.setType(application.getMoblieNum());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                LoginResponse resp = GsonUtil.parse(response,
                        LoginResponse.class);
                if (resp.isSucess()) {
                    String token = resp.getData().getToken();
                    String userName = resp.getData().getUsername();
                    String vipExpiresTime = resp.getData().getVipExpiryTime();
                    String firstMtime = resp.getData().getFirstModifiedTime();

                    application.setToken(token);
                    application.setUserName(userName);
                    application.setVipExpiresTime(vipExpiresTime);
                    application.setFirstModifiedTime(firstMtime);
                    //设置登陆方式为用户用户名登陆
                    application.setLoginType(false);
                    excuteOnchange();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private void excuteOnchange() {
        if (null != listener) {
            listener.onSuccess();
        }
        dismiss();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();

        String old_pwd = etoldpwd.getText().toString();
        String new_pwd = etnewpwd.getText().toString().trim();
        String new_pwd_cf = etnewpwdcf.getText().toString().trim();
        if (id == R.id.btn_confirm) {
            if (StringUtils.isEmpty(old_pwd)) {
                Toast.makeText(getContext(), "原密码不能为空", Toast.LENGTH_SHORT)
                        .show();
                return;
            } else if (StringUtils.isEmpty(new_pwd)) {
                Toast.makeText(getContext(), "请输入新密码", Toast.LENGTH_SHORT)
                        .show();
                return;
            } else if (new_pwd.length() < 6) {
                Toast.makeText(getContext(), "密码长度最少为6位", Toast.LENGTH_SHORT)
                        .show();
                return;
            } else if (!new_pwd.equals(new_pwd_cf)) {
                Toast.makeText(getContext(), "两次密码不一致", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            changePassword(old_pwd, new_pwd);
        }
    }

}
