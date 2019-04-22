
package com.taku.safe.ui.count;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.activity.SignCountActivity;
import com.taku.safe.adapter.SignCountAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespSignRestDataList;
import com.taku.safe.refresh.OnNextPageListener;
import com.taku.safe.refresh.RefreshRecyclerView;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;

import java.util.List;

public class SignCountFragment extends BasePermissionFragment {

    private static final String TAG = SignCountFragment.class.getSimpleName();

    private RefreshRecyclerView mRecyclerView;
    private SignCountAdapter mAdapter;

    private String date;
    private int signType;
    private int signStatus;

    // 是否刷新状态
    private boolean isPullRefresh = false;
    private int curPage = 1;

    public static SignCountFragment newInstance(String date, int signType, int signStatus) {

        SignCountFragment fragment = new SignCountFragment();

        Bundle args = new Bundle();
        args.putString(SignCountActivity.DATE, date);
        args.putInt(SignCountActivity.SIGN_TYPE, signType);
        args.putInt(SignCountActivity.SIGN_STATUS, signStatus);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().size() != 0) {
            date = getArguments().getString(SignCountActivity.DATE);
            signType = getArguments().getInt(SignCountActivity.SIGN_TYPE);
            signStatus = getArguments().getInt(SignCountActivity.SIGN_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_count, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        reqRestSignData(date, signStatus);

        mRecyclerView.setRefreshing(true);  //开启下拉刷新动画
        mRecyclerView.setLoadMoreEnable(false);  //关闭上拉刷新
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


    private void initView(View view) {

        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new SignCountAdapter(getContext(), signType, signStatus);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                layoutManager.getOrientation()));

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
                reqRestSignData(date, signStatus);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }


    /**
     * 获取作息打卡学生信息（按机构 状态 时间段查询）
     *
     * @param day        日期 格式：2019-04-07
     * @param signStatus 签到状态 -2 未激活 -1 未签到 0 异常签到 1 正常
     */
    private void reqRestSignData(String day, int signStatus) {
        String url;

        if (signType == SignCountActivity.TYPE_REST) {
            url = AppConfig.TEACHER_REST_LIST;
        } else {
            url = AppConfig.TEACHER_INTERNSHIP_SINGIN_LIST;
        }


        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();

        if (signType == SignCountActivity.TYPE_REST || mTakuApp.isDecision()) {
            params.put("level", mTakuApp.getLevel());
            params.put("deptId", mTakuApp.getDeptId());
        }
        params.put("day", day);
        params.put("signinStatus", signStatus);

        params.put("page", curPage);
        params.put("limit", 10);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespSignRestDataList bean = GsonUtil.parse(response, RespSignRestDataList.class);
                if (bean != null && bean.isSuccess()) {
                    List<RespSignRestDataList.PageInfoBean.ListBean> list = bean.getPageInfo().getList();
                    mAdapter.setList(list);
                    /*if (ListUtils.isEmpty(list)) {
                        if (mRecyclerView.getVisibility() != View.GONE)
                            mRecyclerView.setVisibility(View.GONE);
                    } else {
                        if (mRecyclerView.getVisibility() != View.VISIBLE)
                            mRecyclerView.setVisibility(View.VISIBLE);
                    }*/

                    curPage++;

                    if (bean.getPageInfo().isLastPage()) {
                        mRecyclerView.setOnNextPageListener(null);
                    } else {
                        mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                    }

                    mRecyclerView.setRefreshEnable(false); //关闭下拉刷新*/

                    if (ListUtils.isEmpty(list)) {
                        mRecyclerView.setOnNextPageListener(null);
                        setRefreshEnable(false);
                    }
                }

                refreshComplete();
            }
        });

    }

}
