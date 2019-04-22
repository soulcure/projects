/**
 * @Title: HCMD_LOGIN_V2_ACCOUNTS.java
 * @Package com.myou.mjava.hall.socket.struc
 * @Description: TODO(用一句话描述该文件做什么)
 * @author fwq
 * @date 2011-5-16 上午11:05:05
 * @version V1.0
 * @sine Copyright 名游网络手机部 Corporation 2011 版权所有
 */
package com.mingyou.login.struc;

import com.mingyou.community.Community;
import com.mykj.comm.io.TDataOutputStream;

/**
 * @ClassName: HCMD_LOGIN_V2_ACCOUNTS
 * @Description: 用户名密码登录参数封装
 * @author fwq
 * @date 2011-5-16 上午11:05:05
 */
public class HCMD_LOGIN_V2_ACCOUNTS extends HCMD_LOGIN_V2 {
	/** 帐号串 */
	String Accounts = "";

	/** 密码串 */
	String PassWord = "";

	public HCMD_LOGIN_V2_ACCOUNTS() {

	}

	public HCMD_LOGIN_V2_ACCOUNTS(String accounts, String password) {
		Accounts = accounts;
		PassWord = password;
	}

	/**
	 * @Title: getData
	 * @Description: 组装数据
	 * @return
	 * @version: 2011-5-12 下午06:09:57
	 */
	public TDataOutputStream getData() {
		TDataOutputStream dos = new TDataOutputStream(false);
		dos.writeInt(PlatID); // 平台ID
		dos.writeInt(Community.getInstacne().getGameID()); // 游戏ID
		dos.write(VerCmd); // 游戏版本，单款和非单款
		dos.writeUTFByte(MobileCode); // 手机型号字符串
		dos.writeUTFByte(Accounts); // 帐号
		dos.writeUTFByte(PassWord); // 密码
		dos.writeInt(ClientID); // 客户端标识
		dos.writeInt(ClientSort); // 客户端类型标识
		dos.writeUTFByte(ChildChanel); // 子渠道号
		dos.writeUTFShort(MoblieProperty); // 手机属性数据
		dos.writeInt(verCode);
		return dos;
	}
}
