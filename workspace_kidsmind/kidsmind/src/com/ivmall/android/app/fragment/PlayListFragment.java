package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.adapter.GridViewAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.entity.CartoonRoleResponse;
import com.ivmall.android.app.entity.MainPlayListRequest;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.R;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.refresh.HeaderSpanSizeLookup;
import com.ivmall.android.app.views.refresh.OnNextPageListener;
import com.ivmall.android.app.views.refresh.RefreshRecyclerView;

import java.util.List;


public class PlayListFragment extends Fragment {
    private static final String TAG = PlayListFragment.class.getSimpleName();

    private RefreshRecyclerView mPullToRefreshView;
    private GridViewAdapter mAdapter;

    private Activity mAct;

    // 是否刷新状态
    protected boolean isPullRefresh = false;


    private int mainListIndex = 0; // 加载playlist索引

    private String mInfo;

    public static PlayListFragment newInstance(String info) {
        Bundle args = new Bundle();
        PlayListFragment fragment = new PlayListFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInfo = getArguments().getString("info");
        return inflater.inflate(R.layout.refresh_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mPullToRefreshView = (RefreshRecyclerView) view.findViewById(R.id.recycle_refresh);

        mPullToRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new GridViewAdapter(getActivity());
        mAdapter.setInfo(mInfo);

        mPullToRefreshView.setAdapter(mAdapter);

        int columns = getResources().getInteger(R.integer.gridview_phone_columns);

        if (!ScreenUtils.isPhone(getActivity())) {
            columns = getResources().getInteger(R.integer.gridview_pad_columns);
        }

        GridLayoutManager manager = new GridLayoutManager(getActivity(), columns);

        HeaderSpanSizeLookup lookup = new HeaderSpanSizeLookup(
                mPullToRefreshView.getHeaderAdapter(), manager.getSpanCount());
        manager.setSpanSizeLookup(lookup); //设置hear and foot view 居中显示
        mPullToRefreshView.setLayoutManager(manager);

        //设置下拉刷新回调
        mPullToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                PlayListFragment.this.onRefresh();
            }
        });

        //设置上拉刷新回调
        mPullToRefreshView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                PlayListFragment.this.onRefresh();
            }
        });

        int paddingItem = getResources().getDimensionPixelOffset(R.dimen.gridview_spacing);

        int screenWidth = ScreenUtils.getWidthPixels(mAct);
        int girdItemWidth = getResources().getDimensionPixelOffset(R.dimen.ITEM_NORMAL_SIZE);

        int padding = (screenWidth - girdItemWidth * columns - paddingItem * columns * 2) / 2;

        mPullToRefreshView.setPadding(padding, 0, padding, 0);
        mPullToRefreshView.addItemDecoration(new PaddingItemDecoration(paddingItem));
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainListIndex = 0;
    }

    private void onRefresh() {
        int offset = 30;
        if (mainListIndex == 0) {
            String cache = ((KidsMindApplication) mAct.getApplication()).getCartoonInfo(mInfo);
            if (!StringUtils.isEmpty(cache)) {
                parseCartoonData(mainListIndex, offset, cache);
            } else {
                reqMainList(mainListIndex, offset);
                //首次进入显示刷新动画
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshing(true);
                    }
                }, 50);
            }
        } else {
            reqMainList(mainListIndex, offset);
        }
    }


    /**
     * 1.17 获取首页剧集列表
     */
    private void reqMainList(final int start, final int offset) {
        String url = AppConfig.MAIN_PLAYLIST_V2;
        MainPlayListRequest request = new MainPlayListRequest();
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setStartIndex(start);
        request.setOffset(offset);
        request.setCategory(mInfo);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (start == 0) {
                    ((KidsMindApplication) mAct.getApplication()).setCartoonInfo(mInfo, response);
                }
                parseCartoonData(start, offset, response);
            }

        });
    }

    private void parseCartoonData(int start, int offset, String data) {
        CartoonRoleResponse resp = GsonUtil.parse(data,
                CartoonRoleResponse.class);
        if (resp != null && resp.isSucess()) {
            int count = resp.getData().getCounts();

            if (mainListIndex == count && mainListIndex != 0) {
                mPullToRefreshView.setLoadMoreEnable(false); //关闭上拉刷新
                showTips(R.string.is_last_page);
                return;
            } else if (start + offset < count) {
                mainListIndex = start + offset;

            } else if (start + offset >= count) {
                mainListIndex = count;
            }

            List<CartoonItem> list = resp.getData().getList();

            mAdapter.setList(list);

            isPullRefresh = false;

            mPullToRefreshView.refreshComplete();
            mPullToRefreshView.setLoadMoreEnable(true);
        }

        setRefreshing(false);

    }


    /**
     * 设置下拉刷新
     *
     * @param b
     */
    private void setRefreshing(boolean b) {
        mPullToRefreshView.setRefreshEnable(b); //关闭下拉刷新
        mPullToRefreshView.setRefreshing(b);  //关闭下拉刷新动画

    }

    private void showTips(int id) {
        Snackbar snack = Snackbar.make(mPullToRefreshView, id, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.primary));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snack.show();
    }


    class PaddingItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        public PaddingItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.top = mSpace / 2;
            outRect.right = mSpace;
            outRect.bottom = mSpace / 2;
        }
    }
}










