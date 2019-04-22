package com.ivmall.android.app.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ivmall.android.app.KidsMindApplication;

/**
 * Created by Markry on 2015/10/20.
 */
public   class Mp3Receiver extends BroadcastReceiver{
    private KidsMindApplication application;
    private NotificationManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {
        application= (KidsMindApplication) context.getApplicationContext();
        manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String ctrl_code = intent.getAction();
        if ("playOrPause".equals(ctrl_code)) {
            application.startOrPlay();
        }  else if ("next".equals(ctrl_code)) {
            application.mpNext();
        } else if ("last".equals(ctrl_code)) {
            application.mpPrevious();
        }
        if ("cancel".equals(ctrl_code)) {
            application.getMpPlay().stop();
            manager.cancel(application.MP3_NOFA_ID);
        }
    }
}
