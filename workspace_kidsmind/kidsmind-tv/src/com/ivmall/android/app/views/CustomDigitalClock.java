package com.ivmall.android.app.views;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.TimeUtils;

import java.lang.ref.WeakReference;


public class CustomDigitalClock extends TextView {

    private Context mContext;

    private long mCountMillSecond = 0;
    private boolean isLimited = true;  //是否显示观看时长

    private TimeHandler mTimeHandler;


    private TimeOutListener mTimeOutListener;

    private static final int HANDLER_CLOCK = 0;

    private static final String TIME_KEY = "time_key";
    //private static final long defaultTime = 30 * 60 * 1000;


    public CustomDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTimeHandler = new TimeHandler(this);
        isLimited = PlaySettingFragment.isTimeSet(context);

    }


    public void setLimited(boolean b) {
        PlaySettingFragment.setTimeSet(mContext, b);
        isLimited = b;
        if (!b) {
            if (mTimeHandler.hasMessages(HANDLER_CLOCK)) {
                mTimeHandler.removeMessages(HANDLER_CLOCK);
            }
            mCountMillSecond = 0;
        } else {
            if (!mTimeHandler.hasMessages(HANDLER_CLOCK))
                mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
        }


    }


    public boolean isLimited() {
        return isLimited;
    }

    public void setCountMillSecond(long millSecond) {
        mCountMillSecond = millSecond;
    }


    public void start() {
        //还未初始化
        if (mCountMillSecond == 0 || mTimeHandler == null) {
            return;
        }

        if (PlaySettingFragment.isParentsSettingTime()) {
            isLimited = PlaySettingFragment.isTimeSet(mContext);

            long limitTime = PlaySettingFragment.getLimitTime(mContext);
            if (limitTime > 0) {
                mCountMillSecond = limitTime;
            }
        }

        boolean isCount = mTimeHandler.hasMessages(HANDLER_CLOCK);
        if (isCount) {
            mTimeHandler.removeMessages(HANDLER_CLOCK);
        }
        mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
    }


    public void pause() {
        AppUtils.setLongSharedPreferences(mContext, TIME_KEY, mCountMillSecond);

        if (mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.removeMessages(HANDLER_CLOCK);
        }

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        long saveTime = AppUtils.getLongSharedPreferences(mContext, TIME_KEY, 0);
        long limitTime = PlaySettingFragment.getLimitTime(mContext);
        if (saveTime > 0) {
            mCountMillSecond = saveTime;
        } else {
            mCountMillSecond = limitTime;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
        mTimeOutListener = null;
        mTimeHandler = null;
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
                        setText(TimeUtils.getTimeFromMillisecond(mCountMillSecond));
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
