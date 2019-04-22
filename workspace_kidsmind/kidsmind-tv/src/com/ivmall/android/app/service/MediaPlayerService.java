package com.ivmall.android.app.service;

import com.smit.android.ivmall.stb.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private Context mContext;


    public static final int WELCOME = 1;
    public static final int BABYINFO = 2;
    public static final int BEFORE = 3;
    public static final int DOYOULAIK = 4;
    public static final int TIMEOVER = 5;
    public static final int BUYVIP = 6;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        }
        int operate = intent.getIntExtra("media", 0);
        AssetFileDescriptor afd = null;
        switch (operate) {
            case WELCOME:
                afd = mContext.getResources().openRawResourceFd(R.raw.welcome_file);
                break;
            case BABYINFO:
                afd = mContext.getResources().openRawResourceFd(R.raw.create_babyinfo_file);
                break;
            case BEFORE:
                afd = mContext.getResources().openRawResourceFd(R.raw.before_play_file);
                break;
            case DOYOULAIK:
                afd = mContext.getResources().openRawResourceFd(R.raw.doyoulike_file);
                break;
            case TIMEOVER:
                afd = mContext.getResources().openRawResourceFd(R.raw.time_over_file);
                break;
            case BUYVIP:
                afd = mContext.getResources().openRawResourceFd(R.raw.buy_vip_file);
                break;
        }
        try {
            if (afd != null) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                afd.close();
            }
        } catch (IOException ex) {
            Log.e(TAG, "create failed:", ex);
        }
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

}
