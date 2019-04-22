package com.taku.safe.utils;

import android.content.Context;

/**
 * Created by Administrator on 2016/12/20.
 */

public class LanguageUtil {


    /**
     * 判断语言是否为中文
     *
     * @param context
     * @return
     */
    public static boolean isCN(Context context) {
        boolean res = false;
        try {
            if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                res = true;
            }
        } catch (Exception e) {

        }
        return res;
    }


    /**
     * 判断语言是否为英文
     *
     * @param context
     * @return
     */
    public static boolean isEN(Context context) {
        boolean res = false;
        try {
            if (context.getResources().getConfiguration().locale.getCountry().equals("EN")) {
                res = true;
            }
        } catch (Exception e) {

        }
        return res;
    }


    /**
     * 获取当前语言
     *
     * @param context
     * @return
     */
    public static String getLang(Context context) {
        String res;
        try {
            res = context.getResources().getConfiguration().locale.getCountry().toLowerCase();
        } catch (Exception e) {
            res = null;
        }
        return res;
    }
}