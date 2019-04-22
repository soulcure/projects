package com.mykj.andr.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.AchieveInfo;
import com.mykj.andr.model.BadgeInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;

/**
 * 徽章成就 内容提供
 * 
 * @author Administrator
 * 
 */
public class AchieveProvider {
	/** 获取用户徽章列表成功 */
	public static final int UIHANDLER_GET_BADGELIST_SUCCESS = 1;
	/** 获取用户徽章列表失败 或者为空 */
	public static final int UIHANDLER_GET_BADGELIST_FAILED = 2;

	public static final int UIHANDLER_GET_AWARD_SUCCESS = 3;

	public static final int UIHANDLER_GET_AWARD_FAILED = 4;

	public static final int UIHANDLER_PUTON_BADGE_SUCCESS = 5;
	public static final int UIHANDLER_PUTON_BADGE_FAILED = 6;
	
	public static final int UIHANDLER_PUTDOWN_BADGE_SUCCESS = 7;
	public static final int UIHANDLER_PUTDOWN_BADGE_FAILED = 8;

	private static final short MDM_ACHIEVE = 120;
	/** 请求用户成就信息： */
	private static final short MSUB_CMD_ACHIEVE_USERINFO_REQ = 5;
	/** 请求用户成就信息返回 */
	private static final short MSUB_CMD_ACHIEVE_USERINFO_RESP = 6;
	/** 徽章领取请求协议 */
	private static final short MSUB_CMD_BADGE_AWARD_REQ = 7;
	/** 徽章领奖返回子协议 */
	private static final short MSUB_CMD_BADGE_AWARD_RESP = 8;
	/** 徽章佩戴请求子协议 */
	private static final short MSUB_CMD_BADGE_WEAR_REQ = 9;
	/** 徽章佩戴返回子协议 */
	private static final short MSUB_CMD_BADGE_WEAR_RESP = 10;

	private OnAchieveListener listener;

	/** 所有基础成就信息 */
	private Map<Integer, AchieveInfo> mBaseAchieveInfo;
	/** 所有基础徽章信息 */
	private Map<Integer, BadgeInfo> mBaseBadgeInfo;

	/** 徽章总数 */
	private int badgeTotal;
	/** 成就总数 */
	private int achieveTotal;

	public AchieveInfo findAchieveInfoById(int id) {
		if (isFinished()) {
			return mBaseAchieveInfo.get(id);
		}
		return null;
	}

	public BadgeInfo findBadgeInfoById(int id) {
		if (isFinished()) {
			return mBaseBadgeInfo.get(id);
		}
		return null;
	}

	/**
	 * 数据是否接收完成 true 接受完成 fakse 接受未完成
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return mBaseAchieveInfo != null && mBaseBadgeInfo != null
				&& badgeTotal == mBaseBadgeInfo.size()
				&& achieveTotal == mBaseAchieveInfo.size();
	}

	public Map<Integer, AchieveInfo> getmBaseAchieveInfo() {
		return mBaseAchieveInfo;
	}

	/**
	 * 这里要考虑到分包接收数据,设置 或者加入到成就基本信息中
	 * 
	 * @param total
	 * @param mBaseAchieveInfo
	 */
	public void setmBaseAchieveInfo(int total,
			Map<Integer, AchieveInfo> mBaseAchieveInfo) {
		if (this.mBaseAchieveInfo == null) {
			this.mBaseAchieveInfo = mBaseAchieveInfo;
		} else {
			this.mBaseAchieveInfo.putAll(mBaseAchieveInfo);
		}
		this.achieveTotal = total;
		if (isFinished() && listener != null) {
			listener.onFinish();
		}
	}

