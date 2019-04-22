package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.uitls.AppUtils;

import java.lang.ref.WeakReference;

/**
 * Created by koen on 2016/4/25.
 */
public class BbsWebFragment extends Fragment {

    private final static int HANDLER_PROGRESS = 4;   //载入进度

    private Activity mAct;
    private WebView mWebView;

    private ActionHandler mHandler;
    private ProgressBar progressBar;
    private KidsMindApplication application;
    StringBuilder sb;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
        application = (KidsMindApplication) mAct.getApplication();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new ActionHandler(this);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.webview);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_loading);
        initView(mWebView);
    }

    private void initView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        /*设置支持javascript ,android调用js方法*/
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCacheMaxSize(10240);

        if (Build.VERSION.SDK_INT > 18) { //webview在adroid4.4以上发生闪动
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        String url = application.getAppConfig("carddetailsURL");
        String token = application.getToken();
        int profileId = application.getProfileId();
        String promoter = application.getPromoter();
        String appVersion = AppUtils.getVersion(mAct);
        int id = getArguments().getInt(BaseActivity.WEBKEY);

        sb = new StringBuilder();
        sb.append(url).append('?');
        sb.append('&').append("token").append('=').append(token);
        sb.append('&').append("profileId").append('=').append(profileId);
        sb.append('&').append("appVersion").append('=').append(appVersion);
        sb.append('&').append("promoter").append('=').append(promoter);
        sb.append('&').append("id").append('=').append(id);
        sb.append('&').append("fromList").append('=').append(true);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(sb.toString());
            }
        }, 5000);*/
        webView.loadUrl(sb.toString());
        webView.setWebChromeClient(new CommWebChromeClient());
        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new CommWebViewClient());


    }

    /**
     * WebViewClient callback
     */
    private class CommWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            return false;
        }

    }

    /**
     * WebChromeClient callback
     */
    private class CommWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
            super.onProgressChanged(view, progress);
            Message msg = mHandler.obtainMessage(HANDLER_PROGRESS);
            msg.arg1 = progress;
            mHandler.sendMessage(msg);

        }
    }

    private class ActionHandler extends Handler {
        private final WeakReference<BbsWebFragment> mTarget;

        ActionHandler(BbsWebFragment target) {
            mTarget = new WeakReference<BbsWebFragment>(target);
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

                default:
                    break;
            }
        }

    }

}
