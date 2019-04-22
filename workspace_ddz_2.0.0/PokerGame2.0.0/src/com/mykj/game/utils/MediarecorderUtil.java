package com.mykj.game.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class MediarecorderUtil {
	private MediaRecorder mRecorder;

	private MediaPlayer mPlayer;

	private String filePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/";

	private String fileName = "audiorecord.3gp";
	
	private static MediarecorderUtil singleton = null;

	private MediarecorderUtil() {

	}

	public synchronized static MediarecorderUtil getSingleton() {
		if (singleton == null) {
			singleton = new MediarecorderUtil();
		}
		return singleton;
	}

	/**
	 * 开始录音
	 */
	public void start() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(filePath + fileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IOException e) {
			Log.e(MediarecorderUtil.class.toString(), "prepare() failed：\n" + e.toString());
		}
		
	}

	/**
	 * 停止录音
	 */
	public byte[] stop() {
		try{
			mRecorder.stop();
			mRecorder.release();
		}catch(Exception e){
			Log.e(MediarecorderUtil.class.toString(), "stop() failed：\n" + e.toString());
		}finally{
			mRecorder = null;
		}
		byte[] data = getBytes(filePath + fileName);
		Util.deleteFile(new File(filePath + fileName));
		return data;
	}

	/**
	 * 开始播放
	 */
	public void startPlay(byte[] data) {
		try {
			if (mPlayer != null && mPlayer.isPlaying()) {
				mPlayer.release();
				mPlayer = null;
			}
			getFile(data);
			mPlayer = new MediaPlayer();
			mPlayer.setDataSource(filePath + fileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(MediarecorderUtil.class.toString(), "播放失败");
		}
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * 获得指定文件的byte数组
	 */
	private byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	private void getFile(byte[] bfile) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
