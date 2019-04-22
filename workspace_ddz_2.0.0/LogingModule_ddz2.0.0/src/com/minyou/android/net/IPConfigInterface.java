/**
 * 
 */
package com.minyou.android.net;

import java.util.Map;

/**
 * @author JasonWin8
 */
public interface IPConfigInterface {

	Map getIP_PortArray();

	boolean hasIPConfigInfo();

	void addUpdateCallBackLis(UpdateConfigCallBackListener lis);

	void addConfigCallBackLis(IPConfigCallBackListener lis);


	public interface UpdateConfigCallBackListener {
		void onUpdate();
	}


	public interface IPConfigCallBackListener {
		void onSucceed();

		void onFailed();
	};

}
