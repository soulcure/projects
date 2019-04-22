/**
 * 
 */
package com.mingyou.community;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.login.utils.DensityConst;
import com.login.utils.HostAddressPool;
import com.mingyou.NoticeMessage.NoticePlacardProvider;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.login.IPConfigManager;
import com.mykj.comm.util.NetDataConnectionState;
import com.mykj.comm.util.TCAgentUtil;

import debug.IP_CONFIG_FILE;

/**
 * 共享数据管理类
 * 
 * @author Jason
 * 
 */
public class Community {
/**淘TV平台id*/
	public static final int PLAT_ALTV = 4;
	/** 阿里斗地主的平台id */
	public static final int PLAT_ALDDZ = 3;
	/** 超爽斗地主平台Id */
	public static final int PLAT_CSDDZ = 2;
	/** 通用平台id */
	public static final int PLAT_COMM = 0;
	/** 北京南苑机房平台Id,超爽平台Id */
	public static final int PLAT_NANYUAN = 5;
	
	/** 北京南苑机房平台Id,赢话费Id 2.0.0修改 */
	public static final int PLAT_YINGHUAFEI = 8;
	// public String configPath = null;
	public static final int ZN_CN = 5; // 简体中文
	public static final int ZN_TW = 6; // 繁体中文
	public static final int US_EN = 7; // 英文
	public static final int DE_DE = 8; // 德语
	public static final int PT_PT = 9; // 葡萄牙语
	public static final int CA_ES = 10; // 西班牙语
	public static final int ID_ID = 11; // 印尼语
	public static final int TH_TH = 12; // 泰语
	public static final int VI_VN = 13; // 越南语
	private static String languageArray[] = { "-1", "-1", "-1", "-1", "-1",
			"cn", "tw", "en", "de", "pt", "es", "id", "th", "vn", };
	private static int curLanguage = -1;

	private boolean initialFinish = false;
	
	public static int getCurLanguage() {
		if (curLanguage == -1) {
			// 检测语言版本
			// String loc = Locale.getDefault().getLanguage();
			String loc = Locale.getDefault().getCountry();
			if (loc != null) {
				for (int i = 0; i < languageArray.length; i++) {
					if (loc.toLowerCase().indexOf(languageArray[i]) > -1) {
						return curLanguage = i;
					}
				}
			}
		}
		return curLanguage;
	}

	/**
	 * 手机端类型标识 <br>
	 * 0-客户移动系列（默认） <br>
	 * 1- mtk系列 <br>
	 * 2- 拆包版本（配合移动大厅） <br>
	 * 其它保留使用，以PC字节序位准。 <br>
	 * 4-定义为android系列
	 */
	public static final int gamePlatform = 4;

	/** 平台ID(仅登录使用，固定为3) **/
	public static int PlatID = PLAT_ALDDZ;

	/** 渠道后两位 01代表j2me平台,02android, */
	public final String PLATFORMID = "02";

	private String CID = null;

	/** 渠道号 */
	private String _channel = "8080";

	/** 子渠道号 **/
	private String _subChannel = "0000";

	// /** 客户端标识由：渠道号和子渠道号连接而成 **/
	// private String _clientID = null;

	/** 机型 */
	private String _build_device = null;

	/** 当前客户端版本号1.0.0 **/
	private String _build_version = null;

	/** 协议版本号，1.5.3加入 */
	private int _verCode;

	/** 当前游戏ID **/
	private static int _gameID = 0;

	/** 是否有效的缓存帐号信息 **/
	public static boolean _isValidAccInfo = false;

	// /** 游戏启动类型 **/
	// /** 移动大厅启动 **/
	// public static final int START_TYPE_CMLOBBY = 1;
	//
	// /** 名游大厅启动 **/
	// public static final int START_TYPE_MYLOBBY = 0;
	//
	// /** 单个游戏启动 **/
	// public static final int START_TYPE_GAME = 2;
	//
	// /** 这个新大厅 **/
	// public static final int START_TYPE_NEWLOBBY = 3;

	// /** 大厅启用方式的常量 */
	// public static final int HALL_TAG = 0;
	//
	// /** 游戏启用方式的常量 */
	// public static final int GAME_TAG = 1;
	//
	// /**
	// * 游戏的启动类型,默认为单款游戏启动类型(此类型将影响帐号数据的存放位置)
	// */
	// private int _startType = GAME_TAG;

