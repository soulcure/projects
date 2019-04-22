package com.applidium.nickelodeon.service;

import com.applidium.nickelodeon.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getSimpleName();

    private SoundPool mSoundPool;
    private MediaPlayer mMediaPlayer;
    private Context mContext;


    public static final int WELCOME = 1;
    public static final int BABYINFO = 2;
    public static final int BEFORE = 3;
    public static final int DOYOULAIK = 4;
    public static final int TIMEOVER = 5;
    public static final int BUYVIP = 6;

    public static final int RATING_START = 7;
    public static final int ONCLICK = 8;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);    //初始化，最多同时播放8个音频
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        }
        int operate = intent.getIntExtra("media", 0);
        AssetFileDescriptor afd = null;
        switch (operate) {
            case WELCOME:
                playSound(R.raw.login);
                break;
            case DOYOULAIK:
                playSound(R.raw.rating_like);
                break;
            case RATING_START:
                playSound(R.raw.rating_start);
                break;
            case ONCLICK:
                playSound(R.raw.click);
                break;
            case BABYINFO:
                afd = mContext.getResources().openRawResourceFd(R.raw.create_babyinfo_file);
                break;
            case BEFORE:
                afd = mContext.getResources().openRawResourceFd(R.raw.before_play_file);
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


    /**
     * 播放声音
     *
     * @param context
     * @param what
     */
    public static void playSound(Context context, int what) {
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.putExtra("media", what);
        context.startService(intent);
    }


    private void playSound(int res) {
        final int sound = mSoundPool.load(mContext, res, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                mSoundPool.play(sound, 1, 1, 0, 0, 1);
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mSoundPool.release();
    }

}
