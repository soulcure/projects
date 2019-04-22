package com.taku.safe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.taku.safe.R;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by Administrator on 2017/5/27.
 * 日历选择对话框
 */

public class CalendarDialog extends Dialog implements View.OnClickListener {

    private TextView tv_title;

    private int maxYear;
    private int maxMonth;
    private int maxDate;

    private int curYear;
    private int curMonth;
    private int curDate;

    private int minYear;
    private int minMonth;
    private int minDate;
    private CallBack mCallBack;
    private MaterialCalendarView calendarView;

    public interface CallBack {
        void onSelect(Date d);
    }

    public static class Builder {
        private Context context;
        private int maxYear;
        private int maxMonth;
        private int maxDate;

        private int curYear;
        private int curMonth;
        private int curDate;

        private int minYear;
        private int minMonth;
        private int minDate;
        private CallBack callBack;

        public CalendarDialog builder() {
            return new CalendarDialog(this);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder maxYear(int maxYear) {
            this.maxYear = maxYear;
            return this;
        }

        public Builder maxMonth(int maxMonth) {
            this.maxMonth = maxMonth;
            return this;
        }

        public Builder maxDate(int maxDate) {
            this.maxDate = maxDate;
            return this;
        }


        public Builder curYear(int curYear) {
            this.curYear = curYear;
            return this;
        }

        public Builder curMonth(int curMonth) {
            this.curMonth = curMonth;
            return this;
        }

        public Builder curDate(int curDate) {
            this.curDate = curDate;
            return this;
        }

        public Builder minYear(int minYear) {
            this.minYear = minYear;
            return this;
        }

        public Builder minMonth(int minMonth) {
            this.minMonth = minMonth;
            return this;
        }

        public Builder minDate(int minDate) {
            this.minDate = minDate;
            return this;
        }

        public Builder callBack(CallBack callBack) {
            this.callBack = callBack;
            return this;
        }
    }


    private CalendarDialog(Builder builder) {
        super(builder.context, R.style.custom_dialog);
        setCanceledOnTouchOutside(true);
        maxYear = builder.maxYear;
        maxMonth = builder.maxMonth;
        maxDate = builder.maxDate;

        if (builder.curYear == 0) {
            builder.curYear = builder.maxYear;
        }

        if (builder.curMonth == 0) {
            builder.curMonth = builder.maxMonth;
        }

        if (builder.curDate == 0) {
            builder.curDate = builder.maxDate;
        }

        curYear = builder.curYear;
        curMonth = builder.curMonth;
        curDate = builder.curDate;

        minYear = builder.minYear;
        minMonth = builder.minMonth;
        minDate = builder.minDate;
        mCallBack = builder.callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendar);
        setDialogFeature();
        init();
    }


    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }


    private void init() {
        tv_title = (TextView) findViewById(R.id.title);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setLeftArrowMask(ContextCompat.getDrawable(getContext(), R.mipmap.ic_forward_black));
        calendarView.setRightArrowMask(ContextCompat.getDrawable(getContext(), R.mipmap.ic_next_black));
        calendarView.setDateSelected(CalendarDay.from(curYear, curMonth, curDate), true);

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(minYear, minMonth, minDate))
                .setMaximumDate(CalendarDay.from(maxYear, maxMonth, maxDate))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

       /* calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    Date d = date.getDate();
                    if (mCallBack != null) {
                        mCallBack.onSelect(d);
                    }
                }
            }
        });*/
    }

    public void setTitle(int resId) {
        tv_title.setText(getContext().getString(resId));
    }

    public void setTitle(String title) {
        if (title.isEmpty()) {
            tv_title.setVisibility(View.GONE);
        } else {
            tv_title.setText(title);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_cancel:
                cancel();
                break;
            case R.id.tv_confirm:
                CalendarDay date = calendarView.getSelectedDate();
                if (date != null) {
                    Date d = date.getDate();
                    if (mCallBack != null) {
                        mCallBack.onSelect(d);
                    }
                }
                cancel();
                break;
        }
    }
}
