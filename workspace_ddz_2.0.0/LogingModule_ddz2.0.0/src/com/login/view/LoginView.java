package com.login.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mingyou.NoticeMessage.NoticePlacardInfo;
import com.mingyou.NoticeMessage.NoticePlacardProvider;
import com.mingyou.NoticeMessage.NoticePlacardProvider.NoticeListener;
import com.mykj.comm.log.MLog;

public class LoginView extends RelativeLayout{
	
    private static final String TAG="LoginView";
	
	private Context mContext;
	
	private AnimationDrawable animationDrawable;  //移动棋牌骰子滚动动画
	
	private RelativeLayout relAutoScroll;  //公共信息向上滚动控件容器
	private AutoScrollTextView autoScrollTextView; //公共信息向上滚动控件
	
	private RelativeLayout.LayoutParams mLp;  //布局信息
	
	private ImageView imgProgress;    //移动棋牌骰子滚动动画，第一帧
	
	private ImageView imgZYLogo;     //上部logo 图片
	private ImageView imgButtomLogo; //下部logo 图片
	private ImageView imgLogo;       //中间logo 图片

	private TextView tvLoading;     // loading 文字
	private Button btnCancel;       //取消按键

	private TextView tvVersion;		//版本信息
	public LoginView(Context context) {
		super(context);
		mContext=context;
		RelativeLayout container = this;
		
		//container.setBackgroundResource(R.drawable.bg);
		
		imgZYLogo=new ImageView(mContext);
		//imgZYLogo.setImageResource(R.drawable.common_zyh_logo);
		imgZYLogo.setId(GlobalViewId.IMG_ZYH_LOGO);

		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mLp.topMargin=10;
		mLp.leftMargin=10;
		container.addView(imgZYLogo, mLp);

		relAutoScroll = new RelativeLayout(mContext);
		//relAutoScroll.setBackgroundResource(R.drawable.common_notice_panel);
		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLp.addRule(RelativeLayout.BELOW,GlobalViewId.IMG_ZYH_LOGO);
		mLp.topMargin=30;
		container.addView(relAutoScroll, mLp);
		
		autoScrollTextView=new AutoScrollTextView(mContext);
		autoScrollTextView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		if(autoScrollTextView.isNeedInit()){
			//setNoticeListener(); //监听滚动信息
		}
		autoScrollTextView.setVisibility(View.GONE);
		
		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		relAutoScroll.addView(autoScrollTextView, mLp);
		
		imgLogo=new ImageView(mContext);
		imgLogo.setId(GlobalViewId.IMG_LOADING_LOGO);
		//imgLogo.setImageResource(R.drawable.common_chinamobilechess);
		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); 
		mLp.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLp.bottomMargin=30;
		container.addView(imgLogo, mLp);

		imgProgress=new ImageView(mContext);
		//imgProgress.setBackgroundResource(R.drawable.loading01);
		container.addView(imgProgress, mLp);
		
		tvLoading=new TextView(mContext);
		//tvLoading.setText("登录中....");
		tvLoading.setId(GlobalViewId.TV_LOADING);
		tvLoading.setTextColor(Color.WHITE);
		tvLoading.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLp.addRule(RelativeLayout.BELOW,GlobalViewId.IMG_LOADING_LOGO);
		mLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mLp.topMargin=20;
		
		container.addView(tvLoading, mLp);

		btnCancel=new Button(mContext);
		btnCancel.setText("取消");
		//btnCancel.setBackgroundDrawable(UtilDrawableStateList.newSelector(context,R.drawable.btn_orange_normal,R.drawable.btn_orange_pressed));
		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLp.addRule(RelativeLayout.BELOW,GlobalViewId.TV_LOADING);
		mLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mLp.topMargin=20;
		container.addView(btnCancel, mLp);
		
		
		
