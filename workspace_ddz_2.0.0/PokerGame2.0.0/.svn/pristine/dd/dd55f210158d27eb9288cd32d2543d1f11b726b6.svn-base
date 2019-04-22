package com.mykj.andr.ui.tabactivity;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.model.VipPrivilegeInfo;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.ui.SinglePayDialog;
import com.mykj.andr.provider.VipPrivilegeProvider;
import com.mykj.andr.provider.VipPrivilegeProvider.OnReceiverListener;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.UtilHelper;

/**
 * 我的特权界面
 *
 * @author Administrator
 */
public class PrivilegeFragment extends Fragment implements OnReceiverListener, OnClickListener {
	Activity act;
	public final int HANDLER_UPDATE_VIEW = 1;
    private boolean configSet = false;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        act = activity;
//        try {
//            mListener = (OnRankArticleSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnRankArticleSelectedListener");
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.usercenter_privilege_tab, container, false);
        configSet = false;
        initView(rootView);
        reqData();

        return rootView;
    }
    TextView mLabel;   //标签
    ImageView mLevel;   //我的级别，非会员，会员，男子伯侯公
    TextView expTime;  //过期时间，非会员显示您还不是会员
    TableLayout mTable; //数据显示表
    Button btn1,btn2,btn3,btn4,btn5,btn6;  //6个购买按钮
    private void initView(View view) {
    	mLabel = (TextView)view.findViewById(R.id.user_label);
    	mLevel = (ImageView)view.findViewById(R.id.vip_level);
    	expTime = (TextView)view.findViewById(R.id.exp_time);
    	mTable = (TableLayout)view.findViewById(R.id.vip_table_container);
    	btn1 = (Button)view.findViewById(R.id.buy_btn1);
    	btn2 = (Button)view.findViewById(R.id.buy_btn2);
    	btn3 = (Button)view.findViewById(R.id.buy_btn3);
    	btn4 = (Button)view.findViewById(R.id.buy_btn4);
    	btn5 = (Button)view.findViewById(R.id.buy_btn5);
    	btn6 = (Button)view.findViewById(R.id.buy_btn6);
    	btn1.setOnClickListener(this);
    	btn2.setOnClickListener(this);
    	btn3.setOnClickListener(this);
    	btn4.setOnClickListener(this);
    	btn5.setOnClickListener(this);
    	btn6.setOnClickListener(this);
        //从用户信息中获取的数据来更新界面
    }

    private void reqData() {
    	if (VipPrivilegeProvider.getInstance().getVipPrivilegeInfo().vipList.size()==0) {
    		VipPrivilegeProvider.getInstance().reqVipData();//重新请求数据，前面请求太慢，取消请求。
		}
    	
        if (VipPrivilegeProvider.getInstance().isFinish()) {
            updateView();
        }
        VipPrivilegeProvider.getInstance().setOnReceiverListener(this);
    }


    private void updateView() {
    	VipPrivilegeInfo vipPrivilegeInfo = VipPrivilegeProvider.getInstance().getVipPrivilegeInfo();
    	UserInfo user = HallDataManager.getInstance().getUserMe();
    	if (vipPrivilegeInfo.endTime==0) {
			if (vipPrivilegeInfo.vipLvl==0) {
				mLabel.setText("主人，贵族这么多特权，好心动，咱们也加入吧~");
				expTime.setText("");
			}else {
//				mLabel.setText("主人，贵族这么多特权，好心动，咱们也加入吧~");
				mLabel.setText("您的"+vipPrivilegeInfo.getVipName()+"身份已经到期");
				expTime.setText("");
			}
		}else {
			mLabel.setText("您当前身份：");
    		expTime.setText(vipPrivilegeInfo.endTimeStr+"到期");
		}
    	
    	UtilHelper.setVipView(mLevel, user.getVipLevel(), !user.isVip());
    	if(configSet){
    		return;
    	}
    	if(vipPrivilegeInfo.vipList.size() > 0){
    		btn1.setText(vipPrivilegeInfo.vipList.get(0).cost + "元");
    		btn1.setTag(vipPrivilegeInfo.vipList.get(0).propId+"");
    	}
    	if(vipPrivilegeInfo.vipList.size() > 1){
    		btn2.setText(vipPrivilegeInfo.vipList.get(1).cost + "元");
    		btn2.setTag(vipPrivilegeInfo.vipList.get(1).propId+"");
    	}
    	if(vipPrivilegeInfo.vipList.size() > 2){
    		btn3.setText(vipPrivilegeInfo.vipList.get(2).cost + "元");
    		btn3.setTag(vipPrivilegeInfo.vipList.get(2).propId+"");
    	}
    	if(vipPrivilegeInfo.vipList.size() > 3){
    		btn4.setText(vipPrivilegeInfo.vipList.get(3).cost + "元");
    		btn4.setTag(vipPrivilegeInfo.vipList.get(3).propId+"");
    	}
    	if(vipPrivilegeInfo.vipList.size() > 4){
    		btn5.setText(vipPrivilegeInfo.vipList.get(4).cost + "元");
    		btn5.setTag(vipPrivilegeInfo.vipList.get(4).propId+"");
    	}
    	if(vipPrivilegeInfo.vipList.size() > 5){
    		btn6.setText(vipPrivilegeInfo.vipList.get(5).cost + "元");
    		btn6.setTag(vipPrivilegeInfo.vipList.get(5).propId+"");
    	}
    	int singleDouble = 1;
    	for(int i = 0; i < vipPrivilegeInfo.effList.size(); i++){
    		List<String> mList = vipPrivilegeInfo.effList.get(i);
    		if(mList == null){
    			continue;
    		}
    		TableRow row = (TableRow)LayoutInflater.from(act).inflate(R.layout.vip_tabrow, null);
    		if(singleDouble %2 == 0){
    			row.findViewById(R.id.vip_row).setBackgroundResource(R.drawable.vip_item_background);
    		}
    		singleDouble++;
    		TextView descV = (TextView)row.findViewById(R.id.desc);
    		if(mList.size() > 0){
    			descV.setText(mList.get(0));
    		}
    		TextView item1 = (TextView)row.findViewById(R.id.item1);
    		if(mList.size() > 1){
    			setItemText(item1,mList.get(1));
    		}
    		TextView item2 = (TextView)row.findViewById(R.id.item2);
    		if(mList.size() > 2){
    			setItemText(item2,mList.get(2));
    		}
    		TextView item3 = (TextView)row.findViewById(R.id.item3);
    		if(mList.size() > 3){
    			setItemText(item3,mList.get(3));
    		}
    		TextView item4 = (TextView)row.findViewById(R.id.item4);
    		if(mList.size() > 4){
    			setItemText(item4,mList.get(4));
    		}
    		TextView item5 = (TextView)row.findViewById(R.id.item5);
    		if(mList.size() > 5){
    			setItemText(item5,mList.get(5));
    		}
    		TextView item6 = (TextView)row.findViewById(R.id.item6);
    		if(mList.size() > 6){
    			setItemText(item6,mList.get(6));
    		}
    		mTable.addView(row);
    	}
    	configSet = true;
    }

    private void setItemText(TextView v, String text){
    	if(text.equals("√")){
    		v.setTextColor(0xff00ff00);
    		v.getPaint().setFakeBoldText(true);
    	}
    	v.setText(text);
    }
    
    @Override
    public void onReceiver() {
        // TODO Auto-generated method stub
    	hand.sendEmptyMessage(HANDLER_UPDATE_VIEW);
//        updateView(vipPrivilegeInfo);
    }

    @Override
    public void onClick(View v) {
    	FiexedViewHelper.getInstance().playKeyClick();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buy_btn1:
            case R.id.buy_btn2:
            case R.id.buy_btn3:
            case R.id.buy_btn4:
            case R.id.buy_btn5:
            case R.id.buy_btn6:
            {
            	if(v.getTag() != null){
            		try{
            			int propId = Integer.parseInt((String) v.getTag());
            			PayUtils.showBuyDialog(act, propId, FastBuyModel.isFastBuyConfirm,"","");
            		}catch(Exception e){
            			e.printStackTrace();
            		}
            	}
            }
                break;

            default:
                break;
        }
    }


    Handler hand = new Handler(){
    	public void dispatchMessage(android.os.Message msg) {
    		switch(msg.what){
    		case HANDLER_UPDATE_VIEW:
    			updateView();
    			break;
    		}
    	};
    };
    
    public void onDestroy() {
    	VipPrivilegeProvider.getInstance().clearOnReceiverListener(this);
    	super.onDestroy();
    };
}
