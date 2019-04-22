package com.ivmall.android.app.uitls;


import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.HeartBeatRequest;
import com.ivmall.android.app.entity.PlaySkipRequest;
import com.ivmall.android.app.entity.PlaySkipResponse;
import com.ivmall.android.app.entity.PlayTimeResponse;
import com.ivmall.android.app.parent.PlaySettingFragment;

import java.lang.ref.WeakReference;


public class CustomDigitalClock {

    private static final String TAG = CustomDigitalClock.class.getSimpleName();

    private static final int DEFAULT_TIME = 60 * 1000 * 30;// 默认30分钟

    private Context mContext;

    private boolean isLimited = true;  //是否显示观看时长

    private TimeHandler mTimeHandler;
    private TimeOutListener mTimeOutListener;

    private static final int HANDLER_COUNT = 1;

    public static final String TIME_SET = "TIME_SET";

    public CustomDigitalClock(Context context) {
        mContext = context;
        mTimeHandler = new TimeHandler(this);
        isLimited = isTimeSet(context);
    }


    /**
     * 是否控制播放时长
     *
     * @param context
     * @return true 表示控制播放时长
     */
    public static boolean isTimeSet(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, TIME_SET, false);
    }


    /**
     * 设置是否控制播放时长
     *
     * @param context
     * @param b       true 表示控制播放时长
     */
    public static void setTimeSet(Context context, boolean b) {
        AppUtils.setBooleanSharedPreferences(context, TIME_SET, b);
    }


    public void setLimited(boolean b) {
        setTimeSet(mContext, b);
        isLimited = b;
        if (b) { //ture 表示控制播放时长
            if (!mTimeHandler.hasMessages(HANDLER_COUNT)) {
                mTimeHandler.sendEmptyMessageDelayed(HANDLER_COUNT, 1000);
            }
        } else {
            playDuration(b, DEFAULT_TIME);
            if (mTimeHandler.hasMessages(HANDLER_COUNT)) {
                mTimeHandler.removeMessages(HANDLER_COUNT);
            }
        }

    }


    public boolean isLimited() {
        return isLimited;
    }


    public void start() {
        if (!mTimeHandler.hasMessages(HANDLER_COUNT) && isLimited) {
            mTimeHandler.sendEmptyMessageDelayed(HANDLER_COUNT, 30 * 1000); //30秒钟后开始记时
        }
    }


    public void pause() {
        if (mTimeHandler.hasMessages(HANDLER_COUNT)) {
            mTimeHandler.removeMessages(HANDLER_COUNT);
        }

    }


    public void setTimeOutListener(TimeOutListener listener) {
        mTimeOutListener = listener;
    }


    private class TimeHandler extends Handler {
        private final WeakReference<CustomDigitalClock> mTarget;

        TimeHandler(CustomDigitalClock target) {
            mTarget = new WeakReference<CustomDigitalClock>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_COUNT:
                    reqPlayTime();
                    break;
                default:
                    break;
            }
        }

    }


    public interface TimeOutListener {
        void timeOut();
    }


    /**
     * 1.17 获取首页剧集列表
     */
    private void reqPlayTime() {
        String url = AppConfig.LEFT_PLAY_DURATION;
        HeartBeatRequest request = new HeartBeatRequest();
        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {

                PlayTimeResponse resp = GsonUtil.parse(response, PlayTimeResponse.class);
                if (resp.isSuccess()) {

                    if (!mTimeHandler.hasMessages(HANDLER_COUNT))
                        mTimeHandler.sendEmptyMessageDelayed(HANDLER_COUNT, 1000 * 60);

                } else if (resp.isTimeOut()) {

                    if (mTimeOutListener != null)
                        mTimeOutListener.timeOut();

                }
            }

        });
    }


    /**
     * 1.85 设置播放时长接口
     *
     * @param effective
     * @param playDuration
     */
    private void playDuration(final boolean effective, final int playDuration) {
        String url = AppConfig.PLAY_DURATION;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();

        request.setToken(token);
        request.setEffective(effective);
        request.setPlayDuration(playDuration);
        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res.isSuccess()) {
                    PlaySettingFragment.setTimeSet(mContext, effective);
                    PlaySettingFragment.setPlayTime(mContext, playDuration);
                }
            }
        });
    }

}
