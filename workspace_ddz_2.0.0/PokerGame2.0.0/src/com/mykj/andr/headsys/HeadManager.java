package com.mykj.andr.headsys;

import java.io.File;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataInputStream.MDataMark;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;


/**
 * @author Administrator wanghj
 * 头像系统管理
 */
public class HeadManager{
	private static final String TAG = "HeadManager";
	public static final int ZONEHEAD = 1;        //分区自定义头像  ,约定分区购买头像uerid==0
	public static final int GAMEHEAD = 2;        //游戏购买头像
	public static final int SELFHEAD = 3;        //用户购买头像

	private static HeadManager instance = null;
	private int curId = 0;               //当前id
	private int askUseId = 0;            //请求使用id
	private static final int HEAD_TYPE = 1;    //与服务器配套
	private static final short LS_MDM_PROP = 17;                 //手机道具主协议
	private static final short LSUB_CMD_EXTEND_SHOP_LIST_REQ=758;  //请求扩展商品列表
	private static final short LSUB_CMD_EXTEND_SHOP_LIST_RESP=759; //接收扩展商品列表
	private static final short LSUB_CMD_EXTEND_PACK_LIST_REQ=808;  //请求扩展背包列表
	private static final short LSUB_CMD_EXTEND_PACK_LIST_RESP=809; //接收扩展背包列表
	private static final short LSUB_CMD_MODIFY_USER_HEAD_REQ=810;  //请求使用头像
	private static final short LSUB_CMD_MODIFY_USER_HEAD_RESP=811;  //接收使用结果

	private static final int HANDLER_DOWNLOAD_IMG = 0;       //通知下载图片
	private static final int HANDLER_REQUEST_PACK = 1;       //通知下载头像道具id
	private static final int HANDLER_GETPACK_FINISH = 2;     //获得头像道具完成
	//private static final int HANDLER_DOWNLOAD_UPLOAD_IMG = 3;     //通知下载自定义图片

	//解析应用列表结果
	public static final int PARSE_NO_NODE = 0;			//没有子节点
	public static final int PARSE_SUCCESS = 1;			//解析成功
	public static final int PARSE_FAIL = 2;				//解析失败

	private List<HeadInfo> heads = new ArrayList<HeadInfo>();         //头像列表
	private HashMap<String, SoftReference<Drawable>> iconMap = new HashMap<String, SoftReference<Drawable>>();   //头像图片缓存
	private Handler updateHandler = null;    //更新ui
	private int updateHandlerMsgWhat = 0;    //更新ui消息
	private String url;                      //下载url
	//private String fileName = "";            //文件名
	private boolean isDownloading = false;   //是否正在下载
	private final String PNG = ".png";     //png后缀
	private final String JPG = ".jpg";     //jpg后缀

	private Context mAct = null;
	public static HeadManager getInstance(){
		if(instance == null){
			instance = new HeadManager();
		}
		return instance;
	}

	private HeadManager(){
	}

	/**
	 * 设置头像上下文，作为弹框用
	 * @param context
	 */
	public void setContext(Context context){
		mAct = context;
	}

	/**
	 * 设置更新ui handler
	 * 当数据改变时发消息通知更新
	 * @param handler
	 * @param updateHandlerMsgWhat
	 */
	public void setUpdateHanler(Handler handler, int updateHandlerMsgWhat){
		updateHandler = handler;
		this.updateHandlerMsgWhat = updateHandlerMsgWhat;
	}

