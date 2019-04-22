package com.mykj.andr.model;

/*
 * 保存从web上解析出的数据的结构类
 */
public class TuHaoItemInfo{
	public int rankId;		// 名次
	public boolean isMale;	// 性别
	public String headPicUrl; // 现在头像的地址
	public String nickName;	  // 玩家昵称
	public int diamondCount;  // 玩家拥有的钻石数量
	public int getRankId() {
		return rankId;
	}
	public void setRankId(int rankId) {
		this.rankId = rankId;
	}
	public boolean isMale() {
		return isMale;
	}
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}
	public String getHeadPicUrl() {
		return headPicUrl;
	}
	public void setHeadPicUrl(String headPicUrl) {
		this.headPicUrl = headPicUrl;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getDiamondCount() {
		return diamondCount;
	}
	public void setDiamondCount(int diamondCount) {
		this.diamondCount = diamondCount;
	}
	
}