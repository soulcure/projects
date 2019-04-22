package com.mykj.andr.provider;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.model.VipPrivilegeInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.ui.fragment.CardZoneFragment;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.utils.Log;

public class VipPrivilegeProvider {

	/**
	 * 登录成功后只请求一次，之后的由变化协议下发
	 */
	private VipPrivilegeInfo mVipPrivilegeInfo;

	private OnReceiverListener listener;
	private OnReceiverListener marketBeanListener;
	private OnReceiverListener marketPropListener;

	// 主协议
	private static final short MDM_USER = 2;
	// 请求VIP信息：
	private static final short SUB_CMD_VIP_INFO_REQ = 200;
	// VIP信息返回：
	private static final short SUB_CMD_VIP_INFO_RESP = 201;
	// 道具主协议
	private static final short MDM_PROP = 17;
	// vip变化监听子协议
	private static final short CMD_USER_VIP_INFO = 859;

	private boolean isFinish = false;
	/**
	 * 需要考虑分包 请求vip数据
	 */
//	public void reqVipData() {
//		clear();
//		TDataOutputStream tdos = new TDataOutputStream(false);
//		tdos.writeInt(FiexedViewHelper.getInstance().getUserId());
//		NetSocketPak nsp = new NetSocketPak(MDM_USER, SUB_CMD_VIP_INFO_REQ,
//				tdos);
//		short[][] parseProtocol = { { MDM_USER, SUB_CMD_VIP_INFO_RESP } };
//		NetPrivateListener npl = new NetPrivateListener(parseProtocol) {
//
//			@Override
//			public boolean doReceive(NetSocketPak netSocketPak) {
//				TDataInputStream tdis = netSocketPak.getDataInputStream();
//				tdis.setFront(false);
//				if (mVipPrivilegeInfo == null) {
//					mVipPrivilegeInfo = new VipPrivilegeInfo();
//				}
//				
//				// 当前成长值
//				mVipPrivilegeInfo.currentGrowthValue = tdis.readInt();
//				// 升到下一等级所需成长值
//				mVipPrivilegeInfo.nextLevelGrowthValue = tdis.readInt();
//				// VIP到期时间，非有效vip此值为0
//				mVipPrivilegeInfo.expirationTime = tdis.readLong();
//				// 帮助url0
//				mVipPrivilegeInfo.helpUrl = tdis.readUTFShort();
//				// 资源url
//				mVipPrivilegeInfo.resUrl = tdis.readUTFShort();
//				// VIP等级总个数 、 这里和下面的count对应，分包获取
//				vipInfoTotalCount = tdis.readByteUnsigned();
//				// 当前VIP等级个数
//				int vipInfoCount = tdis.readByteUnsigned();
//				for (int i = 0; i < vipInfoCount; i++) {
//					// VIP等级
//					int vipLevel = tdis.readByteUnsigned();
//					// VIP特权名称
//					String name = tdis.readUTFByte();
//					// VIP特权内容，以\n换行
//					String desc = tdis.readUTFShort();
//
//					VipInfo vipInfo = new VipInfo();
//					vipInfo.vipLevel = vipLevel;
//					vipInfo.name = name;
//					vipInfo.desc = desc;
//					mVipPrivilegeInfo.vipInfoList.add(vipInfo);
//				}
//
//				if (vipInfoTotalCount == mVipPrivilegeInfo.vipInfoList.size()) {
//					// 分包完成
//					if (listener != null) {
//						listener.onReceiver(mVipPrivilegeInfo);
//					}
//				}
//
//				return true;
//			}
//		};
//		npl.setOnlyRun(false);
//		NetSocketManager.getInstance().addPrivateListener(npl);
//		NetSocketManager.getInstance().sendData(nsp);
//		nsp.free();
//	}

