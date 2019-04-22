/**
 * 
 */
package com.mingyou.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.login.utils.HostAddressPool;
import com.mingyou.community.Community;
import com.mingyou.community.PortGroup;
import com.minyou.android.net.HttpConnector;
import com.minyou.android.net.IPConfigInterface;
import com.minyou.android.net.IRequest;
import com.minyou.android.net.NetService;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.log.MLog;
import com.mykj.comm.util.TCAgentUtil;

import debug.IP_CONFIG_FILE;

/**
 * @author JasonWin8
 * 
 */
public class IPConfigManager implements IPConfigInterface {
	// private static final String FIRST_HOST = "http://115.29.186.100:55556/";
	//
	// private static final String SECOND_HOST = "http://115.29.186.100:55556/";
	private static final String FIRST_HOST = HostAddressPool.FIRST_HOST;
	// "http://iplist.139game.net/";

	private static final String SECOND_HOST = HostAddressPool.SECOND_HOST;
	// "http://iplist.dg668.net/";

	private static final String TAG = "LoginConfigInfoManager";

	private static final String HAS_CONFIG = "hasConfig";

	/** 登录iplist保存文件名 **/
	private static final String _IPConfigFile = "loginConfig1.0.info";

	private static IPConfigManager _instance = null;

	public static IPConfigManager getInstacne() {
		if (_instance == null) {
			_instance = new IPConfigManager();
		}
		return _instance;
	}

	private final static String IP_LIST_KEY = "IPListConfig";

	public void initIPConfig() {
		reqIPConfig();
	}

	/**
	 * 获得IP列表
	 * 
	 * @return
	 */
	public Map<String, PortGroup> getIP_PortArray() {
		return loadLoginConfigInfo();
	}

	/**
	 * 加载本地保存ip列表,一般情况下此方法不需要外部调用，有登录逻辑i处理加载时机
	 * 
	 * @return
	 */
	private Map<String, PortGroup> loadLoginConfigInfo() {
		SharedPreferences ipList = Community.getContext().getSharedPreferences(
				_IPConfigFile, Context.MODE_PRIVATE);
		// final String type = LoginUtil.getIPListKey();
		final String iplist = ipList.getString(IP_LIST_KEY, null);
		if (iplist != null) {
			try {
				return parseLoginConfig(iplist);
			} catch (Exception e) {
				MLog.e(TAG, "loadLoginConfigInfo erro=" + e);
				// 删除此错误信息
				if (ipList != null) {
					ipList.edit().remove(IP_LIST_KEY); // 移除错误信息
					ipList.edit().remove(getHasConfigKey()); // 移除错误信息
				}
			}
		}
		return null;
	}

	/**
	 * 根据接入类型获取是否含有当前的登陆配置
	 * 
	 * @return
	 * @author：pjy
	 */
	public boolean hasIPConfigInfo() {
		SharedPreferences ipList = Community.getContext().getSharedPreferences(
				_IPConfigFile, Context.MODE_PRIVATE);
		String ipListKey = LoginUtil.getIPListKey();// 每次都要重新取，游戏中有切换接入点并在游戏的登陆界面
		if (ipList.contains(getHasConfigKey())) {
			MLog.e(TAG, "SearchLGCONF " + ipListKey + "hasConfig"
					+ " has Value");
			return true;
		}
		MLog.e(TAG, "SearchLGCONF " + ipListKey + "hasConfig" + " has no Value");
		return false;
	}

	private String getHasConfigKey() {
		return IP_LIST_KEY + HAS_CONFIG;
	}

