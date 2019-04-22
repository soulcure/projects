package com.mykj.andr.pay.model;

import com.mykj.andr.model.GoodsItem;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.game.utils.Log;

/**
 * 支付方式,由服务端控制
 * 
 * @author JiangYinZhi
 * 
 */
public class PayWay {

	public int payType;//支付的signtype
	public String payName;//支付的名称
	public String payIconName;
	public byte goodsNum;

	// public ArrayList<GoodsItem> items;// 商品id的组合

	public PayWay(TDataInputStream tdis) {
		if (tdis == null) {
			return;
		}
		final short len = tdis.readShort();
		MDataMark mark = tdis.markData(len);
		// items = new ArrayList<GoodsItem>();
		payType = tdis.readInt();
		payName = tdis.readUTFShort();
		payIconName = tdis.readUTFShort();
		goodsNum = tdis.readByte();
		for (int i = 0; i < goodsNum; i++) {
			final short goodsDataLen = tdis.readShort();
			int curPos = tdis.getPos();
			int goodsId = tdis.readInt();
			GoodsItem goodItem = GoodsItemProvider.getInstance().findGoodsItemById(
					goodsId);
			if (goodItem != null) {
				goodItem.addPayWay(this);
			}
			if (tdis.readBoolean() && goodItem != null) {
				goodItem.addFastBuyPayWay(this);
			}
			byte subIcon = tdis.readByte();
			if (subIcon == 2) {
				int ritio = tdis.readShort();
				goodItem.addSubScriptWithPayWay(new SubScript(this, subIcon,
						ritio));
			} else if (subIcon == 1 || subIcon == 0) {
				tdis.readShort();
				goodItem.addSubScriptWithPayWay(new SubScript(this, subIcon, 0));
			}
			if (goodItem != null) {
				Log.e("PAYWAYLIST", "signType:" + payType + " shopID:"
						+ goodItem.shopID + goodItem.goodsName);
			} else {
				Log.e("PAYWAYLIST", "signType:" + payType + "ShopId:" + goodsId);
			}
			tdis.skip(goodsDataLen - (tdis.getPos() - curPos));
		}
		tdis.unMark(mark);
	}
	
	public PayWay(int payType){
		this.payType = payType;
	}
}
