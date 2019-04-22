/**
 * 
 */
package com.mingyou.login;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Message;

import com.login.utils.HostAddressPool;
import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.community.Community;
import com.mingyou.community.MUserInfo;
import com.minyou.android.net.HttpConnector;
import com.minyou.android.net.IRequest;
import com.minyou.android.net.NetService;
import com.multilanguage.MultilanguageManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.log.MLog;
import com.mykj.comm.util.AndrUtil;
import com.mykj.comm.util.SecretCode;
import com.mykj.comm.util.TCAgentUtil;

import debug.IP_CONFIG_FILE;

/**
 * @author Jason
 * 
 */
public class LoginHttp {

	/** 海华开发原始注册相关 **/
	private final static String _apiKey = "2e635029df7f4092a6208b6000f38a7e";

	/** 海华开发原始注册相关 **/
	private final static String _secret = "1a697f1983934616a881941be4b338af";

	/** http登录相关 **/
	private static final String _cSecret = "MYTL!@#14yhl9tka";

	/** 登录相关地址 **/
	public static String loginHost = null;

	/** 帐号注册地址 **/
	public static String resHost = HostAddressPool.resHost;
	// "http://api.139game.com/request/alone/jumpapi.php"; //
	// "http://192.168.1.186:91/request/alone/jumpapi.php";

	/** 版本检测地址 **/
	//public static String MY_HOST = HostAddressPool.MY_HOST;
	// "http://update.139game.com/android/androidconfig.php";

	/** http登录地址请求 **/
	public static String httpLoginReq = HostAddressPool.httpLoginReq;
	// "http://qpwap.cmgame.com/get_third_entry.php";

	private static LoginHttp _instance = null;

	// 获得http连接
	private static boolean isHasHttpHost = false;

	private static final String TAG = "LoginHttp";

	/**
	 * 从web服务器获取http登录地址<br>
	 * 此方法将会在at，帐号密码，免注册登录三种方式调用之前都做检查，保证登录的有效性，但不会重复请求登录地址
	 */
	private static void reqHttpHost(final HttpReceiveEventCallBack event) {
		if (loginHost == null && !IP_CONFIG_FILE.isOuterNet()) { // 内网连接
			if (event != null) { // 已经据有有效地址，将不再重复请求
				isHasHttpHost = true;
				loginHost = IP_CONFIG_FILE.readPropertyValue("httpLoginUrl");
				MLog.e(TAG, "loginHost=" + loginHost);
				HttpEvent ev = getInstance().new HttpEvent();
				if (loginHost == null) {
					ev._message = MultilanguageManager.getInstance()
							.getValuesString("httpLoginTip1");
					ev._result = HttpEvent.LOGIN_FAILED; // 1表示成功，0表示失败
				} else {
					ev._message = MultilanguageManager.getInstance()
							.getValuesString("httpLoginTip1");
					ev._result = HttpEvent.LOGIN_SUCCESSED; // 1表示成功，0表示失败
				}
				event.onReceive(ev);
			}
			return;
		}
		if (isHasHttpHost && loginHost != null) {
			if (event != null) { // 已经据有有效地址，将不再重复请求
				HttpEvent ev = getInstance().new HttpEvent();
				ev._message = MultilanguageManager.getInstance()
						.getValuesString("httpLoginTip2");
				ev._result = HttpEvent.LOGIN_SUCCESSED; // 1表示成功，0表示失败
				event.onReceive(ev);
			}
			return;
		}
		final HttpConnector http = NetService.getInstance()
				.createHttpConnection(null);
		IRequest iRequest = new IRequest() {

			@Override
			public void handler(byte[] buf) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				// 登录信息成功返回
				InputStream is = new ByteArrayInputStream(buf);
				Document doc;
				byte result = HttpEvent.LOGIN_FAILED;
				String msg = null;
				try {
					doc = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().parse(is);
					NodeList el1 = doc.getElementsByTagName("login");
					ArrayList<String> urlList = new ArrayList<String>();
					for (int i = 0; i < el1.getLength(); i++) {
						Node node = el1.item(i);
						if (node != null) {
							String url = LoginUtil.getNodeValue(
									node.getAttributes(), "url");
							if (url != null) {
								urlList.add(url);
							}
						}
					}
					if (urlList.size() == 0) {
						return;
					}
					final int index = new Random().nextInt(urlList.size());
					loginHost = urlList.get(index);
					if (loginHost != null && loginHost.length() > 0) { // 有效url
						isHasHttpHost = true;
					}
					result = HttpEvent.LOGIN_SUCCESSED;
					msg = MultilanguageManager.getInstance().getValuesString(
							"httpLoginTip3");
				} catch (Exception e) {
					MLog.e(TAG, "reqHttpHost is error");
					e.printStackTrace();
					result = HttpEvent.LOGIN_FAILED;
					msg = MultilanguageManager.getInstance().getValuesString(
							"httpLoginTip4");
				} finally {
					if (event != null) {
						HttpEvent ev = getInstance().new HttpEvent();
						ev._message = msg;
						ev._result = result; // 1表示成功，0表示失败
						event.onReceive(ev);
					}
				}
			}

			@Override
			public void doError(Message msg) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				if (event != null) {
					HttpEvent ev = getInstance().new HttpEvent();
					ev._message = MultilanguageManager.getInstance()
							.getValuesString("httpLoginTip5");
					ev._result = HttpEvent.LOGIN_FAILED; // 1表示成功，0表示失败
					event.onReceive(ev);
				}
			}

