
package com.taku.safe.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taku.safe.R;
import com.taku.safe.tabs.BarChartMonthFragment;
import com.taku.safe.tabs.BarChartWeekFragment;
import com.taku.safe.tabs.SecondFragment;


public class NoSignDataFragment extends Fragment {

    private ViewPager mMainViewPager;
    private TabLayout mTabLayout;
    private FragmentManager fragmentManager;

    private String[] tabTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getChildFragmentManager();
        tabTitle = getResources().getStringArray(R.array.no_sign_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_sign, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        for (String item : tabTitle) {
            mTabLayout.addTab(mTabLayout.newTab().setText(item));
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (position) {
                    case 0: {
                        Fragment fragment = new BarChartMonthFragment();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();
                    }
                    break;
                    case 1: {
                        Fragment fragment = new BarChartWeekFragment();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();
                    }
                    break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mMainViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        setupToolbar(view);
        //setupViewPager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new BarChartMonthFragment();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

    }

    private void setupViewPager() {
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager());
        mMainViewPager.setAdapter(adapter);
        mMainViewPager.setOffscreenPageLimit(mMainViewPager.getAdapter().getCount());
        mTabLayout.setupWithViewPager(mMainViewPager);

    }


    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {


        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

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
                    ft = new BarChartMonthFragment();
                    break;
                case 1:
                    ft = new SecondFragment();
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
