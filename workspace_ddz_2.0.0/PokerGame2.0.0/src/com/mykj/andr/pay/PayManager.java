package com.mykj.andr.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import cn.egame.terminal.paysdk.EgamePay;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.BackPackItem;
import com.mykj.andr.model.GoodsItem;
import com.mykj.andr.model.HPropData;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.model.PayWay;
import com.mykj.andr.pay.payment.AlixPayment;
import com.mykj.andr.pay.payment.MMPayment;
import com.mykj.andr.pay.payment.MobileCMwapPayment;
import com.mykj.andr.pay.payment.MobilePayment;
import com.mykj.andr.pay.payment.MobileSDKPayment;
//import com.mykj.andr.pay.payment.SkyMobilePay;
import com.mykj.andr.pay.payment.TelecomEgamePayment;
import com.mykj.andr.pay.payment.TelecomPayment;
import com.mykj.andr.pay.payment.UnicomSmsPayment;
import com.mykj.andr.pay.payment.UnipayPayment;
import com.mykj.andr.pay.payment.WXPayment;
import com.mykj.andr.pay.ui.SinglePayDialog;
import com.mykj.andr.provider.BackPackItemProvider;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.andr.ui.tabactivity.ExchangeTabActivity;
import com.mykj.andr.ui.tabactivity.MarketActivity;
import com.mykj.andr.ui.widget.CardZoneDataListener;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.MobileHttpApiMgr;
import com.mykj.game.utils.Toast;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

public class PayManager {
	private final static String TAG = "PayManager";

	private static PayManager instance = null;
	private static Context mContext = null;

	/** 使用游戏对象 */
	private static final byte GAME_OBJ = 0;
	private Dialog mProgressDialog;
	/** 当前数 **/
	private static int curBackpackNum = 0;
	/** 短信弹框取消按钮，出破产送 */
	private OnClickListener cancelClickListener;

	public static boolean MARKET_BUY_TAG = false;
	public static boolean FAST_BUY_TAG = true;

	public static final int HANDLER_SIGNTYPE_SUCCUSS = 0; // 获取支付方式成功
	public static final int HANDLER_SIGNTYPE_FAIL = 1; // 获取支付方式失败
	public static final int HANDLER_MARKET_BUY_PRO_SUCCESS = 4;// 购买道具成功，刷新背包和个人信息
	public static final int HANDLER_MARKET_BUY_LEDOU_SUCCESS = 5; // 购买乐豆成功
	public static final int HANDLER_PACK_QUERY_SUCCESS = 6; // 查询背包数据列表成功
	public static final int HANDLER_PACK_QUERY_SUCCESS_NODATA = 7; // 查询背包数据列表成功，但没数据
	public static final int HANDLER_PACK_QUERY_FAIL = 8; // 背包列表协议接收失败
	public static final int HANDLER_MARKET_USE_SUCCESS = 9; // 道具使用成功
	public static final int HANDLER_GOODS_BUY_FAIL = 10;// 购买商品失败
	public static final int HANDLER_ORDER_OVERTIME = 11;// 订单超时

	public static final int PAY_SIGN_MOBILE = 0; // 移动
	public static final int PAY_SIGN_ALIPAY = 1; // 支付宝
	public static final int PAY_SIGN_MOBILE_MM = 39; // 移动MM 市场需求1.7.0 去掉
	public static final int PAY_SIGN_MOBILE_WAP = 88;// 移动cmwap
	public static final int PAY_SIGN_TELECOM = 101; // 电信
	public static final int PAY_SIGN_UNICOM_UNIPAY = 107; // 联通unipay
	public static final int PAY_SIGN_UNICOM_SMS = 111; // 联通短信支付
	public static final int PAY_SIGN_MOBILE_SDK = 103;// 移动sdk
	public static final int PAY_SIGN_SKYMOBILE = 26;// 斯凯SDK
	public static final int PAY_SIGN_WX_SDK = 147;// 微信支付
	public static final int PAY_SIGN_TELECOM_EGAME = 37;// 电信爱游戏

	/** 道具主协议 */
	private static final short MDM_PROP = 17;
	/** 子协议-签名信息获取 **/
	private static final short MSUB_CMD_SIGNATURE_REQ = 790;
	/** 子协议-签名信息返回 **/
	private static final short MSUB_CMD_SIGNATURE_RESP = 791;
	/** 子协议-请求购买商品协议 */
	private static final short MSUB_CMD_BUY_SHOP_REQ = 756;