	/**
	 * 
	 * @param id 道具id
	 * @return 0表示金币或乐豆购买的头像，1表示虚拟币或移动点数， -1表示这不是头像
	 */
	public short getPayType(int id){
		if(heads != null){
			for(HeadInfo head : heads){
				if(head.getId() == id){
					if(head.getCurrencyType() == 3 || head.getCurrencyType() == 4){  //用金币或乐豆买
						return 0;
					}
					else{
						return 1;
					}
				}
			}
		}
		return -1;   //不在列表中
	}





	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case HANDLER_DOWNLOAD_IMG:      //下载图片
				int userId=msg.arg1;
				int faceId=msg.arg2;
				int type;
				if(faceId>5000 && userId!=0){
					type=SELFHEAD;  //用户自定义头像
				}else{
					if(userId==0){ //约定分区购买头像 userId传入0
						type=ZONEHEAD; //分区购买头像
					}else{
						type=GAMEHEAD;//游戏购买头像
					}
				
				}
				startDownloadImg(userId,faceId,type);
				break;
			case HANDLER_REQUEST_PACK:      //请求头像道具列表
				UserInfo u = HallDataManager.getInstance().getUserMe();
				if(u != null){
					requestHeadPackList(u.userID);
				}
				break;
			case HANDLER_GETPACK_FINISH:    //道具列表请求完毕
				int faceID = HallDataManager.getInstance().getUserMe().getFaceId();   //上次使用id
				for(HeadInfo head : heads){
					if(head.getId() == faceID){ 
						if(head.isHaved()){           //拥有，继续使用
							if(curId == 0){
								curId = faceID;
								if(updateHandler != null){   //通知更新
									updateHandler.obtainMessage(updateHandlerMsgWhat).sendToTarget();
								}
							}
						}else{
							if(curId == askUseId){
								requestUseHead(curId);       //不再拥有，请求使用新的
							}
						}
						break;
					}
				}
				break;
//			case HANDLER_DOWNLOAD_UPLOAD_IMG:
//				int downLoadfaceId=msg.arg1;
//				startDownloadUploadImg(downLoadfaceId);
//				break;
			default:
				break;
			}
		}

	};


	/*private void startDownloadUploadImg(int faceId){
		if(isDownloading){   //正在下载中
			return;
		}
		if(!Util.isMediaMounted()){    //没有存储卡
			return;
		}
		//先确定路径创建好
		File parent = new File(HeadConfig.selfHeadSavePth);
		if(!parent.exists()){
			parent.mkdirs();
		}

		String dir;//图片存储文件夹
		String fileType; //图片的文件类型
		String downloadFileName; //图片的名称

		dir = HeadConfig.selfHeadSavePth;
		fileType=JPG;
		downloadFileName = faceId + fileType;
		url =  AppConfig.DOWNLOAD_HOST + "/" + downloadFileName;

		final String fileName = dir + "h_"+ downloadFileName;   //需要下载的文件保存全路径


		if(!isDownloading){   //当前不在下载
			new Thread(){
				public void run(){
					isDownloading = true;   //标识
					boolean downRlt = Util.downloadResByHttp(url, fileName);   //下载

					if(downRlt && updateHandler != null){  //下载成功通知更新ui
						updateHandler.sendEmptyMessage(updateHandlerMsgWhat);
					}
					isDownloading = false;  //标识
				}
			}.start();
		}

	}*/


	/**
	 * 下载图片
	 */
	private void startDownloadImg(int userId,int faceId,int type){
		if(isDownloading ||!Util.isMediaMounted()){   //正在下载中 //没有存储卡
			return;
		}
		
		String dir;//图片存储文件夹
		String fileType; //图片的文件类型
		String downloadFileName; //图片的名称
		
		if(type==ZONEHEAD){  
			dir = HeadConfig.zoneHeadSavePth;
			fileType=PNG;
			downloadFileName = "prop_" + faceId + fileType;
			url = AppConfig.HEAD_ICON_URL + "/" + downloadFileName;
		}else if(type==GAMEHEAD){
			dir = HeadConfig.gameHeadSavePth;
			fileType=PNG;
			downloadFileName = "prop_" + faceId + "_1" + PNG;
			url = AppConfig.HEAD_ICON_URL + "/" + downloadFileName;
		}else{//SELFHEAD
			dir = HeadConfig.selfHeadSavePth;
			fileType=JPG;
			int userIdValue = userId+5000;
			downloadFileName = userIdValue + "_" + faceId + fileType;
			int headDir = userIdValue % 200;
			url =  AppConfig.DOWNLOAD_HOST + "/"+headDir + "/" + downloadFileName;
			//url =  AppConfig.DOWNLOAD_HOST + "/" + downloadFileName;
		}
		
		final String fileName = dir + "h_"+ faceId + fileType;   //需要下载的文件保存全路径
		
		//先确定路径创建好
		File fileDir = new File(dir);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		
		if(!isDownloading){   //当前不在下载
			new Thread(){
				public void run(){
					isDownloading = true;   //标识
					boolean downRlt = Util.downloadResByHttp(url, fileName);   //下载

					if(downRlt && updateHandler != null){  //下载成功通知更新ui
						updateHandler.sendEmptyMessage(updateHandlerMsgWhat);
					}
					isDownloading = false;  //标识
				}
			}.start();
		}
	}

	/**
	 * 获取图片完整文件名
	 * @param userId
     * @param faceId
	 * @param type
	 * @return
	 */
	public String getImgFullFileName(int userId,int faceId, int type){
		String res=null;
		String dir;
		String fileName;
		if(type == SELFHEAD){
			dir = HeadConfig.selfHeadSavePth;
			fileName = dir + "h_"+ faceId + JPG;
		}else{
			dir = HeadConfig.gameHeadSavePth;
			fileName = dir + "h_"+ faceId + PNG;
		}

		File file=new File(fileName);

		if(file.exists() && !file.isDirectory()){
			Drawable icon = getDrawableFromFile(AppConfig.mContext, file,
					DisplayMetrics.DENSITY_HIGH);     //获得drawable
			if(Util.isDrawableAvailable(icon)){
				if(type == ZONEHEAD){   //是分区这边的，则直接缓存起来，免得二次解析
					iconMap.put("h_" + faceId, new SoftReference<Drawable>(icon));
				}
				res= file.getAbsolutePath();
			}else{
				file.delete();   //无效文件则删除
			}
		}

		if(res==null){
			Message msg=handler.obtainMessage();
			msg.arg1=userId;
			msg.arg2=faceId;
			msg.what=HANDLER_DOWNLOAD_IMG;
			handler.sendMessage(msg);
		}

		return res;
	}


	private int needW = 0;     //分区头像需要的宽，缩放用
	private int needH = 0;     //分区头像需要的高，缩放用

	/**
	 * 获得drawable,不用util的因为要进行缩放
	 * @param context
	 * @param pngFile
	 * @param density
	 * @return
	 */
	private Drawable getDrawableFromFile(Context context, File pngFile,
			int density) {
		Bitmap bmp = BitmapFactory.decodeFile(pngFile.getPath());
		if (bmp != null){
			bmp.setDensity(density);
			if(needW == 0){   //未初始化数据，解析默认图片获得宽高
				DisplayMetrics metric = new DisplayMetrics();
				((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
				Drawable ori = context.getResources().getDrawable(R.drawable.ic_female_face);
				needW = ori.getIntrinsicWidth() * density / metric.densityDpi;
				needH = ori.getIntrinsicHeight() * density / metric.densityDpi;
			}
			Bitmap resizeBmp = Bitmap.createScaledBitmap(bmp, needW, needH, true);   //缩放
			if(bmp.hashCode() != resizeBmp.hashCode()){    //当要求大小与原大小一致时可能返回本身，所以需要比较hashcode
				bmp.recycle();   //将原bitmap释放，否则引起内存溢出
			}
			Drawable icon = new BitmapDrawable(context.getResources(), resizeBmp);
			return icon;
		}
		return null;
	}



	/**
	 * 通过id获得图片，可能返回null
	 * @param context
	 * @param id
	 * @return
	 */
	private Drawable getIconFromFile(Context context, int id){
		String key = "h_"+id;
		Drawable icon = null;
		try{
			String fileName = HeadConfig.zoneHeadSavePth + "/"+ key;
			String fileType=PNG; //图片的文件类型

			if(id>5000){  //代表自定义头像
				fileName= HeadConfig.selfHeadSavePth + "/"+ key;
				fileType=JPG;
			}

			File iconFile = new File(fileName + fileType);
			if(iconFile.exists() && !iconFile.isDirectory()){
				icon = getDrawableFromFile(context, iconFile,
						DisplayMetrics.DENSITY_HIGH);
				if(!Util.isDrawableAvailable(icon)){
					icon = null;
					iconFile.delete();
				}
			}
		}catch(Exception e){

		}
		return icon;
	}




	/**
	 * 通过id获得图片，可能返回null
	 * @param context
	 * @param id
	 * @return
	 */
//	public void setSelfImageByValue(Context context,int userId,
//			int faceId, int n){
//		if(faceId<5000 || n<0){  //代表非自定义头像
//			return;
//		}
//
//		String key="self_image"+userId;
//
//		int saveValue=Util.getIntSharedPreferences(context, key, 0);
//		if(saveValue!=n){
//			Util.setIntSharedPreferences(context, key, n);
//
//			String filename = HeadConfig.selfHeadSavePth +"h_"+faceId;
//			String fileType=JPG;
//
//			try{
//				File iconFile = new File(filename + fileType);
//				if(iconFile.exists()){
//					iconFile.delete();
//				}
//				iconMap.remove("h_" + faceId);
//			}catch(Exception e){
//
//			}
//
//		}
//
//	}




	/**
	 * 分区购买头像接口
	 * 通过faceId获得图片，分区用,约定userId=0;
	 * @param context
	 * @param faceId
	 * @return
	 */
	public Drawable getZoneHead(Context context,int faceId){
		//约定分区购买头像 ，userId传入0
		return getZoneHead(context,0,faceId,true, -1);
	}


	/**
	 * 通过userId & faceId 获得图片
	 * 用于用户自定义获取头像
	 */
	public Drawable getZoneHead(Context context, int userId,int faceId){
		return getZoneHead(context,userId,faceId,true, -1);
	}
	
	
	
	
	/**
	 * 通过userId & faceId 获得图片
	 * @param context
	 * @param gender 0-女  1-男  -1 表示该参数无效
	 */
	public Drawable getZoneHead(Context context, int userId,int faceId, boolean needDefault, int gender){
		Drawable icon = null;
		String key = "h_"+faceId;

		if(iconMap.containsKey(key)){  //先在缓存中找
			icon = iconMap.get(key).get();
		}
		
		if(icon != null){  //缓存中有
			return icon;
		}else{  //缓存中没有
			icon = getIconFromFile(context, faceId);   //在文件中找
			if(icon != null){  //文件中有，则加入缓存
				SoftReference<Drawable> s = new SoftReference<Drawable>(icon);
				iconMap.put(key, s);
				return icon;
			}
		}

		//本地没有，需要下载
		Message msg=handler.obtainMessage();
		msg.arg1=userId;
		msg.arg2=faceId;
		msg.what=HANDLER_DOWNLOAD_IMG;

		handler.sendMessage(msg);
		
		//返回默认图片
        if(needDefault){
        	if(gender < 0){
            	byte sex = HallDataManager.getInstance().getUserMe().gender;
        		if(sex == 0){
        			icon= context.getResources().getDrawable(R.drawable.ic_female_face);
        		}else{
        			icon= context.getResources().getDrawable(R.drawable.ic_male_face);
        		}
        	}else{
        		if(gender == 0){
        			icon= context.getResources().getDrawable(R.drawable.ic_female_face);
        		}else{
        			icon= context.getResources().getDrawable(R.drawable.ic_male_face);
        		}
        	}

        }else{
        	if(gender == 0){
    			icon= context.getResources().getDrawable(R.drawable.ic_female_face);
    		}else{
    			icon= context.getResources().getDrawable(R.drawable.ic_male_face);
    		}
        }
		
		return icon;
	
		
	}
	
	
	
	/**
	 * 获得id
	 * @return
	 */
	public int getCurId(){
		return curId;
	}

	private int headCount = 0;   //商城中头像总个数
	private boolean isHeadMarketFinish = false;  //头像商城是否下载完毕

	/**
	 * 头像商城是否下载完毕
	 * @return
	 */
	public boolean isGetHeadMarketListFinish(){
		return isHeadMarketFinish;
	}

	private boolean isRequestHeadMarket = false;
	/**
	 * 请求头像商城列表
	 * @param context
	 */
	public void requestHeadMarketList(Context context) {
		if(isRequestHeadMarket){
			return;
		}
		isRequestHeadMarket = true;
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream(false);
		short type = (short) UtilHelper.getMobileCardType(context); /**1：中国移动,2：中国联通,3：中国电信,4：无卡*/
		if (type == 0) {
			type = 4;
		}
		tdos.writeShort(type);
		tdos.writeShort(HEAD_TYPE);   //1表示头像
		NetSocketPak pointBalance = new NetSocketPak(LS_MDM_PROP,
				LSUB_CMD_EXTEND_SHOP_LIST_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { LS_MDM_PROP, LSUB_CMD_EXTEND_SHOP_LIST_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				TDataInputStream tdis = netSocketPak.getDataInputStream();

				int total = tdis.readShort();     //商城头像总数
				int curNum = tdis.readShort();    //当前包所包含头像数
				int type = tdis.readShort(); // 商品类型
				if(type == HEAD_TYPE){   //表示表情类商品
					headCount += curNum;  //已得到的增加
					for(int i = 0; i < curNum; i++){
						try{
							String servData = tdis.readUTFShort();
							parseHeadMarketXml(servData);
						}catch(Exception e){
							Log.i(TAG, "头像商品解析错误："+i);
						}
					}
					if(headCount >= total){  //认为已接收完成
						isHeadMarketFinish = true;
						isRequestHeadMarket = false;
						if(updateHandler != null){
							updateHandler.obtainMessage(updateHandlerMsgWhat).sendToTarget();
						}

						handler.obtainMessage(HANDLER_REQUEST_PACK).sendToTarget();    //请求下载道具列表
					}
				}

				return true;
			}
		};
		nPListener.setOnlyRun(false);

		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	/**
	 * 解析网络数据，商城头像
	 * @param strXml
	 * @return
	 */
	private int parseHeadMarketXml(String strXml) {
		int isParseSuccess = PARSE_NO_NODE;
		if (strXml == null) {
			return isParseSuccess;
		}
		{
			int startIndex = strXml.indexOf("<?");
			if(startIndex > 0){
				strXml = strXml.substring(startIndex);
			}
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
					if (tagName.equals("h")) {
						HeadInfo head = new HeadInfo(p);
						boolean newData = true;
						for(HeadInfo temp : heads){
							if(temp.getId() == head.getId()){
								newData = false;
								break;
							}
						}
						if(newData){
							heads.add(head);
						}

						isParseSuccess = PARSE_SUCCESS;
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
			isParseSuccess = PARSE_FAIL;
		}
		return isParseSuccess;
	}

	/**
	 * 解析网络数据，背包头像
	 * @param tdis
	 */
	private void parseHeadPackInfo(TDataInputStream tdis){
		int len = tdis.readShort();
		MDataMark mark= tdis.markData(len);
		int id = tdis.readInt();
		int expireDate = tdis.readInt();
		String name = tdis.readUTFShort();
		String desc = tdis.readUTFShort();
		tdis.unMark(mark);
		for(HeadInfo head : heads){   //查找列表，若有数据则更新
			if(head.getId() == id){
				if(!Util.isEmptyStr(name)){
					head.setName(name);
				}
				if(!Util.isEmptyStr(desc)){
					desc = desc.replace("&#x0A;", "\n");
					head.setFullDesc(desc);
				}
				head.setExpireTime(expireDate);
				head.markHaved();
				return;
			}
		}

		//不存在列表中，表明是个下架又没到期的道具
		HeadInfo head = new HeadInfo(id);
		head.setName(name);
		head.setFullDesc(desc);
		head.setExpireTime(expireDate);
		head.markHaved();
		heads.add(head);
		return;
	}

	private boolean isHeadPackFinish = false;    //背包头像是否下载完毕
	private int headPackCount = 0;            //背包头像总数
	private boolean isRequestHeadPack = false;
	/**
	 * 背包头像是否下载完毕
	 * @return
	 */
	public boolean isGetHeadPackFinish(){     
		return isHeadPackFinish;
	}

	/**
	 * 请求背包头像列表
	 * @param userID
	 */
	public void requestHeadPackList(int userID) {
		if(isRequestHeadPack){
			return;
		}
		isRequestHeadPack = true;
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeInt(userID);
		tdos.writeShort(HEAD_TYPE);   //表情类商品
		NetSocketPak pointBalance = new NetSocketPak(LS_MDM_PROP, LSUB_CMD_EXTEND_PACK_LIST_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { LS_MDM_PROP, LSUB_CMD_EXTEND_PACK_LIST_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				try {
					TDataInputStream tdis = netSocketPak.getDataInputStream();
					tdis.setFront(false);

					int total = tdis.readShort(); // 商品总个数
					int type = tdis.readShort(); //商品类型
					if(type == HEAD_TYPE){  //头像

						int num = tdis.readShort(); // 当次商品个数
						headPackCount += num;
						for(int i = 0; i < num; i++){
							parseHeadPackInfo(tdis);
						}

					}

					if(headPackCount >= total){
						isHeadPackFinish = true;
						isRequestHeadPack = false;
						if(updateHandler != null){
							updateHandler.obtainMessage(updateHandlerMsgWhat).sendToTarget();  //下载完通知更新ui
						}
						handler.obtainMessage(HANDLER_GETPACK_FINISH).sendToTarget();
					}


				} catch (Exception e) {
					e.printStackTrace();
				}
				// 数据处理完成，终止继续解析
				return true;
			}
		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}


	/**
	 * 清空头像商城数据
	 */
	public void clearHeadMarketInfo(){
		heads.clear();
		headCount = 0;
		curId = 0;
		askUseId = 0;
		isHeadMarketFinish = false;
		isRequestHeadMarket = false;

	}

	/**
	 * 清空头像背包数据
	 */
	public void clearHeadPackInfo(){
		headPackCount = 0;
		isHeadPackFinish = false;
		isRequestHeadPack = false;
	}

	/**
	 * 获得头像列表
	 * @return
	 */
	public List<HeadInfo> getHeadInfoList(){
		return heads;
	}

	/**
	 * 购买成功
	 * @param id
	 */
	public void buyHeadSuccess(int id){
		time = 0;    //重设计时器
		for(HeadInfo head : heads){
			if(head.getId() == id){
				head.markHaved();  //标记已拥有
				break;
			}
		}
		if(updateHandler != null){  //更新ui
			Message msg = updateHandler.obtainMessage(updateHandlerMsgWhat);
			msg.obj = AppConfig.mContext.getString(R.string.head_buy_success);
			msg.sendToTarget();
		}
	}

	/**
	 * 购买失败
	 * @param id
	 * @param failCode 错误码
	 * @param info 失败信息
	 */
	public void buyHeadFail(int id, int failCode, String info){
		time = 0;   //重设计时器
		if(failCode == 35){ //余额不足
			for(HeadInfo head : heads){
				if(head.getId() == id){
					short currencyType = head.getCurrencyType();
					if(currencyType == 3 || currencyType == 4){     //金币或乐豆购买的
						if(mAct != null){
							//PayUtils.showBuyDialog(mAct,AppConfig.propId,"");
						}
						return;
					}
					break;
				}
			}
		}
		if(updateHandler != null){  //更新ui
			Message msg = updateHandler.obtainMessage(updateHandlerMsgWhat);
			//			msg.obj = AppConfig.mContext.getString(R.string.head_buy_fail);
			msg.obj = info;
			msg.sendToTarget();
		}
	}

	/**
	 * 请求使用id
	 * @param id
	 */
	public void requestUseHead(int id){
		//点击至少间隔2秒
		if(needWait()){
			if(mAct != null){
				Toast.makeText(mAct, mAct.getText(R.string.head_please_wait), Toast.LENGTH_SHORT).show();
			}
			return;
		}
		time = System.currentTimeMillis();    //请求时间
		if(id == curId){   //是当前使用id
			return;
		}

		askUseId = id;
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeInt(HallDataManager.getInstance().getUserMe().userID);
		tdos.writeInt(AppConfig.gameId);
		tdos.writeInt(id);
		NetSocketPak pointBalance = new NetSocketPak(LS_MDM_PROP, LSUB_CMD_MODIFY_USER_HEAD_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { LS_MDM_PROP, LSUB_CMD_MODIFY_USER_HEAD_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				// 解析接受到的网络数据
				try {
					TDataInputStream tdis = netSocketPak.getDataInputStream();
					tdis.setFront(false);
					int rlt = tdis.readShort();    //0表示成功，其他表示失败
					String msg = tdis.readUTFShort();
					if(rlt == 0){
						curId = askUseId;
						HallDataManager.getInstance().setUserHead(curId);
					}

					if(updateHandler != null){
						Message message = updateHandler.obtainMessage(updateHandlerMsgWhat);
						message.obj = msg;
						updateHandler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 数据处理完成，终止继续解析
				time = 0;     //重设计时器
				return true;
			}
		};

		nPListener.setOnlyRun(false);
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(pointBalance);
		// 清理协议对象
		pointBalance.free();
	}

	private static long time = 0;
	/**
	 * 是否需要等待，购买和使用头像时，限制频繁请求
	 * @return
	 */
	private boolean needWait(){
		long newtime = System.currentTimeMillis();
		if(newtime - time < 2000){
			return true;
		}
		return false;
	}

	/**
	 * 请求购买头像
	 * @param item
	 */
	public void requestBuyHead(HeadInfo item){
		//点击至少间隔2秒
		if(needWait()){
			if(mAct != null){
				Toast.makeText(mAct, mAct.getText(R.string.head_please_wait), Toast.LENGTH_SHORT).show();
			}
			return;
		}
		time = System.currentTimeMillis();      //请求时间
		if(mAct != null){
		}
	}
}

