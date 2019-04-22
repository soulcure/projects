package com.mykj.andr.ui;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.login.struc.VersionInfo;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.ui.MySlideButton.OnChangedListener;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.MusicPlayer;
import com.mykj.game.utils.Util;

/**
 * @author wanghj
 *         <p/>
 *         设置对话框
 */
public class SettingDialog extends AlertDialog implements
android.view.View.OnClickListener {
	private Context mContext;
	private Button mButton;
	private static final String TAG = "settingdialog";


	public SettingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting_dialog);
		init();
	}

	private void init() {
		findViewById(R.id.setting_main_cancel).setOnClickListener(this);//退出对话款

		mButton = (Button) findViewById(R.id.setting_main_check_ver);//版本更新
		mButton.setOnClickListener(this);
		((TextView) findViewById(R.id.setting_main_ver)).setText("当前版本" + Util.getVersionName(mContext));
		TextView tvCount = (TextView) findViewById(R.id.userName);
		UserInfo userInfo = HallDataManager.getInstance().getUserMe();
		tvCount.setText(userInfo.account);

		if (LoginInfoManager.getInstance().getLoginType() != AccountItem.ACC_TYPE_TEMP) {
			TextView tvCountType = (TextView) findViewById(R.id.userType);
			tvCountType.setText(R.string.user_account);
		}


		findViewById(R.id.setting_main_change_user).setOnClickListener(this);//切换账号
		//静音模式-----滑动按钮
		MySlideButton musicCheck = (MySlideButton) findViewById(R.id.setting_main_music_checkbox);

        //退出Dialog时保存滑动按钮的状态
		boolean b=FiexedViewHelper.getInstance().isTurnOnBackMusic();
        musicCheck.setCheck(!b);
        musicCheck.SetOnChangedListener(voiceListener);//按钮监听器
		MySlideButton audioCheck = (MySlideButton)findViewById(R.id.setting_main_audio_checkbox);
		boolean c=FiexedViewHelper.getInstance().isTurnOnSound();
        audioCheck.setCheck(!c);
		audioCheck.SetOnChangedListener(audioListener);
        //震动模式-----滑动按钮
        MySlideButton vibrateCheck = (MySlideButton) findViewById(R.id.setting_main_vibrate_checkbox);
        vibrateCheck.SetOnChangedListener(vibListener);//按钮监听器
        vibrateCheck.setCheck(!FiexedViewHelper.getInstance().isVibrate());
    }

    //静音模式监听器

    OnChangedListener voiceListener = new OnChangedListener() {

        @Override
        public void OnChanged(boolean checkState) {
        	
			if (!checkState) {
				FiexedViewHelper.getInstance().turnOnBackMusic();
				MusicPlayer.getInstance(mContext).playBgSound();
			} else {
				FiexedViewHelper.getInstance().turnOffBackMusic();
				MusicPlayer.getInstance(mContext).stopBgSound();
			}
			AnalyticsUtils.onClickEvent(mContext, "096");
		}
	};
	//音效模式监听器
	OnChangedListener audioListener = new OnChangedListener() {

		@Override
		public void OnChanged(boolean checkState) {
			if(!checkState){
				FiexedViewHelper.getInstance().turnOnSound();
			}else{
				FiexedViewHelper.getInstance().turnOffSound();
			}
		}
	};

	//震动模式监听器
	OnChangedListener vibListener = new OnChangedListener() {

		@Override
		public void OnChanged(boolean checkState) {
			if(checkState){
				FiexedViewHelper.getInstance().turnOffVibrate();
			}else{
				FiexedViewHelper.getInstance().turnOnVibrate();
				if(mContext!=null){
					Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
					vib.vibrate(100);
				}
			}
			AnalyticsUtils.onClickEvent(mContext, "097");
		}
	};

	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		int id = v.getId();
		if (id == R.id.setting_main_cancel) {
			dismiss();
		}
		//		else if (id==R.id.setting_main_downother) {
		//			Intent intent =new Intent(mContext, MoregameActivity.class);
		//			mContext.startActivity(intent);
		//		}
		else if (id == R.id.setting_main_check_ver) {
			//检查版本
			final VersionInfo vi = AppConfig.getVersionInfo();
			if (vi != null && vi._upUrl != null) {
				FiexedViewHelper.getInstance().doUpdate(vi, true, mContext, null, 0);
				dismiss();
			} else {
//				Toast.makeText(mContext, "您已经是最新的版本了~", Toast.LENGTH_SHORT).show();
				Util.displayCenterToast(mButton,"您已经是最新的版本了~");
			}
			
			AnalyticsUtils.onClickEvent(mContext, "099");
		} else if (id == R.id.setting_main_change_user) {  //切换账号
			dismiss();
			//			AliBaoDian.getInstance(mContext).switchAccount();
			FiexedViewHelper.getInstance().goToReLoginView();
			AnalyticsUtils.onClickEvent(mContext, "095");
		}
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		FiexedViewHelper.getInstance().saveStatusBits(mContext);
	}


}