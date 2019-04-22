package com.mykj.andr.ui.widget;

import android.os.Handler;
import android.view.View;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow{
	public MyPopupWindow(View v, int w, int h) {
		super(v, w, h);
	}
	private int HANDLER_DISMISS = 1;
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// TODO Auto-generated method stub
		super.showAtLocation(parent, gravity, x, y);
		hand.sendEmptyMessageDelayed(HANDLER_DISMISS, 3000);
	}
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		// TODO Auto-generated method stub
		super.showAsDropDown(anchor, xoff, yoff);
		hand.sendEmptyMessageDelayed(HANDLER_DISMISS, 3000);
	}
	@Override
	public void showAsDropDown(View anchor) {
		// TODO Auto-generated method stub
		super.showAsDropDown(anchor);
		hand.sendEmptyMessageDelayed(HANDLER_DISMISS, 3000);
	}
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if(hand.hasMessages(HANDLER_DISMISS)){
			hand.removeMessages(HANDLER_DISMISS);
		}
		try{
			super.dismiss();
		}catch(Exception e){}
	}
	private Handler hand = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			if(msg.what == HANDLER_DISMISS){
				dismiss();
			}
		};
	};
}