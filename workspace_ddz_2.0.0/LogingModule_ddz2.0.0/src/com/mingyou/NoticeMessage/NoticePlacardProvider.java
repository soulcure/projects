/**
 * 
 */
package com.mingyou.NoticeMessage;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Message;

import com.login.utils.HostAddressPool;
import com.mingyou.community.Community;
import com.mingyou.login.LoginHttp;
import com.minyou.android.net.HttpConnector;
import com.minyou.android.net.IRequest;
import com.minyou.android.net.NetService;
import com.mykj.comm.io.TDataInputStream;

import debug.IP_CONFIG_FILE;

/**
 * @author Jason
 * 
 */
public class NoticePlacardProvider {
	//private final static String NOTICE_HOST = HostAddressPool.NOTICE_HOST;
	// "http://notice.139game.net/notice.php";

	private static NoticePlacardProvider instance = null;

	private NoticePlacardProvider() {
		list = new ArrayList<NoticePlacardInfo>();
	}

	private List<NoticePlacardInfo> list = null;

	public static NoticePlacardProvider getInstance() {
		if (instance == null) {
			instance = new NoticePlacardProvider();
		}
		return instance;
	}

	public void init() {
		list.clear();
	}

	public void addPlacardInfo(NoticePlacardInfo info) {
		list.add(info);
	}

	public interface NoticeListener {
		void onFialed();

		void onSucceed();
	}

	ArrayList<NoticeListener> _list = new ArrayList<NoticePlacardProvider.NoticeListener>();

	public void addNoticeListener(NoticeListener lis) {
		_list.add(lis);
		if (!list.isEmpty()) {
			onSucceed();
		}
	}

	private void onSucceed() {
		for (int i = 0; i < _list.size(); i++) {
			NoticeListener lis = _list.get(i);
			if (lis != null) {
				lis.onSucceed();
			}
		}
		_list.clear();
	}

	private void onFailed() {
		for (int i = 0; i < _list.size(); i++) {
			NoticeListener lis = _list.get(i);
			if (lis != null) {
				lis.onFialed();
			}
		}
		_list.clear();
	}

	public NoticePlacardInfo[] getPlacardInfos() {
		// Collections.reverse(list);
		return list.toArray(new NoticePlacardInfo[list.size()]);
	}

	public List<NoticePlacardInfo> getParsetNoticePersons() {
		return list;
	}

	/***
	 * @Title: addPersonArray
	 * @Description: 添加对象数组到list中
	 * @param array
	 * @version: 2012-11-28 下午03:09:51
	 */
	public void addPlacardArray(NoticePlacardInfo[] array) {
		for (NoticePlacardInfo info : array) {
			list.add(info);
		}
		// Collections.reverse(list);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return list != null && list.isEmpty();
	}

	private int reqNoticeCount = 0;

	private final int max_count = 3;

	public void reqNoticeInfo() {
		if (!list.isEmpty()) {
			return;
		}
		final HttpConnector http = LoginHttp.createLoginHttpConnector();
		IRequest iRequest = new IRequest() {

			@Override
			public void doError(Message msg) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				if (++reqNoticeCount < max_count) {
					reqNoticeInfo();
				} else {
					onFailed();
				}
			}

			public void handler(byte[] buf) {
				NetService.getInstance().removeHttpConnector(http.getTarget());
				final String str = TDataInputStream.getUTF8String(buf);
				try {
					parseNoticePlacardInfo(str);
				} catch (Exception e) {
					e.printStackTrace();
				}
				onSucceed();
			}

			@Override
			public String getParam() {
				StringBuilder buf = new StringBuilder();
				buf.append("gameid=").append(
						Community.getInstacne().getGameID());
				buf.append("&cid=").append(Community.getInstacne().getCID());
				buf.append("&mct=").append("1");
				return buf.toString();
			}

			public String getHttpUrl() {
				StringBuilder buf = new StringBuilder();
				buf.append(HostAddressPool.NOTICE_HOST);
				//超爽斗地主添加
				if(Community.PlatID==Community.PLAT_CSDDZ){
					buf.append("/chaoshuang");
				}
				buf.append("/notice.php");
				
				return buf.toString();
				
			}
		};
		http.addEvent(iRequest);
		http.connect();
	}

	private void parseNoticePlacardInfo(final String text) throws Exception {
		JSONTokener tokener = new JSONTokener(text);
		Object obj = tokener.nextValue();
		if (obj instanceof JSONArray) {
			// 滚动公告信息
			JSONArray noticeList = (JSONArray) obj;
			if (noticeList != null) {
				final int len = noticeList.length();
				if (len != 0) {
					NoticePlacardProvider.getInstance().init(); // 清除系统消息
					for (int i = 0; i < len; i++) {
						JSONObject notice = noticeList.getJSONObject(i);
						final String msg = notice.getString("text");
						NoticePlacardProvider.getInstance().addPlacardInfo(
								new NoticePlacardInfo(msg));
					}
				}
			}
		}
	}

}
