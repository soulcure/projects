package com.ivmall.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by koen on 2016/2/26.
 */
public class MallActivity extends Activity {

    private WebView mWebView;
    private Context mContext;
    private String shoppingUrl;

    public static final String URL_KEY = "SHOPPING_URL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_activity);
        mContext = this;
        mWebView = (WebView) findViewById(R.id.mall_webview);
        Intent intent = getIntent();
        shoppingUrl = intent.getStringExtra(URL_KEY);
        initView(mWebView);
    }


    private void initView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);

        if (Build.VERSION.SDK_INT > 18) { //webview在adroid4.4以上发生闪动
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                super.onProgressChanged(view, progress);
            }
        });

        webView.loadUrl(shoppingUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;  //默认调用浏览器，打开新链接
            }
        });
    }

}



