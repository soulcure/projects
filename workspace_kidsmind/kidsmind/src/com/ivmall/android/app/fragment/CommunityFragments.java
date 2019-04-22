package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.dialog.LoginDialog;
import com.ivmall.android.app.impl.OnLoginSuccessListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.ScreenUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;

/**
 * Created by colin on 2016/4/5.
 */
public class CommunityFragments extends Fragment {

    private static final String TAG = CommunityFragments.class.getSimpleName();

    private static final String HTTP_PARM = "http://";
    private static final String HTTPS_PARM = "https://";
    private static final String JS_LOGIN = "login://?";

    public final static int FILE_CHOOSER_RESULT_CODE = 1;

    private final static int HANDLER_PROGRESS = 4;   //载入进度

    private Activity mAct;
    private WebView mWebView;

    private ActionHandler mHandler;
    private ProgressBar progressBar;
    private KidsMindApplication application;

    private ValueCallback<Uri> mFilePathCallback4;
    private ValueCallback<Uri[]> mFilePathCallback5;

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
        initInputMode();
        initView(mWebView);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initInputMode() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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


        int width = ScreenUtils.getWidthPixels(mAct);
        int height = ScreenUtils.getHeightPixels(mAct);

        /*String url = ((KidsMindApplication) mAct.getApplication())
                .getAppConfig("promotionURL");*/
        String url = application.getAppConfig("communityURL");
        String token = application.getToken();
        int profileId = application.getProfileId();
        String promoter = application.getPromoter();
        String appVersion = AppUtils.getVersion(mAct);

        boolean isMobileLogin = false;
        String mobileNum = "";
        KidsMindApplication.LoginType type = ((KidsMindApplication) mAct.getApplication())
                .getLoginType();
        if (type == KidsMindApplication.LoginType.mobileLogin) {
            isMobileLogin = true;
            mobileNum = application.getMoblieNum();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url).append('?');
        sb.append('&').append("token").append('=').append(token);
        sb.append('&').append("profileId").append('=').append(profileId);
        sb.append('&').append("promoter").append('=').append(promoter);
        sb.append('&').append("appVersion").append('=').append(appVersion);
        sb.append('&').append("mobile").append('=').append(!ScreenUtils.isTv(mAct));
        sb.append('&').append("width").append('=').append(ScreenUtils.pxToDp(mAct, width));
        sb.append('&').append("height").append('=').append(ScreenUtils.pxToDp(mAct, height));
        sb.append('&').append("mLogin").append('=').append(isMobileLogin);
        if (isMobileLogin) {
            sb.append('&').append("mobileNum").append('=').append(mobileNum);
        }

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

            String parm;
            try {
                parm = URLDecoder.decode(url, "UTF-8");
                if (parm.startsWith(JS_LOGIN)) {
                    LoginDialog dialog = new LoginDialog(mAct, (KidsMindApplication) mAct.getApplication(),
                            new OnLoginSuccessListener() {
                                @Override
                                public void onSuccess() {
                                    callJavaScript();
                                }
                            });
                    dialog.show();
                    return true;
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Toast.makeText(mAct, R.string.upload_error_info, Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    /**
     * andrid 调用javascript函数
     */
    private void callJavaScript() {
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        String moblieNum = ((KidsMindApplication) mAct.getApplication()).getMoblieNum();
        boolean mLoginS = true;
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:init(");
        sb.append('"').append(token).append('"');
        sb.append(',').append(moblieNum);
        sb.append(',').append(mLoginS);
        sb.append(")");
        String params = sb.toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(params, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i(TAG, "onReceiveValue value=" + value);
                }
            });
        } else {
            mWebView.loadUrl(params);
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
        private final WeakReference<CommunityFragments> mTarget;

        ActionHandler(CommunityFragments target) {
            mTarget = new WeakReference<CommunityFragments>(target);
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
