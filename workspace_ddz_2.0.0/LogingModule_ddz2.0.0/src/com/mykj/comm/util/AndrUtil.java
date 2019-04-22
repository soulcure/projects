package com.mykj.comm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class AndrUtil {
	public static final String PARENT_PATH = "/.mingyouGames";

	public static final String ICONS_PATH = PARENT_PATH + "/icons";

	public static final String APKS_PATH = PARENT_PATH + "/apks";

	private static final String DOWNLOADING_FILE_EXT_NAME = ".apk";

	private static final String TAG="AndrUtil";

	/**
	 * md5验证
	 * 
	 * @param file
	 *            文件
	 * @param expectedMD5
	 *            md5验证码
	 * @return
	 */
	public static synchronized boolean downloadFileMD5Check(File f,
			String expectedMD5) {
		boolean flag = false;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(f);
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = fis.read(b)) != -1) {
				md.update(b, 0, len);
			}

			if (md5(md).equals(expectedMD5)) {
				flag = true;
			}
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
		char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f' };
		for (int i = 0; i < result16.length; i++) {
			char[] c = new char[2];
			c[0] = digit[result16[i] >>> 4 & 0x0f];
			c[1] = digit[result16[i] & 0x0f];
			strBuf.append(c);
		}

		return strBuf.toString();
	}

	/**
	 * 删除存在的文件
	 * 
	 * @param fileName
	 */
	public static void deleteFile(File file) {
		try {
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络是否连通
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context == null) {
			return true;
		}
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
					return nInfo.getTypeName().toUpperCase().equals("WIFI");
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * SD卡是否挂载
	 */
	public static boolean isMediaMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 获取SD卡的路径
	 */
	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取已安装程序的版本
	 */
	public static String getAppVer(Context context, String packageName) {
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
	 * 获取*.apk的版本信息
	 */
	public static String getApkStringVer(Context context, String apkFilePath) {
		String ver = null;

		PackageManager pm = context.getPackageManager();
		PackageInfo pi = pm.getPackageArchiveInfo(apkFilePath,
				PackageManager.GET_ACTIVITIES);
		ver = pi.versionName;

		return ver;
	}

	/**
	 * 从url解析出fileName
	 */
	public static String getFileNameFromUrl(String strUrl) {
		String fileName = null;

		try {
			if (strUrl != null) {
				String[] tmpStrArray = strUrl.split("/");
				fileName = tmpStrArray[tmpStrArray.length - 1];
				if (fileName == null || fileName.trim().length() == 0) {
					fileName = null;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fileName;
	}

	public static String getApkStringVerByFileName(String fileName) {
		String apkVer = null;

		try {
			String s = fileName.toLowerCase();
			int i = s.indexOf("ver");
			i += 3;
			if (i + 5 < s.length()) {
				apkVer = s.substring(i, i + 5);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apkVer;
	}

	/**
	 * 根据字符串，返回Version的实例。 如果字符串不合法，返回null。
	 */
	public static Version getVersionByString(String strVer) {
		Version ver = null;
		try {
			ver = new Version(strVer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ver;
	}

	/**
	 * 根据App的包名，得到其AndroidManifest.xml中的版本属性的值，并返回一个Version实例。
	 */
	public static Version getVersionByAppPackageName(Context context,
			String packageName) {
		String strVer = getAppVer(context, packageName);
		return getVersionByString(strVer);
	}

	/**
	 * 根据apkFileName，在文件系统中查找apk文件的版本，并返回。
	 */
	public static Version getApkVer(Context context, String apkFileName) {
		Version ver = null;

		try {
			if (apkFileName != null) {
				boolean isMediaMounted = isMediaMounted();
				if (isMediaMounted) {
					File apkFile = new File(getSdcardPath() + APKS_PATH,
							apkFileName);
					if (apkFile.exists() && apkFile.isFile()) {
						ver = getVersionByString(getApkStringVer(context,
								apkFile.getPath()));
					} else {
						// 尚未下载完的文件
						File apkTmpFile = new File(getSdcardPath() + APKS_PATH,
								apkFileName + DOWNLOADING_FILE_EXT_NAME);
						if (apkTmpFile.exists() && apkTmpFile.isFile()) {
							ver = getVersionByString(getApkStringVerByFileName(apkTmpFile
									.getName()));
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ver;
	}

	/**
	 * 检测程序是否已经安装
	 * 
	 * @param packageName
	 *            游戏入口Activity所在的包名。不能以“.”结尾
	 * @param activityName
	 *            游戏入口Activity的类名。以“.”开头
	 */
	public static boolean isActivityInstalled(Context context,
			String packageName, String activityName) {
		if (packageName == null || packageName.trim().length() == 0) {
			return false;
		}
		if (activityName == null || activityName.trim().length() == 0) {
			return false;
		}
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
			String s = packageName + activityName;
			for (int i = 0; i < pi.activities.length; i++) {
				ActivityInfo ai = pi.activities[i];
				if (ai.name.equals(s)) {
					return true;
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * 获取已安装程序的图标
	 */
	public static Drawable getIconFromApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packageName, 0);
			return pi.applicationInfo.loadIcon(pm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSDCardDir() {
		String dir = " ";
		try {
			dir = Environment.getExternalStorageDirectory() + "/";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dir;
	}

	public static String getLocalIpAddress() {
		String ipv4="";  
		try {  
			ArrayList<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
			for (NetworkInterface ni: nilist)   
			{  
				ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
				for (InetAddress address: ialist){  
					if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
					{   
						return ipv4;  
					}  
				}  

			}  

		} catch (SocketException e) {  
			ipv4="";
			Log.e(TAG, "getLocalIpAddress error!");
		}  
		return ipv4;  
	}
	
	/**
	 * xml中解析键值对
	 * 
	 * @param strXml
	 * @param tagName
	 * @return
	 */
	public static String parseStatusXml(String strXml, String tagName) {
		String tagStr = "";
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (p.getName().equals(tagName)) {
						tagStr = p.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				default:
					break;
				}
				if (tagStr != null && tagStr.trim().length() > 0) {
					break;
				}
				// 用next方法处理下一个事件，否则会造成死循环。
				eventType = p.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tagStr;
	}

	public static String parseTagValueXml(String strXml, String tagName,
			String valueName) {
		String tagStr = "";
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (p.getName().equals(tagName)) {
						tagStr = p.getAttributeValue("", valueName);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				default:
					break;
				}
				if (tagStr != null && tagStr.trim().length() > 0) {
					break;
				}
				// 用next方法处理下一个事件，否则会造成死循环。
				eventType = p.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tagStr;
	}

}
