package com.mykj.andr.logingift;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.Util.DownloadResultListener;

import android.content.Context;

public class LoginGiftManager{
	private LoginGiftManager(){}
	
	private static LoginGiftManager instance = null;
	public static LoginGiftManager getInstance(){
		if(instance == null){
			instance = new LoginGiftManager();
		}
		return instance;
	}
	
	/**协议*/
	public static final short MDM_USER_SIGN = 21;  //签到主协议
	public static final short LSUB_CMD_SIGN_CONFIG_REQ = 1; //礼包配置请求
	public static final short LSUB_CMD_SIGN_CONFIG_RESP = 2; //礼包配置下发
	public static final short LSUB_CMD_SIGN_STATUS_REQ = 3;  //签到状态数据请求
	public static final short LSUB_CMD_SIGN_STATUS_RESP = 4; //签到状态数据下发
	public static final short LSUB_CMD_USER_SIGN_REQ = 5;    //正常签到请求
	public static final short LSUB_CMD_USER_REPAIR_SIGN_REQ = 6;   //补签请求
	public static final short LSUB_CMD_USERSIGN_RESP =7;         //签到下发
	public static final short LSUB_CMD_GET_SIGN_AWARD_REQ = 8;   //领奖请求
	public static final short LSUB_CMD_GET_SIGN_AWARD_RESP =9;  //领奖下发
	public static final short LSUB_CMD_DAY_SIGN_CONFIG_REQ = 10; //签到配置请求
	public static final short LSUB_CMD_DAY_SIGN_CONFIG_RESP = 11; //签到配置下发
	
	
	private static final String loginGiftConfigKey = "loginGiftConfigKey";
	private static final String loginGiftConfigVal = "loginGiftConfigVal";
	private static final String loginSignConfigKey = "loginSignConfigKey";
	private static final String loginSignConfigVal = "loginSignConfigVal";
	
	public static final String TAG = "LoginGiftManager";
	public static final String SIGN_IMG_PATH=Util.getSdcardPath() + AppConfig.DOWNLOAD_FOLDER + "/"
			+"signpic"+"/";
	
	private LoginGiftQuickItem quickItem = null;
	private int loginGiftConfigNum = 0;
	private int loginSignConfigNum = 0;
	private String loginGiftConfigStr = "";
	private String loginSignConfigStr = "";
	private List<LoginGiftItem> loginGiftItemList = new ArrayList<LoginGiftManager.LoginGiftItem>();
	private List<LoginSignItem> loginSignItemList = new ArrayList<LoginGiftManager.LoginSignItem>();
	private LoginStatus mStat = new LoginStatus();
	private LoginGiftDialog dlg = null;
	/**
	 * 初始化礼包配置数据
	 */
	private void initGiftConfigData(){
		quickItem = null;
		loginGiftConfigNum = 0;
		loginGiftConfigStr = "";
		synchronized (loginGiftItemList) {
			loginGiftItemList.clear();
		}
	}
	
	public void setDialog(LoginGiftDialog dlg){
		this.dlg = dlg;
	}
	
	public LoginGiftQuickItem getBuyItem(){
		return quickItem;
	}
	
	/**
	 * 请求配置
	 * @param context
	 */
	public void requestConfig(final Context context){
		requestGiftConfig(context);
		requestDaySignConfig(context);
	}
	
