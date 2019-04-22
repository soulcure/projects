package com.applidium.nickelodeon.uitls;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.applidium.nickelodeon.NickelFragmentActivity;
import com.applidium.nickelodeon.dialog.LoadingDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.ActionDialog;
import com.applidium.nickelodeon.dialog.BuyVipDialog;
import com.applidium.nickelodeon.dialog.PlayPauseDialog;
import com.applidium.nickelodeon.entity.ActionRequest;
import com.applidium.nickelodeon.entity.ActionResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    static AppUtils utils;

    public static AppUtils getInstance() {
        if (null == utils) {
            utils = new AppUtils();
        }
        return utils;
    }

//    private AppUtils() {
//        throw new AssertionError();
//    }

    /**
     * 获取当前应用的版本号
     *
     * @param context
     * @return String
     */
    public static String getVersion(Context context) {
        String packageName = context.getPackageName();

        return getAppVersion(context, packageName);

    }

    /**
     * 获取指定包名的应用版本号
     *
     * @param context
     * @param packageName
     * @return String
     */
    private static String getAppVersion(Context context, String packageName) {
        String ver = null;

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            ver = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ver;
    }

    /**
     * 安装APK
     */
    public static void installApk(Context context, String apkFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 写入SharedPreferences数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setStringSharedPreferences(Context context, String key,
                                                  String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取SharedPreferences数据
     *
     * @return String value
     */
    public static String getStringSharedPreferences(Context context,
                                                    String key, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }

    /**
     * 写入SharedPreferences数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setBooleanSharedPreferences(Context context, String key,
                                                   boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 读取SharedPreferences数据
     *
     * @return boolean value
     */
    public static boolean getBooleanSharedPreferences(Context context,
                                                      String key, boolean defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, defaultValue);
    }

    /**
     * 写入SharedPreferences数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setIntSharedPreferences(Context context, String key,
                                               int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 读取SharedPreferences数据
     *
     * @return int value
     */
    public static int getIntSharedPreferences(Context context, String key,
                                              int defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defaultValue);
    }

    /**
     * 写入SharedPreferences数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setLongSharedPreferences(Context context, String key,
                                                long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 读取SharedPreferences数据
     *
     * @return long value
     */
    public static long getLongSharedPreferences(Context context, String key,
                                                long defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConfig.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getLong(key, defaultValue);
    }

    /**
     * 网络是否连通
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (nInfo != null) {
                    if (nInfo.isConnected()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (nInfo != null) {
                    return nInfo.getTypeName().toUpperCase(Locale.US)
                            .equals("WIFI");
                }
            }
        } catch (Exception e) {

        }
        return false;
    }


    /**
     * bytes to kb
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024);
        float returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "  KB ");
    }


    /**
     * bytes to mb
     *
     * @param bytes
     * @return
     */
    public static String bytes2mb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "  MB ");
    }


    /**
     * 是否有外置sdcard
     *
     * @return
     */
    public static boolean isExtSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡的路径
     */
    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public static String getExtSDCardPath() {
        String result = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("sdcard1")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        result = path;
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return result;
    }


    /**
     * 本地sdcard上是否有视频资源
     *
     * @param netPath
     * @return
     */
    public static String parseLocalPath(String netPath) {
        String result = null;

        String[] strs = netPath.split("serie");
        if (strs != null && strs.length == 2) {
            String ext_sdcard = getExtSDCardPath();
            if (!StringUtils.isEmpty(ext_sdcard)) {
                String video = ext_sdcard + "/serie" + strs[1];
                File file = new File(video);
                if (file.isFile()) {
                    result = video;
                }
            }
        }
        return result;
    }


    /**
     * md5验证
     *
     * @param file 文件
     * @param md5  md5验证码
     * @return
     */

    public static boolean checkFileMd5(File file, String md5) {
        boolean flag = false;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = fis.read(b)) != -1) {
                md.update(b, 0, len);
            }

            if (md5(md).equals(md5)) {
                flag = true;
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 获得md5验证码
     *
     * @param md5
     * @return
     */
    public static synchronized String md5(MessageDigest md5) {
        StringBuffer strBuf = new StringBuffer();
        byte[] result16 = md5.digest();
        char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                'b', 'c', 'd', 'e', 'f'};
        for (int i = 0; i < result16.length; i++) {
            char[] c = new char[2];
            c[0] = digit[result16[i] >>> 4 & 0x0f];
            c[1] = digit[result16[i] & 0x0f];
            strBuf.append(c);
        }

        return strBuf.toString();
    }

    public static String md5(String string) {
        if (StringUtils.isEmpty(string)) {
            return "";
        }
        try {
            return getMD5(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private static String getMD5(byte[] source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            StringBuffer result = new StringBuffer();
            for (byte b : md5.digest(source)) {
                result.append(Integer.toHexString((b & 0xf0) >>> 4));
                result.append(Integer.toHexString(b & 0x0f));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 自适应式从图片获取Drawable，类似于把图片放入drawable目录一样
     *
     * @param context
     * @param pngFile
     * @param density
     * @return
     */
    public static Drawable getDrawableFromFile(Context context, File pngFile,
                                               int density) {
        Bitmap bmp = BitmapFactory.decodeFile(pngFile.getPath());
        if (bmp != null)
            bmp.setDensity(density);

        return new BitmapDrawable(context.getResources(), bmp);
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawableFromFile(File pngFile) {

        return Drawable.createFromPath(pngFile.getPath());
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawableFromFile(String filePath) {

        return Drawable.createFromPath(filePath);
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is).copy(
                    Bitmap.Config.ARGB_8888, true);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    /**
     * 指定编码获取properties文件中的属性值（解决中文乱码问题）
     *
     * @param properties java.util.Properties
     * @param key        属性key
     * @return
     */
    public static String getProperty(Properties properties, String key, String encoding)
            throws UnsupportedEncodingException {
        //param check
        if (properties == null)
            return null;

        //如果此时value是中文，则应该是乱码
        String value = properties.getProperty(key);
        if (value == null)
            return null;

        //编码转换，从ISO8859-1转向指定编码
        value = new String(value.getBytes("ISO8859-1"), encoding);
        return value;
    }


    public static boolean isMobileNum(String num) {
        return Pattern.compile("^1[34578]\\d{9}$").matcher(num).matches();
    }

    public static boolean checkPassWord(String pass) {
        CharSequence inputStr = pass;
        if (pass == null || pass.equals("")) {
            return false;
        }
        if (!Pattern.compile("^([\\w!@#$%^*?]){6,20}$").matcher(inputStr)
                .matches()) {
            return false;
        }

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.find()) {
            return false;
        }
        return true;
    }

    public static boolean isSmsCode(String num) {
        return Pattern.compile("\\d{6}").matcher(num).matches();
    }

    /**
     * @param context
     * @param title
     * @param message
     * @param indeterminate
     * @param cancelable
     * @return
     */
    public static ProgressDialog showProgress(Context context, CharSequence title,
                                              CharSequence message, boolean indeterminate,
                                              boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }


    /**
     * 检查是否重复按键
     *
     * @return
     */
    private static long curClickTime;

    public static boolean isRepeatClick(int time) {
        boolean res = false;
        long click = System.currentTimeMillis();
        if (click - curClickTime < time) {
            res = true;
        }
        curClickTime = click;
        return res;
    }


    /**
     * 同步智能推荐剧集保存状态中的收藏剧集状态
     *
     * @param context
     * @param episodeId
     * @param isFavorite
     */
    public static void saveSmartPlayStatus(Context context, int episodeId, boolean isFavorite) {

        String record = AppUtils.getStringSharedPreferences(context, "PLAY_RECORD", "");
        if (!StringUtils.isEmpty(record)) {
            String[] strs = record.split("#");
            if (strs != null && strs.length == 4) {
                if (episodeId == Integer.parseInt(strs[0])
                        && isFavorite != Boolean.parseBoolean(strs[2])) {

                    int lastPos = Integer.parseInt(strs[1]);
                    int langIndex = Integer.parseInt(strs[3]);

                    AppUtils.setStringSharedPreferences(context, "PLAY_RECORD",
                            episodeId + "#" + lastPos + "#" + isFavorite + "#" + langIndex);
                }

            }

        }
    }


    public static void showNetworkError(final Context context) {
        // 网络连接不上的提示操作
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)// 屏蔽返回键
                .setTitle(context.getString(R.string.prompt))
                .setMessage(context.getString(R.string.check_connect));
        //String company = Build.MANUFACTURER;
        //setting network在konka tv上崩溃
        if (!ScreenUtils.isTv(context)) {
            builder.setPositiveButton(context.getString(R.string.setting_network),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            arg0.dismiss();

                        }
                    });
        }
        builder.setNegativeButton(context.getString(R.string.empress_retry),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        arg0.dismiss();
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                });
        builder.show();
    }


    public static CharSequence setHighLightText(String content, int color, int start, int end) {

        SpannableString msp = new SpannableString(content);
        msp.setSpan(new ForegroundColorSpan(color), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    public static boolean isTopActiviy(Context context, String cmdName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if (null != runningTaskInfos) {
            cmpNameTemp = (runningTaskInfos.get(0).topActivity).getClassName();
        }
        if (null == cmpNameTemp) return false;
        return cmpNameTemp.equals(cmdName);
    }


    /**
     * 获取总的接受字节数，包含Mobile和WiFi等
     *
     * @return KB
     */
    public static long getUidRxBytes(Context context) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        long rxBytes = TrafficStats.getTotalRxBytes();
        return TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0 : (rxBytes / 1024);
    }


    //获取促销信息图
    public static final String INDEX = "index";
    public static final String PLAYER = "player";
    public static final String LANG_CN = "zh-cn";
    public static final String LANG_EN = "en-gb";
    private ActionDialog advDialog;

    /**
     * 获取引导下载图
     * 首页
     */
    public void reqAction(final String location, final Activity activity) {
        String url = AppConfig.APP_ACTION;
        ActionRequest request = new ActionRequest();
        String token = ((MNJApplication) (activity.getApplication())).getToken();
        request.setToken(token);
        request.setLocation(location);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                final ActionResponse resp = GsonUtil.parse(response,
                        ActionResponse.class);
                if (resp != null && resp.isSucess()) {
                    String url = resp.getImgUrl();

                    if (AppUtils.isTopActiviy(activity, NickelFragmentActivity.class.getName())
                            || !StringUtils.isEmpty(url)) {
                        Glide.with(activity).load(url).asBitmap()
                                .into(new SimpleTarget<Bitmap>(1227, 930) {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                            glideAnimation) {
                                        if (null != advDialog && advDialog.isShowing()) {
                                            advDialog.dismiss();
                                        }
                                        if (location.equals(INDEX)) {
                                            advDialog = new ActionDialog(activity, resp, resource);
                                            advDialog.show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private PlayPauseDialog playDialog;


    /**
     * 获取引导下载图
     * 播放暂停
     */
    public void reqAction(BuyVipDialog vipDialog, final Activity activity, final PlayPauseDialog.onDismissListener listener) {

        if (null != vipDialog && vipDialog.isShowing()) {
            return;
        }
        String url = AppConfig.APP_ACTION;
        ActionRequest request = new ActionRequest();
        String token = ((MNJApplication) (activity.getApplication())).getToken();
        request.setToken(token);
        request.setLocation(PLAYER);
        request.setLang(LANG_CN);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                final ActionResponse resp = GsonUtil.parse(response,
                        ActionResponse.class);
                if (resp != null && resp.isSucess()) {
                    String url = resp.getImgUrl();

                    if (AppUtils.isTopActiviy(activity, NickelFragmentActivity.class.getName())
                            || !StringUtils.isEmpty(url)) {
                        Glide.with(activity).load(url).asBitmap()
                                .into(new SimpleTarget<Bitmap>(1227, 930) {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                            glideAnimation) {
                                        if (null != playDialog && playDialog.isShowing()) {
                                            playDialog.dismiss();
                                        }
                                        playDialog = new PlayPauseDialog(activity, resource);
                                        playDialog.setOnDismissListener(listener);
                                        playDialog.show();
                                    }
                                });
                    }
                }
            }
        });
    }

    private static LoadingDialog mLoadingDialog = null;


    /**
     * 显示loading界面
     *
     * @param context
     */
    public static void showLoadingDialog(Context context) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(context);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }


    /**
     * 显示loading界面
     *
     * @param context
     * @param msg
     */
    public static void showLoadingDialog(Context context, String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(context, msg);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }


    /**
     * 隐藏loading界面
     */
    public static void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }


}
