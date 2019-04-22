package com.mykj.andr.model;

import java.io.Serializable;

import com.mingyou.community.MUserInfo;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.game.utils.Log;

/***
 * 
 * @ClassName: UserInfo
 * @Description: 用户信息数据
 * @author Administrator
 * @date 2012-9-13 下午04:14:22
 * 
 */
public class UserInfo implements Serializable {
	/**
	 * @Fields serialVersionUID: TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 1L;

	/** 用户头像索引 */
	public int faceID;
	/** 用户ID */
	public int userID;

	/** 组ID */
	public int groupID;

	/** 游戏ID */
	public int gameID;

	/** 用户权限 */
	public int userRight;

	/** 用户管理权限 */
	public int masterRight;

	/** 用户账号 */
	public String account = ""; // 玩家账号，从“（”中分离出来，不需读取包数据
	/** 用户昵称 */
	public String nickName = "";

	public String groupName = ""; // 群组名称

	public String underWrite = "";// 个性签名

	/** 用户性别 0女 1男 */
	public byte gender;

	/** 用户会员等级
	 * (N<12表示非VIP, 灰色VIP0
	 * 12<=N<=20 VIP等级1-9)
	 *  */
	public byte memberOrder;

	public byte masterOrder;// 管理等级

	public byte starLevel;// 会员星级--add

	public long score; // 游戏币种，如欢乐豆、开心果

	public long dbScore; // 用户积分库分数

	public int winCount; // 胜利盘数

	public int lostCount; // 失败盘数

	public int drawCount; // 和局盘数

	public int fleeCount; // 断线数目

	public int experience; // 用户经验

	public short tableID;// 桌子号码

	public short chairID;// 椅子位置

	public byte userStatus = USER_FREE;// 用户状态/** 玩家状态---坐下未举手，坐下举手状态，坐下旁观，游戏中
										// */

	public byte companion;// 比赛头衔(移动等级)*/

	public String province = "";// 省

	public String city = "";// 市

	public String matchID = "";// 比赛标志

	public int masterScore; // 大师分
	
	
	/**用户头像ID对应数值，用户判断用户是否已经修改了用户头像*/
	public byte faceIdValue=0; //2014-9-26用户上传头像功能新增

	// -----------------------------------------------------------------------------------------------------

	public byte cbMobileLevel; // 移动会员等级 //速配协议使用

	public int dwAvataVer; // 游戏ShowID //速配协议使用

	/** 用户经验 */
	public int exp;
	/** 用户密码 */
	public String password = "";

	/** StatusBit 状态位定义（32个bit中） */
	public int statusBit;
	/** 省市编码 */
	public String AreaCode = "";
	/** 用户token串 */
	public String Token = "";
	/** 登录类型 */
	public byte loginType;
	/** 用户乐豆 */
	private int bean = 0;

	public int muid;

	public long guid;
	
	/** 元宝  */
	private int yuanBao;
	/** 话费券 */
	private int mobileVoucher;

	public int getYuanBao() {
		return yuanBao;
	}

	public void setYuanBao(int yuanBao) {
		this.yuanBao = yuanBao;
	}

	public int getMobileVoucher() {
		return mobileVoucher;
	}

	public void setMobileVoucher(int mobileVoucher) {
		this.mobileVoucher = mobileVoucher;
	}

	/** 是否允许旁观 */
	public boolean allowLook;

	/**
	 * 昵称颜色RGB值 列：#FFFFFF 则nickColor为0xFFFFFF
	 * */
	public int nickColor = -1;// 默认值-1,为无效
	/** vip是否过期，0过期，1未过期 */
	public byte isVipExpired = 0;

	// ------------------------------------下面是用户状态：cbUserStatus------------------------------------------------
	// 用户在游戏中状态定义--与服务器定义相同
	/** 没有状态 */
	public final static byte USER_NULL = 0;

	/** 站立状态 */
	public final static byte USER_FREE = 1;

	/** 坐下状态 */
	public final static byte USER_SET = 2;

	/** 准备 举手状态 */
	public final static byte USER_READY = 3;

	/** 旁观状态 */
	public final static byte USER_LOOKON = 4;

	/** 游戏状态 */
	public final static byte USER_PLAY = 5;

	/** 掉线状态 */
	public final static byte USER_OFFLINE = 6;

	/** 托管状态 */
	public final static byte USER_SYSTEMCONTROL = 7;

	// ------------------------------------------------------------------------------------------------------

	public UserInfo() {

	}

	public void setFaceId(int id) {
		faceID = id;
	}

	public int getFaceId() {
		return faceID;
	}

