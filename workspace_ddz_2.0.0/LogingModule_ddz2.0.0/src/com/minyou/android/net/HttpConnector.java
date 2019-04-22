package com.minyou.android.net;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mykj.comm.log.MLog;

/**
 * 此类为socket连接管理类
 * 
 * @author Jason
 * 
 */
public class HttpConnector implements Runnable {

	private final static String TAG = "HttpConnector";

	private HttpClient httpClient;

	// private HttpGet httpGet = null;

	private HttpUriRequest _httpRequest = null;

	private Vector<IRequest> events;

	// private String httpUrl;

	private Handler handler;

	/** 当前网络收发线程是否运行 **/
	private boolean isStart = false;

	/** http连接管理外部接口对象 **/
	private IRequest curReq = null;

	/** 网络错误 **/
	public final static int NETERR_ = 100;//

	/** 网络连接超时 **/
	public final static int NETERR_CONNECTTIMEOUT = NETERR_ + 1;//

	/** 网络读取超时 **/
	public final static int NETERR_READTIMEOUT = NETERR_ + 2;//

	/** 协议错误 **/
	public final static int NETERR_CLIENTPROTOCOL = NETERR_ + 3;//

	/** socket异常 **/
	public final static int NETERR_SOCKETEXPCEPT = NETERR_ + 4;//

	/** IO错误 **/
	public final static int NETERR_IOEXCEPT = NETERR_ + 5;//

	/** 其他错误 **/
	public final static int NETERR_EXCEPT = NETERR_ + 6;//

	/** 数据响应 **/
	public final static int DATARESPONSE = 300;//

	/** 连接标记 **/
	private String _target = null;

	private final static String HTTP_STRING = "http-";

	/**
	 * 创建一个具有指定处理handler和string标记的HttpConnector
	 * 
	 * @return
	 */
	protected static HttpConnector createHttpConnection(Handler handler) {
		HttpConnector http = new HttpConnector(handler);
		http._target = HTTP_STRING + http.hashCode();
		return http;
	}

	/**
	 * *构造一个具有指定处理handler的HttpConnector,handler可以为空，如果为空将会自动创建Handler对象，
	 * 但是此构造函数不能在非android主线程调用因为可能会创建handler，如果要在非主线程创建就必须由外部传入Handler对象
	 * 
	 * @param handler
	 */
	private HttpConnector(Handler handler) {
		events = new Vector<IRequest>();
		// new Handler() ？ 超危险代码，需要优化
		this.handler = handler == null ? new Handler() : handler;
	}

	/**
	 * 添加http请求处理单元
	 * 
	 * @param _eventBody
	 */
	public synchronized final void addEvent(IRequest _eventBody) {
		events.add(_eventBody);
		synchronized (this) {
			notify();
		}
	}

	/**
	 * 连接http，调用此方法将开始请求http
	 */
	public void connect() {
		isStart = true;
		Thread thread = new Thread(this);
		thread.setName("http-thread");
		thread.start();
	}

	/**
	 * 关闭http请求
	 */
	public void shutdown() {
		isStart = false;
		NetService.getInstance().removeHttpConnector(this.getTarget());
		// synchronized (this) {
		// notify();
		// }
	}

