package com.taku.safe.action;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespAction;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by colin on 2017/5/10.
 */
public class ActionFragment extends BasePermissionFragment {

    public final static String TAG = ActionFragment.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private ActionAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initView(view);
        reqActionInfo();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }


    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new ActionAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);


    }


    private void reqActionInfo() {

        List<RespAction> list = new ArrayList<>();

        RespAction item1 = new RespAction();
        item1.setTitle("你是一个自信的人吗1？");
        item1.setDate("2017-12-20");
        item1.setTeacher("陈老师");

        RespAction item2 = new RespAction();
        item2.setTitle("你是一个自信的人吗2？");
        item2.setDate("2017-12-22");
        item2.setTeacher("王老师");

        RespAction item3 = new RespAction();
        item3.setTitle("你是一个自信的人吗3？");
        item3.setDate("2017-12-23");
        item3.setTeacher("李老师");

        RespAction item4 = new RespAction();
        item4.setTitle("你是一个自信的人吗4？");
        item4.setDate("2017-12-24");
        item4.setTeacher("余老师");

        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);

        mAdapter.setList(list);

    }


}
