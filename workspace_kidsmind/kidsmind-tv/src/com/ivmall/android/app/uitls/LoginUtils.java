package com.ivmall.android.app.uitls;

import android.content.Context;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.MobileBindRequest;
import com.ivmall.android.app.entity.ProtocolResponse;
import com.ivmall.android.app.entity.UserInfo;
import com.ivmall.android.app.parent.PlaySettingFragment;

/**
 * Created by koen on 2016/4/27.
 * 用于集中管理用户的登录和退出
 */
public class LoginUtils {

    Context mContext;
    KidsMindApplication application;

    public LoginUtils(Context context) {
        mContext = context;
        application = (KidsMindApplication) mContext.getApplicationContext();
    }

    /**
     * 1.6 注销登录
     */
    public void loginOut() {
        String url = AppConfig.LOGIN_OUT;
        MobileBindRequest request = new MobileBindRequest();

        String token = application.getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);
                if (resp.isSucess()) {
                    MainFragmentActivity.leaveApp(mContext);

                    application.clearMobileToken();
                    application.reqUserInfo();//刷新匿名用户VIP信息
                    application.reqProfile(null);  //刷新匿名profile信息
                    if (loginOutListener != null)
                        loginOutListener.onSuccess();


                } else {
                    if (loginOutListener != null)
                        loginOutListener.onFailed(resp.getMessage());
                }

            }

        });
    }

    /**
     * 1.2 手机号登录
     *
     * @param phoneNum
     * @param smsCode
     */
    public void mobileLogin(final String phoneNum, String smsCode) {
        String url = AppConfig.MOBILE_LOGIN;
        MobileBindRequest request = new MobileBindRequest();
        request.setMobile(phoneNum);
        request.setValidateCode(smsCode);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProtocolResponse resp = GsonUtil.parse(response,
                        ProtocolResponse.class);

                if (resp.isSucess()) {
                    String token = resp.getToken();
                    UserInfo.VipType vip = resp.getVipLevel();
                    application.setToken(token);
                    application.setMoblieNum(phoneNum);
                    application.setVipLevel(vip);
                    application.reqUserInfo();//刷新用户VIP信息

                    if (loginInListener != null)
                        loginInListener.onSuccess();

                    MainFragmentActivity.openApp(mContext);
                    PlaySettingFragment.playSetting(mContext);
                } else {
                    if (loginInListener != null)
                        loginInListener.onFailed(resp.getMessage());
                }
            }

        });

    }

    public interface LoginInListener {
        void onSuccess();

        void onFailed(String message);
    }

    private LoginInListener loginInListener;

    public void setLoginInSuccess(LoginInListener l) {
        loginInListener = l;
    }

    public interface LoginOutListener {
        void onSuccess();

        void onFailed(String message);
    }

    private LoginOutListener loginOutListener;

    public void setLoginOutSuccess(LoginOutListener l) {
        loginOutListener = l;
    }

}
