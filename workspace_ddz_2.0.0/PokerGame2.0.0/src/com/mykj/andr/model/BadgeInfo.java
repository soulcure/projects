package com.mykj.andr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 徽章实体类
 * 
 * @author Administrator
 * 
 */
public class BadgeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 徽章id */
	public int id = -1;
	/** 徽章颜色 */
	public int color = -1;
	/** 徽章名称 */
	public String name = "";
	/** 徽章图片名称 */
	public String photo="";
	/** 徽章描述 */
	public String description="";
	/** 徽章奖励信息文本 列表 */
	public List<String> awardInfoList = new ArrayList<String>();
	
	//======================================
	//以下属性作为 本人成就才包含
	/** 当前持有此徽章的人数 */
	public int ownerCount = 0;
	/**
	 * 徽章 状态 
	 * 0、未完成 
	 * 1、未领奖
	 *  2、未佩戴 
	 *   3、已佩戴
	 * */
	public int status=-1;
	
	public static final int STATUS_UNFINISHED = 0;
	public static final int STATUS_NOT_ACCEPT_AWARD = 1;
	public static final int STATUS_NOT_PUTON = 2;
	public static final int STATUS_ALEARDY_PUTON = 3;
	
	/**包含成就个数*/
	public List<AchieveInfo> achieveList = new ArrayList<AchieveInfo>();
}