	/**
	 * 请求礼包配置
	 * @param context
	 */
	private void requestGiftConfig(final Context context){
		TDataOutputStream dos = new TDataOutputStream(false);
		String ver  =  Util.getStringSharedPreferences(context,
				loginGiftConfigKey, "");
		dos.writeUTF(ver, 32);
		NetSocketPak data = new NetSocketPak(MDM_USER_SIGN,
				LSUB_CMD_SIGN_CONFIG_REQ, dos);
		NetSocketManager.getInstance().sendData(data);
		short[][] parseProtocol = { { MDM_USER_SIGN,
			LSUB_CMD_SIGN_CONFIG_RESP } };
		initGiftConfigData();
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				byte code = tdis.readByte();
				if(code == 1){ //失败
					initGiftConfigData();
				}else if(code == 2){ //本地
					loginGiftConfigStr = Util.getStringSharedPreferences(context, loginGiftConfigVal, "");
					parseGiftConfig(loginGiftConfigStr);
				}else if(code == 0){ //成功
					String ver = tdis.readUTFByte();
					byte QuicklyFlag = tdis.readByte();
					short total = tdis.readShort();
					short current = tdis.readShort();
					for(int i = 0; i < current;i++){
						String data = tdis.readUTFShort();
						loginGiftConfigStr += data + "&&";
					}
					loginGiftConfigNum += current;
					if(loginGiftConfigNum >= total){
						Util.setStringSharedPreferences(context,
								loginGiftConfigKey, ver);
						Util.setStringSharedPreferences(context, loginGiftConfigVal, loginGiftConfigStr);
						parseGiftConfig(loginGiftConfigStr);
					}
				}
				return true;
			}
		};
		nPListener.setOnlyRun(false);
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}
	
	/**
	 * 
	 * 解析礼包配置文件
	 * @param str
	 */
	private void parseGiftConfig(String str){
		if(Util.isEmptyStr(str)){
			return;
		}
		
		String[] vals = str.split("&&");
		for(int i = 0; i < vals.length;i++){
			if(vals[i].contains("<qk")){
				parseQuick(vals[i]);
			}else{
				parseGiftItem(vals[i]);
			}
		}
		
		downloadSubGiftImage();
	}

	/**
	 * 解析快捷购买的内容
	 * @param strXml 要解析的字符串
	 */
	private void parseQuick(String strXml){
		if (strXml == null) {
			return ;
		}
		{
			int startIndex = strXml.indexOf("<?");
			if (startIndex > 0) {
				strXml = strXml.substring(startIndex);
			}
		}

		if(quickItem == null){
			quickItem = new LoginGiftQuickItem();
		}
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = p.getName();
					if (tagName.equals("qk")) { 
						quickItem.id = Integer.parseInt(p.getAttributeValue(null, "gd")); // 道具id
						quickItem.desc = p.getAttributeValue(null, "m"); // 描述
					}
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
			Log.v(TAG, "parse xml error");
			quickItem.id=0;
			quickItem.desc="";
		}
	}
	
	private void downloadSubGiftImage(){
		File path = new File(SIGN_IMG_PATH);
		try{
			if(!path.exists()){
				path.mkdirs();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		synchronized (loginGiftItemList) {
			for(int i = 0; i < loginGiftItemList.size();i++){
				List<GiftSubItem> subs = loginGiftItemList.get(i).subs;
				if(subs!=null && subs.size() > 0)
				for(int j = 0;j < subs.size();j++){
					GiftSubItem subItem = subs.get(j);
					checkAndDownloadImage(subItem.pic, null);
				}
				
			}
		}
	}
	
	public File checkAndDownloadImage(String pic, DownloadResultListener listener){
		if(Util.isEmptyStr(pic)){
			return null;
		}
		File file = new File(SIGN_IMG_PATH+pic);
		if(!file.exists()){
			Util.downloadFile(AppConfig.SIGN_IMG_URL + pic, SIGN_IMG_PATH + pic, listener);
			return null;
		}else{
			return file;
		}
	}
	
	private void downloadSignImage(){
		File path = new File(SIGN_IMG_PATH);
		try{
			if(!path.exists()){
				path.mkdirs();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		synchronized (loginSignItemList) {
			for(int i = 0; i < loginSignItemList.size();i++){
				LoginSignItem item = loginSignItemList.get(i);
				checkAndDownloadImage(item.pic,null);
			}
		}
	}
	
	/**
	 * 解析礼包内容
	 * @param strXml 要解析的字符串
	 */
	private void parseGiftItem(String strXml){
		if (strXml == null) {
			return ;
		}
		{
			int startIndex = strXml.indexOf("<?");
			if (startIndex > 0) {
				strXml = strXml.substring(startIndex);
			}
		}

		try {
			LoginGiftItem item = new LoginGiftItem();
			item.subs = new ArrayList<LoginGiftManager.GiftSubItem>();
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = p.getName();
					if (tagName.equals("ea")) { 
						item.index = Integer.parseInt(p.getAttributeValue(null, "ix")); // 索引
						item.id = Integer.parseInt(p.getAttributeValue(null, "ai")); // id
						item.name = p.getAttributeValue(null, "m"); //名字
						item.desc = p.getAttributeValue(null, "d"); //描述
						item.pic =  p.getAttributeValue(null, "l"); //图片
						item.day = Integer.parseInt(p.getAttributeValue(null, "dy")); // 天数
					}else if(tagName.equals("s")){
						GiftSubItem subItem = new GiftSubItem();
						subItem.id = Integer.parseInt(p.getAttributeValue(null, "i")); // id
						subItem.num = Integer.parseInt(p.getAttributeValue(null, "n")); // 数量;
						subItem.name = p.getAttributeValue(null, "ni"); //名字
						subItem.pic = p.getAttributeValue(null, "p"); //图片
						item.subs.add(subItem);
					}
					break;
				case XmlPullParser.END_TAG:
					String endName = p.getName();
					if (endName.equals("ea")) {
						loginGiftItemList.add(item);
					}
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
			Log.v(TAG, "parse xml error");
		}
	}
	

	/**
	 * @author Administrator
	 * 签到快捷购买的东西，一般是补签卡
	 */
	public class LoginGiftQuickItem{
		int id;
		String desc;
	}
	
	/**
	 * @author Administrator
	 * 签到礼包
	 */
	public class LoginGiftItem{   
		int index;
		int id;
		String name;
		String desc;
		String pic;
		int day;
		List<GiftSubItem> subs;
	}
	
	
	/**
	 * @author Administrator
	 * 礼包里面的东西
	 */
	public class GiftSubItem{
		int id;
		int num;
		String name;
		String pic;
	}
	
	public class LoginSignItem{
		int day;
		String pic;
		String num;
		String item;
	}
	
	/**
	 * 签到状态
	 * @author wanghj
	 *
	 */
	public class LoginStatus{
		byte signNum;
		byte unsignNum;
		boolean todaySign;
		boolean allSign;
		int propCount;
		int awardStatus;
		int signStatus;
		int vipAward = 0;
		int becomeVipProp = 0;
	}
	
	public final List<LoginGiftItem> getGiftItemList(){
		return loginGiftItemList;
	}
	

	/**
	 * 请求签到状态
	 * @param context
	 */
	public void requestStatus(){
		NetSocketPak data = new NetSocketPak(MDM_USER_SIGN,
				LSUB_CMD_SIGN_STATUS_REQ);
		NetSocketManager.getInstance().sendData(data);
		short[][] parseProtocol = { { MDM_USER_SIGN,
			LSUB_CMD_SIGN_STATUS_RESP } };
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				synchronized (mStat) {
					mStat.signNum = tdis.readByte();
					mStat.unsignNum = tdis.readByte();
					mStat.todaySign = (tdis.readByte() == 1);
					mStat.allSign = (tdis.readByte() == 1);
					mStat.propCount = tdis.readInt();
					int val = tdis.readInt();
					mStat.awardStatus = val;
					mStat.signStatus = tdis.readInt();
				}
				if(dlg!=null){
					try{
					dlg.onStatusReceive();
					}catch(Exception e){
						
					}
				}
				return true;
			}
		};
		nPListener.setOnlyRun(false);
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}
	
	
	/**
	 * 请求签到
	 * @param type LSUB_CMD_USER_SIGN_REQ 或者 LSUB_CMD_USER_REPAIR_SIGN_REQ
	 */
	public void requestSign(short type){
		if(type == LSUB_CMD_USER_SIGN_REQ || type == LSUB_CMD_USER_REPAIR_SIGN_REQ){
			NetSocketPak data = new NetSocketPak(MDM_USER_SIGN,
					type);
			NetSocketManager.getInstance().sendData(data);
		}
	}
	
	/**
	 * 注册签到结果监听
	 */
	public void registerSignListener(){
		short[][] parseProtocol = { { MDM_USER_SIGN,
			LSUB_CMD_USERSIGN_RESP } };
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				byte result = tdis.readByte();  //1-31表示成功，0表示失败,32表示补签
				synchronized (mStat) {
					mStat.signNum = tdis.readByte();
					mStat.unsignNum = tdis.readByte();
					mStat.todaySign = (tdis.readByte() == 1);
					mStat.allSign = (tdis.readByte() == 1);
					mStat.propCount = tdis.readInt();
					mStat.awardStatus = tdis.readInt();
					mStat.signStatus = tdis.readInt();
					mStat.vipAward = tdis.readInt();
					mStat.becomeVipProp = tdis.readInt();
				}
				if(dlg != null){
					try{
						dlg.onSignResult(result);
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				return true;
			}
		};
		nPListener.setOnlyRun(false);
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}
	
	/**
	 * 
	 * 请求领奖
	 * @param index 奖品index
	 */
	public void requestAward(byte index){
		TDataOutputStream dos = new TDataOutputStream(false);
		dos.writeByte(index);
		NetSocketPak data = new NetSocketPak(MDM_USER_SIGN,
				LSUB_CMD_GET_SIGN_AWARD_REQ,dos);
		NetSocketManager.getInstance().sendData(data);
		short[][] parseProtocol = { { MDM_USER_SIGN,
			LSUB_CMD_GET_SIGN_AWARD_RESP } };
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				byte result = tdis.readByte();
				synchronized (mStat) {
					mStat.awardStatus = tdis.readInt();
				}
				
				String note = tdis.readUTFShort();
				if(dlg!=null){
					try{
						dlg.onGiftResult(result,note);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				return true;
			}
		};
		nPListener.setOnlyRun(false);
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}
	
	/**
	 * 初始化签到数据
	 */
	private void initSignConfigData(){
		loginSignConfigNum = 0;
		loginSignConfigStr = "";
		synchronized (loginSignItemList) {
			loginSignItemList.clear();
		}
	}
	
	/**
	 * 解析签到配置
	 * @param strXml
	 */
	private void parseSignConfig(String strXml){
		if(Util.isEmptyStr(strXml)){
			return;
		}
		String[] vals = strXml.split("&&");
		for(int i = 0; i < vals.length; i++){
			parseSignItem(vals[i]);
		}
		
		synchronized (loginSignItemList) {  //排序
			Collections.sort(loginSignItemList, new Comparator<LoginSignItem>() {

				@Override
				public int compare(LoginSignItem arg0, LoginSignItem arg1) {
					// TODO Auto-generated method stub
					return arg0.day - arg1.day;
				}
			});
		}
		downloadSignImage();
	}
	
	/**
	 * 解析签到物品
	 * @param strXml
	 */
	private void parseSignItem(String strXml){
		if (strXml == null) {
			return ;
		}
		{
			int startIndex = strXml.indexOf("<?");
			if (startIndex > 0) {
				strXml = strXml.substring(startIndex);
			}
		}

		try {
			LoginSignItem item = new LoginSignItem();
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = p.getName();
					if (tagName.equals("ea")) { 
						item.pic =  p.getAttributeValue(null, "l"); //图片
						item.day = Integer.parseInt(p.getAttributeValue(null, "dy")); // 天数
						item.num = p.getAttributeValue(null, "n");
						item.item = p.getAttributeValue(null, "m");
					}
					break;
				case XmlPullParser.END_TAG:
					String endName = p.getName();
					if (endName.equals("ea")) {
						loginSignItemList.add(item);
					}
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
			Log.v(TAG, "parse xml error");
		}
	}
	
	/**
	 * 请求每日签到配置
	 * @param context
	 */
	private void requestDaySignConfig(final Context context){
		TDataOutputStream dos = new TDataOutputStream(false);
		String ver  =  Util.getStringSharedPreferences(context,
				loginSignConfigKey, "");
		dos.writeUTF(ver, 32);
		NetSocketPak data = new NetSocketPak(MDM_USER_SIGN,
				LSUB_CMD_DAY_SIGN_CONFIG_REQ, dos);
		NetSocketManager.getInstance().sendData(data);
		short[][] parseProtocol = { { MDM_USER_SIGN,
			LSUB_CMD_DAY_SIGN_CONFIG_RESP } };
		initSignConfigData();
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			@Override
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				byte code = tdis.readByte();
				if(code == 1){ //失败
					initSignConfigData();
				}else if(code == 2){ //本地
					loginSignConfigStr = Util.getStringSharedPreferences(context, loginSignConfigVal, "");
					parseSignConfig(loginSignConfigStr);
				}else if(code == 0){ //成功
					String ver = tdis.readUTFByte();
					short total = tdis.readShort();
					short current = tdis.readShort();
					for(int i = 0; i < current;i++){
						String data = tdis.readUTFShort();
						loginSignConfigStr += data + "&&";
					}
					loginSignConfigNum += current;
					if(loginSignConfigNum >= total){
						Util.setStringSharedPreferences(context,
								loginSignConfigKey, ver);
						Util.setStringSharedPreferences(context, loginSignConfigVal, loginSignConfigStr);
						parseSignConfig(loginSignConfigStr);
					}
				}
				return true;
			}
		};
		nPListener.setOnlyRun(false);
		NetSocketManager.getInstance().addPrivateListener(nPListener);
	}
	
	public List<LoginSignItem> getLoginSignItemList(){
		return loginSignItemList;
	}
	
	/**
	 * @param day 要获取的日期，1-31
	 * @return
	 */
	public LoginSignItem getLoginSignItem(int day){
		if(loginSignItemList!=null && loginSignItemList.size() > 0){
			synchronized (loginSignItemList) {
				for(LoginSignItem item : loginSignItemList){
					if(item.day == day){
						return item;
					}
				}
			}
		}
		return null;
	}
	
	public final LoginStatus getLoginStatus(){
		return mStat;
	}
}