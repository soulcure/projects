package com.taku.safe.evaluation;

import android.content.ContentValues;
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
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.HttpConnector;
import com.taku.safe.http.IGetListener;
import com.taku.safe.protocol.respond.RespEvaluation;
import com.taku.safe.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;

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
        String url = AppConfig.STUDENT_TEST_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());
        HttpConnector.httpGet(header, url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespEvaluation bean = GsonUtil.parse(response, RespEvaluation.class);
                if (bean != null && bean.isSuccess()) {
                    List<RespEvaluation.TestListBean> list = bean.getTestList();
                    mAdapter.setList(list);
                }
            }
        });
    }

}
