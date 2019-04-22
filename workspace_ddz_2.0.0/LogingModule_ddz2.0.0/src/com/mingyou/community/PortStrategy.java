/**
 * 
 */
package com.mingyou.community;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * @author JasonWin8
 * 
 */
public class PortStrategy {

	private Map<String, PortGroup> mPortGroupMapedbyIp = null;

	PortStrategy() {
		mPortGroupMapedbyIp = new HashMap<String, PortGroup>();
	}

	/** package */
	int getPort(String ip) {
		if(mPortGroupMapedbyIp==null){
			return 0;
		}
		PortGroup portGroup = mPortGroupMapedbyIp.get(ip);
		if (portGroup == null) {
			return -1;
		}
		int curIndex = portGroup.getIndex();
		int port = -1;
		if (portGroup != null && portGroup.length() > 0) {
			final int length = portGroup.length();
			if (curIndex == -1) {
				curIndex = new Random(System.currentTimeMillis()).nextInt(length);
			}
			port = portGroup.getPort(curIndex);
			if (++curIndex >= length) {
				curIndex = 0;
			}
			portGroup.setIndex(curIndex);
		}
		return port;
	}

	/**
	 * 添加ip与其对应的端口组
	 * 
	 * @param ip
	 * @param pg
	 */
	void addItem(String ip, PortGroup pg) {
		if(mPortGroupMapedbyIp==null){
			return ;
		}
		mPortGroupMapedbyIp.put(ip, pg);
	}

	/**
	 * 
	 */
	void reset() {
		if(mPortGroupMapedbyIp==null){
			return ;
		}
		Iterator<PortGroup> iterator = mPortGroupMapedbyIp.values().iterator();
		while (iterator.hasNext()) {
			PortGroup port = iterator.next();
			if (port != null) {
				port.reset();
			}
		}
	}

	/**
	 * @param _ippostList
	 */
	void setIpPortMap(Map<String, PortGroup> _ippostList) {
		mPortGroupMapedbyIp=_ippostList;
	}

	public void clean() {
		if(mPortGroupMapedbyIp!=null){
			mPortGroupMapedbyIp.clear();
		}
	}
}
