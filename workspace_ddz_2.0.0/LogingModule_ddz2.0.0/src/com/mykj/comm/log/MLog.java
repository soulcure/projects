package com.mykj.comm.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

/**
 * 使用该类需要加入以下权限： <uses-permission
 * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 **/
public class MLog {
	private static String log_dir = "/mlog";

	public static int ADNROID_PRINT = 0x01;

	public static int FILE_PRINT = 0x02;

	public static final int CUSTOMLEVEL1 = android.util.Log.ASSERT + 1;
	public static final int CUSTOMLEVEL2 = android.util.Log.ASSERT + 2;
	public static final int CUSTOMLEVEL3 = android.util.Log.ASSERT + 3;

	public static int curPrintLevel = 0;

	// private static File file = null;

	private static FileWriter writer = null;

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"MM-dd HH:mm:ss.SSS");// 时:分:秒:毫秒

	private static SimpleDateFormat predex = new SimpleDateFormat(
			"MM_dd_HH_mm_ss");

	/** 默认是关闭日志 */
	private static boolean isClose = true;

	static int tagPrint = ADNROID_PRINT | FILE_PRINT;

	private static boolean isOne = false;

	private static boolean isAndroidPrint() {
		if ((tagPrint & ADNROID_PRINT) != 0) {
			return true;
		}
		return false;
	}

	private static boolean isFilePrint() {
		if ((tagPrint & FILE_PRINT) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 次方法必须在init方法之前调用才有效
	 * 
	 * @param isOne
	 */
	public static void setCurPrintLevel(int printLevel) {
		curPrintLevel = printLevel;
	}

	/**
	 * @Title: init
	 * @Description: 初始化日志工具
	 * @param printTag
	 *            日志输出类型：控制台|外部文件
	 * @param fileName
	 *            日志文件名
	 * @param isForceOpen
	 *            是否强制打开日志开关（不受配置文件控制）
	 * @throws IOException
	 * @version: 2013-5-8 16:35:09
	 */
	public static void init(int printTag, String fileName, boolean isForceOpen)
			throws IOException {

		initParam(printTag, fileName, isForceOpen);

		if (isFilePrint()) {
			Date date = new Date();
			String filename = fileName + "_" + predex.format(date) + "_.log";
			File file = createFile(Environment.getExternalStorageDirectory()
					+ log_dir + "/" + filename);

			writer = new FileWriter(file, true);

			print(android.util.Log.ERROR, sdf.format(date) + "test开始：\n");
		}

	}

	/**
	 * @Title: init
	 * @Description: 初始化日志工具,提供输出唯一文件日志输出功能
	 * @param printTag
	 * @param fileName
	 * @param isForceOpen
	 * @param isOneFlag
	 *            ，true，输出文件只有一个，false，输出文件多个
	 * @throws IOException
	 */
	public static void init(int printTag, String fileName, boolean isForceOpen,
			boolean isOneFlag) throws IOException {

		initParam(printTag, fileName, isForceOpen);

		if (isFilePrint()) {
			Date date = new Date();
			String filename = fileName + ".log";
			File file = createFile(Environment.getExternalStorageDirectory()
					+ log_dir + "/" + filename);

			writer = new FileWriter(file, true);

			print(android.util.Log.ERROR, sdf.format(date) + "开始：\n");
		}

	}

	/**
	 * 初始化参数
	 * 
	 * @param printTag
	 * @param fileName
	 * @param isForceOpen
	 * @throws IOException
	 */
	private static void initParam(int printTag, String fileName,
			boolean isForceOpen) throws IOException {
		if (isForceOpen) {
			isClose = false;
		} else {
			configLogOut();
		}
		if (isClose) {
			return;
		}
		tagPrint = printTag;

		if (isFilePrint() && fileName == null) {
			throw new IllegalArgumentException("日志需要输出到外部文件，但没有设置文件名");
		}
	}

	/**
	 * 配置日志输出
	 */
	private static void configLogOut() {
		try {
			File fileOpen = new File(Environment.getExternalStorageDirectory()
					+ "/PRIINT_DEBUG_INFO.txt");
			if (fileOpen.exists()) {
				// 如果SD卡根目录存在日志控制文件，则打开日志功能
				isClose = false;
			} else {
				// 不存在日志控制文件，则关闭日志功能
				isClose = true;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 创建日志文件
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private static File createFile(String filename) throws IOException {
		File file = new File(filename);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		close();

		return file;
	}

	private static void print(int tag, String... strings) {
		if (isClose)
			return;
		if (strings == null)
			throw new NullPointerException("MLog print strings is NULL");

		if (tag < curPrintLevel)
			return;
		Date date = new Date();
		try {
			StringBuffer sb = new StringBuffer();
			// sb.append("|"+sdf.format(date)+"|");
			// writer.append("|"+sdf.format(date)+"|");

			for (int i = 1; i < strings.length; i++) {
				if (i != strings.length - 1)
					sb.append(strings[i] + "|");
				// writer.append(strings[i]+"|");
				else
					sb.append(strings[i]);
				// writer.append(strings[i]);
			}
			sb.append("\n");
			if (isFilePrint() && writer != null) {
				writer.append("|" + sdf.format(date) + "|" + strings[0] + "|"
						+ sb.toString());
				writer.flush();
			}
			if (isAndroidPrint()) {
				if (tag >= android.util.Log.ERROR)
					android.util.Log.e(strings[0], sb.toString());
				else if (tag == android.util.Log.DEBUG)
					android.util.Log.d(strings[0], sb.toString());
				else if (tag == android.util.Log.VERBOSE)
					android.util.Log.v(strings[0], sb.toString());
				else if (tag == android.util.Log.WARN)
					android.util.Log.w(strings[0], sb.toString());
				else if (tag == android.util.Log.INFO)
					android.util.Log.i(strings[0], sb.toString());
			}
			// writer.append("\n");

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public static void e(String... strings) {
		print(android.util.Log.ERROR, strings);
	}

	public static void w(String... strings) {
		print(android.util.Log.WARN, strings);
	}

	public static void i(String... strings) {
		print(android.util.Log.INFO, strings);
	}

	public static void d(String... strings) {
		print(android.util.Log.DEBUG, strings);
	}

	public static void v(String... strings) {
		print(android.util.Log.VERBOSE, strings);
	}

	public static void c1(String... strings) {
		print(CUSTOMLEVEL1, strings);
	}

	public static void c2(String... strings) {
		print(CUSTOMLEVEL2, strings);
	}

	public static void c3(String... strings) {
		print(CUSTOMLEVEL3, strings);
	}

	public static void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writer = null;
		}
	}

	public static void printStack(String info) {
		if (isClose)
			return;
		Exception e = new Exception(info);

		if (isFilePrint() && writer != null) {
			e("printStack:", info);
			e.printStackTrace(new PrintWriter(writer));
		}
		if (isAndroidPrint()) {
			e.printStackTrace();
		}
	}

}