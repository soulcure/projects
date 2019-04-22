package com.mykj.andr.ui.fragment;


import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.login.utils.UtilDrawableStateList;
import com.login.view.AccountManager;
import com.login.view.LoginView;
import com.login.view.LoginViewCallBack;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Util;

public class LoginViewFragment extends FragmentModel implements Runnable {
	public static final String TAG = "LoginViewFragment";

	private Activity mAct;

	private LoginViewCallBack mLoginCallBack;

	private LoginView mView;

	//	private ImageView loadingProgress; //进度条动画

	private ProgressBar loadingprogress;//进度条动画

	private TextView tvTips;

	private ImageView image_frame;//玩命加载帧动画

	private ImageView bubble;//气泡帧动画


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mAct = activity;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mView != null)
			handle.sendEmptyMessage(0);
		/**
		 * 进度条动画
		 * **/
		loadingprogress.setVisibility(View.VISIBLE);
		isProgress = true;
		new Thread(this).start();
		/**
		 * 玩命加载图片动画
		 * **/
		image_frame.setImageResource(R.anim.animation_list);
		final AnimationDrawable animframe = (AnimationDrawable) image_frame.getDrawable();
		animframe.start();
		/**
		 * 气泡动画
		 * **/
		final Animation anim = AnimationUtils.loadAnimation(mAct, R.anim.bubble);
		bubble.setAnimation(anim);
		anim.start();

	}


	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * The Fragment's UI is just a simple text view showing its
	 * instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.loading_main, null);
		//		loadingProgress = (ImageView) parent.findViewById(R.id.loading_progress);
		loadingprogress = (ProgressBar) parent.findViewById(R.id.loading_progress);
		tvTips = (TextView) parent.findViewById(R.id.tvTips);
		bubble = (ImageView) parent.findViewById(R.id.bubble);
		image_frame = (ImageView) parent.findViewById(R.id.loading_frame);
		mView = getLoginView(mAct);
		startLoadingAnimation();  //移动滚筛子动画
		return parent;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (loadingprogress != null) {
				loadingprogress.setProgress(msg.arg1);
			}
		};
	};

	private LoginView getLoginView(Activity act) {
		LoginView v = new LoginView(act);
		//        v.setBackgroundRes(R.drawable.new_bg);
		v.setImgLogo(R.drawable.common_logo);
		v.setScrollTextBackgroundRes(R.drawable.common_notice_panel);
		v.setLoadingText(mAct.getResources().getString(R.string.ddz_into_game));
		v.setBtnCancelBackground(UtilDrawableStateList.newSelector(act,
				R.drawable.btn_orange_normal,
				R.drawable.btn_orange_pressed));
		v.setBtnCancelOnclick(new CancleOnclick());
		v.setVersion("V" + Util.getVersionName(mAct));
		return v;

	}


	/**
	 * 快速登录接口
	 */
	public void quickLogin() {
		AccountManager.getInstance().quickEntrance(mLoginCallBack);
	}


	/**
	 * 设置登录回调函数
	 *
	 * @param callback
	 */
	public void setLoginCallBack(LoginViewCallBack callback) {
		mLoginCallBack = callback;
	}


	private void startLoadingAnimation() {

		mView.startLoadingAnimation(null);


	}


	public void setLoginText(String str) {
		loginText = str;
		mDotCount = 0;
	}

	private int mDotCount = 0;
	private String loginText = "";

	private void setText(int count) {
		String additional;
		switch (count) {
		case 0:
			additional = "";
			break;
		case 1:
			additional = ".";
			break;
		case 2:
			additional = "..";
			break;
		case 3:
			additional = "...";
			break;
		default:
			additional = "";
			break;
		}

		if (mView != null) {
			mView.setLoadingText(loginText + additional);
		}
	}

	public void setText(String text) {
		if (mView != null) {
			mView.setLoadingText(text);
		}
	}

	private Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setText(mDotCount);
			mDotCount = (mDotCount + 1) % 4;
			handle.sendEmptyMessageDelayed(0, 500);
		}
	};

	public void setBtnCancelOnclick(OnClickListener listener) {
		if (mView != null) {
			mView.setBtnCancelOnclick(listener);
		}
	}

	@Override
	public int getFragmentTag() {
		return FiexedViewHelper.LOGIN_VIEW;
	}


	@Override
	public void onBackPressed() {
		loginCancle();
	}

	@Override
	public void onStop() {
		super.onStop();
		isProgress = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mView.stopAnimtionScroll();
		mView.stopAnimationLogo();
	}


	private class CancleOnclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			FiexedViewHelper.getInstance().playKeyClick();
			loginCancle();
		}
	}

	private void loginCancle() {
		if (FiexedViewHelper.getInstance().getCurFragment() == FiexedViewHelper.LOGIN_VIEW) {
			FiexedViewHelper.getInstance().goToReLoginView();
		}
	}

	private boolean isProgress = true;

	int i = 0;

	@Override
	public void run() {
		while (isProgress) {
			try {
				i++;
				Thread.sleep(40);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = handle.obtainMessage();
			msg.arg1 = i % 100;
			mHandler.sendMessage(msg);
		}
	}
}
