package com.mykj.andr.pay.payment;

import java.util.ArrayList;

import mm.sms.purchasesdk.PurchaseSkin;
import mm.sms.purchasesdk.SMSPurchase;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mykj.andr.model.GoodsItem;
import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.mm.IAPHandler;
import com.mykj.andr.pay.mm.IAPListener;
import com.mykj.andr.pay.model.PayWay;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.andr.ui.fragment.CardZoneFragment;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Toast;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

public class MMPayment {
	private final String TAG = "MMPayment";

	private static MMPayment instance = null;

	public SMSPurchase purchase;

	private static IAPListener mListener;

	private static Context mContext;

	private int shopID = -1;

	private MMPayment(Context context) {
		mContext = context;
	}

	public static MMPayment getInstance(Context context) {
		if (instance == null) {
			instance = new MMPayment(context);
		}
		if (context != null) {
			mContext = context;
		}
		return instance;
	}

	/**
	 * 初始化支付
	 */
	public void initPayment() {
		String appid = "300002717189";
		String appkey = "3CD1B8FF229C9A28";
		IAPHandler iapHandler = new IAPHandler((Activity) mContext);
		mListener = new IAPListener((Activity) mContext, iapHandler);
		purchase = SMSPurchase.getInstance();
		// purchase.setAppInfo(appid, appkey, PurchaseSkin.SKIN_SYSTEM_ONE); //
		// 设置计费应用ID和Key (必须)
		purchase.setAppInfo(appid, appkey, PurchaseSkin.SKIN_SYSTEM_TWO); // 设置计费应用ID和Key
																			// (必须)
		// purchase.setAppInfo(appid, appkey, PurchaseSkin.SKIN_SYSTEM_THREE);
		// // 设置计费应用ID和Key (必须)
		purchase.smsInit(mContext, mListener);
	}

	/**
	 * 获取支付参数
	 */
	public void Analytical(int shopID, String signParam) {
		if (shopID != -1) {
			this.shopID = shopID;
		}
		if (!Util.isEmptyStr(signParam)) {
			String productnum = UtilHelper.parseStatusXml(signParam,
					"chargePoint");
			String product = UtilHelper.parseStatusXml(signParam, "orderno");
			CallPayment(shopID, productnum, product);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.pay_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	/***
	 * 调用支付SDK
	 */
	public void CallPayment(int shopID, String productnum, String product) {

		Log.v(TAG, "MMPayment CallPayment start...");
		// 购买回调
//		PayManager.getInstance(mContext).netReceive(shopID,
//				PayManager.PAY_SIGN_MOBILE_MM);

		purchase.smsOrder(mContext, productnum, mListener, product);
	}

	public static final int MM_PAY_CANCEL = 1201;
	public static final int MM_PAY_CONFIRM = 1202;
	public static final int MM_PAY_FAIL = 1203;

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MM_PAY_CANCEL:
				PayUtils.SDKCancel(mContext, shopID);
				break;
			case MM_PAY_CONFIRM:
				PayUtils.SDKOrderOK(mContext, shopID, PayManager.PAY_SIGN_MOBILE_MM);
				break;
			case MM_PAY_FAIL:
				PayManager.getInstance(mContext).netReceive(shopID, PayManager.PAY_SIGN_MOBILE_MM);
				break;
			default:
				break;
			}
		};
	};

}
