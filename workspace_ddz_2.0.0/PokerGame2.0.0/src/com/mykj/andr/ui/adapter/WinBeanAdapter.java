package com.mykj.andr.ui.adapter;

import java.util.List;

import com.mykj.andr.model.ItemInfo;
import com.mykj.andr.ui.tabactivity.tabinterface.OnArticleTabSelectedListener;
import com.mykj.andr.ui.tabactivity.WinBeanFragment.WinBeanItem;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.UtilHelper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WinBeanAdapter extends BaseAdapter{
	
	private Activity mAct;
	private List<ItemInfo> mList;
    private OnArticleTabSelectedListener mListener;

	public WinBeanAdapter(Activity act,List<ItemInfo> list,OnArticleTabSelectedListener listener) {
		mAct = act;
		mList = list;
        mListener=listener;
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
		WinBeanItem item = null;
		if(convertView == null){
			LayoutInflater inflater = mAct.getLayoutInflater();
			convertView = inflater.inflate(R.layout.tu_hao_list_item, null);
			item = new WinBeanItem();
			item.rankId_1 = (ImageView)convertView.findViewById(R.id.player_rank_1);
			item.rankId_2 = (ImageView)convertView.findViewById(R.id.player_rank_2);
			item.gender = (ImageView)convertView.findViewById(R.id.player_gender);
			item.userVipLevel = (TextView)convertView.findViewById(R.id.player_vip_level);
			item.playerName = (TextView)convertView.findViewById(R.id.player_name);
			item.beanCount = (TextView)convertView.findViewById(R.id.player_bean_count);
			convertView.setTag(item);
		}else{
			item = (WinBeanItem)convertView.getTag();
		}
		// 设置listView item 的背景，基数无背景，偶数高亮背景
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.divider_layer);
		} else {
			convertView.setBackgroundResource(0);
		}
		ItemInfo itemInfo = (ItemInfo) getItem(position);
        mListener.setImageViewDrawable(item.rankId_2,
				item.rankId_1, itemInfo.rankId);
		/* 0-女 1-男*/
		if (itemInfo.gender == 1) {
			item.gender.setImageResource(R.drawable.male);
		} else if (itemInfo.gender == 0) {
			item.gender.setImageResource(R.drawable.female);
		} 
		item.playerName.setText(itemInfo.nickName);
		UtilHelper.setVipView(item.userVipLevel, itemInfo.vipId, itemInfo.isExpires);
		UtilHelper.setUserBeanView(item.beanCount, itemInfo.num);
		return convertView;
	}		
}
