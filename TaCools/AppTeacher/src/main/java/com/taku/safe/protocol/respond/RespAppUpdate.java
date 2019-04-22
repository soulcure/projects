package com.taku.safe.protocol.respond;

/**
 * Created by colin on 2017/6/6.
 */

public class RespAppUpdate extends RespBaseBean {

    private int hasUpdate;  // 是否有新版本
    private long size;   // apk 文件大小
    private String latestVersion;   //更新版本号
    private String packageUrl;  //android APK下载地址  or  IOS appstore 页面地址
    private String updateTime; //最近更新时间
    private int forceUpdate;  // 是否强制安装：不安装无法使用app
    private String updateContent; //更新内容描述 （长文本 支持 \n）
    private String md5; // apk 文件 md5码


    public boolean isHasUpdate() {
        return hasUpdate == 1;
    }

    public void setHasUpdate(int hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getLatestVersion() {
        return "V" + latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isForceUpdate() {
        return forceUpdate == 1;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
