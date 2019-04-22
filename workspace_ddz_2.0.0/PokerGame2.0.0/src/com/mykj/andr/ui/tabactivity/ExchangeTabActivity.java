package com.mykj.andr.ui.tabactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.pay.PayManager;
import com.mykj.andr.ui.MyGoodsFragment;
import com.mykj.andr.ui.tabactivity.tabinterface.OnArticleTabChangeCount;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.Log;


public class ExchangeTabActivity extends FragmentActivity implements OnClickListener, OnArticleTabChangeCount {
    public static final String TAG = "ExchangeTabActivity";
    public static final String EXCHANGE_BROADCAST = "com.mykj.andr.ui.tabactivity.broadcast";

    public static int[] countTitle = {0, 10, 0};

    private Context mContext;
    private Resources mResource;

    public static final int TAB_EXCHANGE_ITEMS = 0;
    public static final int TAB_MY_ITEMS = 1;
    public static final int TAB_EXCHANGE_RECORDS = 2;

    /**
     * 标志跳转到背包来自于
     */
    public static final int TO_BACKPACK = 101;

    private static int mobileVoucher;
    private static int yuanBao;

    private TextView tvYuanBao;
    private TextView tvMobileVoucher;
    private ExchangeBroadCastReceiver receiver;        // 元宝或者话费券数目发生变化时，更新界面

    public ViewPager viewPager;
    private String[] tabTitle;


    private Fragment mFragment;    //需要刷新的adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Create ExchangeTabActivity");

        mContext = this;
        mResource = getResources();
        tabTitle = getResources().getStringArray(R.array.tab_exchange);

        setContentView(R.layout.exchange_tab_activity);
        Intent intent = getIntent();
        
        initView();
        int toPage = intent.getIntExtra("gotoPage", -1);
        if(toPage == TO_BACKPACK){
        	viewPager.setCurrentItem(1);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(EXCHANGE_BROADCAST);
        receiver = new ExchangeBroadCastReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
        findViewById(R.id.btn_back).setOnClickListener(this);
        tvMobileVoucher = (TextView) findViewById(R.id.tv_mobile_voucher);
        tvYuanBao = (TextView) findViewById(R.id.tv_yuan_bao);
        updateTextView();
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    //点击我的物品
                    AnalyticsUtils.onClickEvent(ExchangeTabActivity.this, "053");
                } else if (position == 2) {
                    //点击兑换记录
                    AnalyticsUtils.onClickEvent(ExchangeTabActivity.this, "054");
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);


        final SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        SlidingTabLayout.TabColorizer colorizer = new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mContext.getResources().getColor(R.color.tab_bar);
            }

            @Override
            public int getDividerColor(int position) {
                //return mContext.getResources().getColor(R.color.hilight_yellow);
                return -1;
            }

            @Override
            public Bitmap getDividerBitmap(int position) {

                return BitmapFactory.decodeResource(mResource, R.drawable.tab_divider);
            }

            @Override
            public int getIndicatorCount(int position) {
                return countTitle[position];
            }
        };

        slidingTabLayout.setCustomTabView(R.layout.tab_item, R.id.tvTitle, /*R.id.tvCount*/-1);
        slidingTabLayout.setCustomTabColorizer(colorizer);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                // slidingTabLayout.setTabStripCountByIndex(position,testCount++);
            }

        });

        //默认跳转页面
        Intent intent = getIntent();
        int index = intent.getIntExtra("TAB_INDEX", 0);
        if (index > 0) {
            viewPager.setCurrentItem(index);
            slidingTabLayout.scrollToTab(index, 0);
        }


    }

    public void updateTextView() {
        UserInfo userInfo = HallDataManager.getInstance().getUserMe();
        tvMobileVoucher.setText(convertToWan(userInfo.getMobileVoucher()));
        Log.d(TAG, convertToWan(userInfo.getMobileVoucher()));
        tvYuanBao.setText(convertToWan(userInfo.getYuanBao()));
        Log.d(TAG, convertToWan(userInfo.getYuanBao()));
    }

    /*
     * 将整数转化成带“万”的4位显示的格式，多余部分省略。
     * 例如：num = 1944  显示 1944
     *     num = 11944 显示 1.194万 
     *     num = 111944 显示 11.19万
     *     num = 11111944 显示1111万
     *     
     */
    public String convertToWan(int num) {
        StringBuilder sb = new StringBuilder();
        int yi = num / 100000000; // 亿位
        int wan = num % 100000000 / 10000;      // 万位显示的数
        int ge = num % 10000;      // 取万后的余数
        if (yi > 0) {
            if (yi > 1000) {
                sb.append(yi + "亿");
            } else if (yi >= 100) {
                sb.append(yi + "." + wan / 1000 + "亿");
            } else if (yi >= 10) {
                sb.append(yi + "." + wan / 100 + "亿");
            } else if (yi >= 1) {
                sb.append(yi + "." + wan / 10 + "亿");
            }
        } else if (wan > 0) {
            if (wan >= 1000) {
                sb.append(wan + "万");
            } else if (wan >= 100) {
                sb.append(wan + "." + ge / 1000 + "万");
            } else if (wan >= 10) {
                sb.append(wan + "." + ge / 100 + "万");
            } else if (wan >= 1) {
                sb.append(wan + "." + ge / 10 + "万");
            }
        } else {
            sb.append(ge);
        }

        return sb.toString();
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

    @Override
    public void refreshFragment(Fragment fragment) {
    	mFragment = fragment;
    }

    public Fragment getFragment(){
    	return mFragment;
    }

    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment = null;
            switch (arg0) {
                case TAB_EXCHANGE_ITEMS:
                    fragment = new ExchangeGoodsFragment();
                    Bundle data = new Bundle();
                    data.putInt("mobilevoucher", mobileVoucher);
                    data.putInt("yuanbao", yuanBao);
                    fragment.setArguments(data);
                    break;
                case TAB_MY_ITEMS:
                    fragment = new MyGoodsFragment();
                    break;
                case TAB_EXCHANGE_RECORDS:
                    fragment = new ExchangeRecordsFragment();
                    break;
                default:
                    break;
            }

            return fragment;
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

    private class ExchangeBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ExchangeBroadCastReceiver");
            updateTextView();
        }
    }
}
