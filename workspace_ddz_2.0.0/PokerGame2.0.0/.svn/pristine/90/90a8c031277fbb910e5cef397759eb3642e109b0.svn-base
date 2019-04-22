package com.mykj.andr.ui.adapter;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.login.utils.DensityConst;
import com.mykj.andr.model.BackPackItem;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.ImageAsyncTaskDownload;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

/**
 * 
 * @ClassName: BackPageAdapter
 * @Description: 背包adapter
 * @author Administrator
 * @date 2012-7-23 下午02:06:44
 * 
 */
public class BackPageAdapter extends BaseAdapter{

	/** 刷新UI */
	public static final int REFRESH = 1;
	/** 不刷新UI */
	public static final int UNREFRESH = 0;
	private List<BackPackItem> mLists;
	private Activity mAct;
	private Resources mResource;
	private UseCallBack mUseCallBack;

	public BackPageAdapter(Activity act, List<BackPackItem> lists,UseCallBack callBack,
			HandselCallBack handselCallBack) {
		mAct = act;
		mLists=lists;
		mUseCallBack = callBack;
		mResource = mAct.getResources();
	}


	/**
	 * 重设数据
	 * @param listViews
	 */
	public void setList(List<BackPackItem> lists){
		mLists.clear();
		mLists.addAll(lists);
		this.notifyDataSetChanged();
	}
	
	
	
	@Override
	public int getCount() {
		return mLists.size();
		//return 10;
	}

	@Override
	public Object getItem(int position) {
		return mLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder;
		if (row == null) {
			LayoutInflater inflater = mAct.getLayoutInflater();
			row = inflater.inflate(R.layout.backpack_item, null);
			holder = new ViewHolder();

			holder.ivGoods = (ImageView) row.findViewById(R.id.exchange_item_pic);
			holder.tvGoodsName = (TextView) row.findViewById(R.id.tvGoodsName);
			holder.tvGoodsExpire = (TextView)row.findViewById(R.id.tvGoodsExpire);
			holder.tvGoodsDesc = (TextView)row.findViewById(R.id.tvGoodsDesc);
			holder.btnGoodsUse = (Button) row.findViewById(R.id.btnGoodsUse);

			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		BackPackItem pItem = (BackPackItem) getItem(position);


		// 使用按钮
		holder.btnGoodsUse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FiexedViewHelper.getInstance().playKeyClick();
				BackPackItem item = (BackPackItem) v.getTag();
				if (mUseCallBack != null) {
					mUseCallBack.invoke(item);
				}
			}
		});


		// 设置图片
		final String photoFileName=pItem.backpackPhoto;
		holder.ivGoods.setTag(photoFileName);
		holder.ivGoods.setImageResource(R.drawable.goods_icon);
		if (!Util.isEmptyStr(photoFileName)) {
			if (photoFileName.endsWith(".png")||photoFileName.endsWith(".jpg")) {
				int end = photoFileName.length() - 4;
				String photoName = photoFileName.substring(0, end);
				int drawableId = mAct.getResources().getIdentifier(photoName,
						"drawable", mAct.getPackageName());
				if (drawableId > 0) { // res有图片
					holder.ivGoods.setImageResource(drawableId);
				}else{
					String iconDir=Util.getIconDir();
					File file=new File(iconDir,photoFileName);
					if(file.exists()){
						Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
						if(bitmap!=null){
							int width = bitmap.getWidth();
							int height = bitmap.getHeight();
							int disWidth = DensityConst.getWidthPixels();
							Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width
									* disWidth / 800, height * disWidth / 800, true);
							holder.ivGoods.setImageBitmap(scaleBitmap);
						}else{
							file.delete();
							holder.ivGoods.setImageResource(R.drawable.goods_icon);
							String url=AppConfig.imgUrl+photoFileName;
							new ImageAsyncTaskDownload(url,photoFileName,holder.ivGoods).execute();
						}

					}else{
						holder.ivGoods.setImageResource(R.drawable.goods_icon);
						String url=AppConfig.imgUrl+photoFileName;
						new ImageAsyncTaskDownload(url,photoFileName,holder.ivGoods).execute();
					}
				}
			}

		}

		holder.tvGoodsName.setText(pItem.backpackName + "*" + pItem.newHoldCount); // 物品名称	
		holder.tvGoodsExpire.setText(mResource.getString(R.string.ex_goods_expire) + parseDate(pItem.ExpireTime));
		holder.tvGoodsDesc.setText(pItem.backpackDescrip);
		holder.btnGoodsUse.setTag(pItem);
		
		return row;
	}

	private String parseDate(String date){
		StringBuilder sb = new StringBuilder();
		sb.append("20").append( date.substring(0, 2)).append("/");
		sb.append( date.substring(2, 4)).append("/");
		sb.append( date.substring(4, 6));	
		return sb.toString();
	}
	
	private static class ViewHolder {
		ImageView ivGoods; 		// 商品图标
		TextView tvGoodsName; 	// 商品名称
		TextView tvGoodsExpire;	// 商品过期时间
		TextView tvGoodsDesc;	// 商品描述
		Button btnGoodsUse; 	// 使用按钮

	}

	

	// 使用接口回调
	public interface UseCallBack {
		void invoke(BackPackItem item);
	}

	// 使用接口回调赠送
	public interface HandselCallBack {
		void invokeHandsel(BackPackItem item);
	}


}
