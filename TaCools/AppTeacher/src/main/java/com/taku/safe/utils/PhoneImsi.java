package com.taku.safe.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Administrator on 2016/12/9.
 */

public class PhoneImsi {

    // IMSI = MCC + MNC + MSIN
    public static String getMCC(Context context) {
        String res = "";
        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telManager.getSubscriberId();
            if (imsi != null && imsi.length() > 3) {
                res = imsi.substring(0, 3);
            }
        } catch (Exception e) {
            res = "";
        }
        return res;
    }

    public static String getMNC(Context context) {
        String res = "";
        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telManager.getSubscriberId();
            if (imsi != null && imsi.length() > 5) {
                return imsi.substring(3, 5);
            }
        } catch (Exception e) {
            res = "";
        }
        return res;
    }
}