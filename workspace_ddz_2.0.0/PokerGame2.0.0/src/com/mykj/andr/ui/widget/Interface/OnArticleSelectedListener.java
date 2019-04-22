package com.mykj.andr.ui.widget.Interface;

import com.yunva.live.sdk.YunvaLive;

import android.os.Handler;

public interface OnArticleSelectedListener {
	 public void onArticleSelected(Handler handler);
     public boolean isBindMMVideo();
     public void setEntryRoomNoBind(boolean b);
     public YunvaLive getYunvaLive();
}
