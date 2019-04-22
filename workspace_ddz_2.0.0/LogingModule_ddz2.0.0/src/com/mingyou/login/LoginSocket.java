/**
 * 
 */
package com.mingyou.login;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import com.login.utils.HostAddressPool;
import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.community.AddressStrategyImpl;
import com.mingyou.community.Community;
import com.mingyou.community.MUserInfo;
import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mingyou.login.struc.HCMD_LOGIN_V2;
import com.mingyou.login.struc.HCMD_LOGIN_V2_ACCOUNTS;
import com.mingyou.login.struc.HMSUB_CMD_LOGIN_AT;
import com.mingyou.login.struc.VersionInfo;
import com.minyou.android.net.AddressStrategy;
import com.minyou.android.net.HttpConnector;
import com.minyou.android.net.IPConfigInterface.IPConfigCallBackListener;
import com.minyou.android.net.IRequest;
import com.minyou.android.net.NetService;
import com.minyou.android.net.TcpConnector;
import com.multilanguage.MultilanguageManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.log.MLog;
import com.mykj.comm.util.AndrUtil;
import com.mykj.comm.util.MTimer;
import com.mykj.comm.util.TCAgentUtil;

import debug.IP_CONFIG_FILE;

/**
 * @author Jason
 * 
 */
public class LoginSocket {

	private final String TAG = "LoginSocket";

	/********** 4.3. 心跳消息 主协议 **********/
	public static final short MDM_HEARTBEAT = 11;

	/** 4.3. 心跳消息 子协议： */
	public static final short SUB_KN_DETECT_SOCKET = 1;

	/********** 4.4.1. 登录操作相关 主协议 **********/
	public static final short MDM_LOGIN = 12;

	/** 登录失败 */
	public static final short MSUB_CMD_LOGIN_V2_ERR = 6;

	/** 登录成功 */
	public static final short MSUB_CMD_LOGIN_V2_USERINFO = 7;

	/** 登录成功后返回游戏版本信息(android不处理) */
	public static final short MSUB_CMD_LOGIN_V2_VERINFO = 8;

	/** 4.4.1.3. Token登录请求--用于从移动游戏大厅启动 */
	public static final short MSUB_CMD_LOGIN_TOKEN = 11;

	/** 4.4.1.4. AT登录请求 子协议 */
	public static final short MSUB_CMD_LOGIN_AT = 12;

	/** 4.4.1.5. 免注册登录请求 子协议 */
	public static final short MSUB_CMD_UN_REG_LOGIN = 13;

	/** 4.4.1.6.新帐号密码登录请求 子协议 */
	public static final short MSUB_CMD_LOGIN_V3_ACCOUNTS = 18;

	/** 转发登录主协议 **/
	public static final short LS_TRANSIT_LOGON = 18;

	/** 4.4.2.4系统消息请求 子协议 **/
	public static final short MSUB_SYSMSG_REQUEST = 101;

	/** 登录公告返回 子协议 **/
	public static final short MSUB_SYSMSG_LOGON_NOTICE = 102;

	/** 系统消息返回 ***/
	public static final short MSUB_SYSMSG_ROLL_MSG = 103;

	/** 系统留言返回(个人消息)104 **/
	public static final short MSUB_SYSMSG_LEAVE_WORD = 104;

	// 快速断线重回协议
	/** 4.7.1.房间主协议 **/
	static final short MDM_ROOM = 14;

	/** 快速断线重回：请求 **/
	static final short MSUB_CMD_CUT_REUTRN_ROOM_REQ = 15;

	/** 快速断线重回结果：返回 **/
	static final short MSUB_CMD_CUT_RESUTNR_ROOM_RESP = 16;

	//

	NetPrivateListener _MODIFY_PWD_RESP = null;

	NetPrivateListener nPListener = null;

	private NetPrivateListener _DETECT_SOCKET = null;

	// 网络建立的重连次数
	private static byte RE_CONNTION_COUNT = 3;

	AddressStrategyImpl address;

	AddressStrategyImpl addressReConnect = null;

	/**
	 * @Title: sendHEARTBEAT
	 * @Description: 发送心跳包
	 * @version: 2011-5-12 下午04:09:26
	 */
	public void sendHEARTBEAT() {
		if (_DETECT_SOCKET == null) {
			short mds[][] = new short[][] { { MDM_HEARTBEAT,
					SUB_KN_DETECT_SOCKET } };
			_DETECT_SOCKET = new NetPrivateListener(mds) {

				@Override
				public boolean doReceive(NetSocketPak netSocketPak) {
					// Log.e(TAG, "心跳返回！");
					sendHEARTBEAT();
					return true;
				}
			};
			_DETECT_SOCKET.setOnlyRun(false);
			// TcpShareder.getInstance().addTcpListener(_DETECT_SOCKET);
		}
		TcpShareder.getInstance().reqNetData(
				new NetSocketPak(MDM_HEARTBEAT, SUB_KN_DETECT_SOCKET));
	}

