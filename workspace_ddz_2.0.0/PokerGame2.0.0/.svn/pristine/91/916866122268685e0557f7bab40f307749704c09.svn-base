package com.mykj.andr.ui.tabactivity;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mykj.andr.model.ExchangeItem;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.MaterialItem;
import com.mykj.andr.model.RecordsItem;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.ui.adapter.ExchangeRecordsAdapter;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.R.id;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.CenterUrlHelper;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListView.FixedViewInfo;

public class ExchangeRecordsFragment extends Fragment implements OnClickListener {

	public static final String TAG = "ExchangeRecordsFragment";
	
	private Activity mAct;
	private Resources mResources;
	private ListView gvExchangeRecords;
	private List<RecordsItem> mRecordsItems;
	private UserInfo user;
	
	private String exchangeRecordsUrl;
	
	private LinearLayout busy;
	private RelativeLayout recordsContent;
	private LinearLayout recordsNoData;
	private TextView failedTips;
	private Button btnToGame;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAct = activity;
		mResources = mAct.getResources();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.exchange_records, null);
		user = HallDataManager.getInstance().getUserMe();
		
		busy = (LinearLayout)rootView.findViewById(R.id.records_busy);
		recordsContent = (RelativeLayout)rootView.findViewById(id.records_content);
		recordsNoData = (LinearLayout)rootView.findViewById(R.id.records_no_data);
		gvExchangeRecords = (ListView)rootView.findViewById(R.id.lv_exchange_records);
		failedTips = (TextView)rootView.findViewById(R.id.tip_failed);
		btnToGame = (Button)rootView.findViewById(R.id.record_btnToGame);
		btnToGame.setOnClickListener(this);
		
		String sign = CenterUrlHelper.getSign(getUrlParam(),
				CenterUrlHelper.secret);
		exchangeRecordsUrl = getUrl() + sign;		
		new RecordsAsyncTask().execute(exchangeRecordsUrl);
		
		return rootView;
	}
	
	private String getUrlParam() {
		StringBuilder sb = new StringBuilder();
		sb.append("apiname=").append("DdzExcRecord");
		sb.append('&').append("uid=").append(user.userID/*123*/);
		sb.append('&').append("op=").append(121);
		sb.append('&').append("format=").append("xml");
		
		return sb.toString();
	}

	private String getUrl() {
		/*return  AppConfig.RANK_PATH + "?" +getUrlParam();*/
		return  AppConfig.SERVER_ONLINE_PATH + "?" +getUrlParam();
	}
	
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		if(v.getId() == R.id.record_btnToGame){
			FiexedViewHelper.getInstance().sHandler.sendEmptyMessage(FiexedViewHelper.HANDLER_CHARGE_SUCCESS);;
			mAct.finish();
		}
	}
	
	private class RecordsAsyncTask extends AsyncTask<String, Void, Integer>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
//			String sdcard = Util.getSdcardPath();
//			String path = sdcard + "/test.xml";
//			String res=Util.readFromFile(path);
			// 判断文件是否存在
			String url=params[0];
			String res = null;
			try{
				res = Util.getConfigXmlByHttp(url);	
				// 定义工厂
				XmlPullParserFactory f = XmlPullParserFactory.newInstance();
				// 定义解析器
				XmlPullParser p = f.newPullParser();
				// 获取xml输入数据
				//p.setInput(new InputStreamReader(conn.getInputStream()));
				if(Util.isEmptyStr(res)){
					throw new NullPointerException("http访问异常!");
				}
				p.setInput(new StringReader(res));
				RecordsItem itemInfo = null;
				// 解析事件
				int eventType = p.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
						case XmlPullParser.START_DOCUMENT:
							mRecordsItems = new ArrayList<RecordsItem>();
							break;
						case XmlPullParser.START_TAG:
							String tagName = p.getName();
							if(tagName.equals("status")){
								String status = p.nextText();
							}else if(tagName.equals("statusnote")){
								String statusNote = p.nextText();
							}else if (tagName.equals("element")) {
								itemInfo = new RecordsItem();
							}
							
							if(itemInfo != null){
								if(tagName.equals("op_time")){
									itemInfo.setOpTime(p.nextText());;
								}else if(tagName.equals("gifts_id")){
									String giftId = p.nextText();
									if(UtilHelper.isNumeric(giftId)){
										itemInfo.setGiftId(Integer.parseInt(giftId));
									}
								}else if(tagName.equals("gifts_name")){
									itemInfo.setGiftName(p.nextText());
								}else if(tagName.equals("xml")){
									// 从xml中解析出所需数据
									String desc = parseStatusXml(p.nextText(), "s");
									if(desc != null){
										itemInfo.setDesc(desc);										
									}else{
										itemInfo.setDesc("");
									}										
								}
							}
							break;
						case XmlPullParser.END_TAG:							
							if(p.getName().equals("element")){
								mRecordsItems.add(itemInfo);
								itemInfo = null;
							}
							break;
						default:
							break;
					}
				// 用next方法处理下一个事件，否则会造成死循环。
				//eventType = p.nextTag();
				eventType = p.next();
			}

			} catch (IndexOutOfBoundsException e){
				e.printStackTrace();
				return -1;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return -1;
			} catch (UnknownHostException e){
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return -1;
			} catch (NullPointerException e){
				e.printStackTrace();
				return -1;
			} 			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			busy.setVisibility(View.GONE);
			
			if(result == 0){
				// 解析成功
				if(mRecordsItems.size() != 0){
					recordsContent.setVisibility(View.VISIBLE);
					recordsNoData.setVisibility(View.GONE);
					gvExchangeRecords.setAdapter(new ExchangeRecordsAdapter(mAct, mRecordsItems));					
				}else{
					// 提示没有兑换记录
					recordsContent.setVisibility(View.GONE);
					recordsNoData.setVisibility(View.VISIBLE);
				}

			}else{
				// 提示解析失败
				recordsContent.setVisibility(View.GONE);
				recordsNoData.setVisibility(View.GONE);
				failedTips.setVisibility(View.VISIBLE);
			}

		}
	}	
	
	public static String parseStatusXml(String strXml, String tagName) {
		if(Util.isEmptyStr(strXml)){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
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
					String tag = p.getName();
					if (tag.equals(tagName)) {
						sb.append(p.getAttributeValue(null, "m"));	// 消耗的材料名称
						sb.append(p.getAttributeValue(null, "n"));	// 消耗的材料的数量
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
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}	
}
