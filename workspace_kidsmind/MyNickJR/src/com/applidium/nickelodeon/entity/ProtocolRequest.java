package com.applidium.nickelodeon.entity;

import android.content.Context;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ProtocolRequest {

    protected String promoter; //渠道号
    protected String publishId; //合作商，分配给第三方集成的唯一标志, 可传kidsmind
    protected AppConfig.Client client;
    protected String appVersion; //应用版本号，格式如1.1.24或1.0.2
    protected String lang;  //语言，默认“zh-cn”
    protected String deviceDRMId;  //设备编号
    protected String deviceModel;  // 设备型号


    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


    public ProtocolRequest() {
        Context context = AppConfig.AppContext;
        appVersion = AppUtils.getVersion(context);
        client = AppConfig.Client.android;
        MNJApplication app = (MNJApplication) context;
        promoter = app.getProperty("ChannelNo");
        publishId = AppConfig.PARTNER;
        lang = AppConfig.LANG;
        deviceDRMId = AppConfig.getDeviceDRMId(context);
        deviceModel = android.os.Build.MODEL;
    }


    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
