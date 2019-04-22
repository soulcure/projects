package com.mykj.andr.model;

import java.io.Serializable;

/**
 * 成就实体类
 * @author Administrator
 *
 */
public class AchieveInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**成就id*/
	public int id = -1;
	/**该成就所属 徽章类别 的徽章id*/
	public int badgeId = -1;
	/**达到目标的条件值*/
	public int totalCount = 0;
	
	/**成就名称*/
	public String name ="";
	/**成就图片名称*/
	public String photo = "";
	/**成就描述*/
	public String description="";
	
//	========以下只有在我的成就中包含======================
	/**当前已经完成目标值*/
	public int currentCount=0;
	
	/**成就状态
	 * 0:未完成
	 * 1：已完成
	 * */
	public int status=-1;
}
