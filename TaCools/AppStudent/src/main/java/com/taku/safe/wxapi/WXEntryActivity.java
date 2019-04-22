package com.taku.safe.wxapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.taku.safe.AccountInfoActivity;
import com.taku.safe.activity.RechargeResultActivity;
import com.taku.safe.config.Constant;
import com.taku.safe.entity.WechatLoginBean;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.utils.GsonUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName();


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_wxentry);//布局文件 什么都没有 存在则是登录返回页面

        mContext = this;
        //注册API
        api = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APPID);
        api.handleIntent(getIntent(), this);
    }

    private IWXAPI api;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.getType()) {
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX: //微信分享
                finishWxShare(resp.errCode);
                break;
            case ConstantsAPI.COMMAND_SENDAUTH://微信登录
                finishWxLogin(resp);
                break;
            case ConstantsAPI.COMMAND_PAY_BY_WX://微信支付
                finishWxPay(resp.errCode);
                break;
            default:
                finish();
                break;
        }
    }


    /**
     * 获取微信登录token
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     *
     * @param code
     */
    private void getAccessToken(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.weixin.qq.com/sns/oauth2/access_token");
        sb.append("?").append("appid").append("=").append(Constant.WECHAT_APPID);
        sb.append("&").append("secret").append("=").append(Constant.WECHAT_SECRET);
        sb.append("&").append("code").append("=").append(code);
        sb.append("&").append("grant_type").append("=").append("authorization_code");

        String url = sb.toString();
        OkHttpConnector.httpGet(url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                WechatLoginBean bean = GsonUtil.parse(response, WechatLoginBean.class);
                if (bean != null && bean.getAccess_token() != null) {
                    String accessToken = bean.getAccess_token();
                }
            }
        });
    }


    /**
     * 微信登录
     *
     * @param resp
     */
    private void finishWxLogin(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;
            String code = newResp.code;
            switch (newResp.errCode) {
                case BaseResp.ErrCode.ERR_OK: {//成功
                    getAccessToken(code);
                    Intent intent = new Intent(this, AccountInfoActivity.class);
                    intent.putExtra("page_type", AccountInfoActivity.AUTHORIZE_FINISH);
                    if (code != null) {
                        intent.putExtra("code", code);
                    }
                    intent.putExtra("success", true);
                    startActivity(intent);
                    finish();
                }
                break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                case BaseResp.ErrCode.ERR_USER_CANCEL: {//用户取消
                    Intent intent = new Intent(this, AccountInfoActivity.class);
                    intent.putExtra("page_type", AccountInfoActivity.AUTHORIZE_FINISH);
                    if (code != null) {
                        intent.putExtra("code", code);
                    }
                    intent.putExtra("success", false);
                    startActivity(intent);
                    finish();
                }
                break;
            }
        }
    }


    /**
     * 微信分享
     *
     * @param code
     */
    private void finishWxShare(int code) {
        switch (code) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(mContext, "微信分享成功", Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(mContext, "微信分享取消", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mContext, "微信分享失败", Toast.LENGTH_SHORT).show();
                break;
        }
        finish();
    }


    /**
     * 微信支付
     *
     * @param code
     */
    private void finishWxPay(int code) {
        switch (code) {
            case BaseResp.ErrCode.ERR_OK: {//成功
                Log.d(TAG, "微信支付成功");
                Intent intent = new Intent(this, RechargeResultActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("isSuccess", true);
                startActivity(intent);
            }
            break;
            case BaseResp.ErrCode.ERR_USER_CANCEL: {//用户取消
                Log.d(TAG, "微信支付用户取消");
                Intent intent = new Intent(this, RechargeResultActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("isSuccess", false);
                intent.putExtra("tipMsg", "微信支付过程取消");
                startActivity(intent);
            }
            break;
            default: {
                Log.d(TAG, "微信支付失败");
                Intent intent = new Intent(this, RechargeResultActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("isSuccess", false);
                intent.putExtra("tipMsg", "微信支付过程失败");
                startActivity(intent);
            }
            break;
        }
        finish();
    }
}