	private NetPrivateListener _GetProxyInfo = null;

	/**
	 * @Title: sendGetProxyInfo
	 * @Description: 发送获取代理信息协议--穿透串
	 * @param url
	 * @version: 2011-5-17 下午01:43:23
	 */
	public void sendGetProxyInfo(String url) {
		if (_GetProxyInfo == null) {
			_GetProxyInfo = new NetPrivateListener(true) {

				@Override
				public boolean doReceive(NetSocketPak netSocketPak) {
					return false;
				}

				@Override
				public boolean doReceive(byte[] data) {
					// 接受穿透数据
					if (data == null || data.length <= 0) {
						return false;
					}
					final String dataStr = TDataInputStream.getUTF8String(data)
							.trim();
					MLog.c1(TAG, "穿透返回=" + dataStr);
					int index = "CONNECT RESULT=".length();
					int len = dataStr.length();
					// 默认错误：自定义，处理返回数据解析异常问题
					char c = '4';
					if (len >= index) {
						c = dataStr.charAt(index);
					}
					if (c < '0' || c > '9') {
						c = '4';
					}
					if (c >= '0' && c <= '9') {
						// setLoginType(LOGIN_AT);
						final byte bytec = Byte.parseByte(c + "");
						switch (bytec) {
						case 0:
							addNetTask();
							// 穿透成功，发送登录
							int loginType = getLoginType();
							MLog.c1(TAG, "loginType2=" + loginType);
							TcpShareder.getInstance().setUseNetData(true);
							// HProgress.getIntance().setProgressText(HLogin.getProgressTip(HErrorCode.Login_State_Login));
							if (loginType == LOGIN_MobileGameHall_Token) {
								if (LoginInfoManager.getInstance()
										.getMobileGameHallToken() == null) {
									// 过滤，当移动大厅启动但没有token时
									loginType = LOGIN_FREE;
								}
							}
							// TCAgentUtil.onTCAgentEvent("穿透返回开始登录",
							// "loginType=" + loginType);
							switch (loginType) {
							case LOGIN_ACC_PASS:
								HCMD_LOGIN_V2_ACCOUNTS loginMess = new HCMD_LOGIN_V2_ACCOUNTS(
										LoginInfoManager.getInstance()
												.getUserName(),
										LoginInfoManager.getInstance()
												.getUserPassWord());
								sendMSUB_CMD_LOGIN_V3_ACCOUNTS(loginMess);
								break;
							case LOGIN_AT:
								HMSUB_CMD_LOGIN_AT loginMess1 = new HMSUB_CMD_LOGIN_AT(
										LoginInfoManager.getInstance()
												.getToken());
								// HMSUB_CMD_LOGIN_AT loginMess1 = new
								// HMSUB_CMD_LOGIN_AT("rJ5BjuP3gv7xgruhvbt9CQhaJqz+ZjBbHUpQyjZorAxCJh1bzrvAmLZ4ot2m/iApc38cuzdGC1shgYk3eLDClPdrbfIdFrUhpC/21F5z+mAn3WMiJL86JJpCMl83xB15");
								sendMSUB_CMD_LOGIN_AT(loginMess1);
								break;
							case LOGIN_FREE:
								HCMD_LOGIN_V2 loginFree = new HCMD_LOGIN_V2();
								sendMSUB_CMD_UN_REG_LOGIN(loginFree);
								break;
							case LOGIN_MobileGameHall_Token: // 穿透成功后，传入移动游戏大厅的token登录
																// 20130131 FWQ
								HMSUB_CMD_LOGIN_AT loginToken = new HMSUB_CMD_LOGIN_AT(
										LoginInfoManager.getInstance()
												.getMobileGameHallToken());
								sendMSUB_CMD_LOGIN_Mobile_GameHall_TOKEN(loginToken);
								break;
							}
							break;
						case 1:
						case 2:
						case 3:
						case 4: // 自定义，处理返回数据解析异常问题
							// openSocket();
							// 穿透失败，显示错误信息
							// MCommandListener listener = new
							// MCommandListener() {
							// public void commandAction(MCommand c, MView v) {
							// if (c != null &&
							// c.name.equals(StringValues.CONFIRM)) {
							// HUiTools.removeAlert();
							// HLoginSocket.getInstance().closeConn();
							//
							// HLogin.getInstance().backLogin(false);
							// }
							// }
							// };
							// MCommand cmd =
							// HCmdFactory.getInstance().createNormalCmd(StringValues.CONFIRM);
							// HUiTools.showAlert(StringValues.TIP,
							// "服务器繁忙,请稍候再试(" +
							// HErrorCode.ERROR_LOGIN_GATEWAY + "_" + bytec +
							// ")...", cmd,
							// null, listener);
							break;
						}
					}
					return true;
				}
			};
			TcpShareder.getInstance().addTcpListener(_GetProxyInfo);
			_GetProxyInfo = null;
		}
		TcpShareder.getInstance().reqGetProxyInfo(url);
	}

