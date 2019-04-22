package com.login.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.community.Community;
import com.mingyou.login.LoginHttp;
import com.mingyou.login.LoginHttp.HttpEvent;
import com.mingyou.login.LoginHttp.HttpReceiveEventCallBack;
import com.mingyou.login.LoginSocket;
import com.mingyou.login.SocketLoginListener;

public class AccountManager {
	/** 标识http登录的常量 */
	public static final int HTTP_LOGIN_TAG = 0;
	/** 标识TCP登录的常量 */
	public static final int SOCKET_LOGIN_TAG = 1;

	/**
	 * 用于标识以什么方式进行登录的，
	 * 0 Http
	 * 1 Socket
	 */
	private int mLoginTag = -1;


	private static AccountManager instance;


	private AccountManager(){

	}



	public static AccountManager getInstance(){
		if(instance==null){
			instance=new AccountManager();
		}
		return instance;
	}
	/**
	 * 初始化初始数据 并加载相关回调接口
	 * 
	 * @param activity
	 *            调用接口所在的activity
	 * @param loginTag
	 *            使用的登录方式，0为Http登录方式 1为Socket登录方式
	 * @param channel
	 *            渠道号
	 * @param subChannel
	 *            子渠道号
	 * @param lauchTag
	 *            启用方式 0为大厅启用，1为游戏启用
	 */
	public void initialize(Context context,int paltId, int loginTag, int lauchTag, final int gameid,
	 String channel, String subChannel, String version,int vercode) {
		//mContext = context;
		mLoginTag = loginTag;
		Community.getInstacne().initCommunity(context,paltId, gameid, channel,
		 subChannel, lauchTag, version,vercode);
	}




	/**
	 * 是否允许快速登录
	 * 
	 * @return
	 */
	public boolean enableQuickEntrance() {
		boolean isNativeLoginInfo = LoginInfoManager.getInstance().isHasNativeLoginInfo();
		AccountItem accout=LoginInfoManager.getInstance().getLastLoginAcc();  //最后一次登录账号
		if (isNativeLoginInfo && accout == null) { // 没有最后一次登录信息，并且有帐号信息
			return false;
		}
		return true;
	}

	/**
	 * 快速登录
	 * @param callBack
	 */
	public void quickEntrance(final LoginViewCallBack callBack) {
		if (mLoginTag == HTTP_LOGIN_TAG) {
			LoginHttp.autoHttpLogin(parseHttpLoginReturn(callBack));
		} else if (mLoginTag == SOCKET_LOGIN_TAG) {
			SocketLoginListener slListener = new SocketLoginListener() {

				@Override
				public void onSuccessed(Message arg0) {
					if (callBack != null) {
						callBack.loginsuccessed(arg0);
					}
				}

				@Override
				public void onFiled(Message arg0, int param) {
					if (callBack != null) {
						callBack.loginFailed(arg0);
					}
				}
			};
			LoginSocket.getInstance().autoLogin(slListener);
		}
	}




	/**
	 * 账号密码登录
	 */
	public boolean accountLogin(final Context context,
			final String account,
			final String password,
			final LoginViewCallBack callBack){
		boolean res=true;
		final ArrayList<AccountItem> accountPsw = LoginInfoManager.getInstance().getAccountInfo();
		AccountItem accoutSaved=null;
		
		if(isEmptyStr(account)){
			Toast.makeText(context, "请输入账号", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		for (AccountItem item : accountPsw) {
			if ( account.equals(item.getUsername())){
				byte accType=item.getType();
				switch(accType){
				case AccountItem.ACC_TYPE_NULL:
					break;
				case AccountItem.ACC_TYPE_TEMP:
					if(!isEmptyStr(password)){
						Toast.makeText(context, "游客账号无需密码", Toast.LENGTH_SHORT).show();
						res=false;
					}else{
						accoutSaved=item;
					}
					break;
				case AccountItem.ACC_TYPE_CMCC:
					if(!isEmptyStr(password)){
						Toast.makeText(context, "移动账号无需密码", Toast.LENGTH_SHORT).show();
						res=false;
					}else{
						accoutSaved=item;
					}
					break;
				case AccountItem.ACC_TYPE_COMM:
				case AccountItem.ACC_TYPE_THIRD:
					if(isEmptyStr(password)){
						Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
						res=false;
					}
					break;
				default:
					break;
				}
				break;
			}

		}

		if(accoutSaved==null){
			if(isEmptyStr(password)){
				Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
				res=false;
			}
		}
		if (mLoginTag == HTTP_LOGIN_TAG && res) {
			if(accoutSaved!=null){
				LoginHttp.reqHttpLogin(accoutSaved, parseHttpLoginReturn(callBack));
			}else{
				LoginHttp.reqHttpLogin(account, password, parseHttpLoginReturn(callBack));
			}

		} else if (mLoginTag == SOCKET_LOGIN_TAG && res) {
			if(accoutSaved!=null){
				LoginSocket.getInstance().accountLogin(accoutSaved, parseSocketLoginReturn(callBack));
			}else{
				LoginSocket.getInstance().accountLogin(account, password, parseSocketLoginReturn(callBack));
			}
		}
		
		return res;
	}

	/**
	 * 第三方登录接口
	 * 
	 * @param context
	 * @param callBack登录的回调事件
	 * @param isAccountJudge是否需要判断账号信息
	 *            ,比如大厅刚第一次进入需要判断账号信息
	 */
	public void thirdQuickEntrance(final LoginViewCallBack callBack, String thirdToken) {
		SocketLoginListener slListener = new SocketLoginListener() {
			@Override
			public void onSuccessed(Message arg0) {
				if (callBack != null) {
					callBack.loginsuccessed(arg0);
				}
			}

			@Override
			public void onFiled(Message arg0, int param) {
				if (callBack != null) {
					callBack.loginFailed(arg0);
				}
			}
		};

		LoginSocket.getInstance().autoThirdPartyLogin(slListener, thirdToken,"","");

	}

	// http登录解析监听
	private HttpReceiveEventCallBack parseHttpLoginReturn(final LoginViewCallBack callBack) {
		HttpReceiveEventCallBack lis = new HttpReceiveEventCallBack() {
			public void onReceive(final HttpEvent event) {
				Message msg = new Message();
				msg.obj = event.getMessage();
				if (event.getResult() == HttpEvent.LOGIN_SUCCESSED) {
					if (callBack != null) {
						callBack.loginsuccessed(msg);
					}
				} else {
					if (callBack != null) {
						callBack.loginFailed(msg);
					}
				}

			}
		};
		return lis;
	}


	/**
	 * socket登录解析监听
	 * @param callBack
	 * @return
	 */
	private SocketLoginListener parseSocketLoginReturn(final LoginViewCallBack callBack) {
		SocketLoginListener sll = new SocketLoginListener() {

			@Override
			public void onFiled(Message arg0, int param) {

				if (callBack != null) {
					callBack.loginFailed(arg0);
				}

			}

			@Override
			public void onSuccessed(Message arg0) {
				if (callBack != null) {
					callBack.loginsuccessed(arg0);
				}
			}
		};
		return sll;
	}



	/**
	 * 是否添加了新账号
	 * 
	 * @return
	 */
	public static boolean isAddNewAccount() {
		return LoginInfoManager.getInstance().isHasNewAccInfo();
	}
	
	
	/**
	 * 重新加载本地帐号信息，确认是否存在本地帐号信息
	 * 
	 * @return
	 */
	public static boolean reIsHasAccountInfo() {
		return LoginInfoManager.getInstance().reIsHasAccountInfo();
	}

	/**
	 * 判断字符串是否为空 true is null
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmptyStr(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
}
