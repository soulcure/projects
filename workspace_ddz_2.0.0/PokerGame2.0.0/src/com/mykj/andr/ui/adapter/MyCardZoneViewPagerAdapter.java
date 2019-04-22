package com.mykj.andr.ui.adapter;

import java.io.File;
import java.util.List;

import com.login.utils.DensityConst;
import com.mykj.andr.model.NodeData;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.ImageAsyncTaskDownload;
import com.mykj.game.utils.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyCardZoneViewPagerAdapter extends PagerAdapter {
	private List<NodeData> mNodeList;
	private Context mContext;
	private OnNodeClickListener lis;
	private boolean isMatch = false;

	/**
	 * 构造方法，参数是我们的页卡，
	 * 
	 * @param mListViews
	 */
	public MyCardZoneViewPagerAdapter(List<NodeData> nodeList, Context context) {
		this.mNodeList = nodeList;
		if (mNodeList != null && mNodeList.size() > 0
				&& mNodeList.get(0).Type == NodeData.NODE_ENROLL) {
			isMatch = true;
		} else {
			isMatch = false;
		}
		mContext = context;
	}

	public void setOnItemClickListener(OnNodeClickListener lis) {
		this.lis = lis;
	}

	/**
	 * 重设数据
	 * 
	 * @param listViews
	 */
	public void setViews(List<NodeData> nodeList) {
		this.mNodeList.clear();
		this.mNodeList.addAll(nodeList);
		if (mNodeList != null && mNodeList.size() > 0
				&& mNodeList.get(0).Type == NodeData.NODE_ENROLL) {
			isMatch = true;
		} else {
			isMatch = false;
		}
		this.notifyDataSetChanged();
	}

	/**
	 * 删除指定页卡
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// container.removeView(mListViews.get(position));
		((ViewGroup) container).removeView((View) object);
	}

	/**
	 * 这个方法用来实例化页卡
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int mLayoutId = R.layout.cardzone_gridpage;
		if (isMatch) { // 比赛场
			mLayoutId = R.layout.cardzone_gridpage_match;
		}
		View page = LayoutInflater.from(mContext).inflate(mLayoutId, null);
		int index = position * 4;
		RelativeLayout item = (RelativeLayout) page
				.findViewById(R.id.ryCardZoneGridViewItem1);
		setViewData(item, index);
		item = (RelativeLayout) page.findViewById(R.id.ryCardZoneGridViewItem2);
		setViewData(item, index + 1);
		item = (RelativeLayout) page.findViewById(R.id.ryCardZoneGridViewItem3);
		setViewData(item, index + 2);
		item = (RelativeLayout) page.findViewById(R.id.ryCardZoneGridViewItem4);
		setViewData(item, index + 3);
		container.addView(page, 0);// 添加页卡
		return page;
	}

//	int topPadding = 0;
//	int leftPadding = 0;
//	int topPadding2 = 0;
//	int leftPadding2 = 0;
//	int bottomPadding = 0;
//	int rightPadding = 0;
	View.OnClickListener clickLis = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			FiexedViewHelper.getInstance().playKeyClick();
			// TODO Auto-generated method stub
			NodeData node = (NodeData) v.getTag();
			if (lis != null && node != null) {
				lis.onClick(node);
			}
		}
	};

	void setViewData(View v, int index) {
		if (index < 0 || index >= mNodeList.size()) {
			v.setVisibility(View.INVISIBLE);
			v.setTag(null);
			return;
		}

		NodeData node = mNodeList.get(index);
		v.setTag(node);
		v.setOnClickListener(clickLis);
		ViewHolder holder = new ViewHolder();
		holder.tvName = (TextView) v.findViewById(R.id.tvName);
		holder.tvOnLineUsers = (TextView) v.findViewById(R.id.tvOnLineUsers);
		holder.tvInCondition = (TextView) v.findViewById(R.id.tvInCondition);
		holder.tvScore = (TextView) v.findViewById(R.id.tvScore);
		holder.iconImg = (TextView) v.findViewById(R.id.iconImg);
		holder.iconImg2 = (TextView) v.findViewById(R.id.iconImg2);
		// holder.ryCardZoneGridViewItem =
		// (RelativeLayout)v.findViewById(R.id.ryCardZoneGridViewItem);
		holder.pic = (ImageView) v.findViewById(R.id.pic);
		holder.ivName = (ImageView) v.findViewById(R.id.ivName);
		holder.divider = (ImageView) v.findViewById(R.id.divider);
		holder.attend = (ImageView) v.findViewById(R.id.attend);

		if (Util.isEmptyStr(node.Name)) {
			node.Name = "";
		}
		int namePic = 0;
		if (node.Name.equals("练习场")) {
			namePic = R.drawable.cardzone_lianxichang;
		} else if (node.Name.equals("初级场")) {
			namePic = R.drawable.cardzone_chujichang;
		} else if (node.Name.equals("中级场")) {
			namePic = R.drawable.cardzone_zhongjichang;
		} else if (node.Name.equals("高级场")) {
			namePic = R.drawable.cardzone_gaojichang;
		} else if (node.Name.equals("大师场")) {
			namePic = R.drawable.cardzone_dashichang;
		} else if (node.Name.equals("宗师场")) {
			namePic = R.drawable.cardzone_zongshichang;
		} else if (node.Name.equals("元宝场")) {
			namePic = R.drawable.cardzone_yuanbaochang;
		} else if (node.Name.equals("炸弹场")) {
			namePic = R.drawable.cardzone_zhadanchang;
		}

		if (namePic > 0) {
			holder.ivName.setImageResource(namePic);
			holder.ivName.setVisibility(View.VISIBLE);
			holder.tvName.setVisibility(View.INVISIBLE);
			holder.tvScore.setVisibility(View.INVISIBLE);
			holder.divider.setVisibility(View.INVISIBLE);
		} else {
			holder.tvName.setText(node.Name);
			String score = node.ExName;
			holder.tvScore.setText(score);
			holder.ivName.setVisibility(View.INVISIBLE);
			holder.tvName.setVisibility(View.VISIBLE);
			holder.tvScore.setVisibility(View.VISIBLE);
			holder.divider.setVisibility(View.VISIBLE);
		}

		if (node.isAttend()) {
			holder.attend.setVisibility(View.VISIBLE);
		} else {
			holder.attend.setVisibility(View.INVISIBLE);
		}
		Resources mResource = mContext.getResources();
		if (node.onLineUser < 10000) {
			holder.tvOnLineUsers.setText("" + node.onLineUser
					+ mResource.getString(R.string.ddz_person));
		} else if (node.onLineUser < 100000) {
			int sub = node.onLineUser % 10000 / 1000;
			holder.tvOnLineUsers.setText("" + node.onLineUser / 10000
					+ (sub == 0 ? "" : "." + sub)
					+ mResource.getString(R.string.ddz_wan_ren));
		} else {
			holder.tvOnLineUsers.setText("" + node.onLineUser / 10000
					+ mResource.getString(R.string.ddz_wan_ren));
		}
		holder.tvInCondition.setText(node.GTContent);

		if (node.RoomTags != null && node.RoomTags.length != 0) {
			if (node.RoomTags.length > 1) {
				holder.iconImg2.setVisibility(View.VISIBLE);
//				if (leftPadding == 0 || topPadding == 0) {
//					leftPadding = holder.iconImg2.getPaddingLeft();
//					rightPadding = holder.iconImg2.getPaddingRight();
//					topPadding = holder.iconImg2.getPaddingTop();
//					bottomPadding = holder.iconImg2.getPaddingBottom();
//					leftPadding2 = leftPadding * 3 / 2;
//					topPadding2 = topPadding * 2;
//				}

				

				String tip = node.RoomTags[1].Desc;
				if (tip != null) {
					holder.iconImg2
					.setBackgroundResource(getDrawableId(node.RoomTags[1].Type,tip.length()));
//					int left = leftPadding;
//					int top = topPadding;
//					if (tip.length() < 2) {
//						top = topPadding2;
//						left = leftPadding2;
//					} else if (tip.length() <= 3) { // 3个字符是2个字，中间是\n
//						left = leftPadding2;
//					}
//					holder.iconImg2.setPadding(left, top, rightPadding,
//							bottomPadding);
					holder.iconImg2.setText(tip);
				}
			} else {
				holder.iconImg2.setVisibility(View.GONE);
			}
			holder.iconImg.setVisibility(View.VISIBLE);
//			if (leftPadding == 0 || topPadding == 0) {
//				leftPadding = holder.iconImg.getPaddingLeft();
//				rightPadding = holder.iconImg.getPaddingRight();
//				topPadding = holder.iconImg.getPaddingTop();
//				bottomPadding = holder.iconImg.getPaddingBottom();
//				leftPadding2 = leftPadding * 3 / 2;
//				topPadding2 = topPadding * 2;
//			}
			
			String tip = node.RoomTags[0].Desc;
			if (tip != null) {
				holder.iconImg
				.setBackgroundResource(getDrawableId(node.RoomTags[0].Type,tip.length()));
//				int left = leftPadding;
//				int top = topPadding;
//				if (tip.length() < 2) {
//					top = topPadding2;
//					left = leftPadding2;
//				} else if (tip.length() <= 3) { // 3个字符是2个字，中间是\n
//					left = leftPadding2;
//				}
				if (tip.length()>2) {
					holder.iconImg.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
					holder.iconImg.setPadding(0, 2, 0, 0);
				}
				holder.iconImg.setText(tip);
			}

		} else {
			holder.iconImg.setVisibility(View.GONE);
			holder.iconImg2.setVisibility(View.GONE);
		}

		// 设置图片
		final String photoFileName = node.PicName;

		holder.pic.setTag(photoFileName);
		if (!Util.isEmptyStr(photoFileName)) {
			if (photoFileName.endsWith(".png")
					|| photoFileName.endsWith(".jpg")) {
				int end = photoFileName.length() - 4;
				String photoName = photoFileName.substring(0, end);
				int drawableId = mResource.getIdentifier(photoName, "drawable",
						mContext.getPackageName());
				if (drawableId > 0) { // res有图片
					holder.pic.setImageResource(drawableId);
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
							holder.pic.setImageBitmap(scaleBitmap);
						} else {
							file.delete();
							if (isMatch) {
								holder.pic
										.setImageResource(R.drawable.zone_grid_pic_match);
							} else {
								holder.pic
										.setImageResource(R.drawable.chujichang_icon);
							}
							String url = AppConfig.nodeImgUrl + "/" + photoFileName;
							new ImageAsyncTaskDownload(url, photoFileName,
									holder.pic).execute();
						}

					} else {
						if (isMatch) {
							holder.pic
									.setImageResource(R.drawable.zone_grid_pic_match);
						} else {
							holder.pic
									.setImageResource(R.drawable.chujichang_icon);
						}
						String url = AppConfig.nodeImgUrl + "/" + photoFileName;
						new ImageAsyncTaskDownload(url, photoFileName,
								holder.pic).execute();
					}
				}
			}

		}
	}

	/**
	 * 返回页卡的数量
	 */
	@Override
	public int getCount() {
		return (mNodeList.size() + 3) / 4;
	}

	// public View getChild(int position){
	// if(mListViews!=null && position >=0 && position < mListViews.size()){
	// return mListViews.get(position);
	// }
	// return null;
	// }
	private int mChildCount = 0;

	@Override
	public void notifyDataSetChanged() {
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	/**
	 * 官方提示这样写
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	private int getDrawableId(int type, int length) {

		switch (type % 2) {
		case 0:
			if (length>2) {
				return R.drawable.cardzone_corner_purple_big;
			}
			return R.drawable.cardzone_corner_purple;
		case 1:
			if (length>2) {
				return R.drawable.cardzone_corner_blue_big;
			}
			return R.drawable.cardzone_corner_blue;
		default:
			break;
		}
		return R.drawable.cardzone_corner_blue;
	}

	public class ViewHolder {
		TextView tvName; // 名称
		TextView tvOnLineUsers; // 在线人数
		TextView tvInCondition; // 条件
		TextView tvScore; // 底分
		ImageView ivName; // 图片名字
		// Button btnComeIn; // 进入游戏/快速报名/退赛
		TextView iconImg; // 图标，默认，热门，推荐等
		TextView iconImg2; // 图标，默认，热门，推荐等
		RelativeLayout ryCardZoneGridViewItem; // 父容器
		ImageView pic; // 图片，2.0.0新增
		ImageView divider;
		ImageView attend; // 已报名
	}

	public interface OnNodeClickListener {
		void onClick(NodeData data);
	};
}
