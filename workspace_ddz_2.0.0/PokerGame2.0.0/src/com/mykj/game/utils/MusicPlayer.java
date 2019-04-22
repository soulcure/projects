package com.mykj.game.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.mykj.game.FiexedViewHelper;

public class MusicPlayer {

	private static MusicPlayer instance;

	private Context mContext;

	private MediaPlayer bgPlayer;
	
	private boolean isInGame = false;

	private MusicPlayer(Context c) {
		this.mContext = c;
	}

	public static MusicPlayer getInstance(Context c) {
		if (instance == null)
			instance = new MusicPlayer(c);
		return instance;
	}

	public void playBgSound() {
		if (!FiexedViewHelper.getInstance().isTurnOnBackMusic()) {
			return;
		}
		if(isInGame)
			return;
		
		if(bgPlayer != null && 
				bgPlayer.isPlaying())
			return;
		
		//stopBgSound();

		try {
			if (bgPlayer == null) {
				AssetManager assetManager = mContext.getAssets();
				bgPlayer  = new MediaPlayer();
				try {
					
					AssetFileDescriptor fileDescriptor = assetManager.openFd("sound/back_music.mp3");
					bgPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
					                              fileDescriptor.getStartOffset(),
					                              fileDescriptor.getLength());
					bgPlayer.prepare();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				    
				
			}
			bgPlayer.setVolume(0.5f, 0.5f);
			bgPlayer.setLooping(true);
			bgPlayer.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 停止背景音乐的播放
	 */
	public void stopBgSound() {
		if (bgPlayer == null)
			return;
		if (bgPlayer.isPlaying())
			bgPlayer.stop();
		bgPlayer.release();
		bgPlayer = null;
	}

	public boolean isInGame() {
		return isInGame;
	}

	public void setInGame(boolean isInGame) {
		this.isInGame = isInGame;
	}

	
}
