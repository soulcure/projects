package com.mingyou.login.struc;

/**
 * 客户端升级下载监听器
 * @author Jason
 *
 */
public interface DownLoadListener {
	
	void onProgress(int rate,String strRate);
	
	void downloadFail(String err);

}