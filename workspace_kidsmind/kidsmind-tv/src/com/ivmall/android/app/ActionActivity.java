package com.ivmall.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ivmall.android.app.dialog.LoginDialog;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.impl.OnLoginSuccessListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.views.TextProgressBar;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Created by koen on 2016/2/29.
 */
public class ActionActivity extends Activity {

    private static final String TAG = ActionActivity.class.getSimpleName();

    private static final String HTTP_PARM = "http://";
    private static final String HTTPS_PARM = "https://";
    private static final String JS_PARM = "pay://promotion?";
    private static final String WX_SHARE = "weixin://share?";
    private static final String JS_LOGIN = "login://?";

    private Context mContext;
    private WebView mWebView;
    private TextProgressBar progressBar;

    private ActionHandler mHandler;

    private static final int HANDLER_PROGRESS = 0;   //载入进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_activity);
        mContext = this;
        mHandler = new ActionHandler(this);

        mWebView = (WebView) findViewById(R.id.webview);
        progressBar = (TextProgressBar) findViewById(R.id.progress_loading);


        initView(mWebView);

        ((KidsMindApplication) getApplication()).addActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaiduUtils.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BaiduUtils.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KidsMindApplication) getApplication()).finishActivity(this);
    }

    private void initView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);

        /*设置支持javascript ,android调用js方法*/
        webSettings.setJavaScriptEnabled(true);

        /*增加接口方法,让html页面调用andriod方法*/
        /*if (Build.VERSION.SDK_INT >= 17) {
            webView.addJavascriptInterface(new JavaScriptinterface(this), "android");
        }*/

        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);

        if (Build.VERSION.SDK_INT > 18) { //webview在adroid4.4以上发生闪动
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                super.onProgressChanged(view, progress);
                Message msg = mHandler.obtainMessage(HANDLER_PROGRESS);
                msg.arg1 = progress;
                mHandler.sendMessage(msg);

            }
        });


        int width = ScreenUtils.getWidthPixels(mContext);
        int height = ScreenUtils.getHeightPixels(mContext);

        String url = ((KidsMindApplication) getApplication())
                .getAppConfig("promotionURL");
        String token = ((KidsMindApplication) getApplication()).getToken();
        int profileId = ((KidsMindApplication) getApplication())
                .getProfileId();
        String promoter = ((KidsMindApplication) getApplication())
                .getPromoter();
        String appVersion = AppUtils.getVersion(this);

        boolean isMobileLogin = false;
        String moblieNum = "";
        KidsMindApplication.LoginType type = ((KidsMindApplication) getApplication())
                .getLoginType();
        if (type == KidsMindApplication.LoginType.mobileLogin) {
            isMobileLogin = true;
            moblieNum = ((KidsMindApplication) getApplication()).getMoblieNum();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url).append('?');
        sb.append('&').append("token").append('=').append(token);
        sb.append('&').append("profileId").append('=').append(profileId);
        sb.append('&').append("promoter").append('=').append(promoter);
        sb.append('&').append("appVersion").append('=').append(appVersion);
        sb.append('&').append("mobile").append('=').append(!ScreenUtils.isTv(mContext));
        sb.append('&').append("width").append('=').append(ScreenUtils.pxToDp(mContext, width));
        sb.append('&').append("height").append('=').append(ScreenUtils.pxToDp(mContext, height));
        sb.append('&').append("mLogin").append('=').append(isMobileLogin);
        if (isMobileLogin) {
            sb.append('&').append("moblieNum").append('=').append(moblieNum);
        }
        // WebView加载web资源
        webView.loadUrl(sb.toString());

        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (url.startsWith(HTTP_PARM) || url.startsWith(HTTPS_PARM)) {
                    return false;  //默认调用浏览器，打开新链接
                }

                String parm = "";
                try {
                    parm = URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (parm.startsWith(JS_PARM)) { //有风险
                    Intent intent = new Intent(mContext, ParentFragmentActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    BaiduUtils.onEvent(mContext, OnEventId.PARENT_CENTER, mContext.getString(R.string.parent_center));
                    finish();
                } else if (parm.startsWith(WX_SHARE)) {
                    //do nothing
                } else if (parm.startsWith(JS_LOGIN)) {
                    LoginDialog dialog = new LoginDialog(mContext, (KidsMindApplication) getApplication(), new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess() {
                            callJavaScript();
                        }
                    });
                    dialog.show();
                }


                return true;
            }


        });


    }


    private static class ActionHandler extends Handler {
        private final WeakReference<ActionActivity> mTarget;

        ActionHandler(ActionActivity target) {
            mTarget = new WeakReference<ActionActivity>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            ActionActivity activity = mTarget.get();
            switch (msg.what) {
                case HANDLER_PROGRESS:
                    int progress = msg.arg1;
                    if (progress == 100) {
                        activity.progressBar.setVisibility(View.GONE);
                    } else {
                        if (activity.progressBar.getVisibility() != View.VISIBLE) {
                            activity.progressBar.setVisibility(View.VISIBLE);
                        }
                        activity.progressBar.setProgress(progress);
                    }
                    break;

                default:
                    break;
            }
        }

    }


    /**
     * andrid 调用javascript函数
     */
    private void callJavaScript() {
        String token = ((KidsMindApplication) getApplication()).getToken();
        String moblieNum = ((KidsMindApplication) getApplication()).getMoblieNum();
        boolean mLoginS = true;
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:isPhoneLogin(");
        sb.append('"').append(token).append('"');
        sb.append(',').append(moblieNum);
        sb.append(',').append(mLoginS);
        sb.append(")");
        String params = sb.toString();

        if (Build.VERSION.SDK_INT >= 19) {
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
     * javascript调用andrid函数
     */
    public class JavaScriptinterface {

        private Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        public JavaScriptinterface(Context c) {
            mContext = c;
        }

        // JS call method
        /*function showAndroidToast(toast) {
            javascript:android.showToast(toast);
        }*/


        /**
         * Show a toast from the web page
         */
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
}
