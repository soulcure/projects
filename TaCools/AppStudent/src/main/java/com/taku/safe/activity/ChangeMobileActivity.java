package com.taku.safe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.taku.safe.R;
import com.umeng.analytics.MobclickAgent;


public class ChangeMobileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);
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
}