	/**
	 * 这里要考虑到分包接收数据,设置 或者加入到徽章基本信息中
	 * 
	 * @param mBaseBadgeInfo
	 */
	public void setmBaseBadgeInfo(int total,
			Map<Integer, BadgeInfo> mBaseBadgeInfo) {
		if (this.mBaseBadgeInfo == null) {
			this.mBaseBadgeInfo = mBaseBadgeInfo;
		} else {
			this.mBaseBadgeInfo.putAll(mBaseBadgeInfo);
		}

		this.badgeTotal = total;
		if (isFinished() && listener != null) {
			listener.onFinish();
		}
	}

	/**
	 * 请求用户成就信息
	 * */
	public void reqMyAchieveInfo(int userId, final Handler handler) {
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeLong(userId);
		// 请求模式 --0：简略（仅徽章信息）--为游戏内部新增的参数
		// 1：详细（包含成就信息） -- 为分区的请求参数
		tdos.writeByte(1);
		NetSocketPak pointBalance = new NetSocketPak(MDM_ACHIEVE,
				MSUB_CMD_ACHIEVE_USERINFO_REQ, tdos);

		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_ACHIEVE,
				MSUB_CMD_ACHIEVE_USERINFO_RESP } };

		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				// datasize包含用户徽章信息的长度和用户成就信息
				long userId = tdis.readLong();
				short count = tdis.readShort();

				Message msg = handler.obtainMessage();
				msg.what = UIHANDLER_GET_BADGELIST_FAILED;
				List<BadgeInfo> badgeList = new ArrayList<BadgeInfo>();
				for (int i = 0; i < count; i++) {
					int badgeId = tdis.readInt();
					int ownerCount = tdis.readInt();
					int badgeStatus = tdis.readByteUnsigned();

					List<AchieveInfo> achieveList = new ArrayList<AchieveInfo>();
					int achieveCount = tdis.readByteUnsigned();
					for (int j = 0; j < achieveCount; j++) {
						int achieveId = tdis.readInt();
						int achieveStatus = tdis.readByteUnsigned();
						int achieveCurrentCount = tdis.readInt();
						if (mBaseAchieveInfo.containsKey(achieveId)) {
							AchieveInfo achieveInfo = mBaseAchieveInfo
									.get(achieveId);
							achieveInfo.status = achieveStatus;
							achieveInfo.currentCount = achieveCurrentCount;
							achieveList.add(achieveInfo);
						}
					}
//					if (achieveList.size() != achieveCount) {
//						Log.e("AchieveProvider", "请求用户成就信息 -获取的成就列表其中包含未知成就id");
//						msg.what = UIHANDLER_GET_BADGELIST_FAILED;
//						break;
//					} else if (mBaseBadgeInfo.containsKey(badgeId)) {
//						BadgeInfo badgeInfo = mBaseBadgeInfo.get(badgeId);
//						badgeInfo.ownerCount = ownerCount;
//						badgeInfo.status = badgeStatus;
//						badgeInfo.achieveList = achieveList;
//						badgeList.add(badgeInfo);
//						msg.what = UIHANDLER_GET_BADGELIST_SUCCESS;
//						msg.obj = badgeList;
//					} else {
//						Log.e("AchieveProvider", "请求用户成就信息 -获取的徽章列表其中包含未知徽章id");
//						msg.what = UIHANDLER_GET_BADGELIST_FAILED;
//						break;
//					}
					if (mBaseBadgeInfo.containsKey(badgeId)) {
						BadgeInfo badgeInfo = mBaseBadgeInfo.get(badgeId);
						badgeInfo.ownerCount = ownerCount;
						badgeInfo.status = badgeStatus;
						badgeInfo.achieveList = achieveList;
						badgeList.add(badgeInfo);
						msg.what = UIHANDLER_GET_BADGELIST_SUCCESS;
						msg.obj = badgeList;
					}
					

				}
				handler.sendMessage(msg);
				if(FiexedViewHelper.getInstance().inGame()){
					return false;
				}
				return true;
			}
		};
		nPListener.setOnlyRun(true);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	/**
	 * 用户领奖
	 * */
	public void receptAward(int badgeId, final int position,
			final Handler handler) {
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeInt(badgeId);
		NetSocketPak pointBalance = new NetSocketPak(MDM_ACHIEVE,
				MSUB_CMD_BADGE_AWARD_REQ, tdos);

		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_ACHIEVE, MSUB_CMD_BADGE_AWARD_RESP } };

		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				Message msg = handler.obtainMessage();
				msg.arg1 = position;
				int returnBadgeId = tdis.readInt();
				// 领奖结果：0成功，-1失败
				int result = tdis.readByteUnsigned();
				
				int newStatus = tdis.readByteUnsigned();
				
				int newOwnerCount = tdis.readInt();
				
				int errMsgLen = tdis.readByteUnsigned();
				String errMsg = tdis.readUTF(errMsgLen);

				ArrayList<String> giftDataList = new ArrayList<String>();
				int giftCount = tdis.readByteUnsigned();
				for (int i = 0; i < giftCount; i++) {
					int giftStrLen = tdis.readByteUnsigned();
					String giftStr = tdis.readUTF(giftStrLen);
					giftDataList.add(giftStr);
				}

				if (result == 0) {
					// 成功
					msg.what = UIHANDLER_GET_AWARD_SUCCESS;
				} else {
					msg.what = UIHANDLER_GET_AWARD_FAILED;
				}
				msg.obj = errMsg;
				Bundle b = new Bundle();
				b.putStringArrayList("gift_data_list", giftDataList);
				b.putInt("status", newStatus);
				b.putInt("owner_count", newOwnerCount);
				msg.setData(b);
				handler.sendMessage(msg);
				if(FiexedViewHelper.getInstance().inGame()){
					return false;
				}
				return true;
			}
		};
		nPListener.setOnlyRun(true);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	/**
	 * 佩戴徽章 badge = 0时，表示卸下 badge不为0，表示佩戴badgeId的徽章
	 * */
	public void putOnBadge(final int badgeId, final int position,
			final Handler handler) {
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeInt(badgeId);
		NetSocketPak pointBalance = new NetSocketPak(MDM_ACHIEVE,
				MSUB_CMD_BADGE_WEAR_REQ, tdos);

		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_ACHIEVE, MSUB_CMD_BADGE_WEAR_RESP } };

		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				Message msg = handler.obtainMessage();
				msg.arg1 = position;
				int returnBadgeId = tdis.readInt();
				int result = tdis.readByteUnsigned();
				int errMsgLen = tdis.readByteUnsigned();
				String errMsg = tdis.readUTF(errMsgLen);

				if (badgeId != 0) {
					if (result == 0) {
						// 成功
						msg.what = UIHANDLER_PUTON_BADGE_SUCCESS;
					} else {
						msg.what = UIHANDLER_PUTON_BADGE_FAILED;
					}
				}else{
					if (result == 0) {
						// 成功
						msg.what = UIHANDLER_PUTDOWN_BADGE_SUCCESS;
					} else {
						msg.what = UIHANDLER_PUTDOWN_BADGE_FAILED;
					}
				}
				msg.obj = errMsg;

				handler.sendMessage(msg);
				if(FiexedViewHelper.getInstance().inGame()){
					return false;
				}
				return true;
			}
		};
		nPListener.setOnlyRun(true);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	private static AchieveProvider instance;

	private AchieveProvider() {
	}

	public static AchieveProvider getInstance() {
		if (instance == null) {
			instance = new AchieveProvider();
		}
		return instance;
	}

	/**
	 * 注册回调
	 * 
	 * @param onAchieveListener
	 */
	public void register(OnAchieveListener onAchieveListener) {
		this.listener = onAchieveListener;
	}

	/**
	 * 注销回调
	 */
	public void unregister() {
		this.listener = null;
	}

	/**
	 * 成就接收完成监听
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnAchieveListener {
		public void onFinish();
	}

}
