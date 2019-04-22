package com.applidium.nickelodeon.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.ImageUtils;
import com.applidium.nickelodeon.uitls.Log;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();

    public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;


    // WX_APP_ID 应用从官方网站申请到的合法appId
    public static final String WX_APP_ID = "wxcaf562c48129678c";  //应用ID
    public static final String WX_PARTNER_ID = "1246309201";      //商户ID


    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LinearLayout(this));
        Log.v(TAG, "onCreate");

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent");
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String url = bundle.getString("wx_url");
            String title = bundle.getString("wx_title");
            Log.e(TAG, "url=" + url + "&&&title=" + title);
            //String description = bundle.getString("wx_description");

            shareWeiXinFriends(url, title, null, null);

        } else {
            Log.v(TAG, "finish");
            finish();
        }


        BaiduUtils.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        BaiduUtils.onPause(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }


    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                finish();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                finish();
                break;
            default:
                break;
        }
    }


    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        Log.e(TAG, "resp.errCode=" + resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        android.util.Log.e("koen", "onResp result = " + result );
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }

    private void shareWeiXinFriends(String url, String title,
                                    String description, Bitmap bitmap) {

        int wxSdkVersion = api.getWXAppSupportAPI();

        if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
            return;//
        }

        WXWebpageObject web = new WXWebpageObject();
        web.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(web);


        msg.title = title;
        if (!StringUtils.isEmpty((description))) {
            msg.description = description;
        }
        if (bitmap != null) {
            msg.setThumbImage(bitmap);
        }else{
            msg.setThumbImage(ImageUtils.ResourceToBitmap(this, R.drawable.app_icon));
        }


        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage" + System.currentTimeMillis();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }


}