	private NetPrivateListener _LOGIN = null;

	/**
	 * @Title: sendMSUB_CMD_LOGIN_AT
	 * @Description: 使用AT登录到服务器
	 * @param loginWhite
	 * @version: 2011-5-12 下午06:23:00
	 */
	public void sendMSUB_CMD_LOGIN_AT(HMSUB_CMD_LOGIN_AT loginAccounts) {
		if (loginAccounts == null) {
			return;
		}
		addLoginListener();
		TcpShareder.getInstance().reqNetData(
				new NetSocketPak(MDM_LOGIN, MSUB_CMD_LOGIN_AT, loginAccounts
						.getData()));
	}

	/**
	 * @Title: sendMSUB_CMD_LOGIN_Mobile_GameHall_TOKEN
	 * @Description: 使用移动游戏大厅Token登录到服务器
	 * @param loginWhite
	 * @version: 2013-1-31 下午04:52:00 fwq
	 */
	public void sendMSUB_CMD_LOGIN_Mobile_GameHall_TOKEN(
			HMSUB_CMD_LOGIN_AT loginAccounts) {
		if (loginAccounts == null) {
			return;
		}
		addLoginListener();
		TcpShareder.getInstance().reqNetData(
				new NetSocketPak(MDM_LOGIN, MSUB_CMD_LOGIN_TOKEN, loginAccounts
						.getData()));
	}

	/**
	 * 
	 */
	private void addLoginListener() {
		RecoverForLoginStrategy.getInstance().registrationLoginStrategy();
		// 开始登录超时定时器
		if (_LOGIN == null) {
			short mds[][] = new short[][] {
					{ MDM_LOGIN, MSUB_CMD_LOGIN_V2_USERINFO },
					{ MDM_LOGIN, MSUB_CMD_LOGIN_V2_ERR },
					{ MDM_LOGIN, MSUB_CMD_LOGIN_V2_VERINFO /* 登录成功后的版本信息不处理 */} };
			_LOGIN = new NetPrivateListener(mds) {
				public boolean doReceive(NetSocketPak data) {

					switch (data.getSub_gr()) {
					case MSUB_CMD_LOGIN_V2_USERINFO: // 登录成功
						// MTalkingDataGA.onEvent(Community.getContext(),"登陆成功");
						MLog.c1(TAG, "登录成功，取消超时检测定时器");
						RecoverForLoginError.getInstance().cancelRecover();
						RecoverForLoginStrategy.getInstance()
								.destroyLoginStrategy();
						return doReceiveMSUB_CMD_LOGIN_V2_USERINFO(data);
					case MSUB_CMD_LOGIN_V2_ERR: // 登录失败
						// MTalkingDataGA.onEvent(Community.getContext(),"登陆失败");
						MLog.c1(TAG, "登录失败，取消超时检测定时器");
						RecoverForLoginError.getInstance().cancelRecover();
						RecoverForLoginStrategy.getInstance()
								.destroyLoginStrategy();
						return doReceiveMDM_LOGIN_MSUB_CMD_LOGIN_V2_ERR(data);
					case MSUB_CMD_LOGIN_V2_VERINFO: // 登录成功后的版本信息
						return true;
					}
					return false;

				}
			};
		}
		TcpShareder.getInstance().addTcpListener(_LOGIN);
	}

	/**
	 * @Title: sendMSUB_CMD_LOGIN_AT
	 * @Description: 免注册登录到服务器
	 * @param loginWhite
	 * @version: 2011-5-12 下午06:23:00
	 */
	public void sendMSUB_CMD_UN_REG_LOGIN(HCMD_LOGIN_V2 loginAccounts) {
		if (loginAccounts == null) {
			return;
		}
		addLoginListener();
		TcpShareder.getInstance().reqNetData(
				new NetSocketPak(MDM_LOGIN, MSUB_CMD_UN_REG_LOGIN,
						loginAccounts.getData()));
	}

	/**
	 * @Title: sendMSUB_CMD_LOGIN_V2_WHITE
	 * @Description: 使用新用户名密码登录到服务器
	 * @param loginWhite
	 * @version: 2011-5-12 下午06:23:00
	 */
	public void sendMSUB_CMD_LOGIN_V3_ACCOUNTS(
			HCMD_LOGIN_V2_ACCOUNTS loginAccounts) {
		if (loginAccounts == null) {
			return;
		}
		addLoginListener();
		TcpShareder.getInstance().reqNetData(
				new NetSocketPak(MDM_LOGIN, MSUB_CMD_LOGIN_V3_ACCOUNTS,
						loginAccounts.getData()));
	}

	/** 登录方式：帐号密码登录 */
	public static final byte LOGIN_ACC_PASS = 2;

	/** AT登录 */
	public static final byte LOGIN_AT = 3;

	/** 登录方式：TAT登录（同免注册登录 */
	public static final byte LOGIN_FREE = 4;

