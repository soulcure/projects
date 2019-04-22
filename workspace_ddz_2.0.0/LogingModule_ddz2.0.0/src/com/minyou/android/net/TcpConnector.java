package com.minyou.android.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Vector;

import com.mykj.comm.log.MLog;

import debug.IP_CONFIG_FILE;

/**
 * socket连接对象
 * 
 * @author Jason
 * 
 */
public class TcpConnector {

	/** socket网络接受线程对象 **/
	private TcpReceiveHandler _receiver;

	/** socket网络发送线程对象 **/
	private TcpSendHandler _sender;

	/** socket连接对象 **/
	private Socket _socket;

	/** 连接超时时间30s **/
	private int _connectTimeout = 30000;

	/** 接受数据超时时间 **/
	private int _recvTimeout = 40000;

	/** socket连接地址策略 **/
	private AddressStrategy _address = null;

	/** 网络数据及事件处理单元 **/
	private UnitProcess _unprocess;

	private String _target = null;

	private final static String TAG = "SocketConnector";

	private final static String TCP_STRING = "tcp-";

	// /** 网络线程等待 **/
	// private long netThreadSleep = 300;

	private TcpConnector() {
	}

	/**
	 * 创建一个socket连接,需要传入处理单元，地址策略和唯一标识
	 * 
	 * @param up
	 * @param as
	 * @param target
	 * @return
	 */
	protected static TcpConnector createTcpConnection(final UnitProcess up, final AddressStrategy as) {
		if (as == null)
			throw new NullPointerException("TcpConnector createTcpConnection  AddressStrategy is null");
		TcpConnector tcp = new TcpConnector();
		tcp.setUnitProcess(up);
		tcp.setAddressStrategy(as);
		tcp._target = TCP_STRING + tcp.hashCode();
		return tcp;
	}

	/**
	 * 设置当前地址策略处理对象
	 * 
	 * @param as
	 */
	public void setAddressStrategy(AddressStrategy as) {
		if (as == null)
			throw new NullPointerException("setAddressStrategy AddressStrategy is null");
		this._address = as;
	}

	public interface IConnectCallBack {
		/**
		 * 此方法将会在连接正常，并且成功的情况下被调用，由子类实现连接成功后的功能
		 */
		public void connectSucceed();

		/**
		 * 连接失败
		 * 
		 * @param e
		 */
		public void connectFailed(Exception e);

	}

	/**
	 * 
	 * @author Jason
	 * 
	 */
	class Connect implements Runnable {
		IConnectCallBack callBack = null;

		/** 重连次数 */
		int reConnectCount = 0;

		private Connect(IConnectCallBack _callBack, int _reConnectCount) {
			if (_callBack == null) {
				throw new NullPointerException("New Connect _callBack is null");
			}
			if (_reConnectCount <= 0) {
				throw new IllegalArgumentException("New Connect _reConnectCount = " + _reConnectCount);
			}
			callBack = _callBack;
			reConnectCount = _reConnectCount;
		}

