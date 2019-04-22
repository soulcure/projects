package com.ivmall.android.app.uitls;


import android.content.Context;

import com.baidu.mobstat.StatService;
import com.ivmall.android.app.KidsMindApplication;

public class BaiduUtils {

    private BaiduUtils() {
        throw new AssertionError();
    }


    public static void init(Context context) {
        //如果您的app是为电视盒子准备的，请务必调用该接口来进行统计
        if (ScreenUtils.isTv(context)) {
            StatService.setForTv(context, true);
        }

        //appChannel是应用的发布渠道，不需要在mtj网站上注册，直接填写就可以
        String appChannel = ((KidsMindApplication) context.getApplicationContext()).getProperty("ChannelNo");
        StatService.setAppChannel(context, appChannel, true);

        // 打开崩溃收集
        StatService.setOn(context, StatService.EXCEPTION_LOG);

    }


    public static void onResume(Context context) {
        StatService.onResume(context);
    }

    public static void onPause(Context context) {
        StatService.onPause(context);
    }

    /**
     * @param context 设备上下文
     * @param eventId 业务端注册的事件 id
     * @param label   event id 下的各种事件添加的标签
     */
    public static void onEvent(Context context, String eventId, String label) {
        StatService.onEvent(context, eventId, label);
    }


    /**
     * @param context 设备上下文
     * @param eventId 业务端注册的事件 id
     * @param label   event id 下的各种事件添加的标签
     * @param acc     事件的发生次数，不指定时值为 1.
     */
    public static void onEvent(Context context, String eventId, String label, int acc) {
        StatService.onEvent(context, eventId, label, acc);
    }
}
