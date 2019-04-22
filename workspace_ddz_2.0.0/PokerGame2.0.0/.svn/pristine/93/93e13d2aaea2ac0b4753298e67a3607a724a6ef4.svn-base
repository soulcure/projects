package com.mykj.game.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mykj.game.FiexedViewHelper;

import android.os.Handler;

/**
 * 弹出对话框消息队列
 * 
 * @author Administrator
 * 
 */
public class DialogMsgQueue {

	// public static final int NOT_AVALIABLE = -1;
	/**不在分区界面，不弹，推迟*/
	private static final int NOT_CARDZONE = 0xabcdef12;
	
	/** 第一次登录欢迎框*/
	
	public static final int DIALOG_WELCOME = 1;
	
	/** 第一次登录见面礼对话框 */
	public static final int DIALOG_FIRST_LOGIN = 2;
	/** 老用户升级成新用户第一次弹*/
	public static final int DIALOG_OLD_FIRST_LOGIN = 3;
	/** 促销 */
	public static final int DIALOG_DISCOUNT = 4;
	/** 登陆送 */
	public static final int DIALOG_LOGIN_GIFT = 5;
	/**资讯*/
	public static final int DIALOG_NEWS = 6;

	
	public static final int DIALOG_DISCOUNT_OLD = 7;
	
	public static boolean isNewUser = false;
	
	/** 消息队列 */
	private ArrayList<DialogMessage> msgList = new ArrayList<DialogMessage>();

	private OnReceiveMsgListener onReceiveMsgListener;

	/** 是否在处理消息 */
	private boolean isHandleMsg = false;

	private static DialogMsgQueue instance;

	private DialogMsgQueue() {
	}

	public static DialogMsgQueue getInstance() {
		if (instance == null) {
			instance = new DialogMsgQueue();
		}
		return instance;
	}

	/**
	 * 加消息
	 * 
	 * @param msgFlag
	 */
	public void add(int msgFlag) {
		DialogMessage msg = new DialogMessage();
		msg.what = msgFlag;
		add(msg);
	}
	
	/**
	 * 加消息
	 * 
	 * @param msgFlag
	 */
	public void add(int msgFlag, String content) {
		DialogMessage msg = new DialogMessage();
		msg.what = msgFlag;
		msg.obj = content;
		add(msg);
	}

	public void add(DialogMessage msg) {
		if(msg.what == DIALOG_WELCOME){
			isNewUser = true;
		}
		msgList.add(msg);
		if (!isHandleMsg && isRegister()) {
			next();
			Log.i("info", "加入一个消息" + msg.what + "  开始处理");
		} else {
			Log.i("info", "加入一个消息" + msg.what + "  不处理");
			
		}
	}

	/**
	 * 处理完成
	 * 
	 * @param msgFlag
	 */
	public void handleComplete() {
		// 移除消息，进入下一个
		if (msgList.size() > 0){
			Log.i("info", "移除成功"+msgList.get(0));
			msgList.remove(0);
		}else{
			Log.i("info", "移除失败");
		}
		isHandleMsg = false;
		next();
	
	}

	/**
	 * 开始发送下一个消息， 可以在对话框消失的时候调用
	 * 
	 * @return true表示有， false 无
	 */
	private boolean next() {
		if (msgList.size() > 0) {
			//不在分区不弹
			if(FiexedViewHelper.getInstance().getCurFragment() != FiexedViewHelper.CARDZONE_VIEW
					|| !FiexedViewHelper.getInstance().cutLinkFinish){
				if(!mHandler.hasMessages(NOT_CARDZONE)){
					mHandler.sendEmptyMessageDelayed(NOT_CARDZONE, 500);
				}
				return true;
			}
			
			isHandleMsg = true;
			Collections.sort(msgList, new Comparator<DialogMessage>(){

				@Override
				public int compare(DialogMessage lhs, DialogMessage rhs) {
					// TODO Auto-generated method stub
					return lhs.what - rhs.what;
				}
			});
			DialogMessage msg = msgList.get(0);
			Log.i("info", "发送消息去处理："+msg.what);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(msg.what, msg),
					500);
			return true;
		}
		return false;

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what == NOT_CARDZONE){
				next();
			}else if (isRegister()) {
				Log.i("info", "真正处理中，消息队列个数：" +msgList.size()+"消息"+ msg.what);
				onReceiveMsgListener.onReceive((DialogMessage) msg.obj);
			}
		};
	};

	/**
	 * 注册监听
	 * 
	 * @param onReceiveMsgListener
	 */
	public void register(OnReceiveMsgListener onReceiveMsgListener) {
		this.onReceiveMsgListener = onReceiveMsgListener;
		Log.i("info", "注册成功");
		next();
	}
	/**
	 * 注销
	 */
	public void unregister(){
		this.msgList.clear();
		this.isHandleMsg = false;
		this.onReceiveMsgListener = null;
	}
	
	/**
	 * 是否已经注册
	 * @return
	 */
	private boolean isRegister(){
		return onReceiveMsgListener!=null ? true:false;
	}
	
	/**
	 * 接收消息监听
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnReceiveMsgListener {
		public void onReceive(DialogMessage msg);
	}

	public class DialogMessage {
		public int what;
		public int arg1;
		public int arg2;
		public Object obj;
	}
}
