package com.taku.safe.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.HttpConnector;
import com.taku.safe.http.IGetListener;
import com.taku.safe.protocol.respond.RespEvaluationItem;
import com.taku.safe.protocol.respond.RespNewsItem;
import com.taku.safe.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;


public class WebViewActivity extends BasePermissionActivity {

    public static final String WEB_TITLE = "web_title";
    public static final String WEB_URL = "web_url";
    public static final String TEST_ID = "test_id";
    public static final String ARTICLE_ID = "ARTICLE_ID";

    private WebView webView;
    private ProgressBar progressBar;

    // logic
    private String title;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        int testId;
        int articleId;

        if (savedInstanceState == null) {
            title = getIntent().getStringExtra(WEB_TITLE);
            url = getIntent().getStringExtra(WEB_URL);
            testId = getIntent().getIntExtra(TEST_ID, 0);
            articleId = getIntent().getIntExtra(ARTICLE_ID, 0);
        } else {
            title = savedInstanceState.getString(WEB_TITLE);
            url = savedInstanceState.getString(WEB_URL);
            testId = getIntent().getIntExtra(TEST_ID, 0);
            articleId = getIntent().getIntExtra(ARTICLE_ID, 0);
        }

        initTitle(title);
        initView();

        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        } else if (testId != 0) {
            reqEvalution(testId);
        } else if (articleId != 0) {
            reqNewsDetail(articleId);
        }

    }


    private void initTitle(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void initView() {
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        webView = (WebView) findViewById(R.id.web_view);

        // 启用支持javascript
        WebSettings settings = webView.getSettings();
        // 设置可以支持缩放
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);

        //todo start
        //设置加载进来的页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //todo end


        // 设置出现缩放工具
        settings.setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false自己处理
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar == null)
                    return;

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(WEB_TITLE, title);
        outState.putString(WEB_URL, url);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
    }


    /**
     * 获取测评的H5地址
     */
    private void reqEvalution(int testId) {
        String url = AppConfig.STUDENT_TEST_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("testId", testId);

        HttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespEvaluationItem bean = GsonUtil.parse(response, RespEvaluationItem.class);
                if (bean != null && bean.isSuccess()) {
                    webView.loadUrl(bean.getUrl());
                }
            }
        });
    }


    /**
     * 获取每日一文详情的H5地址
     */
    private void reqNewsDetail(int articleId) {
        String url = AppConfig.STUDENT_ARTICLE_DETAIL;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("articleId", articleId);

        HttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespNewsItem bean = GsonUtil.parse(response, RespNewsItem.class);
                if (bean != null && bean.isSuccess()) {
                    webView.loadUrl(bean.getUrl());
                }
            }
        });

    }
}
