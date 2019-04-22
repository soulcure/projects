package com.mykj.andr.lottoround;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Handler;
import android.os.Message;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;

public class LottoRoundManager{
	private static LottoRoundManager instance;
	public static final String TAG = "LottoRoundManager";
	// -----------------------抽奖协议码-------------------------------------------
	/** 抽奖相关主协议码 */
	public static final short MDM_LOTTERY = 19;

	/**	请求抽奖机配置 */
	public static final short LSUB_CMD_LOT_CONFIG_REQ = 1;
	/** 抽奖机配置返回*/
	public static final short LSUB_CMD_LOT_CONFIG_RESP =2;
	/** 请求获取抽奖奖励协议码 */
	public static final short LSUB_CMD_LOTTERY_REQ = 3;
	/** 返回获取抽奖奖励协议码 */
	public static final short LSUB_CMD_LOTTERY_RESP = 4;
	/** 请求更新抽奖次数协议码 */
	public static final short LSUB_CMD_UPDATE_LOT_TIMES_REQ = 5;
	/** 返回更新抽奖次数协议码 */
	public static final short LSUB_CMD_UPDATE_LOT_TIMES_RESP = 6;
	/** 请求获取抽奖次数协议码 */
	public static final short LSUB_CMD_GET_LOT_TIMES_REQ = 7;
	/** 返回获取抽奖次数协议码 */
	public static final short LSUB_CMD_GET_LOT_TIMES_RESP = 8;
	/** 请求获取抽奖奖励协议码 */
	public static final short LSUB_CMD_MULTI_LOTTERY_REQ = 11;
	/** 返回获取抽奖奖励协议码 */
	public static final short LSUB_CMD_MULTI_LOTTERY_RESP = 12;
	/** 请求获取一键抽奖配置协议码 */
	public static final short LSUB_CMD_MULTI_LOT_CONFIG_REQ = 13;
	/** 返回获取一键抽奖配置协议码 */
	public static final short LSUB_CMD_MULTI_LOT_CONFIG_RESP = 14;
	/** 请求获奖名单配置协议码 */
	public static final short LSUB_CMD_WINNER_LIST_REQ = 15;
	/** 返回获奖名单配置协议码 */
	public static final short LSUB_CMD_WINNER_LIST_RESP = 16;
	
	/*-----------------------常量--------------------*/
	public static final int HANDLER_TIMES_REP = 1;  //抽奖次数反馈
	public static final int HANDLER_LOTTO_REP = 2;  //抽奖结果反馈
	public static final int HANDLER_CONFIG_REP = 3; //抽奖配置返回
	
	
	// 一键抽奖版本号sharePreference里的标识
	public static final String MULTI_LOTTERY_VERSION = "multi_lottery_version";
	public static final String LOTTERY_CONFIG = "lottery.cfg";
	public boolean isMultiConfigFinish = false;
	
	/** 多次抽奖配置参数 */
	public byte multiTotal;// 一键抽奖总的道具数
	public String multiVersion;// 一键抽奖的版本号
	public String multiLurl;// 一键抽奖图标的url
	public String multiHurl;// 一键抽奖秘籍的url
	public int multiBCost;// 一键抽奖乐豆抽奖费用
	public byte multiBCostPower;// 一键抽奖乐豆抽奖开关
	public String multiXml;// 一键抽奖奖品列表
	
	
	private LottoRoundManager(){};
	public static LottoRoundManager getInstance(){
		if(instance == null){
			instance = new LottoRoundManager();
		}
		return instance;
	}
	

