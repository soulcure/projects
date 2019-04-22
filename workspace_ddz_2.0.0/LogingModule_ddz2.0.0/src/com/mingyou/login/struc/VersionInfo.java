/**
 * 
 */
package com.mingyou.login.struc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.community.Community;
import com.mingyou.login.LoginUtil;

/**
 * 客户端版本信息对象，管理客户端是否需要升级，下载地址等
 * 
 * @author Jason
 * 
 */
public class VersionInfo {
	/** 不升级 **/
	public static final byte UPGRADE_NONE = 0;

	/** 需要升级 **/
	public static final byte UPGRADE_NEED = 1;

	/** 必须升级 **/
	public static final byte UPGRADE_MUST = 3;

	/** 升级状态 **/
	public byte _upgrade = UPGRADE_NONE;

	/** 升级描述 **/
	public String _upDesc = null;

	/** 升级下载地址 **/
	public String _upUrl = null;

	/** md5校验 **/
	public String _upMd5 = null;

	public String _apkSize = null;
	
	public String _version;

	private NotifiyDownLoad mDownLoad;
	
	private boolean isDownLoading;
	
	public List<GifModel> gifModelList = new ArrayList<GifModel>();
	
	public class GifModel{
		public String picUrl; // 奖励图片地址
		public String gifName; // 奖励名称
	}
	
	/**
	 * 升级跳转,将会自动下载需要升级的客户端 
	 */
	public void gotoUpgrade(DownLoadListener listener) {
		
		if (_upgrade < UPGRADE_NEED || _upUrl == null || _upMd5 == null) {
			return;
		}

		if (listener != null) {
			String downpath = NotifiyDownLoad.getSdcardPath() + NotifiyDownLoad.APKS_PATH;
			if(mDownLoad==null){
				mDownLoad = new NotifiyDownLoad(Community.getContext(), listener);
				mDownLoad.execute(_upUrl, downpath, _upMd5);
			}else{
				mDownLoad.setProgressListener(listener);
			}
		}

	}
	
	/**
	 * 取消下载
	 * @param isCancel
	 */
	public void setDownLoadCancle(boolean isCancel){
		if(mDownLoad != null){
			mDownLoad.setCancel(isCancel);
		}
	}
	
	public boolean isDownLoading() {
		return isDownLoading;
	}

	public void setDownLoading(boolean isDownLoading) {
		this.isDownLoading = isDownLoading;
	}

	/**
	 * 此方法由版本信息返回是调用处理，外部不应该调用
	 * @param buf
	 * @param bool
	 */
	public void parseVersionInfo(final byte[] buf, final boolean bool) {
		InputStream is = new ByteArrayInputStream(buf);
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			NodeList el1 = doc.getElementsByTagName("lobby");
			NodeList el2 = doc.getElementsByTagName("game");
			NodeList list = null;
			if (el1 != null && el1.getLength() > 0) { // 大厅
				list = el1;
			} else if (el2 != null && el2.getLength() > 0) { // 游戏
				list = el2;
			} else {
				return;
			}
			NamedNodeMap map = list.item(0).getAttributes();
			_upUrl = LoginUtil.getNodeValue(map, "updateUrl");
			_upDesc = LoginUtil.getNodeValue(map, (el1 == null || el1.getLength() == 0) ? "updateInfo" : "appInfo");
			final String minVer = LoginUtil.getNodeValue(map, "minVer");
			final String maxVer = LoginUtil.getNodeValue(map, "maxVer");
			_version = maxVer;
			_upMd5 = LoginUtil.getNodeValue(map, "apkMD5");
			if (_upMd5 == null) {
				_upMd5 = LoginUtil.getNodeValue(map, "apkMd5");
			}

			String apkSize=LoginUtil.getNodeValue(map, "apkSize");
			long bytes=Long.parseLong(apkSize);
			_apkSize=NotifiyDownLoad.bytes2mb(bytes);

			if (Community.getInstacne().getAppVersion().compareTo(minVer) < 0) { // 强制升级
				_upgrade = UPGRADE_MUST;
			} else if (Community.getInstacne().getAppVersion().compareTo(maxVer) < 0) {
				_upgrade = UPGRADE_NEED;
			} else {
				_upgrade = UPGRADE_NONE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			_upgrade = UPGRADE_NONE;
		}
	}
	
	public boolean parseStatusXml(final byte[] buf, final boolean bool) {
		boolean isParseSuccess = false;
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			String dataStr = new String(buf, "UTF-8");
			// 获取xml输入数据
			p.setInput(new StringReader(dataStr));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tag = p.getName();
					if (tag.equals("newversion")) {
						_apkSize = p.getAttributeValue(null, "size");
						final String minVer = p.getAttributeValue(null, "minver");
						final String maxVer = p.getAttributeValue(null, "maxver");
						_version = maxVer;
						if (Community.getInstacne().getAppVersion().compareTo(minVer) < 0) { // 强制升级
							_upgrade = UPGRADE_MUST;
						} else if (Community.getInstacne().getAppVersion().compareTo(maxVer) < 0) {
							_upgrade = UPGRADE_NEED;
						} else {
							_upgrade = UPGRADE_NONE;
						}
					} else if (tag.equals("url")) {
						_upUrl = p.getAttributeValue(null, "value");
					} else if (tag.equals("md5")){
						_upMd5 = p.getAttributeValue(null, "value");
					} else if (tag.equals("gift")){
						GifModel gifModel = new GifModel();
						gifModel.picUrl = p.getAttributeValue(null, "photo");
						gifModel.gifName = p.getAttributeValue(null, "name");
						gifModelList.add(gifModel);
					} else if (tag.equals("content")){
						_upDesc = p.getAttributeValue(null, "text");
					}
					isParseSuccess = true;
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				default:
					break;
				}
				// 用next方法处理下一个事件，否则会造成死循环。
				eventType = p.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			isParseSuccess = false;
		}
		return isParseSuccess;
	}

	/**
	 * 判断是否是新用户
	 * 
	 * @return
	 */
	public boolean isNewUser() {
		return !LoginInfoManager.getInstance().isHasNativeLoginInfo();
	}
}
