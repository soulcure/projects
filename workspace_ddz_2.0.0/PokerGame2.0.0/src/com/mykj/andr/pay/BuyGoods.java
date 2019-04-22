package com.mykj.andr.pay;

import com.mykj.andr.model.HPropData;

public class BuyGoods {
	public int userId;// 用户ID
	public int propId; // 道具ID,商品ID
	public String goodsInfo; // 具体描述信息
	public long cliSec;// cliSec客户端标识
	public int propCount; //道具的数量
	public HPropData propData[]; // 道具数据数组
}
