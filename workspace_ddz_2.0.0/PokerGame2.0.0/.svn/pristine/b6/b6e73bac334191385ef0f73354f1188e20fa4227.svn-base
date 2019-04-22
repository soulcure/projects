package com.mykj.andr.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mykj.andr.model.GoodsItem;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.model.VipPrivilegeInfo;
import com.mykj.andr.pay.PayManager;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.andr.provider.VipPrivilegeProvider;
import com.mykj.andr.provider.VipPrivilegeProvider.OnReceiverListener;
import com.mykj.andr.ui.adapter.PropListAdapter;
import com.mykj.andr.ui.tabactivity.MarketActivity;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

/**
 * 道具列表 界面
 *
 * @author Administrator
 */
public class PropListFragment extends Fragment implements OnReceiverListener {
	private static final String TAG = "PropListFragment";
	
	/** 下载图片成功后更新UI */
	public static final int REFLASH_LISTVIEW = 20000;

	/** 下载图片失败后更新UI */
    public static final int REFLASH_LISTVIEW_FAIL = 20001;
    
    /** 更新View */
    public static final byte HANDLER_UPDATE_VIEW = 1;
    
	private MarketActivity mAct;
	private Resources mResource;
	
	private TextView tvCurVipTips;
	private ImageView ivCurVipLevel;//当前vip等级
    private TextView tvExpiresDay;         //过期时间
    private Button btnBeVip;
    private GridView mGridView;//道具列表
    private PropListAdapter mAdapter;
    private List<GoodsItem> mLists;
    private int userID = 0;
    private String userToken = "";
    private int flag;			// 1 乐豆界面  2 道具界面

    private VipPrivilegeProvider vipProvider;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = (MarketActivity)activity;
        mResource = mAct.getResources();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.prop_list, container, false);
        PayManager.getInstance(mAct);
        initView(rootView);

        return rootView;
    }

    private void initView(View view) {
		Bundle args = getArguments();	
		flag = args.getInt("showdata");		// 动态获取该标识 1 标识乐豆，2标识道具
		
		tvCurVipTips = (TextView) view.findViewById(R.id.cur_vip_tips);
		ivCurVipLevel = (ImageView) view.findViewById(R.id.cur_vip_level);
		UserInfo userInfo = HallDataManager.getInstance().getUserMe();	
       
		tvExpiresDay = (TextView) view.findViewById(R.id.expire_date);
		btnBeVip = (Button)view.findViewById(R.id.to_be_vip);
		
        UtilHelper.setVipView(ivCurVipLevel, userInfo.getVipLevel(), !userInfo.isVip());
        vipProvider = VipPrivilegeProvider.getInstance();
		if(userInfo.isVip()){
			btnBeVip.setText(mResource.getString(R.string.vip_privilege));
			// 当前用户是vip
			if(vipProvider.isFinish()){
				updateView();
			}else{
				// 分别更新自己的会员到期时间
				if(flag == GoodsItemProvider.BEAN_GOODS){
					vipProvider.setOnMarketBeanReceiverListener(this);				
				}else{
					vipProvider.setOnMarketPropReceiverListener(this);
				}
			}	

		}else{
			// 当前用户不是vip
			tvCurVipTips.setText(mResource.getString(R.string.non_vip_tips));
			btnBeVip.setText(mResource.getString(R.string.to_be_vip));
		}

		

		btnBeVip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FiexedViewHelper.getInstance().playKeyClick();
				mAct.viewPager.setCurrentItem(2);	// 跳转到贵族页面			
			}
		});
		mGridView = (GridView)view.findViewById(R.id.gridview);
		
		mAdapter = new PropListAdapter(mAct);
		mLists = GoodsItemProvider.getInstance().getClassifyGoodsItem(flag);
		//mLists = GoodsItemProvider.getInstance().getGoodsList();
		mGridView.setAdapter(mAdapter);
		mAdapter.setList(mLists);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            	FiexedViewHelper.getInstance().playKeyClick();
                GoodsItem item = (GoodsItem) mLists.get(position);
                PayManager.getInstance(mAct).requestBuyProp(item,FastBuyModel.isConfirmon);

            }
        });
    }
    
    @Override
    public void onReceiver() {
    	mMarkHandler.sendEmptyMessage(HANDLER_UPDATE_VIEW); 	
    }
    
    public void updateView(){
    	VipPrivilegeInfo vipInfo = VipPrivilegeProvider.getInstance().getVipPrivilegeInfo(); 	
    	tvExpiresDay.setText(vipInfo.endTimeStr+ mAct.getResources().getString(R.string.market_expire_tips));	// 更新vip过期时间    	
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        AnalyticsUtils.onPageStart(mAct);
        AnalyticsUtils.onResume(mAct);

        UserInfo user = HallDataManager.getInstance().getUserMe();
        String key_tag = user.nickName;
        String tag = Util.getStringSharedPreferences(mAct, key_tag,
                AppConfig.DEFAULT_TAG);
        String[] strs = tag.split("&");
        if (strs != null && strs.length == 3) {

            int back = Integer.parseInt(strs[1]);
            if (0 == back) {
//				findViewById(R.id.btnPacket_newTag).setVisibility(View.VISIBLE);
            } else if (1 == back) {
//				findViewById(R.id.btnPacket_newTag).setVisibility(
//						View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        AnalyticsUtils.onPageEnd(this);
        AnalyticsUtils.onPause(mAct);
    }

    @Override
    public void onDestroy() {
    	if(flag == GoodsItemProvider.BEAN_GOODS){
    		vipProvider.setOnMarketBeanReceiverListener(this);	
    	}else{
    		vipProvider.setOnMarketPropReceiverListener(this);	
    	}
        //MainApplication.sharedApplication().finishActivity(mAct);
        super.onDestroy();
    }

    /**
     * *
     * //	 * 定义一个Handler处理线程发送的消息，并更新主UI线程
     * //
     */
    @SuppressLint("HandlerLeak")
    public Handler mMarkHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case REFLASH_LISTVIEW: // 下载图片完成后更新ListView
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();// 刷新UI
                    break;

                case REFLASH_LISTVIEW_FAIL:
                    Log.e(TAG, "道具图片文件下载失败，错误码为：" + msg.arg1);
                    break;
               case HANDLER_UPDATE_VIEW:
            	   updateView();
            	   break;
                default:
                    break;
            }
        }
    };

}
