package com.ivmall.android.app.views;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.uitls.TimeUtils;

import java.lang.ref.WeakReference;


public class CountDownClock extends TextView {

    private Context mContext;
    private long mCountMillSecond;

    private TimeHandler mTimeHandler;


    private TimeOutListener mTimeOutListener;

    private static final int HANDLER_CLOCK = 0;

    private String mTextInfo;


    public CountDownClock(Context context) {
        super(context);
        mContext = context;
        mTimeHandler = new TimeHandler(this);
    }

    public CountDownClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTimeHandler = new TimeHandler(this);
    }


    public void setCountMillSecond(long millSecond) {
        mCountMillSecond = millSecond;
    }


    public long getCountMillSecond() {
        return mCountMillSecond;
    }

    public void pause() {
        if (mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.removeMessages(HANDLER_CLOCK);
        }
    }

    public void start() {
        setEnabled(false);
        if (!mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
        }
    }


    public void setTextInfo(String text) {
        this.mTextInfo = text;
        setText(text);
    }

    @Override
    protected void onAttachedToWindow() {
        start();
        super.onAttachedToWindow();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


    public void setTimeOutListener(TimeOutListener listener) {
        mTimeOutListener = listener;
    }


    private class TimeHandler extends Handler {
        private final WeakReference<CountDownClock> mTarget;

        TimeHandler(CountDownClock target) {
            mTarget = new WeakReference<CountDownClock>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HANDLER_CLOCK:
                    mCountMillSecond -= 1000;
                    setText(TimeUtils.getTimeFromSecond(mCountMillSecond)
                            + mContext.getString(R.string.send_again));
                    if (mCountMillSecond <= 0) {
                        if (mTimeOutListener != null) {
                            mTimeOutListener.timeOut();
                        }
                        setEnabled(true);
                        if (!StringUtils.isEmpty(mTextInfo)) {
                            setText(mTextInfo);
                        } else {
                            setText(mContext.getString(R.string.get_password));
                        }

                    } else {
                        mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
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
