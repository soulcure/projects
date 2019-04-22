package com.taku.safe.activity;

import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.adapter.SchoolAnalysisAdapter;
import com.taku.safe.chart.DayAxisValueFormatterMonth;
import com.taku.safe.chart.DayAxisValueFormatterWeek;
import com.taku.safe.chart.PersonAxisValueFormatter;
import com.taku.safe.chart.XYMarkerView;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespCollegeAnalysis;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class UnusualAnalysisActivity extends BasePermissionActivity implements OnChartValueSelectedListener {
    private static final String TAG = UnusualAnalysisActivity.class.getSimpleName();

    /*************** intent key start ************/
    public static final String DATA_TYPE = "data_type"; //签到类型 key (异常签到或未签到)
    public static final String SIGN_TYPE = "sign_type"; //签到类型 key (作息签到或实习签到)

    public static final String START_DAY = "startDay"; //查询开始日期
    public static final String END_DAY = "endDay"; //查询结束日期

    public static final String CUR_DATE = "cur_date"; //当前日期 key
    public static final String TITLE_DATE = "title_date"; //当前日期范围
    public static final String TIME_RANGE = "timeRange"; //当前日期选择 月或周
    /*************** intent key end ************/


    public static final int DATA_UNUSUAL = 0;// 异常签到
    public static final int DATA_NO_SIGN = -1;// 未签到

    public static final int SIGN_RESET = 10; //作息签到
    public static final int SIGN_PRACTICE = 11; //实习签到

    public static final int DATA_OF_MONTH = 0;// 按月查询
    public static final int DATA_OF_WEEK = 1;// 按周查询

    private BarChart mBarChart;  //柱形图
    private LineChart mLineChart; //折线图

    private TextView tv_num_info;
    private TextView tv_number;
    private TextView tv_rank;

    private RecyclerView mRecyclerView;
    private SchoolAnalysisAdapter mAdapter;

    private ImageView img_pre;
    private ImageView img_next;
    private TextView tv_date;

    private String date;

    private Date curDate;
    private Calendar calendar;

    private int timeRange = DATA_OF_MONTH;

    private int signType;
    private int dataType;

    private String startDay;  //统计开始时间
    private String endDay;  //统计结束时间


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unusual_analysis);

        dataType = getIntent().getIntExtra(DATA_TYPE, DATA_UNUSUAL);
        signType = getIntent().getIntExtra(SIGN_TYPE, SIGN_RESET);
        date = getIntent().getStringExtra(CUR_DATE);

        initTitle();
        initView();

        initDate();
        initBarChart();
        initLineChart();
        reqUnusualStatistics();
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


    public Date getCurDate() {
        return curDate;
    }

    public String getTitleDate() {
        return tv_date.getText().toString();
    }

    public int getTimeRange() {
        return timeRange;
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);

        if (dataType == DATA_UNUSUAL) {
            tv_title.setText(R.string.unusual_title);
        } else {
            tv_title.setText(R.string.no_sign_title);
        }

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

        mBarChart = (BarChart) findViewById(R.id.barChart);
        mLineChart = (LineChart) findViewById(R.id.lineChart);

        tv_num_info = (TextView) findViewById(R.id.tv_num_info);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_rank = (TextView) findViewById(R.id.tv_rank);

        if (signType == SIGN_RESET) {
            if (dataType == DATA_UNUSUAL) {
                tv_num_info.setText(R.string.rest_unusual_sign_number);
            } else {
                tv_num_info.setText(R.string.rest_no_sign_number);
            }
        } else {
            if (dataType == DATA_UNUSUAL) {
                tv_num_info.setText(R.string.practice_unusual_sign_number);
            } else {
                tv_num_info.setText(R.string.practice_no_sign_number);
            }
        }

        if (dataType == DATA_UNUSUAL) {
            tv_number.setText(R.string.unusual_number);
            tv_rank.setText(R.string.unusual_rank);
        } else {
            tv_number.setText(R.string.no_sign_number);
            tv_rank.setText(R.string.no_sign_rank);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SchoolAnalysisAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        SegmentedButtonGroup segmentBtn = (SegmentedButtonGroup) findViewById(R.id.segment);
        segmentBtn.setOnClickedButtonListener(
                new SegmentedButtonGroup.OnClickedButtonListener() {
                    @Override
                    public void onClickedButton(int position) {
                        if (position == 0) {
                            if (mBarChart.getVisibility() == View.GONE) {
                                mBarChart.setVisibility(View.VISIBLE);
                            }
                            if (mLineChart.getVisibility() == View.VISIBLE) {
                                mLineChart.setVisibility(View.GONE);
                            }
                            switchWeekOrMonth(false);

                        } else {
                            if (mLineChart.getVisibility() == View.GONE) {
                                mLineChart.setVisibility(View.VISIBLE);
                            }
                            if (mBarChart.getVisibility() == View.VISIBLE) {
                                mBarChart.setVisibility(View.GONE);
                            }
                            switchWeekOrMonth(true);

                        }
                    }
                });
        segmentBtn.setPosition(0, 0);
    }

    private void initDate() {
        try {
            calendar = TimeUtils.parseDate(date, TimeUtils.DATE_FORMAT_DATE);
            curDate = calendar.getTime();

            tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(curDate));

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void preDate() {
        if (timeRange == DATA_OF_MONTH) {
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

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            tv_date.setText(startDay + " - " + endDay);

            curDate = calendar.getTime();
        }

        reqUnusualStatistics();
    }


    private void nextDate() {
        if (timeRange == DATA_OF_MONTH) {

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

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            tv_date.setText(startDay + " - " + endDay);
            curDate = calendar.getTime();
        }

        reqUnusualStatistics();
    }

    private void switchWeekOrMonth(boolean isWeek) {
        if (isWeek) {  //月-->周
            timeRange = DATA_OF_WEEK;
            mTakuApp.setTimeRange(timeRange);
            calendar.setTime(curDate);

            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            tv_date.setText(startDay + " - " + endDay);
        } else {  //周-->月
            timeRange = DATA_OF_MONTH;
            mTakuApp.setTimeRange(timeRange);
            calendar.setTime(curDate);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));
        }

        reqUnusualStatistics();
    }


    private void initBarChart() {

        mBarChart.setOnChartValueSelectedListener(this);

        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);

        mBarChart.getDescription().setEnabled(false);

        // if more than 31 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(31);

        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);
        mBarChart.setNoDataText("loading...");
        mBarChart.setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatterMonth();
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new PersonAxisValueFormatter();
        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(3, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(1f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(1f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setTextColor(Color.RED);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(mBarChart); // For bounds control
        mBarChart.setMarker(mv); // Set the marker to the chart
        mBarChart.animateY(2000);  // y轴动画

    }


    private void initLineChart() {

        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.getDescription().setEnabled(false);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);

        mLineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);

        // set an alternative background color
        // mLineChart.setBackgroundColor(Color.LTGRAY);

        // if more than 7 entries are displayed in the chart, no values will be
        // drawn
        mLineChart.setMaxVisibleValueCount(7);

        // scaling can now only be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);
        mLineChart.setNoDataText("loading...");
        mLineChart.setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatterWeek();
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new PersonAxisValueFormatter();
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setLabelCount(3, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(1f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(3, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(1f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        Legend l = mLineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setTextColor(Color.RED);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(mLineChart); // For bounds control
        mLineChart.setMarker(mv); // Set the marker to the chart

        mLineChart.animateY(2000);  // y轴动画

    }

    /**
     * 异常统计分析-获取学院统计信息
     */
    private void reqUnusualStatistics() {
        String url;
        if (signType == SIGN_RESET) {
            url = AppConfig.TEACHER_UNUSUAL_COLLEGE_STATISTICS;
        } else {
            url = AppConfig.TEACHER_INTERNSHIP_COLLEGE_STATISTICS;
        }

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("schoolId", mTakuApp.getTeacherInfo().getSchool().getSchoolId());
        params.put("type", dataType);
        params.put("startDay", startDay);
        params.put("endDay", endDay);


        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespCollegeAnalysis bean = GsonUtil.parse(response, RespCollegeAnalysis.class);
                if (!isFinishing() && bean != null && bean.isSuccess()) {
                    List<RespCollegeAnalysis.CollegeListBean> collegeList = bean.getCollegeList();
                    List<RespCollegeAnalysis.UnusualListBean> unusualList = bean.getUnusualList();
                    mAdapter.setList(collegeList);
                    if (timeRange == DATA_OF_MONTH) {
                        setBarChart(unusualList);
                    } else {
                        setLineChart(unusualList);
                    }

                }
            }
        });

    }


    /**
     * 设置柱形图
     *
     * @param list
     */
    private void setBarChart(List<RespCollegeAnalysis.UnusualListBean> list) {
        if (ListUtils.isEmpty(list)) {
            mBarChart.setNoDataText("未查询到数据!");
            mBarChart.invalidate();
            return;
        }

        int maxValue = 0;
        for (RespCollegeAnalysis.UnusualListBean item : list) {
            if (item.getNum() > maxValue) {
                maxValue = item.getNum();
            }
        }

        YAxis leftAxis = mBarChart.getAxisLeft();
        if (maxValue <= 2) {
            leftAxis.setLabelCount(2, false);
        } else if (maxValue == 3) {
            leftAxis.setLabelCount(3, false);
        } else if (maxValue == 4) {
            leftAxis.setLabelCount(4, false);
        } else if (maxValue > 8) {
            leftAxis.setLabelCount(8, false);
        } else {
            leftAxis.setLabelCount(6, false);
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (RespCollegeAnalysis.UnusualListBean item : list) {
            String day = item.getDay();
            try {
                Calendar cal = TimeUtils.parseDate(day, TimeUtils.DATE_FORMAT_DATE);
                int month = cal.get(Calendar.MONTH) + 1;
                int date = cal.get(Calendar.DATE);
                BarEntry entry = new BarEntry(getXvalue(month, date), item.getNum());
                entries.add(entry);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(this, R.color.color_bar_chart)); //one color
        //ColorBarDataSet dataSet = new ColorBarDataSet(entries, "");

        BarData data = new BarData(dataSet);
        data.setHighlightEnabled(false);//柱形是否允许高亮选择
        data.setValueTextColor(Color.BLUE);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                            ViewPortHandler viewPortHandler) {
                int num = Float.valueOf(value).intValue();
                return String.valueOf(num);
            }
        });

        mBarChart.setData(data);
        mBarChart.invalidate();
    }


    /**
     * 设置线形图
     *
     * @param list
     */
    private void setLineChart(List<RespCollegeAnalysis.UnusualListBean> list) {
        if (ListUtils.isEmpty(list)) {
            mLineChart.setNoDataText("未查询到数据!");
            mLineChart.invalidate();
            return;
        }

        int maxValue = 0;
        for (RespCollegeAnalysis.UnusualListBean item : list) {
            if (item.getNum() > maxValue) {
                maxValue = item.getNum();
            }
        }

        YAxis leftAxis = mLineChart.getAxisLeft();
        if (maxValue <= 2) {
            leftAxis.setLabelCount(2, false);
        } else if (maxValue == 3) {
            leftAxis.setLabelCount(3, false);
        } else if (maxValue == 4) {
            leftAxis.setLabelCount(4, false);
        } else if (maxValue > 8) {
            leftAxis.setLabelCount(8, false);
        } else {
            leftAxis.setLabelCount(6, false);
        }

        List<Entry> entries = new ArrayList<>();
        for (RespCollegeAnalysis.UnusualListBean item : list) {
            String day = item.getDay();
            try {
                Calendar cal = TimeUtils.parseDate(day, TimeUtils.DATE_FORMAT_DATE);
                cal.setFirstDayOfWeek(Calendar.MONDAY);

                int week = cal.get(Calendar.DAY_OF_WEEK);
                if (week == Calendar.SUNDAY) {
                    week = 8;
                }
                BarEntry entry = new BarEntry(week, item.getNum());
                entries.add(entry);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //LineDataSet set1 = new LineDataSet(entries, "#未签到人数表");
        LineDataSet set1 = new LineDataSet(entries, "");

        set1.setDrawIcons(false);

        // set the line to be drawn like this "- - - - - -"
        //set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.RED);
        }

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.RED);
        data.setValueTextSize(9f);

        mLineChart.setData(data);
        mLineChart.invalidate();
    }


    private float getXvalue(int month, int date) {
        int sum = month * 100 + date;
        return Integer.valueOf(sum).floatValue();
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = new RectF();
        mBarChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mBarChart.getPosition(e, AxisDependency.LEFT);

        Log.i(TAG, bounds.toString());
        Log.i(TAG, position.toString());

        Log.i(TAG, "low: " + mBarChart.getLowestVisibleX() + ", high: "
                + mBarChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    }


    /**
     * 自定义柱形图颜色
     */
    class ColorBarDataSet extends BarDataSet {
        ColorBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
            if (!isFinishing()) {
                setColors(new int[]{R.color.color_bar_chart, R.color.color_bar_chart,
                        R.color.color_bar_chart}, mContext);
            }
        }

        @Override
        public int getColor(int index) {
            if (!isFinishing()) {
                if (getEntryForIndex(index).getY() < 80) // less than 80 green
                    return mColors.get(0);
                else if (getEntryForIndex(index).getY() < 100) // less than 100 orange
                    return mColors.get(1);
                else // greater or equal than 100 red
                    return mColors.get(2);
            } else {
                return super.getColor(index);
            }
        }
    }


}