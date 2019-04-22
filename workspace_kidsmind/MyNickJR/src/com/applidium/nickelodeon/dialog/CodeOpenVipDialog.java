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
import com.applidium.nickelodeon.entity.CodeOpenVipRequest;
import com.applidium.nickelodeon.entity.CodeOpenVipResponse;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;

/**
 *
 */
public class CodeOpenVipDialog extends Dialog implements OnClickListener {

    private Context mContext;

    private MNJApplication application;
    private EditText et_code;
    private Button btnconfirm;

    public CodeOpenVipDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_open_vip);

        initView();
    }

    private void initView() {

        this.btnconfirm = (Button) findViewById(R.id.btn_confirm);
        this.et_code = (EditText) findViewById(R.id.et_code);
        btnconfirm.setOnClickListener(this);

        application = (MNJApplication) mContext.getApplicationContext();
    }

    /**
     * 监听修改成功
     */
    private ChangeUsernameDialog.OnChangeSuccessListener listener;

    public void setChangeSuccessListener(ChangeUsernameDialog.OnChangeSuccessListener listener) {
        this.listener = listener;
    }

    private void OpenVip() {
        String url = AppConfig.VIP_SUBSCRIBE;
        CodeOpenVipRequest request = new CodeOpenVipRequest();
        request.setToken(application.getToken());
        request.setPassword(application.getPassWord());
        request.setVipCode(et_code.getText().toString());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                CodeOpenVipResponse resp = GsonUtil.parse(response,
                        CodeOpenVipResponse.class);

                if (resp.isSucess()) {
                    Toast.makeText(mContext, "开通成功", Toast.LENGTH_SHORT).show();
                    application.setVipExpiresTime(resp.getData().getVipExpiryTime());
                    application.reqUserInfo();
                    excuteOnchange();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
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

        String code = et_code.getText().toString();
        if (id == R.id.btn_confirm) {
            if (StringUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "请输入开通码", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            OpenVip();
        }
    }

}
