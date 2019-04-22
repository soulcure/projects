package com.taku.safe.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taku.safe.R;


/**
 * 这个类封装了下拉刷新的布局
 *
 * @author Li Hong
 * @since 2013-7-30
 */
public class FooterLoadingLayout extends LoadingLayout {
    /**
     * 进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 显示的文本
     */
    private TextView mHintView;

    private LinearLayout mFooter;

    /**
     * 构造方法
     *
     * @param context context
     */
    public FooterLoadingLayout(Context context) {
        super(context);
        initView();
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public FooterLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        mFooter = (LinearLayout) findViewById(R.id.pull_to_load_footer_content);
        mProgressBar = (ProgressBar) findViewById(R.id.pull_to_load_footer_progressbar);
        mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer, null, false);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public int getContentSize() {
        View view = findViewById(R.id.pull_to_load_footer_content);
        if (null != view) {
            return view.getHeight();
        }

        return (int) (getResources().getDisplayMetrics().density * 40);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        //mFooter.setVisibility(View.GONE);
        //mProgressBar.setVisibility(View.GONE);
        //mHintView.setVisibility(View.GONE);

        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        mHintView.setText(R.string.pull_to_refresh_bottom_hint_normal);
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_bottom_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_header_hint_ready);
    }

    @Override
    protected void onRefreshing() {
        mFooter.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_load_more_text);
    }

    @Override
    protected void onNoMoreData() {
        mFooter.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_bottom_no_more_msg);
    }
}