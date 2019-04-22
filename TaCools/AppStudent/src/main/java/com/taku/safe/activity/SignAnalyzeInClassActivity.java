
package com.taku.safe.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.adapter.ClassAnalysisAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespClassList;
import com.taku.safe.protocol.respond.RespStudentList;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SignAnalyzeInClassActivity extends BasePermissionActivity {
    private static final String TAG = SignAnalyzeInClassActivity.class.getSimpleName();

    public static final String CLASS_ID = "classId";


    private RecyclerView mRecyclerView;
    private ClassAnalysisAdapter mAdapter;

    private ImageView img_pre;
    private ImageView img_next;
    private TextView tv_date;

    private String date;

    private Date curDate;
    private String titleDate;
    private int timeRange;


    private Calendar calendar;
    private int classId;

    private int signType;
    private int dataType;

    private String startDay;  //统计开始时间
    private String endDay;  //统计结束时间


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_by_class);
        initTitle();
        initView();
        initDate();

        reqAnalysisByClass();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
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
        tv_date = (TextView) findViewById(R.id.tv_date);

        img_pre = (ImageView) findViewById(R.id.img_pre);
        img_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preDate();
            }
        });

        img_next = (ImageView) findViewById(R.id.img_next);
        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDate();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ClassAnalysisAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

    }


    private void initDate() {
        dataType = getIntent().getIntExtra(UnusualAnalysisActivity.DATA_TYPE, UnusualAnalysisActivity.DATA_UNUSUAL);
        signType = getIntent().getIntExtra(UnusualAnalysisActivity.SIGN_TYPE, UnusualAnalysisActivity.SIGN_RESET);

        curDate = (Date) getIntent().getSerializableExtra(UnusualAnalysisActivity.CUR_DATE);
        titleDate = getIntent().getStringExtra(UnusualAnalysisActivity.TITLE_DATE);
        timeRange = getIntent().getIntExtra(UnusualAnalysisActivity.TIME_RANGE, UnusualAnalysisActivity.DATA_OF_MONTH);


        startDay = getIntent().getStringExtra(UnusualAnalysisActivity.START_DAY);
        endDay = getIntent().getStringExtra(UnusualAnalysisActivity.END_DAY);

        classId = getIntent().getIntExtra(CLASS_ID, 0);

        tv_date.setText(titleDate);

        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
    }

    private void preDate() {
        if (timeRange == UnusualAnalysisActivity.DATA_OF_MONTH) {
            calendar.add(Calendar.MONTH, -1);
            tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);

            curDate = calendar.getTime();

        } else {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);

            /*int year = calendar.get(Calendar.YEAR);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            String format = getString(R.string.week_of_year);
            String text = String.format(format, year, week);
            tv_date.setText(text);*/

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            tv_date.setText(startDay + " - " + endDay);

            curDate = calendar.getTime();
        }

        reqAnalysisByClass();
    }


    private void nextDate() {
        if (timeRange == UnusualAnalysisActivity.DATA_OF_MONTH) {

            calendar.add(Calendar.MONTH, 1);
            tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);

            curDate = calendar.getTime();
        } else {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);

            /*int year = calendar.get(Calendar.YEAR);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            String format = getString(R.string.week_of_year);
            String text = String.format(format, year, week);
            tv_date.setText(text);*/

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            tv_date.setText(startDay + " - " + endDay);
            curDate = calendar.getTime();
        }
        reqAnalysisByClass();
    }


    private void reqAnalysisByClass() {
        if (classId == 0) {
            return;
        }

        String url;
        if (signType == UnusualAnalysisActivity.SIGN_RESET) {
            url = AppConfig.TEACHER_REST_ANALYSIS_STUDENT_STATISTICS;
        } else {
            url = AppConfig.TEACHER_INTERNSHIP_ANALYSIS_STUDENT_STATISTICS;
        }


        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());


        ContentValues params = new ContentValues();
        params.put("classId", classId);
        params.put("type", dataType);
        params.put("startDay", startDay);
        params.put("endDay", endDay);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespStudentList bean = GsonUtil.parse(response, RespStudentList.class);
                if (bean != null && bean.isSuccess()) {
                    List<RespStudentList.StudentListBean> list = bean.getStudentList();
                    mAdapter.setList(list);
                }
            }
        });

    }

    public Date getCurDate() {
        return curDate;
    }

    public String getTitleDate() {
        return tv_date.getText().toString();
    }

    public int getTimeRange() {
        return timeRange;
    }

    public int getSignType() {
        return signType;
    }

    public int getDataType() {
        return dataType;
    }

    public String getStartDay() {
        return startDay;
    }

    public String getEndDay() {
        return endDay;
    }

}
