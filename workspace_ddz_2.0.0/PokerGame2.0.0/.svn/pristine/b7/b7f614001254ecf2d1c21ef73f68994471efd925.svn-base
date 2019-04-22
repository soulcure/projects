package com.mykj.andr.ui.tabactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.provider.AddGiftProvider;
import com.mykj.andr.pay.provider.PromotionGoodsProvider;
import com.mykj.andr.provider.GoodsItemProvider;
import com.mykj.andr.ui.CustomActivity;
import com.mykj.andr.ui.PropListFragment;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.UtilHelper;

/**
 * 商城tab页
 * 
 * @author Administrator
 */
public class MarketActivity extends FragmentActivity implements OnClickListener {

	private Activity mAct;
	private String[] tabTitle;

	public ViewPager viewPager;
	private TextView tvUserBean;
	private Handler mMarkHandler;
	private TextView tvTelPhone;
	
	public static final int TAB_BEANGOODS = 0;
	public static final int TAB_PROPGOODS = 1;
	public static final int TAB_VIP = 2;
	
	private static final int SHOW_MARKET_PROMOTION_DIALOG = 5445;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mAct = this;
		tabTitle = getResources().getStringArray(R.array.tab_market);
		setContentView(R.layout.market_tab_activity);
		initView();
		mMarkHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case CustomActivity.HANDLER_USER_BEAN: // 获取用户乐豆
					if (tvUserBean != null) {
						UtilHelper.setUserBeanView(tvUserBean, HallDataManager
								.getInstance().getUserMe().getBean());
					}
					break;
				case SHOW_MARKET_PROMOTION_DIALOG:
					PayUtils.showPromotionDialog(mAct,
							AddGiftProvider.getInstance().adviceGoodID, "",
							null,null);
					break;
				default:
					break;

				}
			}
		};
//		if (PromotionGoodsProvider.getInstance().type == 0) {
//			mMarkHandler.sendEmptyMessageDelayed(SHOW_MARKET_PROMOTION_DIALOG,
//					1000);
//		}
		
		int tabId = getIntent().getIntExtra("toPage", -1);
		if(tabId >= 0 && tabId < 3){
			viewPager.setCurrentItem(tabId);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		FiexedViewHelper.getInstance().startActivity(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		FiexedViewHelper.getInstance().stopActivity(this);
		super.onStop();
	}
	
	/**
	 * 初始化tabview
	 */
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		tvUserBean = (TextView) findViewById(R.id.user_bean);
		tvTelPhone = (TextView) findViewById(R.id.telphone);
		tvTelPhone.setOnClickListener(this);

		UtilHelper.setUserBeanView(tvUserBean, HallDataManager.getInstance()
				.getUserMe().getBean());

		findViewById(R.id.btn_back).setOnClickListener(this);

		TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		SlidingTabLayout.TabColorizer colorizer = new SlidingTabLayout.TabColorizer() {

			@Override
			public int getIndicatorCount(int position) {
				return 0;
			}

			@Override
			public int getIndicatorColor(int position) {
				return mAct.getResources().getColor(R.color.tab_bar);
			}

			@Override
			public int getDividerColor(int position) {
//				return mAct.getResources().getColor(R.color.hilight_yellow);
				return -1;
			}

			@Override
			public Bitmap getDividerBitmap(int position) {
				return BitmapFactory.decodeResource(mAct.getResources(), R.drawable.tab_divider);
			}
		};
		slidingTabLayout
				.setCustomTabView(R.layout.title_view, R.id.tvTitle, -1);
		slidingTabLayout.setCustomTabColorizer(colorizer);
		slidingTabLayout.setViewPager(viewPager);
	}

	public Handler getMarkHandler() {
		return mMarkHandler;
	}

	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.telphone:
			// 跳转到拨号界面
			Intent intentPhone = new Intent(Intent.ACTION_DIAL,
					Uri.parse("tel:" + tvTelPhone.getText()));
			// intentPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentPhone);
			break;
		default:
			break;
		}
	}

	private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = null;
			switch (arg0) {
			case 0:
				ft = new PropListFragment(); // 乐豆
				Bundle beanArgs = new Bundle();
				beanArgs.putInt("showdata", GoodsItemProvider.BEAN_GOODS);
				ft.setArguments(beanArgs);
				break;
			case 1:
				ft = new PropListFragment(); // 道具
				Bundle propArgs = new Bundle();
				propArgs.putInt("showdata", GoodsItemProvider.PROP_GOODS);
				ft.setArguments(propArgs);
				break;
			case 2:
				ft = new PrivilegeFragment();
				break;
			default:
				break;
			}
			return ft;
		}

		@Override
		public int getCount() {

			return tabTitle.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitle[position];
		}

	}

}