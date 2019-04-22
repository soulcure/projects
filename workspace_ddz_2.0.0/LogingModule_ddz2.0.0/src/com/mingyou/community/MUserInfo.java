/**
 * 
 */
package com.mingyou.community;

import java.io.IOException;

import com.mykj.comm.io.TDataInputStream;


/**
 * @author Jason
 * 
 */
public class MUserInfo {
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

	/** 玩家状态---坐下未举手，坐下举手状态，坐下旁观，游戏中 */
	public byte cbUserStatus = USER_FREE;

	// -----------属性------
	/** 用户ID */
	public int userId;

	/** 智运会三期添加统一用户ID */
	public int ID;

	/** 智运会三期添加 用户的元宝 */
	public int yuanBao;

	/** 用户昵称 */
	public String nickName;

	/** 帐号 */
	public String account;

	/** 用户头像编号 */
	public int headNo;

	/** 用户性别 0女 1男 */
	public byte cbGender;

	/**
	 * 用户权限 <br>
	 * UR_CANNOT_PLAY 0x00000001L //不能进行游戏 <br>
	 * UR_CANNOT_LOOKON 0x00000002L //不能旁观游戏 <br>
	 * UR_CANNOT_WISPER 0x00000004L //不能发送私聊 <br>
	 * UR_CANNOT_ROOM_CHAT 0x00000008L //不能大厅聊天 <br>
	 * UR_CANNOT_GAME_CHAT 0x00000010L //不能游戏聊天
	 */
	public int userRight;

	/** 会员等级 -------------0-游客 1-注册用户 11-钻石会员1级 12-钻石会员2级 13-钻石会员3级 */
	public byte memberOrder;

	/** 会员星级 */
	public byte starLevel;

	/** 管理等级 */
	public byte masterOrder;

	/**
	 * 管理权限 <br>
	 * UR_CAN_LIMIT_PLAY 0x00000001L //允许禁止游戏 <br>
	 * UR_CAN_LIMIT_LOOKON 0x00000002L //允许禁止旁观 <br>
	 * UR_CAN_LIMIT_WISPER 0x00000004L //允许禁止私聊 <br>
	 */
	public int masterRight;

	/** 组ID */
	public int dwGroupID;

	/** 组名 */
	public String groupName;

	/** 个性签名 */
	public String underWrite;

	/** 用户关系 */
	public byte cbCompanion;

	/** 移动会员等级(现做为比赛场等级头衔) */
	public byte cbMobileLevel;

	/** 游戏ShowID */
	public int dwShowID;

	// ---------------游戏信息-------------
	/** 游戏 I D */
	public int dwGameID;

	/** 胜利盘数 */
	public int lWinCount = 0;

	/** 失败盘数 */
	public int lLostCount = 0;

	/** 和局盘数 */
	public int lDrawCount = 0;

	/** 断线数目 */
	public int lFleeCount = 0;

	/** 是否允许旁观 */
	public boolean allowLook;

	// ----------------拥有财富-------------
	/** 用户分数：在金币场则为金币；在豆场则为开心豆；在积分场则为积分 */
	public long lScore;

	/** 用户积分库分数，在积分场时值为0 */
	public long poolScore;

	/** 用户经验 */
	public String lExperience;

	/** 用户乐豆 */
	public int lBean;

	// -----------------位置---------------
	/** 是否登录 */
	public boolean isLogin;

	/** 所在的游戏名 */
	public static String gameName;

	/** 所在的分区号 */
	public static int areaNo;

	/** 所在分区名字 */
	public static String areaName;

	/** 所在房间类型的序号 */
	public static int roomTypeNo;

	/** 所在房间类型的名字 */
	public static String roomTypeName;

	/** 所在房间号 */
	public static int roomNo;

	/** 所在房间的名字 */
	public static String roomName;

	/** 所在房间在数组里的位置 */
	public static byte roomArrayPos;

	/** 用户所在桌号 从0开始 */
	public short tableNo = -1;

	/** 座位号 做0开始 */
	public byte seatNo = -1;

	// /** 令牌环--令牌用于手机网关登录 */
	// public static String szToken = "50500a21ca1a5f7c19b9092ea367b508";

	/** 省份 */
	public String province;

	/** 城市 */
	public String city;

	/** 游戏通行证 */
	public String gamePassbook;

	/** 比赛标志 **/
	public String matcher;

	/** GUID **/
	public int guid;

	/** 移动社区ID **/
	public int muid = 0;

	/**
	 * StatusBit 状态位定义（32个bit中） 第 1 bit: 0-表示不能购买道具 1-可以购买道具（PC为0x00000001） 第 2
	 * bit: 0-表示MTK购买走社区流程 1-可以MTK购买走MTK流程（PC为0x00000002）
	 */
	public int statusBit;

	/** 大师分 **/
	public int masterScore = 0;
	/** 昵称颜色RGB值*/
	public int nickColor =  -1;//默认值-1,为无效
	/**vip是否过期，0过期，1未过期*/
	public byte isVipExpired = 0;

