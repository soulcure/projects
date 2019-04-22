package com.taku.safe.utils;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.ImageView;

import com.taku.safe.R;

/**
 * Created by colin on 08/12/17.
 */
public class MediaPlayManager {
    private MediaPlayer mMediaPlayer;
    private boolean isPause;
    private ImageView imgVoice;

    private static MediaPlayManager instance;

    private MediaPlayManager() {
        mMediaPlayer = new MediaPlayer();
    }

    public static MediaPlayManager instance() {
        if (instance == null) {
            instance = new MediaPlayManager();
        }
        return instance;
    }


    public void playSound(String filepath, final ImageView img_voice) {
        img_voice.setImageResource(R.drawable.voice_anim);
        final AnimationDrawable anim = (AnimationDrawable) img_voice.getDrawable();

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (imgVoice != null) {
                Drawable drawable = imgVoice.getDrawable();
                if (drawable instanceof AnimationDrawable) {
                    AnimationDrawable animation = (AnimationDrawable) drawable;
                    if (animation.isRunning()) {
                        animation.stop();
                        imgVoice.setImageResource(R.mipmap.voice_anim_3);
                        imgVoice = null;
                    }
                }
            }
        }

        mMediaPlayer.reset();

        try {
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    if (anim != null && anim.isRunning()) {
                        anim.stop();
                        imgVoice = null;
                    }
                    img_voice.setImageResource(R.mipmap.voice_anim_3);
                    return false;
                }
            });

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    if (anim != null && !anim.isRunning()) {
                        anim.start();
                    }
                    imgVoice = img_voice;
                }
            });
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (anim != null && anim.isRunning()) {
                        anim.stop();
                        imgVoice = null;
                    }
                    img_voice.setImageResource(R.mipmap.voice_anim_3);
                }
            });

            mMediaPlayer.setDataSource(filepath);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int voiceTime(String filepath, final boolean isAutoPlay, final ImageView img_voice) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    AnimationDrawable anim = null;

                    Drawable drawable = img_voice.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        anim = (AnimationDrawable) drawable;
                    }

                    if (anim != null && anim.isRunning()) {
                        anim.stop();
                    }
                    img_voice.setImageResource(R.mipmap.voice_anim_3);
                    return false;
                }
            });
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (isAutoPlay) {
                        mp.start();
                        img_voice.setImageResource(R.drawable.voice_anim);
                        AnimationDrawable anim = (AnimationDrawable) img_voice.getDrawable();
                        anim.start();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    AnimationDrawable anim = null;

                    Drawable drawable = img_voice.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        anim = (AnimationDrawable) drawable;
                    }

                    if (anim != null && anim.isRunning()) {
                        anim.stop();
                    }
                    img_voice.setImageResource(R.mipmap.voice_anim_3);
                }
            });
            mMediaPlayer.setDataSource(filepath);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMediaPlayer.getDuration();//获取音频的时间

    }


    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
        if (imgVoice != null) {
            Drawable drawable = imgVoice.getDrawable();
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable anim = (AnimationDrawable) drawable;
                if (anim.isRunning()) {
                    anim.stop();
                    imgVoice.setImageResource(R.mipmap.voice_anim_3);
                    imgVoice = null;
                }
            }
        }
    }

    public void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
