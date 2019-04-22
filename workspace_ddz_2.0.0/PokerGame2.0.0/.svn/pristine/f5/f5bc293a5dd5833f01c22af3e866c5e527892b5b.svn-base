package com.mykj.andr.pay.ui;

import java.util.Random;

import com.mykj.game.ddz.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

/**
 * 
 * @author JiangYinZhi
 * 
 */
public class PayProgressDialog extends Dialog {

	private static final int HANDLER_UPDATE_PROMPT = 1001;
	private static final int HANDLER_DELAY_DIMISS = 1002;

	private TextView tvPrompt;

	private int whichPrompt = -1;
	
	String[] mPrompts = { "逛街神马的最开心了~", "主人刷卡的样子最帅了~超爱主人~么么~", "主人真大方，万岁万岁万万岁~" };

	/**
	 * 
	 * @param context
	 * @param dismissTime自动关闭时间
	 */
	public PayProgressDialog(Context context,long dismissTime) {
		super(context, R.style.horiProgressDialog);
		setContentView(R.layout.pay_progress_dialog);
		init();
		Thread mThread = new Thread() {
			@Override
			public void run() {
				super.run();
				while (true) {
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(HANDLER_UPDATE_PROMPT);
				}
			}
		};
		mThread.start();
		mHandler.sendEmptyMessageDelayed(HANDLER_DELAY_DIMISS, dismissTime);
	}

	private void init() {
		tvPrompt = (TextView) findViewById(R.id.tv_prompt);
		tvPrompt.setText(getCurPrompt());
	}

	private String getCurPrompt() {
		Random r = new Random();
		if (whichPrompt == -1) {
			whichPrompt = r.nextInt(mPrompts.length - 1);
		} else if (whichPrompt == mPrompts.length - 1) {
			whichPrompt = 0;
		} else {
			whichPrompt = whichPrompt + 1;
		}
		return mPrompts[whichPrompt];
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_UPDATE_PROMPT:
				tvPrompt.setText(getCurPrompt());
				break;
			case  HANDLER_DELAY_DIMISS:
				dismiss();
			default:
				break;
			}
		};
	};

}
