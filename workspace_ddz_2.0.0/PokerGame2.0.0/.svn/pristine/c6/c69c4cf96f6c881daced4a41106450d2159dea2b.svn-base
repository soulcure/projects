package com.mykj.game.utils;


import java.io.IOException;
import java.util.LinkedList;

import org.cocos2dx.lib.Cocos2dxMusic;
import org.cocos2dx.lib.Cocos2dxSound;

import android.content.Context;

public class PreloadCocos2dRes {

	private Context mContext;
	private static PreloadCocos2dRes instance=null;
	//private  Cocos2dxSound soundPlayer;
	//private  Cocos2dxMusic backgroundMusicPlayer;

	private PreloadCocos2dRes(Context context){
		mContext=context;
	}

	public static PreloadCocos2dRes getInstance(Context context){
		if(instance==null){
			instance=new PreloadCocos2dRes(context);
		}
		return instance;
	}

	LinkedList<String> resDirs = new LinkedList<String>();
	public static boolean isLoadNow = false;
	/**
	 * 预加载声音
	 * @param path
	 */
	public void perloadSoundRes(final Cocos2dxSound soundPlayer, String resDir){
		if(!resDirs.contains(resDir)){
			synchronized (resDirs) {
				resDirs.add(resDir);
			}
		}
		if(isLoadNow){
			return;
		}
		isLoadNow = true;
		new Thread(){
			public void run() {
				int size = resDirs.size();
				while(size > 0){
					String resDir = resDirs.get(0);
					preloadFile(resDir, soundPlayer);
//					try {
//						String[] paths=mContext.getAssets().list(resDir);
//						for(String path:paths){
//							String filePath=resDir+"/"+path;
//							soundPlayer.preloadEffect(filePath);
//						}
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					synchronized (resDirs) {
						resDirs.remove(resDir);
					}
					size = resDirs.size();
				}
				isLoadNow = false;
			};
		}.start();
		
	}


	private void preloadFile(String resDir, Cocos2dxSound soundPlayer){
		try {
			String[] paths=mContext.getAssets().list(resDir);
			for(String path:paths){
				String filePath=resDir+"/"+path;
				if(path.contains(".")){
				
				soundPlayer.preloadEffect(filePath);
				}else{
					preloadFile(filePath, soundPlayer);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	/**
	 * 预加载背景音乐
	 * @param path
	 */
	public void perloadMusicRes(Cocos2dxMusic backgroundMusicPlayer,String path){
				backgroundMusicPlayer.preloadBackgroundMusic(path);
	}

	
	
	
	
}
