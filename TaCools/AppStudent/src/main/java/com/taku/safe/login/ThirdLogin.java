package com.taku.safe.login;

//import android.app.Activity;
//import android.content.Intent;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.sina.weibo.sdk.WbSdk;
//import com.sina.weibo.sdk.auth.AccessTokenKeeper;
//import com.sina.weibo.sdk.auth.AuthInfo;
//import com.sina.weibo.sdk.auth.Oauth2AccessToken;
//import com.sina.weibo.sdk.auth.WbAuthListener;
//import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
//import com.sina.weibo.sdk.auth.sso.SsoHandler;
//import com.taku.safe.R;
//import com.taku.safe.config.Constant;
//import com.tencent.connect.common.Constants;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.Tencent;
//import com.tencent.tauth.UiError;
//
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;

/**
 * Created by colin on 2017/6/6.
 * 第三方登录
 */

public class ThirdLogin {

    /*public final static String TAG = ThirdLogin.class.getSimpleName();

    private LoginFragment mFragment;
    private Activity mAct;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    //QQ SDK 接口
    private Tencent mTencent;

    //weibo SDK 接口
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;

    *//**
     * QQ 登录回调
     *//*
    private IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object object) {
            JSONObject jsonObject = (JSONObject) object;
            initOpenidAndToken(jsonObject);
        }

        @Override
        public void onError(UiError uiError) {
        }

        @Override
        public void onCancel() {
        }
    };


    public ThirdLogin(LoginFragment fragment) {
        mFragment = fragment;
        mAct = fragment.getActivity();
    }

    *//**
     * 微信登录
     * snsapi_userinfo 是需要认证开发者后才可以申请开通的
     *//*
    public void wechatLogin() {
        api = WXAPIFactory.createWXAPI(mAct, Constant.WECHAT_APPID, false);
        api.registerApp(Constant.WECHAT_APPID);

        //api注册
        if (api.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            //授权读取用户信息
            req.scope = "snsapi_userinfo";
            //自定义信息
            req.state = "toucool_login";
            //向微信发送请求
            api.sendReq(req);

        } else {
            Toast.makeText(mAct, "请安装微信客户端", Toast.LENGTH_SHORT).show();
        }

    }


    *//**
     * QQ登录
     *//*
    public void qqLogin() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constant.QQ_APPID, mAct);
        }
        if (!mTencent.isSupportSSOLogin(mAct)) {
            Toast.makeText(mAct, "QQ未安装或不支持QQ登陆", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mTencent.isSessionValid()) {
            *//**
             * 应用需要获得哪些API的权限，由","分隔。
             * SCOPE = "get_user_info,add_t"
             * 所有权限用"all"
             *//*
            String scope = "all";
            mTencent.login(mFragment, scope, loginListener);
        }
    }


    *//**
     * 微博登录
     *//*
    public void weiboLogin() {
        mAccessToken = AccessTokenKeeper.readAccessToken(mAct);
        if (!mAccessToken.isSessionValid()) {
            *//**
             * 当前应用的回调页，需和申请保持一致
             *//*
            String redirect_url = "http://www.sina.com";
            *//**
             * 权限申请，需和申请保持一致
             *//*
            String scope = "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

            mAuthInfo = new AuthInfo(mAct, Constant.WB_APPID, redirect_url, scope);
            WbSdk.install(mAct, mAuthInfo);

            mSsoHandler = new SsoHandler(mAct);
            mSsoHandler.authorize(new SelfWbAuthListener());
        }

    }

    *//**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     *//*
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = mAct.getString(R.string.weibosdk_token_format);
        Log.v(TAG, String.format(format, mAccessToken.getToken(), date));
    }


    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }


    private class SelfWbAuthListener implements WbAuthListener {

        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
                    if (mAccessToken.isSessionValid()) {
                        // 显示 Token
                        updateTokenView(false);
                        // 保存 Token 到 SharedPreferences
                        AccessTokenKeeper.writeAccessToken(mAct, mAccessToken);
                        Toast.makeText(mAct, "授权成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(mAct, "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(mAct, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }*/


}
