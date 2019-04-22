package com.mykj.andr.pay.model;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.login.utils.DensityConst;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.ImageAsyncTaskDownload;
import com.mykj.game.utils.Util;

/**
 * 
 * 加赠商品实体类
 * 
 * @author JiangYinZhi
 * 
 */
public class AddGiftItem {

	public int count = -1;// 加赠商品数量
	public String iconName;// 图片名字
	public String name;// 加赠商品的名称

	/**
	 * 设置加赠图片
	 * 
	 * @param mContext
	 *            上下文
	 * @param addGiftIV
	 *            加赠图片显示的imageView
	 */
	public void getAddGiftIcon(Context mContext, ImageView addGiftIV) {
		if (iconName == null || iconName.length() <= 0) {
			return;
		}
		// 设置图片
		final String photoFileName = iconName;
		if (!Util.isEmptyStr(photoFileName)) {
			int disWidth = DensityConst.getWidthPixels();
			if (photoFileName.endsWith(".png")
					|| photoFileName.endsWith(".jpg")) {
				int end = photoFileName.length() - 4;
				String photoName = photoFileName.substring(0, end);
				int drawableId = mContext.getResources().getIdentifier(
						photoName, "drawable", mContext.getPackageName());
				if (drawableId > 0) { // res有图片
					addGiftIV.setImageResource(drawableId);
				} else {
					String iconDir = Util.getIconDir();
					File file = new File(iconDir, photoFileName);
					if (file.exists()) {
						Bitmap bitmap = BitmapFactory
								.decodeFile(file.getPath());
						if (bitmap != null) {
							Bitmap scaleBitmap = Bitmap.createScaledBitmap(
									bitmap, 95 * disWidth / 1100,
									95 * disWidth / 1100, true);
							addGiftIV.setImageBitmap(scaleBitmap);
						} else {
							file.delete();
							Bitmap defaultBitmap = BitmapFactory
									.decodeResource(mContext.getResources(),
											R.drawable.goods_icon);
							if (defaultBitmap != null) {
								Bitmap scaleDefaultBitmap = Bitmap
										.createScaledBitmap(defaultBitmap,
												95 * disWidth / 1100,
												95 * disWidth / 1100, true);
								addGiftIV.setImageBitmap(scaleDefaultBitmap);
							}
							String url = AppConfig.imgUrl + photoFileName;
							new ImageAsyncTaskDownload(url, photoFileName,
									addGiftIV).execute();
						}

					} else {
						Bitmap defaultBitmap = BitmapFactory.decodeResource(
								mContext.getResources(), R.drawable.goods_icon);
						if (defaultBitmap != null) {
							Bitmap scaleDefaultBitmap = Bitmap
									.createScaledBitmap(defaultBitmap,
											95 * disWidth / 1100,
											95 * disWidth / 1100, true);
							addGiftIV.setImageBitmap(scaleDefaultBitmap);
						}
						String url = AppConfig.imgUrl + photoFileName;
						new ImageAsyncTaskDownload(url, photoFileName,
								addGiftIV).execute();
					}
				}
			}

		}
	}

}
