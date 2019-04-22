package com.taku.safe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.utils.AppUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by colin on 2017/6/8.
 */

public class AboutTakuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_taku);

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
        tv_title.setText(R.string.about_us);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        String content = AppUtils.readTextFromAsset(this, "taku_about.txt");

        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(content.replace("|", "\n"));

        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("当前应用版本:  V" + AppUtils.getVersion(this));

    }

}
