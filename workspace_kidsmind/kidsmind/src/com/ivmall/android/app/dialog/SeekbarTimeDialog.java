package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.seekbar.ComboSeekBar;

import java.util.Arrays;
import java.util.List;

/**
 * *
 *
 * @author
 * @ClassName: CustomDialog
 * @Description: 一般对话框，只包含显示文本，一个动态修改的按钮,默认是确定按钮
 * @date 2012-7-26 下午03:57:15
 */
public class SeekbarTimeDialog extends Dialog {

    private String mSelectValue = "0";

    private int mSettingCount = 0;

    private Context mContext;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */
    public SeekbarTimeDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
        mSettingCount = 0;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_dialog);
        initView();
    }

    private void initView() {
        TextView tvVipInfo = (TextView) findViewById(R.id.tvVipInfo);

        boolean isVip = ((KidsMindApplication) mContext.getApplicationContext()).isVip();
        if (isVip) {
            String time = ((KidsMindApplication) mContext.getApplicationContext()).getVipExpiresTime();
            if (!StringUtils.isEmpty(time)) {
                String timeStr = mContext.getString(R.string.vip_more) + time + mContext.getString(R.string.vip_more2);
                tvVipInfo.setText(timeStr);
            }
        }


        ComboSeekBar seekbar = (ComboSeekBar) findViewById(R.id.seekbar);
        final List<String> seekbarlist = Arrays.asList("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60");

        seekbar.setAdapter(seekbarlist);
        seekbar.setSelection(6);
//        mSettingCount++;//设置播放时长（默认的30分钟也可以）
        seekbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mSettingCount++;
                mSelectValue = seekbarlist.get(position);
            }
        });

    }


    /**
     * 判断是否用户有设置
     *
     * @return
     */
    public boolean isParentsSettingTime() {
        boolean res = false;
        if (mSettingCount > 1) {
            res = true;
        }
        return res;
    }


    public String getSelectValue() {
        return mSelectValue;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            dismiss();
        } catch (Exception e) {

        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                dismiss();
            } catch (Exception e) {

            }
        }
        return true;
    }
}