	protected Community() {
		initCommunityData();
	}

	private static Community _instacne = null;

	public static Community getInstacne() {
		if (_instacne == null) {
			_instacne = new Community();
		}
		return _instacne;
	}

	private void initCommunityData() {
		String tmp_channel = _channel;
		if (tmp_channel == null) {
			tmp_channel = "154";
		} else if (tmp_channel.trim().length() != 3) {
			if (tmp_channel.length() > 3) {
				tmp_channel = tmp_channel.substring(tmp_channel.length() - 3);
			}
		}

		CID = tmp_channel + PLATFORMID + getDeviceModel() + _subChannel /*
																		 * +
																		 * version_ID
																		 */;
		// setClientIDByChannel(_channel);
		// configPath = LoginHttp.CONFIG_HOST + "/cmwapgame.aspx?cid=" + CID;
	}

	/**
	 * 初始化渠道号信息及当前登录模块的启动方式
	 * 
	 * @param channel
	 * @param subChannel
	 * @param type
	 */
	public void initCommunity(final Context con, final int platID,
			final int gameid, final String channel, final String subChannel,
			final int startType, final String version, final int vercode) {
		_context = con;
		_gameID = gameid;
		PlatID = platID;
		_channel = channel;
		_subChannel = subChannel;
		_build_version = version;
		_verCode = vercode;


		IPConfigManager.getInstacne().initIPConfig();
		Log.e("initChannel", "start type=" + startType);
		initCommunityData();
		LoginInfoManager.initManager(con, startType);
		TCAgentUtil.initTCAgent(con);
		NoticePlacardProvider.getInstance().reqNoticeInfo();
		// 加载调试配置
		IP_CONFIG_FILE.readIpPortFormConfig();
		// 初始化网络状态监听
		NetDataConnectionState.getInstance().init(con);
		initialFinish = true;
	}

	public boolean isFinish(){
		return initialFinish;
	}

	public int getGameID() {
		return _gameID;
	}

	// /**
	// * 获得登录模块的启动方式
	// *
	// * @return
	// */
	// public int startType() {
	// return _startType;
	// }
	//
	// /**
	// * 判断当前启动类型是否是指定类型
	// *
	// * @param type
	// * @return
	// */
	// public boolean isStartType(int type) {
	// return _startType == type;
	// }
	//
	// public void setStartType(int type) {
	// _startType = type;
	// }