		public void run() {
			int currentCount = 0;
			IpPortObj ipport = null;
			while (true) {
				try {
					_socket = new Socket();
					ipport = _address.getIpPort();
					if (ipport != null) {
						connect(_socket, ipport.getIp(), ipport.getPort());
						if (_socket.isConnected()) {
							MLog.c1(IP_CONFIG_FILE.MY_DETECTGAMEDATA, "connect"+" ("+ipport.getIp()+","+ipport.getPort()+") succeed");
							MLog.e(TAG, "getSoTimeOut=" + _socket.getSoTimeout());
							MLog.e(TAG, "getSoLinger=" + _socket.getSoLinger());
							MLog.e(TAG, "ReceiveBufferSize=" + _socket.getReceiveBufferSize());
							MLog.e(TAG, "SendBufferSize=" + _socket.getSendBufferSize());
							try {
								_socket.setReceiveBufferSize(8192);
								_socket.setSendBufferSize(2048);
								_socket.setSoTimeout(_recvTimeout);
								// _socket.setKeepAlive(true);
								_socket.setSoLinger(true, 0);
							} catch (Exception e) {
								// 忽略此类异常
							}
							MLog.e(TAG, "set-SoTimeOut=" + _socket.getSoTimeout());
							MLog.e(TAG, "set-SoLinger=" + _socket.getSoLinger());
							_sender = new TcpSendHandler();
							_sender.start();
							_receiver = new TcpReceiveHandler();
							_receiver.start();
							// 回调
							try {
								callBack.connectSucceed();
							} catch (Exception e) {
								MLog.e(TAG, "connectSucceed exception=" + e);
								e.printStackTrace();
							}
							return;
						} else {
							throw new IOException("socket is not connected");
						}
					} else {
						_address.reset();// 复位地址策略器
						MLog.v(TAG, "ip or port is null");
						throw new Exception("ip or port is null");
					}
				} catch (Exception e) {
					android.util.Log.d(IP_CONFIG_FILE.MY_DETECTGAMEDATA, "connect"+" ("+ipport.getIp()+","+ipport.getPort()+") failed reson:"+e.getMessage());
					if (ipport != null) {
						MLog.v(TAG, "conncect exception ip=" + ipport.getIp() + " port=" + ipport.getPort());
					}

					if (++currentCount < reConnectCount) {
						try {
							Thread.sleep(2000); // 休眠1s
						} catch (InterruptedException e1) {
						}
						continue;
					}

					// 回调
					callBack.connectFailed(e);
					return;
				}
			}
		}

		private void connect(Socket socket, String ip, int port) throws IOException {
			MLog.c1(IP_CONFIG_FILE.MY_DETECTGAMEDATA, "connect"+" ("+ip+","+port+") succeed");
			InetSocketAddress isa = new InetSocketAddress(ip, port);
			socket.connect(isa, _connectTimeout);
			// _socket.setKeepAlive(true);
		}
	}

	/**
	 * 创建连接
	 * 
	 * @param _callBack
	 *            回调对象
	 * @param reConnectCount
	 *            重连次数
	 */
	public void connect(IConnectCallBack _callBack, int reConnectCount) {
		isExited = false;
		Thread thread = new Thread(new Connect(_callBack, reConnectCount));
		thread.setName("connect-thread");
		thread.start();
	}

	/**
	 * 
	 * @param up
	 */
	public void setUnitProcess(UnitProcess up) {
		_unprocess = up;
		if (_unprocess != null) {
			_unprocess.open();
		} 
	}

	public void sendData(byte[] data) {
		if (_sender == null || isExited) {
			return;
		}
		// final int mdms[] = TcpDebugLoger.getDataMdm(data);
		// MLog.v(TAG, "sendData-主-" + mdms[0] + "-子-" + mdms[1]);
		// TcpDebugLoger.getInstance().logTcpReqInfo("sendData加入发送队列", data);
		_sender.send(data);
	}

	private boolean isExited = false;

	public boolean isLive() {
		return !isExited;
	}

	public void shutDown() throws IOException {
		if (isExited) {
			MLog.v(TAG, "shutDown-网络已经关闭");
			return;
		}
		MLog.v(TAG, "shutDown-执行关闭网络");
		isExited = true;
		if(_unprocess!=null) {
			_unprocess.close();
		}
		if (_socket != null) {
			if (!_socket.isOutputShutdown()) {
				try {
					_socket.shutdownOutput();
				} catch (Exception e) {
					// 过滤异常，可能流已经关闭，不能中断后续流程
					// e.printStackTrace();
				}
			}
			if (!_socket.isInputShutdown()) {
				try {
					_socket.shutdownInput();
				} catch (Exception e) {
					// 过滤异常，可能流已经关闭，不能中断后续流程
					// e.printStackTrace();
				}
			}
			if (!_socket.isClosed()) {
				try {
					_socket.close();
				} catch (Exception e) {
					// 过滤异常，可能已经关闭，不能中断后续流程
					// e.printStackTrace();
				}
			}

			if (_receiver != null) {
				_receiver.close();
				_receiver = null;
			}
			if (_sender != null) {
				_sender.close();
				_sender = null;
			}

			_socket = null;
			MLog.e(TAG, "已执行_socket.close()");
		}

	}

