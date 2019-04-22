package com.mingyou.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.community.AddressStrategyImpl;
import com.mingyou.community.Community;
import com.mingyou.distributor.NetPackConstants;
import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.minyou.android.net.TcpConnector;
import com.multilanguage.MultilanguageManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.comm.log.MLog;
import com.mykj.comm.util.AndrUtil;
import com.mykj.comm.util.NetDataConnectionState;

import debug.IP_CONFIG_FILE;

/**
 * 专注断线重连处理
 * 
 * @author Administrator
 * 
 */
public class RecoverForDisconnect {
	private final String TAG = "RecoverForDisconnect";

	/** 完整重连次数 **/
	public int RE_COUNT = 0;

	/** 最大完整重连次数 **/
	public final int RE_MAX_COUNT = 5;

	private int _roomID = 0;

	public static boolean isSendBeginReconnect = false;

	AddressStrategyImpl addressReConnect = null;

	// 快速断线重回协议
	/** 4.7.1.房间主协议 **/
	static final short MDM_ROOM = 14;

	/** 快速断线重回：请求 **/
	static final short MSUB_CMD_CUT_REUTRN_ROOM_REQ = 15;

	/** 快速断线重回结果：返回 **/
	static final short MSUB_CMD_CUT_RESUTNR_ROOM_RESP = 16;

	// 网络建立的重连次数
	private static byte RE_CONNTION_COUNT = 3;

	private Map<Integer, List<NetPrivateListener>> reCutSendData;

	private Map<Integer, List<NetSocketPak>> reCutSendDataNSP;

	private static RecoverForDisconnect _instance = null;

	// 初始步骤
	public static int STEP_INI = 0;

	// 关闭网络
	private static int STEP_CLOSENET = 0x0005;

	// 检测网络
	public static int STEP_CHECKNET = 0x0010;

	// 发起连接
	public static int STEP_CONNECT = 0x0020;

	// 发送断线协议
	public static int STEP_SENDPROTOCOL = 0x0030;

	private int curStep = STEP_INI;//

	public static RecoverForDisconnect getInstance() {
		if (_instance == null) {
			_instance = new RecoverForDisconnect();
		}
		return _instance;
	}

	private RecoverForDisconnect() {
		reCutSendData = new HashMap<Integer, List<NetPrivateListener>>();
		reCutSendDataNSP = new HashMap<Integer, List<NetSocketPak>>();
	}

	public void AddCutResendData(NetPrivateListener listener) {
		if (listener.getReCutFlag() == NetPackConstants.INVALIDRECUTFLAG)
			return;
		List<NetPrivateListener> reCutSendDataList = reCutSendData.get(listener.getReCutFlag());
		if (reCutSendDataList == null) {
			reCutSendDataList = new ArrayList<NetPrivateListener>();
		}
		reCutSendDataList.add(listener);
		reCutSendData.put(listener.getReCutFlag(), reCutSendDataList);
	}

	public void AddCutResendData(NetSocketPak nsp) {
		if (nsp.getReCutFlag() == NetPackConstants.INVALIDRECUTFLAG)
			return;
		List<NetSocketPak> reCutSendDataListNSP = reCutSendDataNSP.get(nsp.getReCutFlag());
		if (reCutSendDataListNSP == null) {
			reCutSendDataListNSP = new ArrayList<NetSocketPak>();
		}
		reCutSendDataListNSP.add(nsp);
		reCutSendDataNSP.put(nsp.getReCutFlag(), reCutSendDataListNSP);
	}

	public void removeCutResendData(int reCutFlag) {
		reCutSendData.remove(reCutFlag);
		reCutSendDataNSP.remove(reCutFlag);
	}

	public void removeCutResendDataAll() {
		reCutSendData.clear();
		reCutSendDataNSP.clear();
	}

