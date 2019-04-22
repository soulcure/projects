package com.mykj.andr.pay.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.util.Log;

import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Toast;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

public class TelecomPayment {
	private final static String TAG = "AlixPayment";

	private static TelecomPayment instance = null;

	private static Context mContext;

	private TelecomPayment(Context context) {
	}

	public static TelecomPayment getInstance(Context context) {
		if (instance == null) {
			instance = new TelecomPayment(context);
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
	@SuppressWarnings("deprecation")
	public void Analytical(int shopID, String signParam, boolean isTry) {
		if (!Util.isEmptyStr(signParam)) {
			// 发送至号码
			String telSmsPhoneNumber = UtilHelper.parseStatusXml(signParam,
					"num");
			// 短信内容:其中包括订单号
			String telSmsContent = UtilHelper.parseStatusXml(signParam, "msg");
			String telSmsPhoneNumber2 = UtilHelper.parseStatusXml(signParam,
					"num2");
			String telSmsContent2 = UtilHelper.parseStatusXml(signParam,
					"msg2");
			String telSmsContentDecoder = null;
			String telSmsContentDecoder2 = null;
			try {
				telSmsContentDecoder = URLDecoder
						.decode(telSmsContent, "utf-8");
				telSmsContentDecoder2 = URLDecoder.decode(telSmsContent2,
						"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String telSmsOrder = UtilHelper
					.parseStatusXml(signParam, "orderId");
			CallPayment(shopID, telSmsPhoneNumber, telSmsContentDecoder,
					telSmsPhoneNumber2, telSmsContentDecoder2, telSmsOrder,
					isTry);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.pay_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	/***
	 * 调用支付SDK
	 */
	public void CallPayment(int shopID, String telSmsPhoneNumber,
			String telSmsContent, String telSmsPhoneNumber2,
			String telSmsContent2, String telSmsOrder, boolean isTry) {
		Log.v(TAG, "TelecomPayment CallPayment start...");
		// 短信购买成功回调
		PayManager.getInstance(mContext).netReceive(shopID,
				PayManager.PAY_SIGN_TELECOM);
		// 发送短信购买
		if (telSmsPhoneNumber != null && telSmsPhoneNumber.length() > 0
				&& telSmsContent != null && telSmsContent.length() > 0) {
			Util.sendTextSMS(telSmsPhoneNumber, telSmsContent, mContext);
		}
		if (telSmsPhoneNumber2 != null && telSmsPhoneNumber2.length() > 0
				&& telSmsContent2 != null && telSmsContent2.length() > 0) {
			Util.sendTextSMS(telSmsPhoneNumber2, telSmsContent2, mContext);
		}
		// 提示支付
		if (!isTry) {
			PayUtils.showMMPayDialog(mContext, 3, "", shopID, null, null);
		}
	}

}
