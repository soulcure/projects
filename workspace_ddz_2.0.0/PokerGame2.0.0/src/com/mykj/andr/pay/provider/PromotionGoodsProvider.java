package com.mykj.andr.pay.provider;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.PromotionGoodsItem;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.game.FiexedViewHelper;

/**
 * 商品推广的服务类，包括首充和推广
 * 
 * @author JiangYinZhi
 * 
 */
public class PromotionGoodsProvider {

	private static PromotionGoodsProvider mInstance;

	public int type = -1;// 0为首充，1为推广
	public int lowBeanShow = -1;// 用于退出游戏时是否要弹最低乐豆显示
	public ArrayList<PromotionGoodsItem> promotionGoods;// 推广的商品的集合

	private PromotionGoodsProvider() {
	};

	public static PromotionGoodsProvider getInstance() {
		if (mInstance == null) {
			mInstance = new PromotionGoodsProvider();
		}
		return mInstance;
	}

	private static final short LS_MDM_PROP = 17;// 主协议
	private static final short CMD_FIRST_BUY_RATIO_RESP = 867;

	private static final int REQ_PROMPTION_LIST_SUCCESS = 9009;

	/**
	 * 请求首充商品列表
	 */
	public void reqPromotionGoodsList() {
		short[][] parseProtocol = { { LS_MDM_PROP, CMD_FIRST_BUY_RATIO_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				// 清空缓存，可能为推广将首充覆盖
				type = -1;
				promotionGoods = null;
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				type = tdis.readByte();
				short goodsCount = tdis.readShort(); // 首充包含的商品数量
				if (goodsCount <= 0) {
					return false;
				} else {
					promotionGoods = new ArrayList<PromotionGoodsItem>();
				}
				for (int i = 0; i < goodsCount; i++) {
					final short len = tdis.readShort();
					MDataMark mark = tdis.markData(len);
					PromotionGoodsItem item = new PromotionGoodsItem();
					item.goodsID = tdis.readInt();
					item.oldCoinNum = tdis.readInt();
					item.currentCoinNum = tdis.readInt();
					Log.d("FirstChargeDatas", "len:" + len + " goodsID:"
							+ item.goodsID + " oldCoinNum:" + item.oldCoinNum
							+ " currentCoinNum:");
					promotionGoods.add(item);
					tdis.unMark(mark);
				}
				lowBeanShow = tdis.readInt();
				// 数据处理完成，终止继续解析
				mHandler.sendEmptyMessage(REQ_PROMPTION_LIST_SUCCESS);
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
			case REQ_PROMPTION_LIST_SUCCESS:
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

	/**
	 * 根据商品id获取相对于的推广相关信息
	 * 
	 * @param goodsID
	 * @return
	 */
	public PromotionGoodsItem getPromotionGoodItem(int goodsID) {
		if (promotionGoods == null || promotionGoods.size() == 0) {
			return null;
		} else {
			for (int i = 0; i < promotionGoods.size(); i++) {
				if (promotionGoods.get(i).goodsID == goodsID) {
					return promotionGoods.get(i);
				}
			}
		}
		return null;
	}

}
