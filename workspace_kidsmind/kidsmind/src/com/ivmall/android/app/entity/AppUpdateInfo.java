package com.ivmall.android.app.entity;

import com.ivmall.android.app.KidsMindApplication;

/**
 * Created by colin on 2015/5/25.
 */
public class AppUpdateInfo {

    private String upgradeUrl;//
    private String description;//
    private String appVersion;//
    private String checksum;

    private KidsMindApplication.Severity severity; //


    public String getUpgradeUrl() {
        return upgradeUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getChecksum() {
        return checksum;
    }

    public KidsMindApplication.Severity getSeverity() {
        return severity;
    }
}
