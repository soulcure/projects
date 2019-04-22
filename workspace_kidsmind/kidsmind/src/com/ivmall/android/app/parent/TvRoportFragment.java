package com.ivmall.android.app.parent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.adapter.TvReportAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.PlaySkipRequest;
import com.ivmall.android.app.entity.TvReportItem;
import com.ivmall.android.app.entity.TvReportResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import java.util.List;

public class TvRoportFragment extends Fragment {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycler;
    private TvReportAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_main, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        recycler = (RecyclerView) view.findViewById(R.id.main_recycler);


        adapter = new TvReportAdapter(getActivity());
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        setHeader(recycler);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvReport();

        //首次进入显示刷新动画
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }, 50);

    }


    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header, view, false);
        adapter.setHeaderView(header);
    }


    /**
     * 1.80 获取TV端播放汇报
     */
    private void tvReport() {
        String url = AppConfig.TV_REPORT;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

        request.setToken(token);
        request.setStartIndex(0);
        request.setOffset(100);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                TvReportResponse res = GsonUtil.parse(response, TvReportResponse.class);
                if (res.isSuccess()) {
                    List<TvReportItem> list = res.getData().getList();
                    adapter.setList(list);
                }
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });
    }


}
