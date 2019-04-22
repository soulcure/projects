/**
 * 
 */
package com.mingyou.distributor;

import java.util.Hashtable;

import android.os.Message;
import android.util.Log;

import com.mingyou.login.LoginSocket;
import com.mykj.comm.log.MLog;
import com.mykj.comm.util.TCAgentUtil;

/**
 * 网络数据分发器
 * 
 * @author Jason
 * 
 */
public class NetDistributor {

	/** 当前缺省网络监听器，此监听器只存在一个 **/
	private NetDefaultListener _defaultListener = null;

	/** 当前网络监听器，此监听器专门处理由于底层网络异常所抛出的异常信息 **/
	private NetErrorListener _netErrorListener = null;

	private NetDataHandleExceptionCheck _netDataCheck = null;

	/** 穿透信息监听器在，map中的固定key **/
	private final String ProxyInfoKey = "ProxyInfo";

	/** 网络分发监听器map，为了提高查找效率使用map来存储网络监听器 **/
	private Hashtable<String, NetPrivateListener> _privateMap = new Hashtable<String, NetPrivateListener>();

	public NetDistributor() {
	}

	/**
	 * 网络错误信息处理函数，如果数据分发和处理过程中出现异常信息，将通知当前监听器doError方法处理当前错误
	 * 
	 * @param msg
	 * @return 当网络信息为Exception时就返回true
	 */
	private boolean netErrorParse(Message msg) {
		if (_netErrorListener == null) {
			return false;
		}
		if (msg.obj != null && msg.obj instanceof Exception) {
			TCAgentUtil.onTCAgentError((Throwable) msg.obj);
			Exception exception = (Exception) msg.obj;
			if (_netErrorListener.doNetError(exception)) {
				// return true;
			}
			// 关闭心跳发送
			LoginSocket.getInstance().cancelNetTask();
			return true;
		}
		return false;
	}

	/**
	 * 网络私有监听器处理函数，如果数据分发和处理过程中出现异常信息，将通知当前监听器doError方法处理当前错误
	 * 
	 * @param msg
	 * @return Message.obj为NetSocketPak或者byte[]对象，并且已经被监听器实现函数正确处理，此函数才返回true
	 * @throws Exception
	 */
	private boolean netPrivateParse(Message msg) {
		NetPrivateListener listener = null;
		boolean isReceived = false;
		boolean isFindPrivateHandler = false;
		try {
			NetSocketPak data = null;
			byte[] buf = null;
			if (msg.obj != null && msg.obj instanceof NetSocketPak) {
				data = (NetSocketPak) msg.obj;
				final String curListenerKey = getTableKey(data.getMdm_gr(),
						data.getSub_gr());
				listener = _privateMap.get(curListenerKey);
				if (listener != null) {
					isFindPrivateHandler = true;
					if (isReceived = listener.receive(data)) {
						return true;
					} else {
						data.getDataInputStream().reset();
					}
				}
			} else if (msg.obj != null && msg.obj instanceof byte[]) {
				buf = (byte[]) msg.obj; // 注：此模式下仅支持穿透信息
				// 穿透处理
				listener = _privateMap.get(ProxyInfoKey);
				if (listener != null && (listener.isProxyInfo())
						&& (isReceived = listener.doReceive(buf))) {
					return true;
				}
			}
		} finally {
			if (isFindPrivateHandler) {
				MLog.e("TcpShareder", "find PrivateHandler");
			} else {
				MLog.e("TcpShareder", "find not PrivateHandler");
			}
			if (isReceived && listener != null && listener.isOnce()) {
				removeTcpListener(listener);
//				final String mdms[] = getListenerKey(listener);
//				if (mdms != null) { // 移除连带所有监听器
//					for (int i = 0; i < mdms.length; i++) {
//						if (mdms[i] != null) {
//							_privateMap.remove(mdms[i]); // 非持久保持对象将自动移除
//						}
//					}
//				}
			}
		}
		return false;
	}

	/**
	 * 根据主，子命令组装私有监听器map内的通用key
	 * 
	 * @param data
	 * @return
	 */
	private String getTableKey(final short mdm, final short subMdm) {
		return mdm + "_" + subMdm;
	}

