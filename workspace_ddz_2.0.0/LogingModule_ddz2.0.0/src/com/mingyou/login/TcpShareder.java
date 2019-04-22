/**
 * 
 */
package com.mingyou.login;

import java.io.IOException;
import java.io.InputStream;

import android.os.Message;

import com.mingyou.distributor.NetDefaultListener;
import com.mingyou.distributor.NetDistributor;
import com.mingyou.distributor.NetErrorListener;
import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.minyou.android.net.AddressStrategy;
import com.minyou.android.net.NetService;
import com.minyou.android.net.TcpConnector;
import com.minyou.android.net.UnitProcess;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.comm.log.MLog;

import debug.IP_CONFIG_FILE;
import debug.TcpDebugLoger;

/**
 * 网络数据封包，网络分发器创建和管理
 * 
 * @author Jason
 * 
 */
public class TcpShareder {

	private final String TAG = "TcpShareder";

	private static TcpShareder _instance = null;

	public static TcpShareder getInstance() {
		if (_instance == null) {
			_instance = new TcpShareder();
		}
		return _instance;
	}

	/** true:使用NetSocketPak方式封包网络数据，false：原始字节数组的方式分发数据 **/
	private boolean isUseNetData = false;

	/** 网络数据风发器 **/
	private NetDistributor distributor = null;

	/** socket连接 **/
	private TcpConnector _tcpConnector = null;

	protected TcpShareder() {
		distributor = new NetDistributor();
	}

	/**
	 * 添加私有监听器对象到分发器
	 * 
	 * @param listener
	 */
	public void addTcpListener(NetPrivateListener listener) {
		distributor.addPrivateListener(listener);
	}

	/**
	 * 移除所有分发器内已注册的监听器
	 */
	public void clearListener() {
		distributor.clearListener();
	}

	/**
	 * 设置分发器的缺省监听器，此监听器每个分发器只有一个，重复设置会覆盖上一个
	 * 
	 * @param listener
	 */
	public void setTcpDefaultListener(NetDefaultListener listener) {
		distributor.setDefaultNetListener(listener);
	}

	/**
	 * 设置分发器的网络错误监听器，此监听器每个分发器只有一个，重复设置会覆盖上一个
	 * 
	 * @param listener
	 */
	public void setNetErrorListener(NetErrorListener listener) {
		distributor.setErrorListener(listener);
	}

	/** 最后一次收到数据时的时间点 **/
	protected static long socketRevDataTime = 0;

	/** socket数据监听器 **/
	private UnitProcess _unitProcess = null;

	/**
	 * 创建一个socket连接并不会自动启动
	 * 
	 * @param address
	 */
	protected void createTcpConnector(AddressStrategy address) {
		if (_unitProcess == null) { // 保证网络数据监听器不会重复创建
			_unitProcess = new UnitProcess() {

				Message message = new Message();

				NetDistributor _distributor = distributor;

				byte[] datahead = new byte[NetSocketPak.HEADSIZE];

				private boolean _isClosed = false;

				public boolean exceptionHandler(Exception e) {
					if (_isClosed) {
						return true;
					}
					// 所有socket底层错误都触发此函数
					Message message = new Message();
					message.obj = e;
					// distributor.sendMessage(message);
					handleMessage(message);
					return true;
				}

				public int handler(InputStream in) {
					if (_isClosed) {
						return -1;
					}
					resetDataHead(); // 重置datahead数组
					try {
						if (isUseNetData || !IP_CONFIG_FILE.isSendProxyInfo()) {
							// 读取数据大小
							final int realdatalen = TDataInputStream.readDataByLen(in, datahead, NetSocketPak.HEADSIZE);
							if (realdatalen < NetSocketPak.HEADSIZE) {
								// Thread.sleep(300);
								MLog.e(TAG, "网络被断开 网络读取流返回-1");
								Message message = new Message();
								message.obj = new Exception("网络被断开");
								// distributor.sendMessage(message);
								handleMessage(message);
								return -1;
							}
							// 总数据大小
							final int datasize = (((datahead[1] & 0xff) << 8) | (datahead[0] & 0xff));
							if (datasize < NetSocketPak.HEADSIZE) {
								MLog.e(TAG, "rev数据包异常小于包头 datasize=" + datasize);
								return 0;
							}
							// 创建数据流
							TDataInputStream dataInputStream = new TDataInputStream(in, datasize - NetSocketPak.HEADSIZE);
							// 默认对齐方式：低位在前
							dataInputStream.setFront(false);
							NetSocketPak recCommand = new NetSocketPak(datahead, dataInputStream);
							if (recCommand.getMdm_gr() == 11 && recCommand.getSub_gr() == 1) {
								// 过滤心跳数据
								return 0;
							}
							socketRevDataTime = System.currentTimeMillis();
							MLog.e(TAG, "rev数据包=" + recCommand);
							// 网络数据包发送
							message.obj = recCommand;
							// distributor.sendMessage(message);
							handleMessage(message);

							// }
						} else {
							// 接收穿透串： CONNECT RESULT=0\r\n\r\n
							final int len = 20; // 穿透信息的固定长度
							byte[] data = new byte[len];
							int readlen = 0;
							while (readlen < len) {
								// 遍历读取穿透串，读取满20才返回 2011-12-16
								byte n = (byte) in.read();
								if (n != -1) {
									data[readlen] = n;
									readlen++;
								}
							}
							// 解析数据
							MLog.v(TAG, "收到穿透信息:" + new String(data));
							socketRevDataTime = System.currentTimeMillis();
							message.obj = data;
							// distributor.sendMessage(message);
							handleMessage(message);
							isUseNetData = true; // 每次连接都穿透一次
						}
					} catch (Exception ex2) {
						// if (ex2 instanceof SocketException || ex2 instanceof
						// SocketTimeoutException) {
						MLog.v(TAG, "接收网络数据错误=" + ex2);
						ex2.printStackTrace();
						message.obj = ex2;
						handleMessage(message);
						return -1; // 返回-1底层网络将会自动关闭
					}
					return 0;
				}

				void handleMessage(Message msg) {
					if (_distributor != null) {
						_distributor.handleMessage(msg);
					}
				}

				/**
				 * 
				 */
				private void resetDataHead() {
					for (int i = 0; i < datahead.length; i++) { // 充值数组
						datahead[i] = 0;
					}
				}

				public void open() {
					if (_distributor != null) {
						_distributor.open();
					}
				}

				public void close() {
					_distributor.close();
					_distributor = null;
					_isClosed = true;
				}
			};
		}
		if (_tcpConnector == null) {
			_tcpConnector = NetService.getInstance().createTcpConnection(_unitProcess, address);
		}
		_tcpConnector.setAddressStrategy(address);
	}

