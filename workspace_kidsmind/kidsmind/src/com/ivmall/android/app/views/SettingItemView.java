package com.ivmall.android.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivmall.android.app.R;

/**
 * Created by koen on 2016/3/16.
 */
public class SettingItemView extends RelativeLayout {

    // 图片
    private int imageId;
    // 文本内容
    private int titleTextId;
    // 右边的文字
    private int rightTextId;
    // 是箭头还是checkbox或者是button

    private final int ARROW = 1;
    private final int BUTTON = 2;
    private final int RADIO = 3;

    private int itemType = 0;

    private ImageView imageView;
    private TextView mTextView;
    private TextView rightTextView;
    private ImageView image_arrow;
    private IosCheckBox mCheckBox;
    private ImageButton imageButton;

    private onSettingItemClickListener clickListener;
    private onSettingItemCheckChangeListener checkChangeListener;
    private onSettingItemButtonClickListener buttonClickListener;


    public SettingItemView(Context context) {
        super(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void init(Context context, AttributeSet attrs) {
        // 读数据
        TypedArray array = context.obtainStyledAttributes(
                attrs, R.styleable.SettingItemView);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.SettingItemView_imageSrc:
                    imageId = array.getResourceId(attr, 0);
                    break;

                case R.styleable.SettingItemView_itemText:
                    titleTextId = array.getResourceId(attr, 0);
                    break;

                case R.styleable.SettingItemView_rightItemText:
                    rightTextId = array.getResourceId(attr, 0);
                    break;

                case R.styleable.SettingItemView_type:
                    itemType = array.getInt(attr, 1);
                    break;
            }
        }
        array.recycle();

        // 读布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_cardview, this);

        imageView = (ImageView) view.findViewById(R.id.card_image);
        mTextView = (TextView) view.findViewById(R.id.card_item_name);
        rightTextView = (TextView) view.findViewById(R.id.card_right_text);
        image_arrow = (ImageView) view.findViewById(R.id.card_arrow);
        imageButton = (ImageButton) view.findViewById(R.id.card_button);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null)
                    buttonClickListener.onButtonClick(v);
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.onClick(v);
            }
        });
        mCheckBox = (IosCheckBox) view.findViewById(R.id.card_checkbox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkChangeListener != null)
                    checkChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        });

        imageView.setBackgroundResource(imageId);
        mTextView.setText(titleTextId);
        rightTextView.setText(rightTextId);
        if (itemType == ARROW) {
            image_arrow.setVisibility(VISIBLE);
            mCheckBox.setVisibility(GONE);
            imageButton.setVisibility(GONE);
        } else if (itemType == RADIO) {
            image_arrow.setVisibility(GONE);
            mCheckBox.setVisibility(VISIBLE);
            imageButton.setVisibility(GONE);
        } else if (itemType == BUTTON) {
            image_arrow.setVisibility(GONE);
            mCheckBox.setVisibility(GONE);
            imageButton.setVisibility(VISIBLE);
        }
    }


    public void setChecked(boolean checked) {
        if (mCheckBox.getVisibility() == VISIBLE)
            mCheckBox.setChecked(checked);
    }

    // 定义它的点击事件
    public interface onSettingItemClickListener {
        void onClick(View arg0);
    }

    // 定义它checkbutton的check事件
    public interface onSettingItemCheckChangeListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }

    // 定义它button的click事件
    public interface onSettingItemButtonClickListener {
        void onButtonClick(View buttonView);
    }


    // 设置栏栏单击事件
    public void setOnSettingItemClickListener(onSettingItemClickListener listener) {
        clickListener = listener;
    }

    // 设置栏选择按钮改变事件
    public void setOnSettingItemCheckChangeListener(onSettingItemCheckChangeListener listener) {
        checkChangeListener = listener;
    }

    // 设置栏关闭按钮点击事件
    public void setOnSettingItemButtonClickListener(onSettingItemButtonClickListener listener) {
        buttonClickListener = listener;
    }


}
