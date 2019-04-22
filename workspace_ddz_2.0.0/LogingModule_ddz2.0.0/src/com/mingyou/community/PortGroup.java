package com.mingyou.community;
/**
 * 
 */


/**
 * @author JasonWin8
 * 
 * 
 */
public class PortGroup {
	private int mPortArray[] = null;

	private int mIndex = -1;

	int length() {
		return mPortArray==null?0:mPortArray.length;
	}

	public void init(int arr[]) {
		mPortArray = arr;
	}

	int getPort(int index) {
		return mPortArray[index];
	}
	
	int getIndex() {
		return mIndex;
	}
	
	void setIndex(int index) {
		mIndex = index;
	}
	
	void reset() {
		mIndex=-1;
	}
}