	/**
	 * 获得当前application的版本号
	 * 
	 * @return
	 */
	public String getAppVersion() {
		if (_build_version == null || _build_version.length() <= 0) {
			PackageInfo info = null;
			try {
				info = getContext().getPackageManager().getPackageInfo(
						getContext().getPackageName(), 0);
				_build_version = info.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return _build_version;
	}

	/**
	 * 获得当前机型,总共只能使用8个字节
	 * 
	 * @return
	 */
	public String getDeviceModel() {
		if (_build_device == null) {
			_build_device = "";
			final String br = Build.BRAND;
			final String mo = Build.MODEL;
			if (br.length() > 3) {
				_build_device += br.substring(0, 3);
			} else {
				_build_device += br;
			}
			if (mo.length() > 5) {
				_build_device += mo.substring(mo.length() - 5);
			} else {
				_build_device += mo;
			}
		}
		if (_build_device == null) {
			_build_device = "AND-0000";
		}
		return _build_device;
	}

	// private void setClientIDByChannel(String channel) {
	// if (channel == null) {
	// return;
	// }
	// int channelNo = 2;
	// try {
	// channelNo = Integer.parseInt(channel);
	// } catch (NumberFormatException e) {
	// e.printStackTrace();
	// }
	// if (channelNo == 2) {// 默认的移动推广
	// channelNo = 8001;
	// } else if (channelNo == 28) {// cmnet访问wap
	// channelNo = 8002;
	// } else {
	// // 其他的channel与ClientID是对应的
	// channelNo = 8000 + channelNo;
	// }
	// // if (channelNo == 8066) {
	// // // MTK平台版本，业务代码420120024000
	// // HShopHttp.cpServiceId = "420120024000";
	// // }
	// //
	// // CHANNEL_CHILD = getChannelChild();
	// /** 客户端标识由：渠道号和子渠道号连接而成 */
	// _clientID = Integer.toString(channelNo);
	// }

	/** 设备标识 **/
	private String _moblieProperty = null;

	// private String hall_version = "3.1.5";

	public String getMoblieProperty() {
		if (_moblieProperty == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
			sb.append("<MoblieProperty ");
			try {
				sb.append("br=\"").append(Build.BRAND).append("\""); // 手机品牌
				sb.append(" mn=\"" + Build.MODEL + "\""); // 手机型号
				final int width = DensityConst.getWidthPixels();
				final int height = DensityConst.getHeightPixels();
				sb.append(" ss=\"" + width + "x" + height + "\"");
				long memorySize = Runtime.getRuntime().totalMemory() / 1024;
				sb.append(" ms=\"" + memorySize + "\"");
				final String IMEI = getIMEI();
				if (IMEI != null && !IMEI.equals("null")) {
					sb.append(" ie=\"" + IMEI + "\"");
				}
				final String IMSI = getIMSI();
				if (IMSI != null && !IMSI.equals("null")) {
					sb.append(" is=\"" + IMSI + "\"");
				}
				final String version = getAppVersion();
				if (version != null) {
					sb.append(" ver=\"" + version + "\"");
				}
				sb.append("/>");
			} catch (Exception e) {
				e.printStackTrace();
			}
			_moblieProperty = sb.toString();
		}
		return _moblieProperty;
	}

	/** 手机的IMEI信息 **/
	private String _IMEI = null;

	/**
	 * 获得手机IMEI信息
	 * 
	 * @return IMEI信息
	 */
	public String getIMEI() {
		if (_IMEI != null) {
			return _IMEI;
		}
		try {
			TelephonyManager telephonyManager = (TelephonyManager) getContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			return _IMEI = telephonyManager.getDeviceId();
		} catch (Exception e) {
			return null;
		}
	}

	/** 手机的IMSI信息 **/
	private String _IMSI = null;

	/**
	 * 获得手机IMSI信息
	 * 
	 * @return IMSI信息
	 */
	public String getIMSI() {
		if (_IMSI != null) {
			return _IMSI;
		}
		try {
			TelephonyManager telephonyManager = (TelephonyManager) getContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			return _IMSI = telephonyManager.getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** 用户信息对象 **/
	private static MUserInfo _sSelftUserInfo = null;

	// /** 非cmwap的只能购买 X_NOCMWAP一下的道具 这个值会在白名单或帐号登录成功后赋值，默认为5 */
	// public static int X_NOCMWAP = 5;

	/**
	 * 获得用户信息对象
	 * 
	 * @return 用户信息对象
	 */
	public static MUserInfo getSelftUserInfo() {
		if (_sSelftUserInfo == null) {
			_sSelftUserInfo = new MUserInfo();
		}
		return _sSelftUserInfo;
	}

	/**
	 * 设置当前用户信息对象
	 * 
	 * @param userInfo
	 *            用户信息对象
	 */
	public static void setSelftUserInfo(MUserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		_sSelftUserInfo = userInfo;
	}

	/** 当前程序上下文 **/
	private static Context _context = null;

	// /**
	// * 次方法为白名单写入专用方法，服务后台启动需要设置context
	// * @param Context
	// */
	// public static void setCurContext(Context context) {
	// _context = context;
	// }

	/**
	 * 获得当前程序上下文
	 * 
	 * @return 当前程序上下文
	 */
	public static Context getContext() {
		return _context;
	}

	/**
	 * 获得当前子渠道号
	 * 
	 * @return 子渠道号
	 */
	public String getSubChannel() {
		return _subChannel;
	}

	/**
	 * 获得当前主渠道号
	 * 
	 * @return 主渠道号
	 */
	public String getChannel() {
		return _channel;
	}

	/**
	 * @return
	 */
	public String getCID() {
		return CID;
	}

	/**
	 * 次值统一使用getChannel主渠道号代替
	 * 
	 * @deprecated
	 * @return 客户端标识
	 */
	public String getClientID() {
		return _channel;
	}

	/**
	 * 获取协议版本号 1.5.3新添加
	 * 
	 * @return
	 */
	public int getVerCode() {
		return _verCode;
	}

}