	/**
	 * 注册vip监听协议
	 */
	public void registerVipListener() {
		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_PROP, CMD_USER_VIP_INFO } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				try {
					TDataInputStream tdis = netSocketPak.getDataInputStream();
					tdis.setFront(false);

					short level = tdis.readShort(); // 当前等级，于vip相关
					int nickColor = tdis.readInt();
					int expirationTime = tdis.readInt();
//					int nickColor = tdis.readInt();
//					int currentGrowthValue = tdis.readInt();
//					int nextLevelGrowthValue = tdis.readInt();

					UserInfo user = HallDataManager.getInstance().getUserMe();
					user.memberOrder = (byte) level;
					user.nickColor = nickColor;
//					user.memberOrder = (byte) level;
//					if (expirationTime == 0) {
//						//vip过期
//						user.isVipExpired = 0;
//					}else{
//						user.isVipExpired = 1;
//					}
//					user.nickColor = nickColor;
					HallDataManager.getInstance().setUserMe(user);
					if(mVipPrivilegeInfo!= null){
						mVipPrivilegeInfo.setExpTime(expirationTime);
					}
//					if (mVipPrivilegeInfo != null) {
//						mVipPrivilegeInfo.currentGrowthValue = currentGrowthValue;
//						mVipPrivilegeInfo.nextLevelGrowthValue = nextLevelGrowthValue;
//						mVipPrivilegeInfo.expirationTime = expirationTime;
//					}

					// 更新大厅vip等级
					if (FiexedViewHelper.getInstance().cardZoneFragment != null) {
						FiexedViewHelper.getInstance().cardZoneFragment.cardZoneHandler
								.sendEmptyMessage(CardZoneFragment.HANDLER_UPDATE_VIP);
					}
					if (listener != null) {
						listener.onReceiver();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 数据处理完成，终止继续解析
				return true;
			}
		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}

	/**
	 * 清理数据，为了切换账号准备
	 */
	public void clear() {
		isFinish = false;
		this.mVipPrivilegeInfo = null;
	}
	
	public void setOnReceiverListener(OnReceiverListener listener) {
		this.listener = listener;
	}
	
	public void setOnMarketBeanReceiverListener(OnReceiverListener listener){
		this.marketBeanListener = listener;
	}
	
	public void setOnMarketPropReceiverListener(OnReceiverListener listener){
		this.marketPropListener = listener;
	}

	public void clearOnReceiverListener(OnReceiverListener listener) {
		if(this.listener != null && listener != null 
				&& this.listener.hashCode() == listener.hashCode()){
			this.listener = null;
		}
	}
	
	public void clearOnMarketBeanReceiverListener(OnReceiverListener listener){
		if(this.marketBeanListener != null && listener != null 
				&& this.marketBeanListener.hashCode() == listener.hashCode()){
			this.marketBeanListener = null;
		}		
	}
	
	public void clearOnMarketPropReceiverListener(OnReceiverListener listener){
		if(this.marketPropListener != null && listener != null 
				&& this.marketPropListener.hashCode() == listener.hashCode()){
			this.marketPropListener = null;
		}		
	}
	
	/**
	 * 是否接收完成, 1、没有完成则注册监听 2、完成了直接获取vip数据
	 * 
	 * @return
	 */
	public boolean isFinish() {
		return isFinish;
	}

	public VipPrivilegeInfo getVipPrivilegeInfo() {
		if(mVipPrivilegeInfo==null){
			mVipPrivilegeInfo = new VipPrivilegeInfo();
		}
		return mVipPrivilegeInfo;
	}

	public interface OnReceiverListener {
		public void onReceiver();
	}

    public void reqVipData(){
    	clear();
    	TDataOutputStream tdos = new TDataOutputStream(false);
		NetSocketPak nsp = new NetSocketPak(MDM_USER, SUB_CMD_VIP_INFO_REQ,
				tdos);
		short[][] parseProtocol = { { MDM_USER, SUB_CMD_VIP_INFO_RESP } };
		NetPrivateListener npl = new NetPrivateListener(parseProtocol) {

			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				if (mVipPrivilegeInfo == null) {
					mVipPrivilegeInfo = new VipPrivilegeInfo();
				}
				mVipPrivilegeInfo.init(tdis);
				isFinish = true;
				if (listener != null) {
					listener.onReceiver();
				}
				if(marketBeanListener != null){
					marketBeanListener.onReceiver();
				}
				if(marketPropListener != null){
					marketPropListener.onReceiver();
				}
				return true;
			}
		};
		npl.setOnlyRun(false);
		NetSocketManager.getInstance().addPrivateListener(npl);
		NetSocketManager.getInstance().sendData(nsp);
		nsp.free();
    }
	
	
	// =====================静态内部类单例模式===============================
	private static class SingleTon {
		private static final VipPrivilegeProvider INSTANCE = new VipPrivilegeProvider();
	}

	private VipPrivilegeProvider() {
	}

	public static VipPrivilegeProvider getInstance() {
		return SingleTon.INSTANCE;
	}
}
