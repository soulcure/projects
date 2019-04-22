/**
 * 
 */
package com.mingyou.login;

import com.mingyou.distributor.NetErrorListener;
import com.mykj.comm.log.MLog;

/**
 * @author JasonWin8 登陆过程网络错误专用
 */
public class RecoverForLoginError extends NetErrorListener {

	private static final String TAG = "RecoverForLoginError";

	private static RecoverForLoginError _instance = null;

	public static RecoverForLoginError getInstance() {
		if (_instance == null) {
			_instance = new RecoverForLoginError();
		}
		return _instance;
	}

	public void registration() {
		cancelRecover();
		TcpShareder.getInstance().setNetErrorListener(this);
	}

	@Override
	public boolean doNetError(Exception e) {
		// 开始登录超时定时器
		// if (++loginTimeCount <= 3) { // 重试三次通知UI层登录失败
		// LoginSocket.getInstance().closeNet();
		// TCAgentUtil.onTCAgentEvent("登录超时重连", loginTimeCount + "次");
		// MLog.e(TAG, "登录超时不返回，开始重连");
		// LoginSocket.getInstance().connectTcp();
		// } else {
		// TCAgentUtil.onTCAgentEvent("3次登录无响应");
		// loginTimeCount = 0;

		MLog.e(TAG, "登录超时="+e);
		LoginSocket.getInstance().loginFiledAction("您的网速不给力，请稍候再试试！");
		// }
		return true;
	}

	protected void cancelRecover() {
		// loginTimeCount = 0;
	}

	// private int loginTimeCount = 0;


}
