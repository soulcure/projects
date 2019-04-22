package com.applidium.nickelodeon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.views.TextProgressBar;

import java.lang.ref.WeakReference;

/**
 *
 */
public class WebViewDialog extends Dialog implements View.OnClickListener {


    private static final int HANDLER_PROGRESS = 0;   //载入进度
    private static final int HANDLER_LOAD_URL = 1;   //载入网页

    private Context mContext;

    private ReportHandler mHandler;
    private ImageButton btnCancel;
    private WebView mWebView;
    private String mUrl;
    private TextProgressBar progressBar;


    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */
    public WebViewDialog(Context context, String url) {
        super(context, R.style.full_dialog);
        mContext = context;
        mUrl = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_dialog);
        mHandler = new ReportHandler(this);
        initView();
    }


    private void initView() {
        btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnCancel.requestFocus();

        progressBar = (TextProgressBar) findViewById(R.id.progress_loading);

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                super.onProgressChanged(view, progress);
                Message msg = mHandler.obtainMessage(HANDLER_PROGRESS);
                msg.arg1 = progress;
                mHandler.sendMessage(msg);

            }
        });
        mWebView.loadUrl(mUrl);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    private class ReportHandler extends Handler {
        private final WeakReference<WebViewDialog> mTarget;

        ReportHandler(WebViewDialog target) {
            mTarget = new WeakReference<WebViewDialog>(target);
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
