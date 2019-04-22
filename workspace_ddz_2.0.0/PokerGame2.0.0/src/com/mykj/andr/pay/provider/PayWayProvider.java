package com.mykj.andr.pay.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.pay.model.PayWay;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.UtilHelper;

public class PayWayProvider {

	private static PayWayProvider mInstance;
	private static Context mContext;

	private static final short LS_MDM_PROP = 17;

	/** 当前返回数 **/
	private int curBackpackNum = 0;

	/** 支付方式 */
	private ArrayList<PayWay> payWaysAL;

	private boolean isREQFinish = false;

	private static final short LSUB_CMD_PAYWAY_REQ = 855;
	private static final short LSUB_CMD_PAYWAY_RESP = 856;

	private static final short GET_PAY_WAY_FAIL = 522;
	private static final short GET_PAY_WAY_SUCCESS = 523;

	private PayWayProvider() {
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static PayWayProvider getInstance(Context mContext) {
		if (mInstance == null) {
			mInstance = new PayWayProvider();
		}
		PayWayProvider.mContext = mContext;
		return mInstance;
	}

	public List<PayWay> getPayWayList() {
		return payWaysAL;
	}

	public boolean isPayWayREQFinish() {
		return isREQFinish;
	}

	// public ArrayList<PayWay> get

	public void reqPayWayList() {
		if (payWaysAL == null) {
			payWaysAL = new ArrayList<PayWay>();
		} else {
			payWaysAL.clear();
		}
		// 创建发送的数据包
		int mobileCardType = UtilHelper.getMobileCardType(mContext);
		curBackpackNum = 0;
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeByte(mobileCardType);
		tdos.writeUTFShort(AppConfig.PAY_WAY_VERSION);
		NetSocketPak pointBalance = new NetSocketPak(LS_MDM_PROP,
				LSUB_CMD_PAYWAY_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { LS_MDM_PROP, LSUB_CMD_PAYWAY_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				short total = tdis.readShort(); // 总共有多少条记
				short count = tdis.readShort(); // 当前数据包中包含个数
				if (count <= 0) {
					mPayWayHandler.sendEmptyMessage(GET_PAY_WAY_FAIL);
				} else {
					// 累计接受到数据到数组中
					for (int i = 0; i < count; i++) {
						payWaysAL.add(new PayWay(tdis));
					}
					curBackpackNum += count; // 积累保存到全局变量，记录当前返回累计数目
					if (curBackpackNum == total) {
						mPayWayHandler.sendEmptyMessage(GET_PAY_WAY_SUCCESS);
					}
				}

				// 数据处理完成，终止继续解析
				return true;
			}
		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	Handler mPayWayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_PAY_WAY_FAIL:
				isREQFinish = false;
				break;

			case GET_PAY_WAY_SUCCESS:
				isREQFinish = true;
				break;

			default:
				break;
			}
		}
	};
}
