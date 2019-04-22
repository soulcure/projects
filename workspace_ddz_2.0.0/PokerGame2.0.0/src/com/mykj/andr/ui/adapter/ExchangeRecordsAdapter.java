package com.mykj.andr.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mykj.andr.model.RecordsItem;
import com.mykj.game.ddz.R;

public class ExchangeRecordsAdapter extends BaseAdapter{
	
	private Activity mAct;
	private Resources mResources;
	private List<RecordsItem> mList;
	public ExchangeRecordsAdapter(Activity act, List<RecordsItem> list){
		mAct = act;
		mList = list;
		mResources = mAct.getResources();
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = mAct.getLayoutInflater();
		Holder holder = null;
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.exchange_records_item, null);	
			holder = new Holder();
			holder.tvExOpTime = (TextView)convertView.findViewById(R.id.exchange_op_time);
			holder.tvExGiftName = (TextView)convertView.findViewById(R.id.exchange_gift_name);
			holder.tvExGiftConsumption = (TextView)convertView.findViewById(R.id.exchange_gift_consumption);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		
		RecordsItem item = (RecordsItem)getItem(position);
		
		holder.tvExOpTime.setText(item.getOpTime());
		holder.tvExGiftName.setText(item.getGiftName());
		holder.tvExGiftConsumption.setText(item.getDesc());
		
		return convertView;
	}
	
	private class Holder{
		private TextView tvExOpTime;			// 兑换时间
		private TextView tvExGiftName;			// 消耗的物品名称
		private TextView tvExGiftConsumption;	// 消耗的物品描述
	}
}