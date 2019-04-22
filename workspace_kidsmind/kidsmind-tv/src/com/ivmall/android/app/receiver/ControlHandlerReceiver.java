package com.ivmall.android.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.ControlResponse;
import com.ivmall.android.app.entity.PlaySkipRequest;
import com.ivmall.android.app.entity.PlaySkipResponse;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.player.SmartPlayingActivity;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

/**
 * Created by colin on 2016/5/18.
 */
public class ControlHandlerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String token = intent.getStringExtra("token");
        String topPackage = AppUtils.getTopPackage(context);
        String topAct = AppUtils.getTopActiviy(context);


        if (action.equals("com.ivmall.android.app.action.CloseApp")) {
            if (context.getPackageName().equals(topPackage)) {
                MainFragmentActivity.leaveApp(context);
                tvCloseConfirm(context);
            }
        } else if (action.equals("com.ivmall.android.app.action.Play")) {
            if (context.getPackageName().equals(topPackage)) {
                if (topAct.equals(FreePlayingActivity.class.getName())
                        || topAct.equals(SmartPlayingActivity.class.getName())) {
                    ((KidsMindApplication) context.getApplicationContext()).finishActivity();
                }

                Intent startIntent = new Intent(context, FreePlayingActivity.class);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.putExtra("token", token);
                startIntent.putExtra("type", FreePlayingActivity.FROM_REMOTE_PLAY);
                context.startActivity(startIntent);
            }
        }

    }


    /**
     * 1.83 远程应用关闭确认接口
     * 当远程设备收到闭关请求时，调这个接口向移动端确认已关闭
     */
    private void tvCloseConfirm(final Context context) {
        String url = AppConfig.TV_CLOSE_CONFIRM;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) context.getApplicationContext()).getToken();

        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res != null && res.isSuccess()) {
                    ((KidsMindApplication) context.getApplicationContext()).AppExit();
                }
            }
        });
    }


}