	/** 登录方式：移动游戏大厅Token登录（同AT */
	public static final byte LOGIN_MobileGameHall_Token = 5;

	/** 登录方式 **/
	protected int _curLoginType = -1;

	private static LoginSocket _instance = null;

	public static LoginSocket getInstance() {
		if (_instance == null) {
			_instance = new LoginSocket();
		}
		return _instance;
	}

	private Handler _handler = null;

	private LoginSocket() {
		_handler = new Handler();
	}

	/**
	 * socket连接函数
	 * 
	 * @param shared
	 *            穿透成功专用事件回调
	 */
	protected void connectTcp() {
		MLog.c1(IP_CONFIG_FILE.MY_DETECTGAMEDATA, "connectTcp");
		MLog.c1(IP_CONFIG_FILE.MY_DETECTGAMEDATA, "账号类型="
				+ LoginInfoManager.getInstance().getLoginType());
		MLog.c1(IP_CONFIG_FILE.MY_DETECTGAMEDATA,
				"启动方式=" + LoginInfoManager.getStartType());
		// 正常登陆后复位断线重连次数
		RecoverForDisconnect.getInstance().RE_COUNT = 0;
		// 创建连接回调
		final TcpConnector.IConnectCallBack connectCallBack = new TcpConnector.IConnectCallBack() {

			public void connectSucceed() {
				if (IP_CONFIG_FILE.isSendProxyInfo()) {
					// TCAgentUtil.onTCAgentEvent("连接成功-发送穿透");
					MLog.v(TAG, "connectSucceed-发送穿透信息");
					TcpShareder.getInstance().setUseNetData(false);
					// 连接成功后立即穿透
					final String url = "CONNECT1.0 cpID=000001,serviceID=000000000001,fid=1000\r\n\r\n";
					// 链接链接到 刘升华处的服务器
					// final String
					// url="CONNSRV1.0 serverhost=192.168.5.120:9001\r\n\r\n";
					// final String url =
					// "CONNSRV1.0 serverhost=192.168.5.23:9001\r\n\r\n";
					sendGetProxyInfo(url);
				} else {
					MLog.v(TAG, "connectSucceed-开始登录");
					addNetTask(); // 启动心跳任务
					startLogin();
				}
			}

			public void connectFailed(final Exception e) {
				_handler.post(new Runnable() {
					public void run() {
						TCAgentUtil.onTCAgentEvent("多次连接失败-登录失败");
						if (_curLoginListener != null) {
							Message msg = new Message();
							msg.obj = "网络连接失败！";
							_curLoginListener.onFiled(msg, 0);
							_curLoginListener = null;
						}
					}
				});
			}
		};

		if (address == null) {
			address = new AddressStrategyImpl(IPConfigManager.getInstacne());
		}

		if ((!IP_CONFIG_FILE.isOuterNet())
				|| IPConfigManager.getInstacne().hasIPConfigInfo()) {
			MLog.d(TAG, "有IP和端口，直接登录");
			openSocket(address, connectCallBack, RE_CONNTION_COUNT);
		} else {
			MLog.d(TAG, "没有有IP和端口，增加登录回调接口，等待回调");
			IPConfigManager.getInstacne().addConfigCallBackLis(
					new IPConfigCallBackListener() {
						public void onSucceed() {
							MLog.d(TAG, "已经获取到IP和端口，继续登录");
							openSocket(address, connectCallBack,
									RE_CONNTION_COUNT);
						}

						public void onFailed() {
							MLog.d(TAG, "获取IP和端口失败了，不能继续登录");
							connectCallBack.connectFailed(new Exception(
									"获取Ip配置失败"));
						}
					});
		}

	}

	private void startLogin() {
		RecoverForLoginError.getInstance().registration();
		int loginType = getLoginType();
		MLog.c1(TAG, "登陆类型=" + loginType);
		if (loginType == LOGIN_MobileGameHall_Token) {
			if (LoginInfoManager.getInstance().getMobileGameHallToken() == null) {
				// 过滤，当移动大厅启动但没有token时
				loginType = LOGIN_FREE;
			}
		}
		switch (loginType) {
		case LOGIN_ACC_PASS:
			HCMD_LOGIN_V2_ACCOUNTS loginMess = new HCMD_LOGIN_V2_ACCOUNTS(
					LoginInfoManager.getInstance().getUserName(),
					LoginInfoManager.getInstance().getUserPassWord());
			sendMSUB_CMD_LOGIN_V3_ACCOUNTS(loginMess);
			break;
		case LOGIN_AT:
			HMSUB_CMD_LOGIN_AT loginMess1 = new HMSUB_CMD_LOGIN_AT(
					LoginInfoManager.getInstance().getToken());
			sendMSUB_CMD_LOGIN_AT(loginMess1);
			break;
		case LOGIN_FREE:
			HCMD_LOGIN_V2 loginFree = new HCMD_LOGIN_V2();
			sendMSUB_CMD_UN_REG_LOGIN(loginFree);
			break;
		case LOGIN_MobileGameHall_Token: // 穿透成功后，传入移动游戏大厅的token登录
			// 20130131 FWQ
			HMSUB_CMD_LOGIN_AT loginToken = new HMSUB_CMD_LOGIN_AT(
					LoginInfoManager.getInstance().getMobileGameHallToken());
			sendMSUB_CMD_LOGIN_Mobile_GameHall_TOKEN(loginToken);
			break;
		}
	}

