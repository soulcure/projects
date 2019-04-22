package com.ivmall.android.app.entity;

import android.content.Context;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class ProtocolRequest {


    protected String appVersion; //应用版本号，格式如1.1.24或1.0.2

    protected AppConfig.Client client;

    protected String promoter; //渠道号

    protected String partner;  //合作商，分配给第三方集成的唯一标志, 可传kidsmind

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    protected String lang;  //语言，默认“zh-cn”

    protected String deviceDRMId;  //设备编号

    protected String deviceModel;  // 设备型号

    protected AppConfig.DeviceType operateFrom;  //控制端区分

    public ProtocolRequest() {
        Context context = AppConfig.AppContext;
        appVersion = AppUtils.getVersion(context);
        client = AppConfig.Client.stb;  //代表电视
        operateFrom = AppConfig.DeviceType.tv; //代表控制端

        KidsMindApplication app = (KidsMindApplication) context;
        promoter = app.getProperty("ChannelNo");
        partner = AppConfig.PARTNER;
        lang = AppConfig.LANG;
        deviceDRMId = AppConfig.getDeviceDRMId(context);
        deviceModel = android.os.Build.MODEL;
    }


    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
