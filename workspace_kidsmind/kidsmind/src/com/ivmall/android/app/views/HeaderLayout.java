package com.ivmall.android.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivmall.android.app.R;

/**
 * Created by koen on 2016/3/23.
 */
public class HeaderLayout extends LinearLayout{

    private TextView mTitleText;
    private ImageButton mReturnBtn;
    private onImageButtonClickListener clickListener;

    public HeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View mHeader = mInflater.inflate(R.layout.header_bar, this);
        mTitleText = (TextView) mHeader.findViewById(R.id.action_bar_title_name);
        mReturnBtn = (ImageButton) mHeader.findViewById(R.id.action_bar_return);
        mReturnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick();
            }
        });
    }

    public void setTitleText(int id) {
        if (mTitleText != null && id > 0) {
            mTitleText.setText(id);
        }
    }

    public interface onImageButtonClickListener {
        void onClick();
    }

    public void setOnImageButtonClickListener (onImageButtonClickListener listener) {
        clickListener = listener;
    }


}
