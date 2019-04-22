package com.login.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 对TextView设置ColorStateList使其在Normal、Pressed、Focused、Unable四种状态下显示不同的颜色。<br/>
 * StateListDrawable可直接使用图片应用在相似场合。
 */
public class UtilDrawableStateList{

	
	/** 设置Selector。 */
	public static StateListDrawable newSelector(Context context, int idNormal, int idPressed) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		bg.addState(new int[] {}, normal);
		return bg;
	}
	
	
	/** 设置Selector。 */
	public static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused,
			int idUnable) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
		Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
		Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
		// View.ENABLED_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		// View.FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_focused }, focused);
		// View.WINDOW_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;
	}
	
	
	
	
	
	

}