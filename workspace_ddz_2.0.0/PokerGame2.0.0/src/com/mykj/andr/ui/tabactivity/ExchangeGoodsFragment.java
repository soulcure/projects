package com.mykj.andr.ui.tabactivity;

import java.util.ArrayList;
import java.util.List;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.BackPackItem;
import com.mykj.andr.model.ExchangeItem;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.provider.BackPackItemProvider;
import com.mykj.andr.provider.ExchangeItemProvider;
import com.mykj.andr.ui.MyGoodsFragment;
import com.mykj.andr.ui.adapter.ExchangeGoodsAdapter;
import com.mykj.andr.ui.tabactivity.tabinterface.OnArticleTabChangeCount;
import com.mykj.andr.ui.widget.CardZoneProtocolListener;
import com.mykj.andr.ui.widget.Interface.DialogCallBack;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Log;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExchangeGoodsFragment extends Fragment implements DialogCallBack {
	
	public static final String TAG = "ExchangeGoodsFragment";
	
	private ExchangeTabActivity mAct;
	private Resources mResources;
	
	private GridView gridExchangeItems;
	private ExchangeGoodsAdapter mExchangeItemAdapter;
	private List<ExchangeItem> dataList;
	private LinearLayout busy;
	private TextView tvNoData;
	
	private int curExchangeItemNum = 0;	
	/* 兑换列表请求协议*/
	private static final short MSUB_CMD_EXCHANGE_RULE_REQ = 851; // 奖品兑换规则请求
	private static final short MSUB_CMD_EXCHANGE_RULE_RESP = 852; // 奖品兑换规则应答		  
	public static final int HANDLER_EXCHANGE_DATA_SUCCESS = 8520;  // 兑换规则列表成功发送消息到Handler	 
	public static final int HANDLER_EXCHANGE_DATA_SUCCESS_NODATA = 8521; // 兑换规则列表成功发送消息到Handler
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAct = (ExchangeTabActivity)activity;
		mResources = mAct.getResources();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.exchange_goods_frag, null);
		gridExchangeItems = (GridView)rootView.findViewById(R.id.exchange_items_grid);
		busy = (LinearLayout)rootView.findViewById(R.id.linear_busy);
		tvNoData = (TextView)rootView.findViewById(R.id.no_data);
		mExchangeItemAdapter = new ExchangeGoodsAdapter(mAct, this, null);
        try{
            OnArticleTabChangeCount lister = mAct;
            lister.refreshFragment(this);
        }catch(ClassCastException e){
            throw new ClassCastException( "must implements OnSetImageViewListener!");
        }

		reqExchangeItemList();		
		FiexedViewHelper.getInstance().requestUserBean();

		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void dialogCallBack() {
		reqExchangeItemList();
		UserInfo userInfo = HallDataManager.getInstance().getUserMe();
		CardZoneProtocolListener.getInstance(mAct).requestBackPackList(userInfo.userID, handler);
	}
			
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_EXCHANGE_DATA_SUCCESS:
				Log.d(TAG, "HANDLER_EXCHANGE_DATA_SUCCESS");				
				busy.setVisibility(View.GONE);
				gridExchangeItems.setVisibility(View.VISIBLE);
				tvNoData.setVisibility(View.GONE);
				
				dataList = ExchangeItemProvider.getInstance()
						.getExchangeItemList();
				mExchangeItemAdapter.setList(dataList);
				gridExchangeItems.setAdapter(mExchangeItemAdapter);
				mExchangeItemAdapter.notifyDataSetChanged();
				break;
			case HANDLER_EXCHANGE_DATA_SUCCESS_NODATA:
				Log.d(TAG, "HANDLER_EXCHANGE_DATA_SUCCESS_NODATA");
				busy.setVisibility(View.GONE);
				gridExchangeItems.setVisibility(View.GONE);
				tvNoData.setVisibility(View.VISIBLE);
				
				mExchangeItemAdapter.setList(new ArrayList<ExchangeItem>());
				gridExchangeItems.setAdapter(mExchangeItemAdapter);
				mExchangeItemAdapter.notifyDataSetChanged();
				break;
			case CardZoneProtocolListener.HANDLER_PACK_QUERY_SUCCESS:
			case CardZoneProtocolListener.HANDLER_PACK_QUERY_SUCCESS_NODATA:
				Intent intent = new Intent(MyGoodsFragment.ACTION_BACKPACK_REFRESH);
				mAct.sendBroadcast(intent);
				break;
