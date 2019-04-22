package com.mykj.andr.pay.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mykj.andr.ui.ServerDialog;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;

public class MMPromptDialog extends Dialog implements OnClickListener {
	private Context mContext;
	private int type;

	private CharSequence msg;
	private TextView tvMsg;
	private CharSequence btnStr;

	private ImageView iv_mm;// mm图片
	private TextView tvPhone; // 客服电话号码文字
	private Button btnConfir;// 确认按钮
	private ImageView imgClose; // 关闭图片

	private String telPhone;

	private View.OnClickListener mConfirmBtnCallBack;
	private View.OnClickListener mCancelBtnCallBack;

	/**
	 * 
	 * @param context
	 * @param type
	 *            0为卖萌 1为笑 2为哭
	 */
	public MMPromptDialog(Context context, CharSequence msg,
			CharSequence btnStr, int type) {
		super(context, R.style.dialog);
		this.type = type;
		this.msg = msg;
		this.btnStr = btnStr;
	}

	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		if (v.getId() == R.id.btnConfir) {
			if (mConfirmBtnCallBack != null) {
				mConfirmBtnCallBack.onClick(v);
			}
		} else if (v.getId() == R.id.phone_label) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ telPhone));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		} else if (v.getId() == R.id.iv_cancel) {
			if (mCancelBtnCallBack != null) {
				mCancelBtnCallBack.onClick(v);
			}
		}
		AnalyticsUtils.onClickEvent(mContext, "001");
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_prompt_dialog);
		init();
	}

	private void init() {
		iv_mm = (ImageView) findViewById(R.id.mm_img);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		tvPhone = (TextView) findViewById(R.id.phone_label);
		btnConfir = (Button) findViewById(R.id.btnConfir);
		imgClose = (ImageView) findViewById(R.id.iv_cancel);

		if (ServerDialog.SERVER_PHONE != null) {
			telPhone = ServerDialog.SERVER_PHONE;
		} else {
			telPhone = "400-777-9996";
		}
		tvPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPhone.setText(telPhone);

		if (type == 0) {
			iv_mm.setImageResource(R.drawable.meinv_kawayi);
		} else if (type == 2) {
			iv_mm.setImageResource(R.drawable.meinv_cry);
		} else {
			iv_mm.setImageResource(R.drawable.meinv_happy);
		}

		if (msg != null && msg.length() > 0) {
			tvMsg.setText(msg);
		}
		if (btnStr != null && btnStr.length() > 0) {
			btnConfir.setText(btnStr);
		}
		btnConfir.setOnClickListener(this);
		tvPhone.setOnClickListener(this);
		imgClose.setOnClickListener(this);
	}

	public void setConfirmBtnCallBack(View.OnClickListener callBack) {
		mConfirmBtnCallBack = callBack;
	}

	public void setCancelBtnCallBack(View.OnClickListener callBack) {
		mCancelBtnCallBack = callBack;
	}

}
