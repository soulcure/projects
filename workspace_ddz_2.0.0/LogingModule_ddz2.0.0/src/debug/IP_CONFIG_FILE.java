/**
 * 
 */
package debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mykj.comm.util.AndrUtil;

/**
 * @author Jason
 * 
 */
public class IP_CONFIG_FILE {
	private static final String TAG = "IP_CONFIG_FILE";

	/** 参数间隔符号 **/
	protected static final String SPACE_CHAR = "=";

	protected static final String IP_CONFIG_FILE = "__IP_PORT_CONFIG__1.0.txt";

	/** 是否打开isTCAgent **/
	protected static boolean isTCAgent = false;

	/** 是否断线重回-默认为-true **/
	protected static boolean isReLogin = true;

	/** 是否发穿透流程的开关 --- 默认false */
	protected static boolean isSendProxyInfo = false;

	protected static boolean isOuterNet = true;// true;//false

	/** 是否共享帐号 **/
	protected static boolean isShareAccount = false;

	// 链接的IP(打现网包不需要修改此项)
	protected static String connIP = "192.168.1.186";

	// 链接的端口(打现网包不需要修改此项)
	protected static int connPort = 7000;// 7000;//30000
	
	//日志检测tag
	public static final String MY_DETECTGAMEDATA = "MY_DETECTGAMEDATA";

	public static void setIsShareAccount(boolean bool) {
		isShareAccount = bool;
	}

	public static boolean IsShareAccount() {
		return isShareAccount;
	}

	public static void setIsTCAgent(boolean bool) {
		isTCAgent = bool;
	}

	public static boolean IsTCAgent() {
		return isTCAgent;
	}

	public static void setIsReLogin(boolean bool) {
		isReLogin = bool;
	}

	public static boolean isReLogin() {
		return isReLogin;
	}

	public static void setIsSendProxyInfo(boolean bool) {
		isSendProxyInfo = bool;
	}

	public static boolean isSendProxyInfo() {
		return isSendProxyInfo;
	}

	public static void setIsOuterNet(boolean bool) {
		isOuterNet = bool;
	}

	public static boolean isOuterNet() {
		return isOuterNet;
	}

	public static void setConnectIP(String ip) {
		connIP = ip;
	}

	public static String getConnectIP() {
		return connIP;
	}

	public static void setConnectPort(int port) {
		connPort = port;
	}

	public static int getConnectPort() {
		return connPort;
	}

	// 从配置文件读取IP/端口以及是否可以链接外网的标识
	public static void readIpPortFormConfig() {
		final String IP_KEY = "ip";
		final String PORT_KEY = "port";
		final String OUT_KEY = "isout";
		String filePath = AndrUtil.getSdcardPath();
		File file = new File(filePath, IP_CONFIG_FILE);
		Properties pro = null;
		InputStream is = null;
		if (file.exists()) {
			try {
				pro = new Properties();
				is = new FileInputStream(file);
				pro.load(is);
				connIP = pro.getProperty(IP_KEY);
				connPort = Integer.parseInt(pro.getProperty(PORT_KEY));
				isOuterNet = Boolean.parseBoolean(pro.getProperty(OUT_KEY));
			} catch (Exception e) {
			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
				} catch (IOException e) {
				}
			}
		}
	}

	public static String readPropertyValue(final String key) {
		String filePath = AndrUtil.getSdcardPath();
		File file = new File(filePath, IP_CONFIG_FILE);
		String value = null;
		if (file.exists()) {
			try {
				Properties pro = new Properties();
				InputStream is = new FileInputStream(file);
				pro.load(is);
				value = pro.getProperty(key);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
