package com.mykj.andr.ui.widget;

import java.io.File;

import org.cocos2dx.util.GameUtilJni;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.provider.NewCardZoneProvider;
import com.mykj.andr.ui.fragment.NodifyPasswordFragmentDialog;
import com.mykj.andr.ui.tabactivity.ExchangeTabActivity;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.Util.DownloadResultListener;
import com.mykj.game.utils.UtilHelper;

/*****
 * 
 * @ClassName: ExitDialog
 * @Description: 自定义退出对话框
 * @author zhd
 * @date 2013-2-22 下午02:51:34
 * 
 */
public class ExitDialog extends AlertDialog implements
		android.view.View.OnClickListener {

	private final int HANDLER_RECV_DATA = 1;
	private final int HANDLER_UPDATE_IMG = 2;
	private Context mContext;
	Button btn3;
	boolean showSingle = false;
	int singleType = -1;
	String singleTxt;
	int singleVal = -1;
	String tomorrowTxt;
	byte multiStatus;
	int multiVal1;
	int multiVal2;
	short beanAward = 0;
	String multiImgName;
	String multiActName;
	String multiUrl;
	private final short MDM_LOGIN = 12;
	private final short MSUB_CMD_OUT_GAME_INFO_REQ = 19;
	private final short MSUB_CMD_OUT_GAME_LEVEL_RSP = 24;
	private final short MSUB_CMD_OUT_GAME_COMM_RSP = 25;

	public ExitDialog(Context context) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		init();
		reqData();
	}






	private void init() {
		UserInfo userInfo = HallDataManager.getInstance().getUserMe();
		findViewById(R.id.exit_main_content).setVisibility(View.INVISIBLE);
		findViewById(R.id.close).setOnClickListener(this);
		findViewById(R.id.exit_button).setOnClickListener(this);
		if (userInfo != null && userInfo.loginType == AccountItem.ACC_TYPE_TEMP
				&& !LoginInfoManager.getInstance().isBind()) {
			findViewById(R.id.complete_button).setOnClickListener(this);
		} else {
			findViewById(R.id.complete_button).setVisibility(View.GONE);
		}
		btn3 = (Button) findViewById(R.id.button_3);
		btn3.setOnClickListener(this);
	}

	void updateInfo() {
		findViewById(R.id.exit_main_content).setVisibility(View.VISIBLE);
		if (showSingle) {
			findViewById(R.id.multi_contain).setVisibility(View.GONE);
			TextView singleInfo = (TextView) findViewById(R.id.single_text);
			singleInfo.setVisibility(View.VISIBLE);
			singleInfo.setText(singleTxt);
			if (singleType == 1) {
				btn3.setText("再玩会");
			} else if (singleType == 2) {
				btn3.setText("开启礼包");
			} else if (singleType == 3) {
				btn3.setText("再玩会");
			} else if (singleType == 4) {
				btn3.setText("免费参赛");
			}
		} else {
			findViewById(R.id.single_text).setVisibility(View.GONE);
			findViewById(R.id.item1).setOnClickListener(this);
			findViewById(R.id.item2).setOnClickListener(this);
			if (multiStatus == 1) {
				((TextView) findViewById(R.id.item1_title)).setText("神秘宝箱");
				((TextView) findViewById(R.id.item1_desc)).setText(multiVal1
						+ "/" + multiVal2);
				((TextView) findViewById(R.id.item1_todo)).setText("立即去做");
			} else if (multiStatus == 2) {
				((TextView) findViewById(R.id.item1_title)).setText("赢话费");
				((TextView) findViewById(R.id.item1_desc)).setText("玩比赛赢话费");
				((TextView) findViewById(R.id.item1_todo)).setText("立即参与");
			}

			((TextView) findViewById(R.id.item2_title)).setText("精彩活动");
			((TextView) findViewById(R.id.item2_todo)).setText(multiActName);
//			ImageView icon = (ImageView) findViewById(R.id.item2_img);
		}
		((TextView) findViewById(R.id.exit_tomorrow)).setText(tomorrowTxt);
		if(findViewById(R.id.complete_button).getVisibility() == View.VISIBLE){
			((TextView) findViewById(R.id.complete_tips)).setText("绑定账号奖励"
					+ beanAward + "乐豆");
			findViewById(R.id.complete_tips).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_givedou_tips_anim));
		}
	}

	private void setActionImg() {
		if (Util.isEmptyStr(multiImgName)) {
			return;
		}
		String filePath = Util.getIconDir() + "/" + multiImgName;
		File file = new File(filePath);
		Drawable drawable = null;
		if (file.exists()) {
			drawable = Util.getDrawableFromFile(filePath);
		}
		if (drawable != null) {
			((ImageView) findViewById(R.id.item2_img))
					.setImageDrawable(drawable);
		} else {
			Util.downloadFile(multiUrl, filePath, new DownloadResultListener() {

				@Override
				public void onDownloadSuccess(String url, String name) {
					// TODO Auto-generated method stub
					hand.sendEmptyMessage(HANDLER_UPDATE_IMG);
				}

				@Override
				public void onDownloadFail(String url, String name) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		int id = v.getId();
		dismiss();
		if (id == R.id.close) {

		} else if (id == R.id.exit_button) {//退出游戏
			GameUtilJni.exitApplication();
			AnalyticsUtils.onClickEvent(mContext, "109");
		} else if (id == R.id.complete_button) {//完善账号
			/*FiexedViewHelper.getInstance().skipToFragment(
					FiexedViewHelper.NODIFY_ACCOUNT_VIEW);*/
            new NodifyPasswordFragmentDialog(mContext).show();
            AnalyticsUtils.onClickEvent(mContext, "110");
		} else if (id == R.id.button_3) {//继续游戏
			if (showSingle) {
				if (singleType == 2) {
					gotoPack();
				} else if (singleType == 1) {
					quickGame();
				} else {
					gotoRoom(singleVal);
				}
			} else {
				quickGame();
			}
			AnalyticsUtils.onClickEvent(mContext, "111");
		} else if (id == R.id.item1) {
			if (multiStatus == 1) {
				quickGame();
			} else {
				gotoRoom(multiVal1);
			}
			AnalyticsUtils.onClickEvent(mContext, "107");
		} else if (id == R.id.item2) {
			showWeb(multiUrl);
			AnalyticsUtils.onClickEvent(mContext, "108");
		}
	}

	void gotoRoom(int id) {
		int index = NewCardZoneProvider.getInstance().getFirstCardZoneIndex(id); // 获得节点index
		if (index >= 0) {
			Message msg = FiexedViewHelper.getInstance().sHandler
					.obtainMessage(FiexedViewHelper.HANDLER_SHOW_CARDZONE_LIST,
							index, 0);
			FiexedViewHelper.getInstance().sHandler.sendMessage(msg);
		}
	}

	void quickGame() {
		FiexedViewHelper.getInstance().quickGame();
	}

	void showWeb(String url) {
		try {
			UtilHelper.onWeb(mContext, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void gotoPack() {
		try {
			Intent intent = new Intent(mContext, ExchangeTabActivity.class);
			intent.putExtra("TAB_INDEX",1);
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_RECV_DATA: {
				updateInfo();
			}
				break;
			case HANDLER_UPDATE_IMG: {
				setActionImg();
			}
				break;
			}
		};
	};

	void reqData() {
		NetSocketPak data = new NetSocketPak(MDM_LOGIN,
				MSUB_CMD_OUT_GAME_INFO_REQ);
		short[][] parseProtocol = { { MDM_LOGIN, MSUB_CMD_OUT_GAME_LEVEL_RSP },
				{ MDM_LOGIN, MSUB_CMD_OUT_GAME_COMM_RSP } };
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				short sub = netSocketPak.getSub_gr();
				if (sub == MSUB_CMD_OUT_GAME_LEVEL_RSP) {
					singleType = tdis.readByte();
					singleTxt = tdis.readUTFShort();
					singleVal = tdis.readInt();
					beanAward = tdis.readShort();
					tomorrowTxt = tdis.readUTFShort();
					showSingle = true;
					hand.sendEmptyMessage(HANDLER_RECV_DATA);
				} else if (sub == MSUB_CMD_OUT_GAME_COMM_RSP) {
					multiStatus = tdis.readByte();
					multiVal1 = tdis.readInt();
					multiVal2 = tdis.readInt();
					multiImgName = tdis.readUTFByte();
					multiActName = tdis.readUTFByte();
					multiUrl = tdis.readUTFShort();
					beanAward = tdis.readShort();
					tomorrowTxt = tdis.readUTFShort();
					showSingle = false;
					hand.sendEmptyMessage(HANDLER_RECV_DATA);
				}
				return true;
			}
		};
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		NetSocketManager.getInstance().sendData(data);
		nPListener.setOnlyRun(false);
	}
}
