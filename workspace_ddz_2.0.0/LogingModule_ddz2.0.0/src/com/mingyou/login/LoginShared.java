/**
 * 
 */
package com.mingyou.login;

import com.mingyou.login.struc.VersionInfo;

/**
 * 基本不再使用
 * @deprecated
 * @author Jason
 * 
 */
public interface LoginShared {
	/** 正在登录（3）：发送登陆请求 **/
	byte LLOGIN_STATUS_REQ_LOGIN = 1;

	/** 正在登录（4）：穿透网关 **/
	byte LLOGIN_STATUS_SERVER_ACK = 2;

	/** 正在建立连接（5）：打开socket **/
	byte LLOGIN_STATUS_OPEN_SOCKET_OK = 3;

	/** 正在建立连接（7）：获取配置文件 **/
	byte LLOGIN_STATUS_REQ_REGIO = 4;

	/**
	 * 正在登录中
	 * 
	 * @param type
	 *            登陆中类型
	 */
	void onLogingAction(final byte type);

	/**
	 * 登录成功
	 */
	void onLoginSuccessed(final byte type);

	/**
	 * 登录失败
	 * 
	 * @param type
	 */
	void onLoginFiled(final String msg);

	/**
	 * 版本信息返回
	 * 
	 * @param versionInfo
	 */
	void onLoginVersionInfoParsed(VersionInfo versionInfo);

	/**
	 * 网络超时
	 */
	void onNetClosed(final String msg);

	/**
	 * 注册成功
	 * 
	 * @param status
	 *            注册返回结果
	 * @param statusnote
	 *            注册返回信息
	 */
	void onRegistrated(final byte status, final String statusnote);

	/**
	 * 修改密码-返回结果
	 * 
	 * @param text
	 */
	void onSetPassWord(final String text);

	/**
	 * 异地登录事件
	 * 
	 * @param text
	 */
	void onMultiLogin(final String text);
}
