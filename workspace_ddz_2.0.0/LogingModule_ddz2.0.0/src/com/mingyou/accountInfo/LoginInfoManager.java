/**
 * 
 */
package com.mingyou.accountInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mingyou.accountInfoInterface.NativeLoginInfoInterface;
import com.mingyou.community.Community;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.comm.log.MLog;
import com.mykj.comm.util.SecretCode;

/**
 * @author Jason
 * 
 */
public class LoginInfoManager /* implements LoginInfoManagerInterface */{
	private static Context _curContex = null;

	private static final String TAG = "LoginInfoManager";

	// private static final String OLD_LOGIN_INFO_PATH =
	// "ddz_config_1_0_4_single_game.db";

	// /** 共享帐号信息存放路径 **/
	// private static final String LOGIN_INFO_SHARE_PATH =
	// ".mingyouGames/userInfo/config_1_0_0_single_game.db";

	// /** 1.0.0初始帐号信息 **/
	// private static final String LOGIN_INFO_PATH_100 =
	// "config_1_0_0_single_game.db";

	/** 1.0.1 新版本帐号信息，头信息校验 **/
	private static final String LOGIN_INFO_PATH_101 = "config_1_0_1_single_game.db";

	private static final String _lobbyLoginInfo_URI = "content://com.mingyou.gameLobby";

	private static final String MINYOU_DATA = "mingyou_data";

	// private static final String OLD_DATA = "data";

	public static void initManager(Context con, int startType) {
		_curContex = con;
		_startType = startType;
		if (_startType == HALL_TAG) {
			MLog.e("initManager-帐号管理为大厅模式");
		} else {
			MLog.e("initManager-帐号管理为游戏独立模式");
		}
	}

	/** 大厅启用方式的常量 */
	public static final int HALL_TAG = 0;

	/** 游戏启用方式的常量 */
	public static final int GAME_TAG = 1;

	/**
	 * 游戏的启动类型,默认为单款游戏启动类型(此类型将影响帐号数据的存放位置)
	 */
	private static int _startType = GAME_TAG;

	/**
	 * 判断当前启动类型是否是指定类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isStartType(int type) {
		return _startType == type;
	}

	public static int getStartType() {
		return _startType;
	}
	
	public AccountItem getLastLoginAcc() {
		if (m_loginRecords._lastLoginAcc == null || m_loginRecords.accounts == null || m_loginRecords.accounts.isEmpty()) {
			return null;
		}
		return m_loginRecords.accounts.get(m_loginRecords._lastLoginAcc);
	}
	/**
	 * 最近登录的帐号信息，都会被保存在第一位
	 * 
	 * @return
	 */
	public ArrayList<AccountItem> getAccountInfo() {
		if (m_loginRecords == null || m_loginRecords.accounts == null) {
			return null;
		}
		ArrayList<AccountItem> itmes = new ArrayList<AccountItem>();
		Iterator<AccountItem> iterator = m_loginRecords.accounts.values().iterator();
		while (iterator.hasNext()) {
			AccountItem acc = iterator.next();
			if (acc.getType() == AccountItem.ACC_TYPE_CMCC || acc.getType() == AccountItem.ACC_TYPE_TEMP) {
				itmes.add(0, acc);
			} else {
				itmes.add(acc);
			}
		}
		return itmes;
	}


//	public static Context getContext() {
//		return _curContex;
//	}

	// public static void setContext(Context con) {
	// _curContex=con;
	// }

	// 登录记录存储结构
	class LoginRecords implements NativeLoginInfoInterface {
		private boolean isAutoLogin = true; // 记录是否勾选了自动登录

		private HashMap<String, AccountItem> accounts = null/* [4] */; // 第一个用于保存移动/游客账号，其他三个用于保存普通账号

		private String _lastLoginAcc = null; // 最后登录的索引，由于普通账号登录完会进行排序，

		public void clean() {
			if (accounts != null) {
				accounts.clear();
			}
			// _lastLoginAcc=null;
		}

		public LoginRecords() {
			accounts = new HashMap<String, AccountItem>();
		}

		public AccountItem getLastAccountItem() {
			// Log.i(TAG, "last_login_index=" + _lastLoginAcc);
			if (_lastLoginAcc == null) {
				return null;
			}
			return accounts.get(_lastLoginAcc);
		}

		public LoginRecords(TDataInputStream dis) {
			this();
			loadNewInfo(dis);
		}

		/**
		 * @param dis
		 * @param b
		 */
		// public LoginRecords(TDataInputStream dis, boolean b) {
		// loadOldInfo(dis);
		// }

