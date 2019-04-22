
package com.taku.safe.ui.sos;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.adapter.SosHistoryAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespSosList;
import com.taku.safe.refresh.OnNextPageListener;
import com.taku.safe.refresh.RefreshRecyclerView;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;

import java.util.List;

public class SosHistoryFragment extends BasePermissionFragment {
    private static final String TAG = SosHistoryFragment.class.getSimpleName();

    private RefreshRecyclerView mRecyclerView;
    private TextView tv_empty;

    private SosHistoryAdapter mAdapter;

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
        return inflater.inflate(R.layout.fragment_sos_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reqSosList();

        mRecyclerView.setRefreshing(true);  //开启下拉刷新动画
        mRecyclerView.setLoadMoreEnable(false);  //关闭上拉刷新
    }


    private void initTitle(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.sos_title);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    private void initView(View view) {
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        /*mRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqSosList();
            }
        });*/
        mRecyclerView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqSosList();
            }
        });


        mAdapter = new SosHistoryAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);
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


    private void reqSosList() {
        String url = AppConfig.TEACHER_SOS_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("page", curPage);
        params.put("limit", 10);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespSosList bean = GsonUtil.parse(response, RespSosList.class);
                if (bean != null && bean.isSuccess()) {
                    List<RespSosList.PageInfoBean.ListBean> list = bean.getPageInfo().getList();
                    if (ListUtils.isEmpty(list)) {
                        tv_empty.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        tv_empty.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    mAdapter.setList(list);
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
        });

    }


}
