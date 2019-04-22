package com.login.utils;


public class HostAddressPool {
	// private static HostAddressPool _instance = null;
	//
	// public static HostAddressPool getInstance() {
	// if (_instance == null) {
	// _instance = new HostAddressPool();
	// }
	// return _instance;
	// }
	//
	// //
	// private Properties _hostPool;
	// private static final String hostAddressFile = "HostAddress.ads";
	//
	// public void loadHost(final Context con) {
	// Properties pre = new Properties();
	// try {
	// pre.load(con.getAssets().open(hostAddressFile));
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// public String getHostAddress(final String key) {
	// if (_hostPool == null) {
	// return null;
	// }
	// final String value = _hostPool.getProperty(key);
	// return value.trim();
	// }

	// 通常使用的名游配置地址
	// =============================================================================================================================
	public static final String API = "http://api3.139game.net";// http://api.139game.com

	public static final String UPDATE = "http://update3.139game.net";// http://update.139game.com

	public static final String UPDATE1 = UPDATE; // http://update1.139game.com

	public static final String HOST = "http://qpwap3.139game.net"; // http://qpwap.cmgame.com

	public final static String FIRST_HOST = "http://iplist3.139game.net/"; // http://iplist.139game.net/

	public final static String SECOND_HOST = FIRST_HOST;
	// "http://115.29.232.38:55559/"; //http://iplist.dg668.net/
	/** 帐号注册地址 **/
	public final static String resHost = API
			+ "/request/alone/jumpapi.php";

	/** 版本检测地址 **/
	// public final static String MY_HOST = UPDATE +
	// "/android/androidconfig.php";

	/** http登录地址请求 **/
	public final static String httpLoginReq = HOST
			+ "/get_third_entry.php";

	public final static String NOTICE_HOST = "http://notice3.139game.net"; // http://notice.139game.net

	public final static String imgUrl = "http://image3.139game.net"; // http://image.139game.net

	public static final String WAP_HOST = "http://wap3.139game.net"; // http://wap.139game.net

	public static final String M_139 = "http://wap3.139game.net"; // http://m.139game.com

	public final static String aliMatchUrl = WAP_HOST + "/rank.php";

	/** 约战邀请推广地址 */
	public static final String DOWNLOADPATH = "http://g.10086.cn/gamecms/go/qpds";

	/** 修改账号密码 */
	public static final String MODIFY_URL = "http://game.10086.cn/home/do.php?ac=lostpasswd";

	// public static final String BIND_URL =
	// "http://192.168.1.186:18765/user.login";

	/** 消息推送URL */
	public static final String MSG_URL= "http://push.qwjj.cn/request/api.php"; // http://push.139game.com
	/**美女视频*/
	public static String MMVIDEO_DIAMOND_URL = "http://qpgame3.139game.net";
	// =============================================================================================================================




	//
}