	/** 设置一键抽奖配置缓存数据 */
	public void setMultiLotteryConfig(TDataInputStream dis, Handler hand) {
		if (dis == null) {
			return;
		}
		dis.setFront(false);
		int tempCode = dis.readByte();
		if (tempCode == 2) {
			// read form file
			TDataInputStream local_tdis = readLotteryData();
			parseLottoconfigData(local_tdis);
			isMultiConfigFinish = true;
			if(hand != null){
				hand.sendEmptyMessage(HANDLER_CONFIG_REP);
			}
		} else if (tempCode == 0) {
			dis.reset();
			parseLottoconfigData(dis);
			// 根据头文件标识的跳过字节数 读取xml数据体
//			dis.skip(headLength - 3);
//			multiXml = dis.readUTFShort();// 配置的xml数据体信息
//			// 跳过3字节进行正常读取
//			dis.reset();
//			dis.skip(3);
//			multiTotal = dis.readByte();// 道具总数
//			multiVersion = dis.readUTFShort();// 版本
//			multiLurl = dis.readUTFShort();// 图标url
//			multiHurl = dis.readUTFShort();// 秘籍url
//			multiBCost = dis.readShort();// 乐豆抽奖费用
//			multiBCostPower = dis.readByte();// 乐豆抽奖开关
//			multiLoTimes = dis.readShort();// 一键抽奖次数
//			multiThreshold = dis.readInt();// 乐豆抽奖门槛
//			multiMaxRaffle = dis.readInt();// 每天允许的最大抽奖次数
			// multiXml = dis.readUTFShort();// 配置的xml数据体信息
			Log.v("MultiLotteryConfig", "total:" + multiTotal + "   "
					+ "version:" + multiVersion + "  " + "lurl:" + multiLurl
					+ "  " + "hurl:" + multiHurl + "  " + "bCost:" + multiBCost
					+ "  " + "bCostPower:" + multiBCostPower + "  " + "times:"
					 + "xml:"
					+ multiXml);
			saveLotteryData(dis);// 保存到文件
			Util.setStringSharedPreferences(AppConfig.mContext,
					MULTI_LOTTERY_VERSION, multiVersion);
			isMultiConfigFinish = true;
			if(hand != null){
				hand.sendEmptyMessage(HANDLER_CONFIG_REP);
			}
		} else {
			isMultiConfigFinish = false;
		}

	}
	

	/** 将本地的抽奖配置文件设置到缓存数据当中 */
	private void parseLottoconfigData(TDataInputStream dis) {
		if (dis == null) {
			return;
		}
		dis.setFront(false);
		// 跳过1字节进行正常读取
		dis.skip(1); //跳过code

		multiTotal = dis.readByte();// 道具总数
		multiVersion = dis.readUTFShort();// 版本
		multiLurl = dis.readUTFShort();// 图标url
		multiHurl = dis.readUTFShort();// 秘籍url
		multiBCost = dis.readShort();// 乐豆抽奖费用
		multiBCostPower = dis.readByte();// 乐豆抽奖开关
		multiXml = dis.readUTFShort();// 配置的xml数据体信息

		parseLotteryPrizesXML(multiXml);
	}
	
	
	/** 保存抽奖配置到本地文件 */
	public void saveLotteryData(TDataInputStream dis) {
		dis.reset();
		File file = AppConfig.mContext.getFileStreamPath(LOTTERY_CONFIG);
		if (file.exists()) {
			AppConfig.mContext.deleteFile(LOTTERY_CONFIG);
		}
		byte[] b = dis.readBytes();
		Util.saveToFile(file, b);
	}
	
	/** 读取本地抽奖配置文件 */
	public TDataInputStream readLotteryData() {
		TDataInputStream tdis = null;
		File file = AppConfig.mContext.getFileStreamPath(LOTTERY_CONFIG);
		if (file.exists()) {
			byte[] b = Util.readBytesFromFile(file);
			tdis = new TDataInputStream(b, false);
		}
		return tdis;
	}
	
	ArrayList<LottoRoundItem> itemList = new ArrayList<LottoRoundItem>();
	
	/**
	 * 获得奖品列表
	 * @return
	 */
	public synchronized ArrayList<LottoRoundItem> getItemList(){
		//数据解析完成前返回null
		if(!isMultiConfigFinish){
			return null;
		}
		return itemList;
	}
	