	/**
	 * 开始断线重连流程--供外部调用
	 * 
	 * @param context
	 * @param listener
	 * @param isGaming
	 */
	public void start(final Context context, final SocketLoginListener listener, final boolean isGaming, int roomid) {

		if (!isReConnect()) {
			Message mesg = new Message();
			mesg.obj = MultilanguageManager.getInstance().getValuesString(
					"disconnectTip1");
			listener.onFiled(mesg, STEP_INI);
			return;
		}
		MLog.e(TAG, "开始重连流程 RecoverForDisconnect start...");
		_roomID = roomid;
		Thread t = new Thread(new Runnable() {
			public void run() {
				curStep = STEP_INI;
				curStep = STEP_CLOSENET;
				LoginSocket.getInstance().loginAgainCloseNet(); // 重连，先关闭网络
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}

				curStep = STEP_CHECKNET;
				int times = 10; // 8次 共16秒
				try {
					int connectState = 0;
					boolean isAddTimes = true;
					while ((connectState = NetDataConnectionState.getInstance().getConnectState()) != TelephonyManager.DATA_CONNECTED
							&& !AndrUtil.isNetworkConnected(context)) {
						if (times <= 0) {
							if (connectState == TelephonyManager.DATA_CONNECTING && isAddTimes) {// 最后正在连接中，追加尝试次数
								isAddTimes = false;
								times = 5;
								MLog.d("reContinueGamePri", "手机正在连接中，继续增加5次等待");
							} else {
								Message mesg = new Message();
								mesg.obj = MultilanguageManager.getInstance()
										.getValuesString("disconnectTip2")
										+ NetDataConnectionState.getInstance()
												.getConnectState();
								listener.onFiled(mesg, curStep);
								return;
							}
						}
						times--;
						MLog.d("reContinueGamePri", "无网络，等待 times=" + times + ", connectState=" + connectState);
						Thread.sleep(2000);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				MLog.d("reContinueGamePri", "已休眠5秒，有网络，开始发起重连");
				// 重新建立连接
				if (addressReConnect == null) {
					addressReConnect = new AddressStrategyImpl(IPConfigManager.getInstacne());
				}
				TcpConnector.IConnectCallBack reConnectCallBack = new TcpConnector.IConnectCallBack() {
					public void connectSucceed() {
						MLog.d("reContinueGamePri", "重连时 网络建立成功");
						LoginSocket.getInstance().addNetTask();
						reqCUT_REUTRN_ROOM(isGaming, listener);
					}

					public void connectFailed(Exception e) {
						MLog.d("reContinueGamePri", "重连时 网络建立失败");
						Message mesg = new Message();
						mesg.obj = MultilanguageManager.getInstance()
								.getValuesString("disconnectTip3");
						listener.onFiled(mesg, curStep);
					}
				};
				// 建立连接
				curStep = STEP_CONNECT;
				LoginSocket.getInstance().openSocket(addressReConnect, reConnectCallBack, RE_CONNTION_COUNT);
			}
		});
		t.setName("RecoverForDisconnect");
		t.start();
	}

	/**
	 * 是否继续重连
	 * 
	 * @return
	 */
	public boolean isReConnect() {
		return IP_CONFIG_FILE.isReLogin() && RE_COUNT < RE_MAX_COUNT;
	}

	/**
	 * 请求断线重回快速进入游戏
	 * 
	 * @param lis
	 */
	private void reqCUT_REUTRN_ROOM(final boolean isGaming, final SocketLoginListener lis) {
		short[][] mdms = new short[][] { { MDM_ROOM, MSUB_CMD_CUT_RESUTNR_ROOM_RESP } };
		NetPrivateListener privateListener = new NetPrivateListener(mdms) {

			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				final byte result = tdis.readByte();
				final String msg = tdis.readUTFByte();
				MLog.e(TAG, "快速断线重回msg=" + msg + "/result=" + result);
				if (result == 0) { // 成功
					++RE_COUNT;
					if (lis != null) {
						Message mesg = new Message();
						mesg.obj = msg;

						// 重新发送断线前的协议
						MLog.e(TAG, "断线重连成功");

						// MLog.e(TAG, "断线重连待发数据量--size="+reCutSendData.size());
						// if(reCutSendData.size() > 0){
						// for(Map.Entry<Integer,List<NetPrivateListener>>
						// entry:reCutSendData.entrySet()){
						// Integer key=entry.getKey();
						// if(key == null)
						// continue;
						// MLog.e(TAG, "断线重连成功NPL key="+key);
						// List<NetPrivateListener> list=entry.getValue();
						// if(list == null)
						// continue;
						// for(NetPrivateListener np:list){
						// MLog.e(TAG, "断线重连成功NPL__M="+np.getMdms()+"___");
						// TcpShareder.getInstance().addTcpListener(np);
						// }
						// }
						// }
						if (!isGaming) {  //仅分区有效
							MLog.e(TAG, "断线重连待发数据量NSP——size=" + reCutSendDataNSP.size());
							if (reCutSendDataNSP.size() > 0) {
								for (Map.Entry<Integer, List<NetSocketPak>> entry : reCutSendDataNSP.entrySet()) {
									Integer key = entry.getKey();
									if (key == null)
										continue;
									MLog.e(TAG, "断线重连成功NSP key=" + key);
									List<NetSocketPak> list = entry.getValue();
									if (list == null)
										continue;
									for (NetSocketPak np : list) {
										MLog.e(TAG, "断线重连成功NSP__M=" + np.getMdm_gr() + "___S=" + np.getSub_gr());
										TcpShareder.getInstance().reqNetData(np);
									}
								}
							}
						}

						lis.onSuccessed(mesg);
					}
				} else {
					// 通知UI网络错误
					MLog.e("reqCUT_REUTRN_ROOM", "重连时 发协议返回失败，返回UI提示");
					if (lis != null) {
						Message mesg = new Message();
						mesg.obj = msg;
						lis.onFiled(mesg, curStep);
					}
				}
				return true;
			}
		};
		TcpShareder.getInstance().addTcpListener(privateListener);
		// 发送快速重回协议
		TDataOutputStream tos = new TDataOutputStream(false);
		tos.writeInt(Community.PlatID);// 平台标识
		tos.writeInt(isGaming ? _roomID : 0);// 房间编号
		tos.writeInt(Community.getInstacne().getGameID());// 游戏编号
		tos.writeInt(Community.getSelftUserInfo().userId);// 用户ID
		tos.writeUTFByte(LoginInfoManager.getInstance().getToken());// AT
		final int channel = Integer.parseInt(Community.getInstacne().getChannel());
		tos.writeInt(channel);// 渠道号
		tos.writeUTFByte(Community.getInstacne().getSubChannel()); // 子渠道号
		MLog.e(TAG, "发送快速断线重回消息");
		curStep = STEP_SENDPROTOCOL;
		TcpShareder.getInstance().reqNetData(new NetSocketPak(MDM_ROOM, MSUB_CMD_CUT_REUTRN_ROOM_REQ, tos));
	}
}
