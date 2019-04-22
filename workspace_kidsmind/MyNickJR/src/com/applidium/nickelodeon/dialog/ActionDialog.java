package com.applidium.nickelodeon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.applidium.nickelodeon.ActionActivity;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.ActionResponse;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.FoucsActionView;

/**
 *
 */
public class ActionDialog extends Dialog implements View.OnClickListener {


    private Context mContext;
    private ImageView img_action;
    private ActionResponse mAction;
    private Bitmap mBitmap;

    private EditText et_phone_number;
    private Button btn_commit;
    private ImageButton btn_cacel;
    private FoucsActionView rel_foucs;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */
    public ActionDialog(Context context, ActionResponse resp, Bitmap bitmap) {
        super(context, R.style.full_dialog);
        mContext = context;
        mAction = resp;
        mBitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.action_dialog);
        initView();

    }


    private void initView() {
        btn_cacel = (ImageButton) findViewById(R.id.btn_cacel);
        img_action = (ImageView) findViewById(R.id.img_action);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        rel_foucs = (FoucsActionView) findViewById(R.id.rel_foucs);

        if (!StringUtils.isEmpty(mAction.getUrl())) {
            rel_foucs.setClickable(true);
            rel_foucs.setFocusable(true);

            rel_foucs.setOnClickListener(this);
            rel_foucs.requestFocus();
        } else {
            btn_cacel.requestFocus();
        }


        btn_cacel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);


        boolean isShow = mAction.isShowMobileInput();
        if (isShow) {
            findViewById(R.id.linearInput).setVisibility(View.VISIBLE);
        }

        img_action.setImageBitmap(mBitmap);
    }


    /**
     * 1.53 活动信息登记接口
     */
    private void joinAction(String num) {
        /*String url = AppConfig.JOIN_ACTION;
        JoinActionRequest request = new JoinActionRequest();

        String token = ((MNJApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);
        request.setMobile(num);
        request.setTitle(mAction.getTitle());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                JoinActionResponse resp = GsonUtil.parse(response,
                        JoinActionResponse.class);
                if (resp.isSucess()) {
                    Toast.makeText(mContext, R.string.join_action_sucess, Toast.LENGTH_SHORT).show();

                    String url = mAction.getImgUrl();
                    String md5 = AppUtils.md5(url);
                    AppUtils.setBooleanSharedPreferences(mContext, md5, false);

                    dismiss();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_commit) {
            String num = et_phone_number.getText().toString();
            if (StringUtils.isEmpty(num)) {
                Toast.makeText(mContext, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AppUtils.isMobileNum(num)) {
                Toast.makeText(mContext, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            joinAction(num);

        } else if (id == R.id.btn_cacel) {
            dismiss();
        } else if (id == R.id.rel_foucs) {
            Intent intent = new Intent(mContext, ActionActivity.class);
            mContext.startActivity(intent);
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }


    @Override
    public void show() {
        String url = mAction.getImgUrl();
        String md5 = AppUtils.md5(url);

        boolean b = AppUtils.getBooleanSharedPreferences(mContext, md5, true);
        if (b || AppConfig.TEST_HOST) {
            try {
                super.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