	/** 子协议-获取背包道具列表 */
	private static final short MSUB_CMD_PACK_PROP_LIST_REQ_EX = 795;
	/** 子协议-返回 失败/成功获取背包道具列表 **/
	private static final short MSUB_CMD_PACK_PROP_LIST_RESP = 763;
	/** 子协议-新道具使用请求协议 */
	private static final short MSUB_CMD_USE_PROP_REQ = 780;
	/** 子协议-新道具使用返回 */
	private static final short MSUB_CMD_USE_PROP_RESP = 781;

	/**
	 * 订单集合，每个新生成的订单，将会插入集合，失败的订单将会删除
	 */

	/**
	 * 私有构造函数
	 * 
	 * @param context
	 */
	private PayManager() {
	}

	public static PayManager getInstance(Context context) {
		if (instance == null) {
			instance = new PayManager();
		}
		mContext = context;
		return instance;
	}

	// 用于第三方支付初始化
	public void thirdPayInit() {
//		UnipayPayment.getInstance(mContext).initPayment();
		MMPayment.getInstance(mContext).initPayment(); // 移动MM支付初始化
		EgamePay.init(mContext);
	}

	@SuppressLint("HandlerLeak")
	private Handler mPayHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_SIGNTYPE_SUCCUSS:// 获取支付方式成功
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				Bundle b = msg.getData();
				final int signType = b.getInt("signType");
				final String signParam = b.getString("signParam");
				boolean isTry = b.getBoolean("isTry");
				boolean isFastBuy = b.getBoolean("isFastBuy");
				GoodsItem item = (GoodsItem) msg.obj;
				final int shopID = item.shopID;
				final int point = item.pointValue;
				final int mobileCardType = UtilHelper
						.getMobileCardType(mContext);
				if (PayUtils.isSDKWithPaySign(signType)
						|| !FastBuyModel.isConfirmon || isTry) {
					toBuy(signType, signParam, shopID, point, mobileCardType,
							mPayHandler, isTry);

				} else {
					PayUtils.showSMSPromptDialog(mContext, item,
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									toBuy(signType, signParam, shopID, point,
											mobileCardType, mPayHandler, false);
								}
							}, cancelClickListener);
				}
				break;
			case HANDLER_SIGNTYPE_FAIL:
				// String error = msg.obj.toString();
				Bundle bundle = msg.getData();
				final String title = bundle.getString("title");
				final int goodsID = bundle.getInt("goodsID");
				final String prompt = bundle.getString("prompt");
				PayUtils.showMMPayDialog(mContext, 1, "", goodsID,
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								PayUtils.showMutilPayDialog(mContext,
										GoodsItemProvider.getInstance()
												.findGoodsItemById(goodsID),
										title, prompt, false,
										cancelClickListener);
							}
						}, cancelClickListener);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				break;
			case HANDLER_MARKET_BUY_PRO_SUCCESS:// 刷新背包和个人信息
				int goodsId = msg.arg1;
				BuyGoods buyedGoods = (BuyGoods) msg.obj;
				GoodsItem goodsItem = GoodsItemProvider.getInstance()
						.findGoodsItemById(goodsId);
				PayUtils.mBuyPropSuccess(mContext, buyedGoods, goodsItem);
				// int userId = FiexedViewHelper.getInstance().getUserId();
				// requestBackPackList(userId, goodsId, buyedGoods,
				// mPayHandler);
				break;
			case HANDLER_GOODS_BUY_FAIL:// 购买道具失败
				final int failGoodsID = msg.arg1;
				PayUtils.showMMPayDialog(mContext, 1, "", failGoodsID,
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								PayUtils.showMutilPayDialog(
										mContext,
										GoodsItemProvider.getInstance()
												.findGoodsItemById(failGoodsID),
										"", "", false, cancelClickListener);
							}
						}, cancelClickListener);
				break;

			case HANDLER_MARKET_BUY_LEDOU_SUCCESS: // 购买商品返回失败

				final BuyGoods ledouGoods = (BuyGoods) msg.obj;
				// String goodsInfo = failGoods.goodsInfo;
				int addBean = msg.arg2;
				final int successGoodsID = msg.arg1;
				GoodsItem successGoodsItem = GoodsItemProvider.getInstance()
						.findGoodsItemById(successGoodsID);
				PayUtils.mBuyLedouSuccess(mContext, addBean, successGoodsItem,
						ledouGoods);
				// UtilHelper.showCustomDialog(mContext, goodsInfo);
				// UtilHelper.showCustomDialog(mContext, goodsInfo,
				// new OnClickListener() {
				//
				// @Override
				// public void onClick(View arg0) {
				// AliBaoDian
				// .getInstance(mContext)
				// .pay(GoodsItemProvider.getInstance()
				// .getGoodsByID(failGoods.propId));
				// }
				// }, true);

				break;
			case HANDLER_PACK_QUERY_SUCCESS:// 查询背包数据列表成功
				final int shopID1 = msg.arg1;
				BuyGoods goods = (BuyGoods) msg.obj;
				int propCount = goods.propCount;
				String goodsInfos = goods.goodsInfo;
				HPropData[] propData = goods.propData;
				if (propCount > 0) {
					if (propData != null) {

						if (mContext instanceof MarketActivity) {
							Handler handler = ((MarketActivity) mContext)
									.getMarkHandler();
							if (handler != null) {
								FiexedViewHelper.getInstance().requestUserBean(
										handler);
							}
						} else {
							FiexedViewHelper.getInstance().requestUserBean();
						}
						// 快速使用
						final BackPackItem backpackInfo = BackPackItemProvider
								.getInstance().getBackPackItem(shopID1);

						UtilHelper.showCustomDialog(mContext, goodsInfos,
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										FiexedViewHelper.getInstance()
												.playKeyClick();
										int userId = FiexedViewHelper
												.getInstance().getUserId();

										if (backpackInfo != null) {
											short urlId = backpackInfo.urlId;
											if (urlId != 0) {

												String userToken = FiexedViewHelper
														.getInstance()
														.getUserToken();
												UtilHelper.showWebView(
														userToken, urlId,
														userId, mContext);
											} else {
												final long CLISEC = 0;
												final byte CBTYPE = 0; // 无扩展数据
												int exData = 0; // 附加参数（如需要GameID、RoomID的道具，可传入相关数据，默认为0）
												if ((null != backpackInfo.Attribute)
														&& ((backpackInfo.Attribute[1] >> GAME_OBJ) & 1) == 1) {
													exData = AppConfig.gameId;
												}
												requestUseMarketGoods(userId,
														shopID1,
														backpackInfo.IndexID,
														exData, CLISEC, CBTYPE,
														mPayHandler);
											}
										}
									}
								});
					}
				} else if (propCount != -1) {
					// 进入我的物品
					UtilHelper.showCustomDialogWithServer(mContext, goodsInfos,
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									FiexedViewHelper.getInstance()
											.playKeyClick();
									Intent intent = new Intent(mContext
											.getApplicationContext(),
											ExchangeTabActivity.class);
									intent.putExtra("gotoPage",
											ExchangeTabActivity.TO_BACKPACK);
									mContext.startActivity(intent);
								}
							});

				}
				break;

			// 查询背包数据列表成功，但没数据
			case HANDLER_PACK_QUERY_SUCCESS_NODATA:
				Log.e(TAG, "我的物品没有数据！");
				break;

			case HANDLER_PACK_QUERY_FAIL:// 背包列表协议接收失败
				UtilHelper.showCustomDialog(mContext, mContext.getResources()
						.getString(R.string.market_buy_success));
				break;
			case HANDLER_MARKET_USE_SUCCESS:
				String strInfo = (String) msg.obj;
				if (!Util.isEmptyStr(strInfo)) {
					UtilHelper.showCustomDialog(mContext, strInfo);
				}
				break;
			// 订单超时的操作
			case HANDLER_ORDER_OVERTIME:
				final int overtimeGoodsID = (Integer) msg.obj;
				GoodsItem overTimeGoodsItem = GoodsItemProvider.getInstance()
						.findGoodsItemById(overtimeGoodsID);
				ArrayList<PayWay> payWays = null;
				if (overTimeGoodsItem != null) {
					payWays = (ArrayList<PayWay>) overTimeGoodsItem
							.getPayWays();
				}
				if (payWays != null && payWays.size() >= 1) {
					PayUtils.showMMPayDialog(mContext, 0, "", overtimeGoodsID,
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									PayUtils.showMutilPayDialog(
											mContext,
											GoodsItemProvider.getInstance()
													.findGoodsItemById(
															overtimeGoodsID),
											"", "", false, cancelClickListener);
								}
							}, cancelClickListener);
				}

				break;
			}
		}
	};

	/**
	 * 商城购买发送购买请求
	 */
	public void requestBuyProp(final GoodsItem goodsItem,
			final boolean isConfirm) {
		cancelClickListener = null;
		if (goodsItem == null) {
			Toast.makeText(mContext, "支付异常", Toast.LENGTH_SHORT).show();
			return;
		}
		boolean isLargeGoods = PayUtils.isLargeGoodsItem(goodsItem.shopID);
		ArrayList<PayWay> payWays = (ArrayList<PayWay>) goodsItem.getPayWays();
		// 使用单支付界面
		if (!isLargeGoods && FastBuyModel.singlePayOn
				|| (payWays == null || payWays.size() <= 0)
				|| (FastBuyModel.lastPayOn && goodsItem.lastSucPaySign != -1)
				|| (payWays != null && payWays.size() == 1)) {
			PayUtils.showSinglePayDialog(mContext, goodsItem, "", "",
					SinglePayDialog.KAWAYI_MEINV_IMG, MARKET_BUY_TAG, false,
					null);
			return;
		}
		boolean[] payEnter = PayUtils.getPayEnters(mContext, payWays);
		// // 如果购买入口只有短信入口，直接请求
		// if (payEnter[0] && !payEnter[1] && !payEnter[2]) {
		// PayManager.getInstance(mContext).mSMSBuy(goodsItem, "", "", false,
		// null);
		// return;
		// }
		// 如果购买只有支付宝入口
		if (!payEnter[0] && payEnter[1] && !payEnter[2]) {
			PayManager.getInstance(mContext).payWithNoPlist(goodsItem,
					PAY_SIGN_ALIPAY, "", "", false);
			return;
		}
		// 如果购买只有微信入口
		if (!payEnter[0] && !payEnter[1] && payEnter[2]) {
			PayManager.getInstance(mContext).payWithNoPlist(goodsItem,
					PAY_SIGN_WX_SDK, "", "", false);
			return;
		}
		PayUtils.showMutilPayDialog(mContext, goodsItem, "", "",
				MARKET_BUY_TAG, null);
	}

	/**
	 * 快捷购买发起购买请求
	 */
	public void requestFastBuyProp(final GoodsItem goodsItem,
			final String title, String msg, final boolean isConfirm,
			int mvType, final OnClickListener cancelClickListener) {
		this.cancelClickListener = null;
		if (goodsItem == null) {
			Toast.makeText(mContext, "支付异常", Toast.LENGTH_SHORT).show();
			return;
		}
		boolean isLargeGoods = PayUtils.isLargeGoodsItem(goodsItem.shopID);
		ArrayList<PayWay> payWays = (ArrayList<PayWay>) goodsItem
				.getFastBuyPayWayList();
		if (payWays == null || payWays.size() <= 0) {
			payWays = (ArrayList<PayWay>) goodsItem.getPayWays();
		}
		// 默认并不需要快捷购买弹框
		if ((payWays == null || payWays.size() <= 0) && !isConfirm) {
			PayManager.getInstance(mContext).mSMSBuy(goodsItem, title, msg,
					true, cancelClickListener);
			return;
		}
		// 使用单支付界面
		if (!isLargeGoods && FastBuyModel.singlePayOn
				|| (payWays == null || payWays.size() <= 0)
				|| (FastBuyModel.lastPayOn && goodsItem.lastSucPaySign != -1)) {
			if (payWays != null && payWays.size() == 1) {
				if (isConfirm) {
					PayUtils.showSinglePayDialog(mContext, goodsItem, title,
							msg, mvType, FAST_BUY_TAG, false,
							cancelClickListener);
					return;
				}
			} else {
				PayUtils.showSinglePayDialog(mContext, goodsItem, title, msg,
						mvType, FAST_BUY_TAG, false, cancelClickListener);
				return;
			}

		}
		// 获取支付入口
		boolean[] payEnter = PayUtils.getPayEnters(mContext, payWays);
		// 如果购买入口只有短信入口，直接请求
		if (payEnter[0] && !payEnter[1] && !payEnter[2]) {
			if (isConfirm) {
				PayUtils.showMutilPayDialog(mContext, goodsItem, title, msg,
						FAST_BUY_TAG, cancelClickListener);
			} else {
				PayManager.getInstance(mContext).mSMSBuy(goodsItem, title, msg,
						true, cancelClickListener);
			}
			return;
		}
		// 如果购买只有支付宝入口
		if (!payEnter[0] && payEnter[1] && !payEnter[2]) {
			if (isConfirm) {
				PayUtils.showMutilPayDialog(mContext, goodsItem, title, msg,
						FAST_BUY_TAG, cancelClickListener);
			} else {
				PayManager.getInstance(mContext).payWithNoPlist(goodsItem,
						PAY_SIGN_ALIPAY, title, msg, true);
			}
			return;
		}
		// 如果购买只有微信入口
		if (!payEnter[0] && !payEnter[1] && payEnter[2]) {
			if (isConfirm) {
				PayUtils.showMutilPayDialog(mContext, goodsItem, title, msg,
						FAST_BUY_TAG, cancelClickListener);
			} else {
				PayManager.getInstance(mContext).payWithNoPlist(goodsItem,
						PAY_SIGN_WX_SDK, title, msg, true);
			}
			return;
		}
		PayUtils.showMutilPayDialog(mContext, goodsItem, title, msg,
				FAST_BUY_TAG, cancelClickListener);
	}

	// 单独支付，不用上传pver和plist
	public void payWithNoPlist(final GoodsItem goodsItem, int signType,
			String title, String msg, boolean isFastBuy) {
		int mobileCardType = UtilHelper.getMobileCardType(mContext);
		final String externalData = PayUtils.getPayExternal(
				(short) mobileCardType, mContext, false, false);
		mProgressDialog = PayUtils.showProgress(mContext);
		requestWhichPay(2, signType, externalData, mPayHandler, goodsItem,
				title, msg, isFastBuy, false);
	}

	/**
	 * 短信入口的购买
	 * 
	 * @param item
	 * @param promptTitle
	 * @param isPayConfirm
	 *            是否需要2次确认
	 * @param isFastBuy
	 *            是否是快捷购买发起的
	 * @param cancelListener
	 * 
	 */
	public void mSMSBuy(final GoodsItem goodsItem, final String title,
			String prompt, boolean isFastBuy, OnClickListener cancelListener) {
		int mobileCardType = UtilHelper.getMobileCardType(mContext);
		final String externalData = PayUtils.getPayExternal(
				(short) mobileCardType, mContext, true, false);
		final int signType = PayUtils.getDefaultPaySign(mContext);
		cancelClickListener = cancelListener;
		mProgressDialog = PayUtils.showProgress(mContext);
		requestWhichPay(2, signType, externalData, mPayHandler, goodsItem,
				title, prompt, isFastBuy, false);
	}

	public static void requestWhichPay(final int dataType, final int signType,
			final String external, final Handler handler, final GoodsItem item,
			final String title, final String prompt, final boolean isFastBuy,
			final boolean isTry) {
		final int point = item.pointValue;
		final int shopID = item.shopID;
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeByte((byte) dataType); // 数据类型1-充值 2-购买
		tdos.writeByte((byte) signType); // 签名类型1-支付宝 2-其他
		tdos.writeInt(point);// 总金额 表示为分,如10元= 1000分
		tdos.writeInt(shopID);// 商品编号，充值ShopID=0
		tdos.writeInt(1); // 商品数量,充值商品数量=0
		tdos.writeUTFShort(external); // 扩展数据，0
		NetSocketPak pointBalance = new NetSocketPak(MDM_PROP,
				MSUB_CMD_SIGNATURE_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_PROP, MSUB_CMD_SIGNATURE_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);
				byte result = tdis.readByte(); // 充值结果 0-成功1-失败
				if (result == 0) {// 成功

					tdis.readByte();// 签名类型1-支付宝 2-易宝 3-酷派
					int signType = tdis.readByte() & 0xff;
					tdis.readShort(); // 参数总长度，超过2k,
					String s = tdis.readUTFShort();
					String signParam = "";
					try {
						signParam = URLDecoder.decode(s, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					Message msg = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("signType", signType);
					b.putString("signParam", signParam);
					b.putBoolean("isTry", isTry);
					b.putBoolean("isFastBuy", isFastBuy);
					msg.setData(b);
					msg.obj = item;
					msg.what = HANDLER_SIGNTYPE_SUCCUSS;
					handler.sendMessage(msg);
				} else {// 失败
					String error = tdis.readUTFShort(); // 消息文本内容
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("title", title);
					bundle.putString("prompt", prompt);
					bundle.putInt("goodsID", item.shopID);
					msg.obj = error;
					msg.what = HANDLER_SIGNTYPE_FAIL;
					// SIM卡类型：联通
					handler.sendMessage(msg);
				}
				// 数据处理完成，终止继续解析
				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();

	}

	/**
	 * 标记用户为付费用户
	 */
	private static void markUserPaySuccess() {
		UserInfo user = HallDataManager.getInstance().getUserMe();
		if (user != null) {
			user.statusBit |= (1 << 24); // 第25位标示是否付费用户
		}
	}

	/** 子协议-失败/成功：购买商品结果返回 */
	private static final short MSUB_CMD_BUY_SHOP_RESP = 757;

	/**
	 * @Description: 购买商品协议接收
	 * @param shopID
	 * @param handler
	 */
	public void netReceive(final int shopId, final int signType) {
		short[][] parseProtocol = { { MDM_PROP, MSUB_CMD_BUY_SHOP_RESP } };
		if (PayUtils.isTimeOutOper(signType)) {
			Message msg = mPayHandler.obtainMessage();
			msg.what = HANDLER_ORDER_OVERTIME;
			msg.obj = shopId;
			mPayHandler.sendMessageDelayed(msg, 1000 * 50);
		}
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {

			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				if (tdis == null) {
					return true;
				}

				BuyGoods goods = new BuyGoods();
				goods.userId = tdis.readInt(); // 用户ID
				goods.propId = tdis.readInt(); // 道具ID,商品ID

				// '33' '扣费失败'
				// '34' 'WAP 购买失败，您可以通过CMNET或WIFI登录，进入商城购买！'
				// '35' '余额不足'
				byte result = tdis.readByte(); // 成功失败

				goods.goodsInfo = tdis.readUTFShort(); // 具体描述信息
				goods.cliSec = tdis.readLong(); // cliSec客户端标识
				int propCount = tdis.readByte(); // 道具数量
				goods.propCount = propCount;
				HPropData propData[] = null;
				if (propCount > 0) {
					propData = new HPropData[propCount]; // 道具数据数组
					for (int i = 0; i < propCount; i++) {
						propData[i] = new HPropData(tdis); // 道具数据
					}

				}
				int addBean = tdis.readInt();// 多赠送的乐豆数，用于购买成功的显示
				goods.propData = propData;

				Message msg = mPayHandler.obtainMessage();
				msg.obj = goods;
				msg.arg1 = shopId;

				if (result == 0) { // 0表示购买需要使用道具，48表示直接到帐道具如获取乐豆
					markUserPaySuccess();
				}
				if (result == 0 || result == 2) {
					mPayHandler.removeMessages(HANDLER_ORDER_OVERTIME, shopId);
				}
				if (result == 0) { // 购买完成 0
					GoodsItem goodsItem = GoodsItemProvider.getInstance()
							.findGoodsItemById(shopId);
					if (goodsItem != null) {
						if (goodsItem.isLedouType()) {
							msg.what = PayManager.HANDLER_MARKET_BUY_LEDOU_SUCCESS;
							msg.arg2 = addBean;
							mPayHandler.sendMessage(msg);
						} else {
							msg.what = PayManager.HANDLER_MARKET_BUY_PRO_SUCCESS;
							mPayHandler.sendMessage(msg);
						}
					}

				} else if (result == 2) {
					int mobileCardType = UtilHelper.getMobileCardType(mContext);
					final String externalData = PayUtils.getPayExternal(
							(short) mobileCardType, mContext, true, true);
					PayManager.requestWhichPay(2, signType, externalData,
							mPayHandler, GoodsItemProvider.getInstance()
									.findGoodsItemById(shopId), "", "", true,
							true);
					return true;
				} else {
					msg.what = PayManager.HANDLER_GOODS_BUY_FAIL;
					mPayHandler.sendMessage(msg);
				}
				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}

	/**
	 * @Title: requestMarketGoods
	 * @Description: 解析请求购买商品协议
	 * @param userID
	 *            用户ID
	 * @param platID
	 *            平台ID
	 * @param currency
	 *            货币类型 （1：虚拟币 2：移动点数）
	 * @param shopID
	 *            商品ID
	 * @param token
	 *            用户token串
	 * @param channelID
	 *            渠道数据
	 * @param key
	 *            如：PlatID=3，则Key为移动用户伪码key长度（来自移动）
	 * @param cliSec
	 *            客户端标识
	 * @param shopCount
	 *            购买商品数量,默认为1
	 * @version: 2012-7-25 下午04:28:11
	 * @param handler
	 */
	public void buyMarketGoods(int userID, int platID, short currency,
			final int shopID, String token, String channelID, String key,
			long cliSec, short shopCount, int gameId) {
		int playid = FiexedViewHelper.getInstance().getGameType();
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream();
		tdos.setFront(false);
		tdos.writeInt(userID);
		tdos.writeInt(platID);
		tdos.writeShort(currency);
		tdos.writeInt(shopID);
		tdos.writeUTFShort(token);
		tdos.writeUTFShort(channelID);
		tdos.writeUTFShort(key);
		tdos.writeLong(cliSec);
		tdos.writeShort(shopCount);
		tdos.writeInt(gameId);

		switch (CardZoneDataListener.NODE_DATA_PROTOCOL_VER) {
		case CardZoneDataListener.VERSION_1:// 列表协议第一版，每个节点单独请求
			break;
		case CardZoneDataListener.VERSION_2:// 列表协议第二版，支持所有列表数据一次请求完成，本地缓存列表数据
			playid = 0;
			tdos.writeByte(playid); // 玩法选择
			break;
		case CardZoneDataListener.VERSION_3:// 列表协议第三版，支持斗地主3种玩法，列表数据zip压缩，本地缓存列表数据
			tdos.writeByte(playid); // 玩法选择
			break;
		default:
			break;
		}

		NetSocketPak requestGoods = new NetSocketPak(MDM_PROP,
				MSUB_CMD_BUY_SHOP_REQ, tdos);
		// 定义接受协议数据
		// 购买成功回调
		netReceive(shopID, PAY_SIGN_MOBILE_WAP);
		// 发送协议
		NetSocketManager.getInstance().sendData(requestGoods);
		// 清理协议对象
		requestGoods.free();
	}

	/**
	 * @Title: requestBackPackList
	 * @Description: 获取背包列表
	 * @param userID
	 * @param PropCount
	 *            作为参数传递商品个数
	 * @param shopID
	 *            作为参数传递商品ID
	 * @version: 2012-7-26 上午11:08:09
	 */
	public void requestBackPackList(int userID, final int shopId,
			final BuyGoods buyGoods, final Handler handler) {
		BackPackItemProvider.getInstance().init();
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream();
		tdos.writeInt(userID, false);
		NetSocketPak pointBalance = new NetSocketPak(MDM_PROP,
				MSUB_CMD_PACK_PROP_LIST_REQ_EX, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_PROP, MSUB_CMD_PACK_PROP_LIST_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);

				int total = tdis.readShort(); // 商品总个数
				int num = tdis.readShort(); // 当次商品个数
				BackPackItem[] backPackItems;
				if (num <= 0) {
					// 未查询到相关记录,请稍后再试...
					Message msg = handler.obtainMessage();
					msg.what = PayManager.HANDLER_PACK_QUERY_SUCCESS_NODATA;
				} else {
					backPackItems = new BackPackItem[num];
					// 累计接受到数据到数组中
					for (int i = 0; i < num; i++) {
						backPackItems[i] = new BackPackItem(tdis);
					}
					BackPackItemProvider.getInstance().setBackPackItem(
							backPackItems);
					curBackpackNum += num; // 积累保存到全局变量，记录当前返回累计数目

					if (curBackpackNum == total) {

						// int
						// propCount=BackPackItemProvider.getInstance().getPorpCount(shopId);

						if (handler != null/* && propCount>=buyGoods.propCount */) {
							Message msg = handler.obtainMessage();
							msg.what = PayManager.HANDLER_PACK_QUERY_SUCCESS;
							msg.arg1 = shopId;
							msg.obj = buyGoods;
							handler.sendMessage(msg);
						}
						BackPackItemProvider.getInstance().setFinish(true);
						curBackpackNum = 0;
					}

				}

				return true;
			}
		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	/***
	 * @Title: requestUseMarketGoods
	 * @Description: 立即使用改道具协议
	 * @param userID
	 *            用户ID
	 * @param propID
	 *            道具ID
	 * @param indexID
	 *            道具索引编号
	 * @param exData
	 *            附加参数（如需要GameID、RoomID的道具，可传入相关数据，默认为0）
	 * @param cliSec
	 *            客户端标识
	 * @param cbType
	 *            0-无扩展数据1 为充值卡 2 为话费券 3.实物道具
	 * @param extXml
	 *            扩展数据
	 * @version: 2012-7-26 上午09:54:59
	 */
	public static void requestUseMarketGoods(int userID, int propID,
			long indexID, int exData, long cliSec, byte cbType,
			final Handler handler) {

		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeInt(userID);
		tdos.writeInt(propID);
		tdos.writeLong(indexID);
		tdos.writeInt(exData);
		tdos.writeLong(cliSec);
		tdos.writeByte(cbType);

		NetSocketPak useGoods = new NetSocketPak(MDM_PROP,
				MSUB_CMD_USE_PROP_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM_PROP, MSUB_CMD_USE_PROP_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {

			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				try {
					TDataInputStream tdis = netSocketPak.getDataInputStream();
					tdis.readInt();
					tdis.readInt();
					int result = tdis.readInt();
					if (tdis.available() > 0) {
						/** long IndexID= */
						tdis.readLong();
					}
					if (tdis.available() > 0) {
						String msgStr = tdis.readUTFByte();
						Message msg = handler.obtainMessage();
						msg.obj = msgStr;
						msg.what = PayManager.HANDLER_MARKET_USE_SUCCESS;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(useGoods);
		// 清理协议对象
		useGoods.free();

	}

	/***
	 * 获取支付类型列表
	 * 
	 * @return
	 */
	public static String getPlistString() {
		StringBuffer sb = new StringBuffer();
		sb.append(PAY_SIGN_MOBILE).append(",");// 移动短代支付
		sb.append(PAY_SIGN_UNICOM_SMS).append(",");// 联通短代支付
		sb.append(PAY_SIGN_TELECOM).append(",");// 电信支付
		sb.append(PAY_SIGN_MOBILE_MM).append(",");// 移动MM
		sb.append(PAY_SIGN_UNICOM_UNIPAY).append(",");// 联通unipay
		sb.append(PAY_SIGN_TELECOM_EGAME).append(",");// 爱游戏
		sb.append(PAY_SIGN_MOBILE_WAP).append(",");// 多酷cmwap支付
		sb.append(PAY_SIGN_SKYMOBILE).append(",");// 斯凯
		sb.append(PAY_SIGN_MOBILE_SDK);// 基地sdk
		return sb.toString();
	}

	/***
	 * 获取客户端默认支付类型
	 * 
	 * @return
	 */
	public static String getSigntypeString() {
		int signType = -1;
		// SIM卡类型:1-移动;2-联通;3-电信
		final int mobileCardType = UtilHelper.getMobileCardType(mContext);
		if (mobileCardType == UtilHelper.UNICOM_TYPE) {// 联通
			signType = PAY_SIGN_UNICOM_SMS;
		} else if (mobileCardType == UtilHelper.TELECOM_TYPE) {// 电信
			signType = PAY_SIGN_TELECOM;
		} else if (mobileCardType == UtilHelper.MOVE_MOBILE_TYPE) {// 移动
			String mobileUserId = MobileHttpApiMgr.getInstance()
					.getOnlineGameUserId();
			if (Util.isCMWap(mContext) && !Util.isEmptyStr(mobileUserId)) {

				signType = PAY_SIGN_MOBILE_WAP;
			} else {
				// 非wap即发短信
				signType = PAY_SIGN_MOBILE;
			}

		} else {// 第三方
			signType = PAY_SIGN_ALIPAY;
		}
		return "" + signType;
	}

	public static void toBuy(int signType, String signParam, int shopID,
			int point, int mobileCardType, Handler handler, boolean isTry) {
		Log.e(TAG, signParam);
		switch (signType) {
		case PAY_SIGN_MOBILE:// 移动短信支付
			MobilePayment.getInstance(mContext).Analytical(shopID, signParam,
					isTry);
			break;
		case PAY_SIGN_MOBILE_MM:// 移动MM支付
			MMPayment.getInstance(mContext).Analytical(shopID, signParam);
			break;
		case PAY_SIGN_TELECOM:// 电信支付
			TelecomPayment.getInstance(mContext).Analytical(shopID, signParam,
					isTry);
			break;
		case PAY_SIGN_ALIPAY:// 支付宝支付
			AlixPayment.getInstance(mContext).Analytical(shopID, signParam);
			break;
		case PAY_SIGN_MOBILE_WAP: // 移动cmwap
			MobileCMwapPayment.getInstance(mContext).Analytical(shopID,
					signParam);
			break;
		case PAY_SIGN_UNICOM_UNIPAY:// 联通unipay支付
			UnipayPayment.getInstance(mContext).Analytical(shopID, signParam);
			break;
		case PAY_SIGN_UNICOM_SMS: // 联通短信支付
			UnicomSmsPayment.getInstance(mContext).Analytical(shopID,
					signParam, isTry);
			break;
		case PAY_SIGN_MOBILE_SDK: // 移动SDK支付
			MobileSDKPayment.getInstance(mContext)
					.Analytical(shopID, signParam);
			break;
		case PAY_SIGN_SKYMOBILE:// 斯凯支付
			//SkyMobilePay.getInstance(mContext).pay(signParam, shopID);
			break;
		case PAY_SIGN_WX_SDK: // 微信支付
			WXPayment.getInstance(mContext).Analytical(shopID, signParam);
			break;
		case PAY_SIGN_TELECOM_EGAME:// 电信爱游戏
			TelecomEgamePayment.getInstance(mContext).Analytical(shopID,
					signParam);
			break;
		default:
			Toast.makeText(mContext, mContext.getString(R.string.buyerror),
					Toast.LENGTH_LONG).show();
			break;
		}

	}

	/**
	 * 清除相关的超时订单
	 * 
	 * @param shopId
	 */
	public void removeOverTimeHandler(int shopId) {
		if (mPayHandler != null) {
			mPayHandler.removeMessages(HANDLER_ORDER_OVERTIME, shopId);
		}
	}
}
