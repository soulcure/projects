package com.taku.safe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.http.IGetListener;
import com.taku.safe.protocol.respond.RespRestSignData;
import com.taku.safe.ui.count.SignCountFragment;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.taku.safe.views.MainViewPager;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.util.Calendar;


public class SignCountActivity extends BasePermissionActivity {


    public static final int TYPE_REST = 0;
    public static final int TYPE_PRACTICE = 1;

    public static final String DATE = "date";
    public static final String CUR_TAB = "cur_tab";
    public static final String SIGN_TYPE = "sign_type";

    public static final String SIGN_STATUS = "sign_status";

    public static final String NORMAL_COUNT = "normal_count";
    public static final String UNUSUAL_COUNT = "unusual_count";
    public static final String NOSIGN_COUNT = "nosign_count";
    public static final String NOREGEDIT_COUNT = "noregedit_count";


    public static final int PI_ZHU = 100;  //1 正常

    //签到状态
    public static final int NORMAL = 1;  //1 正常
    public static final int UNUSUAL = 0; //0 异常签到
    public static final int NOSIGN = -1; //-1 未签到
    public static final int NOREGEDIT = -2; //-2 未激活


    private int signType;

    private ImageView img_pre;
    private ImageView img_next;
    private TextView tv_date;

    private String date;
    private Calendar calendar;


    private MainViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManager;

    private String[] tabCount;

    private boolean isReset;//是否批注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_count);

        signType = getIntent().getIntExtra(SIGN_TYPE, TYPE_REST);

        int normal = getIntent().getIntExtra(NORMAL_COUNT, 0);
        int unusual = getIntent().getIntExtra(UNUSUAL_COUNT, 0);
        int noSign = getIntent().getIntExtra(NOSIGN_COUNT, 0);
        int noRegedit = getIntent().getIntExtra(NOREGEDIT_COUNT, 0);

        tabCount = new String[]{String.valueOf(normal), String.valueOf(unusual),
                String.valueOf(noSign), String.valueOf(noRegedit)};

        initTitle();
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        if (signType == TYPE_REST) {
            tv_title.setText(R.string.sign_rest_count);
        } else {
            tv_title.setText(R.string.sign_practice_count);
        }

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {
        date = getIntent().getStringExtra(DATE);

        try {
            calendar = TimeUtils.parseDate(date, TimeUtils.DATE_FORMAT_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_date.setText(date);

        img_pre = (ImageView) findViewById(R.id.img_pre);
        img_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, -1);
                date = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());
                tv_date.setText(date);
                reqSignRestStatistics(date);
            }
        });

        img_next = (ImageView) findViewById(R.id.img_next);
        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, 1);
                date = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());
                tv_date.setText(date);
                reqSignRestStatistics(date);
            }
        });


        mViewPager = (MainViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        fragmentManager = getSupportFragmentManager();
        mAdapter = new TabFragmentPagerAdapter(fragmentManager, this);

        //mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            View view = mAdapter.getTabView(i);
            if (i == 0) {
                view.setSelected(true);
            }
            tab.setCustomView(view);
        }

        int signType = getIntent().getIntExtra(CUR_TAB, 0);
        mViewPager.setCurrentItem(signType);
    }


    private void reqSignRestStatistics(final String date) {
        if (signType == TYPE_REST) {
            mTakuApp.reqSignRestStatistics(date, new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    RespRestSignData restSignData = GsonUtil.parse(response, RespRestSignData.class);
                    if (restSignData != null && restSignData.isSuccess()) {
                        int normal = restSignData.getSiginNumNormal();
                        int unusual = restSignData.getSiginNumAbnormal();
                        int noSign = restSignData.getNotSignin();
                        int noRegedit = restSignData.getNotActive();
                        String[] counts = new String[]{String.valueOf(normal), String.valueOf(unusual),
                                String.valueOf(noSign), String.valueOf(noRegedit)};
                        setData(counts);
                    }
                }
            });
        } else {
            mTakuApp.reqSignPracticeStatistics(date, new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    RespRestSignData restSignData = GsonUtil.parse(response, RespRestSignData.class);
                    if (restSignData != null && restSignData.isSuccess()) {
                        int normal = restSignData.getSiginNumNormal();
                        int unusual = restSignData.getSiginNumAbnormal();
                        int noSign = restSignData.getNotSignin();
                        int noRegedit = restSignData.getNotActive();
                        String[] counts = new String[]{String.valueOf(normal), String.valueOf(unusual),
                                String.valueOf(noSign), String.valueOf(noRegedit)};
                        setData(counts);
                    }
                }
            });
        }


    }


    @Override
    public void onBackPressed() {
        if (isReset) {
            setResult(Activity.RESULT_OK);
        }
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PI_ZHU && resultCode == Activity.RESULT_OK) {
            teacherPiZhu();
            isReset = true;
        }
    }

    public void teacherPiZhu() {
        String[] numbers = tabCount;
        if (numbers.length != 4) {
            return;
        }

        int normal = Integer.parseInt(numbers[0]);
        normal++;

        int unusual = Integer.parseInt(numbers[1]);
        unusual--;
        if (unusual <= 0) {
            unusual = 0;
        }

        numbers[0] = String.valueOf(normal);
        numbers[1] = String.valueOf(unusual);

        setData(numbers);
    }


    /**
     * 刷新页面数据
     *
     * @param numbers
     */
    private void setData(String[] numbers) {
        tabCount = numbers;
        mAdapter.notifyDataSetChanged();

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            View view = mAdapter.getTabView(i);
            if (i == mViewPager.getCurrentItem()) {
                view.setSelected(true);
            }
            tab.setCustomView(view);

        }
    }


    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private Context mContext;
        private String[] tabTitle;

        private TabFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            tabTitle = getResources().getStringArray(R.array.tab_title);
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;

            switch (arg0) {
                case 0:
                    ft = SignCountFragment.newInstance(date, signType, NORMAL);
                    break;
                case 1:
                    ft = SignCountFragment.newInstance(date, signType, UNUSUAL);
                    break;
                case 2:
                    ft = SignCountFragment.newInstance(date, signType, NOSIGN);
                    break;
                case 3:
                    ft = SignCountFragment.newInstance(date, signType, NOREGEDIT);
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
        public int getItemPosition(Object object) {
            // 系统默认返回     POSITION_UNCHANGED，未改变
            return POSITION_NONE;   // 返回发生改变，让系统重新加载
        }

        private View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            TextView tv_tab = (TextView) v.findViewById(R.id.tv_tab);
            tv_tab.setText(tabTitle[position]);
            TextView tv_count = (TextView) v.findViewById(R.id.tv_count);
            tv_count.setText(tabCount[position]);

            return v;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

    }


}
