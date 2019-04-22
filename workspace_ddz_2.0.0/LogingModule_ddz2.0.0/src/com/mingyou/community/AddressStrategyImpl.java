package com.mingyou.community;

import java.util.Iterator;
import java.util.Map;

import com.minyou.android.net.AddressStrategy;
import com.minyou.android.net.IPConfigInterface;
import com.minyou.android.net.IpPortObj;

import debug.IP_CONFIG_FILE;

public class AddressStrategyImpl implements AddressStrategy, IPConfigInterface.UpdateConfigCallBackListener {

	// private static final String TAG = "AddressStrategyImpl";

	// private Map<String,PortGroup> ippostList;

	boolean isUpdate = false;

	private IPConfigInterface _IPconfig = null;

	private IpStrategy mIpStrategy = null;

	private PortStrategy mPortStrategy = null;

	public AddressStrategyImpl(IPConfigInterface config) {
		_IPconfig = config;
		mIpStrategy = new IpStrategy();
		mPortStrategy = new PortStrategy();
		if (_IPconfig.hasIPConfigInfo()) {
			setPoolData(_IPconfig.getIP_PortArray());
		}
		// 注册Update更新回调
		_IPconfig.addUpdateCallBackLis(this);
	}

	/**
	 * 
	 * @param ippostList
	 */
	public void setPoolData(Map<String, PortGroup> ippostList) {
		clean();
		Iterator<String> iterator = ippostList.keySet().iterator();
		if (iterator != null) {
			while (iterator.hasNext()) {
				String ip = iterator.next();
				if (ip != null) {
					mIpStrategy.addIp(ip);
				}
			}
		}
		mPortStrategy.setIpPortMap(ippostList);
	}



	/**
	 * 添加ip
	 * 
	 * @param ip
	 */
	public void addIp(String ip) {
		mIpStrategy.addIp(ip);
	}

	/**
	 * 添加端口组，并且制定其对应的IP
	 * 
	 * @param ip
	 * @param group
	 */
	public void addPortGroup(String ip, PortGroup group) {
		mPortStrategy.addItem(ip, group);
	}

	public IpPortObj getIpPort() {
		if (!IP_CONFIG_FILE.isOuterNet()) {
			// 从配置读取Ip
			return new IpPortObj(IP_CONFIG_FILE.getConnectIP(), IP_CONFIG_FILE.getConnectPort());
		}

		if (isUpdate) {
			isUpdate = false;
			setPoolData(_IPconfig.getIP_PortArray());
			reset();
		}
		final String ip = mIpStrategy.getIp();
		IpPortObj obj = new IpPortObj(ip, mPortStrategy.getPort(ip));
		return obj;
	}

	public void reset() {
		mPortStrategy.reset();
		mIpStrategy.reset();
	}
	
	/**
	 * 清理ip和port
	 */
	private void clean() {
		mPortStrategy.clean();
		mIpStrategy.clean();
	}

	@Override
	public void onUpdate() {
		isUpdate = true;
	}
}
