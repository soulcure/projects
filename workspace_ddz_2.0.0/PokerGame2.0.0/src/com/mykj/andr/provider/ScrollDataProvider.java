package com.mykj.andr.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.cocos2dx.util.GameUtilJni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.GoodNews;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.utils.Util;
/***
 * 
 * @ClassName: ScrollDataProvider
 * @Description: 赚话费专区，约战专区 用户报名参加显示滚动提示
 * @author
 * @date 2012-10-10 下午05:54:37
 * 
 */
public class ScrollDataProvider {



	private Activity mAct;

	private static ScrollDataProvider instance;
	private TextView     tvBroadcast;
	private Timer timer;

	/**喜报协议码*/
	private static final short MDM__REALTIME=1000;
	private static final short MSUB_REALTIME_MSG = 1;


	/**********handler what***********/
	private static final int HANDLER_GOODNEWS_SUCCESS=1;
	private static final int HANDLER_GOODNEWS_SCROLLER=2;
	
	private static final int HANDLER_GOODNEWS_STOP=3;


	private ArrayList<GoodNews> goodNews = new ArrayList<GoodNews>();

	/**
	 * 私有构造函数
	 * @param act
	 */
	private ScrollDataProvider() {
	}

	/**
	 * 单例构造
	 * @param act
	 * @return
	 */
	public static ScrollDataProvider getInstance(Activity act) {
		synchronized (ScrollDataProvider.class) {
			if (instance == null){
				instance = new ScrollDataProvider();
			}
			instance.mAct = act;
		}
		return instance;
	} 




	@SuppressLint("HandlerLeak")
	private Handler GoodNewsHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_GOODNEWS_SUCCESS:
				GoodNews news=(GoodNews) msg.obj;
				if(FiexedViewHelper.getInstance().getCurFragment() == FiexedViewHelper.COCOS2DX_VIEW){
					GameUtilJni.onGoodNews(news.getMsg());   //发送给游戏
					return;
				}
//		if (indexSrc == FiexedViewHelper.COCOS2DX_VIEW) {
//			GameUtilJni.onGoodNews(content);   //发送给游戏
//		}else )
				synchronized (goodNews) {
					goodNews.add(0, news);
					if(goodNews.size() > 5){
						List<GoodNews> tempList = (List<GoodNews>) goodNews.clone();
						Collections.sort(tempList);
						tempList = tempList.subList(5, tempList.size());
						goodNews.removeAll(tempList);
					}
				}
				startScrollTimer();
				break;
			case HANDLER_GOODNEWS_SCROLLER:    //每3秒变更下一则滚动信息
				if(!goodNews.isEmpty()){
					synchronized (goodNews) {
						GoodNews popNews = goodNews.remove(0);
						sendToGoodNews(popNews.getMsg());   //设置滚动文本信息
						goodNews.add(popNews);
					}
				}
				break;
			case HANDLER_GOODNEWS_STOP:
				setScrollTextView(null);  //隐藏控件显示
				stopScrollTimer();
				break;
			default:
				break;
			}
		}

	};


	//---------------------------public 方法--------------------------------------------------------------------------

	/**
	 * 喜报初始化
	 * @param v
	 */
	public void initialize(View v){
		tvBroadcast = (TextView) v;
		tvBroadcast.setVisibility(View.INVISIBLE);
//		goodNewsListener();
	}



	/**
	 * @Title: removeScrollItems
	 * @Description: 移除全部滚动项
	 * @version: 2012-11-7 下午03:54:54
	 */
	public void removeScrollItems(){
		goodNews.clear();
		stopScrollTimer();
		tvBroadcast = null;
	}

	
	//-------------------------------协议监听------------------------------------------------------
	/**
	 * 喜报协议监听	
	 */
	public void goodNewsListener(){
		// 定义接受数据的协议
		short[][] parseProtocol = { { MDM__REALTIME, MSUB_REALTIME_MSG } };
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);

				GoodNews news = new GoodNews(tdis);
				Message msg=GoodNewsHandler.obtainMessage();
				msg.obj=news;
				msg.what=HANDLER_GOODNEWS_SUCCESS;
				GoodNewsHandler.sendMessage(msg);
				//sendToGoodNews(news.getMsg());
				return true;
			}
		};
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		nPListener.setOnlyRun(false);

	}


	//-------------------------private 方法-----------------------------------------------
	/***
	 * @Title: setScrollTextView
	 * @Description: 设置控件内文本
	 * @param item
	 * @version: 2012-10-11 上午10:09:32
	 */
	private void setScrollTextView(String item){
		if(tvBroadcast == null){
			return;
		}
		if(Util.isEmptyStr(item)){
			tvBroadcast.setVisibility(View.INVISIBLE);
		}else{
			tvBroadcast.setVisibility(View.VISIBLE);
			tvBroadcast.setText(item);
		}
	}



	/**
	 * 按界面分发喜报消息
	 * @param content
	 */
	private void sendToGoodNews(String content){
		if(Util.isEmptyStr(content)){
			return;
		}
		int indexSrc=FiexedViewHelper.getInstance().getCurFragment();
//		if (indexSrc == FiexedViewHelper.COCOS2DX_VIEW) {
//			GameUtilJni.onGoodNews(content);   //发送给游戏
//		}else 
			if(indexSrc==FiexedViewHelper.CARDZONE_VIEW){
			setScrollTextView(content);                //发送给分区
		}
	}




	/****
	 * @Title: startScrollTimer
	 * @Description: 启动滚动计时器
	 * @version: 2012-10-10 下午06:33:11
	 */
	private void startScrollTimer() {
		if(timer!=null)
			return;
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run(){
				if(!goodNews.isEmpty()){
					//Log.e("test", "sendEmptyMessage HANDLER_GOODNEWS_SCROLLER");
					GoodNewsHandler.sendEmptyMessage(HANDLER_GOODNEWS_SCROLLER);
				}else{
					//Log.e("test", "sendEmptyMessage HANDLER_GOODNEWS_STOP");
					GoodNewsHandler.sendEmptyMessage(HANDLER_GOODNEWS_STOP);
				}
			}
		}, 0, 3*1000); // 每隔5秒触发一次
	} 


	/**
	 * 停止计时器
	 */
	private void stopScrollTimer(){
		//Log.e("test", "stopScrollTimer");
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
	}



}
