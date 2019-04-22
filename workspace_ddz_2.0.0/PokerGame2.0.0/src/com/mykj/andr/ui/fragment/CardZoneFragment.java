package com.mykj.andr.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinaMobile.m;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mingyou.login.LoginSocket;
import com.mingyou.login.struc.DownLoadListener;
import com.mingyou.login.struc.NotifiyDownLoad;
import com.mingyou.login.struc.VersionInfo;
import com.mykj.andr.headsys.HeadManager;
import com.mykj.andr.logingift.LoginGiftDialog;
import com.mykj.andr.lottoround.LottoRoundActivity;
import com.mykj.andr.model.ActionInfo;
import com.mykj.andr.model.AllNodeData;
import com.mykj.andr.model.CarouselData;
import com.mykj.andr.model.GoodsItem;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.NewUIDataStruct;
import com.mykj.andr.model.NodeData;
import com.mykj.andr.model.UserCenterData;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.newsflash.NewsFlashManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.provider.AddGiftProvider;
import com.mykj.andr.pay.provider.PromotionGoodsProvider;
import com.mykj.andr.pay.ui.SinglePayDialog;
import com.mykj.andr.provider.ActionInfoProvider;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.andr.provider.NewCardZoneProvider;
import com.mykj.andr.provider.ScrollDataProvider;
import com.mykj.andr.provider.UserCenterProvider;
import com.mykj.andr.ui.ActionActivity;
import com.mykj.andr.ui.CustomActivity;
import com.mykj.andr.ui.CustomDialog;
import com.mykj.andr.ui.FirstLoginedDialog;
import com.mykj.andr.ui.MeinvDialog;
import com.mykj.andr.ui.SettingDialog;
import com.mykj.andr.ui.UpdateDialog;
import com.mykj.andr.ui.WelcomeDialog;
import com.mykj.andr.ui.adapter.MyCardZoneViewPagerAdapter;
import com.mykj.andr.ui.adapter.MyCardZoneViewPagerAdapter.OnNodeClickListener;
import com.mykj.andr.ui.fragment.LoadingFragment.NodeDataType;
import com.mykj.andr.ui.tabactivity.ExchangeTabActivity;
import com.mykj.andr.ui.tabactivity.MarketActivity;
import com.mykj.andr.ui.tabactivity.RankListActivity;
import com.mykj.andr.ui.tabactivity.ServerCenterTabActivity;
import com.mykj.andr.ui.tabactivity.UserCenterActivity;
import com.mykj.andr.ui.widget.ActionInfoWidget;
import com.mykj.andr.ui.widget.CardZoneProtocolListener;
import com.mykj.andr.ui.widget.GameRoomAssociatedWidget;
import com.mykj.andr.ui.widget.HallAssociatedWidget;
import com.mykj.andr.ui.widget.Interface.ActionInfoInterface;
import com.mykj.andr.ui.widget.Interface.GameRoomAssociatedInterface;
import com.mykj.andr.ui.widget.Interface.HallAssociatedInterface;
import com.mykj.andr.ui.widget.Interface.LoginAssociatedInterface;
import com.mykj.andr.ui.widget.Interface.OnArticleSelectedListener;
import com.mykj.andr.ui.widget.LoginAssociatedWidget;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.control.carousel.Carousel;
import com.mykj.control.carousel.CarouselAdapter;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.GlobalFiexParamer;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.ddz.api.UC;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.DialogMsgQueue;
import com.mykj.game.utils.DialogMsgQueue.DialogMessage;
import com.mykj.game.utils.DialogMsgQueue.OnReceiveMsgListener;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CardZoneFragment extends FragmentModel implements OnClickListener,
		OnReceiveMsgListener {
	public final static String TAG = "CardZoneFragment";

	private Activity mAct;

	private Resources mResource;

	// ---------------------------UI控件--------------------------------------------------

	/**
	 * o 头像 *
	 */
	public ImageView ivFace;

	/**
	 * 昵称 *
	 */
	private TextView tvuser_name;

	/**
	 * 乐豆 *
	 */
	private TextView tvuser_bean;

	/** 元宝 */
	// private TextView tvYuanBao;

	/**
	 * 签到
	 */
	private Button ivSignGift;

	private ImageButton ivQuickBuy;
	/**
	 * 客服
	 */
	private Button btnServer;

	/**
	 * 消息
	 */
	private Button btnMsg;

	private Button btnSetting;

	/**
	 * 活动 *
	 */
	private Button btnAction;

	/**
	 * 兑换
	 */
	private Button btnExchange;

	/**
	 * 商城
	 */
	private Button btnMarket;

	/**
	 * 排行榜
	 */
	private Button btnRank;

	/**
	 * 旋转木马
	 */
	private Carousel mCarousel;

	private TextView tvOnline;

	private TextView msgTip;

	private Button btnLotto;

	private View root;
	private ImageView bgPic;
	private View layer1;
	private View layer2;
	private ImageView btnBack;
	private ImageView btnLeft;
	private ImageView btnRight;
	private TextView tvTitle;
	private ImageView picTitle;
	private ImageButton btnQuickgame;
	private Button firstCharge;
	private RelativeLayout contaner; // 容器，根据需要装载卡片或者列表
	private RelativeLayout listContaner;
	private ImageView firstChargeFlash; // 首充按钮光效
	private ImageView lottoFlash; // 抽奖按钮光效

	// private ImageView cardContainerBg;
	private ImageView cardMan;
	/**
	 * 登录相关：接口 *
	 */
	public LoginAssociatedInterface loginAssociated = null;

	/**
	 * 大厅UI相关：接口 *
	 */
	public HallAssociatedInterface hallAssociated = null;

	/**
	 * 与房间相关：接口 *
	 */
	public GameRoomAssociatedInterface gameRoomAssociated = null;

	/**
	 * 活动专区相关：接口 **
	 */
	public ActionInfoInterface actionInfoInterface = null;

	public OnArticleSelectedListener mListener;

	/**
	 * 大厅版本类型(cocos2d-x中为3) *
	 */
	public static final byte LOBBYTYPE = 3;

	// --------------------handler what begin--------------------
	private static final int HANDLER_MIX_QUERY_SUCCESS = 0;

	public static final int HANDLER_SIT_DOWN_FAIL = 1;
	public static final int HANDLER_USER_STATUS_SIT_DOWN_SUCCESS = 2;

	public static final int HANDLER_UPDATE_HEAD = 3;

	public static final int HANDLER_CHECK_UPDATE_COMPLETE = 4;

	private static final int HANDLER_UPDATE_FAIL = 5;
	public static final int HANDLER_GAME_PLAYER = 6; // 游戏玩家按钮显示处理
	private static final int HANDLER_GET_TICKET_SUCCESS = 7;

	public static final int HANDLER_SMALL_MONEYPKG = 8;
	// vip等级更新监听下发
	public static final int HANDLER_UPDATE_VIP = 9;
	public static final int HANDLER_UPDATE_FIRST_CHARGE = 10;

	private static final int HANDLER_CARDZONE_MAN_ANIM = 11;

	private static final int HANDLER_DIALOGMSG = 12;

	// --------------------handler what end--------------------

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mAct = activity;
		this.mResource = mAct.getResources();
		try {
			mListener = (OnArticleSelectedListener) activity;
			// mListener.onArticleSelected(cardZoneHandler); //老平台升级检查方式
			versionUpdate(cardZoneHandler); // 新平台升级检查方式

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initProtocol();// 初始化协议监听
		reqProtocol();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (cardZoneHandler.hasMessages(HANDLER_CARDZONE_MAN_ANIM)) {
			cardZoneHandler.removeMessages(HANDLER_CARDZONE_MAN_ANIM);
		}
		ScrollDataProvider.getInstance(mAct).removeScrollItems();
		DialogMsgQueue.getInstance().unregister();
	}

	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.cardzone_view, container, false);
		initViews(mView, inflater);
		checkSign();
		setOnClickListener();
		DialogMsgQueue.getInstance().register(this);
		return mView;
	}

	private String conversionToWan(long num) {
		if (num < 10000) {
			return "" + num;
		} else {
			long zheng = num / 10000;
			int xiao = (int) (num % 10000);
			if (xiao > 999) {
				return zheng + "." + (xiao / 1000) + "万";
			} else {
				return zheng + "万";
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		System.gc();
		// 请求用户乐豆
		FiexedViewHelper.getInstance().requestUserBean(cardZoneHandler);
		UserInfo user = HallDataManager.getInstance().getUserMe();
		if (user.gender >= 0 && user.nickName != null) {
			setCardZoneUserInfo(user.gender, user.nickName);
		}
		// tvOnline.setText("在线：" +
		// conversionToWan(AllNodeData.getInstance(mAct).getSumPerson()) + "人");

		ivFace.setImageDrawable(HeadManager.getInstance().getZoneHead(mAct,
				user.userID, user.faceID));
		HeadManager.getInstance().setUpdateHanler(cardZoneHandler,
				HANDLER_UPDATE_HEAD);
		setMsgTip();
		// checkShowFirstGift();
		checkTopBtnFlashAnim();
		// AnimationDrawable drawAnim = (AnimationDrawable)
		// btnQuickgame.getDrawable();
		// drawAnim.start();
		mView.findViewById(R.id.quickgameText).startAnimation(
				AnimationUtils.loadAnimation(mAct,
						R.anim.scale_givedou_tips_anim));
		if (!cardZoneHandler.hasMessages(HANDLER_CARDZONE_MAN_ANIM)) {
			cardZoneHandler.sendEmptyMessage(HANDLER_CARDZONE_MAN_ANIM);
		}
	}

	private boolean needShowSign = true;

	private void checkSign() {
		String today = getTodayKey();
		String lastSign = Util.getStringSharedPreferences(mAct, "signDate", "");
		if (needShowSign && !today.equals(lastSign)) {
			DialogMsgQueue.getInstance().add(DialogMsgQueue.DIALOG_LOGIN_GIFT);
			DialogMsgQueue.getInstance().add(DialogMsgQueue.DIALOG_DISCOUNT);
			Util.setStringSharedPreferences(mAct, "signDate", today);
		}
		needShowSign = false;
	}

	String getTodayKey() {
		Calendar c = Calendar.getInstance();
		String today = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
				+ c.get(Calendar.DATE);
		return today + HallDataManager.getInstance().getUserMe().userID;
	}

	public void checkTopBtnFlashAnim() {
		String today = getTodayKey();
		// String lastFirstCharge = Util.getStringSharedPreferences(mAct,
		// "firstChargeDate", "");
		String lastLotto = Util.getStringSharedPreferences(mAct, "lottoDate",
				"");
		AnimationDrawable drawAnim;
		drawAnim = (AnimationDrawable) firstChargeFlash.getDrawable();
		// if (today.equals(lastFirstCharge)) {
		// try {
		// firstChargeFlash.setVisibility(View.INVISIBLE);
		// drawAnim.stop();
		// } catch (Exception e) {
		// }
		// } else {
		drawAnim.start();
		// }
		drawAnim = (AnimationDrawable) lottoFlash.getDrawable();
		if (today.equals(lastLotto)) {
			try {
				lottoFlash.setVisibility(View.INVISIBLE);
				drawAnim.stop();
			} catch (Exception e) {
			}
		} else {
			drawAnim.start();
		}
	}

	public void clearFirstChargeFlash() {
		String today = getTodayKey();
		String lastFirstCharge = Util.getStringSharedPreferences(mAct,
				"firstChargeDate", "");
		if (!today.equals(lastFirstCharge)) {
			Util.setStringSharedPreferences(mAct, "firstChargeDate", today);
		}
		if (firstChargeFlash.getVisibility() == View.VISIBLE)
			try {
				firstChargeFlash.setVisibility(View.INVISIBLE);
				AnimationDrawable drawAnim = (AnimationDrawable) firstChargeFlash
						.getDrawable();
				drawAnim.stop();
			} catch (Exception e) {
			}
	}

	public void clearLottoFlash() {
		String today = getTodayKey();
		String lastLotto = Util.getStringSharedPreferences(mAct, "lottoDate",
				"");
		if (!today.equals(lastLotto)) {
			Util.setStringSharedPreferences(mAct, "lottoDate", today);
		}
		if (lottoFlash.getVisibility() == View.VISIBLE)
			try {
				lottoFlash.setVisibility(View.INVISIBLE);
				AnimationDrawable drawAnim = (AnimationDrawable) lottoFlash
						.getDrawable();
				drawAnim.stop();
			} catch (Exception e) {
			}
	}

	public void setMsgTip() {
		if (msgTip != null) {
			int nfUnread = NewsFlashManager.getInstance().getUnreadSum();
			if (nfUnread <= 0) {
				msgTip.setVisibility(View.GONE);
			} else {
				msgTip.setText("" + nfUnread);
				msgTip.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public int getFragmentTag() {
		return FiexedViewHelper.CARDZONE_VIEW;
	}

	void goBack() {
		if (curShow == GRID_PAGE) {
			showCardPage();
			lastClickTime = 0; // 重置按键响应
		} else {
			boolean isPromption = false;
			if (HallDataManager.getInstance().getUserMe().getBean() < PromotionGoodsProvider
					.getInstance().lowBeanShow) {
				isPromption = PayUtils.showPromotionDialog(mAct,
						AddGiftProvider.getInstance().adviceGoodID, "",
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								UtilHelper.showExitDialog(mAct);
							}
						}, new OnKeyListener() {

							@Override
							public boolean onKey(DialogInterface arg0,
									int arg1, KeyEvent arg2) {
								UtilHelper.showExitDialog(mAct);
								return false;
							}
						});
			}
			if (!isPromption) {
				UtilHelper.showExitDialog(mAct);
			}
			// UtilHelper.showExitDialog(mAct, new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// // 可能有快捷购买，更多游戏
			// if (v.getId() == R.id.img_quick_buy) {
			// Log.e(TAG, "img_quick_buy");
			//
			// } else if (v.getId() == R.id.img_more_games) {
			// // 交叉推广，记录渠道号
			// ChannelDataMgr.getInstance().writeChannelToSDCard();
			//
			// // wanghj 2013-04-16 跳转到更多游戏界面，不是wap网页
			// Intent intent = new Intent(mAct, MoregameActivity.class);
			// startActivity(intent);
			// }
			// }
			// }, new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // 若没有弹出退出强弹框，则退出游戏，否则由强弹框处理
			// if (!FiexedViewHelper.getInstance()
			// .showExitSystemPopDialog()) {
			// FiexedViewHelper.getInstance().exitGame();
			// }
			//
			// }
			// }, null);
		}
	}

	@Override
	public void onBackPressed() {
		goBack();
	}

	/**
	 * 初始化协议监听
	 */
	private void initProtocol() {
		loginAssociated = LoginAssociatedWidget.getInstance();
		hallAssociated = HallAssociatedWidget.getInstance();

		gameRoomAssociated = GameRoomAssociatedWidget.getInstance(mAct);
		gameRoomAssociated.receiveUserStatus();
		gameRoomAssociated.receiveRoomConfigData();

		actionInfoInterface = ActionInfoWidget.getInstance();

	}

	/**
	 * 请求协议
	 */
	private void reqProtocol() {
		int userId = FiexedViewHelper.getInstance().getUserId();
		if (hallAssociated != null) {
			// TcpClient有变动，暂时屏蔽2012-12-10
			hallAssociated.requestSystemMessage(userId);
		}
		CardZoneProtocolListener.getInstance(mAct).protocolInit(); // 添加监听协议

		// 请求活动中心数据,//暂时屏蔽2012-12-10
		actionInfoInterface.requestActionInfoList();

		if (loginAssociated != null) {
			// 每次登陆成功时候都请求断线信息(非每次启动CardZoneActivity时请求)
			loginAssociated.breakLine(LOBBYTYPE);
		}
		// requestMixtureInfoList(); //道具合成
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void initViews(View view, LayoutInflater inflater) {
		/** 头像 */
		ivFace = (ImageView) view.findViewById(R.id.imgUserface);

		UserInfo userInfo = HallDataManager.getInstance().getUserMe();
		/** 昵称 */
		tvuser_name = (TextView) view.findViewById(R.id.tvuser_name);
		tvuser_name.setTextColor(userInfo.nickColor | 0xff000000);
		tvuser_name.setText(userInfo.nickName);

		/** 乐豆 */
		tvuser_bean = (TextView) view.findViewById(R.id.tvuser_bean);
		setUserBean(userInfo.getBean());
		/** 元宝 */
		// tvYuanBao = (TextView) view.findViewById(R.id.tvYuanBao);
		// tvYuanBao.setText("200");

		/** 签到 */
		ivSignGift = (Button) view.findViewById(R.id.ivSignGift);
		// 2013-4-18推荐快捷购买
		// AnimationDrawable adrawable = (AnimationDrawable)
		// ivQuickBuyTop.getBackground();
		// adrawable.start();

		ivQuickBuy = (ImageButton) view.findViewById(R.id.ivQuickBuy);
		/** 客服 */
		btnServer = (Button) view.findViewById(R.id.btnServer);

		/** 消息 */
		btnMsg = (Button) view.findViewById(R.id.btnMsg);

		/** 设置 */
		btnSetting = (Button) view.findViewById(R.id.btnSetting);

		/** 活动 **/
		btnAction = (Button) view.findViewById(R.id.btnAction);

		/** 兑换 */
		btnExchange = (Button) view.findViewById(R.id.btnExchange);

		/** 商城 */
		btnMarket = (Button) view.findViewById(R.id.btnMarket);

		/** 排行榜 */
		btnRank = (Button) view.findViewById(R.id.btnRank);

		/** 在线人数 */
		tvOnline = (TextView) view.findViewById(R.id.tvOnline);

		// root = view.findViewById(R.id.root);
		bgPic = (ImageView) view.findViewById(R.id.bg_pic);
		layer1 = view.findViewById(R.id.layer1);
		layer2 = view.findViewById(R.id.layer2);
		btnBack = (ImageView) view.findViewById(R.id.back);
		btnLeft = (ImageView) view.findViewById(R.id.toLeft);
		btnRight = (ImageView) view.findViewById(R.id.toRight);
		tvTitle = (TextView) view.findViewById(R.id.title);
		picTitle = (ImageView) view.findViewById(R.id.titlePic);
		btnQuickgame = (ImageButton) view.findViewById(R.id.btnQuickgame);
		firstCharge = (Button) view.findViewById(R.id.btnFirstcharge);
		msgTip = (TextView) view.findViewById(R.id.msg_tip);
		// cardContainerBg =
		// (ImageView)view.findViewById(R.id.card_container_bg);
		cardMan = (ImageView) view.findViewById(R.id.main_man);
		cardMan.setOnClickListener(this);
		btnLotto = (Button) view.findViewById(R.id.btnLotto);
		// ScrollDataProvider.getInstance(mAct).initialize( //喜报消息
		// view.findViewById(R.id.tvBroadcast));
		// //喜报消息
		ScrollDataProvider.getInstance(mAct).initialize(
				view.findViewById(R.id.tvBroadcast));
		contaner = (RelativeLayout) view.findViewById(R.id.relContaner);
		listContaner = (RelativeLayout) view.findViewById(R.id.listContaner);
		firstChargeFlash = (ImageView) view.findViewById(R.id.charge_flash);
		lottoFlash = (ImageView) view.findViewById(R.id.lotto_flash);
		List<CarouselData> list = new ArrayList<CarouselData>();

		// CarouselData cmmvideo=new CarouselData();
		// cmmvideo.drawable=getResources().getDrawable(R.drawable.page_mm_video);
		// list.add(cmmvideo);
		//
		// CarouselData cfruit=new CarouselData();
		// cfruit.drawable=getResources().getDrawable(R.drawable.page_fruit_machines);
		// list.add(cfruit);
		//
		// CarouselData crank=new CarouselData();
		// crank.drawable=getResources().getDrawable(R.drawable.page_zone_rank);
		// list.add(crank);

		int total = 0;
		List<NewUIDataStruct> dataList = NewCardZoneProvider.getInstance()
				.getNewUIDataList();
		if (dataList != null && dataList.size() != 0) {
			total = dataList.size();
			NewCardZoneProvider.getInstance().setIndex(-1);// 设置显示二级分区索引，用于快速开始逻辑
		} else {
			// 出错了
		}
		//
		for (int i = 0; i < total; i++) {
			NewUIDataStruct node = dataList.get(i);
			CarouselData crank = new CarouselData();
			if ("比赛场".equals(node.Name)) {
				crank.drawable = getResources().getDrawable(
						R.drawable.card_item_bisai);
			} else if ("美女主播".equals(node.Name)) {
				crank.drawable = getResources().getDrawable(
						R.drawable.card_item_meinv);
			} else if ("经典场".equals(node.Name)) {
				crank.drawable = getResources().getDrawable(
						R.drawable.card_item_jingdian);
			} else if ("癞子场".equals(node.Name)) {
				crank.drawable = getResources().getDrawable(
						R.drawable.card_item_laizi);
			} else {
				crank.drawable = getResources().getDrawable(
						R.drawable.card_item_jingdian);
			}

			list.add(crank);
		}
		//
		mCarousel = new Carousel(mAct, list);

		RelativeLayout.LayoutParams Lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		Lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		mCarousel.setLayoutParams(Lp);
		contaner.setGravity(Gravity.CENTER);
		contaner.addView(mCarousel);
		mCarousel
				.setOnItemClickListener(new com.mykj.control.carousel.CarouselAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(CarouselAdapter<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						FiexedViewHelper.getInstance().playKeyClick();
						showGridPage(position);
					}
				});
		// 切换首充和特惠礼包的按钮
		setChargeKind(PayUtils.getPromptionType());
		// initCard(inflater);
		showCardPage();
	}

	/**
	 * 卡片点击按钮监听，卡片没有id
	 */
	private View.OnClickListener cardListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			FiexedViewHelper.getInstance().playKeyClick();
			// TODO Auto-generated method stub
			String tag = (String) v.getTag();
			int Index = Integer.parseInt(tag);
			showGridPage(Index);
		}
	};

	/**
	 * 设置按键监听
	 */
	private void setOnClickListener() {
		/** 头像 **/
		ivFace.setOnClickListener(this);
		/** 签到 */
		ivSignGift.setOnClickListener(this);
		ivQuickBuy.setOnClickListener(this);
		/** 客服 */
		btnServer.setOnClickListener(this);
		/** 消息 */
		btnMsg.setOnClickListener(this);
		/** 设置 */
		btnSetting.setOnClickListener(this);
		/** 活动 **/
		btnAction.setOnClickListener(this);
		/** 兑换 */
		btnExchange.setOnClickListener(this);
		/** 商城 */
		btnMarket.setOnClickListener(this);
		/** 每日 */
		btnRank.setOnClickListener(this);
		/** 列表界面返回 */
		btnBack.setOnClickListener(this);
		/** 比赛场列表界面向左切换 */
		btnLeft.setOnClickListener(this);
		/** 比赛场列表界面向右切换 */
		btnRight.setOnClickListener(this);
		/** 快速开始 */
		btnQuickgame.setOnClickListener(this);
		/** 首充 */
		firstCharge.setOnClickListener(this);
		/** 抽奖机 */
		btnLotto.setOnClickListener(this);
		mView.findViewById(R.id.quickBuyExpand).setOnClickListener(this);
	}

	private final int CARD_PAGE = 0;
	private final int GRID_PAGE = 1;
	private int curShow = CARD_PAGE; // 当前是卡片界面还是列表界面
	private long lastClickTime = 0; // 上次点击时间
	// CardZoneViewPagerAdapter pageViewAdapter;
	// ViewPager pageView;
	ViewPager page;
	int curPageIndex = 0;

	/**
	 * 显示列表界面，参数需要改变
	 * 
	 * @param index
	 */
	public void showGridPage(final int index) {

		List<NodeData> subList = null;
		List<NewUIDataStruct> dataList = NewCardZoneProvider.getInstance()
				.getNewUIDataList();
		if (dataList != null && dataList.size() > index) {
			subList = dataList.get(index).mSubNodeDataList;
			// 美女视频直接进入
			if (dataList.get(index).Type == NodeData.NODE_MM_VIDEO) {
				if (subList != null && subList.size() > 0) {
					invokeSubItem(subList.get(0));
				} else {
					Util.displayCenterToast(btnQuickgame, "敬请期待");
				}
				return;
			}
			NewCardZoneProvider.getInstance().setIndex(index);// 设置显示二级分区索引，用于快速开始逻辑

		} else {
			// 出错了
		}

		curShow = GRID_PAGE;
		bgPic.setImageResource(R.drawable.zone_bg2);
		layer2.setVisibility(View.VISIBLE);
		layer1.setVisibility(View.GONE);
		cardMan.setVisibility(View.INVISIBLE);
		listContaner.removeAllViews();

		// 改变上部显示
		// RelativeLayout topLayoutCard = (RelativeLayout) mView
		// .findViewById(R.id.cardzone_main_toplayout_card);
		// RelativeLayout topLayoutGrid = (RelativeLayout) mView
		// .findViewById(R.id.cardzone_main_toplayout_grid);
		// topLayoutCard.setVisibility(View.GONE);
		// topLayoutGrid.setVisibility(View.VISIBLE);
		//
		// title.setText(dataList.get(index).Name);
		// title.getPaint().setFakeBoldText(true);
		// online.setVisibility(View.GONE);

		String name = dataList.get(index).Name;
		if (name.contains("经典")) {
			tvTitle.setVisibility(View.INVISIBLE);
			picTitle.setVisibility(View.VISIBLE);
			picTitle.setImageResource(R.drawable.cardzone_title_jingdianchang);
			AnalyticsUtils.onClickEvent(mAct, "017");
		} else if (name.contains("癞子")) {
			tvTitle.setVisibility(View.INVISIBLE);
			picTitle.setVisibility(View.VISIBLE);
			picTitle.setImageResource(R.drawable.cardzone_title_laizichang);
			AnalyticsUtils.onClickEvent(mAct, "018");
		} else if (name.contains("比赛")) {
			tvTitle.setVisibility(View.INVISIBLE);
			picTitle.setVisibility(View.VISIBLE);
			picTitle.setImageResource(R.drawable.cardzone_title_bisaichang);
			AnalyticsUtils.onClickEvent(mAct, "019");
		} else if (name.contains("美女")) {
			tvTitle.setVisibility(View.INVISIBLE);
			// tvTitle.setText(name);
			picTitle.setVisibility(View.VISIBLE);
			picTitle.setImageResource(R.drawable.cardzone_title_meinvchang);
			AnalyticsUtils.onClickEvent(mAct, "020");
		}
		btnLeft.setVisibility(View.INVISIBLE);
		if (subList == null || subList.size() <= 4) {
			btnRight.setVisibility(View.INVISIBLE);
		} else {
			btnRight.setVisibility(View.VISIBLE);
		}

		if (subList == null || subList.size() == 0) { // 有问题，不加载数据
			return;
		}

		if (subList.get(0).Type != 109 || true) {
			/** 非比赛场,里面是个GridView */
			// 动态添加列表
			page = new ViewPager(mAct);
			curPageIndex = 0;
			MyCardZoneViewPagerAdapter adp = new MyCardZoneViewPagerAdapter(
					subList, mAct);
			adp.setOnItemClickListener(new OnNodeClickListener() {

				@Override
				public void onClick(NodeData data) {
					// TODO Auto-generated method stub
					// long curTime = System.currentTimeMillis();
					// if (curTime - lastClickTime < 2000) {
					// return;
					// }
					// lastClickTime = curTime;
					// CardZoneProtocolListener.getInstance(mAct).invokeListItem(
					// data, true);
					FiexedViewHelper.getInstance().playKeyClick();
					invokeSubItem(data);
				}
			});

			// GridView grd = new GridView(mAct);
			RelativeLayout.LayoutParams grdLP = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			// tvTitle.setText(dataList.get(index).Name);

			page.setLayoutParams(grdLP);
			page.setAdapter(adp);
			page.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					// TODO Auto-generated method stub
					if (position <= 0) {
						btnLeft.setVisibility(View.INVISIBLE);
					} else {
						btnLeft.setVisibility(View.VISIBLE);
					}
					if (position >= page.getChildCount() - 1) {
						btnRight.setVisibility(View.INVISIBLE);
					} else {
						btnRight.setVisibility(View.VISIBLE);
					}
					curPageIndex = position;
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}
			});
			// grd.setLayoutParams(grdLP);
			// grd.setNumColumns(2);
			// int px = DensityConst.getPx(5);
			// grd.setHorizontalSpacing(px);
			// grd.setVerticalSpacing(px);
			// grd.setCacheColorHint(0);
			// grd.setSelector(new ColorDrawable(0));
			// grd.setAdapter(new NewCardZoneGridViewAdapter(mAct, subList));
			// grd.setDrawSelectorOnTop(false);
			// grd.setOnItemClickListener(new AdapterView.OnItemClickListener()
			// {
			//
			// @Override
			// public void onItemClick(AdapterView<?> parent, View view,
			// int position, long id) {
			// // TODO Auto-generated method stub
			// long curTime = System.currentTimeMillis();
			// if (curTime - lastClickTime < 2000) {
			// return;
			// }
			// lastClickTime = curTime;
			// // FiexedViewHelper.getInstance().playKeyClick();
			// NodeData node = NewCardZoneProvider.getInstance()
			// .getNewUIDataList().get(index).mSubNodeDataList
			// .get(position);
			// CardZoneProtocolListener.getInstance(mAct).invokeListItem(
			// node, true);
			// }
			//
			// });
			// listContaner.addView(grd);
			listContaner.addView(page);
		}
		// else{
		// /** 比赛场，里面是个pageView，每个page都是个GridView */
		// List<List<NodeData>> mLList =
		// AllNodeData.getInstance(mAct).getMatchList();
		// if(mLList.size() == 0){ //没有数据
		// return;
		// }
		//
		// List<View> vList = new ArrayList<View>();
		// for(int i = 0; i<mLList.size();i++){
		// // 动态添加列表
		// GridView grd = new GridView(mAct);
		// RelativeLayout.LayoutParams grdLP = new RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.MATCH_PARENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// grd.setLayoutParams(grdLP);
		// grd.setNumColumns(2);
		// int px = DensityConst.getPx(5);
		// grd.setHorizontalSpacing(px);
		// grd.setVerticalSpacing(px);
		// grd.setCacheColorHint(0);
		// grd.setSelector(new ColorDrawable(0));
		// grd.setAdapter(new NewCardZoneGridViewAdapter(mAct, mLList.get(i)));
		// grd.setDrawSelectorOnTop(false);
		// grd.setTag(mLList.get(i));
		// grd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// long curTime = System.currentTimeMillis();
		// if (curTime - lastClickTime < 2000) {
		// return;
		// }
		// lastClickTime = curTime;
		// // FiexedViewHelper.getInstance().playKeyClick();
		// List<NodeData> nodeList = (List<NodeData>)parent.getTag();
		// // NodeData node = NewCardZoneProvider.getInstance()
		// // .getNewUIDataList().get(index).mSubNodeDataList
		// // .get(position);
		// if(nodeList != null && nodeList.size() > position){
		// CardZoneProtocolListener.getInstance(mAct).invokeListItem(
		// nodeList.get(position), true);
		// }
		// }
		//
		// });
		// vList.add(grd);
		// }
		// pageViewAdapter = new CardZoneViewPagerAdapter(vList);
		// pageView = new ViewPager(mAct);
		// RelativeLayout.LayoutParams pgLP = new RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.MATCH_PARENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// pageView.setLayoutParams(pgLP);
		// pageView.setAdapter(pageViewAdapter);
		// pageView.setOnPageChangeListener(new
		// ViewPager.OnPageChangeListener(){
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onPageSelected(int position) {
		// // TODO Auto-generated method stub
		// setPageTitle(position);
		//
		// }
		//
		// });
		// listContaner.addView(pageView);
		// setPageTitle(0);
		// }
		// if(subList.size() > 4 && !showTip[index]){
		// showTip[index]=true;
		// showTips();
		// }
	}

	private void invokeSubItem(NodeData data) {
		long curTime = System.currentTimeMillis();
		if (curTime - lastClickTime < 2000) {
			return;
		}
		lastClickTime = curTime;
		CardZoneProtocolListener.getInstance(mAct).invokeListItem(data, true);
	}

	// private void setPageTitle(int position){
	// if(position == 0){
	// btnLeft.setVisibility(View.INVISIBLE);
	// }else{
	// btnLeft.setVisibility(View.VISIBLE);
	// }
	// if(position < pageViewAdapter.getCount() - 1){
	// btnRight.setVisibility(View.VISIBLE);
	// }else{
	// btnRight.setVisibility(View.INVISIBLE);
	// }
	// try{
	// List<List<NodeData>> mLList =
	// AllNodeData.getInstance(mAct).getMatchList();
	// if(mLList!=null && mLList.size() > position)
	// tvTitle.setText(mLList.get(position).get(0).NameKey);
	// }catch (Exception e){}
	// }

	/**
	 * 显示卡片界面
	 */
	public void showCardPage() {
		curShow = CARD_PAGE;
		// root.setBackgroundResource(R.drawable.zone_bg);
		bgPic.setImageResource(R.drawable.zone_bg);
		layer1.setVisibility(View.VISIBLE);
		layer2.setVisibility(View.GONE);
		// cardContainerBg.setVisibility(View.VISIBLE);
		cardMan.setVisibility(View.VISIBLE);

	}

	/**
	 * 设置用户乐豆数
	 * 
	 * @param bean
	 */
	public void setUserBean(long bean) {
		UtilHelper.setUserBeanView(tvuser_bean, bean);
	}

	public void updateNickName() {
		try {
			UserInfo user = HallDataManager.getInstance().getUserMe();
			if (user.gender >= 0 && user.nickName != null) {
				setCardZoneUserInfo(user.gender, user.nickName);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 设置用户性别昵称
	 * 
	 * @param gender
	 * @param nickName
	 */
	private void setCardZoneUserInfo(byte gender, String nickName) {

		if (tvuser_name != null) {
			tvuser_name.setText(nickName);
		}

	}

	/**
	 * 各类点击事件
	 */
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		int id = v.getId();
		if (id == R.id.imgUserface) {
			mAct.startActivity(new Intent(mAct, UserCenterActivity.class));
			/** 点击 主界面-个人头像事件 */
			AnalyticsUtils.onClickEvent(mAct, "004");
		} else if (id == R.id.ivSignGift) { // 签到
			(new LoginGiftDialog(mAct)).show();
			// 快捷购买
			// UtilHelper.showBuyDialog(mAct, AppConfig.propId, false,
			// AppConfig.isConfirmon, AppConfig.ACTION_ZONE); // 2代表 分区【充】

			/** 点击获取乐豆 */
			AnalyticsUtils.onClickEvent(mAct, "006");
		} else if (id == R.id.ivQuickBuy || id == R.id.quickBuyExpand) { // 快捷购买,扩大区域，因为按钮太小
			// 快捷购买
			PayUtils.showBuyDialog(mAct, FastBuyModel.propId,
					FastBuyModel.isFastBuyConfirm, "", "");
		} else if (id == R.id.btnServer) {
			// ServerDialog dialog = new ServerDialog(mAct);
			// dialog.show();
			mAct.startActivity(new Intent(mAct, ServerCenterTabActivity.class));
			/** 点击 主界面-点击客服事件 */
			AnalyticsUtils.onClickEvent(mAct, "010");
		} else if (id == R.id.btnMsg) {
			// 跳转到消息盒子
			// mAct.startActivity(new Intent(mAct, MessageBoxActivity.class));
			NewsFlashManager.getInstance().displayNewsFlashDialog(mAct, btnMsg);
			/** 点击 主界面-点击消息盒子事件 */
			AnalyticsUtils.onClickEvent(mAct, "011");
		} else if (id == R.id.btnAction) {
			mAct.startActivity(new Intent(mAct, ActionActivity.class));
			/** 点击 主界面-点击活动事件 */
			AnalyticsUtils.onClickEvent(mAct, "014");
		} else if (id == R.id.btnExchange) { // 兑换
			// mAct.startActivity(new Intent(mAct, MarketActivity.class));
			mAct.startActivity(new Intent(mAct, ExchangeTabActivity.class));
			AnalyticsUtils.onClickEvent(mAct, "013");
		} else if (id == R.id.btnMarket) {
			mAct.startActivity(new Intent(mAct, MarketActivity.class));
			/** 统计点击商城事件 */
			AnalyticsUtils.onClickEvent(mAct, "016");
		} else if (id == R.id.btnRank) { // 排行榜
			// mAct.startActivity(new Intent(mAct, MarketActivity.class));
			// mAct.startActivity(new Intent(mAct, EverydayTaskActivity.class));
			mAct.startActivity(new Intent(mAct, RankListActivity.class));
			AnalyticsUtils.onClickEvent(mAct, "015");

		} else if (id == R.id.back) {
			goBack();
		} else if (id == R.id.toLeft) {
			if (page != null) {
				int cur = page.getCurrentItem();
				if (cur > 0) {
					page.setCurrentItem(cur - 1);
				}
			}
		} else if (id == R.id.toRight) {
			if (page != null) {
				int cur = page.getCurrentItem();
				if (cur < page.getChildCount() - 1) {
					page.setCurrentItem(cur + 1);
				}
			}
		} else if (id == R.id.btnQuickgame) { // 快速开始
			FiexedViewHelper.getInstance().quickGame();
			AnalyticsUtils.onClickEvent(mAct, "021");
		} else if (id == R.id.btnFirstcharge) { // 首充
			PayUtils.showPromotionDialog(mAct,
					AddGiftProvider.getInstance().adviceGoodID, "", null, null);
			AnalyticsUtils.onClickEvent(mAct, "007");
			// clearFirstChargeFlash();
		} else if (id == R.id.btnSetting) {// 设置

			new SettingDialog(mAct).show();
			AnalyticsUtils.onClickEvent(mAct, "012");
		} else if (id == R.id.btnLotto) {// 抽奖
			clearLottoFlash();
			mAct.startActivity(new Intent(mAct, LottoRoundActivity.class));
			AnalyticsUtils.onClickEvent(mAct, "009");
		} else if (id == R.id.main_man) {
			PayUtils.showPromotionDialog(mAct,
					AddGiftProvider.getInstance().adviceGoodID, "", null, null);
		}

	}

	/**
	 * 处理 ActionInfoInterface.HANDLER_ACT_QUERY_SUCCESS
	 * 
	 * @param msg
	 */
	private void handlerActQuerySuccess(Message msg) {
		if (msg.obj != null) {
			ActionInfoProvider.getInstance().setActionInfoProvider(
					(ActionInfo[]) msg.obj);
		}
	}

	/**
	 * 处理 CustomActivity.HANDLER_USER_BEAN
	 */
	private void handlerUserBean(Message msg) {
		/* 兑换之后，元宝或者话费券数目发生变化时，通知ExchangeTabActivity更新界面 */
		int bean = HallDataManager.getInstance().getUserMe().getBean();
		mAct.sendBroadcast(new Intent(ExchangeTabActivity.EXCHANGE_BROADCAST));
		UserCenterData userData = UserCenterProvider.getInstance()
				.getUserCenterData();
		if (userData != null) {
			userData.setLeDou(bean);
		}
		setUserBean(bean);
		if (FiexedViewHelper.getInstance().startGame) {
			FiexedViewHelper.getInstance().startGame = false;
			FiexedViewHelper.getInstance().quickGame();
		}
	}

	/**
	 * 处理 HallAssociatedInterface.HANDLER_BANKRUPTCY
	 */
	private void handlerBankruptcy(Message msg) {
		String beanContent = String.valueOf(msg.obj);
		if (beanContent != null && beanContent.length() > 0) {
			String status = UtilHelper.getTagStr(beanContent, "status");
			if (Integer.parseInt(status) == 0) {
				int mbean = UtilHelper.stringToInt(
						UtilHelper.getTagStr(beanContent, "bean"), 0);
				if (mbean > 0) {
					// 设置乐豆
					int b = HallDataManager.getInstance().getUserMe().getBean()
							+ mbean;

					HallDataManager.getInstance().getUserMe().setBean(b);
					setUserBean(b);

					String str = UtilHelper.getTagStr(beanContent, "msg");
					MeinvDialog dialog = new MeinvDialog(mAct, str,
							getString(R.string.continue_game),
							MeinvDialog.MEINV_TYPE_KAWAYI);

					dialog.setConfirmCallBack(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FiexedViewHelper.getInstance().playKeyClick();
							// TODO Auto-generated method stub
							FiexedViewHelper.getInstance().quickGame();
							AnalyticsUtils.onClickEvent(mAct, UC.EC_226);
						}
					});
					dialog.show();
				}
			}
		}
	}

	/**
	 * 处理 LoginAssociatedInterface.HANDLER_CUT_LINK_HAVE_DATA
	 * 
	 * @param msg
	 */
	private void handlerCutLinkHaveData(Message msg) {
		FiexedViewHelper.getInstance().skipToFragment(
				FiexedViewHelper.LOADING_VIEW);
		if (FiexedViewHelper.getInstance().loadingFragment != null) {
			FiexedViewHelper.getInstance().loadingFragment.setLoadingType(
					mResource.getString(R.string.ddz_return_game),
					NodeDataType.NODE_TYPE_NOT_DO);
		}
		int roomID = msg.arg1;
		if (gameRoomAssociated != null) {
			gameRoomAssociated.enterRoom(roomID);
		}
	}

	/**
	 * 处理 LoginAssociatedInterface.HANDLER_CUT_LINK_NOT
	 * 
	 * @param msg
	 */
	private void handlerCutLinkNot(Message msg) {
		FiexedViewHelper.getInstance().cutLinkFinish = true;
	}

	/**
	 * 处理 HANDLER_SIT_DOWN_FAIL
	 * 
	 * @param msg
	 */
	private void handlerSitDownFail(Message msg) {
		String errorMsg = (String) msg.obj;
		FiexedViewHelper.getInstance().skipToFragment(
				FiexedViewHelper.CARDZONE_VIEW);
		UtilHelper.showCustomDialog(mAct, errorMsg);
	}

	/**
	 * 处理 HANDLER_USER_STATUS_SIT_DOWN_SUCCESS
	 * 
	 * @param msg
	 */
	private void handlerUserStatusSitDownSuccess(Message msg) {
		/*********************************
		 * 坐下成功后切换到cocos2d-x游戏
		 ********************************/
		FiexedViewHelper.getInstance().skipToFragment(
				FiexedViewHelper.COCOS2DX_VIEW);
	}

	/**
	 * 处理 HANDLER_UPDATE_HEAD
	 * 
	 * @param msg
	 */
	private void handlerUpdateHead(Message msg) {
		UserInfo user = HallDataManager.getInstance().getUserMe();
		try {
			ivFace.setImageDrawable(HeadManager.getInstance().getZoneHead(mAct,
					user.userID, user.faceID));
		} catch (Exception e) {
		}
	}

	/**
	 * 处理 HANDLER_CHECK_UPDATE_COMPLETE
	 * 
	 * @param msg
	 */
	private void handlerCheckUpdateComplete(Message msg) {
		VersionInfo vi = (VersionInfo) msg.obj;
		if (vi != null) {
			AppConfig.setVersionInfo(vi);
		} else {
			vi = AppConfig.getVersionInfo();
		}
		if (FiexedViewHelper.getInstance().getCurFragment() == FiexedViewHelper.CARDZONE_VIEW
				&& FiexedViewHelper.getInstance().cutLinkFinish) {
			doUpdate(vi, true);
		} else {
			cardZoneHandler.sendEmptyMessageDelayed(
					HANDLER_CHECK_UPDATE_COMPLETE, 1500);
		}
	}

	/**
	 * 处理 HANDLER_UPDATE_FAIL
	 * 
	 * @param msg
	 */
	private void handlerUpdateFail(Message msg) {
		String err = mAct.getString(R.string.download_error);
		UtilHelper.showCustomDialog(mAct, err);

		// 强制升级失败，关闭客户端
		if (msg.arg1 == 1) {
			android.os.Handler hander = new android.os.Handler();
			hander.postDelayed(new Runnable() {
				@Override
				public void run() {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}, 2000);
		}
	}

	/**
	 * 处理 HANDLER_UPDATE_VIP
	 * 
	 * @param msg
	 */
	private void handlerUpdateVip(Message msg) {
		if (tvuser_name != null) {
			tvuser_name
					.setTextColor(HallDataManager.getInstance().getUserMe().nickColor | 0xff000000);
		}
	}

	/**
	 * 处理 HANDLER_CARDZONE_MAN_ANIM
	 * 
	 * @param msg
	 */
	private void handlerCardzoneManAnim(Message msg) {
		int arg1 = msg.arg1;
		int time = 0;
		if (arg1 % 2 == 1) {
			cardMan.setImageResource(R.drawable.cardzone_main_man2);
			time = 300;
		} else {
			cardMan.setImageResource(0);
			time = 2000;
		}
		arg1++;
		cardZoneHandler.sendMessageDelayed(cardZoneHandler.obtainMessage(
				HANDLER_CARDZONE_MAN_ANIM, arg1, 0), time);
	}

	/**
	 * 处理 HANDLER_DIALOGMSG
	 * 
	 * @param msg
	 */
	private void handlerDialogMsg(Message msg) {
		onReceive((DialogMessage) msg.obj);
	}

	// 速配Handler处理
	@SuppressLint("HandlerLeak")
	public Handler cardZoneHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ActionInfoInterface.HANDLER_ACT_QUERY_SUCCESS:
				handlerActQuerySuccess(msg);
				break;
			case CustomActivity.HANDLER_USER_BEAN: // 获取用户乐豆
				handlerUserBean(msg);
				break;
			case HallAssociatedInterface.HANDLER_BANKRUPTCY: // 破产赠送乐豆
				handlerBankruptcy(msg);
				break;
			case LoginAssociatedInterface.HANDLER_CUT_LINK_HAVE_DATA: // 有断线重连数据
				handlerCutLinkHaveData(msg);
				break;
			case LoginAssociatedInterface.HANDLER_CUT_LINK_NOT: // 没有断线重连的数据
				handlerCutLinkNot(msg);
				break;
			case HANDLER_MIX_QUERY_SUCCESS:
				break;
			case HANDLER_SIT_DOWN_FAIL: // 用户坐下失败
				handlerSitDownFail(msg);
				break;
			case HANDLER_USER_STATUS_SIT_DOWN_SUCCESS: // 坐下成功，下发用户信息后处理(跳转到游戏界面)
				handlerUserStatusSitDownSuccess(msg);
				break;
			case HANDLER_UPDATE_HEAD:
				handlerUpdateHead(msg);
				break;
			case HANDLER_CHECK_UPDATE_COMPLETE:
				handlerCheckUpdateComplete(msg);
				break;
			case HANDLER_UPDATE_FAIL:
				handlerUpdateFail(msg);
				break;
			case HANDLER_GAME_PLAYER:
				// 游戏玩家功能去除
				break;
			case HANDLER_GET_TICKET_SUCCESS:
				break;
			case HANDLER_UPDATE_VIP:// 更新vip等级
				handlerUpdateVip(msg);
				break;
			case HANDLER_UPDATE_FIRST_CHARGE: {
				checkShowFirstGift();
			}
				break;
			case HANDLER_CARDZONE_MAN_ANIM: // 处理人物动画
				handlerCardzoneManAnim(msg);
				break;
			case HANDLER_DIALOGMSG:
				handlerDialogMsg(msg);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @Title: showQuickBuyDialog
	 * @Description: 弹出快捷购买对话框，
	 * @version: 2012-12-28 下午09:41:10
	 */
	public void showQuickBuyDialog(int propID, String proTitle,
			String proMessage, String ensureBtnStr, String cancelBtnStr) {
		if (propID != -1 && !Util.isEmptyStr(proMessage)) {
			final GoodsItem goodItem = UtilHelper.getGoodsItem(propID);
			if (goodItem != null) {
				PayUtils.showBuyDialog(mAct, goodItem.shopID, true, proTitle,
						proMessage, new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								requestBankruptcy();
							}
						});
			}
		}
	}

	public void requestBankruptcy() {
		// 关闭后调用破产赠送
		if (hallAssociated != null) {
			NodeData node = HallDataManager.getInstance().getCurrentNodeData();
			if (node != null && node.Type == NodeData.NODE_NORMAL) {
				hallAssociated.givingBankruptcy();
			}
		}
	}

	public void showQuickBuyDialog(int propID, String proMessage) {
		showQuickBuyDialog(propID, proMessage, "",
				mResource.getString(R.string.Ensure),
				mResource.getString(R.string.Cancel));
	}

	private void checkShowFirstGift() {
		if (!Util.isEmptyStr(AppConfig.firstGiftDesc)) {
			firstCharge.setVisibility(View.VISIBLE);
			// AnimationDrawable animationDrawable = (AnimationDrawable)
			// getResources().getDrawable(
			// R.anim.cardzone_firstcharge);
			// firstCharge.setBackgroundDrawable(animationDrawable);
			// animationDrawable.start();
		} else {
			// try {
			// AnimationDrawable animationDrawable = (AnimationDrawable)
			// firstCharge.getBackground();
			// animationDrawable.stop();
			// } catch (Exception e) {
			// }
			// ;
			firstCharge.setVisibility(View.GONE);
		}
	}

	public void refreshMatchNodeData() {
		boolean isFresh = false;
		List<NewUIDataStruct> dataList = NewCardZoneProvider.getInstance()
				.getNewUIDataList();
		int index = NewCardZoneProvider.getInstance().getIndex();
		if (index == -1 || index >= dataList.size()) {
			return;
		}
		List<NodeData> subList = dataList.get(index).mSubNodeDataList;
		// for (NodeData item : subList) {
		// if (item.Type == NodeData.NODE_ENROLL) {
		// isFresh = true;
		// break;
		// }
		// }
		if (subList != null && subList.size() > 0) {
			isFresh = true;
		}

		if (isFresh) {
			if (page != null && page.getAdapter() != null) {
				page.getAdapter().notifyDataSetChanged();
			}
			// for(int i = 0;i<pageViewAdapter.getCount();i++){
			// View child = pageViewAdapter.getChild(i);
			// if(child!=null && child instanceof GridView){
			// if (((GridView)child).getAdapter() instanceof
			// NewCardZoneGridViewAdapter) {
			// ((NewCardZoneGridViewAdapter)((GridView)child).getAdapter()).notifyDataSetChanged();
			// }
			// }
			// }
		}
	}

	@Override
	public void onReceive(DialogMessage msg) {
		if (FiexedViewHelper.getInstance().getCurFragment() != FiexedViewHelper.CARDZONE_VIEW) {
			Message message = cardZoneHandler.obtainMessage(HANDLER_DIALOGMSG,
					msg);
			cardZoneHandler.sendMessageDelayed(message, 1000);
			return;
		}
		switch (msg.what) {
		case DialogMsgQueue.DIALOG_WELCOME:
			showWelcomeDialog();
			break;
		case DialogMsgQueue.DIALOG_FIRST_LOGIN:
			showFirstLoginDialog(msg.obj.toString());
			break;
		case DialogMsgQueue.DIALOG_OLD_FIRST_LOGIN:
			showOldFirstLogin(msg.obj.toString());
			break;
		case DialogMsgQueue.DIALOG_LOGIN_GIFT:
			LoginGiftDialog login = new LoginGiftDialog(mAct);
			login.setOnDismissListener(new OnDismissListener() {
				//
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					// 弹下一个对话框，这里是资讯，会在登录送后面弹出来
					DialogMsgQueue.getInstance().handleComplete();
				}
			});
			login.show();

			// new LoginGiftDialog(mAct, AppConfig.giftPack).show();
			break;
		case DialogMsgQueue.DIALOG_DISCOUNT:
			if (DialogMsgQueue.isNewUser) {
				showDiscountDialog();
			} else {
				DialogMsgQueue.getInstance().add(
						DialogMsgQueue.DIALOG_DISCOUNT_OLD);
				DialogMsgQueue.getInstance().handleComplete();
			}
			break;
		case DialogMsgQueue.DIALOG_DISCOUNT_OLD:
			showDiscountDialog();
			break;
		case DialogMsgQueue.DIALOG_NEWS:
			NewsFlashManager.getInstance().displayNewsFlashDialog(mAct,
					new OnDismissListener() {
						//
						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							// 弹下一个对话框，这里是资讯，会在登录送后面弹出来
							DialogMsgQueue.getInstance().handleComplete();
						}
					}, btnMsg);
			// NewsFlashManager.getInstance()
			// .displayNewsFlashDialog(mAct, new OnDismissListener() {
			//
			// @Override
			// public void onDismiss(DialogInterface dialog) {
			// // TODO Auto-generated method stub
			// // 弹下一个对话框，这里是资讯，会在登录送后面弹出来
			// DialogMsgQueue.getInstance().handleComplete();
			// }
			// });
			break;
		default:
			break;
		}

	}

	/**
	 * 监听版本更新数据
	 */
	private void versionUpdate(final Handler handler) {
		NetSocketPak mVersionUpdateSock = new NetSocketPak(
				GlobalFiexParamer.LS_TRANSIT_LOGON,
				GlobalFiexParamer.MSUB_CMD_NEW_VERSION_UPDATE_REQ);

		// 定义接受数据的协议
		short[][] parseProtocol = { { GlobalFiexParamer.LS_TRANSIT_LOGON,
				GlobalFiexParamer.MSUB_CMD_NEW_VERSION_UPDATE_RESP } };

		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				try {
					TDataInputStream tdis = netSocketPak.getDataInputStream();
					tdis.setFront(false);
					short len = tdis.readShort();
					String ver = tdis.readUTF(len);
					VersionInfo versionInfo = new VersionInfo();
					if (versionInfo.parseStatusXml(ver.getBytes(), true)) {
						handler.obtainMessage(
								CardZoneFragment.HANDLER_CHECK_UPDATE_COMPLETE,
								versionInfo).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}

		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(mVersionUpdateSock);
		// 清理协议对象
		mVersionUpdateSock.free();

	}

	/**
	 * 版本升级操作
	 * 
	 * @param vi
	 */
	private void doUpdate(final VersionInfo vi, boolean isShow) {

		if (Util.getAPNType(mAct) == Util.NET_WIFI) {
			try {
				// 如果下载文件已经存在
				if (isNewestVersionExist(vi)) {
					showUpdateDialog(vi, isShow);
				} else {
					checkDownLoadVersion(vi, null);
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		showUpdateDialog(vi, isShow);
	}

	/**
	 * 最近版本文件是否下载完成
	 * 
	 * @param vi
	 * @return
	 */
	private boolean isNewestVersionExist(VersionInfo vi) {
		String downpath = NotifiyDownLoad.getSdcardPath()
				+ NotifiyDownLoad.APKS_PATH; // 最新版本下载目录
		String fileName = NotifiyDownLoad.getFileNameFromUrl(vi._upUrl); // 最新版本文件名
		if (fileName == null) {
			return false;
		}
		File file = new File(downpath, fileName);
		return file.exists();
	}

	private void showUpdateDialog(final VersionInfo vi, boolean isShow) {
		byte versionTag = vi._upgrade;
		String upDesc = null;

		if (Util.isEmptyStr(vi._upUrl) || Util.isEmptyStr(vi._upDesc)) {
			return;
		}
		if (FiexedViewHelper.getInstance().getCurFragment() != FiexedViewHelper.CARDZONE_VIEW) {
			if (!cardZoneHandler.hasMessages(HANDLER_CHECK_UPDATE_COMPLETE)) {
				cardZoneHandler.sendEmptyMessageDelayed(
						HANDLER_CHECK_UPDATE_COMPLETE, 1500);
			}
			return;
		}
		final UpdateDialog updateDialog = new UpdateDialog(mAct, vi);
		updateDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				int rate = updateDialog.getRate(); // 当前下载进度
				Log.e(TAG, "rate = " + rate);
				if (rate != 100) {
					vi.setDownLoadCancle(true);
				}
			}
		});
		upDesc = vi._upDesc.replace("#", "\n");
		// 当状态为需要升级的时候，事件所做的操作
		if (versionTag == VersionInfo.UPGRADE_NEED) {
			updateDialog.show();
			if (upDesc != null) {
				updateDialog.setUpgradeDesc(upDesc);
				updateDialog.setApkSize(vi._apkSize);
				updateDialog.setVersion(vi._version);
				updateDialog.setGifContent();
			}
			updateDialog.setOnCancelUpgradeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FiexedViewHelper.getInstance().playKeyClick();
					updateDialog.dismiss();
				}
			});
			updateDialog.setOnEnsureUpgradeListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					FiexedViewHelper.getInstance().playKeyClick();
					if (v instanceof Button) {
						Button btn = (Button) v;
						String text = btn.getText().toString();
						if (text.equals(mAct.getString(R.string.update_Confir))) {
							checkDownLoadVersion(vi, updateDialog);
						} else {
							String downpath = NotifiyDownLoad.getSdcardPath()
									+ NotifiyDownLoad.APKS_PATH; // 最新版本下载目录
							String fileName = NotifiyDownLoad
									.getFileNameFromUrl(vi._upUrl); // 最新版本文件名
							NotifiyDownLoad.installApk(mAct, downpath + "/"
									+ fileName);
						}
					} else {
						checkDownLoadVersion(vi, updateDialog);
					}

					// dialog.dismiss();
				}
			});
		} else if (versionTag == VersionInfo.UPGRADE_MUST) {
			updateDialog.show();
			if (upDesc != null) {

				updateDialog.setUpgradeDesc(upDesc);
				updateDialog.setApkSize(vi._apkSize);
				updateDialog.setVersion(vi._version);
				updateDialog.setGifContent();
				updateDialog.setCancelable(false);
				updateDialog.setCanceledOnTouchOutside(false);

			}
			updateDialog.setOnCancelUpgradeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FiexedViewHelper.getInstance().playKeyClick();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			updateDialog.setOnEnsureUpgradeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FiexedViewHelper.getInstance().playKeyClick();
					if (v instanceof Button) {
						Button btn = (Button) v;
						String text = btn.getText().toString();
						if (text.equals(mAct.getString(R.string.update_Confir))) {
							checkDownLoadVersion(vi, updateDialog);
						} else {
							String downpath = NotifiyDownLoad.getSdcardPath()
									+ NotifiyDownLoad.APKS_PATH; // 最新版本下载目录
							String fileName = NotifiyDownLoad
									.getFileNameFromUrl(vi._upUrl); // 最新版本文件名
							NotifiyDownLoad.installApk(mAct, downpath + "/"
									+ fileName);
						}
					} else {
						checkDownLoadVersion(vi, updateDialog);
					}

					// dialog.dismiss();
				}
			});

		} else {
			if (isShow) {
				Toast.makeText(mAct, "您已经是最新的版本了~", Toast.LENGTH_SHORT).show();
			} else {
				// do nothing
			}
		}
	}

	/**
	 * 升级下载时候如果是非wifi网络，给予用户提示
	 * 
	 * @param vi
	 */
	private void checkDownLoadVersion(final VersionInfo vi,
			final UpdateDialog updateDialog) {
		if (Util.getAPNType(mAct) == Util.NET_WIFI) {
			try {
				if (!vi.isDownLoading()) {
					vi.gotoUpgrade(getDownLoadListener());
					vi.setDownLoading(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			UtilHelper.showCustomDialog(mAct, mAct.getString(R.string.no_wifi),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							FiexedViewHelper.getInstance().playKeyClick();
							if (!vi.isDownLoading()) {
								vi.gotoUpgrade(getDownLoadListener(updateDialog));
								vi.setDownLoading(true);
							}
						}
					}, true);

		}
	}

	private DownLoadListener getDownLoadListener() {

		DownLoadListener lis = new DownLoadListener() {
			@Override
			public void onProgress(int rate, String strRate) {
				try {
					if (rate == 100) {
						// 下载完成后
						UtilHelper.showCustomDialog(mAct,
								mAct.getString(R.string.download_success),
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										FiexedViewHelper.getInstance()
												.playKeyClick();
										String downpath = NotifiyDownLoad
												.getSdcardPath()
												+ NotifiyDownLoad.APKS_PATH; // 最新版本下载目录
										String fileName = NotifiyDownLoad.getFileNameFromUrl(AppConfig
												.getVersionInfo()._upUrl); // 最新版本文件名
										NotifiyDownLoad.installApk(mAct,
												downpath + "/" + fileName);
									}
								}, true);
						// setVersionName();
						AppConfig.getVersionInfo().setDownLoading(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void downloadFail(String err) {
				/*
				 * Message msg = cardZoneHandler.obtainMessage(); msg.what =
				 * HANDLER_UPDATE_FAIL; msg.obj = err;
				 * cardZoneHandler.sendMessage(msg);
				 */
				try {
					AppConfig.getVersionInfo().setDownLoading(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return lis;
	}

	private DownLoadListener getDownLoadListener(final UpdateDialog dialog) {
		DownLoadListener lis = new DownLoadListener() {
			@Override
			public void onProgress(int rate, String strRate) {
				dialog.setProgressBar(rate);
				dialog.setRateText(rate, strRate);
				if (rate == 100) {
					if (dialog.isShowing()) {
						// dialog.dismiss();
						dialog.showInstall();
					}
					// setVersionName();
					AppConfig.getVersionInfo().setDownLoading(false);
				}
			}

			@Override
			public void downloadFail(String err) {
				dialog.dismiss();
				Message msg = cardZoneHandler.obtainMessage();
				msg.what = HANDLER_UPDATE_FAIL;
				msg.obj = err;
				cardZoneHandler.sendMessage(msg);
				AppConfig.getVersionInfo().setDownLoading(false);
			}
		};
		return lis;
	}

	private void showFirstLoginDialog(String Content) {
		FirstLoginedDialog dialog = new FirstLoginedDialog(mAct, Content);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				DialogMsgQueue.getInstance().handleComplete();
			}
		});
		dialog.show();
	}

	private void showOldFirstLogin(String content) {
		CustomDialog dialog = new CustomDialog(mAct, content);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				DialogMsgQueue.getInstance().handleComplete();
			}
		});
		dialog.show();
	}

	private void showWelcomeDialog() {
		WelcomeDialog dlg = new WelcomeDialog(mAct);
		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				DialogMsgQueue.getInstance().handleComplete();
			}
		});
		dlg.show();
	}

	private void showDiscountDialog() {
		if (!PayUtils.showPromotionDialog(mAct,
				AddGiftProvider.getInstance().adviceGoodID, "",
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						DialogMsgQueue.getInstance().handleComplete();

					}
				}, new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface arg0, int arg1,
							KeyEvent arg2) {
						DialogMsgQueue.getInstance().handleComplete();
						return false;
					}
				}, null)) {
			DialogMsgQueue.getInstance().handleComplete();
		}
	}

	/**
	 * @param type
	 *            1显示充值翻倍，2显示特惠礼包
	 */
	public void setChargeKind(int type) {
		if (firstCharge != null) {
			if (type == 0) {
				firstCharge.setVisibility(View.VISIBLE);
				firstCharge.setBackgroundResource(R.drawable.ic_firstcharge);
			} else if (type == 1) {
				firstCharge.setVisibility(View.VISIBLE);
				firstCharge.setBackgroundResource(R.drawable.ic_discount);
			} else {
				firstCharge.setVisibility(View.GONE);
				clearFirstChargeFlash();
			}
		}
	}

}
