package com.mykj.game.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PostAsyncTask extends AsyncTask<String, Integer, Boolean> {

	private static final String TAG="PostAsyncTask";
	private static String FORM_TABLE_NAME = "avatar";// 自己需要配置的表单

	private static String FORM_REPORT_NAME = "screen";// 自己需要配置的表单
	private static String FORM_STRING_NAME = "report";// 自己需要配置的表单
	private static String FORM_REPORT_CON = "content";// 举报内容表单项
	
	private static String FORM_VERSION_NAME = "version";// 自己需要配置的表单


	private File imageFile;//上传的文件
	
	private Bitmap bitmap;//上传的Bitmap
	private Context mContext;

	/**
	 * 构造函数
	 * @param file
	 */
	public PostAsyncTask(Context context,File file){
		mContext=context;
		imageFile=file;
	}

	
	public PostAsyncTask(Context context,Bitmap bm){
		mContext=context;
		bitmap=bm;
	}
	

	@Override
	protected Boolean doInBackground(String... params) {
		HttpClient httpclient = null;
		httpclient = new DefaultHttpClient();

		final HttpPost httppost = new HttpPost(params[0]);
		String upLoadInfo;
		String report;
		
		try{
			upLoadInfo=params[1];
			report=params[2];
		}catch(ArrayIndexOutOfBoundsException e){
			upLoadInfo=null;
			report=null;
		}

		final MultipartEntity multipartEntity = new MultipartEntity();
		
		if(imageFile==null){
			if(bitmap!=null){
				imageFile=Util.getOutputMediaFile();
				saveMyBitmap(imageFile,bitmap);
			}else{
				return false;
			}
		}
		

		if(!Util.isEmptyStr(upLoadInfo)
				&& !Util.isEmptyStr(report)){
			try {
				ContentBody contentBody = new FileBody(imageFile);
				FormBodyPart formBodyPart = new FormBodyPart(FORM_REPORT_NAME,
						contentBody);
				multipartEntity.addPart(formBodyPart);  //添加图片

				StringBody cbMessage = new StringBody(upLoadInfo,Charset.forName("UTF-8"));
				multipartEntity.addPart(FORM_STRING_NAME,cbMessage); //添加文字


				StringBody cbReport = new StringBody(report,Charset.forName("UTF-8"));
				multipartEntity.addPart(FORM_REPORT_CON,cbReport); //添加文字

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			try {
				ContentBody contentBody = new FileBody(imageFile);
				FormBodyPart formBodyPart = new FormBodyPart(FORM_TABLE_NAME,
						contentBody);
				multipartEntity.addPart(formBodyPart);//添加图片

				StringBody version = new StringBody(Util.getVersionName(mContext),Charset.forName("UTF-8"));
				multipartEntity.addPart(FORM_VERSION_NAME,version); //添加文字,版本信息
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}



		httppost.setEntity(multipartEntity);

		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httppost);

			final int statusCode = httpResponse.getStatusLine()
					.getStatusCode();

			String response = EntityUtils.toString(
					httpResponse.getEntity(), HTTP.UTF_8);

			//JSONObject jsonObjArr = new JSONObject(); 

			JSONObject obj;
			try {
				obj = new JSONObject(response);
				int code = Integer.parseInt(obj.getString("code"));
				String msg = obj.getString("message");

				if (statusCode == HttpStatus.SC_OK
						&&code==0) {
					return true;
				}

				Log.v(TAG,"response=" + msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
				httpclient = null;
			}
		}
		return false;

	}





	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
//			Toast.makeText(mContext, "post文件成功", Toast.LENGTH_SHORT).show();
		}else{
//			Toast.makeText(mContext, "post文件失败", Toast.LENGTH_SHORT).show();
		}
	}


	
	private File saveMyBitmap(File file,Bitmap bitmap) {

		FileOutputStream fOut = null;

		try {
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			Log.e("test", "saveMyBitmap is null");
			file=null;
		}
		return file;
	}


}
