package com.ivmall.android.app.dialog;


import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.TextView;

import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.uitls.ScreenUtils;


public class BirthdayPickerDialog extends Dialog implements Formatter, View.OnClickListener {

    private NumberPicker mYear;// 年
    private NumberPicker mMonth;// 月
    private NumberPicker mDay;// 日

    private Button btnConfirm;
    private Button btnCacel;
    private TextView tvInfo;

    private Context mContext;
    private OnclickmDateLinster mDateLinster;


    public interface OnclickmDateLinster {

        void choseDate(String date);

    }


    public BirthdayPickerDialog(Context context) {
        super(context, R.style.dialog_holo_theme);
        mContext = context;
    }


    public BirthdayPickerDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_dialog);

        initView();

    }


    private void initView() {
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);

        btnCacel = (Button) findViewById(R.id.btn_cacel);
        btnCacel.setOnClickListener(this);

        tvInfo = (TextView) findViewById(R.id.tv_info);

        Calendar calendar = Calendar.getInstance();

        mYear = (NumberPicker) findViewById(R.id.year);
        mYear.setFormatter(this);
        mYear.setEnabled(true);// 内容不可编辑

        int curYear = calendar.get(Calendar.YEAR);
        mYear.setMaxValue(curYear);// 获取当前年
        mYear.setMinValue(curYear - 8);// 最小值
        mYear.setValue(curYear - 3);


        mMonth = (NumberPicker) findViewById(R.id.month);
        mMonth.setFormatter(this);
        mMonth.setEnabled(true);// 内容不可编辑

        mMonth.setMaxValue(12);// 最大值
        mMonth.setMinValue(1);// 最小值
        mMonth.setValue(calendar.get(Calendar.MONTH));


        mDay = (NumberPicker) findViewById(R.id.day);
        mDay.setFormatter(this);
        mDay.setEnabled(true);// 内容不可编辑

        mDay.setMaxValue(31);
        mDay.setMinValue(1);
        mDay.setValue(calendar.get(Calendar.DATE));

        if (ScreenUtils.isTv(mContext)) {
            int sdkVer = android.os.Build.VERSION.SDK_INT;
            if (sdkVer > 17) {
                btnConfirm.setVisibility(View.GONE);
                btnCacel.setVisibility(View.GONE);

                SpannableString msp = new SpannableString(mContext.getString(R.string.birthday_info));

                int color = mContext.getResources().getColor(R.color.birday_info);

                msp.setSpan(new ForegroundColorSpan(color), 1, 4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvInfo.setText(msp);

            } else {
                btnCacel.requestFocus();
            }


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            dismiss();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            String year = String.valueOf(mYear.getValue());// 年
            String month = String.valueOf(format(mMonth.getValue()));// 月
            String date = String.valueOf(format(mDay.getValue()));// 日
            mDateLinster.choseDate(year + "-" + month + "-" + date);
            dismiss();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }


    /***
     * value的值格式，当值小于10，前面加一个0
     */
    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }


    public void setDateLinster(OnclickmDateLinster linster) {
        this.mDateLinster = linster;
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_confirm:
                String year = String.valueOf(mYear.getValue());// 年
                String month = String.valueOf(format(mMonth.getValue()));// 月
                String date = String.valueOf(format(mDay.getValue()));// 日
                mDateLinster.choseDate(year + "-" + month + "-" + date);
                dismiss();

                break;
            case R.id.btn_cacel:
                dismiss();
                break;

            default:
                break;
        }

    }

}
