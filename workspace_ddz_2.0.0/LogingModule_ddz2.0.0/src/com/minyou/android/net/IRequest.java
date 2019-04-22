package com.minyou.android.net;

import android.os.Message;

/**
 * Htpp连接地址，参数，返回数据管理接口
 * 
 * @author Jason
 * 
 */
public abstract class IRequest {

	/** http get方式常量 **/
	public final static byte HTTP_GET = 0;

	/** http post方式常量 **/
	public final static byte HTTP_POST = 1;

	/**
	 * 要发送的数据
	 * 
	 * @return
	 */
	public byte[] getData() {
		return null;
	};

	/**
	 * URL附带的参数
	 * 
	 * @return
	 */
	public String getParam() {
		return null;
	};

	/**
	 * htpp连接的主url注：url的host和参数的分割符“？”底层会自定添加
	 * 
	 * @return
	 */
	public abstract String getHttpUrl();
	
	protected final String getPrivateParam() {
		String str=getParam();
		if(str!=null) {
			str=str.replace(" ","");
		}
		return str;
	}
	protected final String getPrivateUrl() {
		String str=getHttpUrl();
		if(str!=null) {
			str=str.replace(" ","");
		}
		return str;
	}

	/**
	 * http连接的自定义标记
	 * 
	 * @return
	 */
	public int getTag() {
		return 0;
	};

	/**
	 * 传回的数据处理
	 * 
	 * @param buf
	 */
	public abstract void handler(final byte[] buf);

	/**
	 * 所有错误处理，包括网络错误，解析错误
	 * 
	 * @param msg
	 */
	public void doError(Message msg) {
	};

	public int getHttpState() {
		return HTTP_GET;
	}
}
