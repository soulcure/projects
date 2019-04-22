package com.taku.safe.campus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespReportList;
import com.taku.safe.refresh.OnNextPageListener;
import com.taku.safe.refresh.RefreshRecyclerView;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


public class TucaoFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = TucaoFragment.class.getSimpleName();

    public static final int SEND_RECTOR = 30;

    private RefreshRecyclerView mRecyclerView;
    private TextView tv_empty;

    private TucaoAdapter mAdapter;

    // 是否刷新状态
    private boolean isPullRefresh = false;
    private int curPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tucao, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reqCampusMsgList();

        //setRefreshEnable(false);

        mRecyclerView.setRefreshing(true);  //开启下拉刷新动画
        mRecyclerView.setLoadMoreEnable(false);  //关闭上拉刷新

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

    /**
     * 设置下拉刷新
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshing(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setRefreshing(b);  //是否关闭下拉刷新动画
    }


    /**
     * 是否刷新是否可用
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshEnable(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setLoadMoreEnable(b);  //是否关闭上拉刷新
    }

    /**
     * 关闭刷新动画
     */
    private void refreshComplete() {
        isPullRefresh = false;
        mRecyclerView.setRefreshing(false);  //关闭下拉刷新动画
        mRecyclerView.refreshComplete();//关闭上拉刷新动画
    }


    // ToRectorActivity
    private void initView(View view) {
        view.findViewById(R.id.fab).setOnClickListener(this);

        tv_empty = (TextView) view.findViewById(R.id.tv_empty);

        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));

        /*mRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqCampusMsgList();
            }
        });*/

        mRecyclerView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqCampusMsgList();
            }
        });

        mAdapter = new TucaoAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 获取学校列表
     */
    public void reqCampusMsgList() {
        String url = AppConfig.PRESIDENT_MSG_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("page", curPage);
        params.put("limit", 10);

        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isAdded()) {
                    RespReportList bean = GsonUtil.parse(response, RespReportList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<RespReportList.PageInfoBean.ListBean> lists = bean.getPageInfo().getList();
                        mAdapter.setList(lists);
                        if (ListUtils.isEmpty(lists) && curPage == 1) {
                            mRecyclerView.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.VISIBLE);
                        } else {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            tv_empty.setVisibility(View.GONE);
                        }

                        curPage++;

                        if (bean.getPageInfo().isLastPage()) {
                            mRecyclerView.setOnNextPageListener(null);
                        } else {
                            mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                        }
                        mRecyclerView.setRefreshEnable(false); //关闭下拉刷新
                    }

                    refreshComplete();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == SEND_RECTOR) {
            curPage = 1;
            mAdapter.clear();
            reqCampusMsgList();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                startActivityForResult(new Intent(getContext(), ToRectorActivity.class), SEND_RECTOR);
                break;
        }
    }
}
