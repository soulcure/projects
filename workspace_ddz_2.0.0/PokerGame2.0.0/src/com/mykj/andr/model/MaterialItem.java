package com.mykj.andr.model;

import java.io.Serializable;

public class MaterialItem implements Serializable {
	private static final long serialVersionUID = 2052937845161903592L;
	/**
	 * 实物兑换材料（元宝，话费券，虚拟货币）
	 * 
	 * @author X.Y.Z
	 * 
	 */
	private String materialName; // 兑换材料名称
	private int number; // 兑换材料数量
	private int materialType; // 兑换材料 0=乐豆，1=道具, 2=元宝，3=话费券
	private int materialId; // 兑换材料ID
								// 当materialType=1时，materialId表示道具ID，当materialType=0表示乐豆时，materialId无意义
	private int type; // 可以获取该兑换材料途径类型 1、URL；2、节点
	private int getWay; // 可以获取该兑换材料途径值   0 不跳转 1 跳转比赛房间列表 2 速配

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getMaterialType() {
		return materialType;
	}

	public void setMaterialType(int materialType) {
		this.materialType = materialType;
	}

	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getValue() {
		return getWay;
	}

	public void setValue(int value) {
		this.getWay = value;
	}
}