	/**用户头像ID对应数值，用户判断用户是否已经修改了用户头像*/
	public byte faceIdValue=0;  //2014-9-26用户上传头像功能新增
	
	/**
	 * 构造函数
	 */
	public MUserInfo() {
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: 支持从流构造一个用户信息对象
	 * </p>
	 * 
	 * @param io
	 */
	public MUserInfo(TDataInputStream io) {
		setValue(io);
	}

	/**
	 * @Title: setValue
	 * @Description: 从流中读取并设置用户数据
	 * @param io
	 * @version: 2011-7-6 下午02:58:13
	 */
	public void setValue(TDataInputStream dis) {
		if (dis == null) {
			return;
		}
		dis.setFront(false);
		headNo = dis.readShort(); // 表情
		userId = dis.readInt(); // 用户ID 4
		dwGroupID = dis.readInt(); // 组ID 4
		dwGameID = dis.readInt(); // 游戏ID 4
		userRight = dis.readInt(); // 用户权限 4
		masterRight = dis.readInt(); // 管理权限 4
		final String temp = dis.readUTFShort(); // 昵称（若长度为0 则这里没有）UTF8
												// NickNameLen
		final String[] str = getNickNameAndAccountName(temp, "（", "）");
		if (str != null && str.length == 2) {
			account = str[0];
			nickName = str[1];
		}
		groupName = dis.readUTFShort(); // 组名称 UTF8 GroupNameLen
		underWrite = dis.readUTFShort(); // 签名 UTF8 UnderWriteLen
		cbGender = dis.readByte(); // 用户性别 1
		memberOrder = dis.readByte(); // 会员等级 1
		masterOrder = dis.readByte(); // 管理等级 1
		starLevel = dis.readByte(); // 会员星级 1
		lScore = dis.readLong(); // 游戏币种 8
		poolScore = dis.readLong(); // 用户积分库分数 8
		lWinCount = dis.readInt(); // 胜利盘数 4
		lLostCount = dis.readInt(); // 失败盘数 4
		lDrawCount = dis.readInt(); // 和局盘数 4
		lFleeCount = dis.readInt(); // 断线数目 4
		lExperience = dis.toString(); // 用户经验 4
		tableNo = dis.readShort(); // 桌子号码 2
		seatNo = (byte) dis.readShort(); // 椅子位置 2
		cbUserStatus = dis.readByte(); // 用户状态 1
		cbCompanion = dis.readByte(); // 1
		province = dis.readUTFByte(); // 省份 UTF8
		city = dis.readUTFByte(); // 城市 UTF8
		matcher = dis.readUTFByte(); // 比赛标志 UTF8
//		masterScore = dis.readInt(); // 大师分
		
	}

	/**
	 * 将当前用户信息对象设置为无效对象，userid=0
	 */
	public void setUnValid() {
		userId = 0;
	}

	/**
	 * 验证当前用户信息是否有效,userid>0
	 * 
	 * @return
	 */
	public boolean isValid() {
		return userId > 0;
	}

	/**
	 * 此方法用来分割服务器下发的用户昵称和帐号
	 * 
	 * @param res
	 * @param clipStr1
	 * @param clipStr2
	 * @return
	 */
	public static String[] getNickNameAndAccountName(String res, String clipStr1, String clipStr2) {
		if (res == null) {
			return null;
		}
		if (clipStr1 == null) {
			return null;
		}
		int index = -1;
		int index2 = -2;
		index = res.indexOf(clipStr1);
		if (clipStr2 == null) {
			if (index == -1 || index > res.length() - 1) {
				return null;
			}
			return new String[] { res.substring(0, index), res.substring(index + 1) };
		}
		index2 = res.indexOf(clipStr2, index + 1);
		if (index2 == -1 || index2 > res.length() - 1) {
			return null;
		}
		return new String[] { res.substring(0, index), res.substring(index + 1, index2) };
	}

	/**
	 * 初始化，还原对象参数
	 * 
	 * @author 139game 2009-12-29上午10:52:01
	 */
	public void init() {
		userId = 0;
		nickName = null;
		headNo = 0;
		cbGender = 0;
		userRight = 0;
		memberOrder = 0;
		starLevel = 0;
		masterOrder = 0;
		masterRight = 0;
		dwGroupID = 0;
		dwGameID = 0;
		lScore = 0;
		poolScore = 0;
		lWinCount = 0;
		lLostCount = 0;
		lDrawCount = 0;
		lFleeCount = 0;
		allowLook = false;
		lExperience = null;
		groupName = null;
		underWrite = null;
		cbCompanion = 0;
		matcher = null;
		tableNo = -1;
		seatNo = -1;
		cbUserStatus = USER_FREE;
//		masterScore = 0;
	}

	/**
	 * 赋值
	 * 
	 * @param user
	 */
	public void setValue(final MUserInfo user) {
		if (user == null || user.userId <= 0) { // 2011.3.31增加判断无效用户
			return;
		}
		userId = user.userId;
		account = user.account;
		nickName = user.nickName;
		headNo = user.headNo;
		cbGender = user.cbGender;
		userRight = user.userRight;
		memberOrder = user.memberOrder;
		starLevel = user.starLevel;
		masterOrder = user.masterOrder;
		masterRight = user.masterRight;
		dwGroupID = user.dwGroupID;
		dwGameID = user.dwGameID;
		lScore = user.lScore;
		lBean = (int) lScore;// 2012.3.20为了更新标题栏(lScore下发必须总是为玩家乐豆才行) 李南坤
		poolScore = user.poolScore;
		lWinCount = user.lWinCount;
		lLostCount = user.lLostCount;
		lDrawCount = user.lDrawCount;
		lFleeCount = user.lFleeCount;
		allowLook = user.allowLook;
		lExperience = user.lExperience;
		groupName = user.groupName;
		underWrite = user.underWrite;
		cbCompanion = user.cbCompanion;

		tableNo = user.tableNo;
		seatNo = user.seatNo;
		cbUserStatus = user.cbUserStatus;
		province = user.province;
		city = user.city;
		gamePassbook = user.gamePassbook;
		muid = user.muid;
//		masterScore = user.masterScore;
		// MLoger.info("用户信息\r"+toString());
	}

	/**
	 * 复制对象
	 * 
	 * @return
	 */
	public MUserInfo clone() {
		MUserInfo user = new MUserInfo();
		user.userId = userId;
		user.account = account;
		user.nickName = nickName;
		user.headNo = headNo;
		user.cbGender = cbGender;
		user.userRight = userRight;
		user.memberOrder = memberOrder;
		user.starLevel = starLevel;
		user.masterOrder = masterOrder;
		user.masterRight = masterRight;
		user.dwGroupID = dwGroupID;
		user.dwGameID = dwGameID;
		user.lScore = lScore;
		user.poolScore = poolScore;
		user.lWinCount = lWinCount;
		user.lLostCount = lLostCount;
		user.lDrawCount = lDrawCount;
		user.lFleeCount = lFleeCount;
		user.allowLook = allowLook;
		user.lExperience = lExperience;
		user.tableNo = tableNo;
		user.seatNo = seatNo;
		user.cbUserStatus = cbUserStatus;
		user.groupName = groupName;
		user.underWrite = underWrite;
//		user.masterScore=masterScore;
		return user;
	}

	/**
	 * @Title: setValueForRoomParse
	 * @Description: 设置进入房间成功后的用户信息解析，专用与进入房间成功协议
	 * @param dis
	 * @version: 2011-7-11 下午02:01:50
	 */
	public static void setValueForRoomParse(final TDataInputStream dis, final MUserInfo user) {
		// 保存读取信息
		dis.setFront(false);
		// 用户属性
		user.headNo = dis.readShort(); // 头像索引
		/** 字节对齐 跳过两个字节 */
		try {
			dis.read(new byte[2]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		user.userId = dis.readInt(); // 用户I D
		user.dwGameID = dis.readInt(); // 游戏I D
		user.dwGroupID = dis.readInt(); // 社团索引
		user.userRight = dis.readInt(); // 用户等级
		user.masterRight = dis.readInt(); // 管理权限
		// 用户属性
		user.cbGender = dis.readByte(); // 用户性别
		user.memberOrder = dis.readByte(); // 会员等级
		user.masterOrder = dis.readByte(); // 管理等级
		user.starLevel = dis.readByte(); // 会员星级
		// 用户状态
		user.tableNo = dis.readShort(); // 桌子号码
		user.seatNo = (byte) dis.readShort(); // 椅子位置
		user.cbUserStatus = dis.readByte(); // 用户状态
		/** 字节对齐 跳过七个字节 */
		try {
			dis.read(new byte[3]);
			dis.read(new byte[4]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 用户积分
		user.lScore = dis.readLong(); // 游戏币种，如欢乐豆、开心果
		user.poolScore = dis.readLong(); // 用户积分库分数
		user.lWinCount = dis.readInt(); // 胜利盘数
		user.lLostCount = dis.readInt(); // 失败盘数
		user.lDrawCount = dis.readInt(); // 和局盘数
		user.lFleeCount = dis.readInt(); // 断线数目
		user.lExperience = dis.toString(); // 用户经验
		/** 字节对齐 跳过四个字节 */
		try {
			dis.read(new byte[4]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		user.cbMobileLevel = dis.readByte(); // 移动会员等级
		user.dwShowID = dis.readInt(); // 游戏ShowID
//		user.masterScore=dis.readInt();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("userId: ").append(userId).append('\n');
		sb.append("account: ").append(account).append('\n');
		sb.append("nickName: ").append(nickName).append('\n');
		sb.append("headNo: ").append(headNo).append('\n');
		sb.append("cbGender: ").append(cbGender).append('\n');
		sb.append("tableNo: ").append(tableNo).append('\n');
		sb.append("seatNo: ").append(seatNo).append('\n');
		sb.append("cbUserStatus: ").append(cbUserStatus).append('\n');
//		sb.append("masterScore:").append(masterScore);
		return sb.toString();
	}
}
