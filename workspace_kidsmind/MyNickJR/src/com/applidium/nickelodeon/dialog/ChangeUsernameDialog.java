package com.applidium.nickelodeon.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.ChangeUserNameRequest;
import com.applidium.nickelodeon.entity.ProtocolResponse;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.uitls.TimeUtils;

/**
 *
 */
public class ChangeUsernameDialog extends Dialog implements OnClickListener {

    private Context mContext;

    private EditText etusername;
    private Button btnconfirm;
    private MNJApplication application;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */

    public ChangeUsernameDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_us);

        initView();
    }

    private void initView() {
        this.btnconfirm = (Button) findViewById(R.id.btn_confirm);
        this.etusername = (EditText) findViewById(R.id.et_username);

        btnconfirm.setOnClickListener(this);
        application = (MNJApplication) mContext.getApplicationContext();
    }

    /**
     * 监听修改成功
     */
    private OnChangeSuccessListener listener;

    public void setChangeSuccessListener(OnChangeSuccessListener listener) {
        this.listener = listener;
    }

    public interface OnChangeSuccessListener {
        void onSuccess();
    }

    /**
     * 修改用户名
     */
    private void changeUsername(final String username) {
        String url = AppConfig.UPDATE_USERNAME;
        ChangeUserNameRequest request = new ChangeUserNameRequest();
        request.setUsername(username);
        request.setToken(application.getToken());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    application.setUserName(username);
                    application.setFirstModifiedTime(TimeUtils.getTime(System.currentTimeMillis()));
                    if (null != listener) {
                        listener.onSuccess();
                    }
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }

        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();

        if (id == R.id.btn_confirm) {
            String us = etusername.getText().toString();
            if (StringUtils.isEmpty(us)) {
                Toast.makeText(getContext(), "不能为空", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (us.length() < 6) {
                Toast.makeText(getContext(), "用户名长度最少为6位", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            changeUsername(us);
        }
    }

}