			@Override
			public String getHttpUrl() {
				if (IP_CONFIG_FILE.isOuterNet()) {
					return httpLoginReq;
				} else {
					return IP_CONFIG_FILE.readPropertyValue("httpLoginReq");
				}
			}

		};
		http.addEvent(iRequest);
		http.connect();
	}

	// end 获取http连接

	protected LoginHttp() {
	}

	public static LoginHttp getInstance() {
		if (_instance == null) {
			_instance = new LoginHttp();
		}
		return _instance;
	}

	/**
	 * 此接口已限定内部使用
	 * 
	 * @author Jason
	 * 
	 */
	private interface httpListener {
		public void onReceive(final byte[] array);

		public void onNetError(final String msg);
	}

	public class HttpEvent {
		/** 登录成功 **/
		public static final byte LOGIN_SUCCESSED = 0;

		/** 登录失败 **/
		public static final byte LOGIN_FAILED = 1;

		private String _message = null;

		private Object _obj = null;

		private int _result = LOGIN_FAILED;

		HttpEvent() {
		}

		public String getMessage() {
			return _message;
		}

		public Object getObject() {
			return _obj;
		}

		public int getResult() {
			return _result;
		}
	}

	/**
	 * 由UI层注册的监听器接口
	 * 
	 * @author Jason
	 * 
	 */
	public interface HttpReceiveEventCallBack {

		void onReceive(final HttpEvent event);
	}

	protected void httpConnect(final String path, final String arg,
			final httpListener listener, final String csecret) {
		httpConnect(path, arg, listener, csecret, IRequest.HTTP_POST);
	}

	/***
	 * 
	 * @param arg
	 *            参数key必须字典排序 参数,默认采用post方式
	 * @param csecret
	 * @param httpGet
	 */
	protected void httpConnect(final String path, final String arg,
			final httpListener listener, final String csecret,
			final byte httpGet) {
		final HttpConnector http = NetService.getInstance()
				.createHttpConnection(null);
		IRequest iRequest = new IRequest() {

			public void handler(final byte[] buf) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				if (listener != null) {
					listener.onReceive(buf);
				}
			}

			@Override
			public void doError(Message msg) {
				if (listener != null) {
					listener.onNetError(MultilanguageManager.getInstance()
							.getValuesString("httpLoginTip6"));
				}
				NetService.getInstance().removeHttpConnector(http.getTarget());
			}

			public String getParam() {
				final String oldArg = arg.replace(" ", ""); // 原始参数
				final String md5Str = oldArg.replace("&", ""); // 转换为md5参数
				final String md5Arg = md5Str + "secret=" + csecret;
				final String md5s = SecretCode.getMD5(md5Arg);
				final String args = oldArg + "&sig=" + md5s;
				return args;
			}

			public String getHttpUrl() {
				return path;
			}

			@Override
			public int getHttpState() {
				return httpGet;
			}
		};
		http.addEvent(iRequest);
		http.connect();
	}

	public static HttpConnector createLoginHttpConnector() {
		HttpConnector _httpcConnector = NetService.getInstance()
				.createHttpConnection(null);
		return _httpcConnector;
	}

	// 暴露外部接口
	/**
	 * http注册接口
	 * 
	 * @param acc
	 * @param pass
	 * @param shared
	 * @return
	 */
	public static boolean reqRegistration(final String acc, final String pass,
			final HttpReceiveEventCallBack event) {
		if (acc == null || acc.length() == 0 || pass == null
				|| pass.length() == 0) {
			return false;
		}
		httpListener listener = new httpListener() {

			public void onReceive(byte[] buf) {
				InputStream is = new ByteArrayInputStream(buf);
				String statu = null;
				String statusnote = null;
				try {
					Document doc = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().parse(is);
					NodeList listWeuid = doc.getElementsByTagName("weuid");
					/* final String weuid = */LoginUtil.getNodeText(listWeuid);
					NodeList listStatus = doc.getElementsByTagName("status");
					statu = LoginUtil.getNodeText(listStatus);
					NodeList listStatusnote = doc
							.getElementsByTagName("statusnote");
					statusnote = LoginUtil.getNodeText(listStatusnote);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (event != null) {
						byte b = HttpEvent.LOGIN_FAILED;
						try {
							b = Byte.parseByte(statu);
						} catch (Exception e) {
						}
						HttpEvent ev = getInstance().new HttpEvent();
						ev._message = statusnote;
						ev._result = b == 0 ? HttpEvent.LOGIN_SUCCESSED
								: HttpEvent.LOGIN_FAILED;
						event.onReceive(ev);
					}
				}
			}

			@Override
			public void onNetError(String msg) {
				if (event != null) {
					HttpEvent ev = getInstance().new HttpEvent();
					ev._message = msg;
					ev._result = HttpEvent.LOGIN_FAILED;
					event.onReceive(ev);
				}
			}
		};
		final String host = resHost;
		// 参数必须按字典排序
		StringBuilder buf = new StringBuilder();
		buf.append("apikey=").append(_apiKey);
		buf.append("&childchannel=").append(
				Community.getInstacne().getSubChannel());
		buf.append("&clientid=").append(Community.getInstacne().getChannel());
		buf.append("&format=").append("xml");
		buf.append("&method=quickreg");
		String pas = encodePass(pass);
		buf.append("&op=").append(System.currentTimeMillis());
		buf.append("&password=").append(pas);
		buf.append("&platid=").append(Community.PlatID);
		buf.append("&username=").append(acc);
		final String arg = buf.toString();
		getInstance().httpConnect(host, arg, listener, _secret);
		return true;
	}

	/**
	 * @param pass
	 * @return
	 */
	private static String encodePass(final String pass) {
		final byte[] array = pass.getBytes();
		String pas = SecretCode.base64encode(array);
		try {
			pas = URLEncoder.encode(pas, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return pas;
	}

	/**
	 * http帐号登录
	 * 
	 * @param acc
	 * @param pass
	 * @return
	 */
	public static boolean reqHttpLogin(final String acc, final String pass,
			final HttpReceiveEventCallBack event) {
		HttpReceiveEventCallBack hostListener = new HttpReceiveEventCallBack() {

			@Override
			public void onReceive(HttpEvent tev) {
				if (tev._result == HttpEvent.LOGIN_SUCCESSED) { // 请求成功
					httpListener listener = new httpListener() {

						public void onReceive(byte[] array) {
							parseHttpLoginInfo(event, array);
						}

						@Override
						public void onNetError(String msg) {
							if (event != null) {
								HttpEvent ev = getInstance().new HttpEvent();
								ev._message = msg;
								ev._result = HttpEvent.LOGIN_FAILED;
								event.onReceive(ev);
							}
						}
					};
					final String host = loginHost;
					// 参数必须按字典排序
					StringBuilder buf = new StringBuilder();
					buf.append("bcp=").append(0);
					buf.append("&cid=").append(
							Community.getInstacne().getChannel());
					buf.append("&gameid=").append(
							Community.getInstacne().getGameID());
					try {
						buf.append("&ip=").append(
								URLEncoder.encode(AndrUtil.getLocalIpAddress(),
										"UTF-8"));
						buf.append("&lm=").append(0);
						buf.append("&method=").append("login_account");
						buf.append("&mode=").append(0);
						String pas = encodePass(pass);
						buf.append("&pwd=").append(pas);
						buf.append("&pid=").append(Community.PlatID);
						buf.append("&plt=").append(
								Community.getInstacne().getCID());
						String str = URLEncoder.encode(Community.getInstacne()
								.getMoblieProperty(), "UTF-8");
						str = str.replace("\n", "");
						buf.append("&pt=").append(str);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					buf.append("&scid=").append(
							Community.getInstacne().getSubChannel());
					buf.append("&uname=").append(acc);
					AccountItem item = new AccountItem(acc, pass, null,
							AccountItem.ACC_TYPE_COMM, 0);
					LoginInfoManager.getInstance().setCurAccountItem(item);
					getInstance().httpConnect(host, buf.toString(), listener,
							_cSecret, IRequest.HTTP_GET);
				} else { // 请求失败
					if (event != null) {
						HttpEvent tmpEvent = getInstance().new HttpEvent();
						tmpEvent._message = MultilanguageManager.getInstance()
								.getValuesString("httpLoginTip5");
						tmpEvent._result = HttpEvent.LOGIN_FAILED;
						event.onReceive(tmpEvent);
					}
				}
			}
		};
		reqHttpHost(hostListener);
		return true;
	}

	/**
	 * httpAT登录 移动帐号和零时帐号都使用次登录方式
	 * 
	 * @param at
	 * @return
	 */
	public static boolean reqHttpLogin(final AccountItem item,
			final HttpReceiveEventCallBack event) {
		HttpReceiveEventCallBack httpReceiveEvent = new HttpReceiveEventCallBack() {

			@Override
			public void onReceive(HttpEvent hev) {
				if (hev._result == HttpEvent.LOGIN_SUCCESSED) {
					if (item.getToken() == null
							|| item.getToken().length() == 0) { // at信息有误
						if (event != null) {
							HttpEvent tmpEvent = getInstance().new HttpEvent();
							tmpEvent._message = MultilanguageManager
									.getInstance().getValuesString(
											"httpLoginTip5")
									+ item.getToken();
							tmpEvent._result = HttpEvent.LOGIN_FAILED;
							event.onReceive(tmpEvent);
						}
						return;
					}
					httpListener listener = new httpListener() {

						public void onReceive(byte[] array) {
							parseHttpLoginInfo(event, array);
						}

						@Override
						public void onNetError(String msg) {
							if (event != null) {
								HttpEvent ev = getInstance().new HttpEvent();
								ev._message = msg;
								ev._result = HttpEvent.LOGIN_FAILED;
								event.onReceive(ev);
							}
						}
					};
					final String host = loginHost;
					// 参数必须按字典排序
					StringBuilder buf = new StringBuilder();
					try {
						buf.append("at=").append(
								URLEncoder.encode(item.getToken(), "UTF-8"));
						buf.append("&cid=").append(
								Community.getInstacne().getChannel());
						buf.append("&gameid=").append(
								Community.getInstacne().getGameID());
						String ip = AndrUtil.getLocalIpAddress();
						buf.append("&ip=").append(ip);
						buf.append("&method=").append("loginat");
						buf.append("&mode=").append(0);
						buf.append("&opid=").append("3");
						buf.append("&plt=").append(
								Community.getInstacne().getCID());
						String str = null;
						str = URLEncoder.encode(Community.getInstacne()
								.getMoblieProperty(), "UTF-8");
						str = str.replace("\n", "");
						buf.append("&pt=").append(str);
						buf.append("&scid=").append(
								Community.getInstacne().getSubChannel());
						LoginInfoManager.getInstance().setCurAccountItem(item);
						getInstance().httpConnect(host, buf.toString(),
								listener, _cSecret, IRequest.HTTP_GET);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					if (event != null) {
						HttpEvent tmpEvent = getInstance().new HttpEvent();
						tmpEvent._message = MultilanguageManager.getInstance()
								.getValuesString("httpLoginTip5");
						tmpEvent._result = HttpEvent.LOGIN_FAILED;
						event.onReceive(tmpEvent);
					}
				}
			}
		};
		reqHttpHost(httpReceiveEvent);

		return true;
	}

	/**
	 * 免注册登录
	 * 
	 * @return
	 */
	public static boolean reqHttpFreeLogin(final HttpReceiveEventCallBack event) {
		HttpReceiveEventCallBack httpReceiveEvent = new HttpReceiveEventCallBack() {

			@Override
			public void onReceive(HttpEvent hev) {
				if (hev._result == HttpEvent.LOGIN_SUCCESSED) {
					httpListener listener = new httpListener() {

						public void onReceive(byte[] array) {
							parseHttpLoginInfo(event, array);
						}

						@Override
						public void onNetError(String msg) {
							if (event != null) {
								HttpEvent ev = getInstance().new HttpEvent();
								ev._message = msg;
								ev._result = HttpEvent.LOGIN_FAILED;
								event.onReceive(ev);
							}
						}
					};
					final String host = loginHost;
					// 参数
					StringBuilder buf = new StringBuilder();
					buf.append("cid=").append(
							Community.getInstacne().getChannel());
					buf.append("&gameid=").append(
							Community.getInstacne().getGameID()); // gameID
					buf.append("&method=").append("autoreg");
					buf.append("&mode=").append(0);
					buf.append("&pid=").append(Community.PlatID);
					buf.append("&plt=")
							.append(Community.getInstacne().getCID());
					String str = null;
					try {
						str = URLEncoder.encode(Community.getInstacne()
								.getMoblieProperty(), "UTF-8");
						str = str.replace("\n", "");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					buf.append("&pt=").append(str);
					buf.append("&scid=").append(
							Community.getInstacne().getSubChannel());
					String imsi = Community.getInstacne().getIMSI();
					try {
						imsi = URLEncoder.encode(imsi == null ? "" : imsi,
								"UTF-8");
					} catch (Exception e) {
						e.printStackTrace();
					}
					buf.append("&hid=").append(imsi); // IMSI
					buf.append("&opid=").append(
							Community.getInstacne().getSubChannel()); // 运营商ID
					LoginInfoManager.getInstance().setLoginType(
							AccountItem.ACC_TYPE_TEMP);
					getInstance().httpConnect(host, buf.toString(), listener,
							_cSecret, IRequest.HTTP_GET);
				} else {
					if (event != null) {
						HttpEvent tmpEvent = getInstance().new HttpEvent();
						tmpEvent._message = hev._message;
						tmpEvent._result = HttpEvent.LOGIN_FAILED;
						event.onReceive(tmpEvent);
					}
				}
			}
		};
		reqHttpHost(httpReceiveEvent);

		return true;
	}

	/**
	 * @param event
	 * @param array
	 */
	private static void parseHttpLoginInfo(
			final HttpReceiveEventCallBack event, byte[] array) {
		MUserInfo user = null;
		HttpReceiveEventCallBack tmpEvent = event;
		String msg = null;
		byte result = HttpEvent.LOGIN_FAILED;
		try {
			final String loginStr = TDataInputStream.getUTF8String(array);
			if (loginStr == null) {
				return;
			}
			final String[] loginStrs = loginStr.split("#");
			if (loginStrs.length <= 5) { // 失败
				LoginInfoManager.getInstance().cleanCurPassWord(); // 登录失败清空以缓存密码
				// LoginInfoManager.getInstance().cleanLastAccountInfo();
				final int code = Integer.parseInt(loginStrs[0]); // code
				MLog.e(TAG, "http登录失败code=" + code);
				msg = loginStrs[1]; // message
				result = HttpEvent.LOGIN_FAILED;
			} else if (loginStrs.length > 16) { // 成功
				user = Community.getSelftUserInfo();
				user.userId = Integer.parseInt(loginStrs[0]);
				user.headNo = Integer.parseInt(loginStrs[1]);
				user.cbGender = Byte.parseByte(loginStrs[2]);
				user.memberOrder = Byte.parseByte(loginStrs[3]);
				user.lExperience = loginStrs[4];
				// String str = loginStrs[5];
				user.account = loginStrs[6];
				user.nickName = loginStrs[7];
				/**
				 * StatusBit 状态位定义（32个bit中） 第 1 bit: 0-表示不能购买道具
				 * 1-可以购买道具（PC为0x00000001） 第 2 bit: 0-表示MTK购买走社区流程
				 * 1-可以MTK购买走MTK流程（PC为0x00000002）
				 */
				// loginStrs[8]; // 地区编码
				final String Token = loginStrs[9];
				user.lBean = Integer.parseInt(loginStrs[10]);
				final byte loginType = Byte.parseByte(loginStrs[11]);
				user.guid = Integer.parseInt(loginStrs[12]);
				// Integer.parseInt(loginStrs[13]); //用户管理等级
				// Integer.parseInt(loginStrs[14]) == 1;//是否首次登录
				// final int result = Integer.parseInt(loginStrs[15]);//
				// 有没有绑定0-表示没有绑定 1-已经绑定
				user.muid = Integer.parseInt(loginStrs[16]); // 移动社区ID
				/** 设置我的用户信息对象 */
				Community.setSelftUserInfo(user);
				if (loginType == 0) { // tat登录成功
					LoginInfoManager.getInstance().setIsBind(false); // 只有游客帐号才绑定
					AccountItem item = new AccountItem(AccountItem.ACC_TEMP,
							AccountItem.NO_PASS, Token,
							AccountItem.ACC_TYPE_TEMP, user.userId);
					LoginInfoManager.getInstance().setCurAccountItem(item);
				} else { // at登录成功
					if (LoginInfoManager.getInstance().getLoginType() == AccountItem.ACC_TYPE_COMM) { // 登录是普通帐号
						AccountItem item = new AccountItem(user.account,
								LoginInfoManager.getInstance()
										.getUserPassWord(), Token,
								AccountItem.ACC_TYPE_COMM, user.userId);
						LoginInfoManager.getInstance().setCurAccountItem(item);
					} else if (LoginInfoManager.getInstance().getLoginType() == AccountItem.ACC_TYPE_TEMP) { // 此处为手机帐号
						LoginInfoManager.getInstance().setIsBind(true);
						AccountItem item = new AccountItem(
								AccountItem.ACC_PHONE, AccountItem.NO_PASS,
								Token, AccountItem.ACC_TYPE_TEMP, user.userId);
						LoginInfoManager.getInstance().setCurAccountItem(item);
					} /*
					 * else if (LoginInfoManager.getInstance().getLoginType() ==
					 * AccountItem.ACC_TYPE_CMCC) { AccountItem item = new
					 * AccountItem( AccountItem.ACC_CMCC, AccountItem.NO_PASS,
					 * Token, AccountItem.ACC_TYPE_CMCC, user.userId);
					 * LoginInfoManager.getInstance().setCurAccountItem(item); }
					 */else if (LoginInfoManager.getInstance().getLoginType() == AccountItem.ACC_TYPE_THIRD) {
						AccountItem item = new AccountItem(null, null, Token,
								AccountItem.ACC_TYPE_COMM, user.userId);
						LoginInfoManager.getInstance().setCurAccountItem(item);
					} else {
						TCAgentUtil
								.onTCAgentEvent("未知帐号类型登录成功", "loginType="
										+ LoginInfoManager.getInstance()
												.getLoginType());
						AccountItem item = new AccountItem(user.account, null,
								Token, AccountItem.ACC_TYPE_COMM, user.userId);
						LoginInfoManager.getInstance().setCurAccountItem(item);
						String msgs = "login recv is error loginType="
								+ LoginInfoManager.getInstance().getLoginType();
						MLog.v(TAG, msgs);
						MLog.e(TAG, "未知帐号类型登录成功" + msgs);
					}
				}
				LoginInfoManager.getInstance().updateAccountInfo(); // 必须先更新帐号信息，在保存
				result = HttpEvent.LOGIN_SUCCESSED;
				msg = MultilanguageManager.getInstance().getValuesString(
						"httpLoginTip8");
			} else {
				MLog.v(TAG, "登录失败，服务器返回错误=" + loginStr);
				result = HttpEvent.LOGIN_FAILED;
				msg = MultilanguageManager.getInstance().getValuesString(
						"httpLoginTip8");
			}
		} catch (Exception e) {
			MLog.e(TAG, "parseHttpLoginInfo e:" + e.getMessage());
		} finally {
			if (tmpEvent != null) {
				HttpEvent hevent = getInstance().new HttpEvent();
				hevent._message = msg;
				hevent._obj = user;
				hevent._result = result;
				tmpEvent.onReceive(hevent);
				tmpEvent = null;
			}
		}
	}

	/**
	 * 此方法会自动识别当前合适的登录方式进行登录,如帐号密码，at等
	 * 
	 * @return
	 */
	public static boolean autoHttpLogin(final HttpReceiveEventCallBack event) {
		HttpReceiveEventCallBack httpReceiveEvent = new HttpReceiveEventCallBack() {

			@Override
			public void onReceive(HttpEvent hev) {
				if (hev._result == HttpEvent.LOGIN_SUCCESSED) {
					if (LoginInfoManager.getInstance().isHasNativeLoginInfo()) {
						AccountItem accItem = LoginInfoManager.getInstance()
								.getLastAccountItem();
						if (accItem == null) { // 没有帐号信息
							MLog.e(TAG,
									"autoHttpLogin accItem is null freeLogin");
							reqHttpFreeLogin(event);
						} else if (accItem.isValidToken()) { // AT登录
							reqHttpLogin(accItem, event);
						} else if (accItem.isValidAccAndPass()) { // 帐号密码登录
							reqHttpLogin(accItem.getUsername(),
									accItem.getPassword(), event);
						} else {
							if (event != null) {
								HttpEvent tmpEvent = getInstance().new HttpEvent();
								tmpEvent._message = MultilanguageManager
										.getInstance().getValuesString(
												"httpLoginTip10");
								tmpEvent._result = HttpEvent.LOGIN_FAILED;
								event.onReceive(tmpEvent);
							}
							MLog.e(TAG,
									"autoHttpLogin AccountItem is error acc,pas,token is null");
							return;
						}
					} else { // 不存在表示免注册登录
						MLog.e(TAG,
								"autoHttpLogin isHasNativeLoginInfo is false freeLogin");
						reqHttpFreeLogin(event);
					}
				} else {
					if (event != null) {
						HttpEvent tmpEvent = getInstance().new HttpEvent();
						tmpEvent._message = MultilanguageManager.getInstance()
								.getValuesString("httpLoginTip5");
						tmpEvent._result = HttpEvent.LOGIN_FAILED;
						event.onReceive(tmpEvent);
					}
				}
			}
		};
		reqHttpHost(httpReceiveEvent);
		return true;
	}

	// end
	// 第三方相关
	public static void reqBindThird(final int type, final String token,
			final String tid, final String thirdAcc,
			final HttpReceiveEventCallBack eventCallBack) {
		HttpReceiveEventCallBack callBack = new HttpReceiveEventCallBack() {

			@Override
			public void onReceive(HttpEvent event) {
				if (event._result == HttpEvent.LOGIN_SUCCESSED) {
					httpBindThird(token, tid, type, thirdAcc, eventCallBack); // 发起绑定请求
				} else {
					if (eventCallBack != null) {
						eventCallBack.onReceive(event);
					}
				}
			}
		};
		reqHttpHost(callBack);
	}

	private static void httpBindThird(final String token, final String tid,
			final int type, final String thirdAcc,
			final HttpReceiveEventCallBack callBack) {
		httpListener listener = new httpListener() {

			@Override
			public void onReceive(byte[] array) {
				String strXml = TDataInputStream.getUTF8String(array);
				String status = AndrUtil.parseTagValueXml(strXml, "result",
						"status");
				String msg = AndrUtil.parseTagValueXml(strXml, "result", "msg");
				if (callBack != null) {
					HttpEvent event = getInstance().new HttpEvent();
					event._message = msg;
					try{
						event._result = Integer.parseInt(status);
					}catch (NumberFormatException e){
						event._result=-1;
						event._message = "绑定失败，请稍后再试！";
					}
					
					if (event._result == 0) { // 绑定成功
						// 更新当前账号信息
						AccountItem item = LoginInfoManager.getInstance()
								.getCurAccountItem();
						item.setThirdAccName(thirdAcc);
						item.setThirdAccUID(tid);
						LoginInfoManager.getInstance().updateAccInfo(item);
					}
					callBack.onReceive(event);
				}
			}

			@Override
			public void onNetError(String msg) {
				if (callBack != null) {
					HttpEvent event = getInstance().new HttpEvent();
					event._message = msg;
					event._result = -1;
					callBack.onReceive(event);
				}
			}
		};

		StringBuilder buf = new StringBuilder();
		try {
			buf.append("at=").append(URLEncoder.encode(token, "UTF-8"));
			buf.append("&cid=").append(Community.getInstacne().getChannel());
			buf.append("&gameid=").append(Community.getInstacne().getGameID());
			buf.append("&method=bindthird");
			buf.append("&pid=").append(Community.PlatID);
			buf.append("&scid=")
					.append(Community.getInstacne().getSubChannel());
			buf.append("&tid=").append(tid);
			buf.append("&type=").append(type);
			getInstance().httpConnect(loginHost, buf.toString(), listener,
					_cSecret, IRequest.HTTP_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// getToken
	public static void reqThirdToken(final int type, final String uid,
			final HttpReceiveEventCallBack eventCallBack) {
		HttpReceiveEventCallBack callBack = new HttpReceiveEventCallBack() {

			@Override
			public void onReceive(HttpEvent event) {
				if (event._result == HttpEvent.LOGIN_SUCCESSED) {
					httpGetThirdToken(uid, type, eventCallBack); // 获取token请求
				} else {
					if (eventCallBack != null) {
						eventCallBack.onReceive(event);
					}
				}
			}
		};
		reqHttpHost(callBack);
	}

	private static void httpGetThirdToken(final String uid, final int type,
			final HttpReceiveEventCallBack callBack) {
		httpListener listener = new httpListener() {

			@Override
			public void onReceive(byte[] array) {
				String strXml = TDataInputStream.getUTF8String(array);
				String status = AndrUtil.parseTagValueXml(strXml, "result",
						"status");
				String msg = AndrUtil.parseTagValueXml(strXml, "result", "msg");
				String token = null;
				if ("0".equals(status)) {// 成功
					token = AndrUtil
							.parseTagValueXml(strXml, "result", "token");
				}
				if (callBack != null) {
					HttpEvent event = getInstance().new HttpEvent();
					event._message = ("0".equals(status)) ? token : msg; // 成功为token，失败为msg
					event._result = ("0".equals(status)) ? HttpEvent.LOGIN_SUCCESSED
							: HttpEvent.LOGIN_FAILED;
					callBack.onReceive(event);
				}
			}

			@Override
			public void onNetError(String msg) {
				if (callBack != null) {
					HttpEvent event = getInstance().new HttpEvent();
					event._message = msg;
					event._result = HttpEvent.LOGIN_FAILED;
					callBack.onReceive(event);
				}
			}
		};

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("").append("cid=")
					.append(Community.getInstacne().getChannel());
			sb.append('&').append("ext=").append("");
			sb.append('&').append("format=").append("xml");
			sb.append('&').append("gameid=")
					.append(Community.getInstacne().getGameID());
			sb.append('&').append("gid=").append("10022");
			sb.append("&method=").append("gettoken");
			sb.append('&').append("oid=").append(uid);
			sb.append('&').append("pid=").append(Community.PlatID);
			sb.append('&').append("scid=")
					.append(Community.getInstacne().getSubChannel());
			sb.append('&').append("sid=").append("0");
			sb.append('&').append("type=").append(type);
			getInstance().httpConnect(loginHost, sb.toString(), listener,
					_cSecret, IRequest.HTTP_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// getToken end
}
