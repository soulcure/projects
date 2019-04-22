/**
 * 
 */
package com.mingyou.login;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mingyou.community.Community;

/**
 * @author Jason
 * 
 */
public class LoginUtil {

	public static final String WIFI_IPLIST = "WIFI_IPLIST";

	private static final String CMWAP_IPLIST = "CMWAP_IPLIST";

	private static final String CMNET_IPLIST = "CMNET_IPLIST";

	private static final String CTWAP_IPLIST = "CTWAP_IPLIST";

	private static final String CTNET_IPLIST = "CTNET_IPLIST";

	private static final String UNWAP_IPLIST = "UNWAP_IPLIST";

	private static final String UNNET_IPLIST = "UNNET_IPLIST";

	private static final String DEFAULT_IPLIST = "DEFAULT_IPLIST";

	/** 未知 --要同web定义保存一致 */
	public static final int UNKNOW_TYPE = 0;

	/** 移动 --要同web定义保存一致 */
	public static final int MOVE_MOBILE_TYPE = 1;

	/** 联通 --要同web定义保存一致 */
	public static final int UNICOM_TYPE = 2;

	/** 电信 --要同web定义保存一致 */
	public static final int TELECOM_TYPE = 3;

	/**
	 * sim卡信息：MCC(移动国家码，中国460)+MNC(移动网络码)+MSIN (有10位EF+M0M1M2M3+ABCD )
	 * 中国移动系统使用00
	 * 、02、07，中国联通GSM系统使用01，中国电信CDMA系统使用03，一个典型的IMSI号码为460030912121001;
	 * 
	 * @param act
	 * @return 返回不同运营商类型
	 */
	public static int getMobileCardType() {
		Context act = Community.getContext();
		if (act == null)
			return UNKNOW_TYPE;
		TelephonyManager mTm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
		String msim = mTm.getSimOperator();
		if (msim != null) {
			if (msim.equals("46000") || msim.equals("46002") || msim.equals("46007")) {
				return MOVE_MOBILE_TYPE;
			} else if (msim.equals("46001")) {
				return UNICOM_TYPE;
			} else if (msim.equals("46003")) {
				return TELECOM_TYPE;
			}
		}
		return UNKNOW_TYPE;
	}

	final static int NONET = -1;

	final static int WIFI = 1;

	final static int CMWAP = 2;

	final static int CMNET = 3;

	final static int UNWAP = 4;

	final static int UNNET = 5;

	final static int CTWAP = 6;

	final static int CTNET = 7;

	final static int UNKNOWNET = 255;

	private static String NetWork[] = { "wifi", "cmwap", "cmnet", "unwap", "unnet", "ctwap", "ctnet", "default" };

	public static String getNetWorkType() {
		final int type = getAPNType();
		if (type == -1 || type == 255) {
			return NetWork[7];
		}
		return NetWork[type - 1];
	}

	public static boolean isAPNType(final int type) {
		return getAPNType() == type;
	}

	/**
	 * 获取当前的网络状态 -1：没有网络 1：WIFI网络 2：cmwap网络 3：cmnet网络
	 * 
	 * @param context
	 * @return
	 */
	public static int getAPNType() {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) Community.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		} else if (nType == ConnectivityManager.TYPE_MOBILE) {
			final String extra = networkInfo.getExtraInfo();
			if (extra == null) {
				return netType = CMWAP;
			}
			Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is " + extra);
			if (extra.toLowerCase().equals("cmnet")) {
				netType = CMNET;
			} else if (extra.toLowerCase().equals("cmwap")) {
				netType = CMWAP;
			} else { // 暂时把其他运营山的网络定义为wifi 待修正
				netType = CMWAP;
			}
		}

		// else if (networkInfo.getExtraInfo().toLowerCase().indexOf("net") !=
		// -1) {
		// if (getMobileCardType() == UNICOM_TYPE) {
		// netType = UNNET;
		// } else if (getMobileCardType() == TELECOM_TYPE) {
		// netType = CTNET;
		// }
		// } else if (networkInfo.getExtraInfo().toLowerCase().indexOf("wap") !=
		// -1) {
		// if (getMobileCardType() == UNICOM_TYPE) {
		// netType = UNWAP;
		// } else if (getMobileCardType() == TELECOM_TYPE) {
		// netType = CTWAP;
		// }
		// }
		// } else {
		// netType = UNKNOWNET;
		// }
		return netType;
	}

	/**
	 * @return
	 */
	public static String getIPListKey() {
		final int type = getAPNType();
		if (type == WIFI) {
			return WIFI_IPLIST;
		} else if (type == CMWAP) {
			return CMWAP_IPLIST;
		} else if (type == CMNET) {
			return CMNET_IPLIST;
		} else if (type == CTWAP) {
			return CTWAP_IPLIST;
		} else if (type == CTNET) {
			return CTNET_IPLIST;
		} else if (type == UNWAP) {
			return UNWAP_IPLIST;
		} else if (type == UNNET) {
			return UNNET_IPLIST;
		}
		return DEFAULT_IPLIST;
	}

	public static boolean isNetworkConnected(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

	public static String getNodeValue(final NamedNodeMap map, final String item) {
		if (map == null || item == null) {
			return null;
		}
		Node node = map.getNamedItem(item);
		return node != null ? node.getNodeValue() : null;
	}

	/**
	 * @param listStatusnote
	 */
	public static String getNodeText(NodeList list) {
		String text = null;
		if (list != null && list.getLength() > 0) {
			Node tmp = list.item(0);
			NodeList tmpList = tmp.getChildNodes();
			if (tmpList != null && tmpList.getLength() > 0) {
				tmp = tmpList.item(0);
				text = tmp.getNodeValue();
			}
		}
		return text;
	}
}