	/**
	 * http请求线程函数
	 */
	public final void run() {
		// 设置连接超时时间和数据读取超时时间
		if (httpClient == null) {
			// 在这里创建，线程复用
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

			httpClient = new DefaultHttpClient(httpParams);
			Log.v(TAG, "new httpClient");
		}
		while (isStart) {
			// 没有事件并且没有得到退出指令的时候就等待
			if(isStart && events.size() == 0) {
				shutdown();
				return;
			}
			if (!isStart) {
				Log.v(TAG, "exit thread!");
				break;
			}
			int result = -1;
			try {
				curReq = events.get(0);
				if (curReq == null) {
					continue;
				}
				String httpUrl = null;
				if (curReq.getHttpState() == IRequest.HTTP_GET) {
					httpUrl = curReq.getPrivateUrl();
					if (httpUrl == null) {
						Log.v(TAG, "curReq=" + curReq + "http url is null");
						continue;
					}
					final String param = curReq.getPrivateParam();
					if (param != null) {
						String reqUrl = httpUrl + "?" + param;
						_httpRequest = new HttpGet(reqUrl);
						Log.v(TAG, "http-url=" + reqUrl);
					} else {
						_httpRequest = new HttpGet(httpUrl);
						Log.v(TAG, "http-url=" + httpUrl);
					}
				} else if (curReq.getHttpState() == IRequest.HTTP_POST) {
					httpUrl = curReq.getPrivateUrl();
					if (httpUrl == null) {
						Log.v(TAG, "curReq=" + curReq + "http url is null");
						continue;
					}
					final String param = curReq.getPrivateParam();
					if (param != null) {
						String reqUrl = httpUrl + "?" + param;
						_httpRequest = new HttpPost(reqUrl);
						Log.v(TAG, "http-url=" + reqUrl);
					} else {
						_httpRequest = new HttpPost(httpUrl);
						Log.v(TAG, "http-url=" + httpUrl);
					}
					final byte[] array = curReq.getData(); // 写入数据
					if (array != null) {
						// 写入附加数据
						HttpEntity httpEntity = new ByteArrayEntity(array);
						((HttpPost) _httpRequest).setEntity(httpEntity);
					}
				}
				// 请求
				HttpResponse httpResponse = httpClient.execute(_httpRequest);
				result = httpResponse.getStatusLine().getStatusCode();
				if (result == HttpStatus.SC_OK) { // 成功
					String charSet = EntityUtils.getContentCharSet(httpResponse.getEntity());
					if (charSet == null) {
						charSet = "UTF-8";
					}

					final String strResult = EntityUtils.toString(httpResponse.getEntity(), charSet);
					// final byte[] byteResult =
					// EntityUtils.toByteArray(httpResponse.getEntity());
					final byte[] byteResult = strResult.getBytes("UTF-8");

					handler.post(new Runnable() {

						public void run() {
							curReq.handler(byteResult);
						}
					});
				} else { // 返回失败
					netErrMsgSend(new Exception("http error=" + result), NETERR_EXCEPT);
				}
			} catch (ConnectTimeoutException e) {
				// e.printStackTrace();
				Log.e(TAG, "run url:" + _httpRequest + ". e=" + e);
				netErrMsgSend(e, NETERR_CONNECTTIMEOUT);
			} catch (SocketTimeoutException e) {
				// e.printStackTrace();
				Log.e(TAG, "run url:" + _httpRequest + ". e=" + e);
				netErrMsgSend(e, NETERR_READTIMEOUT);
			} catch (ClientProtocolException e) {
				// e.printStackTrace();
				Log.e(TAG, "run url:" + _httpRequest + ". e=" + e);
				netErrMsgSend(e, NETERR_CLIENTPROTOCOL);
			} catch (SocketException e) {
				// e.printStackTrace();
				Log.e(TAG, "run url:" + _httpRequest + ". e=" + e);
				netErrMsgSend(e, NETERR_SOCKETEXPCEPT);
			} catch (IOException e) {
				// e.printStackTrace();
				Log.e(TAG, "run url:" + _httpRequest + ". e=" + e);
				netErrMsgSend(e, NETERR_IOEXCEPT);
			} catch (Exception e) {
				// e.printStackTrace();
				Log.e(TAG, "run url:" + _httpRequest + ". e=" + e);
				netErrMsgSend(e, NETERR_EXCEPT);
			} finally {
				Log.i(TAG, "http recv result=" + result);
				events.removeElementAt(0);
			}
		}
		Log.v(TAG, "httpClient shutdown");
		try {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MLog.e("" + e);
		}
	}

	/**
	 * http错误信息转发函数,此处所有错需信息都会post到已经创建的Handler由外部处理
	 * 
	 * @param errMsg
	 * @param errType
	 */
	private void netErrMsgSend(Exception errMsg, int errType) {
		final Message msg = handler.obtainMessage();
		msg.what = errType;
		msg.arg1 = curReq.getTag();
		msg.arg2 = errType;
		msg.obj = errMsg;
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (curReq != null) {
					curReq.doError(msg);
				}
			}
		});
	}

	/**
	 * 获得此连接已经设置的唯一标识
	 * 
	 * @return
	 */
	public String getTarget() {
		return _target;
	}
}