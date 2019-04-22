package com.mykj.andr.pay.model;


/**
 * 标识脚标的类
 * @author JiangYinZhi
 *
 */
public class SubScript {

	public PayWay payway;
	public byte subIcon; //脚标类别 0：无脚标 1：推荐脚标 2：打折脚标
	public int ritio; //只有打折脚标有百分比
	
	public SubScript(PayWay payway,byte subIcon,int ritio){
		this.payway = payway;
		this.subIcon = subIcon;
		this.ritio = ritio;
	}
}
