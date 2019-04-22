package com.taku.safe.config;

import android.os.Environment;

import java.io.File;

public class FileConfig {

    public static final String HuXinPath = "/TaKu";
    public static final String FilePaths = HuXinPath + "/File/";
    public static final String PicPaths = HuXinPath + "/Pic/";
    public static final String VideoPaths = HuXinPath + "/Video/";
    public static final String LogPaths = HuXinPath + "/Log/";
    public static final String CrashPaths = HuXinPath + "/Crash/";
    public static final String AudioPaths = HuXinPath + "/Audio/";
    public static final String InfoPaths = HuXinPath + "/Info/";
    public static final String GlideCachePaths = HuXinPath + "/GlideCache/";

    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HuXin 全路径
     *
     * @return
     */
    public static String getHuXinCachePath() {
        if (!isCanUseSD()) {
            return null;
        }
        String path = Environment.getExternalStorageDirectory().getPath() + HuXinPath;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }

    /**
     * Apk下载路径
     *
     * @return
     * @author
     * @date 2017年1月17日
     */
    public static String getFileDownLoadPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + FilePaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }

    /**
     * 视频文件下载路径
     *
     * @return
     * @author
     * @date 2017年1月17日
     */
    public static String getVideoDownLoadPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + VideoPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }

    /**
     * 获取音频路径
     *
     * @return
     */
    public static String getAudioDownLoadPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + AudioPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }


    /**
     * 图片下载路径
     *
     * @return
     * @author
     * @date 2017年1月17日
     */
    public static String getPicDownLoadPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + PicPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }


    /**
     * 异常数据路径
     *
     * @return
     * @author
     * @date 2016年8月19日
     */
    public static String getExceptionPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + CrashPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }


    /**
     * 获取日志文件路径
     *
     * @return
     */
    public static String getLogPaths() {
        String path = Environment.getExternalStorageDirectory().getPath() + LogPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }


    /**
     * 获取设备信息
     *
     * @return
     * @author
     * @date 2017年1月17日
     */
    public static String getInfoPaths() {
        String path = Environment.getExternalStorageDirectory().getPath() + InfoPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }


    /**
     * 手机Glide缓存图片SD卡路径
     *
     * @return
     * @author
     * @date 2017年1月17日
     */
    public static String getGlideCachePath() {
        String path = Environment.getExternalStorageDirectory().getPath() + GlideCachePaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }

}
