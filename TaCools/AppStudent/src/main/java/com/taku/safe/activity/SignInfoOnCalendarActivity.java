package com.taku.safe.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespStudSignRecord;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class SignInfoOnCalendarActivity extends BasePermissionActivity {

    public static final String STUDENT_ID = "student_id";
    public static final String UNUSUAL_DAYS = "unusualDays";

    private RoundedImageView imgHeard;

    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_class;
    private TextView tv_times;

    private TextView tv_info;

    private MaterialCalendarView calendarView;

    private String date;

    private Date curDate;
    private String titleDate;
    private int timeRange;

    private Calendar calendar;
    private int studentId;

    private int signType;
    private int dataType;

    private String startDay;  //统计开始时间
    private String endDay;  //统计结束时间

    private boolean isInit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info_on_calendar);

        initTitle();
        initView();
        initDate();

        reqRestSignDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initTitle() {
        String name = getIntent().getStringExtra("name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(name);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        imgHeard = (RoundedImageView) findViewById(R.id.cropImageView);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_class = (TextView) findViewById(R.id.tv_class);
        tv_times = (TextView) findViewById(R.id.tv_times);
        tv_info = (TextView) findViewById(R.id.tv_info);

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setLeftArrowMask(ContextCompat.getDrawable(this, R.mipmap.ic_forward_black));
        calendarView.setRightArrowMask(ContextCompat.getDrawable(this, R.mipmap.ic_next_black));

        /*calendarView.setDateSelected(CalendarDay.from(2017, Calendar.JULY, 1), true);   //设置选择日期 ，月份默认0-11
        calendarView.setDateSelected(CalendarDay.from(2017, 6, 2), true);
        calendarView.setDateSelected(CalendarDay.from(2017, 6, 3), true);*/

        String[] array = getIntent().getStringArrayExtra(UNUSUAL_DAYS);
        if (array != null) {
            try {
                for (String item : array) {
                    Date date = TimeUtils.DATE_FORMAT_DATE.parse(item);
                    calendarView.setDateSelected(date, true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tv_times.setText(String.valueOf(array.length));
        } else {
            tv_times.setText(String.valueOf(0));
        }

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(2017, 5, 1))
                .setMaximumDate(CalendarDay.from(2018, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                Calendar calendar = date.getCalendar();

                calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
                startDay = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());

                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                endDay = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());

                reqRestSignDetail();
            }
        });

    }


    private void initDate() {
        dataType = getIntent().getIntExtra(UnusualAnalysisActivity.DATA_TYPE, UnusualAnalysisActivity.DATA_UNUSUAL);
        signType = getIntent().getIntExtra(UnusualAnalysisActivity.SIGN_TYPE, UnusualAnalysisActivity.SIGN_RESET);

        curDate = (Date) getIntent().getSerializableExtra(UnusualAnalysisActivity.CUR_DATE);
        titleDate = getIntent().getStringExtra(UnusualAnalysisActivity.TITLE_DATE);
        timeRange = getIntent().getIntExtra(UnusualAnalysisActivity.TIME_RANGE, UnusualAnalysisActivity.DATA_OF_MONTH);

        //startDay = getIntent().getStringExtra(UnusualAnalysisActivity.START_DAY);
        //endDay = getIntent().getStringExtra(UnusualAnalysisActivity.END_DAY);

        studentId = getIntent().getIntExtra(STUDENT_ID, 0);

        if (dataType == UnusualAnalysisActivity.DATA_UNUSUAL) {
            tv_info.setText(R.string.unusual_times);
        } else {
            tv_info.setText(R.string.no_sign_times);
        }

        calendar = Calendar.getInstance();
        calendar.setTime(curDate);

        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        startDay = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        endDay = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());

    }


    /**
     * 获取学生单次打卡详情
     */
    private void reqRestSignDetail() {
        if (studentId == 0) {
            return;
        }

        String url;
        if (signType == UnusualAnalysisActivity.SIGN_RESET) {
            url = AppConfig.TEACHER_REST_ANALYSIS_STUDENT_DAYS;
        } else {
            url = AppConfig.TEACHER_INTERNSHIP_ANALYSIS_STUDENT_DAYS;
        }


        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());


        ContentValues params = new ContentValues();
        params.put("studentId", studentId);
        params.put("type", dataType);   //签到状态 -1 未签到 0 异常
        params.put("startDay", startDay);
        params.put("endDay", endDay);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespStudSignRecord bean = GsonUtil.parse(response, RespStudSignRecord.class);
                if (bean != null && bean.isSuccess()) {
                    if (!isInit) {
                        isInit = true;
                        refreshUserInfo(bean);
                    }
                    List<String> list = bean.getUnusualDays();
                    if (list != null) {
                        for (String item : list) {
                            try {
                                Date date = TimeUtils.DATE_FORMAT_DATE.parse(item);
                                calendarView.setDateSelected(date, true);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        tv_times.setText(String.valueOf(list.size()));
                    } else {
                        tv_times.setText(String.valueOf(0));
                    }

                }
            }
        });
    }


    /**
     * 刷新学生信息
     *
     * @param info
     */
    private void refreshUserInfo(RespStudSignRecord info) {
        String avatar = info.getAvatarUrl();
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(mContext)
                    .load(avatar)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imgHeard);
        } else {
            imgHeard.setImageResource(R.mipmap.ic_male);
        }

        tv_name.setText(info.getStudentName());
        tv_phone.setText(info.getStudentPhone());
        tv_class.setText(info.getClassName());

    }


}