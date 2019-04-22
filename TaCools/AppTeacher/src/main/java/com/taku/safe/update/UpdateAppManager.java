package com.taku.safe.update;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.taku.safe.TakuApp;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.HttpConnector;
import com.taku.safe.protocol.respond.RespAppUpdate;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.GsonUtil;

import ezy.boost.update.ICheckAgent;
import ezy.boost.update.IUpdateChecker;
import ezy.boost.update.IUpdateParser;
import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;

/**
 * Created by soulcure on 2017/11/5.
 */

public class UpdateAppManager {

    private static UpdateAppManager instance;

    private String updateInfo;
    private boolean hasUpdate;

    private UpdateAppManager() {

    }

    public static UpdateAppManager instance() {
        if (instance == null) {
            instance = new UpdateAppManager();
        }
        return instance;
    }

    /**
     * @param context
     * @param notifyId
     */
    public void check(final Context context, final int notifyId) {
        UpdateManager.create(context)
                .setChecker(new IUpdateChecker() {
                    @Override
                    public void check(ICheckAgent agent, String url) {
                        if (TextUtils.isEmpty(updateInfo)) {
                            updateInfo = appUpdateInfo(context, url);
                        }
                        agent.setInfo(updateInfo);
                    }
                })
                .setUrl(AppConfig.COMMON_APP_UPDATE)
                //.setManual(true)//提示低优先级toast
                .setNotifyId(notifyId)
                .setWifiOnly(false)
                .setParser(new IUpdateParser() {
                    @Override
                    public UpdateInfo parse(String source) throws Exception {
                        UpdateInfo info = new UpdateInfo();
                        if (!TextUtils.isEmpty(source)) {
                            RespAppUpdate update = GsonUtil.parse(source, RespAppUpdate.class);
                            if (update != null && update.isSuccess()) {
                                info.hasUpdate = update.isHasUpdate();
                                hasUpdate = update.isHasUpdate();

                                info.updateContent = update.getUpdateContent();
                                //info.versionCode = 587;
                                info.versionName = update.getLatestVersion();
                                info.url = update.getPackageUrl();  //"http://dldir1.qq.com/dmpt/apkSet/qqcomic_android_dm2102.apk"
                                info.md5 = update.getMd5(); //"a12cd8b13348077f598ebae608196761"
                                info.size = update.getSize();
                                info.isForce = update.isForceUpdate();
                                info.isIgnorable = false;  //是否静默下载：有新版本时不提示直接下载
                                info.isSilent = false; //是否可忽略该版本
                            }
                        }
                        return info;
                    }
                }).check();
    }


    /**
     * 获取app update信息
     */
    private static String appUpdateInfo(Context context, String url) {
        //String token = ((TakuApp) context.getApplicationContext()).getToken();
        String appVer = AppUtils.getVersion(context);
        String channel = AppUtils.getChannel(context);
        String platform = "android";

        ContentValues params = new ContentValues();
        params.put("versionName", appVer);
        params.put("channel", channel);
        params.put("platform", platform);

        return HttpConnector.doPost(url, null, params);
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }
}