	private boolean _isShuntDown = true;

	/**
	 * socket连接函数
	 * 
	 * @param shared
	 *            快速重连专用监听器参数
	 */
	void openSocket(AddressStrategy _address,
			final TcpConnector.IConnectCallBack _callBack,
			final int _reConnectCount) {
		if (!_isShuntDown) {
			new Exception("openSocket重复调用").printStackTrace();
			return;
		}
		_isShuntDown = false;
		// TCAgentUtil.onTCAgentEvent("开始连接");
		MLog.c1(TAG, "当前网络类型=" + LoginUtil.getNetWorkType());

		TcpShareder.getInstance().createTcpConnector(_address);
		TcpShareder.getInstance().connect(_callBack, _reConnectCount);
	}

	private SocketLoginListener _curLoginListener = null;

	/**
	 * 免注册登录
	 */
	public void freeLogin(SocketLoginListener listener) {
		TCAgentUtil.onTCAgentEvent("免注册登录");
		// 首次免注册登录
		_curLoginListener = listener;
		if (LoginInfoManager.getInstance().isHasNativeLoginInfo()
				&& LoginInfoManager.getInstance().getUserName() != null) {
			setLoginType(LOGIN_AT);
		} else {
			setLoginType(LOGIN_FREE);
		}
		LoginInfoManager.getInstance().setLoginType(AccountItem.ACC_TYPE_TEMP);
		connectTcp();
	}

	public void accountLogin(final String name, final String pwd,
			SocketLoginListener listener) {
		AccountItem item = new AccountItem(name, pwd, null,
				AccountItem.ACC_TYPE_COMM, 0, null,null);
		accountLogin(item, listener);
	}

	/**
	 * 帐号登录方法，自动识别，登录类型，优先级：帐号密码，AT，免注册登录
	 * 
	 * @param account
	 */
	public void accountLogin(final AccountItem account,
			SocketLoginListener listener) {
		TCAgentUtil.onTCAgentEvent("帐号登录");
		if (account == null || !account.isValid()) {
			if (listener != null) {
				Message msg = new Message();
				msg.obj = "帐号信息有误！";
				listener.onFiled(msg, 0);
			}
			return;
		}
		_curLoginListener = listener;
		LoginInfoManager.getInstance().setCurAccountItem(account);
		// if (account.isValidAccAndPass()) {
		// setLoginType(LOGIN_ACC_PASS);
		// } else if (account.isValidToken()) {
		// setLoginType(LOGIN_AT);
		// } else {
		// setLoginType(LOGIN_FREE);
		// LoginInfoManager.getInstance().cleanCurAccountInfo(); // 清除当前登录信息
		// }
		autoSetLoginType();
		connectTcp();
	}

	private void autoSetLoginType() {
		if (LoginInfoManager.getInstance().isVaildToken()) { // 最优先AT登录
			setLoginType(LOGIN_AT); // 设置AT码登录
		} else if (LoginInfoManager.getInstance().isVaildAccAndPass()) {
			setLoginType(LOGIN_ACC_PASS); // 设置帐号密码登录
		} else {
			setLoginType(LOGIN_FREE); // 设置免注册登录
			LoginInfoManager.getInstance().cleanCurAccountInfo(); // 清除当前登录信息
		}
	}

	/**
	 * @return
	 */
	protected int getLoginType() {
		return _curLoginType;
	}

	private void setLoginType(final byte st) {
		_curLoginType = st;
	}