	public UserInfo(TDataInputStream dis) {
		if (dis == null) {
			return;
		}
		dis.setFront(false);
		faceID = dis.readShort(); // 表情ID  2014-10-08 废弃
		userID = dis.readInt(); // 用户ID 4
		groupID = dis.readInt(); // 组ID 4
		gameID = dis.readInt(); // 游戏ID 4
		userRight = dis.readInt(); // 用户权限 4
		masterRight = dis.readInt(); // 管理权限 4

		Short nickLen = dis.readShort();
		String temp = dis.readUTF(nickLen);// 昵称（若长度为0 则这里没有）UTF8
											// NickNameLen:因为服务器下发的账户用和昵称是一起的
		/** 格式是：dajsdhasd(41564554) **/
		String[] str = UserInfo.getNickNameAndAccountName(temp, "（", "）");
		if (str != null && str.length == 2) {
			account = str[0];
			nickName = str[1];
		}

		Short groupLen = dis.readShort();
		// 2013-1-22新增大师分
		masterScore = (int) dis.readLong(); // 大师分 4
		groupName = dis.readUTF(groupLen-8); // 组名称 UTF8 GroupNameLen   //游戏使用了此数据字段表示扩展数据

		Short underLen = dis.readShort();
		underWrite = dis.readUTF(underLen); // 签名 UTF8 UnderWriteLen

		gender = dis.readByte(); // 用户性别 1
		memberOrder = dis.readByte(); // 会员等级 1
		masterOrder = dis.readByte(); // 管理等级 1
		starLevel = dis.readByte(); // 会员星级 1
		score = dis.readLong(); // 游戏币种 8
		dbScore = dis.readLong(); // 用户积分库分数 8
		winCount = dis.readInt(); // 胜利盘数 4
		lostCount = dis.readInt(); // 失败盘数 4
		drawCount = dis.readInt(); // 和局盘数 4
		fleeCount = dis.readInt(); // 断线数目 4
		experience = dis.readInt(); // 用户经验 4
		tableID = dis.readShort(); // 桌子号码 2
		chairID = (byte) dis.readShort(); // 椅子位置 2
		userStatus = dis.readByte(); // 用户状态 1
		companion = dis.readByte(); // 1
		province = dis.readUTFByte(); // 省份 UTF8
		city = dis.readUTFByte(); // 城市 UTF8
		matchID = dis.readUTFByte(); // 比赛标志 UTF8

		
	}

	/**
	 * 获取用户乐豆
	 * 
	 * @return
	 */
	public int getBean() {
		return bean;
	}

	/** 统一设置乐豆 **/
	public void setBean(int bean) {
		this.bean = bean;
	}

	/** 统一设置昵称 **/
	public void setNickName(String nick) {
		nickName = nick;
	}

	/**
	 * @Title: setValueForRoomParse
	 * @Description: 设置进入房间成功后的用户信息解析，专用与进入房间成功协议
	 * @param dis
	 * @version: 2011-7-11 下午02:01:50
	 */
	public static void setValueForRoomParse(TDataInputStream dis, UserInfo user) {
		// 保存读取信息
		dis.setFront(false);
		// 用户属性
		user.faceID = dis.readInt(); // 头像索引
		/** 字节对齐 跳过两个字节 */
		//dis.skip(2); // chenqy faceID--已经由short改为int
		user.userID = dis.readInt(); // 用户I D
		user.gameID = dis.readInt(); // 游戏I D
		user.groupID = dis.readInt(); // 社团索引
		user.userRight = dis.readInt(); // 用户等级
		user.masterRight = dis.readInt(); // 管理权限
		// 用户属性
		user.gender = dis.readByte(); // 用户性别
		user.memberOrder = dis.readByte(); // 会员等级
		user.masterOrder = dis.readByte(); // 管理等级
		user.starLevel = dis.readByte(); // 会员星级

		user.tableID = dis.readShort(); // 桌子号码(进入房间成功后，需要坐下桌子)
		user.chairID = (byte) dis.readShort(); // 椅子位置
		user.userStatus = dis.readByte(); // 用户状态
		/** 字节对齐 跳过七个字节 */
		dis.skip(7);
		// 用户积分
		user.score = dis.readLong(); // 游戏币种，如欢乐豆、开心果
		user.dbScore = dis.readLong(); // 用户积分库分数

		user.winCount = dis.readInt(); // 胜利盘数
		user.lostCount = dis.readInt(); // 失败盘数
		user.drawCount = dis.readInt(); // 和局盘数
		user.fleeCount = dis.readInt(); // 断线数目
		user.experience = dis.readInt(); // 用户经验
		/** 字节对齐 跳过四个字节 */
		dis.skip(4);
		user.cbMobileLevel = dis.readByte(); // 移动会员等级
		user.dwAvataVer = dis.readInt(); // 游戏ShowID
		
		dis.skip(19);  //服务器预留字段
		user.faceIdValue=dis.readByte(); //2014-9-26用户上传头像功能新增  ,暂不使用
		
//		HeadManager.getInstance().setSelfImageByValue(AppConfig.mContext,
//				user.userID, user.faceID, user.faceIdValue);
		
	}

