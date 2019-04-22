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

public class UnicomSmsPayment {
	private final static String TAG = "UnicomSmsPayment";

	private static UnicomSmsPayment instance = null;

	private Context mContext;

	private UnicomSmsPayment() {
	}

	public static UnicomSmsPayment getInstance(Context context) {
		if (instance == null) {
			instance = new UnicomSmsPayment();
		}
		instance.mContext = context; // 此处为了实时更新Context,解决弹框界面不一致
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
					"phonenum");
			// 短信内容:其中包括订单号
			String uniSmsContent = UtilHelper.parseStatusXml(signParam, "pass");
			String uniSmsPhoneNumber2 = UtilHelper.parseStatusXml(signParam,
					"phonenum2");
			String uniSmsContent2 = UtilHelper.parseStatusXml(signParam,
					"pass2");
			String uniSmsContentDecoder = null;
			String uniSmsContentDecoder2 = null;
			try {
				uniSmsContentDecoder = URLDecoder
						.decode(uniSmsContent, "utf-8");
				uniSmsContentDecoder2 = URLDecoder.decode(uniSmsContent2,
						"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String uniSmsOrder = UtilHelper
					.parseStatusXml(signParam, "orderId");
			CallPayment(shopID, telSmsPhoneNumber, uniSmsContentDecoder,
					uniSmsPhoneNumber2, uniSmsContentDecoder2, uniSmsOrder,
					isTry);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.pay_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	/***
	 * 调用支付SDK
	 */
	private void CallPayment(int shopID, String uniSmsPhoneNumber,
			String uniSmsContent, String uniSmsPhoneNumber2,
			String uniSmsContent2, String uniSmsOrder, boolean isTry) {
		Log.v(TAG, "TelecomPayment CallPayment start...");
		// 短信购买成功回调
		PayManager.getInstance(mContext).netReceive(shopID,
				PayManager.PAY_SIGN_UNICOM_SMS);
		// 发送短信购买
		if (uniSmsPhoneNumber != null && uniSmsPhoneNumber.length() > 0
				&& uniSmsContent != null && uniSmsContent.length() > 0) {
			Util.sendTextSMS(uniSmsPhoneNumber, uniSmsContent, mContext);
		}
		if (uniSmsPhoneNumber2 != null && uniSmsPhoneNumber2.length() > 0
				&& uniSmsContent2 != null && uniSmsContent2.length() > 0) {
			Util.sendTextSMS(uniSmsPhoneNumber2, uniSmsContent2, mContext);
		}

		String content = mContext.getString(R.string.buysuccess);
		// SpannableStringBuilder msg = new SpannableStringBuilder(content);
		// int index = content.indexOf("\n");
		// if(index > 0){
		// msg.setSpan(new
		// ForegroundColorSpan(0xffffff00),0,index,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		// //设置指定位置文字的颜色
		// }
		// 提示支付
		if (!isTry) {
			PayUtils.showMMPayDialog(mContext, 3, "", shopID, new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			}, null);
		}
	}

}