	/**
	 * @param data
	 * @return
	 */
	private boolean doReceiveMDM_LOGIN_MSUB_CMD_LOGIN_V2_ERR(NetSocketPak data) {
		TDataInputStream dis = data.getDataInputStream();
		// 设置为低位在前、高位在后
		if (dis == null) {
			return true;
		}
		LoginInfoManager.getInstance().cleanCurPassWord(); // 登录失败清空已缓存密码
		// LoginInfoManager.getInstance().cleanLastAccountInfo();
		final byte err_code = dis.readByte();
		String msg = null;
		if (err_code != 0) {
			msg = dis.readUTFByte() /* + "(" + err_code + ")" */;
		}
		TCAgentUtil.onTCAgentEvent("登录失败返回", "errCode=" + err_code + "/"
				+ "msg=" + msg);
		// if (err_code == 1) {
		// msg = "账号或密码错误，请重新输入" + "(" + err_code + ")";
		// } else if (err_code == 103) {
		// msg = "网络繁忙，密码修改失败，请稍后再试" + "(" + err_code + ")";
		// } else if (err_code == 22) { // AT格式错误
		// // freeLogin();
		// // HLogin.getInstance().loginAgainFree();
		// // return true;
		// msg = "帐号信息有误" + "(" + err_code + ")";
		// } else {
		// if (msg == null || msg.length() <= 0) {
		// msg = "网络繁忙，登陆失败，请稍后再试" + "(" + err_code + ")";
		// } else {
		// msg = msg + "(" + err_code + ")"; // 2012.02.13登录失败信息显示不正确
		// }
		// }
		if (msg == null) {
			msg = MultilanguageManager.getInstance().getValuesString(
					"socketLoginTip2")
					+ "(" + err_code + ")";
		}
		/** 白名单绑定失败 */
		if (err_code != 102) {
			loginFiledAction(msg);
		}
		MLog.c1(IP_CONFIG_FILE.MY_DETECTGAMEDATA, "登陆失败=" + msg);
		return true;
	}

	protected void loginSucceedAction(final String msg) {
		if (_curLoginListener != null) {
			Message mes = new Message();
			mes.obj = msg;
			_curLoginListener.onSuccessed(mes);
			_curLoginListener = null;
		}
	}

	/**
	 * 
	 */
	protected void loginFiledAction(final String msg) {
		if (_curLoginListener != null) {
			Message mes = new Message();
			mes.obj = msg;
			_curLoginListener.onFiled(mes, 0);
			_curLoginListener = null;
		}
	}

