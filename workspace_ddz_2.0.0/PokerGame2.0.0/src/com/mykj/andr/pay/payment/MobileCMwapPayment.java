package com.mykj.andr.pay.payment;

import android.content.Context;
import android.util.Log;

import com.mykj.andr.pay.PayManager;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.MobileHttpApiMgr;
import com.mykj.game.utils.Toast;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;


public class MobileCMwapPayment{
	private final static String TAG = "MobilePayment";

	private static MobileCMwapPayment instance = null;
	/** 平台ID **/
	private static final int MOBILEPLATID = 3;

	private static Context mContext;

	private MobileCMwapPayment(Context context){
	}

	public static MobileCMwapPayment getInstance(Context context) {
		if (instance == null) {
			instance = new MobileCMwapPayment(context);
		}
		mContext=context;
		return instance;
	}
	/**
	 * 初始化支付
	 */
	public void initPayment(){


	}

	/**
	 * 获取支付参数
	 */
	public void Analytical(int shopID,String signParam) {
		if (!Util.isEmptyStr(signParam)) {
			CallPayment(shopID);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.pay_error),Toast.LENGTH_SHORT).show();
		}
	}


	/***
	 * 调用支付SDK
	 */
	public void CallPayment(int shopID) {
		Log.v(TAG, "AlixPayment CallPayment start...");


		String mobileUserId = MobileHttpApiMgr.getInstance()
				.getOnlineGameUserId();
		if (!Util.isEmptyStr(mobileUserId)) {
			final int userId = FiexedViewHelper.getInstance().getUserId();
			final String token = FiexedViewHelper.getInstance().getUserToken();

			final short CURRENCY = 2;
			final short ONE = 1;
			final long CLISEC = 0;
			String channelId = AppConfig.channelId + "#"
					+ AppConfig.childChannelId;
			PayManager.getInstance(mContext).buyMarketGoods(userId, MOBILEPLATID, CURRENCY,
					shopID, token, channelId, mobileUserId, CLISEC, ONE,
					AppConfig.gameId);
			UtilHelper.showTimeColorDialog(mContext,
					mContext.getString(R.string.buysuccess), null, false,false);
		}

	}

}
