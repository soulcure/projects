package com.mykj.andr.ui.adapter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.login.utils.DensityConst;
import com.mykj.andr.model.BackPackItem;
import com.mykj.andr.model.ExchangeItem;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.MaterialItem;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.provider.BackPackItemProvider;
import com.mykj.andr.ui.tabactivity.ExchangeGoodsFragment;
import com.mykj.andr.ui.tabactivity.ExchangeTabActivity;
import com.mykj.andr.ui.widget.CardZoneProtocolListener;
import com.mykj.andr.ui.widget.ExchangeInfoDialog;
import com.mykj.andr.ui.widget.Interface.DialogCallBack;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.ImageAsyncTaskDownload;
import com.mykj.game.utils.Util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ExchangeGoodsAdapter extends BaseAdapter {

	public String TAG = "ExchangeItemsAdapter";
	
	private Activity mAct;
	private Resources mResources;
	private UserInfo user;
	private List<ExchangeItem> mList;
	private DialogCallBack mCallBack;
		
	public static final int BEAN = 0;			// 乐豆
	public static final int PROP = 1;			// 道具
	public static final int YUAN_BAO = 2;		// 元宝
	public static final int MOBILE_VOUCHER = 3;	// 话费券
	
	/* 需要显示的对话框的类型 */
	public static final int SHOW_NO_DIALOG = 0; 			// 元宝兑换乐豆，不显示对话框直接请求兑换
	public static final int SHOW_ADDRESS_DIALOG = 1;		// 元宝兑换实物，显示填写收货人信息
	public static final int SHOW_PHONE_DIALOG = 2;			// 话费券兑换话费，显示填写手机号码对话框
	public static final int SHOW_SUCCESS_DIALOG = 3;		// 兑换成功后，显示提示信息
	public static final int SHOW_LACK_DIALOG = 4;			// 元宝不足提示信息
	public static final int SHOW_EXCHAGNING_DIALOG = 6;	    // 显示正在兑换中
	public static final int SHOW_FAILED_DIALOG = 7;			// 兑换失败，显示提示信息
	public static final int SHOW_CHOOSE_EX_COUNT = 8;		// 显示选择要兑换的的物品个数
	
	
	public ExchangeGoodsAdapter(Activity act, DialogCallBack callBack,  List<ExchangeItem> list) {
		mAct = act;
		mResources = mAct.getResources();
		mList = list;
		mCallBack = (DialogCallBack)callBack;
	}

	public void setList(List<ExchangeItem> list) {
		mList = list;
	}

	public List<ExchangeItem> getList() {
		return mList;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			LayoutInflater inflater = mAct.getLayoutInflater();
			convertView = inflater.inflate(R.layout.exchange_grid_item, null);
			holder = new ViewHolder();
			holder.ivExchangeItemPic = (ImageView)convertView.findViewById(R.id.exchange_item_pic);
			holder.tvExchangeItemName = (TextView)convertView.findViewById(R.id.exchange_item_name);
			holder.tvItemRemainNum = (TextView)convertView.findViewById(R.id.item_remain_number);
			holder.tvConditions = (TextView)convertView.findViewById(R.id.conditions);
			holder.btnExchange = (Button)convertView.findViewById(R.id.exchange);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ExchangeItem exchangeItem = mList.get(position);
		// 获取图片
		String photoFileName = exchangeItem.photoName;
		holder.ivExchangeItemPic.setImageResource(R.drawable.goods_icon);
		if(!Util.isEmptyStr(photoFileName)){
			if(photoFileName.endsWith(".png") || photoFileName.endsWith(".jpg")){
				int end = photoFileName.length() - 4;
				String photoName = photoFileName.substring(0, end);
				int drawableId = mResources.getIdentifier(photoName, "drawable", mAct.getPackageName());
				if(drawableId > 0){ // res 有图片
					holder.ivExchangeItemPic.setImageResource(drawableId);
				}else{
					String iconDir = Util.getIconDir();
					File file = new File(iconDir, photoFileName);
					if(file.exists()){
						Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
						if(bitmap != null){
							int width = bitmap.getWidth();
							int height = bitmap.getHeight();
							int disWidth = DensityConst.getWidthPixels();
							Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width * disWidth / 800,
									height * disWidth / 800, true);
							holder.ivExchangeItemPic.setImageBitmap(scaleBitmap);
						}else{
							file.delete();
							String url = AppConfig.imgUrl + photoFileName;
							new ImageAsyncTaskDownload(url, photoFileName, holder.ivExchangeItemPic).execute();
						}
					}else{
						String url = AppConfig.imgUrl + photoFileName;
						new ImageAsyncTaskDownload(url, photoFileName, holder.ivExchangeItemPic).execute();	
					}
				}
			}
		}
		
		holder.tvExchangeItemName.setText(exchangeItem.itemName);
		int remainNumber = exchangeItem.remainNumber;
		if(remainNumber < 0){
			holder.tvItemRemainNum.setText(mResources.getString(R.string.exchange_remain_tips));			
		}else{
			holder.tvItemRemainNum.setText(mResources.getString(R.string.exchange_remain_num) + remainNumber);
		}
	
		int materialItemSize = exchangeItem.materialItemList.size();
		
		user = HallDataManager.getInstance().getUserMe();
		
		if(materialItemSize == 0){			
			holder.tvConditions.setText(mResources.getText(R.string.no_conditions));			
		} else if(materialItemSize == 1){
			// 兑换原材料只有一种的情况
			MaterialItem materialItem = exchangeItem.materialItemList.get(0);
			
			String materialName = materialItem.getMaterialName();
			if(materialName.contains("|")){
				
				// 去除materialName 中 "|"
				String[] names = materialName.split("\\|");
				String newName = names[0] + names[1];
				holder.tvConditions.setText(mResources.getText(R.string.ddz_condition) + newName);				
			}else{
				holder.tvConditions.setText(mResources.getText(R.string.ddz_condition) + materialName);
			}

			holder.btnExchange.setTag(exchangeItem);
			
			if(materialItem.getMaterialType() == YUAN_BAO){
					
				holder.btnExchange.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						FiexedViewHelper.getInstance().playKeyClick();
						ExchangeItem item = (ExchangeItem)v.getTag();
						MaterialItem materialItem = item.materialItemList.get(0);
						int lackCount = user.getYuanBao() - materialItem.getNumber();
						if(lackCount >= 0){
							if(item.itemType == ExchangeItem.EXCHANGE_BEAN 
							   || item.itemType == ExchangeItem.EXCHANGE_PROP){
								// 兑换乐豆或道具的情况，不用弹框，直接换乐豆								
								ExchangeInfoDialog dialog = new ExchangeInfoDialog(mAct, item, SHOW_CHOOSE_EX_COUNT, user.getYuanBao());
								dialog.setDiglogCallBack(mCallBack);
								dialog.show();								
							}else if(item.itemType == ExchangeItem.EXCHANGE_GOODS){
								// 兑换实物的情况, 先弹出对话框让用户填写收货人信息
								ExchangeInfoDialog dialog = new ExchangeInfoDialog(mAct, item, SHOW_ADDRESS_DIALOG);		
								dialog.setDiglogCallBack(mCallBack);
								dialog.show();									
							}else{
								// 兑换物品为其他情况，do what
							}
						}else{
							ExchangeInfoDialog dialog = new ExchangeInfoDialog(mAct, item, SHOW_LACK_DIALOG, user.getYuanBao());
							dialog.show();
						}
						int key = 55+position;
						String keyString = "0"+String.valueOf(key);
						AnalyticsUtils.onClickEvent(mAct, keyString);//点击兑换
					}
				});				
			
			}else if(materialItem.getMaterialType() == MOBILE_VOUCHER){
				
				holder.btnExchange.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {	
						FiexedViewHelper.getInstance().playKeyClick();
						ExchangeItem item = (ExchangeItem)v.getTag();
						MaterialItem materialItem = item.materialItemList.get(0);
						int lackCount = user.getMobileVoucher() - materialItem.getNumber();
						if(lackCount >= 0){
							// 满足条件，直接弹填写手机号码对话框
							ExchangeInfoDialog dialog = new ExchangeInfoDialog(mAct, item, SHOW_PHONE_DIALOG);
							dialog.setDiglogCallBack(mCallBack);
							dialog.show();									
						}else{
							// 提示话费券不足
							new ExchangeInfoDialog(mAct, item, SHOW_LACK_DIALOG, user.getMobileVoucher()).show();
						}			
					}
				});						
			
			}else if(materialItem.getMaterialType() == PROP){
				
				holder.btnExchange.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						FiexedViewHelper.getInstance().playKeyClick();
						ExchangeItem item = (ExchangeItem)v.getTag();
						MaterialItem materialItem = item.materialItemList.get(0);
						// 话费券碎片兑换话费，话费券碎片来自背包					
						int userCount = findMaterialNumFromBackPack(materialItem.getMaterialId());		// 获得该道具用户拥有的数量
						int lackCount = userCount - materialItem.getNumber();
						if(lackCount >= 0){
							// 话费券碎片数满足兑换所需的条件,不显示
							ExchangeInfoDialog dialog = new ExchangeInfoDialog(mAct, item, SHOW_CHOOSE_EX_COUNT, userCount);
							dialog.setDiglogCallBack(mCallBack);
							dialog.show();
						}else{
							// 提示各种道具不足
							new ExchangeInfoDialog(mAct, item, SHOW_LACK_DIALOG, userCount).show();
						}
					}
				});		
				
			}
		} else {
			// 其他情况暂不支持
			Log.d(TAG, "other conditions not support!");
		}

		return convertView;
	}
	
	/*
	 * 根据相应的话费券id从背包中找相应的话费券道具
	 * @return 相应的话费券碎片数量
	 */
	private int findMaterialNumFromBackPack(int id){
		List<BackPackItem> backPackList = BackPackItemProvider.getInstance().getBackPageList();
		// 遍历背包，找到相应的道具数量
		for(BackPackItem backPackItem : backPackList){
			if(id == backPackItem.id){
				// 匹配到
				return backPackItem.newHoldCount;
			}
		}
		return 0;
	}
	
	private class ViewHolder {
		public ImageView ivExchangeItemPic;
		public TextView tvExchangeItemName;
		public TextView tvItemRemainNum;
		public TextView tvConditions;
		public Button btnExchange;
	}
}
