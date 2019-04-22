package com.taku.safe.ui.Analysis;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.activity.HardwareAnalyzeActivity;
import com.taku.safe.adapter.AnalysisAdapter;
import com.taku.safe.entity.AnalysisItem;
import com.taku.safe.utils.SpacesGridItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.List;


public class AnalysisFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = AnalysisFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    //疑似失联 超级宅人 异常出入 严重逃课
    public static final AnalysisItem[] iconRes = new AnalysisItem[]{
            new AnalysisItem(R.mipmap.ic_not_connection, "疑似失联", HardwareAnalyzeActivity.class),
            new AnalysisItem(R.mipmap.ic_not_out, "超级宅人", HardwareAnalyzeActivity.class),
            new AnalysisItem(R.mipmap.ic_access_unusual, "异常出入", HardwareAnalyzeActivity.class),
            //new AnalysisItem(R.mipmap.ic_not_track, "行踪异常", HardwareAnalyzeActivity.class),
            //new AnalysisItem(R.mipmap.ic_not_sleep, "也不归宿", HardwareAnalyzeActivity.class),
            new AnalysisItem(R.mipmap.ic_not_class, "严重逃课", HardwareAnalyzeActivity.class)};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        List<AnalysisItem> list = Arrays.asList(iconRes);

        AnalysisAdapter adapter = new AnalysisAdapter(getContext(), list);

        //mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getContext()));//添加分割线
        mRecyclerView.addItemDecoration(new SpacesGridItemDecoration(4)); //添加间距

        mRecyclerView.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:

                break;
            case R.id.tv_forget:

                break;
            case R.id.tv_confirm:

                break;
        }

    }


    private class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable mDivider;

        public DividerGridItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }

        private int getSpanCount(RecyclerView parent) {
            // 列数
            int spanCount = -1;
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                spanCount = ((StaggeredGridLayoutManager) layoutManager)
                        .getSpanCount();
            }
            return spanCount;
        }

        private void drawHorizontal(Canvas c, RecyclerView parent) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getLeft() - params.leftMargin;
                final int right = child.getRight() + params.rightMargin
                        + mDivider.getIntrinsicWidth();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        private void drawVertical(Canvas c, RecyclerView parent) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);

                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getTop() - params.topMargin;
                final int bottom = child.getBottom() + params.bottomMargin;
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                    int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                        return true;
                    }
                } else {
                    childCount = childCount - childCount % spanCount;
                    if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                        return true;
                }
            }
            return false;
        }

        private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                                  int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                // StaggeredGridLayoutManager 且纵向滚动
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    childCount = childCount - childCount % spanCount;
                    // 如果是最后一行，则不需要绘制底部
                    if (pos >= childCount)
                        return true;
                } else {
                    // StaggeredGridLayoutManager 且横向滚动

                    // 如果是最后一行，则不需要绘制底部
                    if ((pos + 1) % spanCount == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition,
                                   RecyclerView parent) {
            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();
            if (isLastRaw(parent, itemPosition, spanCount, childCount)) {// 如果是最后一行，则不需要绘制底部
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {// 如果是最后一列，则不需要绘制右边
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(),
                        mDivider.getIntrinsicHeight());
            }
        }
    }

}
