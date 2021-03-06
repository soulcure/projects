package com.mykj.andr.pay.provider;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.AddGiftItem;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.game.FiexedViewHelper;

/**
 * 加赠礼包的相关协议，分首充送的礼包和推广送的礼包
 * @author JiangYinZhi
 *
 */
public class AddGiftProvider {

	private static AddGiftProvider mInstance;
	
	public int type = -1;//0为首充，1为推广
	public String desc;//加赠的描述
	public int adviceGoodID;//推荐商品id
	
	public ArrayList<AddGiftItem> addGiftItems;//推广商品相关数据，首充完后会被替换
	
	public static AddGiftProvider getInstance(){
		if(mInstance == null){
			mInstance = new AddGiftProvider();
		}
		return mInstance;
	}
	
	private static final short LS_MDM_PROP = 17;// 主协议
	private static final short CMD_GOODS_BUY_RATIO_RESP = 868;
	
	private static final int REQ_ADDGIFT_LIST_SUCCESS = 9009;

	/**
	 * 请求商品推广协议
	 */
	public void reqAddGiftList() {
		short[][] parseProtocol = { { LS_MDM_PROP, CMD_GOODS_BUY_RATIO_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				//释放礼包数据缓存
				type = -1;
				addGiftItems = null;
				addGiftItems = new ArrayList<AddGiftItem>();
				type = tdis.readByte();
				adviceGoodID = tdis.readInt();
				desc = tdis.readUTFShort();
				int count = tdis.readByte();
				for(int i=0;i<count;i++){
					final short len = tdis.readShort();
					MDataMark mark = tdis.markData(len);
					AddGiftItem addGiftItem = new AddGiftItem();
					addGiftItem.iconName = tdis.readUTFByte();
					addGiftItem.name = tdis.readUTFByte();
					addGiftItem.count = tdis.readInt();
					addGiftItems.add(addGiftItem);
					tdis.unMark(mark);
				}
				// 数据处理完成，终止继续解析
				mHandler.sendEmptyMessage(REQ_ADDGIFT_LIST_SUCCESS);
				return true;
			}
		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}
	
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQ_ADDGIFT_LIST_SUCCESS:
				if (FiexedViewHelper.getInstance().cardZoneFragment != null) {
					// 切换首充和特惠礼包的按钮
					FiexedViewHelper.getInstance().cardZoneFragment
							.setChargeKind(PayUtils.getPromptionType());
				}
				break;

			default:
				break;
			}
		}
	};
}
