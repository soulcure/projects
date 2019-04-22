package com.mykj.andr.ui.tabactivity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.mykj.andr.ui.ServerDialog;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;


/**
 * 客服中心，tab页
 *
 * @author Administrator
 */
public class ServerCenterTabActivity extends FragmentActivity implements OnClickListener {

    private Context mContext;
    private  String[] tabTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext=this;
        tabTitle = getResources().getStringArray(R.array.tab_server);
        setContentView(R.layout.servercenter_tab_activity);
        initView();
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btnOnlineServer).setOnClickListener(this);

        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (position == 1) {
					//点击帮助
					AnalyticsUtils.onClickEvent(ServerCenterTabActivity.this, "092");
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        SlidingTabLayout.TabColorizer colorizer = new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return mContext.getResources().getColor(R.color.tab_bar);
            }

            @Override
            public int getDividerColor(int position) {
//                return mContext.getResources().getColor(R.color.hilight_yellow);
            	return -1;
            }

            @Override
            public Bitmap getDividerBitmap(int position) {
            	return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tab_divider);
            }

            @Override
            public int getIndicatorCount(int position) {
                return 0;
            }
        };
        slidingTabLayout.setCustomTabView(R.layout.title_view, R.id.tvTitle, -1);
        slidingTabLayout.setCustomTabColorizer(colorizer);
        slidingTabLayout.setViewPager(viewPager);

    }


    @Override
    public void onClick(View v) {
    	FiexedViewHelper.getInstance().playKeyClick();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btnOnlineServer:
                ServerDialog.getOnlineServerIntent(this);
                break;
            default:
                break;
        }
    }


    private  class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case 0:
                    ft = new ServerCenterFragment();
                    break;
                case 1:
                    ft = new ServerHelpFragment();
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
