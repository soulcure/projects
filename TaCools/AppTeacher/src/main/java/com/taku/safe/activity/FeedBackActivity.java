package com.taku.safe.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by colin on 2017/6/8.
 */

public class FeedBackActivity extends BasePermissionActivity implements View.OnClickListener {

    private EditText ed_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initTitle();
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("意见反馈");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        /*ed_content = (FJEditTextCount) findViewById(R.id.ed_content);
        ed_content.setEtHint("反馈内容")//设置提示文字
                .setEtMinHeight(200)//设置最小高度，单位px
                .setLength(150)//设置总字数
                //TextView显示类型(SINGULAR单数类型)(PERCENTAGE百分比类型)
                .setType(FJEditTextCount.SINGULAR)
                //.setLineColor("#3F51B5")//设置横线颜色
                .show();*/
        ed_content = (EditText) findViewById(R.id.ed_content);
        findViewById(R.id.btn_commit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_commit:
                feedback();
                break;
        }
    }


    private void feedback() {
        String content = ed_content.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            String url = AppConfig.TEACHER_USER_FEEDBACK;

            ContentValues header = new ContentValues();
            header.put("token", mTakuApp.getToken());

            ContentValues params = new ContentValues();
            params.put("content", content);

            OkHttpConnector.httpPost(header, url, params, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    if (!isFinishing()) {
                        RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                        if (baseBean != null && baseBean.isSuccess()) {
                            Toast.makeText(mContext, "发送反馈信息成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                }
            });
        } else {
            Toast.makeText(this, "请输入反馈内容!", Toast.LENGTH_SHORT).show();
        }
    }


}
