package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ivmall.android.app.impl.OnfinshListener;
import com.smit.android.ivmall.stb.R;

/**
 * Created by koen on 2015/12/18.
 */
public class LuckyPanView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;  //与SurfaceHolder绑定的Canvas
    private Thread drawThread;  //用于绘制的线程
    private boolean isRunning;  //线程的控制开关

    private int mItemCount = 6; //盘块的个数
    private RectF mRange = new RectF();  //盘块的范围
    private int mRadius;  //圆的直径
    private Paint mArcPaint;  //绘制盘块的画笔
    private Paint mTextPaint;  //绘制文字的画笔

    private double mSpeed; //滚动速度
    private volatile float mStartAngle = 0;
    private boolean isShouldEnd;  //是否点击了停止

    private int mCenter;  //控件中心位置
    private Rect bgRect;
    private int mPadding ;
	private boolean isSubmit = false;

	private OnfinshListener listener;
    /**
     * 背景图的bitmap
     */
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.roal_turntable);

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            20,getResources().getDisplayMetrics());

    public LuckyPanView(Context context) {
        super(context);
    }

    public LuckyPanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    /**
     * 设置控件的形状为正方形
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //  获取圆形的直径
        mRadius = width - getPaddingLeft() - getPaddingRight();
        mPadding = getPaddingLeft();
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// 初始化绘制圆弧的画笔
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);
		mArcPaint.setDither(true);
		// 初始化绘制文字的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xFFffffff);
		mTextPaint.setTextSize(mTextSize);
		// 圆弧的绘制范围
		mRange = new RectF(getPaddingLeft(), getPaddingLeft(), mRadius
				+ getPaddingLeft(), mRadius + getPaddingLeft());

		bgRect = new Rect(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredWidth() - mPadding / 2);

		// 开启线程
		isRunning = true;
        drawThread = new Thread(this);
        drawThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		bgRect = new Rect(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredWidth() - mPadding / 2);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// 通知关闭线程
		isRunning = false;
	}

	@Override
	public void run()
	{
		// 不断的进行draw
		while (isRunning)
		{
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();
			try
			{
				if (end - start < 50)
				{
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}

	}

	public void setFinshListener(OnfinshListener ls) {
		listener = ls;
	}

	private void draw()
	{
		try
		{
			// 获得canvas
			mCanvas = mHolder.lockCanvas();
			if (mCanvas != null)
			{
				// 绘制背景图
				drawBg();
				mCanvas.rotate(mStartAngle,mCenter,mCenter);
				mCanvas.drawBitmap(mBgBitmap, null, bgRect, null);
				
				// 如果mSpeed不等于0，则相当于在滚动
				mStartAngle += mSpeed;

				// 点击停止时，设置mSpeed为递减，为0值转盘停止
				if (isShouldEnd)
				{
					mSpeed -= 1;
				}
				if (mSpeed <= 0)
				{
					mSpeed = 0;
					isShouldEnd = false;
					if (isSubmit) {
						listener.finshed();
						isSubmit = false;
					}
				}
				// 根据当前旋转的mStartAngle计算当前滚动到的区域
				calInExactArea(mStartAngle);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (mCanvas != null)
				mHolder.unlockCanvasAndPost(mCanvas);
		}

	}

	/**
	 * 根据当前旋转的mStartAngle计算当前滚动到的区域 绘制背景，不重要，完全为了美观
	 */
	private void drawBg()
	{
		mCanvas.drawColor(0xFFde3637);
		mCanvas.drawBitmap(mBgBitmap, null, bgRect, null);
	}

	/**
	 * 根据当前旋转的mStartAngle计算当前滚动到的区域
	 * 
	 * @param startAngle
	 */
	public void calInExactArea(float startAngle)
	{
		// 让指针从水平向右开始计算
		float rotate = startAngle + 90;
		rotate %= 360.0;
		for (int i = 0; i < mItemCount; i++)
		{
			// 每个的中奖范围
			float from = 360 - (i + 1) * (360 / mItemCount);
			float to = from + 360 - (i) * (360 / mItemCount);

			if ((rotate > from) && (rotate < to))
			{
				return;
			}
		}
	}

	/**
	 * 绘制图片
	 * 
	 * @param startAngle
	 * @param i
	 */
	/*private void drawIcon(float startAngle, int i)
	{
		// 设置图片的宽度为直径的1/8
		int imgWidth = mRadius / 4;
		int imgHeight = imgWidth / 2;

		float angle = (float) ((30 + startAngle) * (Math.PI / 180));

		int x = (int) (mCenter + mRadius / 2 / 1.5 * Math.cos(angle));  //确定它的中心点
		int y = (int) (mCenter + mRadius / 2 / 1.5 * Math.sin(angle));

		// 确定绘制图片的位置
		Rect rect = new Rect(x - imgWidth / 2, y - imgHeight / 2, x + imgWidth
				/ 2, y + imgHeight / 2);

		//mCanvas.drawBitmap(mImgsBitmap[i], null, rect, null);

	}*/

	/**
	 * 绘制文本
	 * 
	 * @param startAngle
	 * @param sweepAngle
	 * @param string
	 */
	/*private void drawText(float startAngle, float sweepAngle, String string)
	{
		Path path = new Path();
		path.addArc(mRange, startAngle, sweepAngle);
		float textWidth = mTextPaint.measureText(string);  //获取字符串宽度
		// 利用水平偏移让文字居中
		float hOffset = (float) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);// 水平偏移
		float vOffset = mRadius / 2 / 6;// 垂直偏移
		mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
	}*/

	/**
	 * 点击开始旋转
	 * 
	 * @param luckyIndex
	 */
	public void luckyStart(int luckyIndex)
	{
		// 每项角度大小
		float angle = (float) (360 / mItemCount);
		// 中奖角度范围（因为指针向上，所以水平第一项旋转到指针指向，需要旋转210-270；）
		float from = 270 - (luckyIndex + 1) * angle;
		float to = from + angle;
		// 停下来时旋转的距离
		float targetFrom = 1 * 360 + from;
		float v1 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetFrom) - 1) / 2;
		float targetTo = 1 * 360 + to;
		float v2 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetTo) - 1) / 2;

		double random = Math.random();
		if (random < 0.1) random = 0.1;
		if (random > 0.9) random = 0.9;
		mSpeed = (float) (v1 + random * (v2 - v1));
		isShouldEnd = false;
		isSubmit = true;
	}

	public void luckyEnd()
	{
		mStartAngle = 0;
		isShouldEnd = true;
	}

	public boolean isStart()
	{
		return mSpeed != 0;
	}

	public boolean isShouldEnd()
	{
		return isShouldEnd;
	}

}
