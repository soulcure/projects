package com.taku.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taku.safe.R;
import com.umeng.analytics.MobclickAgent;


/**
 * WebView
 *
 * @author modify by YW
 * @version V1.0
 * @date 2016年8月13日
 */
public class WebViewActivity extends AppCompatActivity {

    public static final String WEB_TITLE = "web_title";
    public static final String WEB_URL = "web_url";

    private WebView webView;
    private ProgressBar progressBar;

    // logic
    private String title;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (savedInstanceState == null) {
            title = getIntent().getStringExtra(WEB_TITLE);
            url = getIntent().getStringExtra(WEB_URL);
        } else {
            title = savedInstanceState.getString(WEB_TITLE);
            url = savedInstanceState.getString(WEB_URL);
        }


        initTitle(title);
        initView();

        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }

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
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
    }
}
