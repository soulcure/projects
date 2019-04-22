package com.ivmall.android.app.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.ivmall.android.app.entity.PlayUrlItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markey on 2015/10/19.
 * 暂停使用
 */
public class Mp3PlayerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{
    private MediaPlayer mediaPlayer; // 媒体播放器对象
    private List<PlayUrlItem> langUrl;
    private int index=0;//播放索引
    private boolean ifCanPlay=false;//是否可以播放

    public static final String MP3_LISTS="Mp3_lists";//MP3 地址集合
    public static final String MP3_TYPE="Mp3_type";//区别播放动作
    public static final int MP3_PREVIOUS=-1;//上一首
    public static final int MP3_START_OR_PAUSE=0;//播放或暂停
    public static final int MP3_NEXT=1;//下一首
    @Override
    public void onCreate() {
        super.onCreate();
        langUrl=new ArrayList<PlayUrlItem>();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null!=intent.getExtras().getSerializable(MP3_LISTS)){
            langUrl= (List<PlayUrlItem>)intent.getExtras().getSerializable("MP3_LISTS");
            //初始化媒体播放内容
            initMedia();
        }
        //service 被系统kill之后 重传intent
        return Service.START_REDELIVER_INTENT;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 初始化媒体播放内容
     * */
    private void initMedia(){
        try {
            //媒体资源没有准备好，不能播放
            ifCanPlay=false;
            //准备媒体
            String url=langUrl.get(index).getPlayUrl();
            if(null!=url){
                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 上一首
     * */
    private void mp3Previous(){
        if(index==0){
            index=langUrl.size()-1;
        }else{
            index--;
        }
        initMedia();
    }
    /**
     * mp3 暂停或者播放
     * */
    private void mp3PlayOrPause(){
        if(null!=mediaPlayer){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else if(ifCanPlay){
                mediaPlayer.start();
            }
        }
    }
    /**
     * 下一首
     * */
    private void mp3Next() {
        if(index==langUrl.size()-1){
            index=0;
        }else{
            index++;
        }
        initMedia();
    }
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        ifCanPlay=true;
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
