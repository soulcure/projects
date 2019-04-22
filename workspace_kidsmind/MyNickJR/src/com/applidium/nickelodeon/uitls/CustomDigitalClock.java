package com.applidium.nickelodeon.uitls;


import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.applidium.nickelodeon.ParentsCenterActivity;

import java.lang.ref.WeakReference;


public class CustomDigitalClock {

    private static final String TAG = CustomDigitalClock.class.getSimpleName();

    private Context mContext;

    private long mCountMillSecond = 0;
    private boolean isLimited = true;  //是否显示观看时长

    private TimeHandler mTimeHandler;


    private TimeOutListener mTimeOutListener;

    private static final int HANDLER_CLOCK = 0;

    public static final String TIME_SET = "TIME_SET";

    private static final String TIME_KEY = "time_key";

    private static final long defaultTime = 0;


    private String mTimeKey;

    public CustomDigitalClock(Context context) {
        mContext = context;
        mTimeHandler = new TimeHandler(this);
        isLimited = isTimeSet(context);
        mTimeKey = TIME_KEY + TimeUtils.getDate(System.currentTimeMillis());
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


    public void setLimitTime(Context context, long time) {
        AppUtils.setLongSharedPreferences(context, mTimeKey, time);
    }


    public long getLimitTime(Context context) {
        //获取当天剩余播放时间
        long time = AppUtils.getLongSharedPreferences(context, mTimeKey, defaultTime);
        if (time == defaultTime) {
            time = ParentsCenterActivity.getLimitTime(context);  //获取默认的设置时间
        }
        return time;
    }


    public void setLimited(boolean b) {
        setTimeSet(mContext, b);
        isLimited = b;
        if (b) { //ture 表示控制播放时长
            if (!mTimeHandler.hasMessages(HANDLER_CLOCK))
                mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
        } else {
            if (mTimeHandler.hasMessages(HANDLER_CLOCK)) {
                mTimeHandler.removeMessages(HANDLER_CLOCK);
            }
            mCountMillSecond = 0;
        }


    }


    public boolean isLimited() {
        return isLimited;
    }

    public void setCountMillSecond(long millSecond) {
        mCountMillSecond = millSecond;
    }


    public void start() {
        isLimited = isTimeSet(mContext); //true表示控制播放时长
        if (isLimited) {
            long limitTime = getLimitTime(mContext);
            if (limitTime > 0) {
                mCountMillSecond = limitTime;
            }

            if (mTimeHandler.hasMessages(HANDLER_CLOCK)) {
                mTimeHandler.removeMessages(HANDLER_CLOCK);
            }

            mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
        }


    }


    public void pause() {
        if (mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.removeMessages(HANDLER_CLOCK);
            setLimitTime(mContext, mCountMillSecond);
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
                case HANDLER_CLOCK:
                    if (isLimited) {
                        mCountMillSecond -= 1000;
                        Log.v(TAG, TimeUtils.getTimeFromMillisecond(mCountMillSecond));

                        if (mTimeOutListener != null && mCountMillSecond <= 0) {
                            mTimeOutListener.timeOut();
                        } else {
                            mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
                        }
                    }

                    break;
                default:
                    break;
            }
        }

    }


    public interface TimeOutListener {
        void timeOut();
    }

}
