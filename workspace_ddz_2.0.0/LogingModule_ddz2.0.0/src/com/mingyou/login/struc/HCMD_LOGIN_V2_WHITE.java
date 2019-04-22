/**
 * @Title: HCMD_LOGIN_V2_WHITE.java
 * @Package com.myou.mjava.hall.socket.struc
 * @Description: TODO(用一句话描述该文件做什么)
 * @author fwq
 * @date 2011-5-12 下午05:21:43
 * @version V1.0
 * @sine Copyright 名游网络手机部 Corporation 2011 版权所有
 */
package com.mingyou.login.struc;

import com.mingyou.community.Community;
import com.mykj.comm.io.TDataOutputStream;

/**
 * @ClassName: HCMD_LOGIN_V2_WHITE
 * @Description: 白名单登录参数封装 <br>
 *               参考
 * @author fwq
 * @date 2011-5-12 下午05:21:43
 */
public class HCMD_LOGIN_V2_WHITE extends HCMD_LOGIN_V2 {

	/** 移动基地用户ID ，来自白名单链接 */
	int UserID;

	/** 移动token串 */
	String API_KEY = "";

	public HCMD_LOGIN_V2_WHITE(int UserID, String API_KEY) {
		this.UserID = UserID;
		this.API_KEY = API_KEY;
	}

	/**
	 * @Title: getData
	 * @Description: 组装数据
	 * @return
	 * @version: 2011-5-12 下午06:09:57
	 */
	public TDataOutputStream getData() {
		TDataOutputStream dos = new TDataOutputStream(false);
		dos.writeInt(PlatID);   //
		dos.writeInt(Community.getInstacne().getGameID());
		dos.write(VerCmd);
		dos.writeUTFByte(MobileCode);
		dos.writeInt(UserID);
		dos.writeUTFByte(API_KEY);
		dos.writeInt(ClientID);
		dos.writeInt(ClientSort);
		dos.writeUTFByte(ChildChanel);
		dos.writeUTFShort(MoblieProperty);
		dos.writeInt(verCode);
		return dos;
	}

}
