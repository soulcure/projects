package com.ivmall.android.app.parent;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.views.TextProgressBar;

import java.lang.ref.WeakReference;

public class ReportStudyFragment extends Fragment {

    public static final String TAG = ReportStudyFragment.class.getSimpleName();

    private Activity mAct;

    private TextProgressBar progressBar;
    private WebView mWebView;
    private boolean isRunOnce = false;

    private ReportHandler mHandler;

    private static final int HANDLER_PROGRESS = 0;   //载入进度
    private static final int HANDLER_LOAD_URL = 1;   //载入网页

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new ReportHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.action_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.setFocusable(false);
        //mWebView.setNextFocusLeftId(R.id.tab_lean_result);

        ViewTreeObserver vto = mWebView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (!isRunOnce) {
                    int height = mWebView.getMeasuredHeight();
                    int width = mWebView.getMeasuredWidth();
                    Message msg = mHandler.obtainMessage(HANDLER_LOAD_URL);
                    msg.arg1 = width;
                    msg.arg2 = height;
                    mHandler.sendMessage(msg);
                    isRunOnce = true;
                }
                return true;
            }
        });


        progressBar = (TextProgressBar) view.findViewById(R.id.progress_loading);

        if (Build.VERSION.SDK_INT > 18) { //webview在adroid4.4以上发生闪动
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                super.onProgressChanged(view, progress);
                Message msg = mHandler.obtainMessage(HANDLER_PROGRESS);
                msg.arg1 = progress;
                mHandler.sendMessage(msg);

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }


    private class ReportHandler extends Handler {
        private final WeakReference<ReportStudyFragment> mTarget;

        ReportHandler(ReportStudyFragment target) {
            mTarget = new WeakReference<ReportStudyFragment>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HANDLER_PROGRESS:
                    int progress = msg.arg1;
                    if (progress == 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        if (progressBar.getVisibility() != View.VISIBLE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        progressBar.setProgress(progress);
                    }
                    break;
                case HANDLER_LOAD_URL:
                    int width = msg.arg1;
                    int height = msg.arg2;
                    String url = ((KidsMindApplication) mAct.getApplication())
                            .getAppConfig("learnReportURL");

                    String token = ((KidsMindApplication) mAct.getApplication()).getToken();
                    int profileId = ((KidsMindApplication) mAct.getApplication()).getProfileId();

                    StringBuilder sb = new StringBuilder();
                    sb.append(url).append('?');
                    sb.append('&').append("token").append('=').append(token);
                    sb.append('&').append("profileId").append('=').append(profileId);
                    sb.append('&').append("mobile").append('=').append(ScreenUtils.isPhone(mAct));
                    sb.append('&').append("width").append('=').append(ScreenUtils.pxToDp(mAct, width));
                    sb.append('&').append("height").append('=').append(ScreenUtils.pxToDp(mAct, height));
                    sb.append('&').append("promoter").append('=').append(((KidsMindApplication) mAct.getApplication()).getProperty("ChannelNo"));
                    mWebView.loadUrl(sb.toString());
                    break;
                default:
                    break;
            }
        }

    }
}
