package com.mykj.comm.util;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class NetDataConnectionState {
	int connectState = TelephonyManager.DATA_CONNECTED;
	boolean isInit = false;
	
	private static NetDataConnectionState instance;
	private NetDataConnectionState(){}
	
	public static NetDataConnectionState getInstance(){
		if(instance == null){
			instance = new NetDataConnectionState(); 
		}
		return instance;
	}
	
	public void init(Context context){
		if(isInit){
			return;
		}
		isInit = true;
		final TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
		PhoneStateListener lis = new PhoneStateListener(){
			public void onDataConnectionStateChanged(int state) {
				connectState = state;
//		          switch(state){
//		              case TelephonyManager.DATA_DISCONNECTED://网络断开
//		            	  
//		                   break;
//		              case TelephonyManager.DATA_CONNECTING://网络正在连接
//		            	  
//		                 break;
//		             case TelephonyManager.DATA_CONNECTED://网络连接上
//		            	 
//		                 break;
//		        }
			}
		};
		mTelephonyMgr.listen(lis, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	/**
	 * 获取当前网络状态
	 * @return
	 */
	public int getConnectState() {
		return connectState;
	}
}
