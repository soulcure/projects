package com.applidium.nickelodeon.uitls;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    private DeviceUtils() {
        throw new AssertionError();
    }

    /**
     * 获取用于注册的设备序列号
     *
     * @param context
     * @return
     */
    public static String getDeviceDRMId(Context context) {
        int DRM_ID_LEN = 60;
        String PROJECT_NAME = "KMNJ";
        String VENDOR = "AD";
        String MODEL = "MB";

        String mac = getLocalMacAddress(context);

        if (!StringUtils.isEmpty(mac)) {
            mac = mac.replace(":", "");
        } else {
            mac = null;
        }

        String serial = getSerialNumber();
        //添加当serial id获取不到的时候，防止DeviceDRMId冲突
        if (StringUtils.isEmpty(serial)
                || serial.equals("unknown")
                || serial.equals("12345678900")) {
            String android_id = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
            serial = android_id;
        }

        String drmId = PROJECT_NAME + VENDOR + MODEL + mac;
        int serial_len = 0;
        String zero = "";
        if (serial != null) {
            serial_len = serial.length();
        }
        for (int i = 0; i < DRM_ID_LEN - drmId.length() - serial_len; i++) {
            zero += "0";
        }
        drmId = drmId + zero + serial;

        return drmId;
    }


    /**
     * 获取设备的 ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return "unknown";
    }


    /**
     * 获取设备的mac地址
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取设备序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * 获取手机IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String imsi = null;
        try {
            TelephonyManager phoneManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi = phoneManager.getSubscriberId();
            Log.v(TAG, imsi);
        } catch (Exception e) {
            Log.e(TAG, "getIMSI error!");
            imsi = "";
        }

        if (imsi == null) {
            imsi = "";
        }
        return imsi;
    }

    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = null;
        try {
            TelephonyManager phoneManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = phoneManager.getDeviceId();
        } catch (Exception e) {
            Log.e(TAG, "getIMEI error!");
            imei = "";
        }
        if (imei == null) {
            imei = "";
        }
        return imei;
    }

    /**
     * 获取iccid SIM卡序列号
     *
     * @param context
     * @return
     */
    public static String getICCID(Context context) {
        String iccid = "";
        try {
            TelephonyManager phoneManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            iccid = phoneManager.getSimSerialNumber();
        } catch (Exception e) {
            Log.e(TAG, "getIMEI error!");
            iccid = "";
        }
        if (iccid == null) {
            iccid = "";
        }
        return iccid;
    }

}