		public void writeToStream(TDataOutputStream dos) {
			TDataOutputStream d = new TDataOutputStream();
			d.writeBoolean(isAutoLogin);
			d.writeUTFByte(_lastLoginAcc);
			d.writeShort(accounts == null ? 0 : accounts.size());
			if (accounts != null) {
				Iterator<AccountItem> iterator = accounts.values().iterator();
				while (iterator.hasNext()) {
					AccountItem acc = iterator.next();
					if (acc != null && acc.isValid()) { // 只写入有效帐号
						acc.writeToStream(d);
					}
				}
			}
			final byte[] array = d.toByteArray();
			dos.writeInt(array.length);
			dos.write(array, 0, array.length);
			try {
				d.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// public void oldwriteToStream(TDataOutputStream dos) {
		// dos.writeBoolean(isAutoLogin);
		// dos.writeBytes(0, 3);
		// AccountItem lastAcc = getLastAccountItem();
		// AccountItem accs[] = new AccountItem[4];
		// int index = -1;
		// int count = 0;// 普通帐号从2开始
		// AccountItem temp = null;
		// if (lastAcc != null) {
		// // accounts.remove(lastAcc.username); //先移除
		// if (lastAcc.getType() == AccountItem.ACC_TYPE_TEMP ||
		// lastAcc.getType() == AccountItem.ACC_TYPE_CMCC) {
		// // 临时帐号或者移动帐号 写入索引0位置
		// index = 0;
		// lastAcc._index = (byte) index;
		// accs[index] = lastAcc;
		// count = 1;
		// } else {
		// // 帐号密码登录 写入索引1号位置
		// temp = getTEMPAcc();
		// if (temp == null) { // 没有游客和移动帐号，帐号将被放在第一位
		// index = 0;
		// lastAcc._index = (byte) index;
		// accs[index] = lastAcc;
		// count = 1;
		// setIsBind(true); // 没有游客帐号也要保证旧版本客户端不会提示修改密码
		// } else {
		// // 有效游客或者移动帐号必须在第一位
		// temp._index = 0;
		// accs[0] = temp;
		// // 非游客和移动帐号
		// index = 1;
		// lastAcc._index = (byte) index;
		// accs[index] = lastAcc;
		// count = 2;
		// }
		// }
		// } else {
		// index = 0;
		// }
		// Iterator<AccountItem> iterator = accounts.values().iterator();
		// while (iterator.hasNext()) {
		// AccountItem acc1 = iterator.next();
		// if (acc1 != null && !acc1.equals(lastAcc) && (temp == null ||
		// !acc1.equals(temp))) { // 处理最后登录的帐号位置
		// if (count < accs.length) {
		// acc1._index = (byte) count;
		// accs[count++] = acc1;
		// }
		// }
		// }
		// // 帐号信息写入
		// for (int i = 0; i < accs.length; i++) {
		// AccountItem tmpAcc = new AccountItem(accs[i]);
		// if (tmpAcc == null || !tmpAcc.isValid()) {
		// writeOldZeroForDataSize(dos, i); // 写入空对象所占字节数
		// } else {
		// if (tmpAcc.getType() == AccountItem.ACC_TYPE_TEMP || tmpAcc.getType()
		// == AccountItem.ACC_TYPE_CMCC) {
		// tmpAcc._password = null;
		// }
		// tmpAcc.oldwriteToStream(dos);
		// }
		// }
		// dos.writeByte(index); // 始终使用第一个帐号登录
		// dos.writeBytes(0, 3);
		// }

		/**
		 * @return
		 */
		// private AccountItem getTEMPAcc() {
		// AccountItem item = accounts.get(AccountItem.ACC_CMCC); // 移动帐号
		// if (item != null) {
		// return item;
		// }
		// item = accounts.get(AccountItem.ACC_TEMP); // 游客帐号
		// if (item != null) {
		// return item;
		// }
		// item = accounts.get(AccountItem.ACC_PHONE); // 手机帐号
		// if (item != null) {
		// return item;
		// }
		// return null;
		// }

		/**
		 * AccountItem对象所占字节数
		 * 
		 * @param dos
		 * @param i
		 */
		// public void writeOldZeroForDataSize(TDataOutputStream dos, int i) {
		// AccountItem accountItem = new AccountItem();
		// accountItem._index = (byte) i;
		// accountItem.oldwriteToStream(dos);
		// // final int size = 4 + 64 + 64 + 256 + 4;
		// // dos.writeBytes(0, size);
		// }

		/**
		 * @param dis
		 */
		// public void loadOldInfo(TDataInputStream dis) {
		// if (accounts == null) {
		// accounts = new HashMap<String, AccountItem>();
		// }
		// isAutoLogin = dis.readBoolean();
		// dis.skip(3);
		// AccountItem acc[] = new AccountItem[4];
		// for (int i = 0; i < acc.length; i++) {
		// acc[i] = new AccountItem(dis, false);
		// }
		// final byte last_index = dis.readByte();
		// // Log.e(TAG, "------------------xxx---last_index=" + last_index);
		// for (int i = 0; i < acc.length; i++) {
		// if (acc[i].isValid()) { // 当前帐号是最后登录帐号
		// if (/* acc[i]._type == ACC_TYPE_CMCC || */(acc[i]._type ==
		// AccountItem.ACC_TYPE_TEMP && AccountItem.ACC_PHONE
		// .equals(acc[i]._username))) { // 移动帐号要覆盖游客帐号//
		// // 游客帐号，并且用户名为手机帐号，将替换原来的游客帐号
		// removeTempAccount(acc[i]._type); // 移除所有类型为游客的帐号(游客帐号+手机帐号)
		// }
		// AccountItem tmpAcc = accounts.put(getAccountKey(acc[i]), acc[i]);
		// if (tmpAcc != null) {
		// acc[i]._isCopyAcc = tmpAcc._isCopyAcc;
		// acc[i]._accUserID = tmpAcc._accUserID; // 保证userID保留
		// }
		// // Log.e(TAG, "---------------------last_index=" +
		// // last_index);
		// // Log.e(TAG, "--------------------- acc[i]._index =" +
		// // acc[i]._index);
		// // Log.e(TAG, "--------------------- _lastLoginAcc =" +
		// // _lastLoginAcc);
		// // Log.e(TAG,
		// // "--------------------- getAccountKey(acc[i]) =" +
		// // getAccountKey(acc[i]));
		// if (last_index == acc[i]._index && (_lastLoginAcc == null ||
		// !_lastLoginAcc.equals(getAccountKey(acc[i])))) { //
		// 无效对象置为空,是否和上次登录帐号一样
		// _lastLoginAcc = getAccountKey(acc[i]);
		// _isHasNewAccInfo = true;
		// }
		// // Log.e(TAG, "acc=" + acc[i]);
		// }
		// }
		// dis.skip(3);
		// }

		/**
		 * @param dis
		 */
		public void loadNewInfo(TDataInputStream dis) {
			final int dlen = dis.readInt();
			// final int mark = dis.getMarkLen();
			// final int pos = dis.markLen(dlen);
			MDataMark mark = dis.markData(dlen);
			isAutoLogin = dis.readBoolean();
			final String last_index = dis.readUTFByte();
			final int len = dis.readShort();
			if (len > 0) {
				if (accounts == null) {
					accounts = new HashMap<String, AccountItem>();
				}
				for (int i = 0; i < len; i++) {
					AccountItem acc = new AccountItem(dis);
					if (acc.isValid() /* && !accIsExist(acc) */) {
						if (/* acc._type == ACC_TYPE_CMCC || */acc._type == AccountItem.ACC_TYPE_TEMP) { // 移动帐号要覆盖游客帐号
							// 游客帐号，并且用户名为手机帐号，将替换原来的游客帐号
							// accounts.remove(ACC_TEMP); // 替换游客帐号
							// accounts.remove(ACC_PHONE); // 替换手机帐号
							removeTempAccount(acc._type); // 移除所有类型为游客的帐号(游客帐号+手机帐号)
						}
						AccountItem tmpAcc = accounts.put(getAccountKey(acc),
								acc);
						if (tmpAcc != null) {
							acc._isCopyAcc = tmpAcc._isCopyAcc;
						}
						if (last_index != null
								&& getAccountKey(acc).equals(last_index)
								&& (_lastLoginAcc == null || !_lastLoginAcc
										.equals(last_index))) { // 最后一次登录帐号
							_lastLoginAcc = last_index;
							_isHasNewAccInfo = true;
						}
					}
				}
			}
			dis.unMark(mark);
			// dis.unMark(dlen, pos);
			// dis.markLen(mark);
		}

		@Override
		public void readFromStream(TDataInputStream dis) {
			loadNewInfo(dis);
		}
	};

	/**
	 * @param accountItem
	 * @return
	 */
	private String getAccountKey(AccountItem accountItem) {
		String str = accountItem._username;
		if (isStartType(GAME_TAG)) { // 游戏启动就加入userID
			str += "-" + accountItem._accUserID;
		}
		return str;
	}

	/** 帐号类型 **/
	// protected byte _accItemType = 0;

	// 存储数据start
	LoginRecords m_loginRecords;

	// 用于保存AT相关的用户名和密码
	// int _curUserID = 0; // 用户唯一标识ID

	// String _username_at; // 48

	// String _password_at;// 48

	// private String username; // 账户名 (手动登录记录) 48 新数据格式不再使用2012.11.21
	//
	// private String password; // 密码 (手动登录记录) 48 新数据格式不再使用2012.11.21

	// private boolean bSavePassword = true; // 是否保存密码
	//
	// private boolean bFastLogon = true; // 是否自动登陆

	// /** 白名单Token串(api_key) */
	// public String whiteNameToken = null;
	// /** 白名单Token串(api_key) */
	// private String _whiteNameToken; // 白名单 48
	//
	// private String m_CMNETToken;// 用于获取白名单的TOKEN 50
	//
	// private String m_RandomNum; // 保存随机数用于获取白名单TOKEN 10

	// /** 白名单用户ID(uid) */
	// public String whiteNameUserID = null;

	// /** 白名单用户ID(uid) */
	// private int _whiteNameUserID; // 保存白名单返回的移动ID
	//
	// private String m_Imsi; // IMSI号 50
	//
	// private static String m_IPList;// IP列表 1024
	//
	// private int m_music; // 背景音乐大小
	//
	// private int m_sound; // 游戏声音大小

	// protected String _curToken; // 保存登陆AT数据 //AT
	// 的最大长度是128，要保证以'/0'结尾。增加两个字节,保存免注册登录 130
	// protected String _thirdAccName = null;

	protected AccountItem _curAccItem = null;

	/** 移动大厅启动时传入的token */
	private String mobileGameHallToken = null;

	public String getMobileGameHallToken() {
		return mobileGameHallToken;
	}

	public void setMobileGameHallToken(String mobileGameHallToken) {
		this.mobileGameHallToken = mobileGameHallToken;
	}

	protected boolean _isCopyAccItem = false;

	private boolean _isBind = false; // AT是否绑定成功，默认为false

	/**
	 * 当前游客帐号是否绑定(是否修改过密码)
	 * 
	 * @return
	 */
	public boolean isBind() {
		return _isBind;
	}

	public void setIsBind(boolean bool) {
		_isBind = bool;
	}

	// private int m_userId;

	// 存储数据end

	private static LoginInfoManager _instacne = null;

	// 用于操作登录数据，所有登录数据在此类操作
	public static LoginInfoManager getInstance() {
		if (_instacne == null) {
			_instacne = new LoginInfoManager();
		}
		return _instacne;
	}

	/**
	 * 判断指定帐号是否已存在内存中
	 * 
	 * @return
	 */
	// public boolean accIsExist(final AccountItem item) {
	// if (item._username == null || m_loginRecords == null
	// || m_loginRecords.accounts == null
	// || m_loginRecords.accounts.isEmpty()) {
	// return false;
	// }
	// return m_loginRecords.accounts.containsKey(item._username);
	// }

	public String getToken() {
		if(_curAccItem==null){
			return null;
		}
		return _curAccItem._token;
	}

	public byte getLoginType() {
		if(_curAccItem==null){
			return AccountItem.ACC_TYPE_NULL;
		}
		return _curAccItem._type;
	}

	public AccountItem getCurAccountItem() {
		if(_curAccItem==null){
			return null;
		}
		return _curAccItem;
	}

	/**
	 * 设置当前登录的帐号类型
	 * 
	 * @param type
	 */
	public void setLoginType(byte type) {
		if(_curAccItem==null){
			return;
		}
		_curAccItem._type = type;
	}

	public void setCurAccountItem(AccountItem item) {
		if (item == null) {
			MLog.e("setCurAccountItem-accountItem is null");
			return;
		}
		_curAccItem = item;
		// LoginInfoManager.getInstance()._username_at = item._username;
		// LoginInfoManager.getInstance()._password_at = item._password;
		// LoginInfoManager.getInstance()._curToken = item._token;
		// LoginInfoManager.getInstance()._accItemType = item._type;
		// LoginInfoManager.getInstance()._curUserID = item._accUserID;
	}

	private LoginInfoManager() {
		_curAccItem = new AccountItem();
		m_loginRecords = new LoginRecords();
		// bSavePassword = false;
		// bFastLogon = false;
		_isBind = false;
		// _whiteNameUserID = 0;
		// m_music = 1;
		// m_sound = 1;
		// m_userId = 0;
	}

	/**
	 * 最近登录的帐号信息，都会被保存在第一位
	 * 
	 * @return
	 */
	// public ArrayList<AccountItem> getAccountInfo() {
	// if (m_loginRecords == null || m_loginRecords.accounts == null) {
	// return null;
	// }
	// ArrayList<AccountItem> itmes = new ArrayList<AccountItem>();
	// Iterator<AccountItem> iterator = m_loginRecords.accounts.values()
	// .iterator();
	// while (iterator.hasNext()) {
	// AccountItem acc = iterator.next();
	// if (/* acc.getType() == AccountItem.ACC_TYPE_CMCC || */acc
	// .getType() == AccountItem.ACC_TYPE_TEMP) {
	// itmes.add(0, acc);
	// } else {
	// itmes.add(acc);
	// }
	// }
	// return itmes;
	// }

	/**
	 * 从内存和本地文件删除当前已登录或正在使用的帐号
	 */
	public void deleteCurAccountInfo() {
		if (m_loginRecords == null || m_loginRecords.accounts == null) {
			return;
		}
		m_loginRecords.accounts.remove(m_loginRecords._lastLoginAcc);
		cleanCurAccountInfo();
		saveNativeLoginInfo();
		return;
	}

	/**
	 * 删除指定帐号信息,true：删除帐号为当前登录帐号，false：非当前登录帐号
	 * 
	 * @param acc
	 */
	public boolean deleteAccountInfo(AccountItem acc) {
		if (m_loginRecords == null || m_loginRecords.accounts == null
				|| acc == null) {
			return false;
		}
		AccountItem lastAcc = m_loginRecords.accounts
				.get(m_loginRecords._lastLoginAcc);
		boolean bool = false;
		if (acc.equals(lastAcc)) { // 如果删除的帐号为当前登录帐号
			cleanCurAccountInfo();
			bool = true;
			_isHasNativeLoginInfo = false; // 删除当前帐号需要重新加载帐号信息
		}
		m_loginRecords.accounts.remove(getAccountKey(acc));
		saveNativeLoginInfo();
		return bool;
	}

	/**
	 * 更新当前账号
	 * 
	 * @param accountItem
	 */
	public void updateAccInfo(AccountItem accountItem) {
		// 修改密码默认不保存密码，需要完成登录操作才能保存
		deleteCurAccountInfo();
		setIsBind(true);
//		if (accountItem != null) {
//			if (accountItem.getType() == AccountItem.ACC_TYPE_TEMP) { // 如果是游客帐号强制修改为普通帐号
//				accountItem._type = AccountItem.ACC_TYPE_COMM;
//			}
//		}
		updateAccountInfo(accountItem);
		saveNativeLoginInfo();
	}

	/**
	 * 此方法提供仅个人中心修改密码专用
	 * 
	 * @param accountItem
	 */
	public void updateAccPassInfo(AccountItem accountItem) {
		// 修改密码默认不保存密码，需要完成登录操作才能保存
		deleteCurAccountInfo();
		setIsBind(true);
		if (accountItem.getType() == AccountItem.ACC_TYPE_TEMP) { // 如果是游客帐号强制修改为普通帐号
			accountItem._type = AccountItem.ACC_TYPE_COMM;
		}
		updateAccountInfo(accountItem);
		saveNativeLoginInfo();
	}

	/**
	 * 更新当前帐号信息到本地
	 */
	public void updateAccountInfo() {
		// AccountItem accItem = new AccountItem();
		// accItem._username = _username_at;
		// accItem._password = _password_at;
		// accItem._type = _accItemType;
		// accItem._token = _curToken;
		// accItem._accUserID = _curUserID;
		updateAccountInfo(_curAccItem);
		saveNativeLoginInfo();
	}

	/**
	 * 更新当前帐号信息,最近登录的帐号信息，都会被保存在第一位
	 */
	protected void updateAccountInfo(AccountItem accItem) {
		if (m_loginRecords == null) {
			m_loginRecords = new LoginRecords();
		}
		if (m_loginRecords.accounts == null) {
			m_loginRecords.accounts = new HashMap<String, AccountItem>();
		}
		if (/* accItem.getType() == ACC_TYPE_CMCC || */accItem.getType() == AccountItem.ACC_TYPE_TEMP) { // 移动帐号
			// 替换游客帐号
			// 替换手机帐号
			removeTempAccount(accItem.getType()); // 移除所有类型为游客的帐号(游客帐号+手机帐号)
		}
		Iterator<AccountItem> iterator = m_loginRecords.accounts.values()
				.iterator();
		while (iterator.hasNext()) {
			AccountItem acc = iterator.next();
			if (accItem.equals(acc)) { // 如果是相同帐号必须要替换
				iterator.remove();
			}
		}
		m_loginRecords._lastLoginAcc = getAccountKey(accItem);
		m_loginRecords.accounts.put(m_loginRecords._lastLoginAcc, accItem);
		Log.v(TAG, "m_loginRecords.last_login_index="
				+ m_loginRecords._lastLoginAcc);
	}

	/**
	 * 提供给android斗地主精简版更新大厅登录记录(精简版专用)
	 */
	public void updateAccountInfo(String name, String pass, String token,
			byte type, int userID) {
		AccountItem accItem = new AccountItem(name, pass, token, type, userID);
		updateAccountInfo(accItem);
	}

	/**
	 * 
	 */
	private void removeTempAccount(final int type) {
		Iterator<AccountItem> iterator = m_loginRecords.accounts.values()
				.iterator();
		while (iterator.hasNext()) {
			AccountItem acc = iterator.next();
			if (acc != null && acc.getType() == AccountItem.ACC_TYPE_TEMP) { // 零时帐号
				iterator.remove(); // 移除当前key+value
			}
		}
	}

	// db交互
	public static final Uri dbUri = Uri.parse(_lobbyLoginInfo_URI);

	void saveNativeLoginInfo() {
		if (isStartType(HALL_TAG)) { // 名游大厅和名游新大厅启动模式
			saveLobbyLoginInfo();
		} else if (isStartType(GAME_TAG)) { // 游戏独立启动
			saveGameLoginInfo();
		}
	}

	/**
	 * 
	 */
	private void saveGameLoginInfo() {
		FileOutputStream fos = null;
		try {
			// if (IP_CONFIG_FILE.IsShareAccount()) {
			// final File filePath = new File(AndrUtil.getSDCardDir() +
			// LOGIN_INFO_SHARE_PATH);
			// // 检查父路径是否存在
			// final File checkPath = new File(filePath.getParent());
			// if (!checkPath.exists()) {
			// checkPath.mkdirs();
			// }
			// fos = new FileOutputStream(filePath);
			// } else {
			fos = Community.getContext().openFileOutput(LOGIN_INFO_PATH_101, 0);
			// }
			String checkString = getIMEI();
			checkString = SecretCode.getMD5(checkString);
			// 写入校验串
			final byte[] checkbuf = checkString.getBytes("utf-8");
			fos.write(checkbuf.length);
			fos.write(checkbuf);
			// 写入帐号信息
			final String result = getNewNaviteInfo();
			final byte buf[] = TDataOutputStream.utf8toBytes(result);
			fos.write(buf);
		} catch (Exception e) {
			Log.e(TAG, "_新数据写入异常_" + e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			TelephonyManager telephonyManager = (TelephonyManager) Community.getContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			return _IMEI = telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// /** 手机的IMSI信息 **/
	// private String _IMSI = null;
	//
	// /**
	// * 获得手机IMSI信息
	// *
	// * @return IMSI信息
	// */
	// public String getIMSI() {
	// if (_IMSI != null) {
	// return _IMSI;
	// }
	// try {
	// TelephonyManager telephonyManager = (TelephonyManager)
	// getContext().getSystemService(Context.TELEPHONY_SERVICE);
	// return _IMSI = telephonyManager.getSubscriberId();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	/**
	 * 
	 */
	private void saveLobbyLoginInfo() {
		try {
			// Log.i("====", "saveUserData: " + userData);
			ContentResolver resolver = Community.getContext().getContentResolver();
			ContentValues cv = new ContentValues();
			Cursor cr = resolver.query(dbUri, null, null, null, null);
			// cv.put(OLD_DATA, getOldNaviteInfo());
			final int mindex = cr.getColumnIndex(MINYOU_DATA);// 要检查是否存在新数据
			if (mindex != -1) {
				cv.put(MINYOU_DATA, getNewNaviteInfo());
			}
			resolver.update(dbUri, cv, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 2012.11.21新数据格式存储
	 * 
	 * @return
	 * @throws IOException
	 */
	private String getNewNaviteInfo() throws IOException {
		// Log.v(TAG, "getNewNaviteInfo=" + _curToken);
		TDataOutputStream dos = new TDataOutputStream();
		m_loginRecords.writeToStream(dos);
		final byte[] array = dos.toByteArray();
		dos.close();
		return SecretCode.base64encode(array);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	// private String getOldNaviteInfo() throws IOException {
	// TDataOutputStream dos = new TDataOutputStream(false);
	// // 存储数据start
	// m_loginRecords.oldwriteToStream(dos);
	// checkTempAcc();
	// // 用于保存AT相关的用户名和密码1392
	// dos.writeUTF(_username_at, 48);// 48
	// dos.writeUTF(_password_at, 48);// 48
	// dos.writeUTF(username, 48);// 账户名 (手动登录记录) 48
	// dos.writeUTF(password, 48);// 密码 (手动登录记录) 48
	// dos.writeBoolean(true);// 是否保存密码
	// dos.writeBoolean(true);// 是否自动登陆
	// dos.writeUTF(_whiteNameToken, 48);// 白名单 48
	// dos.writeUTF(m_CMNETToken, 50);// 用于获取白名单的TOKEN 50
	// dos.writeUTF(m_RandomNum, 10);// 保存随机数用于获取白名单TOKEN 10
	// dos.writeInt(_whiteNameUserID);// 保存白名单返回的移动ID
	// dos.writeBytes(0, 2); // 字节对齐2
	// dos.writeUTF(m_Imsi, 50);// IMSI号 50
	// dos.writeUTF(m_IPList, 1024);// IP列表 1024
	// dos.writeInt(m_music);// 背景音乐大小
	// dos.writeInt(m_sound);// 游戏声音大小
	// dos.writeBytes(0, 2); // 字节对齐2
	// dos.writeUTF(_curToken, 130);// 保存登陆AT数据 //AT
	// // 的最大长度是128，要保证以'/0'结尾。增加两个字节,保存免注册登录 130
	// dos.writeBoolean(_isBind);
	// dos.writeInt(m_userId);
	// final byte[] array = dos.toByteArray();
	// dos.close();
	// return SecretCode.base64encode(array);
	// }

	/**
	 * 
	 */
	// private void checkTempAcc() {
	// // 没有游客帐号
	// if (m_loginRecords.accounts.isEmpty()) {
	// Log.e(TAG, "帐号信息被清空，重置绑定状态");
	// setIsBind(false); // 没有帐号，重置绑定状态
	// }
	// }

	private boolean _isHasNativeLoginInfo = false;

	public boolean isHasNativeLoginInfo() {
		if (!_isHasNativeLoginInfo) {
			loadNativeLoginInfo();
		}
		return _isHasNativeLoginInfo;
	}

	/**
	 * 强制重新加载本地帐号信息
	 */
	protected void reLoadNativeLoginInfo() {
		_isHasNativeLoginInfo = false;
		loadNativeLoginInfo();
	}

	private boolean _isHasNewAccInfo = false;

	/**
	 * 判断是否有新的最后登录帐号增加
	 * 
	 * @return
	 */
	public boolean isHasNewAccInfo() {
		_isHasNewAccInfo = false;
		reLoadNativeLoginInfo();
		return _isHasNewAccInfo;
	}

	/**
	 * 重新加载本地帐号信息，确认是否有本地帐号信息
	 * 
	 * @return
	 */
	public boolean reIsHasAccountInfo() {
		_isHasNativeLoginInfo = false;
		return isHasNativeLoginInfo();
	}

	public void loadNativeLoginInfo() {
		if (_isHasNativeLoginInfo) {
			Log.e(TAG, "-----------------_isHasNativeLoginInfo is true");
			return;
		}
		// 如果要强制读取本地信息，必须先清除内存数据
		if (m_loginRecords != null) {
			m_loginRecords.clean();
		}
		if (isStartType(HALL_TAG)) { // 名游大厅和名游新大厅启动模式
			Log.e(TAG, "-----------------HALL_TAG");
			loadLobbyLoginInfo();
		} else if (isStartType(GAME_TAG)) { // 游戏独立启动
			Log.e(TAG, "-----------------GAME_TAG");
			loadGameLoginInfo();
		}
	}

	/** 获得包名，由分区实现 */
	public static String getPackageName(Context context) {
		String path = null;
		try {
			path = context.getPackageName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (path == null) {
			path = "com.MyGame.Midlet";
		}
		return path;
	}

	/**
	 * 
	 */
	private void loadGameLoginInfo() {
		FileInputStream fis = null;
		// String packName = null;
		try {
			// try {
			// // 老数据读取
			// packName = getPackageName(getContext());
			// fis = new FileInputStream("/data/data/" + packName + "/" +
			// OLD_LOGIN_INFO_PATH);
			// byte[] array = new byte[3104];
			// fis.read(array);
			// parseOldLoginInfo(array);
			// // 数据读取成功后删除原文件
			// fis.close();
			// File file = new File("/data/data/" + packName + "/" +
			// OLD_LOGIN_INFO_PATH);
			// file.delete();
			// } catch (Exception e) {
			// cleanCurAccountInfo(); // 如果读取老文件异常，清除当前读取的帐号信息
			// Log.i(TAG, "老数据读取异常=" + e);
			// // e.printStackTrace();
			// } finally {
			// //
			// try {
			// if (fis != null) {
			// fis.close();
			// fis = null;
			// }
			// } catch (Exception e2) {
			// Log.e(TAG, OLD_LOGIN_INFO_PATH + "_删除旧数据异常+" + e2);
			// }
			// }
			// // 1.3.0版本帐号信息读取，覆盖安装
			// try {
			// fis = getContext().openFileInput(LOGIN_INFO_PATH_100);
			// byte[] array = new byte[fis.available()];
			// fis.read(array);
			// final String result = TDataInputStream.getUTF8String(array);
			// parseNewLoginInfo(result);
			// fis.close();
			// getContext().deleteFile(LOGIN_INFO_PATH_100);
			// // final File file = new File("/data/data/" + packName +
			// // "/file/" + LOGIN_INFO_PATH_100);
			// // file.delete();
			// } catch (Exception e) {
			// Log.i(TAG, "1.3.0数据读取异常=" + e);
			// // e.printStackTrace();
			// } finally {
			// try {
			// if (fis != null) {
			// fis.close();
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// 新帐号信息保存在SD卡目录
			try {
				// if (IP_CONFIG_FILE.IsShareAccount()) {// 共享帐号目录
				// final File filePath = new File(AndrUtil.getSDCardDir() +
				// LOGIN_INFO_SHARE_PATH);
				// fis = new FileInputStream(filePath);
				// } else { // 游戏沙盒
				fis = Community.getContext().openFileInput(LOGIN_INFO_PATH_101);
				// }
				// 读取校验信息
				final byte len = (byte) fis.read();
				byte[] checkBuf = new byte[len];
				fis.read(checkBuf);
				final String checkStr = new String(checkBuf, "utf-8");
				final String fStr = SecretCode.getMD5(getIMEI());
				if (checkStr.compareTo(fStr) != 0) { // 校验为本机绑定帐号信息
					throw new Exception("非本机绑定帐号信息，不继续读取");
				}
				// 读取帐号信息
				byte[] array = new byte[fis.available()];
				fis.read(array);
				final String result = TDataInputStream.getUTF8String(array);
				parseNewLoginInfo(result);
			} catch (Exception e) {
				Log.i(TAG, "新数据读取异常=" + e);
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Log.v(TAG, "loadGameLoginInfo.last_login_index="
					+ m_loginRecords._lastLoginAcc);
			AccountItem acc = m_loginRecords.getLastAccountItem();
			if (acc != null) {
				_curAccItem = acc;
				// _username_at = acc._username;
				// _password_at = acc._password;
				// _curToken = acc._token;
				// _accItemType = acc._type;
				// _isCopyAccItem = acc._isCopyAcc;
				// _curUserID = acc._accUserID;
			} else {
				cleanCurAccountInfo();
			}
			_isHasNativeLoginInfo = (!m_loginRecords.accounts.isEmpty());
			// 新旧数据可能不一致，所以立刻进行一次本地化操作
			saveNativeLoginInfo();
		}
	}

	/**
	 * 大厅登录数据读取
	 */
	private void loadLobbyLoginInfo() {
		// 加载本地登录数据
		String result = null;
		try {
			ContentResolver resolver = Community.getContext().getContentResolver();
			Cursor cr = resolver.query(dbUri, null, null, null, null);
			try {
				final int mindex = cr.getColumnIndex(MINYOU_DATA);// 查找并尝试使用新数据
				if (mindex != -1) {
					// 新版本数据格式
					cr.moveToFirst();
					result = cr.getString(mindex);
					parseNewLoginInfo(result);
					// return;
				}
			} catch (Exception e) { // 新数据读取异常将继续读取老数据
				e.printStackTrace();
			}
			// try {
			// final int oindex = cr.getColumnIndex(OLD_DATA);// 查找并尝试使用新数据
			// if (oindex != -1) {
			// // 新版本数据格式
			// cr.moveToFirst();
			// result = cr.getString(oindex);
			// parseOldLoginInfo(result);
			// // return;
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Log.v(TAG, "loadNativeLoginInfo.last_login_index="
					+ m_loginRecords._lastLoginAcc);
			AccountItem acc = m_loginRecords.getLastAccountItem();
			if (acc != null) {
				_curAccItem = acc;
				// _username_at = acc._username;
				// _password_at = acc._password;
				// _curToken = acc._token;
				// _accItemType = acc._type;
				// _isCopyAccItem = acc._isCopyAcc;
			} else {
				cleanCurAccountInfo();
			}
			_isHasNativeLoginInfo = (!m_loginRecords.accounts.isEmpty());
			// 新旧数据可能不一致，所以立刻进行一次本地化操作
			saveNativeLoginInfo();
		}
	}

	public void cleanCurPassWord() {
		// _password_at = null;
		_curAccItem._password = null;
	}

	/**
	 * 
	 */
	public void cleanCurAccountInfo() {
		m_loginRecords._lastLoginAcc = null;
		_curAccItem = null;
		// _username_at = null;
		// _password_at = null;
		// _curToken = null;
		// _accItemType = 0;
		// _isCopyAccItem = false;
	}

	/**
	 * 2012.11.21,新开发版本将使用新数据格式
	 * 
	 * @param result
	 * @throws Exception
	 */
	private void parseNewLoginInfo(String result) throws Exception {
		if (result == null || result.length() == 0) {
			throw new Exception("parseNewLoginInfo result is null");
		}
		final byte[] array = SecretCode.base64decode(result);
		TDataInputStream dis = new TDataInputStream(array);
		if (m_loginRecords == null) {
			m_loginRecords = new LoginRecords(dis);
		} else {
			m_loginRecords.loadNewInfo(dis);
		}
		dis.close();
	}

	// private void parseOldLoginInfo(final byte[] buf) throws Exception {
	// if (buf == null || buf.length == 0) {
	// throw new Exception("parseOldLoginInfo result is null");
	// }
	// TDataInputStream dis = new TDataInputStream(buf, false);
	// if (m_loginRecords == null) {
	// m_loginRecords = new LoginRecords(dis, false);
	// } else {
	// m_loginRecords.loadOldInfo(dis);
	// }
	// // 用于保存AT相关的用户名和密码,后续数据不再使用，2012.11.27
	// _username_at = dis.readUTF(48);
	// _password_at = dis.readUTF(48);
	// username = dis.readUTF(48);
	// password = dis.readUTF(48);
	// /* bSavePassword = */dis.readBoolean();
	// /* bFastLogon = */dis.readBoolean();
	// _whiteNameToken = dis.readUTF(48);
	// m_CMNETToken = dis.readUTF(50);
	// m_RandomNum = dis.readUTF(10);
	// _whiteNameUserID = dis.readInt();
	// dis.skip(2);
	// m_Imsi = dis.readUTF(50);
	// m_IPList = dis.readUTF(1024); // 保存ip列表
	// m_music = dis.readInt();
	// m_sound = dis.readInt();
	// dis.skip(2);
	// _curToken = dis.readUTF(130);
	// _isBind = dis.readBoolean();
	// m_userId = dis.readInt();
	// dis.close();
	// }

	/**
	 * 有字符串base64解码
	 * 
	 * @param result
	 */
	// private void parseOldLoginInfo(String result) throws Exception {
	// if (result == null || result.length() == 0) {
	// throw new Exception("parseOldLoginInfo result is null");
	// }
	// final byte[] array = SecretCode.base64decode(result);
	// parseOldLoginInfo(array);
	// }

//	public AccountItem getLastLoginAcc() {
//		if (m_loginRecords._lastLoginAcc == null
//				|| m_loginRecords.accounts == null
//				|| m_loginRecords.accounts.isEmpty()) {
//			return null;
//		}
//		return m_loginRecords.accounts.get(m_loginRecords._lastLoginAcc);
//	}

	public String getUserName() {
		if(_curAccItem==null){
			return null;
		}
		return _curAccItem._username;
	}

	//
	public String getUserPassWord() {
		if(_curAccItem==null){
			return null;
		}
		return _curAccItem._password;
	}

	public String getCurThirdAccName() {
		if(_curAccItem==null){
			return null;
		}
		return _curAccItem._thirdAccName;
	}
	
	public String getCurThirdAccUID() {
		if(_curAccItem==null){
			return null;
		}
		return _curAccItem._thirdAccUID;
	}

	/**
	 * 判断是否有效帐号密码
	 * 
	 * @return
	 */
	public boolean isVaildAccAndPass() {
		if(_curAccItem==null){
			return false;
		}
		return _curAccItem.isValidAccAndPass();
		// if (_accItemType == AccountItem.ACC_TYPE_TEMP /*
		// * || _accItemType ==
		// * AccountItem.ACC_TYPE_CMCC
		// */) {
		// return false;
		// }
		// return _username_at != null && _username_at.length() != 0 /*
		// * &&
		// * !_username_at
		// * .equals(
		// * AccountItem
		// * .ACC_CMCC)
		// */
		// && _password_at != null && _password_at.length() != 0
		// && !_password_at.equals(AccountItem.NO_PASS);
	}

	/**
	 * 判断是否有效token
	 * 
	 * @return
	 */
	public boolean isVaildToken() {
		if(_curAccItem==null){
			return false;
		}
		return _curAccItem.isValidToken();
		// _curToken != null && _curToken.length() != 0;
	}

	// /**
	// * 后台白名单获取，专用保存方法
	// *
	// * @param wToken
	// */
	// public void updateWhiteName(final String wToken) {
	// if (wToken == null || wToken.length() == 0) {
	// MLog.v(TAG, "updateWhiteName 写入失败，token is null");
	// return;
	// }
	// Log.e(TAG, "写入白名单");
	// loadNativeLoginInfo(); // 先加载到内存保证数据不重复
	// final String lastAcc = m_loginRecords._lastLoginAcc;
	// AccountItem accountItem = new AccountItem();
	// // accountItem._username = AccountItem.ACC_CMCC;
	// // accountItem._type = AccountItem.ACC_TYPE_CMCC;
	// accountItem._token = wToken;
	// updateAccountInfo(accountItem);
	// // _username_at = ACC_CMCC;
	// // _password_at = null;
	// // _curToken = wToken;
	// // _loginType = ACC_TYPE_CMCC;
	// // updateAccountInfo();
	// if (lastAcc != null) { // 白名单帐号不能自动设置为最后一次登录帐号
	// m_loginRecords._lastLoginAcc = lastAcc;
	// }
	// setIsBind(true);
	// saveNativeLoginInfo();
	// _isHasNativeLoginInfo = false; // 后台保存白名单可能没有正确释放此单利
	// }

	/**
	 * 
	 */
	public void cleanLastAccountInfo() {
		m_loginRecords._lastLoginAcc = null;
	}

	/**
	 * 检查是否存在移动帐号
	 * 
	 * @return
	 */
	// public boolean isHasAccontCMCC() {
	// if (!isHasNativeLoginInfo()) {
	// return false;
	// }
	// Iterator<AccountItem> iterator =
	// m_loginRecords.accounts.values().iterator();
	// while (iterator.hasNext()) {
	// AccountItem item = iterator.next();
	// if (item._type == AccountItem.ACC_TYPE_CMCC) { // 如果有移动帐号
	// return true;
	// }
	// }
	// return false;
	// }

	public AccountItem getLastAccountItem() {
		// Log.i(TAG, "last_login_index=" + _lastLoginAcc);
		if (m_loginRecords == null || m_loginRecords._lastLoginAcc == null) {
			loadNativeLoginInfo();
//			return null;
		}
		return m_loginRecords.accounts.get(m_loginRecords._lastLoginAcc);
	}


}
