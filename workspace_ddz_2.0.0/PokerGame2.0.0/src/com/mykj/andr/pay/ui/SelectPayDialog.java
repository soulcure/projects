package com.mykj.andr.pay.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mykj.andr.model.GoodsItem;
import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.model.PayWay;
import com.mykj.andr.pay.model.SubScript;
import com.mykj.andr.ui.ServerDialog;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;

//import com.alipay.android.app.pay.PayTask;

public class SelectPayDialog extends AlertDialog implements
		View.OnClickListener {

	private TextView tvBuyPrompt; // 购买提示
	private TextView tvTitle;// 标题
	private TextView tvPhone; // 客服电话号码文字

	private RelativeLayout reAliBuy;// 阿里购买
	private RelativeLayout reSMSBuy; // 短信购买
	private RelativeLayout reWXBuy; // 微信购买

	private ImageView ivAliCorner;// 阿里脚标
	private ImageView ivSMSCorner;// 短信脚标
	private ImageView ivWXCorner;// 微信脚标

	private ImageView imgClose; // 关闭图片

	/** 快捷购买的商品 */
	private GoodsItem goodsItem;
	private Context mContext;
	private String title;
	private String prompt;
	private boolean isFastBuy;

	private View.OnClickListener mAliPayBtnCallBack;
	private View.OnClickListener mSMSPayBtnCallBack;
	private View.OnClickListener mWXPayBtnCallBack;

	private View.OnClickListener mCancelBtnCallBack;

	private String telPhone;

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param item
	 * @param info
	 */
	public SelectPayDialog(Context context, GoodsItem item, String title,
			String prompt,boolean isFastBuy) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		this.mContext = context;
		this.goodsItem = item;
		this.title = title;
		this.prompt = prompt;
		this.isFastBuy = isFastBuy;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pay_dialog);
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		tvBuyPrompt = (TextView) findViewById(R.id.buy_prompt_label);
		tvTitle = (TextView) findViewById(R.id.system_pop_dialog_title);
		tvPhone = (TextView) findViewById(R.id.phone_label);

		reAliBuy = (RelativeLayout) findViewById(R.id.rl_ali_pay); // 阿里购买
		reSMSBuy = (RelativeLayout) findViewById(R.id.rl_SMS_pay); // 短信购买
		reWXBuy = (RelativeLayout) findViewById(R.id.rl_Weixin_pay); // 微信购买

		ivAliCorner = (ImageView) findViewById(R.id.ali_pay_corner_mark_id);// 阿里脚标
		ivSMSCorner = (ImageView) findViewById(R.id.sms_pay_corner_mark_id);// 短信脚标
		ivWXCorner = (ImageView) findViewById(R.id.weixin_pay_corner_mark_id);// 微信脚标

		imgClose = (ImageView) findViewById(R.id.iv_cancel);

		setBuyPrompt();
		tvPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		if (title != null & title.length() > 0) {
			tvTitle.setText(title);
		} else {
			tvTitle.setText("请选择付款方式");
		}
		if (ServerDialog.SERVER_PHONE != null) {
			telPhone = ServerDialog.SERVER_PHONE;
		} else {
			telPhone = "400-777-9996";
		}
		tvPhone.setText(telPhone);
		ArrayList<PayWay> payWays = null;
		if (goodsItem != null) {
			if (isFastBuy == PayManager.FAST_BUY_TAG) {
				payWays = (ArrayList<PayWay>) goodsItem.getFastBuyPayWayList();
				if (payWays == null || payWays.size() <= 0) {
					payWays = (ArrayList<PayWay>) goodsItem.getPayWays();
				}
			} else if (isFastBuy == PayManager.MARKET_BUY_TAG) {
				payWays = (ArrayList<PayWay>) goodsItem.getPayWays();
			}
		}
		tvPhone.setOnClickListener(this);
		reAliBuy.setOnClickListener(this);
		reSMSBuy.setOnClickListener(this);
		reWXBuy.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		ArrayList<SubScript> subScripts = goodsItem.getSubScripts();
		if (subScripts == null || subScripts.size() == 0) {
			ivAliCorner.setVisibility(View.GONE);
			ivSMSCorner.setVisibility(View.GONE);
			ivWXCorner.setVisibility(View.GONE);
		}
		for (int i = 0; i < subScripts.size(); i++) {
			SubScript subScript = subScripts.get(i);
			PayWay payway = subScript.payway;
			if (payway.payType == 0) {
				PayUtils.setPayCorner(mContext, ivSMSCorner, subScript);
			} else if (payway.payType == 1) {
				PayUtils.setPayCorner(mContext, ivAliCorner, subScript);
			} else if (payway.payType == 147) {
				PayUtils.setPayCorner(mContext, ivWXCorner, subScript);
			}
		}
		imgClose.setOnClickListener(this);
		if (payWays == null || payWays.size() == 0) {
			reAliBuy.setVisibility(View.GONE);
			reWXBuy.setVisibility(View.GONE);
			reSMSBuy.setVisibility(View.VISIBLE);
			return;
		}
		boolean[] payEnters = PayUtils.getPayEnters(mContext, payWays);
		boolean isHasMessageType = payEnters[0];
		boolean isHasAliBAODIANType = payEnters[1];
		boolean isHasWxType = payEnters[2];
		if (isHasMessageType) {
			reSMSBuy.setVisibility(View.VISIBLE);
		}
		if (isHasAliBAODIANType) {
			reAliBuy.setVisibility(View.VISIBLE);
		}
		if (isHasWxType) {
			reWXBuy.setVisibility(View.VISIBLE);
		}
		if (!isHasAliBAODIANType && !isHasWxType && !isHasMessageType) {
			reAliBuy.setVisibility(View.GONE);
			reWXBuy.setVisibility(View.GONE);
			reSMSBuy.setVisibility(View.VISIBLE);
			return;
		}
	}

	private void setBuyPrompt() {
		if (goodsItem == null) {
			return;
		}
		String goodsDescrip = goodsItem.goodsDescrip;
		String goodsPresent = goodsItem.goodsPresented;
		String goodsPrice = goodsItem.pointValue / 100 + "";

		// String mBuyPrompt = "你选择购买 " + goodsName + ",共花费"
		// + goodsPrice + "元";
		String mBuyPrompt = null;
		if(goodsPresent!=null||goodsPresent.length()>0){
//			mBuyPrompt = "仅需" + goodsPrice + "元获得" + goodsDescrip+"(赠"+goodsPresent+")";
			mBuyPrompt = "仅需%d元获得%s(%f)";
		}else{
			mBuyPrompt = "仅需%d元获得%s";
		}

		Spanned msp = PayUtils.getHtmlPrompt(mContext, goodsItem, mBuyPrompt);
		if (tvBuyPrompt != null) {
			if (prompt != null && prompt.length() > 0) {
				Spanned promptMsp = PayUtils.getHtmlPrompt(mContext, goodsItem, prompt);
				tvBuyPrompt.setText(promptMsp);
			} else {
				tvBuyPrompt.setText(msp);
			}
		}
	}

	/**
	 * 设置取消按键监听
	 * 
	 * @param callBack
	 */
	public void setAliPayBtnCallBack(View.OnClickListener callBack) {
		mAliPayBtnCallBack = callBack;
	}

	/**
	 * 设置确定按键监听
	 * 
	 * @param callBack
	 */
	public void setSMSPayBtnCallBack(View.OnClickListener callBack) {
		mSMSPayBtnCallBack = callBack;
	}

	/**
	 * 设置确定按键监听
	 * 
	 * @param callBack
	 */
	public void setWXPayBtnCallBack(View.OnClickListener callBack) {
		mWXPayBtnCallBack = callBack;
	}

	public void setCancelBtnCallBack(View.OnClickListener callBack) {
		mCancelBtnCallBack = callBack;
	}

	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		int id = v.getId();
		if (id == R.id.rl_ali_pay) {
			// 请求快速购买道具
			if (mAliPayBtnCallBack != null) {
				mAliPayBtnCallBack.onClick(v);
			}
			dismiss();
		} else if (id == R.id.rl_SMS_pay) {
			if (mSMSPayBtnCallBack != null) {
				mSMSPayBtnCallBack.onClick(v);
			}
			dismiss();
		} else if (id == R.id.rl_Weixin_pay) {
			if (mWXPayBtnCallBack != null) {
				mWXPayBtnCallBack.onClick(v);
			}
			dismiss();
		} else if (id == R.id.iv_cancel) {
			if (mCancelBtnCallBack != null) {
				mCancelBtnCallBack.onClick(v);
			}
			dismiss();
			AnalyticsUtils.onClickEvent(mContext, "018");
		} else if (id == R.id.phone_label) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ telPhone));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}

	}

	/**
	 * 设置多支付标题
	 * 
	 * @param mTitle
	 */
	public void setSelectPayTitle(String mTitle) {
		if (mTitle != null && mTitle.length() > 0) {
			tvTitle.setText(mTitle);
		}
	}

	/**
	 * 设置多支付标题
	 * 
	 * @param mPrompt
	 */
	public void setSelectPayPrompt(String mPrompt) {
		if (mPrompt != null && mPrompt.length() > 0) {
			tvTitle.setText(mPrompt);
		}
	}

}
