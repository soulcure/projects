package com.ivmall.android.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ScrollGridView extends GridView {

	public ScrollGridView(Context context) {
		super(context);
	}

	public ScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 加上下面的话即可实现ListView GridView在scrollview中滑动
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);


	}
}
