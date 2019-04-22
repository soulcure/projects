package com.viewpagerindicator;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 提供给viewpager 的适配器
 */
public class ViewPagerAdapter extends PagerAdapter {

	private List<View> mListViews;
	public ViewPagerAdapter(List<View> listViews){
		mListViews = listViews;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView(mListViews.get(position));
	}


	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(mListViews.get(position),0);
		return mListViews.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
