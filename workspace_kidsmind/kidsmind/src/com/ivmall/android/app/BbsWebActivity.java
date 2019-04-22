package com.ivmall.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ivmall.android.app.uitls.AppUtils;

import java.lang.ref.WeakReference;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


/**
 * Created by koen on 2016/2/26.
 */
public class BbsWebActivity extends Activity {

    private static final String HTTP_PARM = "http://";
    private static final String HTTPS_PARM = "https://";
    private WebView mWebView;
    private MaterialProgressBar progressBar;
    private Context mContext;
    private KidsMindApplication application;

    private ActionHandler mHandler;


    public final static int FILE_CHOOSER_RESULT_CODE = 1;
    private final static int HANDLER_PROGRESS = 4;

    private ValueCallback<Uri> mFilePathCallback4;
    private ValueCallback<Uri[]> mFilePathCallback5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_activity);
        mContext = this;
        mHandler = new ActionHandler(this);
        application = (KidsMindApplication) getApplication();
        mWebView = (WebView) findViewById(R.id.my_webView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_btn);
        toolbar.setTitle(R.string.my_post_detail);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressBar = (MaterialProgressBar) findViewById(R.id.progress_bar);
        initView(mWebView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);

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
        String appVersion = AppUtils.getVersion(mContext);
        int id = getIntent().getIntExtra(BaseActivity.WEBKEY, 0);

        StringBuilder sb = new StringBuilder();
        sb.append(url).append('?');
        sb.append('&').append("token").append('=').append(token);
        sb.append('&').append("profileId").append('=').append(profileId);
        sb.append('&').append("appVersion").append('=').append(appVersion);
        sb.append('&').append("promoter").append('=').append(promoter);
        sb.append('&').append("id").append('=').append(id);
        sb.append('&').append("fromList").append('=').append(true);

        // WebView加载web资源
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
            if (url.startsWith(HTTP_PARM) || url.startsWith(HTTPS_PARM)) {
                return false;  //默认调用浏览器，打开新链接
            }
            return true;
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

        // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
        public void openFileChooser(ValueCallback<Uri> filePathCallback) {
            openFileChooserImpl(filePathCallback);
        }

        // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
        public void openFileChooser(ValueCallback filePathCallback, String acceptType) {
            openFileChooserImpl(filePathCallback);
        }

        // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
        public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
            openFileChooserImpl(filePathCallback);

        }

        // For Android > 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            onShowFileChooserImpl(filePathCallback);
            return true;
        }

    }


    /**
     * android4.0 及以下版本 选择文件
     *
     * @param uploadMsg
     */
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mFilePathCallback4 = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    /**
     * android5.0 及以上版本 选择文件
     *
     * @param uploadMsg
     */
    private void onShowFileChooserImpl(ValueCallback<Uri[]> uploadMsg) {
        mFilePathCallback5 = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_CHOOSER_RESULT_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Uri result = null;
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FILE_CHOOSER_RESULT_CODE) {

                    if (null == mFilePathCallback4) return;

                    if (data != null) {
                        result = data.getData();
                    }

                }
            }

            mFilePathCallback4.onReceiveValue(result);
            mFilePathCallback4 = null;

        } else {
            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FILE_CHOOSER_RESULT_CODE) {

                    if (null == mFilePathCallback5) return;

                    if (data != null) {
                        results = new Uri[]{data.getData()};
                    }

                }
            }

            mFilePathCallback5.onReceiveValue(results);
            mFilePathCallback5 = null;

        }
    }


    private class ActionHandler extends Handler {
        private final WeakReference<BbsWebActivity> mTarget;

        ActionHandler(BbsWebActivity target) {
            mTarget = new WeakReference<BbsWebActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
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
