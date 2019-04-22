/**
 * @Title: HMSUB_CMD_LOGIN_AT.java
 * @Package com.myou.mjava.hall.socket.struc
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Link
 * @date 2011-8-24 下午05:00:00
 * @version V1.0
 * @sine 
 * Copyright 名游网络手机部  Corporation 2011
 * 版权所有
 * 
 */
package com.mingyou.login.struc;

import com.mingyou.community.Community;
import com.mykj.comm.io.TDataOutputStream;

/**
 * @ClassName: HMSUB_CMD_LOGIN_AT
 * @Description: AT登录请求数据封装
 * @author Link
 * @date 2011-8-24 下午05:00:00
 * 
 */
public class HMSUB_CMD_LOGIN_AT extends HCMD_LOGIN_V2 {
	/** AT串 */
	String AT = "";

	public HMSUB_CMD_LOGIN_AT(String AT) {
		this.AT = AT;
	}

	/**
	 * @Title: getData
	 * @Description: 组装数据
	 * @return
	 * @version: 2011-5-12 下午06:09:57
	 */
	public TDataOutputStream getData() {
		TDataOutputStream dos = new TDataOutputStream(false);
		dos.writeInt(PlatID);
		dos.writeInt(Community.getInstacne().getGameID());
		dos.write(VerCmd);
		dos.writeUTFByte(MobileCode);
		dos.writeUTFByte(AT);
		dos.writeInt(ClientID);
		dos.writeInt(ClientSort);
		dos.writeUTFByte(ChildChanel);
		dos.writeUTFShort(MoblieProperty);
		dos.writeInt(verCode);
		return dos;
	}
}
