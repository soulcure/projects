package com.mykj.andr.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewParent;

import com.mykj.game.ddz.R;
import com.mykj.game.ddz.R.bool;

public class MySlideButton extends View implements OnTouchListener {  

	private boolean nowChoose = false;// 记录当前按钮是否打开，true为打开，false为关闭  
	private boolean onSlip = false;// 记录用户是否在滑动  
	private float downX, nowX; // 按下时的x，当前的x  
	private Rect btn_on, btn_off;// 打开和关闭状态下，游标的Rect  

	//	    private boolean isChgLsnOn = false;//是否设置监听  
	private OnChangedListener changedLis;  

	private Bitmap bg_on, bg_off, slip_btn;  //上移
	private Context mContext;
	public MySlideButton(Context context, AttributeSet attrs) {  
		super(context, attrs);  
		mContext = context;
		init();
	}  

	public MySlideButton(Context context) {  
		super(context); 
		mContext = context;
		init();
	}  


	private void init() {  
		// 载入图片资源  
		bg_on = BitmapFactory.decodeResource(getResources(),  
				R.drawable.checkbox_open);  
		bg_off = BitmapFactory.decodeResource(getResources(),  
				R.drawable.checkbox_close);  
		slip_btn = BitmapFactory.decodeResource(getResources(),  
				R.drawable.buttonup);  
		// 获得需要的Rect数据  
		btn_on = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());  
		btn_off = new Rect(bg_on.getWidth() - slip_btn.getWidth(), 0,  
				bg_on.getWidth(), slip_btn.getHeight());  
		setOnTouchListener(this);  
	}  
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		widthMeasureSpec = bg_on.getWidth() + MeasureSpec.EXACTLY;
		heightMeasureSpec = bg_on.getHeight() + MeasureSpec.EXACTLY;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public boolean isChecked(){
		return nowChoose;
	}

	public void setCheck(boolean isCheck){
		if(!onSlip && nowChoose != isCheck){
			nowChoose = isCheck;//false 
			invalidate();
		}
	}
	Paint paint1 = new Paint();
	Paint paint2 = new Paint();
	@Override  
	protected void onDraw(Canvas canvas) {  
		// TODO Auto-generated method stub  
		super.onDraw(canvas);  

		paint1.setColor(Color.RED);
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(26);//画笔文字大小
		FontMetrics fontMetrics =paint2.getFontMetrics();
		float fontHeigh=fontMetrics.bottom-fontMetrics.top;
		float textY = bg_on.getHeight()-(bg_on.getHeight()-fontHeigh)/2-fontMetrics.bottom;//文字在Y轴上垂直居中
		float x;     

//		boolean nowChoose =true;
		{  
			if (nowX<(bg_on.getWidth()/2)&&nowChoose==false) {//滑动到前半段与后半段的背景不同,在此做判断  
				canvas.drawBitmap(bg_on, 0, 0, paint1);//画出打开时的背景 
				int textX = slip_btn.getWidth()+(bg_off.getWidth()-slip_btn.getWidth()) / 2-slip_btn.getWidth() / 2;//关闭时文字的x轴 的位置
				canvas.drawText("开",textX, textY, paint2);
			}
			else { 
				canvas.drawBitmap(bg_off, 0, 0, paint1);//画出关闭时的背景  
				int textX=(bg_on.getWidth() - slip_btn.getWidth()*2+slip_btn.getWidth()/2) / 2;//打开时文字的x轴 的位置
				canvas.drawText("关",textX, textY, paint2);

			}
			if (onSlip) {//是否是在滑动状态,    
				if(nowX >= bg_on.getWidth())//是否划出指定范围,不能让游标跑到外头,必须做这个判断  
					x = bg_on.getWidth() - slip_btn.getWidth()/2;//减去游标1/2的长度  
				else  
					x = nowX - slip_btn.getWidth()/2;  
			}else {  
				if(nowChoose)//根据现在的开关状态设置画游标的位置   
					x = btn_off.left; 
				else  
					x = btn_on.left;  
			}  

			if (x < 0 ) //对游标位置进行异常判断..  
				x = 0;  
			else if(x > bg_on.getWidth() - slip_btn.getWidth())  
				x = bg_on.getWidth() - slip_btn.getWidth();  
			int y=(bg_on.getHeight() - slip_btn.getHeight()) / 2; //垂直居中
			canvas.drawBitmap(slip_btn, x,y, null);//画出游标. 

		}  
	}  

	@Override  
	public boolean onTouch(View v, MotionEvent event) {  

		switch (event.getAction()) {//根据动作来执行代码  

		case MotionEvent.ACTION_MOVE://滑动
		{
			final ViewParent parent = getParent();
			if (parent != null) {
				parent.requestDisallowInterceptTouchEvent(true);
			} 
	
			onSlip = true;  
			nowX = event.getX();  
		}
		break;  
		case MotionEvent.ACTION_DOWN://按下  
			final ViewParent parent = getParent();
			if (parent != null) {
				parent.requestDisallowInterceptTouchEvent(true);
			}
			if (event.getX() > bg_on.getWidth() || event.getY() > bg_on.getHeight())   
				return false;  
			//
			downX = event.getX();  
			nowX = downX;  
			break;  
		case MotionEvent.ACTION_UP://松开  
			onSlip = false;  
			boolean lastChoose = nowChoose;  
			if (event.getX() >= (bg_on.getWidth()/2)) {
				nowChoose = true; 
			}else{
				nowChoose = false; 
			}
			if(changedLis!=null && (lastChoose != nowChoose))//如果设置了监听器,就调用其方法.  
				changedLis.OnChanged(nowChoose);  
			break;  
		default:  
			break;  
		}  
		invalidate();  
		return true;  
	}  

	public void SetOnChangedListener(OnChangedListener l){//设置监听器,当状态修改的时候  
		changedLis = l;  
	}  

	public interface OnChangedListener {  
		abstract void OnChanged(boolean checkState);  
	}  
}  