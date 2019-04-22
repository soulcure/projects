/**
 * @Title: HCMD_LOGIN_V2.java
 * @Package com.myou.mjava.hall.socket.struc
 * @Description: TODO(用一句话描述该文件做什么)
 * @author fwq
 * @date 2011-5-16 上午11:08:04
 * @version V1.0
 * @sine Copyright 名游网络手机部 Corporation 2011 版权所有
 */
package com.mingyou.login.struc;

import com.mingyou.community.Community;
import com.mykj.comm.io.TDataOutputStream;

/**
 * @ClassName: HCMD_LOGIN_V2
 * @Description: 登录公共数据封装
 * @author fwq
 * @date 2011-5-16 上午11:08:04
 */
public class HCMD_LOGIN_V2 {
	/** 平台ID */
	int PlatID = Community.PlatID;

//	/** 游戏ID */
//	int GameID = 0;

	/**
	 * 游戏版本请求命令 0 –请求单个游戏版本 1—请求所有游戏版本
	 */
	byte VerCmd = 1;

	/** 手机型号字符串 */
	public String MobileCode = Community.getInstacne().getCID();

	/**
	 * 客户端标识(新开发必须增加字段) 编号由运维部分配
	 */
	int ClientID = 8001;

	/**
	 * 手机端类型标识 <br>
	 * 0-客户移动系列（默认） <br>
	 * 1- mtk系列 <br>
	 * 2- 拆包版本（配合移动大厅） <br>
	 * 其它保留使用，以PC字节序位准。 <br>
	 * 4-定义为android系列
	 */
	int ClientSort = Community.gamePlatform;

	/**
	 * ChildChanel: 由纯数字组成的不超过19位长度的字符串，采用字符串格式
	 */
	String ChildChanel = Community.getInstacne().getSubChannel();

	/**
	 * MoblieProperty: 手机属性数据 机型名 分辨率 内存大小 系统名 系统版本号 IMSI IMEI。。。
	 */
	public String MoblieProperty = Community.getInstacne().getMoblieProperty();
	
	/**
	 * verCode: 登录协议版本号
	 */
	public int verCode=Community.getInstacne().getVerCode();

	public HCMD_LOGIN_V2() {
		try {
			ClientID = Integer.parseInt(Community.getInstacne().getChannel());
		} catch (Exception e) {
			ClientID = 8001;
		}
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
		dos.writeInt(ClientID);
		dos.writeInt(ClientSort);
		dos.writeUTFByte(ChildChanel);
		dos.writeUTFShort(MoblieProperty);
		dos.writeInt(verCode);
		return dos;
	}
}
