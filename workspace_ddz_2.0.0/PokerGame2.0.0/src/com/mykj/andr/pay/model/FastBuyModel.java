package com.mykj.andr.pay.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author JiangYinZhi
 * 
 */
public class FastBuyModel {

	public static int propId = 64;// 快捷购买道具
	public static boolean isConfirmon = false;// 是否需要短信2次确认
	public static boolean isShowCancel;// 是否显示取消按钮
	public static boolean isFastBuyConfirm = true;//快捷购买是否2次确认
	public static boolean isPromotionConfirm = false;//首充推广是否需要2次确认
	public static boolean singlePayOn = true;// 是否使用单支付
	public static int defaultSinglePaySign1 = 0;// 默认单支付1
	public static int defaultSinglePaySign2 = -1;// 默认单支付2
	public static boolean fastTipOn = true;// 支付提示开关
	public static String fastTip1;// 提示1
	public static String fastTip2;// 提示2
	public static boolean lastPayOn = false;// 是否要使用上次成功的购买支付
	public static boolean fastCancelPliston =false;// 是否要使用点击取消按钮多次支付
	public static Map<Integer, Integer> lastPayGoods;// 上次购买成功的支付列表,map键表示商品id，map值表示上次成功的支付方式
	public static ArrayList<Integer> largeIDs = new ArrayList<Integer>();//大额道具id集合
}