	/**
	 * @Title: doReceiveMDM_LOGIN_MSUB_CMD_LOGIN_V2_USERINFO
	 * @Description: 解析登录成功的数据 // 1 userId DWORD 4 用户ID // 2 headNo DWORD 4 头像索引
	 *               // 3 sex BYTE 1 性别 // 4 memberLevel DWORD 4 会员等级 // 5 exp
	 *               DWORD 4 用户经验 // 6 PassLen BYTE 1 密码串长度 // 7 pass char
	 *               PassLen 密码串 // 8 AccountsLen BYTE 1 帐号长度 // 9 accounts Utf8
	 *               AccountsLen 帐号字符串 // 10 NicknameLen BYTE 1 昵称长度 // 11
	 *               nickname Utf8 NicknameLen 昵称 // 12 statusBit DWORD 4
	 *               状态位（每个比特表示一种状态，包含32中状态）
	 * @param data
	 * @return
	 * @version: 2011-5-16 下午02:44:54
	 */
	private boolean doReceiveMSUB_CMD_LOGIN_V2_USERINFO(NetSocketPak data) {
		// TCAgentUtil.onTCAgentEvent("登录成功返回");
		
		/*******************************************
		 * NOTICE:
		 * 这协议在分区模块还有一个，完善账号成功时下发的，请保持一致
		 ******************************************/
		TDataInputStream dis = data.getDataInputStream();
		/** 添加玩家信息完整性检查 11-11-02 */
		MUserInfo userInfo = new MUserInfo();
		/** 用户ID */
		userInfo.userId = dis.readInt();
		/** 用户头像索引 */
		userInfo.headNo = dis.readInt();
		/** 用户性别 */
		userInfo.cbGender = dis.readByte();
		/** 用户会员等级 */
		userInfo.memberOrder = (byte) dis.readInt();
		/** 用户经验 */
		userInfo.lExperience = dis.readInt() + "";
		/** 用户密码 */
		/* String pass = */dis.readUTFByte();
		/** 用户帐号 */
		userInfo.account = dis.readUTFByte();
		/** 用户昵称 */
		userInfo.nickName = dis.readUTFByte();
		/**
		 * StatusBit 状态位定义（32个bit中） 第 1 bit: 0-表示不能购买道具 1-可以购买道具（PC为0x00000001）
		 * 第 2 bit: 0-表示MTK购买走社区流程 1-可以MTK购买走MTK流程（PC为0x00000002）
		 */
		userInfo.statusBit = dis.readInt();
		/** 省市编码 */
		/* String AreaCode = */dis.readUTF(4);
		/** 用户token串 */
		final String Token = dis.readUTFByte();
		/** 登录类型 */
		final byte loginType = dis.readByte();
		/** 用户乐豆 */
		userInfo.lBean = dis.readInt();
		dis.readInt();// 用户管理权限
		userInfo.muid = dis.readInt();// 移动社区ID(MUID)
		userInfo.guid = (int) dis.readLong();//用户guid
		
		userInfo.nickColor = dis.readInt();
		userInfo.isVipExpired = dis.readByte();
		
		userInfo.faceIdValue=dis.readByte();//2014-9-26用户上传头像功能新增
		dis.readInt(); //完善账号奖励
		/** 设置我的用户信息对象 */
		Community.setSelftUserInfo(userInfo);
		MLog.c1(TAG, "loginType=" + loginType);
		if (loginType == LOGIN_FREE) { // tat登录成功
			LoginInfoManager.getInstance().setIsBind(false); // 只有游客帐号才绑定
			AccountItem item = new AccountItem(AccountItem.ACC_TEMP,
					AccountItem.NO_PASS, Token, AccountItem.ACC_TYPE_TEMP,
					userInfo.userId, LoginInfoManager.getInstance()
							.getCurThirdAccName(),LoginInfoManager.getInstance()
							.getCurThirdAccUID());
			LoginInfoManager.getInstance().setCurAccountItem(item);
			MLog.c1(TAG, "当前帐号TAT类型");
		} else { // at登录成功
			MLog.c1(TAG, "当前帐号----------------------------AT类型");
			if (LoginInfoManager.getInstance().getLoginType() == AccountItem.ACC_TYPE_COMM) { // 登录是普通帐号
				AccountItem item = new AccountItem(userInfo.account,
						LoginInfoManager.getInstance().getUserPassWord(),
						Token, AccountItem.ACC_TYPE_COMM, userInfo.userId,
						LoginInfoManager.getInstance().getCurThirdAccName(),LoginInfoManager.getInstance()
						.getCurThirdAccUID());
				LoginInfoManager.getInstance().setCurAccountItem(item);
			} else if (LoginInfoManager.getInstance().getLoginType() == AccountItem.ACC_TYPE_TEMP) { // 此处专为伪造游客帐号处理
				LoginInfoManager.getInstance().setIsBind(true);
				AccountItem item = new AccountItem(AccountItem.ACC_PHONE,
						AccountItem.NO_PASS, Token, AccountItem.ACC_TYPE_TEMP,
						userInfo.userId, LoginInfoManager.getInstance()
								.getCurThirdAccName(),LoginInfoManager.getInstance()
								.getCurThirdAccUID());
				LoginInfoManager.getInstance().setCurAccountItem(item);
			}/*
			 * else if (LoginInfoManager.getInstance().getLoginType() ==
			 * AccountItem.ACC_TYPE_CMCC) { AccountItem item = new
			 * AccountItem(AccountItem.ACC_CMCC, AccountItem.NO_PASS, Token,
			 * AccountItem.ACC_TYPE_CMCC, userInfo.userId);
			 * LoginInfoManager.getInstance().setCurAccountItem(item); }
			 */else if (LoginInfoManager.getInstance().getLoginType() == AccountItem.ACC_TYPE_THIRD) {
				AccountItem item = new AccountItem(userInfo.account, null,
						Token, AccountItem.ACC_TYPE_COMM, userInfo.userId,
						LoginInfoManager.getInstance().getCurThirdAccName(),LoginInfoManager.getInstance()
						.getCurThirdAccUID());
				LoginInfoManager.getInstance().setCurAccountItem(item);
			} else {
				TCAgentUtil.onTCAgentEvent("未知帐号类型登录成功", "loginType="
						+ LoginInfoManager.getInstance().getLoginType());
				AccountItem item = new AccountItem(userInfo.account, null,
						Token, AccountItem.ACC_TYPE_COMM, userInfo.userId,
						LoginInfoManager.getInstance().getCurThirdAccName(),LoginInfoManager.getInstance()
						.getCurThirdAccUID());
				LoginInfoManager.getInstance().setCurAccountItem(item);
				String msg = "login recv is error loginType="
						+ LoginInfoManager.getInstance().getLoginType();
				MLog.c1(TAG, "未知帐号类型登录成功" + msg);
			}
		}
		//
		MLog.c1(TAG, "登录成功");
		LoginInfoManager.getInstance().updateAccountInfo(); // 必须先更新帐号信息，在保存
		if (_curLoginListener != null) {
			Message mes = new Message();
			mes.obj = MultilanguageManager.getInstance().getValuesString(
					"socketLoginTip3");
			_curLoginListener.onSuccessed(mes);
			_curLoginListener = null;
		}
		// AccountType type = LoginInfoManager.getInstance().getLoginType() ==
		// AccountItem.ACC_TYPE_TEMP ? AccountType.ANONYMOUS
		// : AccountType.REGISTERED;
		// MTalkingDataGA.login(userInfo.userId, type);
		// 再次请求并更新登录配置信息

		return true;
	}

	private TimerTask _heartTask = null;

	/**
	 * 
	 */
	void addNetTask() {
		if (_heartTask == null) {
			// TCAgentUtil.onTCAgentEvent("心跳启动");
			_heartTask = new TimerTask() {
				public void run() {
					sendHEARTBEAT();
				}
			};
			int heartTime = 3000; // 心跳任务为3秒
			if (AndrUtil.isWifi(Community.getContext())) {
				heartTime = 10000; // wifi 10秒
			}
			MTimer.getInstacne().schedule(_heartTask, 3000, heartTime);
		}
	}

	public void cancelNetTask() {
		if (_heartTask != null) {
			_heartTask.cancel();
			_heartTask = null;
		}
	}

