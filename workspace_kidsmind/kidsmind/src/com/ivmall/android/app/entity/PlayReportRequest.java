package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2016/3/30.
 */
public class PlayReportRequest extends ProtocolRequest {


    private String token;
    private int episodeId;
    private int playDuration;  //播放动画时长，单位：秒
    private long startTime;   //播放动画开始时间戳
    private long endTime;     //播放动画结束时间戳
    private int firstBufferLen;  //首次缓冲时长，单位：秒
    private int getPlayUrlLen;    //获取播放Url时长，单位：秒
    private List<LoadingData> lockData;  //卡顿情况
    private List<PauseData> pauseData;  //暂停情况

    private int behaviorPlayId;
    private String categoryType;

    public PlayReportRequest() {
        lockData = new ArrayList<LoadingData>();
        pauseData = new ArrayList<PauseData>();
    }


    public static class LoadingData {
        public long lockStartTime;  //卡顿开始时间戳
        public long lockEndTime;   //卡顿结束时间戳
    }

    public static class PauseData {
        public long pauseStartTime; //暂停开始时间戳
        public long pauseEndTime;   //暂停结束时间戳
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }


    public void setFirstBufferLen(int firstBufferLen) {
        this.firstBufferLen = firstBufferLen;
    }


    public void setPlayUrlLen(int getPlayUrlLen) {
        this.getPlayUrlLen = getPlayUrlLen;
    }

    public List<LoadingData> getLockData() {
        return lockData;
    }


    public void lockDataAdd(LoadingData data) {
        lockData.add(data);
    }


    public List<PauseData> getPauseData() {
        return pauseData;
    }


    public void pauseDataAdd(PauseData data) {
        this.pauseData.add(data);
    }


    public void setBehaviorPlayId(int behaviorPlayId) {
        this.behaviorPlayId = behaviorPlayId;
    }

    public void setInfo(String info) {
        categoryType = info;
    }


    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


    public void clear() {
        episodeId = 0;
        playDuration = 0;
        startTime = 0;
        endTime = 0;
        firstBufferLen = 0;
        getPlayUrlLen = 0;
        lockData.clear();
        pauseData.clear();
    }

}
