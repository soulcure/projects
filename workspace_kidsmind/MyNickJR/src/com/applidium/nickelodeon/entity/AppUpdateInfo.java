package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.MNJApplication;

/**
 * Created by colin on 2015/5/25.
 */
public class AppUpdateInfo {

    private String upgradeUrl;//
    private String description;//
    private String appVersion;//
    private String checksum;

    private MNJApplication.Severity severity; //


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

    public MNJApplication.Severity getSeverity() {
        return severity;
    }
}
