package com.ivmall.android.app.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.PayRequest;
import com.ivmall.android.app.entity.PayResponse;
import com.ivmall.android.app.entity.PayStatusRequest;
import com.ivmall.android.app.entity.PayStatusResponse;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.List;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	private static final String TAG = WXPayEntryActivity.class.getSimpleName();
	private static final int PAY_SDK_RESULT = 1;
	private static final int PAY_SERVER_RESULT = 2;

	// 微信api接口实例
	private IWXAPI wxApi;
	private int mQueryCount;
	private PayHandler mHandler;

	private Double mPrice;
	private String mVipGuid;
	private String mToken;

	// extra data
	private static final String PARAM_OUT_TRADE_NO = "out_trade_no";
	private static final String PARAM_TOTAL_FEE = "total_fee";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new LinearLayout(this));

		wxApi = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID);
		wxApi.handleIntent(getIntent(), this);

		mPrice = getIntent().getExtras().getDouble(PaymentDialog.PRCIE);
		mVipGuid = getIntent().getExtras().getString(PaymentDialog.VIPGUID);
		mToken = ((KidsMindApplication) getApplication()).getToken();

		PayRequest payRequest = new PayRequest();
		payRequest.setPrice(mPrice);
		payRequest.setVipGuid(mVipGuid);
		payRequest.setToken(mToken);
		pay(payRequest);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		wxApi.handleIntent(intent, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		BaiduUtils.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		BaiduUtils.onPause(this);
	}

	public boolean isWXAppInstalled() {
		return wxApi.isWXAppInstalled();
	}

	public boolean getWXAppSupportAPI() {
		return wxApi.getWXAppSupportAPI() >= Build.EMOJI_SUPPORTED_SDK_INT;
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		int result = 0;

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				handleSuccessBill();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = R.string.errcode_pay_cancel;
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = R.string.errcode_pay_deny;
				break;
			default:
				result = R.string.errcode_pay_unknown;
				break;
		}
		if(0!=result){
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	public void handleSuccessBill() {
		// 执行交易状态查询
		PayStatusRequest req = new PayStatusRequest();
		req.setToken(mToken);
		mQueryCount = 9;
		Message message = mHandler.obtainMessage();
		message.what = PAY_SERVER_RESULT;
		message.obj = req;
		mHandler.sendMessage(message);
	}

	/**
	 * 创建kidsmind支付订单
	 *
	 * @param payRequest
	 */
	public void pay(PayRequest payRequest) {
		String url = AppConfig.PAY_WX_PREPARE;
		String json = payRequest.toJsonString();
		HttpConnector.httpPost(url, json, new IPostListener() {

			@Override
			public void httpReqResult(String response) {
				// TODO Auto-generated method stub
				PayResponse payResponse = GsonUtil.parse(response,
						PayResponse.class);
				if (payResponse.isSuccess()) {
					callWeChatPay(payResponse, wxApi);
				}
			}
		});
	}

	/**
	 * 调起支付宝应用
	 *
	 * @param payResponse
	 * @return
	 */
	private void callWeChatPay(PayResponse payResponse, IWXAPI api) {
		if (!isWXAppInstalled()) {
			Toast.makeText(this, getString(R.string.not_install_wxapp), Toast.LENGTH_SHORT).show();
			return;
		}
		if (!getWXAppSupportAPI()) {
			Toast.makeText(this, getString(R.string.unsupported_pay_wxapp), Toast.LENGTH_SHORT).show();
			return;
		}
		api.registerApp(WXEntryActivity.WX_APP_ID);

		PayReq req = new PayReq();
		req.appId = WXEntryActivity.WX_APP_ID;// 应用ID
		req.partnerId = WXEntryActivity.WX_PARTNER_ID;// 商户id
		req.prepayId = payResponse.getPrepayid();// 预支付订单
		req.nonceStr = payResponse.getNonceStr();// 随机串
		req.timeStamp = String.valueOf(payResponse.getTimestamp());// 时间戳
		req.packageValue = "Sign=WXPay";// 商家根据文档填写的数据和签名  "Sign=WXPay"
		req.sign = payResponse.getSign();

	   /* List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("appkey", appKey));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genSign(signParams);*/

		// 附加数据
		/*JSONObject extData = new JSONObject();
		try {
			extData.put(PARAM_OUT_TRADE_NO, payResponse.getOutTradeNo());
			extData.put(PARAM_TOTAL_FEE, payResponse.getTotalFee());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		req.extData = extData.toString();*/

		api.sendReq(req);
	}

	public static String genSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (; i < params.size() - 1; i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append(params.get(i).getName());
		sb.append('=');
		sb.append(params.get(i).getValue());

		String sha1 = sha1(sb.toString());
		com.ivmall.android.app.uitls.Log.d(TAG, "genSign, sha1 = " + sha1);
		return sha1;
	}

	public static String sha1(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f'};

		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes());

			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 查询支付宝交易结果
	 *
	 * @param url
	 * @param req
	 */
	private void queryPayStatus(String url, final PayStatusRequest req) {
		String json = req.toJsonString();
		HttpConnector.httpPost(url, json, new IPostListener() {

			@Override
			public void httpReqResult(String response) {
				PayStatusResponse statusResponse = GsonUtil.parse(response, PayStatusResponse.class);
				if (statusResponse.isSuccess()) {
					if (statusResponse.isTradeResult()) {
						((KidsMindApplication) getApplication()).reqUserInfo(WXPayEntryActivity.this, statusResponse.getVipName());
					} else {
						if (mQueryCount > 0 && mHandler != null) {
							Message msg = mHandler.obtainMessage();
							msg.what = PAY_SERVER_RESULT;
							msg.obj = req;
							mHandler.sendMessageDelayed(msg, 2000);
							mQueryCount--;
						}
					}
				}
			}
		});
	}

	private class PayHandler extends Handler {
		private final WeakReference<WXPayEntryActivity> mTarget;

		PayHandler(WXPayEntryActivity target) {
			mTarget = new WeakReference<WXPayEntryActivity>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case PAY_SDK_RESULT:
					break;
				case PAY_SERVER_RESULT:
					// 执行交易状态查询
					PayStatusRequest request = (PayStatusRequest) msg.obj;
					String url = AppConfig.PAY_WX_QUERY;
					queryPayStatus(url, request);
					break;
				default:
					break;
			}
		}
	}
}