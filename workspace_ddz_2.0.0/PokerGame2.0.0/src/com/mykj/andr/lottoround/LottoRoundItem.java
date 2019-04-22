package com.mykj.andr.lottoround;

import java.io.Serializable;


/**
 * 抽奖机奖品实例
 * 
 * @author JiangYinZhi
 * 
 */
public class LottoRoundItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 索引 */
	private int index;
	/** 类型,2为乐豆 */
	private int type;
	/** id */
	private int id;
	/** 数量 */
	private int num;
	/** 描述 */
	private String desc;
	/** 文件路径名称 */
	private String fileName;
	
	/**
	 * 
	 * @param index
	 *            索引
	 */
	public LottoRoundItem(int index) {
		this.index = index;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