	/**
	 * 将指定字节数组解析为登录配置信息，并保存到本地文件中，一般不由外部调用
	 * 
	 * @param buf
	 * @return
	 */
	private boolean saveLoginConfigInfo(final byte[] buf) {
		//
		final String str = TDataInputStream.getUTF8String(buf);
		try {
			if (parseLoginConfig(str) == null) {
				throw new Exception("ip和port不存在");
			}
		} catch (Exception e) {
			onFailed();
			MLog.e(TAG, "保存iplist时解析数据异常");
			return false;
		}
		SharedPreferences ipList = Community.getContext().getSharedPreferences(
				_IPConfigFile, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = ipList.edit();
		// final String strTypeString = LoginUtil.getIPListKey();
		editor.putString(IP_LIST_KEY, str);
		editor.putBoolean(getHasConfigKey(), true);
		editor.commit();
		onUpdateConfig();
		onSucceed();
		return true;
	}

	/**
	 * 解析登录配置信息的数据，解析出ip列表，公告等信息
	 * 
	 * @param text
	 * @throws JSONException
	 */
	private Map<String, PortGroup> parseLoginConfig(final String text)
			throws Exception {
		// 此处内部不能有任何try catch语句,异常全部由外部处理
		JSONTokener tokener = new JSONTokener(text);
		Object obj = tokener.nextValue();
		if (obj instanceof JSONObject) {
			JSONObject jobject = (JSONObject) obj;
			// IP+prot解析
			JSONArray plist = jobject.getJSONArray("plist"); // plist
			if (plist != null) {
				final int len = plist.length();
				if (len == 0) {
					return null;
				}
				Map<String, PortGroup> ipMap = new HashMap<String, PortGroup>();
				for (int i = 0; i < len; i++) {
					JSONObject ipProt = plist.getJSONObject(i);
					try {
						final String ip = ipProt.getString("ip"); // ip
						JSONArray jsarray = ipProt.getJSONArray("portlist");
						int array[] = new int[jsarray.length()];
						for (int j = 0; j < array.length; j++) {
							array[j] = jsarray.getInt(j);
						}
						PortGroup portGroup = new PortGroup();
						portGroup.init(array);
						ipMap.put(ip, portGroup);
					} catch (Exception e) {
					}
				}
				if (ipMap.size() == 0) {
					return null;
				}
				return ipMap;
			}
		}
		return null;
	}

	private int reqConfigCount = 0;

	private final int max_count = 3;

	private boolean _isFirstHost = true;

	/**
	 * 通过http请求IP列表
	 */
	private void reqIPConfig() {
		TCAgentUtil.onTCAgentEvent("请求IP列表");
		final HttpConnector http = LoginHttp.createLoginHttpConnector();
		IRequest iRequest = new IRequest() {

			@Override
			public void doError(Message msg) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				TCAgentUtil.onTCAgentEvent("IP列表-返回失败");
				MLog.v(TAG, "http请求登录配置信息返回失败-" + msg.what);
				if (++reqConfigCount < max_count) {
					reqIPConfig();
				} else {
					if (!_isFirstHost) {
						onFailed();
					} else {
						reqConfigCount = 0;
						_isFirstHost = false;
						reqIPConfig();
					}
				}
			}

			public void handler(byte[] buf) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				TCAgentUtil.onTCAgentEvent("IP列表-返回成功");
				MLog.v(TAG, "http请求登录配置信息返回成功");
				/* final boolean bool = */saveLoginConfigInfo(buf);

			}

			public String getHttpUrl() {
				// 1.6.0/iplist.php?cid=8001&gameid=100&version=1.6.0
				StringBuilder buf = new StringBuilder();
				if (_isFirstHost) {
					buf.append(HostAddressPool.FIRST_HOST /*FIRST_HOST*/);
				} else {
					buf.append(HostAddressPool.SECOND_HOST/*SECOND_HOST*/);
				}
				//超爽斗地主添加
				if(Community.PlatID==Community.PLAT_CSDDZ){
					buf.append("chaoshuang/");
				}
				buf.append("iplist.php");
				return buf.toString();
			}

			@Override
			public String getParam() {
				// 1.6.0/iplist.php?cid=8001&gameid=100&version=1.6.0
				StringBuilder buf = new StringBuilder();
				buf.append("cid=").append(Community.getInstacne().getChannel());
				buf.append("&gameid=").append(
						Community.getInstacne().getGameID());
				buf.append("&version=").append(
						Community.getInstacne().getAppVersion());
				buf.append("&platform=").append(
						Community.PlatID);
				return buf.toString();
			}
		};
		http.addEvent(iRequest);
		http.connect();
	}

	private void onFailed() {
		for (int i = 0; i < _listener.size(); i++) {
			IPConfigCallBackListener lis = _listener.get(i);
			if (lis != null) {
				lis.onFailed();
			}
		}
		_listener.removeAllElements();
	}

	private void onSucceed() {
		for (int i = 0; i < _listener.size(); i++) {
			IPConfigCallBackListener lis = _listener.get(i);
			if (lis != null) {
				lis.onSucceed();
			}
		}
		_listener.removeAllElements();
	}

	private void onUpdateConfig() {
		for (int i = 0; i < _updatelistener.size(); i++) {
			UpdateConfigCallBackListener lis = _updatelistener.get(i);
			if (lis != null) {
				lis.onUpdate();
			}
		}
		_updatelistener.removeAllElements();
	}

	private Vector<UpdateConfigCallBackListener> _updatelistener = new Vector<UpdateConfigCallBackListener>();

	private Vector<IPConfigCallBackListener> _listener = new Vector<IPConfigCallBackListener>();

	@Override
	public void addUpdateCallBackLis(UpdateConfigCallBackListener lis) {
		_updatelistener.add(lis);
	}

	@Override
	public void addConfigCallBackLis(IPConfigCallBackListener lis) {
		if (hasIPConfigInfo()) {
			if (lis != null) {
				lis.onSucceed();
			}
			return;
		}
		_listener.add(lis);
	}

}
