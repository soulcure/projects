package com.mykj.andr.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.mykj.andr.model.GoodsItem;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Util;

/***
 * 
 * @ClassName: GoodsItemProvider
 * @Description: 商城列表数据
 * @author
 * @date 2012-8-6 上午11:26:43
 * 
 */
public class GoodsItemProvider {
	private static GoodsItemProvider instance;
	private List<GoodsItem> list = null;			// 所有商品列表（包括需要隐藏和显示的）
	private List<GoodsItem> showList = null;		// 非隐藏商品列表
	private List<GoodsItem> classifyList = null;	// 分为乐豆和道具
	private boolean isFinish = false;

	public static final int UNSHOW_GOODS = 0;	// 不需要再商城列表中显示
	public static final int BEAN_GOODS = 1;		// 乐豆列表
	public static final int PROP_GOODS = 2;		// 道具列表
	
	
	private GoodsItemProvider() {
		list = new ArrayList<GoodsItem>();
	}

	public static GoodsItemProvider getInstance() {

		synchronized (GoodsItemProvider.class) {
			if (instance == null)
				instance = new GoodsItemProvider();
		}
		return instance;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	/**
	 * 判断商城列表是否接收完成
	 * 
	 * @return
	 */
	public boolean getFinish() {
		return isFinish;
	}

	
	public void goodsListClear(){
		list.clear();
	}
	
	
	public void add(GoodsItem info) {
		// list.add(0, info);// list倒序
		list.add(info);// list正序
	}

	public List<GoodsItem> getGoodsList() {
		return list;
	}

	public GoodsItem findGoodsItemById(int id) {
		GoodsItem goodsItem = null;
		for (GoodsItem item : list) {
			if (item.shopID == id) {
				goodsItem = item;
				break;
			}
		}
		return goodsItem;
	}

	/**
	 * 获取小钱包购买状态 
	 * true：list中包含小钱包；
	 *  false：：list中没有小钱包；
	 * 
	 * @return
	 */
	public boolean hasSmallMoneyPkg(Context c) {
		if(needShowSmallPkg(c)){
			GoodsItem good = findGoodsItemById(AppConfig.smallMoneyPkgPropId);
			return good == null ?false:true;
		}
		return false;
	}

	/**
	 * 隐藏掉小钱包
	 * @param bool
	 * @return
	 */
	public void removeSmalMoneyPkg(Context c) {
		if (hasSmallMoneyPkg(c))
			list.remove(findGoodsItemById(AppConfig.smallMoneyPkgPropId));
	}
	/*
	 * 简化操作，屏蔽此接口 public GoodsItem[] getGoodsItems() { return list.toArray(new
	 * GoodsItem[list.size()]); }
	 */
	
	/**
	 * 是否需要显示小钱包
	 * @param c
	 * @return
	 */
	private boolean needShowSmallPkg(Context c){
		return Util.providersNameIsYidong(c);
	}

	public void  setShowGoodsList(){
		
		showList = new ArrayList<GoodsItem>();
		// 1 不需要再商城列表中显示
		for(GoodsItem item : list){
			if(item.showType != 0){
				showList.add(item);
			}
		}			
	}
	
	public List<GoodsItem> getClassifyGoodsItem(int flag){
		
		if(showList == null){
			return list;		// 如果showList没有加载完，则返回所有商品列表，避免NullPointer
		}
		
		classifyList = new ArrayList<GoodsItem>();
		if(flag == BEAN_GOODS){
			// 2 乐豆列表  0 表示默认商品 放在乐豆列表中
			for(GoodsItem item : showList){
				if(item.shopType == 2 || item.shopType == 0){
					classifyList.add(item);
				}
			}		
		}else if(flag == PROP_GOODS){
			// 3 道具列表
			for(GoodsItem item : showList){
				if(item.shopType == 3){
					classifyList.add(item);
				}
			}
		}
		return classifyList;
	}
	
}