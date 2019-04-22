package com.taku.safe.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespOrder;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class OrderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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
        tv_title.setText("我的订单");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new OrderAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);
    }


    private void reqOrderList() {
        List<RespOrder> list = new ArrayList<>();

        RespOrder item1 = new RespOrder();
        item1.setTitle("你是一个自信的人吗？");
        item1.setDate("2017-12-20");
        item1.setOrderId("12365");
        item1.setPrice("5元");

        RespOrder item2 = new RespOrder();
        item2.setDate("2017-12-22");
        item2.setOrderId("13265");
        item2.setPrice("5元");

        RespOrder item3 = new RespOrder();
        item3.setDate("2017-12-23");
        item3.setOrderId("65345");
        item3.setPrice("5元");

        RespOrder item4 = new RespOrder();
        item4.setDate("2017-12-24");
        item4.setOrderId("87895");
        item4.setPrice("5元");

        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);

        mAdapter.setList(list);
    }

}
