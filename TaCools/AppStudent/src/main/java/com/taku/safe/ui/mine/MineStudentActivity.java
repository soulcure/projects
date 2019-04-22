package com.taku.safe.ui.mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.taku.safe.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by colin on 2018/1/8.
 */

public class MineStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_student);
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
