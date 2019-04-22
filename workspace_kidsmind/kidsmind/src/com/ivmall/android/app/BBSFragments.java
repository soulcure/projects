package com.ivmall.android.app;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivmall.android.app.parent.AnswerToMeFragment;
import com.ivmall.android.app.parent.SendFromMeFragment;


public class BBSFragments extends Fragment {
    public final static String TAG = BBSFragments.class.getSimpleName();

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private Context mContext;
    private String[] tabTitle;

    // --------------------配置数据 end--------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabTitle = getResources().getStringArray(R.array.tab_bbs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bbs_fragments, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager());

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment ft = null;
            switch (position) {
                case 0:
                    ft = new SendFromMeFragment();
                    break;
                case 1:
                    ft = new AnswerToMeFragment();
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
