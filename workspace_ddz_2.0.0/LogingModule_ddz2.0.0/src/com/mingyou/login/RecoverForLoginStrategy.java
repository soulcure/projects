/**
 * 
 */
package com.mingyou.login;

import java.util.TimerTask;

import com.mykj.comm.log.MLog;
import com.mykj.comm.util.MTimer;
import com.mykj.comm.util.TCAgentUtil;

/**
 * @author JasonWin8
 * 
 */
public class RecoverForLoginStrategy {
	private static final String TAG = "RecoverForLoginStrategy";

	private static RecoverForLoginStrategy _instance = null;

	public static RecoverForLoginStrategy getInstance() {
		if (_instance == null) {
			_instance = new RecoverForLoginStrategy();
		}
		return _instance;
	}

	private int loginTimeCount = 0;

	private TimerTask _timeTask = null;

	public void registrationLoginStrategy() {
		if (_timeTask == null) {
			_timeTask = new TimerTask() {

				@Override
				public void run() {
					_timeTask=null;
					// 开始登录超时定时器
					if (++loginTimeCount <= 3) { // 重试三次通知UI层登录失败
						LoginSocket.getInstance().loginAgainCloseNet();
						TCAgentUtil.onTCAgentEvent("登录超时重连", loginTimeCount + "次");
						MLog.e(TAG, "登录超时不返回，开始重连");
						LoginSocket.getInstance().connectTcp();
					} else {
						//TCAgentUtil.onTCAgentEvent("3次登录无响应");
						//LoginSocket.getInstance().loginFiledAction("您的网速不给力，请稍候再试试！");
					}
				}
			};
			MTimer.getInstacne().schedule(_timeTask, 15000);
		}
	}

	public void destroyLoginStrategy() {
		loginTimeCount = 0;
		if (_timeTask != null) {
			_timeTask.cancel();
			_timeTask = null;
		}
	}
}
