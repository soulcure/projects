package com.applidium.nickelodeon;

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
import android.widget.ImageButton;
import android.widget.Toast;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.LoginDialog;
import com.applidium.nickelodeon.dialog.PaymentDialog;
import com.applidium.nickelodeon.entity.VipListItem;
import com.applidium.nickelodeon.entity.VipListRequest;
import com.applidium.nickelodeon.entity.VipListResponse;
import com.applidium.nickelodeon.impl.OnLoginSuccessListener;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.views.TextProgressBar;
import com.applidium.nickelodeon.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;

public class ActionActivity extends Activity {

    private static final String TAG = ActionActivity.class.getSimpleName();

    private static final String HTTP_PARM = "http://";
    private static final String JS_PARM = "pay://promotion?";
    private static final String WX_SHARE = "weixin://share?";
    private static final String JS_LOGIN = "login://?";
    private static final String AC_PAY = "pay://?";

    private Context mContext;
    private WebView mWebView;
    private TextProgressBar progressBar;

    private ActionHandler mHandler;
    private List<VipListItem> mList;

    private MNJApplication application;

    ImageButton bckbtn;

    private static final int HANDLER_PROGRESS = 0;   //载入进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_activity);
        mContext = this;
        application = ((MNJApplication) getApplication());
        mHandler = new ActionHandler(this);

        mWebView = (WebView) findViewById(R.id.webview);
        progressBar = (TextProgressBar) findViewById(R.id.progress_loading);

        initView(mWebView);

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

        String url = application.getAppConfig("AndroidPromotionURL");
        String token = application.getToken();
        int profileId = application.getProfileId();
        String promoter = application.getPromoter();
        String appVersion = AppUtils.getVersion(this);

        boolean isMobileLogin = false;
        MNJApplication.LoginType type = application.getLoginType();
        if (type == MNJApplication.LoginType.mobileLogin) {
            isMobileLogin = true;
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
        // WebView加载web资源
        webView.loadUrl(sb.toString());

        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (url.startsWith(HTTP_PARM)) {
                    view.loadUrl(url);
                    return true;
                }
                String parm;
                try {
                    parm = URLDecoder.decode(url, "UTF-8");
                    String paramStr = null;
                    if (parm.startsWith(JS_PARM)) {
                        paramStr = parm.replace(JS_PARM, "");
                    } else if (parm.startsWith(WX_SHARE)) {
                        paramStr = parm.replace(WX_SHARE, "");
                    } else if (parm.startsWith(JS_LOGIN)) {
                        paramStr = parm.replace(JS_LOGIN, "");
                    } else if (parm.startsWith(AC_PAY)) {
                        paramStr = parm.replace(AC_PAY, "");

                    }

                    String content = paramStr.replace('&', '\n');
                    ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
                    Properties properties = new Properties();
                    properties.load(is);
                    if (parm.startsWith(AC_PAY)) {
                        String title = AppUtils.getProperty(properties, "title", "UTF-8");
                        if (title.equals("20160201")) {
                            /*mList = application.getVipListItems();
                            if (mList != null) {
                                openDialog(mList);
                            } else {
                                reqVipList();
                            }*/
                            Intent intent = new Intent();
                            intent.setClass(mContext, VipInfoActivity.class);
                            startActivity(intent);
                        }
                    } else if (parm.startsWith(JS_PARM)) { //有风险
                        String vipGuid = properties.getProperty("vipGuid", "false");
                        String vipName = AppUtils.getProperty(properties, "vipName", "UTF-8");
                        double vipPrice = Double.parseDouble(properties.getProperty("vipPrice", "0"));
                        String productId = AppUtils.getProperty(properties, "partnerProductId", "UTF-8");

                        PaymentDialog.payment(mContext, vipPrice, vipGuid, vipName, productId);
                    } else if (parm.startsWith(WX_SHARE)) {
                        IWXAPI api = WXAPIFactory.createWXAPI(mContext, WXEntryActivity.WX_APP_ID, false);
                        if (!api.isWXAppInstalled()) {
                            Toast.makeText(mContext, "未发现有安装微信客户端", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        String shareUrl = properties.getProperty("url", "");
                        String title = AppUtils.getProperty(properties, "title", "UTF-8");

                        Intent intent = new Intent(mContext,
                                WXEntryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("wx_url", shareUrl);
                        bundle.putString("wx_title", title);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else if (parm.startsWith(JS_LOGIN)) {
                        LoginDialog dialog = new LoginDialog(mContext, application, new OnLoginSuccessListener() {
                            @Override
                            public void onSuccess() {
                                callJavaScript();
                            }
                        });
                        dialog.show();
                    }
                    is.close();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }


        });

        bckbtn = (ImageButton) findViewById(R.id.btn_play_return);
        if (ScreenUtils.isTv(mContext)) {
            bckbtn.setVisibility(View.GONE);
        } else {
            bckbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(NickelFragmentActivity.RESULT_MODEL_A_BACK);
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        setResult(NickelFragmentActivity.RESULT_MODEL_A_BACK);
        finish();
    }

    private class ActionHandler extends Handler {
        private final WeakReference<ActionActivity> mTarget;

        ActionHandler(ActionActivity target) {
            mTarget = new WeakReference<ActionActivity>(target);
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


    /**
     * android 调用javascript函数
     */
    private void callJavaScript() {
        String token = application.getToken();
        int profileId = application.getProfileId();
        boolean mLoginS = true;
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:isPhoneLogin(");
        sb.append('"').append(token).append('"');
        sb.append(',').append(profileId);
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

    /**
     * 1.30 获取VIP列表
     *//*
    private void reqVipList() {
        String url = AppConfig.VIP_LIST;
        VipListRequest request = new VipListRequest();

        String token = application.getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                VipListResponse resp = GsonUtil.parse(response,
                        VipListResponse.class);
                if (resp.isSucess()) {
                    mList = resp.getData().getList();
                    if (mList == null || mList.size() == 0) {
                        Toast.makeText(ActionActivity.this, "暂时没有VIP列表信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        application.setVipListItems(mList);
                        openDialog(mList);
                    }
                }
            }
        });
    }*/

    /*private void openDialog(List<VipListItem> list) {
        for (int i = 0; i < list.size(); i ++) {
            VipListItem item = list.get(i);
            String vipTitle = item.getVipTitle();
            if (vipTitle.contains("年")) {
                try {
                    double price = item.getpriceDouble();
                    PaymentDialog.payment(mContext, price, item.getVipGuid(),
                            item.getVipTitle(), item.getPartnerProductId());
                } catch (NumberFormatException e) {
                }
                break;
            }
        }
    }*/
}
