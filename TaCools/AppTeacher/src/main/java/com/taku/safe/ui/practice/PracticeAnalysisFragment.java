package com.taku.safe.ui.practice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.activity.SignCountActivity;
import com.taku.safe.activity.UnusualAnalysisActivity;
import com.taku.safe.dialog.CalendarDialog;
import com.taku.safe.dialog.SchoolDialog;
import com.taku.safe.http.IGetListener;
import com.taku.safe.protocol.respond.RespRestSignData;
import com.taku.safe.protocol.respond.RespSchoolList;
import com.taku.safe.protocol.respond.RespTeacherInfo;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PracticeAnalysisFragment extends BasePermissionFragment implements View.OnClickListener {
    public final static String TAG = PracticeAnalysisFragment.class.getSimpleName();

    private TextView tv_choose_date;
    private TextView tv_choose_school;

    private TextView tv_normal;
    private TextView tv_unusual;
    private TextView tv_no_sign;
    private TextView tv_no_regedit;

    private FrameLayout container;
    private PieChart mChart;

    private String date;  //当前选择日期

    private int normal;
    private int unusual;
    private int noSign;
    private int noRegedit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice_analysis, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        setListener();
        initBarChart();
        reqTeacherInfo();
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


    private void initView(View view) {
        container = (FrameLayout) view.findViewById(R.id.container);

        tv_choose_date = (TextView) view.findViewById(R.id.tv_choose_date);
        tv_choose_school = (TextView) view.findViewById(R.id.tv_choose_school);

        mChart = (PieChart) view.findViewById(R.id.pieChart);
    }


    private void initData(int role) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View viewDetail;

        if (role == 1) {  //1 决策者 other 管理者
            viewDetail = inflater.inflate(R.layout.main_teacher_detail_1, container, true);

            viewDetail.findViewById(R.id.tv_unusual_analysis).setOnClickListener(this);
            viewDetail.findViewById(R.id.tv_no_sign_analysis).setOnClickListener(this);

        } else {
            viewDetail = inflater.inflate(R.layout.main_teacher_detail_2, container, true);
        }

        tv_normal = (TextView) viewDetail.findViewById(R.id.tv_normal);
        tv_unusual = (TextView) viewDetail.findViewById(R.id.tv_unusual);
        tv_no_sign = (TextView) viewDetail.findViewById(R.id.tv_no_sign);
        tv_no_regedit = (TextView) viewDetail.findViewById(R.id.tv_no_regedit);

        viewDetail.findViewById(R.id.linear_normal).setOnClickListener(this);
        viewDetail.findViewById(R.id.linear_unusual).setOnClickListener(this);
        viewDetail.findViewById(R.id.linear_no_sign).setOnClickListener(this);
        viewDetail.findViewById(R.id.linear_no_regedit).setOnClickListener(this);
    }


    private void setListener() {
        tv_choose_date.setOnClickListener(this);
        tv_choose_school.setOnClickListener(this);
    }


    /**
     * 请求签到信息
     */
    private void reqTeacherInfo() {
        mTakuApp.reqSchoolList(new TakuApp.SchoolInfo() {
            @Override
            public void success(RespSchoolList list) {
                mTakuApp.reqTeacherInfo(new TakuApp.TeacherInfo() {
                    @Override
                    public void success(RespSchoolList list, RespTeacherInfo info) {
                        if (isAdded() && info != null && info.isSuccess()) {
                            initData(info.getRole());

                            date = mTakuApp.getPracticeDate();
                            if (TextUtils.isEmpty(date)) {
                                Calendar calendar = Calendar.getInstance(); //当天
                                //calendar.add(Calendar.DATE, -1);    //得到前1天
                                date = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());
                            }

                            tv_choose_date.setText(date);
                            reqSignPracticeStatistics(date);

                            String deptName = mTakuApp.getDeptName();
                            tv_choose_school.setText(deptName);

                            if (info.getRole() != 1) {//决策者才有组织机构选择
                                tv_choose_school.setVisibility(View.INVISIBLE);
                            }

                        }
                    }
                });
            }
        });
    }


    /**
     * @param day 天
     */
    private void reqSignPracticeStatistics(String day) {

        mTakuApp.reqSignPracticeStatistics(day, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespRestSignData restSignData = GsonUtil.parse(response, RespRestSignData.class);
                if (isAdded() && restSignData != null && restSignData.isSuccess()) {

                    normal = restSignData.getSiginNumNormal();
                    unusual = restSignData.getSiginNumAbnormal();
                    noSign = restSignData.getNotSignin();
                    noRegedit = restSignData.getNotActive();

                    tv_normal.setText(String.valueOf(normal));
                    tv_unusual.setText(String.valueOf(unusual));
                    tv_no_sign.setText(String.valueOf(noSign));
                    tv_no_regedit.setText(String.valueOf(noRegedit));

                    setDataBarChart(normal, unusual, noSign, noRegedit);
                }
            }
        });
    }


    private void initBarChart() {

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(1, 1, 1, 1);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(false);
        mChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.color_blue));

        mChart.setTransparentCircleColor(ContextCompat.getColor(getContext(), R.color.color_blue));
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(false);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.animateY(200, Easing.EasingOption.EaseInOutQuad);  //动画设置
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }


    private void setDataBarChart(int normal, int unusual, int noSign, int noRegedit) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (normal == 0 && unusual == 0 && noSign == 0 && noRegedit == 0) {
            entries.add(new PieEntry(1, "正常"));
            entries.add(new PieEntry(1, "异常"));
            entries.add(new PieEntry(1, "未签到"));
            entries.add(new PieEntry(1, "待注册"));
        } else {
            if (normal > 0) {
                entries.add(new PieEntry(normal, "正常"));
            }
            if (unusual > 0) {
                entries.add(new PieEntry(unusual, "异常"));
            }
            if (noSign > 0) {
                entries.add(new PieEntry(noSign, "未签到"));
            }
            if (noRegedit > 0) {
                entries.add(new PieEntry(noRegedit, "待注册"));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "loading... data");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(2f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        if (normal == 0 && unusual == 0 && noSign == 0 && noRegedit == 0) {
            colors.add(ContextCompat.getColor(getContext(), R.color.color_normal));
            colors.add(ContextCompat.getColor(getContext(), R.color.color_unusual));
            colors.add(ContextCompat.getColor(getContext(), R.color.color_nosign));
            colors.add(ContextCompat.getColor(getContext(), R.color.color_noregedit));
        } else {
            if (normal > 0) {
                colors.add(ContextCompat.getColor(getContext(), R.color.color_normal));
            }
            if (unusual > 0) {
                colors.add(ContextCompat.getColor(getContext(), R.color.color_unusual));
            }
            if (noSign > 0) {
                colors.add(ContextCompat.getColor(getContext(), R.color.color_nosign));
            }
            if (noRegedit > 0) {
                colors.add(ContextCompat.getColor(getContext(), R.color.color_noregedit));
            }
        }

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.text_gray_87));
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*switch (requestCode) {
        }*/
    }


    private class CalendarSelector implements CalendarDialog.CallBack {
        @Override
        public void onSelect(Date d) {
            date = TimeUtils.DATE_FORMAT_DATE.format(d);

            mTakuApp.setPracticeDate(date);
            tv_choose_date.setText(date);
            reqSignPracticeStatistics(date);
        }
    }


    /**
     * 选择日期对话框
     */
    private void showCalendarDialog() {
        CalendarDialog.Builder builder = new CalendarDialog.Builder(getContext());
        Calendar calendar = Calendar.getInstance();

        builder.maxYear(calendar.get(Calendar.YEAR)).maxMonth(calendar.get(Calendar.MONTH))
                .maxDate(calendar.get(Calendar.DATE));
        calendar.add(Calendar.MONTH, -6);
        builder.minYear(calendar.get(Calendar.YEAR)).minMonth(calendar.get(Calendar.MONTH))
                .minDate(calendar.get(Calendar.DATE));

        try {
            Calendar selectDate = TimeUtils.parseDate(date, TimeUtils.DATE_FORMAT_DATE);
            builder.curYear(selectDate.get(Calendar.YEAR)).curMonth(selectDate.get(Calendar.MONTH))
                    .curDate(selectDate.get(Calendar.DATE));

        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.callBack(new CalendarSelector());
        builder.builder().show();
    }

    /**
     * 选择学校对话框
     */
    private void showSchoolDialog() {
        SchoolDialog.Builder builder = new SchoolDialog.Builder(getContext());
        builder.teacherInfo(mTakuApp.getTeacherInfo());
        builder.level(mTakuApp.getLevel());
        builder.onConfim(new SchoolDialog.OnConfim() {
            @Override
            public void onConfirm(String content) {
                if (!TextUtils.isEmpty(content)) {
                    tv_choose_school.setText(content);
                }
                reqSignPracticeStatistics(date);
            }
        });
        builder.builder().show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_choose_date:
                showCalendarDialog();
                break;
            case R.id.tv_choose_school:
                showSchoolDialog();
                break;
            case R.id.tv_unusual_analysis: {
                Intent intent = new Intent(mContext, UnusualAnalysisActivity.class);
                intent.putExtra(UnusualAnalysisActivity.DATA_TYPE, UnusualAnalysisActivity.DATA_UNUSUAL);
                intent.putExtra(UnusualAnalysisActivity.SIGN_TYPE, UnusualAnalysisActivity.SIGN_PRACTICE);
                intent.putExtra(UnusualAnalysisActivity.CUR_DATE, date);
                startActivity(intent);
            }
            break;
            case R.id.tv_no_sign_analysis: {
                Intent intent = new Intent(mContext, UnusualAnalysisActivity.class);
                intent.putExtra(UnusualAnalysisActivity.DATA_TYPE, UnusualAnalysisActivity.DATA_NO_SIGN);
                intent.putExtra(UnusualAnalysisActivity.SIGN_TYPE, UnusualAnalysisActivity.SIGN_PRACTICE);
                intent.putExtra(UnusualAnalysisActivity.CUR_DATE, date);
                startActivity(intent);
            }
            break;
            case R.id.linear_normal: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_PRACTICE);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 0);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);

                startActivity(intent);
            }
            break;
            case R.id.linear_unusual: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_PRACTICE);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 1);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);
                startActivity(intent);
            }
            break;
            case R.id.linear_no_sign: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_PRACTICE);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 2);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);
                startActivity(intent);
            }
            break;
            case R.id.linear_no_regedit: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_PRACTICE);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 3);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);
                startActivity(intent);
            }
            break;
        }

    }


}