	/**
	 * 根据私有监听器的主，子命令数组，生成一个私有监听器map内的key数组
	 * 
	 * @param listener
	 * @return
	 */
	private String[] getListenerKey(NetPrivateListener listener) {
		final short mdms[][] = listener.getMdms();
		if (mdms == null) { // 穿透信息没有命令码
			if (listener.isProxyInfo()) { // 如果是穿透监听器设置特殊key
				return new String[] { ProxyInfoKey };
			}
			return new String[] {};
		}
		String mdmstr[] = new String[mdms.length];
		for (int i = 0; i < mdmstr.length; i++) {
			mdmstr[i] = getTableKey(mdms[i][0], mdms[i][1]);
		}
		return mdmstr;
	}

	/**
	 * 缺省监听器数据分发处理函数，如果数据分发和处理过程中出现异常信息，将通知当前监听器doError方法处理当前错误
	 * 
	 * @param msg
	 * @return Message.obj为NetSocketPak或者byte[]对象，并且已经被监听器实现函数正确处理，此函数才返回true
	 */
	private boolean netDefaultParse(Message msg) {
		if (_defaultListener == null) {
			return false;
		}
		NetSocketPak data = null;
		byte[] buf = null;
		if (msg.obj instanceof NetSocketPak) {
			data = (NetSocketPak) msg.obj;
		} else if (msg.obj instanceof byte[]) {
			buf = (byte[]) msg.obj;
		}
		if (data == null && buf == null) {
			return false;
		}
		if (data != null && _defaultListener.receive(data)) {
			return true;
		} else if (buf != null && _defaultListener.doReceive(buf)) {
			return true;
		}
		return false;
	}

	/**
	 * 添加一个指定的NetPrivateListener私有监听器,此监听器将根据主，子命令码，自动生成映射对象，便于数据分发时快速查找和响应
	 * 
	 * @param listener
	 */
	public void addPrivateListener(NetPrivateListener listener) {

		MLog.e("TcpShareder", "addPrivateListern 主：" + listener.getMdms()[0][0]
				+ "子：+" + listener.getMdms()[0][1]);
		if (listener == null || _privateMap.contains(listener)) {
			MLog.e("TcpShareder", "添加失败");
			return;
		}
		final String mdms[] = getListenerKey(listener);
		for (int i = 0; i < mdms.length; i++) {
			_privateMap.put(mdms[i], listener); // 多命令组，映射同一监听器
		}
	}

	/**
	 * 清理当前所有监听器
	 */
	public void clearListener() {
		if (_privateMap != null) {
			_privateMap.clear();
		}
		_netErrorListener = null;
		_defaultListener = null;
		_netDataCheck = null;
	}

	private boolean isClosed = true;

	/**
	 * 打开分发器
	 */
	public void open() {
		MLog.e("distributor is open");
		isClosed = false;
	}

	/**
	 * 关闭分发器
	 */
	public void close() {
		MLog.e("distributor is closed");
		isClosed = true;
	}

	public void setHandleExceptionCheck(NetDataHandleExceptionCheck ndec) {
		_netDataCheck = ndec;
	}

	/**
	 * 设置当前缺省监听器，此监听器当前只有一个，重复设置将会替换上一个已经设置的监听器
	 * 
	 * @param lis
	 */
	public void setDefaultNetListener(NetDefaultListener lis) {
		_defaultListener = lis;
	}

	/**
	 * 移除指定的私有监听器，此方法移除的依据是私有监听器的主，子命令码
	 * 
	 * @param listener
	 */
	protected void removeTcpListener(NetPrivateListener listener) {
		final String mdms[]=getListenerKey(listener);
		for (int i = 0; i < mdms.length; i++) {
			if(mdms[i]!=null){
				_privateMap.remove(mdms[i]);
			}
		}
	}

	/**
	 * 设置网络错误监听器，此监听当前只有一个，重复设置将会替换上一个已经设置的监听器
	 * 
	 * @param listener
	 */
	public void setErrorListener(NetErrorListener listener) {
		_netErrorListener = listener;
	}

	public void handleMessage(Message msg) {
		if (msg == null || isClosed) {
			return;
		}
		try {
			if (netErrorParse(msg)) {
				return;
			}
			if (netPrivateParse(msg)) { // 轻量级事件分发
				return;
			}
			if (netDefaultParse(msg)) { // 事件分发
				return;
			}
		} catch (Exception e) {
			netDataHandleCheck(e);
		}
	}

	/**
	 * @param e
	 */
	private void netDataHandleCheck(Exception e) {
		if (_netDataCheck != null) {
			_netDataCheck.handle(e);
		} else {
			Log.e("netDataHandleCheck", "********协议处理逻辑异常，请检查代码********");
			e.printStackTrace();
		}
	}
}