	/**
	 * 网络数据发送
	 * 
	 * @param data
	 */
	public void reqNetData(NetSocketPak data) {
		if (_tcpConnector == null || data == null) {
			return;
		}
		MLog.v(TAG, "reqNetData" + data);
		final byte[] buf = data.getBufferByte();
		TcpDebugLoger.getInstance().logTcpReqInfo("reqNetData发送", buf);
		if (buf != null && buf.length > 0) {
			reqNetData(buf);
		} else {
			new Exception("NetSocketPak bytes is null").printStackTrace();
		}
	}

	/**
	 * 发送穿透
	 * 
	 * @param url
	 */
	protected void reqGetProxyInfo(String url) {
		if (_tcpConnector == null || url == null) {
			MLog.e(TAG, "reqNetData-_tcpConnector is null=" + url);
			return;
		}
		_tcpConnector.sendData(TDataOutputStream.utf8toBytes(url));
	}

	/**
	 * 发送网络数据,已字节数组形式
	 * 
	 * @param buf
	 */
	public void reqNetData(final byte[] buf) {
		if (_tcpConnector == null || buf == null) {
			MLog.e(TAG, "reqNetData-_tcpConnector is null=" + buf);
			return;
		}
		_tcpConnector.sendData(buf);
	}

	/**
	 * 设置下一次网络数据的封包方式
	 * 
	 * @param b
	 */
	protected void setUseNetData(boolean b) {
		MLog.e("TcpShareder", "setUseNetData b=" + b);
		isUseNetData = b;
	}

	/**
	 * 连接已经创建的socket
	 */
	protected void connect(TcpConnector.IConnectCallBack _callBack, int reConnectCount) {
		if (_tcpConnector != null) {
			_tcpConnector.connect(_callBack, reConnectCount);
		}
	}

	/**
	 * 链路层关闭网络
	 */
	protected void closeSocket() {
		MLog.e("TcpShareder", "closeSocket");
		if (_unitProcess != null) {
			_unitProcess = null; // 解绑与底层的关联关系
		}
		if (_tcpConnector != null) {
			NetService.getInstance().removeTcpConnector(_tcpConnector.getTarget()); // 移除已关闭连接
			try {
				if (_tcpConnector.isLive()) {
					_tcpConnector.shutDown();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			_tcpConnector = null;
		}
	}

	/**
	 * 清理分发器内的所有监听器，关闭socket连接,释放此单例对象
	 */
	protected void closeTcp() {
		MLog.e("TcpShareder", "closeTcp");
		clearListener(); // 清理监听器
		closeSocket();
	}
}