//			case HANDLER_REWARD_SUCCESS: // 领奖成功
//				if (pd != null && pd.isShowing()) {
//					pd.cancel();
//				}
//				UtilHelper.showCustomDialog(getParent(), msg.obj.toString());
//				UserInfo userInfo = HallDataManager.getInstance().getUserMe();
//				CardZoneProtocolListener.getInstance(AcceptRewardActivity.this)
//						.requestBackPackList(userInfo.userID, handler);
//				break;
//			case HANDLER_REWARD_FAILED: // 领奖失败
//				if (pd != null && pd.isShowing()) {
//					pd.cancel();
//				}
//				UtilHelper.showCustomDialog(getParent(), msg.obj.toString());
//				break;
//			case CardZoneProtocolListener.HANDLER_PACK_QUERY_SUCCESS: // 获取背包列表数据成功
//				CardZoneProtocolListener.getInstance(AcceptRewardActivity.this)
//						.obtainRewardData(handler);
//				intent.putExtra("isSuccess", true);
//				intent.putExtra("resultDesc", "");
//				sendBroadcast(intent);
//				break;
//			case CardZoneProtocolListener.HANDLER_PACK_QUERY_SUCCESS_NODATA:
//				CardZoneProtocolListener.getInstance(AcceptRewardActivity.this)
//						.obtainRewardData(handler);
//				intent.putExtra("isSuccess", true);
//				intent.putExtra("resultDesc", "");
//				sendBroadcast(intent);
//				break;
			default:
				break;
			}
		};
	};

	/**
	 * 请求兑换物品列表
	 */
	public void reqExchangeItemList() {
		Log.d(TAG, "reqExchangeItemList");
		ExchangeItemProvider.getInstance().init();
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream(false);

		NetSocketPak rewardData = new NetSocketPak(CardZoneProtocolListener.MDM_PROP,
				MSUB_CMD_EXCHANGE_RULE_REQ, tdos);

		// 定义接受数据的协议
		short[][] parseProtocol = { { CardZoneProtocolListener.MDM_PROP,
				MSUB_CMD_EXCHANGE_RULE_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				Log.d(TAG, "reqExchangeItemList_back");
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);

				int total = tdis.readShort(); // 奖品总个数
				int num = tdis.readShort(); // 当次奖品个数
				ExchangeItem[] exchangeItems;
				if (num <= 0) {
					if (handler != null) {
						Message msg = handler
								.obtainMessage(HANDLER_EXCHANGE_DATA_SUCCESS_NODATA);
						handler.sendMessage(msg);
					}
				} else {
					exchangeItems = new ExchangeItem[num];
					// 累计接受到数据到数组中
					for (int i = 0; i < num; i++) {
						exchangeItems[i] = new ExchangeItem(tdis);
					}
					ExchangeItemProvider.getInstance().setExchangeItem(
							exchangeItems);
					curExchangeItemNum += num; // 积累保存到全局变量，记录当前返回累计数目

					if (curExchangeItemNum == total) {
						if (handler != null) {
							Message msg = handler
									.obtainMessage(HANDLER_EXCHANGE_DATA_SUCCESS);
							handler.sendMessage(msg);
						}
						ExchangeItemProvider.getInstance().setFinish(true);
						curExchangeItemNum = 0;
					}
				}

				// 数据处理完成，终止继续解析
				return true;
			}
		};
		
		nPListener.setOnlyRun(false);
		
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(rewardData);
		// 清理协议对象
		rewardData.free();
	}
}
