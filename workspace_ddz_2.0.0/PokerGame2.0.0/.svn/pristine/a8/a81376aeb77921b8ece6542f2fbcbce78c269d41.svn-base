package com.mykj.andr.pay.payment;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import cn.egame.terminal.paysdk.EgamePay;
import cn.egame.terminal.paysdk.EgamePayListener;

import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Toast;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;
//import cn.egame.terminal.paysdk.EgamePay;
//import cn.egame.terminal.paysdk.EgamePayListener;

public class TelecomEgamePayment {
	private final static String TAG = "TelecomEgamePayment";

	private static TelecomEgamePayment instance = null;

	private static Context mContext;

	private TelecomEgamePayment(Context context) {
	}

	public static TelecomEgamePayment getInstance(Context context) {
		if (instance == null) {
			instance = new TelecomEgamePayment(context);
		}
		mContext = context;
		return instance;
	}

	/**
	 * 初始化支付
	 */
	public void initPayment() {

	}

	/**
	 * 获取支付参数
	 */
	public void Analytical(int shopID, String signParam) {
		if (!Util.isEmptyStr(signParam)) {
					String orderNo = UtilHelper.parseStatusXml(signParam,
					"orderId");
			int price = GoodsItemProvider.getInstance().findGoodsItemById(shopID).pointValue;
//			int price = Integer.parseInt(UtilHelper.parseStatusXml(signParam,
//					"price"));
			CallPayment(shopID, price, orderNo);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.pay_error),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private int currentShopID = -1;

	/***
	 * 调用支付SDK
	 */
	public void CallPayment(int shopID, int fee,String serialNo){
		Log.v(TAG, "TelecomEgamePayment CallPayment start...");
		//短信购买成功回调
		HashMap<String, String> payParams=new HashMap<String, String>();
		payParams.put(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE, fee/100+"");
		payParams.put(EgamePay.PAY_PARAMS_KEY_CP_PARAMS, serialNo);
		currentShopID = shopID;
		EgamePay.pay(mContext, payParams, feeResultListener);
//		PayManager.getInstance(mContext).netReceive(shopID,PayManager.PAY_SIGN_TELECOM_EGAME);
//		EgamePay.payBySms(mContext, fee/100, serialNo, feeResultListener);
		// 提示支付
		if(mContext==null){
			return;
		}
//		UtilHelper.showCustomDialog(mContext,
//				mContext.getString(R.string.buysuccess));
	}

	private EgamePayListener feeResultListener = new EgamePayListener() {

		@Override
		public void paySuccess(Map<String, String> params) {
//			Toast.makeText(mContext, "爱游戏支付成功", Toast.LENGTH_SHORT).show();
			PayUtils.SDKOrderOK(mContext, currentShopID, PayManager.PAY_SIGN_TELECOM_EGAME);
		}

		@Override
		public void payFailed(Map<String, String> params, int errorInt) {
			Toast.makeText(mContext, "计费请求发送失败:" + errorInt, Toast.LENGTH_LONG)
					.show();

		}

		@Override
		public void payCancel(Map<String, String> params) {
			PayUtils.SDKCancel(mContext, currentShopID);
		}
	};

}
