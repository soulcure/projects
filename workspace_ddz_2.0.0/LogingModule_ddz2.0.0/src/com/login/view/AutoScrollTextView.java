package com.login.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.login.utils.DensityConst;
import com.mingyou.NoticeMessage.NoticePlacardInfo;
import com.mingyou.NoticeMessage.NoticePlacardProvider;


public class AutoScrollTextView extends TextView implements OnClickListener{

	private static final int SHOW_LINE = 2;         //文字显示行数 2行
	private static final long INTERVAL_TIME = 5000L;//文字停留间隔时间 5秒
	private static final int SPEED = 10;            //文字向上移动的速度

	private ArrayList<String> dividedTexts=null ;//分割后的字符串集合  

	private int viewWidth;//控件的宽度（渲染之前无法获取控件宽度，等于屏幕宽度）
	private int viewHeight;//控件的高度（渲染之前无法获取控件宽度，这里等于SHOW_LINE*字体的高度+10）

	private int txtNum = 0;//标记正在显示的是哪一个字符串

	private long startMill = 0L;
	private long stopTime = 0L;//已经停留的时间
	private int step = 0;//字符串向上移动的距离,显示完毕需要重置为0

	private Paint mPaint;
	private float perY = 0;//每一个的y值
	private boolean isStarting = false;// 是否开始滚动


	public AutoScrollTextView(Context context) {
		super(context);
		viewWidth = DensityConst.getWidthPixels();
		viewHeight =  (((int)getTextSize() + getPaddingTop() + 5)*2+10);
		setHeight(viewHeight);
		perY = getTextSize()+getPaddingTop()+5;
		setOnClickListener(this);

		setNotices();
	}


	public void init(ArrayList<String> texts){
		mPaint = getPaint();
		mPaint.setColor(getCurrentTextColor());
		//分配分割后字符串的集合
		dividedTexts = new ArrayList<String>();
		dividedTexts.addAll(texts);
	}




	@Override
	public void onClick(View v) {
		if (isStarting)
			stopAnimtionScroll();
		else
			startAnimtionScroll();

	} 


	/**
	 * 用于显示登录成功后加载页面
	 * 
	 * @param context
	 */
	public void setNotices() {
		final ArrayList<String> texts = new ArrayList<String>();
		List<NoticePlacardInfo> noticePlacardInfos = NoticePlacardProvider.getInstance().getParsetNoticePersons();
		for(NoticePlacardInfo item:noticePlacardInfos){
			texts.add(item._msg);
		}
		if(texts.size()>0){
			init(texts);
			startAnimtionScroll();
		}

	}

	public void startAnimtionScroll(){
		isStarting=true;
		new Thread(new GameThread()).start();
	}



	public void stopAnimtionScroll(){
		isStarting = false;
	}


	private class GameThread implements Runnable {
		public void run() {
			while (!Thread.currentThread().isInterrupted() && isStarting) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				// 使用postInvalidate可以直接在线程中更新界面
				postInvalidate();
			}
		}
	};





	public boolean isNeedInit(){
		boolean res=true;
		if(dividedTexts!=null){
			res=false;
		}
		return res;
	}





	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(dividedTexts==null||dividedTexts.size()==0){
			return;
		}

		txtNum=txtNum % dividedTexts.size();

		ArrayList<String> lists=divideText(dividedTexts.get(txtNum));
		if(lists == null){
			return;
		}
		int limitLine = lists.size() < SHOW_LINE ? lists.size() : SHOW_LINE;
		if(stopTime < INTERVAL_TIME){//停留INTERVAL_TIME秒
			if(startMill==0){
				startMill = System.currentTimeMillis();
			}

			stopTime = System.currentTimeMillis() - startMill;

			for(int i=0; i <limitLine;i++){
				canvas.drawText(lists.get(i), 0, perY*(i+1) , mPaint);
			}
		}else{//绘制向上滑动效果
			for(int i=0;i<limitLine;i++){
				canvas.drawText(lists.get(i), 0, perY*(i+1)-step , mPaint);
			}
			step = step + SPEED;
			if(step>= viewHeight-9){
				step = 0;
				startMill = 0L;
				stopTime = 0L;
				txtNum++;
			}
		}
	}








	/**
	 * 分割字符串
	 * @param text 被分割的字符串
	 * @return 返回的分割后的字符串集合
	 */
	private ArrayList<String> divideText(String text){
		if(text == null||text.trim().length() == 0){
			return null;
		}

		ArrayList<String> dividedTexts = new ArrayList<String>();

		float length = 0;    
		StringBuilder sb = new StringBuilder();

		for(int i=0;i<text.length();i++){
			if(length<viewWidth){
				sb.append(text.charAt(i));
				length += mPaint.measureText(text.substring(i, i+1));
				if(i==text.length()-1){
					dividedTexts.add(sb.toString());
				}
			}else{
				dividedTexts.add(sb.toString().substring(0,sb.toString().length()-1));
				sb.delete(0, sb.length()-1) ;
				length= mPaint.measureText(text.substring(i, i+1));
				i--;
			}

		}

		return dividedTexts;
	}



}