		imgButtomLogo=new ImageView(mContext);
		//imgButtomLogo.setBackgroundResource(R.drawable.common_chinamobile_logo);
		mLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); 
		mLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mLp.rightMargin=10;
		mLp.bottomMargin=10;
		container.addView(imgButtomLogo, mLp);
		
		//版本信息
		tvVersion = new TextView(mContext);
		tvVersion.setId(GlobalViewId.TV_VERSION);
		tvVersion.setTextColor(0xffffffff);
		tvVersion.setTextSize(20);
		mLp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mLp.leftMargin=20;
		mLp.bottomMargin=20;
		container.addView(tvVersion, mLp);
	}

	
	
	/**
	 * 设置取消按键响应
	 * @param listener
	 */
	public void setBtnCancelOnclick(OnClickListener listener){
		if(btnCancel!=null){
			btnCancel.setOnClickListener(listener);
		}
	}
	
	
	/**
	 * 设置取消按键文字
	 * @param listener
	 */
	public void setBtnCancelText(String text){
		if(btnCancel!=null){
			btnCancel.setText(text);
		}
	}
	
	
	
	/**
	 * 设置取消按键文字颜色
	 * @param listener
	 */
	public void setBtnCancelTextColor(int color){
		if(btnCancel!=null){
			btnCancel.setTextColor(color);
		}
	}
	
	
	/**
	 * 设置滚动动画
	 * @param frames
	 */
	public void startLoadingAnimation(Drawable[] frames){
		if(frames==null){
			imgProgress.setVisibility(View.GONE);
		}else{
			animationDrawable=new AnimationDrawable();

			for(int i=0;i<frames.length;i++){
				animationDrawable.addFrame(frames[i], 150);
			}
			animationDrawable.setOneShot(false);
			imgProgress.setBackgroundDrawable(animationDrawable);
			animationDrawable.start();
		}

	}
	
	

	/**
	 * 设置背景
	 * @param resId
	 */
	public void setBackgroundRes(int resId){
		this.setBackgroundResource(resId);
	}
	
	/**
	 * 设置上部logo图片
	 * @param resId
	 */
	public void setImgZYLogo(int resId){
		if(imgZYLogo!=null){
			imgZYLogo.setImageResource(resId);
		}
	}
	
	
	/**
	 * 设置下部logo图片
	 * @param resId
	 */
	public void setImgButtomZYLogo(int resId){
		if(imgButtomLogo!=null){
			this.removeView(imgButtomLogo);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT); 
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp.rightMargin=10;
			lp.topMargin=10;
			this.addView(imgButtomLogo, lp);
			imgButtomLogo.setBackgroundResource(resId);
		}
	}
	
	/**
	 * 设置中部logo 图片
	 * @param resId
	 */
	public void setImgLogo(int resId){
		if(imgLogo!=null){
			imgLogo.setImageResource(resId);
		}
	}
	
	
	/**
	 * 设置向上文字滚动框控件背景
	 * @param resId
	 */
	public void setScrollTextBackgroundRes(int resId){
		if(relAutoScroll!=null){
			relAutoScroll.setBackgroundResource(resId);
		}
	}
	
	/**
	 * 设置骰子滚动动画控件 第一帧
	 * @param resId
	 */
	public void setImgProgressRes(int resId){
		if(imgProgress!=null){
			imgProgress.setBackgroundResource(resId);
		}
	}
	
	
	/**
	 * 设置loading 文字说明
	 * @param str
	 */
	public void setLoadingText(String str){
		if(tvLoading!=null){
			tvLoading.setText(str);
		}
	}
	
	
	/**
	 * 设置取消按钮背景
	 * @param drawable
	 */
	public void setBtnCancelBackground(Drawable drawable){
		if(btnCancel!=null){
			btnCancel.setBackgroundDrawable(drawable);
		}
	}
	
	
	/**
	 * 停止向上滚动文字
	 */
	public void stopAnimtionScroll(){
		autoScrollTextView.stopAnimtionScroll();
	}
	
	/**
	 * 停止动画滚动
	 */
	public void stopAnimationLogo(){
		if(animationDrawable!=null){
			animationDrawable.stop();
		}
	}
	
	public void setVersion(String ver){
		if(tvVersion != null){
			tvVersion.setText(ver);
		}
	}
	
	/**
	 * 获取登录公告信息
	 * @param context
	 */
	private void setNoticeListener() {
		NoticeListener listener = new NoticeListener() {
			@Override
			public void onFialed() {
				MLog.e(TAG, "获取公告失败");
			}

			@Override
			public void onSucceed() {
				final ArrayList<String> texts = new ArrayList<String>();
				List<NoticePlacardInfo> noticePlacardInfos = NoticePlacardProvider.getInstance().getParsetNoticePersons();
				for(NoticePlacardInfo item:noticePlacardInfos){
					texts.add(item._msg);
				}
				autoScrollTextView.init(texts);
				autoScrollTextView.startAnimtionScroll();
			}
		};
		NoticePlacardProvider.getInstance().addNoticeListener(listener);
		
	}
	
	
	
}