	/**
	 * @param loDrowPrizes
	 *            抽奖机奖品集合
	 * @param loPrizesXml需要解析的抽奖机奖品列表
	 */
	private boolean parseLotteryPrizesXML(
			String loPrizesXml) {
		boolean isParseSuccess = false;
		if (Util.isEmptyStr(loPrizesXml)) {
			return isParseSuccess;
		}
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(loPrizesXml));
			// 解析事件
			int eventType = p.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = p.getName();
					if (tagName.equals("b")) {
						LottoRoundItem loDrowPrize = new LottoRoundItem(
								Integer.parseInt(p.getAttributeValue(null, "i")));
						loDrowPrize.setType(Integer.parseInt(p
								.getAttributeValue(null, "t")));
						loDrowPrize.setId(Integer.parseInt(p.getAttributeValue(
								null, "p")));
						loDrowPrize.setNum(Integer.parseInt(p
								.getAttributeValue(null, "c")));
						loDrowPrize.setDesc(p.getAttributeValue(null, "d"));
						loDrowPrize.setFileName(p
								.getAttributeValue(null, "bit"));
						// 添加进提供器中
						synchronized (itemList) {
							itemList.add(loDrowPrize);
						}
					}
					isParseSuccess = true;
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					synchronized (itemList) {
						if(itemList.size() > 0){
							Collections.sort(itemList, new Comparator<LottoRoundItem>() {

								@Override
								public int compare(LottoRoundItem lhs, LottoRoundItem rhs) {
									// TODO Auto-generated method stub
									return rhs.getIndex() - lhs.getIndex();
								}
							});
						}
					}
					break;
				default:
					break;
				}
				// 用next方法处理下一个事件，否则会造成死循环。
				eventType = p.next();
			}
		} catch (Exception e) {
			Log.e(TAG, "抽奖机解析XML文件出错！XML文件格式有误,或者文件保存格式有误！");
			synchronized (itemList) {
				itemList.clear();
			}
			isParseSuccess = false;
		}
		return isParseSuccess;
	}
	
	/**
	 * @Title: requestLotteryNum
	 * @Description: 解析请求获取抽奖次数的协议
	 * @param userID
	 *            用户ID
	 * @param gameID
	 *            游戏ID
	 * @version:
	 * @param handler
	 */
	public void requestLotteryNum(final Handler handler) {
		int userId = FiexedViewHelper.getInstance().getUserId();
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream();
		tdos.setFront(false);
		tdos.writeInt(userId);
		tdos.writeShort(AppConfig.gameId);

		NetSocketPak requestGoods = new NetSocketPak(MDM_LOTTERY,
				LSUB_CMD_GET_LOT_TIMES_REQ, tdos);

		short[][] parseProtocol = { { MDM_LOTTERY, LSUB_CMD_GET_LOT_TIMES_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {

			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				byte code = tdis.readByte();
				Message msg= handler.obtainMessage(HANDLER_TIMES_REP);
				if (code == 0) {
					short logonLotteryNo = tdis.readShort();// 登录送抽奖次数
					short taskLotteryNo = tdis.readShort();// 任务送抽奖次数
					short buyLotteryNo = tdis.readShort();// 购买抽奖次数
					short otherLotteryNo = tdis.readShort();// 其他渠道抽奖次数
					int leftLoNo = (short) (logonLotteryNo + taskLotteryNo + buyLotteryNo + otherLotteryNo);
					msg.arg1 = leftLoNo;
				} else {
					msg.arg1 = -1;
				}
				handler.sendMessage(msg);
				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);

		// 发送协议
		NetSocketManager.getInstance().sendData(requestGoods);
		// 清理协议对象
		requestGoods.free();
	}
	
	
	/**一键抽奖未使用，用下面的*/
	/**
	 * @Title: requestLotteryConfig
	 * @Description: 解析请求一键抽奖配置协议
	 */
//	public void requestMultiLotteryConfig() {
//		isMultiConfigFinish = false;
//		int userId = FiexedViewHelper.getInstance().getUserId();
//		short gameId = (short) AppConfig.gameId;
//
//		final String multiLoVersion = Util.getStringSharedPreferences(AppConfig.mContext,
//				MULTI_LOTTERY_VERSION, "");
//
//		short channelID = 0;
//		short childChannelID = 0;
//		if (!Util.isEmptyStr(AppConfig.channelId)) {
//			channelID = Short.parseShort(AppConfig.channelId);
//		}
//		if (!Util.isEmptyStr(AppConfig.childChannelId)) {
//			childChannelID = Short.parseShort(AppConfig.childChannelId);
//		}
//
//		int mobileVersion = Util.getProtocolCode(AppConfig.ZONE_VER);
//		// 创建发送的数据包
//		TDataOutputStream tdos = new TDataOutputStream();
//		tdos.setFront(false);
//		tdos.writeInt(userId);
//		tdos.writeShort(gameId);
//		tdos.writeShort(channelID);
//		tdos.writeShort(childChannelID);
//		tdos.writeInt(mobileVersion);
//		tdos.writeUTF(multiLoVersion, 32);// 32字节
//		NetSocketPak requestGoods = new NetSocketPak(MDM_LOTTERY,
//				LSUB_CMD_MULTI_LOT_CONFIG_REQ, tdos);
//		short[][] parseProtocol = { { MDM_LOTTERY,
//				LSUB_CMD_MULTI_LOT_CONFIG_RESP } };
//		// 创建协议解析器
//		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
//
//			@Override
//			public boolean doReceive(NetSocketPak netSocketPak) {
//
//				TDataInputStream tdis = netSocketPak.getDataInputStream();
//				setMultiLotteryConfig(tdis);
//				return true;
//			}
//		};
//		// 注册协议解析器到网络数据分发器中
//		NetSocketManager.getInstance().addPrivateListener(nPListener);
//
//		// 发送协议
//		NetSocketManager.getInstance().sendData(requestGoods);
//		// 清理协议对象
//		requestGoods.free();
//	}
	
	/**
	 * @Title: requestLotteryConfig
	 * @Description: 解析请求一键抽奖配置协议
	 */
	public void requestLotteryConfig(final Handler handler) {
		isMultiConfigFinish = false;
		int userId = FiexedViewHelper.getInstance().getUserId();
		short gameId = (short) AppConfig.gameId;

		final String multiLoVersion = Util.getStringSharedPreferences(AppConfig.mContext,
				MULTI_LOTTERY_VERSION, "");

		short channelID = 0;
		short childChannelID = 0;
		if (!Util.isEmptyStr(AppConfig.channelId)) {
			channelID = Short.parseShort(AppConfig.channelId);
		}
		if (!Util.isEmptyStr(AppConfig.childChannelId)) {
			childChannelID = Short.parseShort(AppConfig.childChannelId);
		}

//		int mobileVersion = Util.getProtocolCode(AppConfig.ZONE_VER);
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream();
		tdos.setFront(false);
		tdos.writeInt(userId);
		tdos.writeShort(gameId);
		tdos.writeShort(channelID);
		tdos.writeShort(childChannelID);
//		tdos.writeInt(mobileVersion);  //一键抽奖新加的
		tdos.writeUTF(multiLoVersion, 32);// 32字节
		NetSocketPak requestGoods = new NetSocketPak(MDM_LOTTERY,
				LSUB_CMD_LOT_CONFIG_REQ, tdos);
		short[][] parseProtocol = { { MDM_LOTTERY,
			LSUB_CMD_LOT_CONFIG_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {

			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {

				TDataInputStream tdis = netSocketPak.getDataInputStream();
				setMultiLotteryConfig(tdis,handler);
				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);

		// 发送协议
		NetSocketManager.getInstance().sendData(requestGoods);
		// 清理协议对象
		requestGoods.free();
	}
	
	/**
	 * @Title: requestLotteryResult
	 * @param drowType
	 *            抽奖类型 0：使用乐豆抽奖 1：使用抽奖次数抽奖
	 * @Description: 解析请求获取抽奖结果的协议
	 */
	public void requestLotteryResult(int drowType, final Handler handler) {
		int userId = FiexedViewHelper.getInstance().getUserId();
		int gameId = AppConfig.gameId;
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream();
		tdos.setFront(false);
		tdos.writeInt(userId);
		tdos.writeShort(gameId);
		tdos.writeByte(drowType);

		NetSocketPak requestGoods = new NetSocketPak(MDM_LOTTERY,
				LSUB_CMD_LOTTERY_REQ, tdos);

		short[][] parseProtocol = { { MDM_LOTTERY, LSUB_CMD_LOTTERY_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {

			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {

				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				byte code = tdis.readByte();
				ResultData data = new ResultData();
				data.code = code;
				if(code == 0){
				data.index = tdis.readByte();
				data.propId = tdis.readInt();
				data.leftTimes = tdis.readShort();
				data.desc = tdis.readUTFShort();
				}else if(code == 99){
					data.desc = tdis.readUTFShort();
				}
				handler.obtainMessage(HANDLER_LOTTO_REP, data).sendToTarget();
				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);

		// 发送协议
		NetSocketManager.getInstance().sendData(requestGoods);
		// 清理协议对象
		requestGoods.free();
	}
	
	class ResultData{
		byte code;
		byte index;
		int propId;
		short leftTimes;
		String desc;
	}
}