	public static void parseUserInfo(MUserInfo muserInfo, UserInfo user) {
		user.faceID = muserInfo.headNo; // 头像索引
		user.userID = muserInfo.userId; // 用户I D
		user.gameID = muserInfo.dwGameID; // 游戏I D
		user.groupID = muserInfo.dwGroupID; // 社团索引
		user.userRight = muserInfo.userRight; // 用户等级
		user.masterRight = muserInfo.masterRight; // 管理权限
		user.bean = muserInfo.lBean;
		// 用户属性
		user.gender = muserInfo.cbGender; // 用户性别
		user.memberOrder = muserInfo.memberOrder; // 会员等级
		user.masterOrder = muserInfo.masterOrder; // 管理等级
		user.starLevel = muserInfo.starLevel; // 会员星级

		user.tableID = muserInfo.tableNo; // 桌子号码(进入房间成功后，需要坐下桌子)
		user.chairID = muserInfo.seatNo; // 椅子位置
		user.userStatus = muserInfo.cbUserStatus; // 用户状态
		user.score = muserInfo.lScore; // 游戏币种，如欢乐豆、开心果
		user.dbScore = muserInfo.poolScore; // 用户积分库分数

		user.winCount = muserInfo.lWinCount; // 胜利盘数
		user.lostCount = muserInfo.lLostCount; // 失败盘数
		user.drawCount = muserInfo.lDrawCount; // 和局盘数
		user.fleeCount = muserInfo.lFleeCount; // 断线数目
		// user.lExperience = muserInfo.lExperience; // 用户经验
		// user.cbMobileLevel = muserInfo.readByte(); // 移动会员等级
		// user.dwAvataVer=user.dwShowID = muserInfo.readInt(); // 游戏ShowID

		user.guid = muserInfo.guid;
		// user.statusBit=
		user.nickName = muserInfo.nickName;
		// user.loginType=muserInfo.
		user.muid = muserInfo.muid;
		user.account = muserInfo.account;
		user.statusBit = muserInfo.statusBit;
		user.nickColor = muserInfo.nickColor;
		user.isVipExpired = muserInfo.isVipExpired;
		
		//user.masterScore=muserInfo.masterScore;
		
		user.faceIdValue = muserInfo.faceIdValue;  //2014-9-26用户上传头像功能新增
        
//		HeadManager.getInstance().setSelfImageByValue(AppConfig.mContext,
//				user.userID, user.faceID, user.faceIdValue);
	}

	/**
	 * 赋值
	 * 
	 * @param user
	 */
	public void setValue(final UserInfo user) {
		if (user == null || user.userID <= 0) { // 2011.3.31增加判断无效用户
			return;
		}
		userID = user.userID;
		account = user.account;
		nickName = user.nickName;
		//faceID = user.faceID;  //原short类型faceId已经废弃，游戏使用扩展数据表示新 faceId
		gender = user.gender;
		userRight = user.userRight;
		memberOrder = user.memberOrder;
		starLevel = user.starLevel;
		masterOrder = user.masterOrder;
		masterRight = user.masterRight;

		groupID = user.groupID;
		gameID = user.gameID;
		score = user.score;
		bean = (int) score;// 2012.3.20为了更新标题栏(lScore下发必须总是为玩家乐豆才行) 李南坤
		winCount = user.winCount;
		lostCount = user.lostCount;
		drawCount = user.drawCount;
		fleeCount = user.fleeCount;
		allowLook = user.allowLook; // 是否允许旁观（本身没有这个属性）
		experience = user.experience;
		groupName = user.groupName;// 扩展参数
		underWrite = user.underWrite;// 个性签名
		companion = user.companion;
		tableID = user.tableID;
		chairID = user.chairID;
		userStatus = user.userStatus;
		province = user.province;
		city = user.city;
		// gamePassbook = user.gamePassbook; 游戏通行证
	}

	/**
	 * 获取账号及昵称
	 * 
	 * @param res
	 * @param clipStr1
	 * @param clipStr2
	 * @return
	 */
	public static String[] getNickNameAndAccountName(String res,
			String clipStr1, String clipStr2) {
		if (res == null || clipStr1 == null) {
			return null;
		}

		res = res.replace(clipStr1, "@");
		res = res.replace(clipStr2, "");
		return res.split("@");
	}

	public boolean isVip() {
		return memberOrder >0;
//		return isVipExpired == 1 ? true : false;
	}
	public String getVipName(){
		if(memberOrder > 0){
    		if(memberOrder == 1){
    			return "会员";
    		}else if(memberOrder == 2){
    			return "男爵";
    		}else if(memberOrder == 3){
    			return "子爵";
    		}else if(memberOrder == 4){
    			return "伯爵";
    		}else if(memberOrder == 5){
    			return "侯爵";
    		}else{
    			return "公爵";
    		}
		}
		return "";
	}
	public int getVipLevel() {
//		if(isVip()){
//			return memberOrder < 12 ? 0 : memberOrder - 11;
//		}
//		return memberOrder<2? 0 : memberOrder; 
//		
		return memberOrder;
//		return memberOrder < 12 ? memberOrder : memberOrder - 11;
	}

}
