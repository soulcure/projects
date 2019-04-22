package com.taku.safe.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.taku.safe.views.ArcView;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;


public class MainTeacherFragment extends BasePermissionFragment implements View.OnClickListener {
    public final static String TAG = MainTeacherFragment.class.getSimpleName();

    private TextView tv_choose_date;
    private TextView tv_choose_school;

    private TextView tv_percent;

    private TextView tv_normal;
    private TextView tv_unusual;
    private TextView tv_no_sign;
    private TextView tv_no_regedit;

    private FrameLayout container;
    private ArcView arcView;

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
        return inflater.inflate(R.layout.fragment_main_teacher, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        setListener();
        reqTeacherInfo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        tv_percent = (TextView) view.findViewById(R.id.tv_percent);
        arcView = (ArcView) view.findViewById(R.id.arcView);
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
                            int role = info.getRole();
                            mTakuApp.setRole(role);
                            initData(role);

                            date = mTakuApp.getSignDate();
                            if (TextUtils.isEmpty(date)) {
                                Calendar calendar = Calendar.getInstance(); //当天
                                //calendar.add(Calendar.DATE, -1);    //得到前1天
                                date = TimeUtils.DATE_FORMAT_DATE.format(calendar.getTime());
                            }

                            tv_choose_date.setText(date);
                            reqSignRestStatistics(date);

                            String deptName = mTakuApp.getDeptName();
                            tv_choose_school.setText(deptName);

                        }
                    }
                });
            }
        });
    }


    /**
     * @param day 天
     */
    private void reqSignRestStatistics(String day) {

        mTakuApp.reqSignRestStatistics(day, new IGetListener() {
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

                    //for 初始数据都为0
                    if (normal == 0 && unusual == 0 && noSign == 0 && noRegedit == 0) {
                        arcView.setNumber(1, 1, 1, 1);
                    } else {
                        arcView.setNumber(normal, unusual, noSign, noRegedit);
                    }

                    String format = getString(R.string.percentage);

                    int signNumAct = restSignData.getSiginNumAct();  //签到人数
                    int signNumExpect = restSignData.getSiginNumExpect(); //应签到人数

                    if (signNumAct != 0) {
                        float f = signNumAct * 100.0f / signNumExpect;
                        BigDecimal b = new BigDecimal(f);
                        float rote = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        tv_percent.setText(String.format(format, rote));
                    } else {
                        tv_percent.setText(String.format(format, 0.0));
                    }

                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignCountActivity.PI_ZHU && resultCode == Activity.RESULT_OK) {
            reqSignRestStatistics(date);
        }
    }


    private class CalendarSelector implements CalendarDialog.CallBack {
        @Override
        public void onSelect(Date d) {
            date = TimeUtils.DATE_FORMAT_DATE.format(d);

            mTakuApp.setSignDate(date);
            tv_choose_date.setText(date);
            reqSignRestStatistics(date);
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
                    if (mTakuApp.isCommander()) {
                        tv_choose_school.setText(content);
                    } else {
                        tv_choose_school.setText(mTakuApp.getDeptName());
                    }
                }
                reqSignRestStatistics(date);
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
                intent.putExtra(UnusualAnalysisActivity.SIGN_TYPE, UnusualAnalysisActivity.SIGN_RESET);
                intent.putExtra(UnusualAnalysisActivity.CUR_DATE, date);
                startActivity(intent);
            }
            break;
            case R.id.tv_no_sign_analysis: {
                Intent intent = new Intent(mContext, UnusualAnalysisActivity.class);
                intent.putExtra(UnusualAnalysisActivity.DATA_TYPE, UnusualAnalysisActivity.DATA_NO_SIGN);
                intent.putExtra(UnusualAnalysisActivity.SIGN_TYPE, UnusualAnalysisActivity.SIGN_RESET);
                intent.putExtra(UnusualAnalysisActivity.CUR_DATE, date);
                startActivity(intent);
            }
            break;
            case R.id.linear_normal: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_REST);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 0);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);

                startActivityForResult(intent, SignCountActivity.PI_ZHU);
            }
            break;
            case R.id.linear_unusual: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_REST);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 1);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);
                startActivityForResult(intent, SignCountActivity.PI_ZHU);
            }
            break;
            case R.id.linear_no_sign: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_REST);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 2);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);
                startActivityForResult(intent, SignCountActivity.PI_ZHU);
            }
            break;
            case R.id.linear_no_regedit: {
                Intent intent = new Intent(mContext, SignCountActivity.class);
                intent.putExtra(SignCountActivity.SIGN_TYPE, SignCountActivity.TYPE_REST);
                intent.putExtra(SignCountActivity.DATE, date);
                intent.putExtra(SignCountActivity.CUR_TAB, 3);

                intent.putExtra(SignCountActivity.NORMAL_COUNT, normal);
                intent.putExtra(SignCountActivity.UNUSUAL_COUNT, unusual);
                intent.putExtra(SignCountActivity.NOSIGN_COUNT, noSign);
                intent.putExtra(SignCountActivity.NOREGEDIT_COUNT, noRegedit);
                startActivityForResult(intent, SignCountActivity.PI_ZHU);
            }
            break;
        }

    }


}
