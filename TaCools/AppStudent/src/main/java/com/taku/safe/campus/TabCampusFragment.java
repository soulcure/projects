package com.taku.safe.campus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taku.safe.R;

/**
 * Created by colin on 2017/5/10.
 */
public class TabCampusFragment extends Fragment {
    private ViewPager mMainViewPager;
    private TabLayout mTabLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_tabpager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mMainViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        //setupToolbar(view);
        setupViewPager();
    }

    /*private void setupToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
    }*/

    private void setupViewPager() {
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager());
        mMainViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mMainViewPager);

    }


    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitle;


        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabTitle = getResources().getStringArray(R.array.campus_title);
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
                    ft = new TucaoFragment();
                    break;
                case 1:
                    ft = new CampusFragment();
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
