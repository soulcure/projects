package com.mykj.andr.pay.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * 单支付的弹框
 * 
 * @author JiangYinZhi
 * 
 */
public class SinglePayDialog extends AlertDialog implements
		View.OnClickListener {

	// private TextView tvTitle; // 标题
	private TextView tvBuyPrompt;// 购买提示
	private TextView tvPayPrompt1;// 购买推荐1
	private TextView tvPayPrompt2;// 购买推荐2

	private TextView tv1;// 提示1
	private TextView tv2;// 提示2

	private TextView tvPhone; // 客服电话号码文字

	private Button btnPayConfirm;// 购买确认按钮button

	private TextView tvGotoGame;// 去游戏的textview
	private ImageView mvImg;

	private LinearLayout llAdvicePayPrompt;// 建议支付提示
	private LinearLayout llPhoneLabel;// 客服电话ll

	private String telPhone;

	private ImageView ivPayCorner;// 脚标

	private ImageView imgClose; // 关闭图片

	/** 快捷购买的商品 */
	private GoodsItem goodsItem;
	private Context mContext;
	private int mvType;
	// private String mTitle;
	private String message;
	private boolean isFastBuy = false;

	private View.OnClickListener mSMSBtnCallBack;
	private View.OnClickListener mAlipayCallBack;
	private View.OnClickListener mWXCallBack;

	private View.OnClickListener mCancelCallBack;

	private View.OnClickListener mGotoGameCallBack;

	private ArrayList<PayWay> mSequencePayWays;

	public static final int HAPPY_MEINV_IMG = 0;
	public static final int KAWAYI_MEINV_IMG = 1;
	public static final int CRY_MEINV_IMG = 2;

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param item
	 * @param title
	 * @param message
	 * @param type
	 *            0标识快捷购买，1表示商城,2单支付方式
	 * 
	 */
	public SinglePayDialog(Context context, GoodsItem item, int mvType,
			String message, boolean isFastBuy) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		this.mContext = context;
		this.goodsItem = item;
		this.message = message;
		this.mvType = mvType;
		this.isFastBuy = isFastBuy;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_pay_dialog);
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		// tvTitle = (TextView) findViewById(R.id.tv_title);
		tvBuyPrompt = (TextView) findViewById(R.id.tv_buy_prompt);

		tvPayPrompt1 = (TextView) findViewById(R.id.tv_pay_prompt1);
		tvPayPrompt2 = (TextView) findViewById(R.id.tv_pay_prompt2);
		tvPhone = (TextView) findViewById(R.id.phone_label);
		tvPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		llPhoneLabel = (LinearLayout) findViewById(R.id.ll_phone_label);
		mvImg = (ImageView) findViewById(R.id.mm_img);

		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);

		btnPayConfirm = (Button) findViewById(R.id.btn_pay_confirm);
		tvGotoGame = (TextView) findViewById(R.id.goto_game_id);

		llAdvicePayPrompt = (LinearLayout) findViewById(R.id.ll_advice_pay_prompt);

		ivPayCorner = (ImageView) findViewById(R.id.pay_corner_mark_id);// 支付按钮脚标

		imgClose = (ImageView) findViewById(R.id.iv_cancel);
		tvGotoGame.getPaint().setUnderlineText(true);
		setBuyPrompt();
		tvGotoGame.setOnClickListener(this);
		btnPayConfirm.setOnClickListener(this);
		tvPayPrompt1.setOnClickListener(this);
		tvPayPrompt2.setOnClickListener(this);
		imgClose.setOnClickListener(this);
		tvPhone.setOnClickListener(this);
		if (ServerDialog.SERVER_PHONE != null) {
			telPhone = ServerDialog.SERVER_PHONE;
		} else {
			telPhone = "400-777-9996";
		}
		tvPhone.setText(telPhone);
		// if (mTitle != null && mTitle.length() > 0) {
		// tvTitle.setText(mTitle);
		// } else {
		// tvTitle.setText("购买确认");
		// }
		if (mvType == HAPPY_MEINV_IMG) {
			mvImg.setImageResource(R.drawable.meinv_happy);
		} else if (mvType == KAWAYI_MEINV_IMG) {
			mvImg.setImageResource(R.drawable.meinv_kawayi);
		} else if (mvType == CRY_MEINV_IMG) {
			mvImg.setImageResource(R.drawable.meinv_cry);
		}
		if (FastBuyModel.fastTip1 != null) {
			tv1.setText(FastBuyModel.fastTip1);
		}
		if (FastBuyModel.fastTip2 != null) {
			tv2.setText(FastBuyModel.fastTip2);
		}
		tvPayPrompt1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPayPrompt2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		if (!FastBuyModel.fastTipOn) {
			llPhoneLabel.setVisibility(View.VISIBLE);
			llAdvicePayPrompt.setVisibility(View.GONE);
		} else {
			llAdvicePayPrompt.setVisibility(View.VISIBLE);
			llPhoneLabel.setVisibility(View.GONE);
		}
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
		setPayUI(payWays);
	}

	private void setBuyPrompt() {
		// if (message != null && message.length() > 0) {
		// tvBuyPrompt.setText(message);
		// return;
		// }
		if (goodsItem == null) {
			return;
		}
		String goodsPresent = goodsItem.goodsPresented;

		String mBuyPrompt = null;
		if (goodsPresent != null && goodsPresent.length() > 0) {
			mBuyPrompt = "仅需%d元获得%s(%f)";
		} else {
			mBuyPrompt = "仅需%d元获得%s";
		}

		Spanned msp = PayUtils.getHtmlPrompt(mContext, goodsItem,
				mBuyPrompt);
		if (tvBuyPrompt != null) {
			if (message != null && message.length() > 0) {
				Spanned promptMsp = PayUtils.getHtmlPrompt(mContext,
						goodsItem, message);
				tvBuyPrompt.setText(promptMsp);
			} else {
				tvBuyPrompt.setText(msp);
			}
		}
	}

	/**
	 * 设置短信按键监听
	 * 
	 * @param callBack
	 */
	public void setSMSCallBack(View.OnClickListener callBack) {
		mSMSBtnCallBack = callBack;
	}

	/**
	 * 设置支付宝按键监听
	 * 
	 * @param callBack
	 */
	public void setAlipayCallBack(View.OnClickListener callBack) {
		mAlipayCallBack = callBack;
	}

	/**
	 * 设置微信按键监听
	 * 
	 * @param callBack
	 */
	public void setWXCallBack(View.OnClickListener callBack) {
		mWXCallBack = callBack;
	}

	public void setCancelCallBack(View.OnClickListener callBack) {
		mCancelCallBack = callBack;
	}

	/**
	 * 
	 */
	public void setGoToGameCallBack(View.OnClickListener callBack) {
//		if (tvGotoGame != null) {
//			tvGotoGame.setVisibility(View.VISIBLE);
//		}
		mGotoGameCallBack = callBack;
	}

	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		int id = v.getId();
		if (id == R.id.btn_pay_confirm) {
			// 请求购买道具
			if (mSequencePayWays == null || mSequencePayWays.size() == 0) {
				setPayBtnOnclick(0, v);
			} else if (mSequencePayWays.size() >= 1) {
				setPayBtnOnclick(mSequencePayWays.get(0).payType, v);
			}
			dismiss();
		} else if (id == R.id.tv_pay_prompt1) {
			if (mSequencePayWays.size() >= 2) {
				setPayBtnOnclick(mSequencePayWays.get(1).payType, v);
			}
			dismiss();
		} else if (id == R.id.tv_pay_prompt2) {
			if (mSequencePayWays.size() >= 3) {
				setPayBtnOnclick(mSequencePayWays.get(2).payType, v);
			}
			dismiss();
		} else if (id == R.id.iv_cancel) {
			if (mCancelCallBack != null) {
				mCancelCallBack.onClick(v);
			}
			dismiss();
			AnalyticsUtils.onClickEvent(mContext, "018");
		} else if (id == R.id.goto_game_id) {
			if (mGotoGameCallBack != null) {
				mGotoGameCallBack.onClick(v);
			}
			dismiss();
		} else if (id == R.id.phone_label) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ telPhone));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}

	}

	private void setPayUI(ArrayList<PayWay> payWays) {
		if (payWays == null || payWays.size() <= 0) {
			setPayBtn(0);
			llAdvicePayPrompt.setVisibility(View.GONE);
			llPhoneLabel.setVisibility(View.VISIBLE);
			return;
		}
		mSequencePayWays = getPaySignSequence(payWays);
		setPayBtn(mSequencePayWays.get(0).payType);
		if (mSequencePayWays.size() == 1) {
			llAdvicePayPrompt.setVisibility(View.GONE);
			llPhoneLabel.setVisibility(View.VISIBLE);
		} else if (mSequencePayWays.size() == 2) {
			if (FastBuyModel.fastTipOn) {
				llAdvicePayPrompt.setVisibility(View.VISIBLE);
				llPhoneLabel.setVisibility(View.GONE);
			} else {
				llAdvicePayPrompt.setVisibility(View.GONE);
				llPhoneLabel.setVisibility(View.VISIBLE);
			}
			setPayAdvice(mSequencePayWays.get(1).payType, tvPayPrompt1);
			tvPayPrompt2.setVisibility(View.GONE);
		} else if (mSequencePayWays.size() == 3) {
			if (FastBuyModel.fastTipOn) {
				llAdvicePayPrompt.setVisibility(View.VISIBLE);
				llPhoneLabel.setVisibility(View.GONE);
			} else {
				llAdvicePayPrompt.setVisibility(View.GONE);
				llPhoneLabel.setVisibility(View.VISIBLE);
			}
			setPayAdvice(mSequencePayWays.get(1).payType, tvPayPrompt1);
			setPayAdvice(mSequencePayWays.get(2).payType, tvPayPrompt2);
		}

	}

	/**
	 * 是否是默认的单支付方式
	 * 
	 * @param paySign
	 * @return 获取顺序的支付方式
	 */
	public ArrayList<PayWay> getPaySignSequence(ArrayList<PayWay> payWays) {
		ArrayList<PayWay> seqPayWays = new ArrayList<PayWay>();
		PayWay containDefaultPayWay1 = null;
		PayWay containDefaultPayWay2 = null;
		PayWay unContainDefaultPayWay = null;
		int defaultSinglePaySign1 = -1;
		int defaultSinglePaySign2 = -1;
		if (FastBuyModel.lastPayOn && goodsItem.lastSucPaySign != -1) {
			defaultSinglePaySign1 = goodsItem.lastSucPaySign;
		} else {
			defaultSinglePaySign1 = FastBuyModel.defaultSinglePaySign1;
			defaultSinglePaySign2 = FastBuyModel.defaultSinglePaySign2;
		}
		for (int i = 0; i < payWays.size(); i++) {
			if (FastBuyModel.lastPayOn && goodsItem.lastSucPaySign != -1) {
				if (payWays.get(i).payType == defaultSinglePaySign1) {
					containDefaultPayWay1 = payWays.get(i);
				} else if (containDefaultPayWay2 == null) {
					containDefaultPayWay2 = payWays.get(i);
				} else {
					unContainDefaultPayWay = payWays.get(i);
				}
			} else {
				if (payWays.get(i).payType == defaultSinglePaySign1) {
					containDefaultPayWay1 = payWays.get(i);
				} else if (payWays.get(i).payType == defaultSinglePaySign2) {
					containDefaultPayWay2 = payWays.get(i);
				} else {
					if (defaultSinglePaySign2 != -1) {
						unContainDefaultPayWay = payWays.get(i);
					}
				}
			}
		}
		if (containDefaultPayWay1 != null) {
			seqPayWays.add(containDefaultPayWay1);
		}
		if (containDefaultPayWay2 != null) {
			seqPayWays.add(containDefaultPayWay2);
		}
		if (unContainDefaultPayWay != null) {
			seqPayWays.add(unContainDefaultPayWay);
		}
		return seqPayWays;
	}

	private void setPayBtn(int paySign) {
		if (paySign == 0) {
			btnPayConfirm.setText("确定");
		} else if (paySign == 1) {
			btnPayConfirm.setText("支付宝购买");
		} else if (paySign == 147) {
			btnPayConfirm.setText("微信购买");
		}
		SubScript subScript = getSubScript(paySign);
		if (subScript == null) {
			ivPayCorner.setVisibility(View.GONE);
		} else {
			PayUtils.setPayCorner(mContext, ivPayCorner, subScript);
		}
	}

	private void setPayAdvice(int paySign, TextView tvPayPrompt) {
		tvPayPrompt.setVisibility(View.VISIBLE);
		SubScript subScript = getSubScript(paySign);
		String paySignTxt = "";
		if (paySign == 0) {
			paySignTxt = "短信购买";
		} else if (paySign == 1) {
			paySignTxt = "支付宝";
		} else if (paySign == 147) {
			paySignTxt = "微信";
		}
		if (subScript != null && subScript.subIcon == 2 && subScript.ritio > 0) {
			paySignTxt = paySignTxt + "(加赠" + subScript.ritio + "%)";
		}
		paySignTxt = " " + paySignTxt + " ";
		tvPayPrompt.setText(paySignTxt);
	}

	private SubScript getSubScript(int paySign) {
		ArrayList<SubScript> tempSubScripts = goodsItem.getSubScripts();
		for (int i = 0; i < tempSubScripts.size(); i++) {
			if (tempSubScripts.get(i).payway.payType == paySign) {
				return tempSubScripts.get(i);
			}
		}
		return null;
	}

	private void setPayBtnOnclick(int paySign, View view) {
		if (paySign == 0) {
			if (mSMSBtnCallBack != null) {
				mSMSBtnCallBack.onClick(view);
			}
		} else if (paySign == 1) {
			if (mAlipayCallBack != null) {
				mAlipayCallBack.onClick(view);
			}
		} else if (paySign == 147) {
			if (mWXCallBack != null) {
				mWXCallBack.onClick(view);
			}
		}
	}

	public Button getPayConfirm() {
		return btnPayConfirm;
	}
}
