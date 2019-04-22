package com.taku.safe.internship;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.taku.safe.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by colin on 2017/6/8.
 */

public class InternshipReportActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private InternshipReportAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_report);

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
        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("实习报告");

        TextView tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        Drawable drawableCorrect = ContextCompat.getDrawable(this, R.mipmap.ic_close_black);

        tv_confirm.setCompoundDrawablesWithIntrinsicBounds(drawableCorrect, null, null, null);
        tv_confirm.setOnClickListener(this);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new InternshipReportAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_confirm:
                break;
        }
    }
}
