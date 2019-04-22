package com.mingyou.login.struc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mykj.comm.util.SecretCode;


public class NotifiyDownLoad extends AsyncTask<String, Integer, String> {
	public static final String PARENT_PATH = "/.mingyou";

	public static final String APKS_PATH = PARENT_PATH + "/apks";

	private static final String TAG = "NotifiyDownLoad";

	private Context mContext;
	private DownLoadListener mProgressListener;

	private String mStrRate;

	private boolean isCancel=false;

	public NotifiyDownLoad(Context context,DownLoadListener listener) {
		super();
		mContext = context;
		mProgressListener=listener;

	}

	/**
	 * 从url解析出fileName
	 */
	public static String getFileNameFromUrl(String strUrl) {
		String fileName = null;

		if (strUrl != null) {
			String[] tmpStrArray = strUrl.split("/");
			fileName = tmpStrArray[tmpStrArray.length - 1];
			if (fileName.trim().length() == 0) {
				fileName = null;
			}
		}
		fileName = fileName.replaceAll(".rar", ".apk");
		return fileName;
	}

	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	@Override
	protected String doInBackground(String... params) {
		Log.v(TAG, "doInBackground...");
		String url = params[0];
		String downloadpath = params[1];
		String md5 = params[2];
		Log.v(TAG, "url=" + url);
		Log.v(TAG, "downloadpath" + downloadpath);

		File path = new File(downloadpath);
		if (!path.exists()) {
			path.mkdirs();
		}

		String filename = getFileNameFromUrl(url);
		File downloadFile = new File(downloadpath, filename);
		if (downloadFile.exists()) {
			// 已经存在 不用下载
			Log.v(TAG, "file is exist,don't download");
			return downloadFile.getPath();
		}

		String downLoadFileTmpName = filename + ".tmp"; // 设置下载的临时文件名

		File tmpFile = new File(downloadpath, downLoadFileTmpName);

		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(httpParams, 30000);

			HttpGet httpGet = new HttpGet(url);

			long startPosition = tmpFile.length(); // 已下载的文件长度
			String start = "bytes=" + startPosition + "-";
			httpGet.addHeader("Range", start);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
					|| httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT) {
				HttpEntity entity = httpResponse.getEntity();
				long length = entity.getContentLength();// 请求文件长度

				InputStream inputStream = entity.getContent();
				byte[] b = new byte[1024];
				int readedLength = -1;

				OutputStream outputStream = new FileOutputStream(tmpFile, true);

				long fileSize=length + startPosition;
				long percentile = fileSize/20; // 文件每下载5%的长度
				int rate = 0;
				int count = 0;
				long downloadfile=startPosition;

				String strDownLoad;
				String strFileSize=bytes2mb(fileSize);
				int k=1;
				while (((readedLength = inputStream.read(b)) != -1) && !isCancel && getAPNType(mContext) == 1) {
					//下载刚开始就显示一次进度
					while(k>0){
						k--;
						strDownLoad= bytes2mb(downloadfile);
						mStrRate=strDownLoad+"/"+strFileSize;
						int startRate=(int) ((downloadfile/fileSize)*100);
						publishProgress(startRate);
					}


					outputStream.write(b, 0, readedLength);
					downloadfile +=readedLength;
					startPosition += readedLength;

					if (startPosition >= percentile) // 每下载5%，计算进度条
					{
						count = (int) (startPosition / percentile) * 5;
						Log.v(TAG, "count=" + count);
						rate += count;

						startPosition = 0;
						count = 0;

						// 调用了这个方法之后会触发onProgressUpdate(Integer... values)
						strDownLoad= bytes2mb(downloadfile);
						mStrRate=strDownLoad+"/"+strFileSize;

						publishProgress(rate);
						Log.v(TAG, "文件已下载" + rate + "%");

					}
				}
				strDownLoad= bytes2mb(downloadfile);
				mStrRate=strDownLoad+"/"+strFileSize;
				inputStream.close();
				outputStream.close();

				// 下载文件MD5检测
				if (downloadFileMD5Check(tmpFile, md5) && !isCancel) {
					Log.v(TAG, "download file md5 check success");
					tmpFile.renameTo(downloadFile);
				}else if(isCancel){
					Log.v(TAG, "download file is cancel");
					mProgressListener.downloadFail("download file is cancel");
					return null;
				}else {
					Log.e(TAG, "download file md5 check fail");
					tmpFile.delete();
					mProgressListener.downloadFail("download file md5 check fail");
					return null;

				}

			}
		} catch (ClientProtocolException e) {
			mProgressListener.downloadFail(e.toString());
			return null;
		} catch (IOException e) {
			mProgressListener.downloadFail(e.toString());
			return null;
		}
		return downloadFile.getPath();
	}

	/**
	 * 对下载的文件进行md5校验
	 */
	public static synchronized boolean downloadFileMD5Check(File f, String expectedMD5) {
		boolean flag = false;
        String fileMD5=SecretCode.getMD5(f);
        if(fileMD5.equals(expectedMD5)){
            flag = true;
        }
		return flag;
	}

	@Override
	protected void onPostExecute(String strPath) {
		super.onPostExecute(strPath);
		if (strPath != null) {
			mProgressListener.onProgress(100,mStrRate);
			installApk(mContext, strPath);
		}
	}

	/**
	 * 安装APK
	 */
	public static void installApk(Context context, String apkFilePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		int rate = progress[0].intValue();

		mProgressListener.onProgress(rate,mStrRate);

	}


	/**
	 * bytes to kb
	 * @param bytes
	 * @return
	 */
	public   static  String bytes2kb( long  bytes)  {
		BigDecimal filesize  =   new  BigDecimal(bytes);
		BigDecimal kilobyte  =   new  BigDecimal( 1024 );
		float returnValue  =  filesize.divide(kilobyte,  2 , BigDecimal.ROUND_UP).floatValue();
		return (returnValue  +   "  KB " );
	}

	/**
	 * bytes to mb
	 * @param bytes
	 * @return
	 */
	public   static  String bytes2mb( long  bytes)  {
		BigDecimal filesize  =   new  BigDecimal(bytes);
		BigDecimal megabyte  =   new  BigDecimal( 1024 * 1024 );
		float  returnValue  =  filesize.divide(megabyte,  2 , BigDecimal.ROUND_UP).floatValue();
		return (returnValue  +   "  MB " );
	}



	/**
	 * 取消下载
	 * @param isCancel
	 */
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	public void setProgressListener(DownLoadListener lis){
		if(lis!=null){
			mProgressListener = lis;
		}
	}
	
	/**
	 * @param context
	 * @return 返回网络类型 1 mean wifi 2 mean CMWAP 3 mean CMNET
	 */
	@SuppressLint("DefaultLocale")
	private int getAPNType(Context context) {
		int netType = -1;
		int WIFI = 1;
		int CMWAP = 2;
		int CMNET = 3;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String eInfo = networkInfo.getExtraInfo();
			if (eInfo != null) {
				if (eInfo.toLowerCase().equals("cmnet")) {
					netType = CMNET;
				} else {
					netType = CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		}
		return netType;
	}

}
