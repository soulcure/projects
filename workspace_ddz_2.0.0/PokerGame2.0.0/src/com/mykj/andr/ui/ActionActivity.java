package com.mykj.andr.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mykj.andr.model.ActionInfo;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.provider.ActionInfoProvider;
import com.mykj.andr.ui.adapter.ActionAdapter;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.MainApplication;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.CenterUrlHelper;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

public class ActionActivity extends ListActivity implements OnClickListener,
OnItemClickListener {

	private ActionAdapter actionAdapter;
	private ActionInfo actionInfo;
	private ListView lvAction;
	private Activity mAct;

	//	private ImageView btnFreeBean;    //免费赚豆按钮
	/** 下载图片成功后更新UI */
	public static final int REFLASH_LISTVIEW = 10000;
	/** 下载图片失败后更新UI */
	public static final int REFLASH_LISTVIEW_FAIL = 10001;

	private static final int START_ANIMITION = 10002; //启动动画
	private static ActionActivity instance;

	public static ActionActivity getInstance() {
		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_activity);
		MainApplication.sharedApplication().addActivity(this);
		mAct = this;
		instance = this;
		init();
		setActionShowTag();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AnalyticsUtils.onPageStart(this);
		AnalyticsUtils.onResume(this);
		FiexedViewHelper.getInstance().startActivity(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		AnalyticsUtils.onPageEnd(this);
		AnalyticsUtils.onPause(this);
	}

	@Override
	protected void onDestroy(){
		MainApplication.sharedApplication().finishActivity(this);
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		FiexedViewHelper.getInstance().stopActivity(this);
		super.onStop();
	}
	
	private void init() {
		LinearLayout lyListView =(LinearLayout)findViewById(R.id.lyListView);
		LinearLayout lygoGame= (LinearLayout)findViewById(R.id.lygoGame);
		Button goQuick_game = (Button)findViewById(R.id.go_quick_game);
		findViewById(R.id.tvBack).setOnClickListener(this); // 返回按钮
		((TextView)findViewById(R.id.txt_actioncenter)).setText(R.string.actioncenter);

		List<ActionInfo> testList = ActionInfoProvider.getInstance().getList();

		List<ActionInfo> actionInfos = new ArrayList<ActionInfo>();
		actionInfos.addAll(testList);


		lvAction = getListView();

		if (actionInfos.size()>0) {
			lyListView.setVisibility(View.VISIBLE);
			lygoGame.setVisibility(View.GONE);
			actionAdapter = new ActionAdapter(ActionActivity.this,actionInfos);
			lvAction.setAdapter(actionAdapter);
			lvAction.setOnItemClickListener(this);

		}else {
			lygoGame.setVisibility(View.VISIBLE);
			lyListView.setVisibility(View.GONE);
			goQuick_game.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FiexedViewHelper.getInstance().playKeyClick();
					mAct.finish();
					FiexedViewHelper.getInstance().quickGame();
				}
			});

		}

		//		btnFreeBean = (ImageView) findViewById(R.id.btnFreeBean);
		//		btnFreeBean.setOnClickListener(this);
		//		handler.sendEmptyMessageDelayed(START_ANIMITION,300);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		FiexedViewHelper.getInstance().playKeyClick();
		if(id==R.id.tvBack){
			finish();
			//获得赚豆
			//		}else if(id == R.id.btnFreeBean){
			//			//免费赚豆
			//			Intent intent = new Intent(ActionActivity.this, MoregameActivity.class);
			//			startActivity(intent);
		}
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case REFLASH_LISTVIEW: // 下载图片完成后更新ListView
				if (actionAdapter != null)
					actionAdapter.notifyDataSetChanged();// 刷新UI
				break;

			case REFLASH_LISTVIEW_FAIL:
				Log.e("act", "道具图片文件下载失败，错误码为：" + msg.arg1);
				break;

				//			case START_ANIMITION:
				//				{
				//					if(btnFreeBean != null){
				//						AnimationDrawable ad = (AnimationDrawable) btnFreeBean.getBackground();
				//						ad.stop();
				//		                ad.start();
				//					}
				//				}
				//				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		// TODO 跳转web
		FiexedViewHelper.getInstance().playKeyClick();
		actionInfo = ActionInfoProvider.getInstance().getList().get(pos);
		String url = actionInfo.url;
		url = url.contains("?") ? url + "&" : url + "?";
		// 跳转
		if (!Util.isEmptyStr(url)) {
			int userId=FiexedViewHelper.getInstance().getUserId();
			String finalUrl=CenterUrlHelper.getUrl(url,userId);
			int uo=actionInfo.uo;
			UtilHelper.onWeb(ActionActivity.this, finalUrl, uo);
		}

	}


	/**
	 * 设置动画显示信息
	 */
	private void setActionShowTag(){
		UserInfo userInfo = HallDataManager.getInstance().getUserMe();
		String key_tag=userInfo.nickName;
		String tag=Util.getStringSharedPreferences(AppConfig.mContext, key_tag, AppConfig.DEFAULT_TAG);
		String[] strs=tag.split("&");
		if(strs!=null&&strs.length==3){
			strs[0]="1";
			StringBuilder sb=new StringBuilder();
			sb.append(strs[0]).append("&").append(strs[1]).append("&").append(strs[2]);
			Util.setStringSharedPreferences(AppConfig.mContext, key_tag,sb.toString());

		}

	}

}
