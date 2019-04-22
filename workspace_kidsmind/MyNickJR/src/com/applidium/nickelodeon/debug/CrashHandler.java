package com.applidium.nickelodeon.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.DeviceUtils;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;


public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();


    /**
     * 错误报告文件的扩展名
     */
    public static final String CRASH_REPORTER_EXTENSION = ".log";

    /**
     * CrashHandler实例
     */
    private static CrashHandler instance;

    /**
     * 错误报告文件名中的日期格式
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);

    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 程序的Context对象
     */
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        handleException(ex);

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            Log.v(TAG, "handleException Throwable is null");
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        if (msg == null) {
            return false;
        }

        // 保存错误报告文件
        saveCrashInfoToFile(ex);
        return true;
    }


    /**
     * 保存错误信息到文件中
     */
    private void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        Log.v(TAG, "crash info=" + result);

        printWriter.close();
        try {
            String accountId = ((MNJApplication) mContext.getApplicationContext()).getUserId();
            String fileName = "crash_" + accountId + "_" + dateFormat.format(new Date(System.currentTimeMillis())) + CRASH_REPORTER_EXTENSION;
            FileOutputStream trace = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            trace.write(result.getBytes());
            trace.flush();
            trace.close();
        } catch (Exception e) {
            Log.v(TAG, "an error occured while writing report file!", e);
        }

    }


    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的
     */
    public void sendCrashLogToServer(Context context) {
        String[] crFiles = getCrashLogFiles(context);
        for (String fileName : crFiles) {
            File file = new File(context.getFilesDir(), fileName);
            postReport(file);
        }
    }

    private void postReport(File file) {

        String url = AppConfig.CRASH_LOG_REPORT_HOST;

        Map<String, String> params = new HashMap<String, String>();
        getCrashDeviceInfo(mContext, params); //获取设备的基本信息

        Map<String, File> files = new HashMap<String, File>();

        files.put(file.getName(), file);

        if (file.getName().contains("crash")) {
            params.put("type", "crash");//崩溃错误
        } else {
            params.put("type", "network");//网络播放错误
        }

        HttpConnector.httpPostFile(url, params, files, new IPostListener() {
            @Override
            public void httpReqResult(String response) {

            }
        });

    }


    /**
     * 获取错误报告文件名
     */
    private String[] getCrashLogFiles(Context context) {
        File filesDir = context.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }


    /**
     * 收集程序崩溃的设备信息
     */
    public void getCrashDeviceInfo(Context context, Map<String, String> params) {
        String version = AppUtils.getVersion(context);
        String phoneNum = ((MNJApplication) context.getApplicationContext()).getMoblieNum();
        String token = ((MNJApplication) context.getApplicationContext()).getToken();
        String promoter = ((MNJApplication) context.getApplicationContext()).getPromoter();
        String deviceDRMId = AppConfig.getDeviceDRMId(context);

        String serial = Build.SERIAL;
        String macAddr = DeviceUtils.getLocalMacAddress(context);
        String brand = Build.BRAND;
        String vendor = Build.PRODUCT;

        String manufacturer;
        String com = Build.MANUFACTURER;
        try {
            manufacturer = URLEncoder.encode(com, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            manufacturer = "unknown";
        }

        String model;
        String m = Build.MODEL;
        try {
            model = URLEncoder.encode(m, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            model = "unknown";
        }

        params.put("version", version);  //客户端版本号
        params.put("phoneNum", phoneNum);//用户手机号
        params.put("token", token); //用户token
        params.put("promoter", promoter); //渠道号
        params.put("deviceDRMId", deviceDRMId); //用户设备ID
        params.put("serial", serial); //设备序列号
        params.put("macAddr", macAddr); //设备mac地址
        params.put("brand", brand);//BRAND 运营商
        params.put("vendor", vendor);//生产商
        params.put("manufacturer", manufacturer); //生产厂家
        params.put("model", model);//设备型号
    }


    /**
     * 保存错误信息到文件中
     */
    public void saveVideoSourceErrorToFile(String url) {
        StringBuffer sb = new StringBuffer();
        sb.append('\n');
        sb.append("play video source error & ");
        sb.append("time：").append(dateFormat.format(new Date(System.currentTimeMillis()))).append(" & ");
        sb.append("Url：").append(url);
        sb.append('\n');
        String result = sb.toString();

        try {
            String userId = ((MNJApplication) mContext.getApplicationContext()).getUserId();
            String fileName = "network_" + userId + CRASH_REPORTER_EXTENSION;
            FileOutputStream trace = mContext.openFileOutput(fileName, Context.MODE_APPEND);
            trace.write(result.getBytes());
            trace.flush();
            trace.close();
        } catch (Exception e) {
            Log.v(TAG, "an error occured while writing report file!", e);
        }

    }
}