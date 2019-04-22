/**
 * 
 */
package com.mykj.comm.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 单例的定时任务维护队列，timer会创建线程，所以所有定时任务使用同一定时器较好
 * @author Jason
 * 
 */
public class MTimer {
	private Timer _timer = null;

	private MTimer() {
		_timer = new Timer("instance_timer");
	}

	private static MTimer _instacne = null;

	public static MTimer getInstacne() {
		if (_instacne == null) {
			_instacne = new MTimer();
		}
		return _instacne;
	}

	/** 终止此计时器，丢弃所有当前已安排的任务。 **/
	public void cancel() {
	}

	/** 从此计时器的任务队列中移除所有已取消的任务。 **/
	public int purge() {
		return _timer.purge();
	}

	/** 安排在指定的时间执行指定的任务。 **/
	public void schedule(TimerTask task, Date time) {
		_timer.schedule(task, time);
	}

	/** 安排指定的任务在指定的时间开始进行重复的固定延迟执行。 **/
	public void schedule(TimerTask task, Date firstTime, long period) {
		_timer.schedule(task, firstTime, period);
	}

	/** 安排在指定延迟后执行指定的任务。 **/
	public void schedule(TimerTask task, long delay) {
		_timer.schedule(task, delay);
	}

	/** 安排指定的任务从指定的延迟后开始进行重复的固定延迟执行。 **/
	public void schedule(TimerTask task, long delay, long period) {
		_timer.schedule(task, delay, period);
	}

	/** 安排指定的任务在指定的时间开始进行重复的固定速率执行。 **/
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
		_timer.scheduleAtFixedRate(task, firstTime, period);
	}

	/** 安排指定的任务在指定的延迟后开始进行重复的固定速率执行。 **/
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		_timer.scheduleAtFixedRate(task, delay, period);
	}

}
