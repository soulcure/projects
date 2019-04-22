package com.taku.safe.login;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.activity.UserProtoActivity;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespLogin;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.GsonUtil;


public class ChangePasswordFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = ChangePasswordFragment.class.getSimpleName();


    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_new_password_again;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_pw, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
    }


    private void initTitle(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.change_password);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    private void initView(View view) {
        et_old_password = (EditText) view.findViewById(R.id.et_old_password);
        et_new_password = (EditText) view.findViewById(R.id.et_new_password);
        et_new_password_again = (EditText) view.findViewById(R.id.et_new_password_again);

        view.findViewById(R.id.btn_commit).setOnClickListener(this);

    }


    /**
     * 用户注册
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    private void changePassword(String oldPassword, String newPassword) {
        String url = AppConfig.STUDENT_CHANGEWD;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在请求修改密码，请稍后...");
        dialog.show();

        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    RespLogin bean = GsonUtil.parse(response, RespLogin.class);
                    if (bean != null) {
                        if (bean.isSuccess()) {
                            mTakuApp.setToken(bean.getToken());
                            mTakuApp.setTs(bean.getTs());
                            mTakuApp.setExpire(bean.getExpire());
                            mTakuApp.setBind(bean.isBind());
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_confirm:
                getActivity().finish();
                break;
            case R.id.btn_commit:
                String oldPassword = et_old_password.getText().toString();
                String newPassword = et_new_password.getText().toString();
                String newPasswordAgain = et_new_password_again.getText().toString();

                if (TextUtils.isEmpty(oldPassword)) {
                    Toast.makeText(getContext(), "请输入原密码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(getContext(), "请输入新密码", Toast.LENGTH_SHORT).show();
                } else if (!AppUtils.isPassWord(newPassword)) {
                    Toast.makeText(getContext(), "密码必须为6-20个字母或数字组成", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newPasswordAgain)) {
                    Toast.makeText(getContext(), "请再次输入新密码", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(newPasswordAgain)) {
                    Toast.makeText(getContext(), "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    changePassword(oldPassword, newPassword);
                }

                break;
            case R.id.tv_proto:
                startActivity(new Intent(mContext, UserProtoActivity.class));
                break;
        }

    }


}