	/**
	 * 第三方登录方法
	 * 
	 * @param listener
	 * @param token
	 */
	public void autoThirdPartyLogin(final SocketLoginListener listener,
			String token, final String accName,final String accUID) {
		// TCAgentUtil.onTCAgentEvent("第三方登录");
		_curLoginListener = listener;
		// LoginInfoManager.getInstance()._curToken = token;
		// LoginInfoManager.getInstance()._loginType=LoginInfoManager.ACC_TYPE_THIRD;
		AccountItem item = new AccountItem(null, null, token,
				AccountItem.ACC_TYPE_THIRD, 0, accName,accUID);
		LoginInfoManager.getInstance().setCurAccountItem(item);
		setLoginType(LOGIN_AT); // 设置AT码登录
		connectTcp();
	}

	/**
	 * 自动登录，仅用于版本信息验证后调用
	 */
	public void autoLogin(final SocketLoginListener listener) {
		// TCAgentUtil.onTCAgentEvent("自动登录");
		boolean isMobileGameHall = false;
		if (LoginInfoManager.getInstance().getMobileGameHallToken() != null
				&& LoginInfoManager.getInstance().getMobileGameHallToken()
						.length() > 0) {
			isMobileGameHall = true;
		}
		LoginInfoManager.getInstance().loadNativeLoginInfo(); // 读取本地信息
		_curLoginListener = listener;
		if (isMobileGameHall) {
			// 设置为 移动游戏大厅启动方式启动
			setLoginType(LOGIN_MobileGameHall_Token);
			LoginInfoManager.getInstance().setLoginType(
					AccountItem.ACC_TYPE_COMM);
		} else {
			autoSetLoginType();
		}
		connectTcp();
	}

	/**
	 * 重连专用关闭网络方法，不移除网络监听器
	 */
	public void loginAgainCloseNet() {
		_isShuntDown = true;
		// TCAgentUtil.onTCAgentEvent("断线重连专用-关闭网络");
		_LOGIN = null;
		cancelNetTask();
		MLog.c1("LoginSocket", "loginAgainCloseNet");
		TcpShareder.getInstance().closeSocket();
	}

	/**
	 * 关闭当前网络
	 */
	public void closeNet() {
		_isShuntDown = true;
		TCAgentUtil.onTCAgentEvent("关闭网络");
		// MLog.printStack("closeNet");
		_LOGIN = null;
		cancelNetTask();
		// if (_task != null) {
		// _task.cancel();
		// _task = null;
		// }
		TcpShareder.getInstance().closeTcp();
		// if (_sharedListener != null) {
		// _sharedListener.onNetClosed("网络不通畅，请返回重新登录，建议您优先使用wifi/3G接入点连网！");
		// } 
	}

	/**
	 * 请求版本信息
	 * 
	 * @param id
	 *            大厅：0，斗地主：100...
	 * @param listener
	 */
	public void reqVersionInfo(final int id, final SocketLoginListener listener) {
		// TCAgentUtil.onTCAgentEvent("请求版本信息");
		// http请求验证版本信息
		final HttpConnector _httpConnector = LoginHttp
				.createLoginHttpConnector();
		IRequest request = new IRequest() {
			@Override
			public void doError(Message msg) {
				// TCAgentUtil.onTCAgentEvent("版本信息-返回失败");
				NetService.getInstance().removeHttpConnector(
						_httpConnector.getTarget());
				if (listener != null) {
					Message m = new Message();
					m.obj = MultilanguageManager.getInstance().getValuesString(
							"socketLoginTip3");
					listener.onFiled(m, 0);
				}
			}

			public void handler(final byte[] buf) {
				// TCAgentUtil.onTCAgentEvent("版本信息-返回成功");
				NetService.getInstance().removeHttpConnector(
						_httpConnector.getTarget());
				VersionInfo versionInfo = new VersionInfo();
				versionInfo.parseVersionInfo(buf, true);
				if (listener != null) { // 通知监听器版本信息解析完毕，注：如果版本信息下发有误或解析失败，将视为不更新，
					Message msg = new Message();
					msg.obj = versionInfo;
					listener.onSuccessed(msg);
				}
			}

			public String getParam() {
				if(Community.getInstacne().isFinish()){
				return "cmd=" + (id == 0 ? "lobby" : "singlegame")
						+ "&channelId=" + Community.getInstacne().getChannel()
						+ (id == 0 ? "" : "&gameid=" + id) + "&childChannelId="
						+ Community.getInstacne().getSubChannel();
				}
				return "";
			}

			public String getHttpUrl() {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(HostAddressPool.UPDATE);
				//超爽斗地主添加
				if(Community.PlatID==Community.PLAT_CSDDZ){
					sb.append("/chaoshuang");
				}
				sb.append("/android/androidconfig.php");

				return sb.toString();
			}
		};
		_httpConnector.addEvent(request);
		_httpConnector.connect();
	}

}
