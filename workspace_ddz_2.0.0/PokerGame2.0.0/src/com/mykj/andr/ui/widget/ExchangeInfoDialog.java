package com.mykj.andr.ui.widget;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.model.ExchangeItem;
import com.mykj.andr.model.MaterialItem;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.provider.NewCardZoneProvider;
import com.mykj.andr.ui.adapter.ExchangeGoodsAdapter;
import com.mykj.andr.ui.widget.Interface.DialogCallBack;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExchangeInfoDialog extends Dialog implements View.OnClickListener{

	public static final String TAG = "ExchangeInfoDialog";
	
	/* 对话框的公共部分*/
	private TextView tvTitle;

	/* 收货人信息 */
	private LinearLayout llShowAddInfo;
	private EditText etConsigneeAddress;		// 收货人地址信息
	private EditText etConsigneeName;			// 收货人名字
	private EditText etConsigneeCellphone;		// 收货人手机号码
	
	/* 话费券兑换话费，获取手机号码  */
	private LinearLayout llShowPhoneInfo;
	private EditText etMobilePhone;				// 手机号码
	private EditText etMobilePhoneRepeat;		// 手机号码确认
	
	/* 兑换成功显示内容 */
	private LinearLayout llShowExchangeSuccesss;
	private TextView tvExchangeSuccessTips;
	
	/* 元宝不足显示内容 */
	private LinearLayout llShowYuanBaoLack;
	private TextView tvYuanBaoLackTips;
	
	/* 正在兑换中 */
	private LinearLayout llShowExhanging;
//	private TextView tvExchangingtips;
	
	/* 选择要兑换物品的数量 */
	private LinearLayout llShowChooseExCount;
	private TextView tvExGoodsTips;
//	private TextView tvChooseGoodsContent;
	private EditText etChooseGoodsContent;
	
	private ExchangeItem mExchangeItem;		// 兑换的目标物品
	private MaterialItem mMaterialItem;		// 兑换的原材料
	private Activity mAct;
	private Resources mResource;
	private int mShowType = -1;			    // -1  表示位置类型
//	private UserInfo user;
	private int mUserCount;
	private int mMaxExCount; 					// 最大可兑换的次数
	
	private String mTips;
	
	private DialogCallBack mCallBack;
	/* 请求兑换物品协议 */
	public static final short CMD_EXCHANGE_REAL_OBJECT_REQ = 865;	// 请求兑换奖品
	public static final short CMD_EXCHANGE_REAL_OBJECT_RESP = 866;	// 请求兑换奖品返回		
	public static final int HANDLER_EXCHANGE_ITEM_SUCCESS = 8522;	// 处理兑换物品	
	public static final int HANDLER_EXCHANAGE_ITEM_FAILED = 8533;	// 处理兑换物品失败 
	
	public ExchangeInfoDialog(Context context, ExchangeItem exchangeItem, int showType){
		super(context, R.style.dialog);
		mAct = (Activity)context;
		mExchangeItem = exchangeItem;	
		mMaterialItem = mExchangeItem.materialItemList.get(0);
		mShowType = showType;
		mResource = mAct.getResources();
	}
	
	public ExchangeInfoDialog(Context context, ExchangeItem exchangeItem, int showType, String tips){
		this(context, exchangeItem, showType);
		mTips = tips;
	}	
	
	public ExchangeInfoDialog(Context context, ExchangeItem exchangeItem, int showType, int userCount){
		this(context, exchangeItem, showType);
		mUserCount = userCount;
	}		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		user = HallDataManager.getInstance().getUserMe();
		
		// 公共显示内容
		setContentView(R.layout.exchange_info_dialog);
		((Button)findViewById(R.id.btn_closed)).setOnClickListener(this);
		
		tvTitle = (TextView)findViewById(R.id.tv_title);		
//		if(mShowType == ExchangeItemsAdapter.SHOW_ADDRESS_DIALOG 				
//				|| mShowType == ExchangeItemsAdapter.SHOW_PHONE_DIALOG){
//			tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_confirm));
//		}else if(mShowType == ExchangeItemsAdapter.SHOW_SUCCESS_DIALOG){
//			tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_success));
//		}else if(mShowType == ExchangeItemsAdapter.SHOW_YUAN_BAO_LACK_DIALOG){
//			
//		}
					
		// 收货人信息
		llShowAddInfo = (LinearLayout)findViewById(R.id.show_address_info);
		etConsigneeAddress = (EditText)findViewById(R.id.consignee_address);
		etConsigneeName = (EditText)findViewById(R.id.consignee_name);
		etConsigneeCellphone = (EditText)findViewById(R.id.consignee_cellphone);
		((Button)findViewById(R.id.exchange_info_confirm)).setOnClickListener(this);
		
		// 话费券兑换话费，获取手机号码
		llShowPhoneInfo = (LinearLayout)findViewById(R.id.show_phone_info);
		etMobilePhone = (EditText)findViewById(R.id.mobile_phone);
		etMobilePhoneRepeat = (EditText)findViewById(R.id.mobile_phone_confirm);
		((Button)findViewById(R.id.exchange_phone_confirm)).setOnClickListener(this);
		
		// 兑换成功显示内容
		llShowExchangeSuccesss = (LinearLayout)findViewById(R.id.show_exchange_success);
		tvExchangeSuccessTips = (TextView)findViewById(R.id.tv_exchange_success_info);
		((Button)findViewById(R.id.exchange_success_confirm)).setOnClickListener(this);
		
		// 元宝不足显示内容
		llShowYuanBaoLack = (LinearLayout)findViewById(R.id.show_yuan_bao_lack);
		tvYuanBaoLackTips = (TextView)findViewById(R.id.tv_exchange_yuan_bao_lack);
		((Button)findViewById(R.id.exchange_obtain_yuanbao)).setOnClickListener(this);
		
		// 正在兑换中
		llShowExhanging = (LinearLayout)findViewById(R.id.show_exchanging);
//		tvExchangingtips = (TextView)findViewById(R.id.tv_exchanging_tips);
		
		// 选择要兑换物品的数量
		llShowChooseExCount = (LinearLayout)findViewById(R.id.show_choose_goods_count);
		tvExGoodsTips = (TextView)findViewById(R.id.ex_goods_prop_tips);
//		tvChooseGoodsContent = (TextView)findViewById(R.id.ex_choose_goods_content);
		etChooseGoodsContent = (EditText)findViewById(R.id.ex_choose_goods_content);
		
		((Button)findViewById(R.id.ex_goods_add)).setOnClickListener(this);
		((Button)findViewById(R.id.ex_goods_sub)).setOnClickListener(this);
		((Button)findViewById(R.id.exchange_confirm)).setOnClickListener(this);
		
		showContent();
	}
		
	/*
	 * 根据不同的showType显示不同内容
	 */
	private void showContent(){
		
		if(mShowType == ExchangeGoodsAdapter.SHOW_NO_DIALOG){
			
			tvTitle.setText(mResource.getString(R.string.exchanging_title));
			tvYuanBaoLackTips.setText(mResource.getText(R.string.exchanging_content));
			llShowAddInfo.setVisibility(View.GONE);
			llShowPhoneInfo.setVisibility(View.GONE);
			llShowExchangeSuccesss.setVisibility(View.GONE);
			llShowYuanBaoLack.setVisibility(View.VISIBLE);	
			llShowExhanging.setVisibility(View.GONE);
			llShowChooseExCount.setVisibility(View.GONE);
			
		}else if(mShowType == ExchangeGoodsAdapter.SHOW_ADDRESS_DIALOG){
			
			tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_confirm));
			llShowAddInfo.setVisibility(View.VISIBLE);
			llShowPhoneInfo.setVisibility(View.GONE);
			llShowExchangeSuccesss.setVisibility(View.GONE);
			llShowYuanBaoLack.setVisibility(View.GONE);
			llShowExhanging.setVisibility(View.GONE);
			llShowChooseExCount.setVisibility(View.GONE);
			
		}else if(mShowType == ExchangeGoodsAdapter.SHOW_PHONE_DIALOG){
			
			tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_confirm));
			llShowAddInfo.setVisibility(View.GONE);
			llShowPhoneInfo.setVisibility(View.VISIBLE);
			llShowExchangeSuccesss.setVisibility(View.GONE);
			llShowYuanBaoLack.setVisibility(View.GONE);	
			llShowExhanging.setVisibility(View.GONE);
			llShowChooseExCount.setVisibility(View.GONE);
			
		}else if(mShowType == ExchangeGoodsAdapter.SHOW_SUCCESS_DIALOG
				|| mShowType == ExchangeGoodsAdapter.SHOW_FAILED_DIALOG){
			
			if(mShowType == ExchangeGoodsAdapter.SHOW_SUCCESS_DIALOG){
				tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_success));
			}else{
				tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_failed));
			}
			tvExchangeSuccessTips.setText(mTips);
			llShowAddInfo.setVisibility(View.GONE);
			llShowPhoneInfo.setVisibility(View.GONE);
			llShowExchangeSuccesss.setVisibility(View.VISIBLE);
			llShowYuanBaoLack.setVisibility(View.GONE);		
			llShowExhanging.setVisibility(View.GONE);
			llShowChooseExCount.setVisibility(View.GONE);
		
		}else if(mShowType == ExchangeGoodsAdapter.SHOW_LACK_DIALOG){
		
			tvTitle.setText(mResource.getString(R.string.ex_lack_tips));
			// 单个兑换条件提示
			int lackCount = mUserCount - mMaterialItem.getNumber();
			String materialName = mMaterialItem.getMaterialName();
			if(materialName.contains("|")){
				
				String[] names = materialName.split("\\|");		// 去除materialName 中 "|"和 "|"前面的数字  
				String newName = Math.abs(lackCount) + names[1];
				tvYuanBaoLackTips.setText(mResource.getText(R.string.ex_lack_content1).toString() + newName 
						+ mResource.getText(R.string.ex_lack_content2).toString());				
			}else{
				tvYuanBaoLackTips.setText(mResource.getText(R.string.ex_lack_content1).toString() + materialName 
						+ mResource.getText(R.string.ex_lack_content2).toString());						
			}

			// 两个兑换条件提示			
			llShowAddInfo.setVisibility(View.GONE);
			llShowPhoneInfo.setVisibility(View.GONE);
			llShowExchangeSuccesss.setVisibility(View.GONE);
			llShowYuanBaoLack.setVisibility(View.VISIBLE);
			llShowExhanging.setVisibility(View.GONE);
			llShowChooseExCount.setVisibility(View.GONE);
			
		}else if(mShowType == ExchangeGoodsAdapter.SHOW_EXCHAGNING_DIALOG){
			
			tvTitle.setText(mResource.getString(R.string.exchanging_title));
			llShowExhanging.setVisibility(View.VISIBLE);
			llShowAddInfo.setVisibility(View.GONE);
			llShowPhoneInfo.setVisibility(View.GONE);
			llShowExchangeSuccesss.setVisibility(View.GONE);
			llShowYuanBaoLack.setVisibility(View.GONE);	
			llShowChooseExCount.setVisibility(View.GONE);
			
		}else if(mShowType == ExchangeGoodsAdapter.SHOW_CHOOSE_EX_COUNT){

			tvTitle.setText(mResource.getString(R.string.exchange_dialog_tip_confirm));
			// 能兑换的次数 = 用户拥有的总数量/每次兑换需要消耗的数
			mMaxExCount = mUserCount / mMaterialItem.getNumber();
			tvExGoodsTips.setText(mResource.getString(R.string.ex_choose_prop_tip_1) 
						+ mMaterialItem.getMaterialName() + mUserCount
						+ mResource.getString(R.string.ex_choose_prop_tip_2)
						+ mMaxExCount + mResource.getString(R.string.ex_choose_prop_tip_3));
			updateView(mMaxExCount);	// 默认显示最大值
			
			llShowExhanging.setVisibility(View.GONE);
			llShowAddInfo.setVisibility(View.GONE);
			llShowPhoneInfo.setVisibility(View.GONE);
			llShowExchangeSuccesss.setVisibility(View.GONE);
			llShowYuanBaoLack.setVisibility(View.GONE);	
			llShowChooseExCount.setVisibility(View.VISIBLE);
			
		}
	}
	
	private void updateView(int count){
//		tvChooseGoodsContent.setText(count + "");
		etChooseGoodsContent.setText(count + "");
	}
	
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		switch(v.getId()){
		case R.id.btn_closed:
			dismiss();
			break;
		case R.id.exchange_info_confirm:
			String address = etConsigneeAddress.getText().toString().trim();
			String name = etConsigneeName.getText().toString().trim();
			String cellphone = etConsigneeCellphone.getText().toString().trim();
			boolean isPhone = isCellPhone(cellphone);
			if( isPhone && (!address.equals("")) && (!name.equals(""))){
				try {
					reqExchangeItem(mExchangeItem.index, name, address, cellphone, 1);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					Log.d(TAG, "UnsupportedEncodingException");
				}
				// 提示正在处理中
			}else{
				// 亲，手机号码输错了 地址不能为空，名字不能为空
				if(address.equals("")){
//					Toast.makeText(mAct, "地址不能为空", Toast.LENGTH_SHORT).show();
					Util.displayCenterToast(tvYuanBaoLackTips, "地址不能为空");
				}else if(name.equals("")){
//					Toast.makeText(mAct, "姓名不能为空", Toast.LENGTH_SHORT).show();
					Util.displayCenterToast(tvYuanBaoLackTips, "姓名不能为空");
				}else if(!isPhone){
//					Toast.makeText(mAct, "手机号码错误", Toast.LENGTH_SHORT).show();
					Util.displayCenterToast(tvYuanBaoLackTips, "手机号码错误");
				}
			}
			break; 	
		case R.id.exchange_phone_confirm:
			String phoneNumber = etMobilePhone.getText().toString().trim();
			String phoneNumberRepeat = etMobilePhoneRepeat.getText().toString().trim();
			if(isCellPhone(phoneNumber) && isCellPhone(phoneNumberRepeat)
					&& phoneNumber.equals(phoneNumberRepeat)){
				// 输入正确
				try {
					reqExchangeItem(mExchangeItem.index, "", "", phoneNumber, 1);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					Log.d(TAG, "UnsupportedEncodingException");
				}
			}else{
//				Toast.makeText(mAct, "手机号码错误", Toast.LENGTH_SHORT).show();
				Util.displayCenterToast(tvYuanBaoLackTips, "手机号码错误");
			}
			break;
		case R.id.exchange_success_confirm:			
			dismiss();
			break;
		case R.id.exchange_confirm:
			String strCount1 = etChooseGoodsContent.getText().toString();
			if(UtilHelper.isNumeric(strCount1)){
				int count = Integer.parseInt(strCount1);
				if(count >= 0 && count <= mMaxExCount){
					try {
						reqExchangeItem(mExchangeItem.index, "", "", "", count);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						Log.d(TAG, "UnsupportedEncodingException");
					}					
				}else{
//					Toast.makeText(mAct, "超出范围", Toast.LENGTH_SHORT).show();	
					Util.displayCenterToast(tvYuanBaoLackTips, "超出范围");
				}
			}else{
//				Toast.makeText(mAct, "输入有误", Toast.LENGTH_SHORT).show();	
				Util.displayCenterToast(tvYuanBaoLackTips, "输入有误");
			}			
			break;
		case R.id.exchange_obtain_yuanbao:
			// 去获取			
			int getWay = mMaterialItem.getValue();
			if(getWay == 1){
				// 跳转到比赛场房间列表
                int index = NewCardZoneProvider.getInstance()
                        .getMatchIndex();
                if (index >= 0) {
                    mAct.finish();
                    FiexedViewHelper.getInstance().jumpToMatchList(index);
                }				
			}else if(getWay == 2){
				// 速配，进入游戏房间
				mAct.finish();	
				FiexedViewHelper.getInstance().sHandler.sendEmptyMessage(FiexedViewHelper.HANDLER_CHARGE_SUCCESS);;				
			}else{
				// 不跳转
			}
			dismiss();
			break;
		case R.id.ex_goods_add:
//			if(mChooseCount < mMaxExCount){
//				mChooseCount++;		
//			}else{
//				mChooseCount = mMaxExCount;
//			}
//			updateView(mChooseCount);	
			String strCount = etChooseGoodsContent.getText().toString();
			if(UtilHelper.isNumeric(strCount)){
				int count = Integer.parseInt(strCount);
				if(count >= 0 && count < mMaxExCount){
					count++;
					updateView(count);
				}else{
//					Toast.makeText(mAct, "超出范围", Toast.LENGTH_SHORT).show();
					Util.displayCenterToast(tvYuanBaoLackTips, "超出范围");
				}
			}else{
//				Toast.makeText(mAct, "输入有误", Toast.LENGTH_SHORT).show();
				Util.displayCenterToast(tvYuanBaoLackTips, "输入有误");
			}
			break;
		case R.id.ex_goods_sub:
//			if(mChooseCount > 0){
//				mChooseCount--;
//			}else{
//				mChooseCount = 0;
//			}
//			updateView(mChooseCount);
			String strCount2 = etChooseGoodsContent.getText().toString();
			if(UtilHelper.isNumeric(strCount2)){
				int count = Integer.parseInt(strCount2);
				if(count > 0 && count <= mMaxExCount){
					count--;
					updateView(count);
				}else{
//					Toast.makeText(mAct, "超出范围", Toast.LENGTH_SHORT).show();
					Util.displayCenterToast(tvYuanBaoLackTips, "超出范围");
				}
			}else{
//				Toast.makeText(mAct, "输入有误", Toast.LENGTH_SHORT).show();	
				Util.displayCenterToast(tvYuanBaoLackTips, "输入有误");
			}
			break;
		default:
			break;
		}
	}
	
	/*
	 * 校验是否是手机号码
	 */
	public boolean isCellPhone(String num){
		 Pattern p = null;  
	        Matcher m = null;  
	        boolean b = false;   
	        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); 
	        m = p.matcher(num);  
	        b = m.matches();   
	        return b;  
	}
	
	public void setDiglogCallBack(DialogCallBack callBack){
		mCallBack = (DialogCallBack)callBack;
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case HANDLER_EXCHANGE_ITEM_SUCCESS:
				//tips = (String)msg.obj;
				//Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
				new ExchangeInfoDialog(mAct, mExchangeItem, 
						 ExchangeGoodsAdapter.SHOW_SUCCESS_DIALOG, ((String)msg.obj)).show();
				// 1 元宝或者话费券要做更新
				// 2 物品列表也要做更细
				// 3背包列表也要更新
				if(mCallBack != null)
					mCallBack.dialogCallBack();
				dismiss();
				break;
			case HANDLER_EXCHANAGE_ITEM_FAILED:
				new ExchangeInfoDialog(mAct, mExchangeItem, 
						 ExchangeGoodsAdapter.SHOW_FAILED_DIALOG, ((String)msg.obj)).show();
				dismiss();
				// 给个错误失败的提示
				break;	
			default:
				break;
			}
		}
	};
	/**
	 * 奖品兑换请
	 * @throws UnsupportedEncodingException 
	 */
	public void reqExchangeItem(short index, String name, String address, String phoneNumber, int count) 
			throws UnsupportedEncodingException {
		// 创建发送的数据包
		TDataOutputStream tdos = new TDataOutputStream(false);
		tdos.writeShort(index);
		tdos.writeByte((byte)count);		// 领取数量目前固定为1
		tdos.writeByte(name.getBytes("utf-8").length);
		tdos.writeUTF(name);
		tdos.writeShort(address.getBytes("utf-8").length);
		tdos.writeUTF(address);
		tdos.writeByte(phoneNumber.getBytes("utf-8").length);
		tdos.writeUTF(phoneNumber);
		
		NetSocketPak exchangeData = new NetSocketPak(CardZoneProtocolListener.MDM_PROP,
				CMD_EXCHANGE_REAL_OBJECT_REQ, tdos);
		// 定义接受数据的协议
		short[][] parseProtocol = { { CardZoneProtocolListener.MDM_PROP,
			CMD_EXCHANGE_REAL_OBJECT_RESP } };
		// 创建协议解析器
		NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
			public boolean doReceive(NetSocketPak netSocketPak) {
				TDataInputStream tdis = netSocketPak.getDataInputStream();
				tdis.setFront(false);

				int result = tdis.readByte(); // 兑换结果，0=成功 !0=失败
				int len = tdis.readByte(); // 兑换结果信息长度
				String content = tdis.readUTF(len); // 兑换结果信息
				Message msg = handler.obtainMessage(HANDLER_EXCHANAGE_ITEM_FAILED);
				if(result == 0){
					msg.what = HANDLER_EXCHANGE_ITEM_SUCCESS;
					msg.obj = content;
					handler.sendMessage(msg);
				}else{
					msg.obj = content;
					handler.sendMessage(msg);
				}
					
				// 数据处理完成，终止继续解析
				return true;
			}
		};
		// 注册协议解析器到网络数据分发器中
		NetSocketManager.getInstance().addPrivateListener(nPListener);
		// 发送协议
		NetSocketManager.getInstance().sendData(exchangeData);
		// 清理协议对象
		exchangeData.free();
	}
}
