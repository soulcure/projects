package com.ivmall.android.app.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.ivmall.android.app.ActionActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.KidsMindFragmentActivity;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.BaiduPushInfoRequest;
import com.ivmall.android.app.entity.ControlResponse;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by smit on 2015/11/4.
 */
public class MyPushMessageReceiver extends PushMessageReceiver {

    public static final String ACTION = "action";
    public static final String SERIEID = "serieId";
    public static final String SEQUENCEID = "sequence";
    public static final String SERIE = "serie";
    public static final String ACTIVITY = "activity";
    public static final String TOPIC = "topic";
    public static final String TOPICID = "topicId";

    public static final String TAG = MyPushMessageReceiver.class.getSimpleName();

    /**
     * 绑定push server的结果
     *
     * @param context
     * @param errorCode 绑定成功为0
     * @param appid     应用id
     * @param userId    应用user id
     * @param channelId 应用channel id
     * @param requestId 向服务器发起的请求ID, 用于追查问题
     */
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {

        if (errorCode == 0) {
            PushManager.listTags(context);  //请求tags列表
            reqBaiduPushInfo(channelId);
        }
    }

    private void reqBaiduPushInfo(String channelId) {
        String url = AppConfig.BAIDU_PUSH_REGISTER;
        BaiduPushInfoRequest request = new BaiduPushInfoRequest();
        request.setChannelId(channelId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {

            }
        });
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    /**
     * listTags() 的回调函数。
     *
     * @param context
     * @param errorCode 错误码，0表示列举tag成功。
     * @param tags      当前应用设置的所有tag。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
        //获取成功
        if (errorCode == 0) {
            ((KidsMindApplication) context.getApplicationContext()).setBaiduPushTag(tags);
        }
    }

    /**
     * 接受透传信息
     *
     * @param context
     * @param s                   推送的消息
     * @param customContentString 自定义内容
     */
    @Override
    public void onMessage(Context context, String s, String customContentString) {

        ControlResponse resp = GsonUtil.parse(s, ControlResponse.class);
        if (resp != null)
            setWatchdog(context, resp);

    }

    /**
     * 接收通知栏点击的函数，用于点击后直接播放视频
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {

        if (!TextUtils.isEmpty(customContentString)) {
            boolean isAppRunning = false;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(context.getPackageName())
                        && info.baseActivity.getPackageName().equals(context.getPackageName())) {
                    isAppRunning = true;
                    break;
                }
            }
            try {
                JSONObject customJson = new JSONObject(customContentString);
                if (!customJson.isNull(ACTION)) {
                    String action = customJson.getString(ACTION);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(ACTION, action);  //传入分类
                    if (action.equals(SERIE)) {
                        if (!customJson.isNull(SERIEID)) {
                            String serieId = customJson.getString(SERIEID);
                            bundle.putInt(SERIEID, Integer.parseInt(serieId));
                        }
                        if (!customJson.isNull(SEQUENCEID)) {
                            String epsodeId = customJson.getString(SEQUENCEID);
                            bundle.putInt(SEQUENCEID, Integer.parseInt(epsodeId));
                        }
                        if (isAppRunning) {
                            intent.setClass(context.getApplicationContext(), FreePlayingActivity.class);
                        }
                    } else if (action.equals(ACTIVITY)) {
                        if (isAppRunning) {
                            intent.setClass(context.getApplicationContext(), ActionActivity.class);
                        }
                    } else if (action.equals(TOPIC)) {
                        //进入推荐页面
                        if (!customJson.isNull(TOPICID)) {
                            String topicId = customJson.getString(TOPICID);
                            bundle.putInt(TOPICID, Integer.parseInt(topicId));
                        }
                        if (isAppRunning) {
                            intent.setClass(context.getApplicationContext(), FreePlayingActivity.class);
                        }
                    }

                    if (!isAppRunning) {
                        intent.setClass(context.getApplicationContext(), KidsMindFragmentActivity.class);
                    }
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }


    /**
     * 定时创建服务
     */
    public static void setWatchdog(Context context, ControlResponse resp) {
        ControlResponse.ControlType type = resp.getCustomContent();

        Intent intent = new Intent();
        if (type == ControlResponse.ControlType.confirm) {
            intent.setAction("com.ivmall.android.app.action.Confirm");
        }
        context.sendBroadcast(intent);
    }
}
