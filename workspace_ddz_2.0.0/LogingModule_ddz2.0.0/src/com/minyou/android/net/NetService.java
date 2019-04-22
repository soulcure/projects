package com.minyou.android.net;

import java.io.IOException;
import java.util.HashMap;

import android.os.Handler;

/**
 * http和socket连接管理器
 * 
 * @author Jason
 * 
 */
public class NetService {

	private static NetService _instance = null;

	public static NetService getInstance() {
		if (_instance == null) {
			_instance = new NetService();
		}
		return _instance;
	}

	protected NetService() {

	}

	// tcp
	/**
	 * 创建的tcp连接不会自动连接需要主动调用start方法，此对象被自动加入ConnectionManager进行管理
	 * 当http连接废弃的时候必须使用removeTcpConnector将其移除
	 * 
	 * @param up
	 * @param as
	 * @return
	 */
	public TcpConnector createTcpConnection(final UnitProcess up, final AddressStrategy as) {
		// final int count = ConnectionManager.getInstance().tcpCount();
		TcpConnector tcpCon = TcpConnector.createTcpConnection(up, as);
//		if (tcpCon != null) {
//			ConnectionManager.getInstance().appendTcpConnection(tcpCon);
//		}
		return tcpCon;
	}

	/**
	 * 如果已经设置了tcp的唯一标识，将可以通过标识检索到tcpConnector
	 * 
	 * @param target
	 * @return TcpConnector
	 */
	public TcpConnector getTcpConnector(String target) {
		return ConnectionManager.getInstance().getTcpConnector(target);
	}

	/**
	 * 如果已经设置了tcp的唯一标识，将可以通过标识移除tcpConnector
	 * 
	 * @param target
	 */
	public void removeTcpConnector(String target) {
		ConnectionManager.getInstance().removeTcp(target);
	}

	// tcp end

	// http
	/**
	 * 创建的http连接不会自动连接需要主动触发connect，此对象被自动加入ConnectionManager进行管理
	 * 当http连接废弃的时候必须使用removeHttpConnector将其移除
	 * 
	 * @param httpUrl
	 * @param handler
	 * @return
	 */
	public HttpConnector createHttpConnection(final Handler handler) {
		// final int count = ConnectionManager.getInstance().httpCount();
		HttpConnector http = HttpConnector.createHttpConnection(handler);
//		if (http != null) {
//			ConnectionManager.getInstance().appendHttpConnection(http);
//		}
		return http;
	}

	/**
	 * 如果已经设置了http连接的唯一标识，将可以通过标识检索到HttpConnector
	 * 
	 * @param target
	 * @return TcpConnector
	 */
	public HttpConnector getHttpConnector(String target) {
		return ConnectionManager.getInstance().getHttpConnector(target);
	}

	/**
	 * 如果已经设置了http连接的唯一标识，将可以通过标识移除HttpConnector
	 * 
	 * @param target
	 * @return TcpConnector
	 */
	public void removeHttpConnector(String target) {
		ConnectionManager.getInstance().removeHttp(target);
	}

	// http end

	/**
	 * http和socket连接管理池
	 * 
	 * @author Jason
	 * 
	 */
	static class ConnectionManager {

		/** tcp连接池 **/
		private HashMap<String, TcpConnector> _tcpList = null;

		/** http连接池 **/
		private HashMap<String, HttpConnector> _httpList = null;

		/** ConnectionManager单列 **/
		private static ConnectionManager _instance = null;

		/**
		 * 获得ConnectionManager单列对象
		 * 
		 * @return
		 */
		protected static ConnectionManager getInstance() {
			if (_instance == null) {
				_instance = new ConnectionManager();
			}
			return _instance;
		}

		/**
		 * 获得所有TcpConnector对象
		 * 
		 * @return
		 */
		protected HashMap<String, TcpConnector> getTcpList() {
			if (_tcpList == null) {
				_tcpList = new HashMap<String, TcpConnector>(1);
			}
			return _tcpList;
		}

		/**
		 * 获得所有HttpConnector对象
		 * 
		 * @return
		 */
		protected HashMap<String, HttpConnector> getHttpList() {
			if (_httpList == null) {
				_httpList = new HashMap<String, HttpConnector>(2);
			}
			return _httpList;
		}

		/**
		 * 添加一个TcpConnector对象到管理池
		 * 
		 * @param tcp
		 */
		protected void appendTcpConnection(TcpConnector tcp) {
			getTcpList().put(tcp.getTarget(), tcp);
			// Log.i("appendTcpConnection", "getTarget=" + tcp.getTarget());
			// Log.i("appendTcpConnection", "count=" + getTcpList().size());
		}

		/**
		 * 添加一个HttpConnector对象到管理池
		 * 
		 * @param http
		 */
		protected void appendHttpConnection(HttpConnector http) {
			getHttpList().put(http.getTarget(), http);
		}

		/**
		 * 移除一个TcpConnector对象
		 * 
		 * @param tcp
		 */
		protected void removeTcp(TcpConnector tcp) {
			// getTcpList().remove(tcp);
			removeTcp(tcp.getTarget());
		}

		/**
		 * 移除一个TcpConnector对象，根据target
		 * 
		 * @param tcp
		 */
		protected void removeTcp(String tcp) {
			TcpConnector tcpCon = getTcpList().get(tcp);
			if (tcpCon != null) {
				try {
					if(tcpCon.isLive()){
						tcpCon.shutDown();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			getTcpList().remove(tcp);
		}

		/**
		 * 移除一个HttpConnector对象
		 * 
		 * @param tcp
		 */
		protected void removeHttp(HttpConnector http) {
			removeHttp(http.getTarget());
		}

		/**
		 * 移除一个HttpConnector对象，根据target
		 * 
		 * @param http
		 */
		protected void removeHttp(String http) {
			HttpConnector httpCon = getHttpList().get(http);
			if (httpCon != null) {
				httpCon.shutdown();
			}
			getHttpList().remove(http);
		}

		/**
		 * 获得所有TcpConnector对象的数量
		 * 
		 * @return
		 */
		protected int tcpCount() {
			return getTcpList().size();
		}

		/**
		 * 获得所有HttpConnector对象的数量
		 * 
		 * @return
		 */
		protected int httpCount() {
			return getHttpList().size();
		}

		/**
		 * 获得一个已经创建的TcpConnector，根据target
		 * 
		 * @param target
		 * @return
		 */
		protected TcpConnector getTcpConnector(String target) {
			return getTcpList().get(target);
		}

		/**
		 * 获得一个已经创建的HttpConnector，根据target
		 * 
		 * @param target
		 * @return
		 */
		protected HttpConnector getHttpConnector(String target) {
			return getHttpList().get(target);
		}
	}

}
