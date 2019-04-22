package com.applidium.nickelodeon.player;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;


import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.PlayUrlInfo;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.DeviceUtils;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IGetListener;
import com.applidium.nickelodeon.uitls.Log;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;


/*
 * the player report model
 * should be static in the project and no sync lock
 */

public class PlayerReportModel {
    private static String TAG = PlayerReportModel.class.getSimpleName();

    private static final String VERSION = "2.0";   //汇报服务器版本号

    private Context mContext;

    private String session;  //会话ID
    private int contentId; //内容ID episodeId
    private String accountId; //userId
    private int profileId;
    private String audioLang;  //播放节目的语音en-gb/zh-cn
    private String model;     //设备型号
    private String appVersion; //应用版本号
    private String resolution; //屏幕分辨率  例如1024x768
    private String version;   //汇报服务器版本号，固定1.0
    private String osVersion;  //系统版本号
    private String serial;    //设备序列号
    private String deviceDRMId; //设备DRMId
    private String promoterCode; //应用渠道号

    private String macAddr;   //mac地址
    private String brand;   //BRAND 运营商
    private String vendor;  //生产商
    private String manufacturer;  //生产厂家

    private int totalTime; //总时间 ，所有时间单位为秒
    private int playTime;  //播放总时间
    private int startLatency; //从进入播放到视频开始播放的时间
    private int userPauseTime; //暂停总时间
    private int bufferingPauseTime; //缓冲总时间
    private int seekPauseTime;  //seek puase时间
    private int userPauses; //用户暂停次数
    private int bufferingPauses;  //缓冲暂停次数
    private int seekForward;  //向前seek次数
    private int seekBackward;//向后seek次数


    private long curPlayTime;

    private long loadingTime;

    private long seekTime;

    private long pauseTime;

    private long getUrlTime;
    private String cdn;


    private long curForwardTime;
    private long curBackwardTime;


