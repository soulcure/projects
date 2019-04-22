package com.mykj.andr.pay.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Toast;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

public class MobilePayment {
	private final static String TAG = "MobilePayment";

	private static MobilePayment instance = null;

	private static Context mContext;

	private MobilePayment(Context context) {
	}

	public static MobilePayment getInstance(Context context) {
		if (instance == null) {
			instance = new MobilePayment(context);
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
	public void Analytical(int shopID, String signParam, boolean isTry) {
		if (!Util.isEmptyStr(signParam)) {
			String smsPhoneNumber = UtilHelper.parseStatusXml(signParam,
					"phonenum");
			String smsContent = UtilHelper.parseStatusXml(signParam, "pass");
			String smsPhoneNumber2 = UtilHelper.parseStatusXml(signParam,
					"phonenum2");
			String smsContent2 = UtilHelper.parseStatusXml(signParam, "pass2");
			String smsContentDecoder = null;
			String smsContentDecoder2 = null;
			try {
				smsContentDecoder = URLDecoder.decode(smsContent, "utf-8");
				smsContentDecoder2 = URLDecoder.decode(smsContent2, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// CP服务器产生的订单号
			String smsOrder = UtilHelper.parseStatusXml(signParam, "orderId");
			CallPayment(shopID, smsPhoneNumber, smsContentDecoder,
					smsPhoneNumber2, smsContentDecoder2, smsOrder, isTry);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.pay_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	/***
	 * 调用支付SDK
	 */
	public void CallPayment(int shopID, String smsPhoneNumber,
			String smsContent, String smsPhoneNumber2, String smsContent2,
			String smsOrder, boolean isTry) {
		Log.v(TAG, "MobilePayment CallPayment start...");
		// 短信购买成功回调
		PayManager.getInstance(mContext).netReceive(shopID,
				PayManager.PAY_SIGN_MOBILE);
		// 发送短信购买
		if (smsPhoneNumber != null && smsPhoneNumber.length() > 0
				&& smsContent != null && smsContent.length() > 0) {
			Util.sendTextSMS(smsPhoneNumber, smsContent, mContext);
		}
		if (smsPhoneNumber2 != null && smsPhoneNumber2.length() > 0
				&& smsContent2 != null && smsContent2.length() > 0) {
			Util.sendTextSMS(smsPhoneNumber2, smsContent2, mContext);
		}
		// 提示支付
		if (mContext == null) {
			return;
		}
		if (!isTry) {
			PayUtils.showMMPayDialog(mContext, 3, "", shopID, null, null);
		}
	}

}
