package com.ivmall.android.app.parent;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;

import com.ivmall.android.app.adapter.BbsFromMeAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.BBSItem;
import com.ivmall.android.app.entity.BBSResponse;
import com.ivmall.android.app.entity.MainPlayListRequest;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.views.refresh.OnNextPageListener;
import com.ivmall.android.app.views.refresh.RefreshRecyclerView;

import java.util.List;

public class SendFromMeFragment extends Fragment {

    public static final String TAG = SendFromMeFragment.class.getSimpleName();

    private Activity mAct;

    private RefreshRecyclerView mPullToRefreshView;
    private BbsFromMeAdapter mAdapter;

    // 是否刷新状态
    protected boolean isPullRefresh = false;
    private int mainListIndex = 0; // 加载playlist索引

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.refresh_fragment, container,
                false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mPullToRefreshView = (RefreshRecyclerView) view.findViewById(R.id.recycle_refresh);

        mPullToRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new BbsFromMeAdapter(getActivity());

        mPullToRefreshView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mAct);
        mPullToRefreshView.setLayoutManager(layoutManager);

        mPullToRefreshView.setRefreshEnable(true);
        mPullToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                SendFromMeFragment.this.onRefresh();
            }
        });
        mPullToRefreshView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                SendFromMeFragment.this.onRefresh();
            }
        });

        mPullToRefreshView.addItemDecoration(new PaddingItemDecoration(20));

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public void onResume() {
        super.onResume();
        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
        
        mainListIndex = 0;
        mAdapter.clean();

        onRefresh();

        //首次进入显示刷新动画
        mPullToRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
            }
        }, 50);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainListIndex = 0;
    }

    private void onRefresh() {
        int offset = 30;
        if (ScreenUtils.isTv(mAct)) {
            offset = 1000;
        }

        reqBbsList(mainListIndex, offset);
    }


    /**
     * 1.99 我发布的帖子（社区）
     */
    private void reqBbsList(final int start, final int offset) {
        String url = AppConfig.MY_NOTE;
        MainPlayListRequest request = new MainPlayListRequest();
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setStartIndex(start);
        request.setOffset(offset);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                BBSResponse resp = GsonUtil.parse(response,
                        BBSResponse.class);
                if (resp != null && resp.isSucess()) {
                    int count = resp.getData().getCounts();
                    if (count == 0) {
                        ((BaseActivity) getActivity()).showTips(R.string.my_bbs_empty);
                    }

                    if (mainListIndex == count && mainListIndex != 0) {
                        mPullToRefreshView.setRefreshing(false);
                        mPullToRefreshView.setLoadMoreEnable(false);
                        mPullToRefreshView.setRefreshEnable(false);

                        return;
                    } else if (start + offset < count) {
                        mainListIndex = start + offset;

                    } else if (start + offset >= count) {
                        mainListIndex = count;
                    }

                    List<BBSItem> list = resp.getData().getList();

                    mAdapter.setList(list);

                    isPullRefresh = false;

                    mPullToRefreshView.refreshComplete();
                    mPullToRefreshView.setLoadMoreEnable(true);
                }

                setRefreshing(false);
            }

        });
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


    class PaddingItemDecoration extends RecyclerView.ItemDecoration {
        /**
         * RecyclerView的布局方向，默认先赋值
         * 为纵向布局
         * RecyclerView 布局可横向，也可纵向
         * 横向和纵向对应的分割想画法不一样
         */
        private int mOrientation = LinearLayoutManager.VERTICAL;

        /**
         * item之间分割线的size，默认为1
         */
        private int mItemSize = 1;

        /**
         * 绘制item分割线的画笔，和设置其属性
         * 来绘制个性分割线
         */
        private Paint mPaint;

        private int mSpace;

        public PaddingItemDecoration(int space) {
            mSpace = space;

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.DKGRAY);
            /*设置填充*/
            mPaint.setStyle(Paint.Style.FILL);
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace / 2;
            outRect.top = mSpace;
            outRect.right = mSpace / 2;
            outRect.bottom = mSpace;
        }

        /*@Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }*/


        /**
         * 绘制纵向 item 分割线
         *
         * @param canvas
         * @param parent
         */
        private void drawVertical(Canvas canvas, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + layoutParams.bottomMargin;
                final int bottom = top + mItemSize;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

        /**
         * 绘制横向 item 分割线
         *
         * @param canvas
         * @param parent
         */
        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int left = child.getRight() + layoutParams.rightMargin;
                final int right = left + mItemSize;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
