package com.ivmall.android.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smit.android.ivmall.stb.R;

/**
 * Created by Markry on 2015/11/13.
 */
public class BabyAblityStarLayout extends LinearLayout {
    private Context context;
    private View contentView;
    private int starSize = 1;
    //private CheckBox star_one;
    private CheckBox star_two;
    private CheckBox star_three;
    private boolean addOrdele = true;
    private String ablity = "";

    public BabyAblityStarLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BabyAblityStarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public BabyAblityStarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BabayAblity);
        ablity = typedArray.getString(R.styleable.BabayAblity_ablity);
        typedArray.recycle();
        init();
    }

    private void init() {
        contentView = LayoutInflater.from(context).inflate(R.layout.baby_ablity_layout, null);
        //star_one = (CheckBox) contentView.findViewById(R.id.star_one);
        star_two = (CheckBox) contentView.findViewById(R.id.star_two);
        star_three = (CheckBox) contentView.findViewById(R.id.star_three);

        TextView text_ablity = (TextView) contentView.findViewById(R.id.text_ablity);
        text_ablity.setText(ablity);

        addView(contentView);
    }

    /**
     * 返回星星的个数
     */
    public int getStarSize() {
        return starSize;
    }

    /**
     * 设置星星的个数
     */
    public void setStarSize(int size) {
        //star_one.setChecked(false);
        star_two.setChecked(false);
        star_three.setChecked(false);
        starSize = 1;
        if (size == 1) {
            //star_one.setChecked(true);
            starSize = 1;
        } else if (size == 2) {
            // star_one.setChecked(true);
            star_two.setChecked(true);
            starSize = 2;
        } else if (size == 3) {
            // star_one.setChecked(true);
            star_two.setChecked(true);
            star_three.setChecked(true);
            starSize = 3;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.hasFocus()) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (starSize == 3) {
                    starSize = starSize - 1;
                    addOrdele = false;
                } else if (starSize == 1) {
                    starSize = starSize + 1;
                    addOrdele = true;
                } else {
                    if (addOrdele) {
                        starSize = starSize + 1;
                    } else {
                        starSize = starSize - 1;
                    }
                }
                setStarSize(starSize);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
