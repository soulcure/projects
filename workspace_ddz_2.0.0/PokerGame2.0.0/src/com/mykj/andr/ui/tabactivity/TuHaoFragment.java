package com.mykj.andr.ui.tabactivity;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.ItemInfo;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.ui.SinglePayDialog;
import com.mykj.andr.ui.adapter.TuHaoAdapter;
import com.mykj.andr.ui.tabactivity.tabinterface.OnArticleTabSelectedListener;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.CenterUrlHelper;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TuHaoFragment extends ListFragment {

	private Activity mAct;
	private Resources mResources;
	private String tuHaoUrl;
	private List<ItemInfo> itemInfos; // 接收来自web的数据
	private OnArticleTabSelectedListener mSetImageViewListener;
	private TuHaoItem item;
	private UserInfo user;
	private int curUserRank;
	private LinearLayout busy;
	private TextView tip; // 提示加载失败
	private RelativeLayout rankMain;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAct = activity;
		try {
			mSetImageViewListener = (OnArticleTabSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implements OnSetImageViewListener!");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tuhao_list, null);

		mResources = mAct.getResources();
		user = HallDataManager.getInstance().getUserMe();

		String sign = CenterUrlHelper.getSign(getUrlParam(),
				CenterUrlHelper.secret);
		tuHaoUrl = getUrl() + sign;

		new TuHaoAsyncTask().execute(tuHaoUrl);

		busy = (LinearLayout) rootView.findViewById(R.id.linear_busy);
		rankMain = (RelativeLayout) rootView.findViewById(R.id.rank_main);
		tip = (TextView) rootView.findViewById(R.id.tip_failed);

		item = new TuHaoItem();

		item.rankId_1 = (ImageView) rootView.findViewById(R.id.user_rank_1);
		item.rankId_2 = (ImageView) rootView.findViewById(R.id.user_rank_2);
		item.playerName = (TextView) rootView.findViewById(R.id.user_name);
		item.beanCount = (TextView) rootView.findViewById(R.id.user_bean_count);
		item.userVipLevel = (TextView) rootView
				.findViewById(R.id.user_vip_level);
		item.gender = (ImageView) rootView.findViewById(R.id.user_gender);
		item.btnToWin = (Button) rootView.findViewById(R.id.btnToWin);

		return rootView;
	}

	private String getUrl() {
		return AppConfig.RANK_PATH + "?" + getUrlParam();
	}

	private String getUrlParam() {
		StringBuffer sb = new StringBuffer();
		sb.append("apiname=").append("DdzRank");
		sb.append('&').append("rid=").append(14);
		sb.append('&').append("op=").append(12121);
		sb.append('&').append("format=").append("xml");

		return sb.toString();
	}

	/*
	 * 负责显示数据的View集合
	 */
	public static class TuHaoItem {
		public ImageView rankId_1; // 个位数的图片
		public ImageView rankId_2; // 十位数的图片
		public ImageView gender; // 性别
		public TextView playerName; // 玩家昵称
		public TextView userVipLevel; // vip 等级
		public TextView beanCount; // 乐豆数量
		public Button btnToWin;
	}

	private class TuHaoAsyncTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			// 判断文件是否存在
			String url = params[0];
			String res = null;
			try {
				res = Util.getConfigXmlByHttp(url);
				// 定义工厂
				XmlPullParserFactory f = XmlPullParserFactory.newInstance();
				// 定义解析器
				XmlPullParser p = f.newPullParser();
				// 获取xml输入数据
				// p.setInput(new InputStreamReader(conn.getInputStream()));
				if (Util.isEmptyStr(res)) {
					throw new NullPointerException("http访问异常!");
				}
				p.setInput(new StringReader(res));
				ItemInfo itemInfo = null;
				// 解析事件
				int eventType = p.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						itemInfos = new ArrayList<ItemInfo>();
						break;
					case XmlPullParser.START_TAG:
						String tagName = p.getName();
						if (tagName.equals("status")) {
							String status = p.nextText();
						} else if (tagName.equals("statusnote")) {
							String statusNote = p.nextText();
						} else if (tagName.equals("element")) {
							itemInfo = new ItemInfo();
						}

						if (itemInfo != null) {
							if (tagName.equals("rank_num")) {
								itemInfo.rankId = Integer
										.parseInt(p.nextText());
							} else if (tagName.equals("uid")) {
								itemInfo.uid = Integer.parseInt(p.nextText());
							} else if (tagName.equals("guid")) {
								itemInfo.guid = Long.parseLong(p.nextText());
							} else if (tagName.equals("nick_name")) {
								itemInfo.nickName = p.nextText();
							} else if (tagName.equals("score")) {
								itemInfo.num = Integer.parseInt(p.nextText());
							} else if (tagName.equals("area_id")) {
								// try{
								// itemInfo.areaId =
								// Short.parseShort(p.nextText());
								// }catch(Exception e){
								// e.printStackTrace();
								// }
							} else if (tagName.equals("play_id")) {
								itemInfo.playId = Byte.parseByte(p.nextText());
							} else if (tagName.equals("post_date")) {
								itemInfo.date = p.nextText();
							} else if (tagName.equals("avatar_id")) {
								itemInfo.faceId = Integer
										.parseInt(p.nextText());
							} else if (tagName.equals("sex")) {
								itemInfo.gender = Byte.parseByte(p.nextText());
							} else if (tagName.equals("vip_id")) {
								itemInfo.vipId = Byte.parseByte(p.nextText());
							} else if (tagName.equals("is_expires")) {
								itemInfo.isExpires = Boolean.parseBoolean(p
										.nextText());
							}
						}

						break;
					case XmlPullParser.END_TAG:
						if (p.getName().equals("element")) {
							itemInfos.add(itemInfo);
							itemInfo = null;
						}
						break;
					default:
						break;
					}
					// 用next方法处理下一个事件，否则会造成死循环。
					// eventType = p.nextTag();
					eventType = p.next();
				}

			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return -1;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return -1;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return -1;
			} catch (NullPointerException e) {
				e.printStackTrace();
				return -1;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			// 0-get itemInfos success
			busy.setVisibility(View.GONE);
			if (result == 0) {
				if (!itemInfos.isEmpty()) {
					rankMain.setVisibility(View.VISIBLE);
					TuHaoFragment.this.setListAdapter(new TuHaoAdapter(mAct,
							itemInfos, mSetImageViewListener));
					for (ItemInfo itemInfo : itemInfos) {
						if (itemInfo.uid == user.userID) {
							curUserRank = itemInfo.rankId;
						}
					}

					if (curUserRank > 50 || curUserRank <= 0) { // only show the
																// first 50
																// players
						item.rankId_2.setImageDrawable(mResources
								.getDrawable(R.drawable.no_rank));
						item.rankId_1.setImageDrawable(null);
					} else {
						// 显示当前用户的排行
						mSetImageViewListener.setImageViewDrawable(
								item.rankId_2, item.rankId_1, curUserRank);
						// 已经得出了自己排行，剩下的全是其他用户排名，直接忽略
						curUserRank = 0;
					}
					item.playerName.setText(user.nickName);
					UtilHelper.setUserBeanView(item.beanCount, user.getBean());
					UtilHelper.setVipView(item.userVipLevel,
							user.getVipLevel(), !user.isVip());
					item.btnToWin.setText(mResources
							.getString(R.string.tu_hao_tip));
					item.btnToWin
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// 充值冲榜 ->快捷购买
									PayUtils.showBuyDialog(mAct,
											FastBuyModel.propId,
											FastBuyModel.isFastBuyConfirm, "",
											"");
								}
							});
					/* 0-女 1-男 */
					if (user.gender == 0) {
						item.gender.setImageResource(R.drawable.female);
					} else {
						item.gender.setImageResource(R.drawable.male);
					}
				} else {
					tip.setVisibility(View.VISIBLE);
					rankMain.setVisibility(View.GONE);
					tip.setText(mResources
							.getString(R.string.cur_rank_info_empty));
				}

			} else {
				// tips get itemInfos fail
				tip.setVisibility(View.VISIBLE);
				rankMain.setVisibility(View.GONE);
			}
		}

	}
}
