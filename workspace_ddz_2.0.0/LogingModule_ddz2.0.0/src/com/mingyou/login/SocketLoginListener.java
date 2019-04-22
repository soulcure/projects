/**
 * 
 */
package com.mingyou.login;

import android.os.Message;

/**
 * socket网络协议监听器，主要用户登录等协议通知监听器成功和失败状态等信息
 * 
 * @author Jason
 * 
 */
public interface SocketLoginListener {

	/**
	 * 协议返回成功状态
	 * 
	 * @param msg
	 */
	void onSuccessed(Message msg);

	/**
	 * 协议返回失败，或处理过程出错
	 * 
	 * @param msg
	 * @param param 参数
	 */
	void onFiled(Message msg, int param);
}
