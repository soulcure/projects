package com.mykj.andr.ui.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.login.utils.DensityConst;
import com.mykj.andr.model.GoodsItem;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.ImageAsyncTaskDownload;
import com.mykj.game.utils.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PropListAdapter extends BaseAdapter {

	private Context context;
	private List<GoodsItem> goodsList;
	private Resources mResource;

	public PropListAdapter(Context context) {
		this.context = context;
		this.goodsList = new ArrayList<GoodsItem>();
		mResource = context.getResources();
	}

	public void setList(List<GoodsItem> goodsList) {
		this.goodsList = goodsList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return goodsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return goodsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final GoodsItem goodItem = (GoodsItem) getItem(position);
		
//		if(goodItem.showType == 0 || goodItem.goodsType == 2){
//			return null;
//		}else{
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.prop_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.prop_icon);
//				holder.name = (TextView)convertView.findViewById(R.id.prop_name);
				holder.tabRecommend = (TextView) convertView
						.findViewById(R.id.tag_recommend);
				holder.tvGoodsDesc = (TextView) convertView
						.findViewById(R.id.prop_content);
				holder.tvPropName = (TextView)convertView.findViewById(R.id.prop_name);
				holder.price = (TextView) convertView.findViewById(R.id.prop_price);
				holder.award = (TextView) convertView.findViewById(R.id.prop_award);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置图片
			final String photoFileName = goodItem.goodsPhoto;
			holder.icon.setTag(photoFileName);
//			holder.icon.setBackgroundResource(R.drawable.market_icon);
			if (!Util.isEmptyStr(photoFileName)) {
				if (photoFileName.endsWith(".png")
						|| photoFileName.endsWith(".jpg")) {
					int end = photoFileName.length() - 4;
					String photoName = photoFileName.substring(0, end);
					int drawableId = context.getResources().getIdentifier(
							photoName, "drawable", context.getPackageName());
					if (drawableId > 0) { // res有图片
						holder.icon.setImageResource(drawableId);
					} else {
						String iconDir = Util.getIconDir();
						File file = new File(iconDir, photoFileName);
						if (file.exists()) {
							Bitmap bitmap = BitmapFactory
									.decodeFile(file.getPath());
							if (bitmap != null) {
								int width = bitmap.getWidth();
								int height = bitmap.getHeight();
								int disWidth = DensityConst.getWidthPixels();
								Bitmap scaleBitmap = Bitmap.createScaledBitmap(
										bitmap, width * disWidth / 800, height
												* disWidth / 800, true);
								holder.icon.setImageBitmap(scaleBitmap);
							} else {
								file.delete();
								holder.icon.setImageResource(R.drawable.market_icon);
								String url = AppConfig.imgUrl + photoFileName;
								new ImageAsyncTaskDownload(url, photoFileName,
										holder.icon).execute();
							}

						} else {
							holder.icon.setImageResource(R.drawable.market_icon);
							String url = AppConfig.imgUrl + photoFileName;
							new ImageAsyncTaskDownload(url, photoFileName,
									holder.icon).execute();
						}
					}
				}

			}
			if(goodItem.goodsName != null){
				holder.tvPropName.setText(goodItem.goodsName);	// 物品名称	
			}else{
				holder.tvPropName.setText("");
			}
			if(goodItem.priceDesc != null){
				holder.price.setText(goodItem.priceDesc); 		// 设置商品价格		
			}else{
				holder.price.setText("");
			}

			if (goodItem.goodsPresented != null
					&& goodItem.goodsPresented.trim().length() > 1){
				// 如果字符串中 “+”就换行
				if(goodItem.goodsPresented.contains("+")){
					String[] str = goodItem.goodsPresented.split("\\+");
					String goodsAwardDetails = str[0] + "\n" + str[1];
					holder.award.setText(goodsAwardDetails);
				}else{
					holder.award.setText(goodItem.goodsPresented); // 赠送					
				}
			}else{
				holder.award.setText(""); // 赠送
			}

//			holder.money.setText(goodItem.getGoodsPrice());

			if (goodItem != null && goodItem.goodsDescrip != null) {
				holder.tvGoodsDesc.setText(goodItem.goodsDescrip); // 子项商品详细描述
				holder.tvGoodsDesc.setTag(goodItem.goodsDescrip.trim());
			}else{
				holder.tvGoodsDesc.setText("");
				holder.tvGoodsDesc.setTag("");
			}
//			holder.ivGoodsBuy.setTag(goodItem);
			// 根据不同值设置角标记:1：打折，2：热卖，3，推荐
			if (goodItem.cornID != 0) {
				if (goodItem.cornID == 1) {
					holder.tabRecommend.setBackgroundResource(R.drawable.tag_discount);
					holder.tabRecommend.setText(mResource.getString(R.string.goods_discount));
				} else if (goodItem.cornID == 2) {
					holder.tabRecommend.setBackgroundResource(R.drawable.tag_hot);
					holder.tabRecommend.setText(mResource.getString(R.string.goods_hot));
				} else if (goodItem.cornID == 3) {
					holder.tabRecommend.setBackgroundResource(R.drawable.tag_discount);
					holder.tabRecommend.setText(mResource.getString(R.string.goods_recomend));
				}
			} else {
				holder.tabRecommend.setBackgroundResource(0); // 使用默认标记,透明	
				holder.tabRecommend.setText("");		
			}


			return convertView;			
//		}

	}

	class ViewHolder {
		ImageView icon;
		TextView tabRecommend;
		TextView tvPropName;
		TextView tvGoodsDesc;
		TextView price;
		TextView award;
	}
}
