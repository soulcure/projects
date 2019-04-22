/**
 * 
 */
package com.mykj.comm.util;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import debug.IP_CONFIG_FILE;

/**
 * @author JasonWin8
 * 
 */
public class TCAgentUtil {
	/** 当前程序上下文 **/
	private static Context _context = null;

	/**
	 * 设置当前程序上下文,
	 * 
	 * @param Context
	 */
	public static void initTCAgent(Context context) {
		_context = context;
	}

	/**
	 * 获得当前程序上下文
	 * 
	 * @return 当前程序上下文
	 */
	public static Context getContext() {
		return _context;
	}

	// TCAgent
	public static void onTCAgentError(Throwable able) {
		try {
			if (IP_CONFIG_FILE.IsTCAgent()) {
				//TCAgent.onError(getContext(), able);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Log.e("onTCAgent", "onTCAgent-"+able);
	}

	public static void onTCAgentEvent(final String arg1, final String arg2, Map<String, Number> map) {
		try {
			if (IP_CONFIG_FILE.IsTCAgent()) {
				//TCAgent.onEvent(getContext(), arg1, arg2, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onTCAgentEvent(final String arg1, final String arg2) {
		try {
			if (IP_CONFIG_FILE.IsTCAgent()) {
				//TCAgent.onEvent(getContext(), arg1, arg2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onTCAgentEvent(final String arg1) {
		try {
			if (IP_CONFIG_FILE.IsTCAgent()) {
				//TCAgent.onEvent(getContext(), arg1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void onResume(Activity act){
		try {
			if (IP_CONFIG_FILE.IsTCAgent()) {
				//TCAgent.onResume(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void onPause(Activity act){
		try {
			if (IP_CONFIG_FILE.IsTCAgent()) {
				//TCAgent.onPause(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
