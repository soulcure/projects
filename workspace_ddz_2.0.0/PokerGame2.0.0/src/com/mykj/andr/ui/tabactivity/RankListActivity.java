package com.mykj.andr.ui.tabactivity;

import com.mykj.andr.ui.tabactivity.tabinterface.OnArticleTabSelectedListener;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.Log;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class RankListActivity extends FragmentActivity implements
        OnClickListener,OnArticleTabSelectedListener {

    public static final String TAG = "RankListActivity";
    public static String[] tabTitle = {"昨日赢豆榜", "上周土豪榜"};
    private Context mContext;
    
    private int[] rankdrawable= {R.drawable.num_0, R.drawable.num_1, R.drawable.num_2,
    							 R.drawable.num_3, R.drawable.num_4, R.drawable.num_5,
    							 R.drawable.num_6, R.drawable.num_7, R.drawable.num_8,
    							 R.drawable.num_9};

    private Resources mResources;
    public static final int TAB_TUHAO = 1;
    public static final int TAB_WIN_BEAN = 0;

    private TextView tvUpdateTip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Create RankListActivity");

        mContext = this;
        mResources = getResources();
        setContentView(R.layout.rank_list);
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
        tvUpdateTip = (TextView)findViewById(R.id.update_tip);
        tvUpdateTip.setText(R.string.update_per_day_tip);
        findViewById(R.id.btn_back).setOnClickListener(this);
        
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
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
            	 return BitmapFactory.decodeResource(mResources, R.drawable.tab_divider);
            }
            
            @Override
            public int getIndicatorCount(int position) {
                return 10;
            }
        };
        slidingTabLayout.setCustomTabView(R.layout.title_view, R.id.tvTitle, -1);
        slidingTabLayout.setCustomTabColorizer(colorizer);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                	tvUpdateTip.setText(R.string.update_per_day_tip);
                	AnalyticsUtils.onClickEvent(RankListActivity.this, "070");
                }else {
                	tvUpdateTip.setText(R.string.update_per_week_tip);
                	AnalyticsUtils.onClickEvent(RankListActivity.this, "071");
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
    	FiexedViewHelper.getInstance().playKeyClick();
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    /*
     * @params rank_single 个位数字
     *
     * @params rank_ten    十位数字
     *
     * @params rankId
     */
    @Override
    public void setImageViewDrawable(ImageView rank_single, ImageView rank_ten,
                                     int rankId) {

        if (rankId <= 0) {
            return;
        }

        if (rankId >= 1 && rankId <= 3) {
            switch (rankId) {
                case 1:
                	rank_single.setImageDrawable(mResources.getDrawable(R.drawable.first_cup));
                	rank_ten.setImageDrawable(null);
                    break;
                case 2:
                	rank_single.setImageDrawable(mResources.getDrawable(R.drawable.second_cup));
                	rank_ten.setImageDrawable(null);
                    break;
                case 3:
                	rank_single.setImageDrawable(mResources.getDrawable(R.drawable.third_cup));
                	rank_ten.setImageDrawable(null);
                    break;
                default:
                    break;
            }
        } else if (rankId > 3 && rankId < 10) {
            Bitmap bitmap = getBitmap(rankId);
            rank_single.setImageDrawable(new BitmapDrawable(mResources, bitmap));
            rank_ten.setImageDrawable(null);

        } else if (rankId >= 10 && rankId < 100) {
            int singleDigits = rankId % 10;
            Bitmap singleBitmap = getBitmap(singleDigits);
            int tenDigits = rankId / 10;
            Bitmap tenBitmap = getBitmap(tenDigits);
            rank_single.setImageDrawable(new BitmapDrawable(mResources,
                    singleBitmap));
            rank_ten.setImageDrawable(new BitmapDrawable(mResources,
                    tenBitmap));
        }
    }

    public Bitmap getBitmap(int num) {
        Bitmap bitmap = null;
        if(num >= 0 && num < rankdrawable.length)
        	bitmap = BitmapFactory.decodeResource(mResources, rankdrawable[num]);
        return bitmap;
    }


    class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case TAB_TUHAO:
                	
                    ft = new TuHaoFragment();
                    break;
                case TAB_WIN_BEAN:
                	
                    ft = new WinBeanFragment();
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
