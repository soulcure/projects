package com.mykj.andr.ui.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.RoomData;
import com.mykj.andr.task.GameTask;
import com.mykj.andr.ui.widget.LoginAssociatedWidget;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.R.bool;

/*****
 * 
 * @ClassName: LoadingWidget
 * @Description:加载控件
 * @author zhanghuadong
 * @date 2012-12-15 下午12:39:44
 * 
 */
public class LoadingFragment extends FragmentModel implements Runnable{
	public static final String TAG = "LoadingFragment";
	public static final String LOGINVIEW_TAG = "LoginFragment";
	public static final String 	LOADING_PROGERESS_RECEIVER = "android.anim.loadingfragment.LOADING_PROGRESS";

	private NodeDataType mType = NodeDataType.NODE_TYPE_NOT_DO;
	private static Activity mAct = null;

	private static final int HANLDER_TIMER = 0;
	/** 延迟消失 */
	private static final int HANDLER_DELAY = 1;

	private ProgressBar  loadingprogress;//进度条动画

	private TextView tvTips;

	private ImageView image_frame;//气泡帧动画

	public enum NodeDataType {
		NODE_TYPE_101, NODE_TYPE_109, NODE_TYPE_111, NODE_TYPE_NOT_DO;
	}

	private ImageView[] loadingIvs = new ImageView[4];

	private ImageView bubble;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mAct = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@SuppressLint("HandlerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.loading_main, null);
		loadingprogress = (ProgressBar)parent.findViewById(R.id.loading_progress);
		tvTips = (TextView) parent.findViewById(R.id.tvTips);
		bubble =(ImageView)parent.findViewById(R.id.bubble);
		image_frame =(ImageView)parent.findViewById(R.id.loading_frame);
		return parent;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(loadingprogress!=null){
				loadingprogress.setProgress(msg.arg1);
			}
		};

	};

	@Override
	public void onResume() {
		super.onResume();
		/**
		 * 玩命加载动画
		 * **/
		image_frame.setImageResource(R.anim.animation_list);
		final AnimationDrawable animframe = (AnimationDrawable)image_frame.getDrawable();
		animframe.start();
		/**
		 * 气泡动画
		 * **/
		final Animation anim =AnimationUtils.loadAnimation(mAct, R.anim.bubble);
		bubble.setAnimation(anim);
		anim.start();
		/**
		 * 进度条动画
		 * **/
		loadingprogress.setVisibility(View.VISIBLE);
		isProgress=true;
		new Thread(this).start();
	}

	@Override
	public int getFragmentTag() {
		return FiexedViewHelper.LOADING_VIEW;
	}

	@Override
	public void onBackPressed() {
		if (_logonViewCallBack != null) {
			_logonViewCallBack.onLoginFailed(null);
		} else {
			cancelLoading();
		}
	}

	private static boolean isProgress=true;

	private int i =0;

	/**
	 * 设置loading 内容
	 * 
	 * @param content
	 */
	public void setLoadingType(String loading, NodeDataType type) {

		if (type != null) {
			this.mType = type;
		}
	}

	public void cancelLoading() {
		if (FiexedViewHelper.getInstance().getCurFragment() == FiexedViewHelper.LOADING_VIEW) {

			switch (mType) {
			case NODE_TYPE_101: // 普通节点(自由、约占)
				// 自由战区/智运会
				RoomData room = HallDataManager.getInstance()
				.getCurrentRoomData(); // 获取速配成功进入房间保存的房间信息
				if (room != null) {
					// 请求离开房间
					GameTask.getInstance().clrearTask();
					LoginAssociatedWidget.getInstance().exitRoom(room.RoomID);
				}
				break;
			case NODE_TYPE_109: // 报名节点
				// 发送退出登录 发送201----106
				if (FiexedViewHelper.getInstance().amusementFragment != null) {
					FiexedViewHelper.getInstance().amusementFragment.exitLogin();
				}
				break;
			case NODE_TYPE_111: // 约战节点
				//				if (FiexedViewHelper.getInstance().challengeFragment != null) {
				//					FiexedViewHelper.getInstance().challengeFragment
				//					.leaveChallenge();
				//				}
				break;
			case NODE_TYPE_NOT_DO: // 不进行操作
				break;
			default:
				break;
			}

			FiexedViewHelper.getInstance().skipToFragment(
					FiexedViewHelper.CARDZONE_VIEW);
		}
	}

	private LogonViewCallBack _logonViewCallBack = null;

	public void setLogonCallBack(LogonViewCallBack logonViewCallBack) {
		_logonViewCallBack = logonViewCallBack;
	}

	public interface LogonViewCallBack {
		public void onLoginsuccessed(Message msg);

		public void onLoginFailed(Message msg);

		public void onLoginThirdParty(Message msg);
	}
	//Progress加载停止，接受广播
	public static class loadingRecevier extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			isProgress = false;//当isProgress为false时，线程关闭
		}
	}
	
	@Override
	public void run() {
		loadingRecevier recevier =new loadingRecevier();
		while(isProgress){
			try {
				i++;
				Thread.sleep(40);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = mHandler.obtainMessage();
			msg.arg1=i%100;
			mHandler.sendMessage(msg);
		}
	}  
}
