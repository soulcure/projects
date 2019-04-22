package com.mykj.andr.lottoround;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mykj.game.ddz.R;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.Util.DownloadResultListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LottoRoundView extends RelativeLayout{
	
	private final int HANDLER_UPDATE_LIGHT = 1;  //刷新闪灯
	private final int HANDLER_RUN = 2; //转动转盘
	private final int HANDLER_UPDATE_ITEM = 3;
	
	
	private final int LIGHT_NUM = 24; //闪灯个数

	private final int ITEM_NUM = 10; //奖品个数，10个
	
	private int scrW = 0;       //屏幕宽
	private int scrH = 0;       //屏幕高
	private float scale = 0;    //缩放比例
	private int status = 0;     //闪灯状态标识，根据此标识0和1闪灯交替闪烁

	private Area bgArea = new Area();  //背景区域
	private Area ptArea = new Area();  //指针区域
	List<Area> lightAreaList = new ArrayList<LottoRoundView.Area>(24);   //闪灯区域

	Drawable bgBm;             //背景图片
	Drawable ptBm;             //指针图片
	Drawable lightBm1;         //闪灯图片1
	Drawable lightBm2;			//闪灯图片2
	
	Button closeBtn;           //关闭按钮
	Button startBtn;           //开始按钮
	View.OnClickListener closeLis;  //关闭按钮响应
	View.OnClickListener startLis;  //开始按钮响应
	OnRoundFinishListener finishLis; //动画结束
	
	private Canvas canvas = new Canvas();  //画布，中间转盘
	Bitmap fgBm = null;  //画布所用位图，中间转盘
	Area fgArea = new Area(); //转盘在布局中的位置
	Area itemArea = new Area(); //物品在转盘中的位置
	Matrix fgMatrix = new Matrix(); //转盘矩阵
	final float itemWidth = 100;   //物品宽
	final float itemHeight = 100;  //物品高
	final float textSize = 28;     //奖品文字大小
	Area noteArea = new Area();    //提示文字区域
	TextView note;                 //提示文字控件
	CharSequence noteStr;          //提示文字内容
	private String filePath = Util.getLotteryDir() + "/"; //物品图片存储路径
	private int drawImgBit = 0;               //物品图片位图，为0的表示没绘制，为1的表示已绘制
	private int drawTextBit = 0;              //物品文字位图，为0的表示没绘制，为1的表示已绘制
	Paint paint = new Paint();                //绘制文字时的画笔
	float textY = 0;                          //物品文字y坐标
	private boolean isDestroyed = false;      //用来标志是否被销毁，销毁后不应该做任何事
	float curDegree = 180.0f/ITEM_NUM;        //当前角度，默认指向交界处
	private boolean inRun = false;            //是否在转
	int runtime = 0;                          //已经转的时间，用来记录转到什么阶段了
	private float finalDegree = 180.0f/ITEM_NUM; //两个交界的地方
	private int resultIndex = -1;             //结果索引，默认是个错误值

	
	
	public LottoRoundView(Context context){
		super(context);
	}
	
	public LottoRoundView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public LottoRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 初始化背景，设置背景图片和区域
	 */
	private void initBg(){
		bgArea.width = (int)(680 * scale);
		bgArea.height = (int)(675 * scale);
		bgArea.x = (scrW - bgArea.width)/2;
		bgArea.y = (scrH - bgArea.height)/2;
		if(bgBm == null){
			bgBm = getContext().getResources().getDrawable(R.drawable.lottoround_bg);
		}
		bgBm.setBounds(bgArea.x, bgArea.y, bgArea.x + bgArea.width, bgArea.y + bgArea.height);
	}
	
	
	/**
	 * 初始化指针，设置指针图片和区域
	 */
	private void initPoint(){
		ptArea.width = (int)(59 * scale);
		ptArea.height = (int)(129 * scale);
		ptArea.x = bgArea.x + (bgArea.width - ptArea.width)/2;
		ptArea.y = bgArea.y + (bgArea.height)/2 - ptArea.height - (int)(100 * scale);
		if(ptBm == null){
			ptBm = getContext().getResources().getDrawable(R.drawable.lottoround_pointer);
		}
		ptBm.setBounds(ptArea.x, ptArea.y, ptArea.x+ptArea.width, ptArea.y+ptArea.height);
	}
	
	
	/**
	 * 初始化闪灯，设置闪灯图片以及计算闪灯位置
	 */
	private void initLight(){
		if(lightBm1 == null){
			lightBm1 = getContext().getResources().getDrawable(R.drawable.lottoround_light1);
		}
		if(lightBm2 == null){
			lightBm2 = getContext().getResources().getDrawable(R.drawable.lottoround_light2);
		}
		
		int centerRadiu = bgArea.width/2 - (int)(40 * scale) / 2;  //闪灯中心到背景中心的距离
		float detaAngle = (float) (2 * Math.PI / LIGHT_NUM);  //变化角度，单位是弧度
		float curAngle = 0;
		int centerX = bgArea.x + bgArea.width/2;  //中心坐标
		int centerY = bgArea.y + bgArea.height/2; //中心坐标
		
		synchronized (lightAreaList) {
			if(lightAreaList.size() == 0){
				for(int i = 0; i < LIGHT_NUM;i++){
					Area lightArea = new Area();
					lightArea.width = (int)(40 * scale);
					lightArea.height = (int)(40 * scale);
					lightArea.x= centerX + (int)(Math.cos(curAngle)* centerRadiu) - lightArea.width / 2;
					lightArea.y= centerY + (int)(Math.sin(curAngle)* centerRadiu) - lightArea.height / 2;
					lightAreaList.add(lightArea);
					curAngle += detaAngle;
				}
			}else{
				for(int i = 0; i < lightAreaList.size();i++){
					Area lightArea = lightAreaList.get(i);
					lightArea.width = (int)(40 * scale);
					lightArea.height = (int)(40 * scale);
					lightArea.x= centerX + (int)(Math.cos(curAngle)* centerRadiu) - lightArea.width / 2;
					lightArea.y= centerY + (int)(Math.sin(curAngle)* centerRadiu) - lightArea.height / 2;
					curAngle += detaAngle;
				}
			}
		}
	}

	/**
	 * 初始化关闭按钮
	 */
	private void initClose(){
		closeBtn = (Button)findViewWithTag("close"); //先判断有没有添加，不能重复添加
		if(closeBtn == null){
			closeBtn = new Button(getContext());
			closeBtn.setBackgroundResource(R.drawable.btn_dlg_close);
			closeBtn.setTag("close");
			
			if(closeLis!=null){
				closeBtn.setOnClickListener(closeLis);
			}
			
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)closeBtn.getLayoutParams();
			if(lp == null){
				lp = new RelativeLayout.LayoutParams((int) (80*scale), (int) (80*scale));
				lp.leftMargin = bgArea.x + bgArea.width - (int) (80*scale) - 20;
				lp.topMargin = bgArea.y + 20;
				closeBtn.setLayoutParams(lp);
			}else{
				lp.width = (int) (80*scale);
				lp.height = (int) (80*scale);
				lp.leftMargin = bgArea.x + bgArea.width - (int) (80*scale) - 20;
				lp.topMargin = bgArea.y + 20;
			}
			addView(closeBtn);
			closeBtn.invalidate();
		}
		
	}
	
	
	/**
	 * 初始化开始按钮
	 */
	private void initStart(){
		startBtn = (Button)findViewWithTag("start"); //先判断有没有添加，不能重复添加
		if(startBtn == null){
			startBtn = new Button(getContext());
			startBtn.setBackgroundResource(R.drawable.btn_lottoround_start);
			startBtn.setTag("start");
			if(startLis!=null){
				startBtn.setOnClickListener(startLis);
			}
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)startBtn.getLayoutParams();
			if(lp == null){
				lp = new RelativeLayout.LayoutParams((int) (235*scale), (int) (235*scale));
				lp.leftMargin = bgArea.x + (bgArea.width - (int) (235*scale))/2;
				lp.topMargin = bgArea.y + (bgArea.height - (int)(235*scale))/2;
				startBtn.setLayoutParams(lp);
			}else{
				lp.width = (int) (235*scale);
				lp.height = (int) (235*scale);
				lp.leftMargin = bgArea.x + (bgArea.width - (int) (235*scale))/2;
				lp.topMargin = bgArea.y + (bgArea.height - (int)(235*scale))/2;
			}
			addView(startBtn);
			startBtn.invalidate();
		} 
	}

	/**
	 * 初始化提示文字
	 */
	private void initNote(){
		note = (TextView)findViewWithTag("note");
		if(note == null){
			note = new TextView(getContext());
			note.setTag("note");
			note.setTextColor(0xffffffff);
			note.setSingleLine(true);
			note.setTextSize(TypedValue.COMPLEX_UNIT_PX,25 * scale);
			note.setGravity(Gravity.CENTER);
			note.getPaint().setFakeBoldText(true);
			if(noteStr!=null){
				note.setText(noteStr);
			}
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)note.getLayoutParams();
			if(lp == null){
				lp =  new RelativeLayout.LayoutParams((int)(200*scale), RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.leftMargin = bgArea.x + (bgArea.width - (int) (200*scale))/2;
				lp.topMargin =  (int) (bgArea.y + (bgArea.height - 30*scale)/2 + 50*scale);
				note.setLayoutParams(lp);
			}else{
				lp.width = (int)(200*scale);
				lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
				lp.leftMargin = bgArea.x + (bgArea.width - (int) (200*scale))/2;
				lp.topMargin = (int) (bgArea.y + (bgArea.height - 30*scale)/2 + 80*scale);
			}
			note.setGravity(note.getGravity() | Gravity.CENTER_HORIZONTAL);
			addView(note);
			note.invalidate();
		}
	}
	
	
	/**
	 * 初始化
	 * @param width 可用宽
	 * @param height 可用高
	 */
	private void init(int width, int height){
		scrW = width;
		scrH = height;
		scale = (float) (Math.min(scrW, scrH)/720.0);
		initBg();
		initFg();
		initPoint();
		initLight();
		initClose();
		initStart();
		initNote();
		updateItem();
	}

	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		if(changed){
			init(right - left, bottom - top);
			if(!handler.hasMessages(HANDLER_UPDATE_LIGHT)){
				handler.sendEmptyMessage(HANDLER_UPDATE_LIGHT);
			}
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		draw(canvas);   //group默认不调用自己的draw，所以需要在这调用
		super.dispatchDraw(canvas);
	}

	/**
	 * 绘制闪灯
	 * @param canvas
	 */
	private void drawLight(Canvas canvas) {
		synchronized (lightAreaList) {
			Area area;
			Drawable d1;
			Drawable d2;
			if (status == 0) {
				d1 = lightBm1;
				d2 = lightBm2;
			}else{
				d1 = lightBm2;
				d2 = lightBm1;
			}
			if (d1 != null) {
				/**从0开始，隔着绘制*/
				for (int i = 0; i < lightAreaList.size(); i = i + 2) {
					area = lightAreaList.get(i);
					d1.setBounds(area.x, area.y, area.x + area.width,
							area.y + area.height);
					d1.draw(canvas);
				}
			}
			if (d2 != null) {
				/**从1开始，隔着绘制*/
				for (int i = 1; i < lightAreaList.size(); i = i + 2) {
					area = lightAreaList.get(i);
					d2.setBounds(area.x, area.y, area.x + area.width,
							area.y + area.height);
					d2.draw(canvas);
				}
			}
		}
	}
	
	/** 
	 * 绘制
	 */
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(!inRun){ //没在转，就绘制抗锯齿的，默认绘制是不抗锯齿的
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		}
		if(bgBm!=null){ //绘制背景
			bgBm.draw(canvas);
		}
		drawFg(canvas);  //绘制转盘
		if(ptBm!=null){  //绘制指针
			ptBm.draw(canvas);
		}
		drawLight(canvas);  //绘制闪灯
	}
	


	/**
	 * 设置关闭监听
	 * @param lis
	 */
	public void setCloseListener(View.OnClickListener lis){
		closeLis = lis;
		if(closeBtn!=null){
			closeBtn.setOnClickListener(closeLis);
		}
	}
	
	/**
	 * 设置开始监听
	 * @param lis
	 */
	public void setStartListener(View.OnClickListener lis){
		startLis = lis;
		if(startBtn!=null){
			startBtn.setOnClickListener(startLis);
		}
	}
	
	/**
	 * 设置动画结束监听
	 * @param lis
	 */
	public void setFinishListener(OnRoundFinishListener lis){
		finishLis = lis;
	}
	
	/**
	 * 下载监听
	 */
	DownloadResultListener downloadListener = new DownloadResultListener() {
		
		@Override
		public void onDownloadSuccess(String url, String name) {
			// TODO Auto-generated method stub
			if(!handler.hasMessages(HANDLER_UPDATE_ITEM))
			handler.sendEmptyMessage(HANDLER_UPDATE_ITEM);
		}
		
		@Override
		public void onDownloadFail(String url, String name) {
			// TODO Auto-generated method stub
			
		}
	};

	/**
	 * 更新物品
	 */
	void updateItem() {
		if (inRun) { //旋转中不更新
			if (!handler.hasMessages(HANDLER_UPDATE_ITEM)) {
				handler.sendEmptyMessageDelayed(HANDLER_UPDATE_ITEM, 500);
			}
			return;
		}
		ArrayList<LottoRoundItem> list = LottoRoundManager.getInstance()
				.getItemList();
		if (list != null) {
			synchronized (list) {
				int i = 0;
				boolean needUp = false;  //是否需要更新，若全画完了就不需要更新，否则需要再次更新
				int canvasW = canvas.getWidth();  //画布宽
				int canvasH = canvas.getHeight(); //画布高
				for (i = 0; i < list.size() && i < ITEM_NUM; i++) {
					//文字和图片都已经绘制，直接画下一个
					if((drawImgBit & (1 << i)) != 0 && (drawTextBit & (1 << i)) != 0){
						continue;
					}
					LottoRoundItem item = list.get(i);
					canvas.save();
					canvas.translate(canvasW / 2, canvasH / 2);   //平移到中心
					canvas.rotate(-i * (360 / ITEM_NUM));    //反向旋转
					canvas.translate(-canvasW / 2, -canvasH / 2);  //回移
					
					if ((drawImgBit & (1 << i)) == 0) {  //图片没绘制
						Drawable draw = Util.getDrawableFromFile(filePath
								+ item.getFileName());
						if (draw == null) { //需要下载
							Util.downloadFile(
									LottoRoundManager.getInstance().multiLurl + item.getFileName(),
									filePath + item.getFileName(), downloadListener);
							needUp = true;
						} else {
							draw.setBounds(itemArea.x, itemArea.y, itemArea.x
									+ itemArea.width, itemArea.y + itemArea.height);
							draw.draw(canvas);
							drawImgBit = (drawImgBit | (1 << i));
						}
					}
					
					String desc = item.getDesc();
					if (!Util.isEmptyStr(desc)) {
						if((drawTextBit & (1 << i))==0){  //文字需要绘制
							float descW = paint.measureText(desc);
							float descX = itemArea.x + (itemArea.width - descW) / 2;
							canvas.drawText(desc, descX, textY, paint);
							drawTextBit = (drawTextBit | (1<<i));
						}
						
					}
					canvas.restore();
				}
				if(needUp){
					if (!handler.hasMessages(HANDLER_UPDATE_ITEM)) {
						handler.sendEmptyMessageDelayed(HANDLER_UPDATE_ITEM, 1000);
					}
				}
			}
			
		}else{
			if (!handler.hasMessages(HANDLER_UPDATE_ITEM)) {
				handler.sendEmptyMessageDelayed(HANDLER_UPDATE_ITEM, 1000);
			}
		}
	}
	
	Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			if(isDestroyed){
				return;
			}
			switch (msg.what) {
			case HANDLER_UPDATE_LIGHT:   //闪灯
			{
				if(status == 0){   //切换状态
					status = 1;
				}else{
					status = 0;
				}
				if(!inRun){ //没转的时候自己更新
					invalidate();
				}
				sendEmptyMessageDelayed(HANDLER_UPDATE_LIGHT, 1000);
			}
				break;
			case HANDLER_RUN:{  //转
				calculateRoundProgress();
				postInvalidate();
				if(inRun){
					sendEmptyMessageDelayed(HANDLER_RUN, timeSegment);
				}
			}
			break;
			case HANDLER_UPDATE_ITEM:
			{
				updateItem();
			}
			break;
			default:
				break;
			}
		};
	};
	
	private int timeSegment = 30;    //时间段，单位毫秒，1000/timeSegment得到1秒时间片数量，越小越快
	private int accelerateTime = 30; //加速到最大速度所需时间片
	private int uniformTime = 30;   //最大速度运行时间片
	private int decelerateTime = 90; //从最大速度减到5所需时间片
	private int smallDecelerateTime = 30; //速度从5到0的最少时间片，不能小于30
	private int maxSpeed = 30; //最大速度，不能小于5，最好不小于20
	
	/**
	 * 计算旋转进度
	 */
	private void calculateRoundProgress(){
		runtime++;
		if(smallDecelerateTime<30){
			smallDecelerateTime = 30;
		}
		if(maxSpeed < 5){
			maxSpeed = 5;
		}
		if(runtime < accelerateTime){ //小于60加速
//			curDegree += 2 * (runtime/6);
			curDegree += maxSpeed*runtime/accelerateTime;
		}else if(runtime < accelerateTime + uniformTime){ //60-180匀速
			curDegree += maxSpeed;
		}else if(runtime < accelerateTime + uniformTime + decelerateTime){ //180-270减速
//			curDegree += ((accelerateTime + uniformTime + decelerateTime + smallDecelerateTime - runtime)/6);
			curDegree += maxSpeed - (runtime - accelerateTime - uniformTime)*(maxSpeed - 5)/decelerateTime;
		}else{ //大于270慢慢减速到0
			float deta = finalDegree - curDegree; //还需要走的距离
			if(deta<0){  //一圈校准
				deta += 360;
			}
			/**
			 * 计算法则
			 * 最终需要减速距离为2*10+3*5+4*5 = 55；最终减速时间从290开始
			 * 当剩余路程>55时以5的速度走，当剩余路程在35-55时以4的速度走，
			 * 当剩余路程在15-35时以3的速度走，在剩余路程<15时以2的速度走，
			 * 当剩余路程不足2说明已经到达终点
			 */
			int minStopTime = accelerateTime + uniformTime + decelerateTime + smallDecelerateTime;
			//290之前剩余路程不能少于55,295之前剩余路程不能少于35,300之前剩余路程不能少于20，否则以速度5多转一圈
			if((deta < 55 && runtime < minStopTime - 10)||(deta < 35 && runtime < minStopTime - 5) || (deta < 20 && runtime < minStopTime)){ 
				deta += 360;
			}
			if(deta > 55){
				curDegree += 5;
			}else if(deta > 35){
				curDegree += 4;
			}else if(deta > 20){
				curDegree += 3;
			}else if(deta < 2){  //到达终点
				runtime = 0;
				inRun = false;
				curDegree = finalDegree;
				if(finishLis!=null){
					finishLis.onRoundFinish(resultIndex);
				}
			}else{
				curDegree += 2;
			}
		}
		if(curDegree >= 360){   //减圈数循环
			curDegree -= 360;
		}
	}

	/**
	 * 销毁，用来做清理工作
	 */
	public void onDestroy(){
		try{
			isDestroyed = true;
			
			bgBm = null; // 背景图片
			ptBm = null; // 指针图片
			lightBm1 = null; // 闪灯图片1
			lightBm2 = null; // 闪灯图片2
			 
			if(handler.hasMessages(HANDLER_UPDATE_LIGHT)){
				handler.removeMessages(HANDLER_UPDATE_LIGHT);
			}
			if(handler.hasMessages(HANDLER_UPDATE_ITEM)){
				handler.removeMessages(HANDLER_UPDATE_ITEM);
			}
			if(handler.hasMessages(HANDLER_RUN)){
				handler.removeMessages(HANDLER_RUN);
			}
			if(canvas!=null){
				canvas.setBitmap(null);
			}
			if(fgBm!=null && !fgBm.isRecycled()){
				fgBm.recycle();
				fgBm = null;
			}
			
		}catch(Exception e){}
	}

	/**
	 * 初始化转盘
	 */
	private void initFg(){
		/**若canvas没创建，就创建个大小固定的canvas，在draw的时候若大小改变了通过Matrix改变*/
		synchronized (canvas) {
			if(fgBm == null){
				fgBm = Bitmap.createBitmap((int)(604 * scale), (int)(604 * scale),Bitmap.Config.ARGB_8888);
				canvas.setBitmap(fgBm);
				Drawable fg = getResources().getDrawable(R.drawable.lottoround_fg);
				fg.setBounds(0, 0, (int)(604 * scale), (int)(604 * scale));
				fg.draw(canvas);

				//物品位置
				itemArea.x = (int)((604 - itemWidth)/2 * scale);
				itemArea.y = (int)(62 * scale);
				itemArea.width = (int)(itemWidth * scale);
				itemArea.height = (int)(itemHeight * scale);
				
				//画笔
				paint.setTextSize(textSize * scale);
				paint.setColor(0xff793400);
				paint.setAntiAlias(true); 
				
				//物品文字位置
				textY = 45*scale;
			}
		}
		
		//转盘在容器中的位置
		fgArea.width = (int)(604 * scale);
		fgArea.height = (int)(604 * scale);
		fgArea.x = bgArea.x + (bgArea.width - fgArea.width) / 2;
		fgArea.y = bgArea.y + (bgArea.height - fgArea.height) / 2;
	}

	/**
	 * 是否在抽奖
	 * @return
	 */
	public boolean isInRun(){
		return inRun;
	}
	
	
	/**
	 * 画转盘
	 * @param canvas
	 */
	void drawFg(Canvas canvas){
		if(fgBm == null){
			return;
		}
		canvas.save();
		fgMatrix.reset();
		fgMatrix.postTranslate(-fgArea.width/2, -fgArea.height/2);
		fgMatrix.postRotate(curDegree);
		fgMatrix.postTranslate(fgArea.x + fgArea.width/2, fgArea.y + fgArea.height/2);
		canvas.drawBitmap(fgBm, fgMatrix, null);
		canvas.restore();
	}
	
	
	/**
	 * 开始旋转
	 */
	public void startRun(){
		//已经在转就不做任何操作
		if(!inRun && !handler.hasMessages(HANDLER_RUN)){
			inRun = true;
			handler.sendEmptyMessage(HANDLER_RUN);
			setResultIndex(-1); //给个错误位置
		}
	}
	
	
	/**
	 * 设置提示文字
	 * @param noteStr
	 */
	public void setNoteStr(CharSequence noteStr){
		this.noteStr = noteStr;
		if(note!=null){
			note.setText(noteStr);
		}
	}
	
	/**
	 * 设置结果
	 * @param index
	 */
	public void setResultIndex(int index){
		if(!inRun){  //不在转的时候无效
			return;
		}
		resultIndex = index;
		finalDegree = 180.0f/ITEM_NUM + ((int)(Math.random() * ITEM_NUM)) * 360 / ITEM_NUM; //先用交界处做结果
		ArrayList<LottoRoundItem> lst = LottoRoundManager.getInstance().getItemList();
		if(lst!=null){
			synchronized (lst) {
				for(int i = 0; i < lst.size() && i < ITEM_NUM; i++){
					if(lst.get(i).getIndex() == index){
						finalDegree = i * 360.0f / ITEM_NUM;  //最终角度
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 转盘结束监听
	 * @author Administrator
	 *
	 */
	public interface OnRoundFinishListener{
		public void onRoundFinish(int index);
	}
	
	/**
	 * 区域类，用来确定绘制区域
	 * @author Administrator
	 *
	 */
	class Area{
		int x; //x坐标
		int y; //y坐标
		int width; //宽
		int height;  //高
	}
}