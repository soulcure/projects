package com.taku.safe.evaluation;

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
import com.taku.safe.protocol.respond.RespEvaluation;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class EvaluationFragment extends BasePermissionFragment {

    private static final String TAG = EvaluationFragment.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private EvaluationAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evaluation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initView(view);

        reqEvalution();
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
        mAdapter = new EvaluationAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

    }


    private void reqEvalution() {

        List<RespEvaluation> list = new ArrayList<>();

        RespEvaluation item1 = new RespEvaluation();
        item1.setTitle("你是一个自信的人吗？");
        item1.setNumber("已经有200人参与");

        RespEvaluation item2 = new RespEvaluation();
        item2.setTitle("测试你会成为那个超级英雄！");
        item2.setNumber("已经有300人参与");

        RespEvaluation item3 = new RespEvaluation();
        item3.setTitle("测试有多少钱你会满足？");
        item3.setNumber("已经有1200人参与");

        RespEvaluation item4 = new RespEvaluation();
        item4.setTitle("你是个很性感的人吗？");
        item4.setNumber("已经有1200人参与");

        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);

        mAdapter.setList(list);

    }

}
