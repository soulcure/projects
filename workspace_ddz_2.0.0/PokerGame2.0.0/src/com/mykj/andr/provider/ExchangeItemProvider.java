package com.mykj.andr.provider;

import java.util.ArrayList;
import java.util.List;

import com.mykj.andr.model.ExchangeItem;

/*
 * 实物兑换数据
 */
public class ExchangeItemProvider {
	
	private static ExchangeItemProvider instance;
	private List<ExchangeItem> mList;
	private boolean isFinish=false;
	
	private ExchangeItemProvider(){
		mList = new ArrayList<ExchangeItem>();
	}
	
	public static ExchangeItemProvider getInstance(){
		if(instance == null){
			synchronized (ExchangeItemProvider.class) {
				if(instance == null){
					instance = new ExchangeItemProvider();
				}
			}
		}
		return instance;
	}

	public void init(){
		if(mList!=null){
			mList.clear();
		}
		isFinish=false;
	}
	
	public void setExchangeItem(ExchangeItem[] array){
		if(array == null){
			return;
		}
		for(ExchangeItem item : array){
			mList.add(item);
		}
	}
	
	public void addExchangeItemToFirst(ExchangeItem info){
		if(mList.isEmpty()){
			mList.add(info);
		}else{
			mList.add(0,info);
		}
	}
	
	public void addExchangeItem(ExchangeItem info){
		mList.add(info);
	}
	
	public List<ExchangeItem> getExchangeItemList(){ 
		return mList;
	}
	
	public boolean isFinish() {
		return isFinish;
	}


	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
}