	/**
	 * 唯一标识
	 * 
	 * @return
	 */
	public String getTarget() {
		return _target;
	}

	public class TcpReceiveHandler implements Runnable {
		InputStream is;

		boolean isExit = false;

		int hashcode;

		public void run() {
			while (!isExit) {
				if (_unprocess != null) {
					final int result = _unprocess.handler(is);
					if (result == -1) {
						MLog.e(TAG, "TcpReceiveHandler is return -1 hashcode=" + hashcode);
						break;
					}
				} else {
					new Exception("_unprocess is null").printStackTrace();
					break;
				}
			}
			// try {
			// if(isLive()){
			// MLog.e("TcpReceiveHandler", "run shutDown hashcode=" + hashcode);
			// shutDown();
			// }
			// } catch (IOException e) {
			// }
		}

		public void setExit(boolean isExit) {
			this.isExit = isExit;
		}

		TcpReceiveHandler() throws Exception {
			hashcode = hashCode();
			is = _socket.getInputStream();
		}

		void start() {
			Thread thread = new Thread(this);
			thread.setName("tcpReceive-thread");
			thread.start();
		}

		void close() {
			MLog.e("TcpReceiveHandler", " close() hashcode=" + hashcode);
			setExit(true);
			if (is != null) {
				try {
					MLog.e("TcpReceiveHandler", "is.close hashcode=" + hashcode);
					is.close();
				} catch (IOException e) {
				}
				is = null;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			// try {
			// _socket.shutdownInput();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

		}

	}

	public class TcpSendHandler implements Runnable {

		OutputStream os;

		Vector<byte[]> buffer;

		boolean isExit = false;

		int hashcode;

		public void run() {
			while (!isExit) {
				final byte[] data = buffer.size() > 0 ? buffer.get(0) : null;
				if (data != null) {
//					int mdms[] = TcpDebugLoger.getDataMdm(data);
//					MLog.e(TAG, "开始发送-主-" + mdms[0] + "-子-" + mdms[1] + ".hashcode=" + hashcode);
					// TcpDebugLoger.getInstance().logTcpReqInfo("开始发送", data);
					try {
						os.write(data);
						os.flush();
						buffer.remove(0);
						// MLog.v(TAG, "发送完毕-主-" + mdms[0] + "-子-" + mdms[1]);
						// TcpDebugLoger.getInstance().logTcpReqInfo("发送完毕",
						// data);
					} catch (Exception e) {
						MLog.v(TAG, "TcpSendHandler is error=" + e + ".hashcode=" + hashcode);
						e.printStackTrace();
						isExit = true;
						if (_unprocess != null) {
							if (_unprocess.exceptionHandler(e)) {
								break;
							}
						}
					}
				} else {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			// try {
			// if(isLive()){
			// MLog.e("TcpSendHandler", " run shutDown hashcode=" + hashcode);
			// shutDown();
			// }
			// } catch (IOException e) {
			// }
		}

		void start() {
			Thread thread = new Thread(this);
			thread.setName("tcpSend-thread");
			thread.start();
		}

		protected TcpSendHandler() throws IOException {
			os = _socket.getOutputStream();
			buffer = new Vector<byte[]>();
			hashcode = hashCode();
		}

		protected void send(byte[] data) {
			buffer.add(data);
			synchronized (this) {
				notify();
			}
		}

		protected void close() throws IOException {
			MLog.e("TcpSendHandler", " close() hashcode=" + hashcode);
			isExit = true;
			synchronized (this) { // 激活线程
				notify();
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
				}
			}
			// try {
			// if (_socket != null) {
			// _socket.shutdownOutput();
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			os = null;
		}

	}

}
