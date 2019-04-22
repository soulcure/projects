/**
 * 
 */
package com.mingyou.community;

import java.util.Random;
import java.util.Vector;

/**
 * @author JasonWin8
 *
 */
public class IpStrategy {

	private Vector<String> mIpArray=null;
	
	private int mCurIndex = -1;
	
	IpStrategy(){
		mIpArray=new Vector<String>();
	}
	
	/*package*/ String getIp() {
		String ip = null;
		if (mIpArray != null && mIpArray.size() > 0) {
			final int length =mIpArray.size();
			if (mCurIndex == -1) {
				mCurIndex = new Random(System.currentTimeMillis()).nextInt(length);
			}
			ip = mIpArray.get(mCurIndex);
			if (++mCurIndex >= length) {
				mCurIndex = 0;
			}
		}
		return ip;
	}
	
	void addIp(String ip) {
		mIpArray.add(ip);
	}
	
	void reset() {
		mCurIndex=-1;
	}

	public void clean() {
		if(mIpArray!=null){
			mIpArray.clear();
		}
		reset();
	}
}