    public PlayerReportModel(Context context) {
        mContext = context;

        accountId = ((MNJApplication) context.getApplicationContext()).getUserId();

        String m = Build.MODEL;
        try {
            model = URLEncoder.encode(m, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            model = "unknown";
        }

        appVersion = AppUtils.getVersion(context);
        resolution = ScreenUtils.getScreenPixels(context); //屏幕分辨率  例如1024x768
        version = VERSION;   //汇报服务器版本号
        promoterCode = ((MNJApplication) context.getApplicationContext()).getProperty("ChannelNo");

        initSystemVer();
        serial = Build.SERIAL;
        deviceDRMId = AppConfig.getDeviceDRMId(context);
        macAddr = DeviceUtils.getLocalMacAddress(context);
        brand = Build.BRAND;
        vendor = Build.PRODUCT;
        String com = Build.MANUFACTURER;
        try {
            manufacturer = URLEncoder.encode(com, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            manufacturer = "unknown";
        }


    }

    public void init(int profileId, int episodeId, int langIndex) {
        String lang;
        int cur = langIndex % 2;
        if (cur == 1) {
            lang = PlayUrlInfo.ENGLISH;
        } else {
            lang = PlayUrlInfo.CHINESE;
        }

        this.init(profileId, episodeId, lang);
    }

    public void init(int profileId, int episodeId, String audioLang) {
        this.profileId = profileId;
        this.contentId = episodeId;
        this.audioLang = audioLang;
        session = UUID.randomUUID().toString();


    }

    public void genSession() {
        session = UUID.randomUUID().toString();
    }


    public boolean hasSession() {
        return !StringUtils.isEmpty(session);
    }

    public void clear() {
        synchronized (this) {
            totalTime = 0;
            playTime = 0;
            userPauseTime = 0;
            bufferingPauseTime = 0;
            seekPauseTime = 0;
            userPauses = 0;
            bufferingPauses = 0;
            seekForward = 0;
            seekBackward = 0;
        }
    }


    /**
     * 获取系统版本好
     */
    private void initSystemVer() {
        switch (Build.VERSION.SDK_INT) {
            case 14:
                osVersion = "android4.0";
                break;
            case 15:
                osVersion = "android4.0.3";
                break;
            case 16:
                osVersion = "android4.1";
                break;
            case 17:
                osVersion = "android4.2";
                break;
            case 18:
                osVersion = "android4.3";
                break;
            case 19:
            case 20:
                osVersion = "android4.4";
                break;
            case 21:
                osVersion = "android5.0";
                break;
            case 22:
                osVersion = "android5.1";
                break;
            case 23:
                osVersion = "android6.0";
                break;
            default:
                osVersion = "unknown";
                break;
        }

    }


    public void addTotalTime(int time) {
        totalTime += time;
    }

    public void addPlayTime(int time) {
        playTime += time;
    }


    public void playStart() {
        startLatency = 0;
        curPlayTime = System.currentTimeMillis();
    }

    public void playPrepared() {
        int temp = (int) (System.currentTimeMillis() - curPlayTime) / 1000;
        if (temp < 60 && temp > 0) {
            startLatency = temp;
        } else {
            startLatency = 0;
        }


    }


    /**
     * 缓冲开始
     */
    public void loadingStart() {
        loadingTime = System.currentTimeMillis();
    }

    /**
     * 缓冲结束
     */
    public void loadingEnd() {
        long sum = System.currentTimeMillis() - loadingTime;
        if (sum > 0) {
            bufferingPauseTime += sum;
        }
    }


    public void pauseStart() {
        pauseTime = System.currentTimeMillis();
    }

    public void pauseEnd() {
        long sum = System.currentTimeMillis() - pauseTime;
        if (sum > 0) {
            userPauseTime += sum;
        }
    }


    /**
     * 用户主动seek开始
     */
    public void seekPauseStart() {
        seekTime = System.currentTimeMillis();
    }

    /**
     * 由于seek造成的缓存结束
     */
    public void seekPauseEnd() {
        int sum = (int) (System.currentTimeMillis() - seekTime) / 1000;
        if (sum < 10 && sum > 0) {
            seekPauseTime += sum;
        }


    }


    public void userPauseTimes() {
        userPauses++;
    }


    /**
     * 由于缓存造成的播放暂停
     */
    public void pauseTimesByLoading() {
        bufferingPauses++;
    }


    /**
     * 用户快进
     */
    public void userSeekForward() {
        long time = System.currentTimeMillis() - curForwardTime;
        if (time > 1000) {
            curForwardTime = System.currentTimeMillis();
            seekForward++;
        }


    }

    /**
     * 用户快退
     */
    public void userSeekBackward() {

        long time = System.currentTimeMillis() - curBackwardTime;
        if (time > 1000) {
            curBackwardTime = System.currentTimeMillis();
            seekBackward++;
        }

    }

    public void startReqUrlTime() {
        getUrlTime = System.currentTimeMillis();
    }

    public void setReqUrlTime() {
        getUrlTime = (System.currentTimeMillis() - getUrlTime) / 1000;
    }

    public void setPlayUrl(String url) {
        if (url.contains("http://")) {
            String params = url.replace("http://", "").trim();
            String[] strs = params.split("/");
            if (strs != null && strs.length > 0) {
                cdn = strs[0];
            }

        }

    }


    public void report() {
        if (session == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(AppConfig.REPORT_HOST).append('?');
        sb.append('&').append("session").append('=').append(session);
        sb.append('&').append("contentId").append('=').append(contentId);
        sb.append('&').append("accountId").append('=').append(accountId);
        sb.append('&').append("profileId").append('=').append(profileId);
        sb.append('&').append("audioLang").append('=').append(audioLang);
        sb.append('&').append("model").append('=').append(model);
        sb.append('&').append("appVersion").append('=').append(appVersion);
        sb.append('&').append("resolution").append('=').append(resolution);
        sb.append('&').append("version").append('=').append(version);
        sb.append('&').append("osVersion").append('=').append(osVersion);
        sb.append('&').append("macAddr").append('=').append(macAddr);
        sb.append('&').append("brand").append('=').append(brand);
        sb.append('&').append("vendor").append('=').append(vendor);
        sb.append('&').append("manufacturer").append('=').append(manufacturer);


        if (totalTime < playTime) {
            totalTime = playTime;
        }

        sb.append('&').append("totalTime").append('=').append(totalTime / 1000);
        sb.append('&').append("playTime").append('=').append(playTime / 1000);
        sb.append('&').append("startLatency").append('=').append(startLatency);

        if (mContext instanceof FreePlayingActivity) {
            if (((FreePlayingActivity) mContext).isPlayingPause()) {
                pauseEnd();
                pauseStart();
            }
        } else if (mContext instanceof SmartPlayingActivity) {
            if (((SmartPlayingActivity) mContext).isPlayingPause()) {
                pauseEnd();
                pauseStart();
            }
        }

        //由于用户快进，出现卡顿时间特别短，卡顿次数特别多
        if (bufferingPauseTime < 1000 && bufferingPauses > 10) {
            bufferingPauses = 1;
        }

        sb.append('&').append("userPauseTime").append('=').append(userPauseTime / 1000);
        sb.append('&').append("bufferingPauseTime").append('=').append(bufferingPauseTime / 1000);
        sb.append('&').append("seekPauseTime").append('=').append(seekPauseTime);
        sb.append('&').append("userPauses").append('=').append(userPauses);
        sb.append('&').append("bufferingPauses").append('=').append(bufferingPauses);
        sb.append('&').append("seekForward").append('=').append(seekForward);
        sb.append('&').append("seekBackward").append('=').append(seekBackward);


        if (getUrlTime > 60) {
            getUrlTime = 1;
        }

        sb.append('&').append("getUrlTime").append('=').append(getUrlTime);
        sb.append('&').append("cdn").append('=').append(cdn);


        sb.append('&').append("promoterCode").append('=').append(promoterCode);
        sb.append('&').append("serial").append('=').append(serial);
        sb.append('&').append("deviceDRMId").append('=').append(deviceDRMId);

        String url = sb.toString();

        String msg = "accountId" + accountId
                + "& bufferingPauseTime=" + bufferingPauseTime
                + "& session=" + session
                + " & totalTime = " + totalTime / 1000
                + " & playTime = " + playTime / 1000
                + " & bufferingPauses = " + bufferingPauses;
        Log.v("colin", msg);
        if (AppConfig.debug) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }

        HttpConnector.httpGet(url, new IGetListener() {
            @Override
            public void httpReqResult(final String response) {
                Log.v(TAG, "PlayerReportModel = " + response);
            }
        });

        session = null;

    }

}
