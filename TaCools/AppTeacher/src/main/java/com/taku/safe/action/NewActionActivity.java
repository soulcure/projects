package com.taku.safe.action;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.taku.safe.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by colin on 2017/6/8.
 */

public class NewActionActivity extends AppCompatActivity {

    private EditText et_code;
    private TextView tv_choice;
    private Switch switch_code;
    private Switch switch_sign;
    private Button btn_next;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_action);

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
        tv_title.setText("加入活动");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {

        et_code = findViewById(R.id.et_code);
        tv_choice = findViewById(R.id.tv_choice);
        switch_code = findViewById(R.id.switch_code);
        switch_sign = findViewById(R.id.switch_sign);


        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

}
