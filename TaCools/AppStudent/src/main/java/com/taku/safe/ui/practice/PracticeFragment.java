package com.taku.safe.ui.practice;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.activity.MapActivity;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespSignList;
import com.taku.safe.protocol.respond.RespStudentInfo;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.refresh.OnNextPageListener;
import com.taku.safe.refresh.RefreshRecyclerView;
import com.taku.safe.timeline.TimeLineAdapter;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by colin on 2017/5/10.
 */
public class PracticeFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = PracticeFragment.class.getSimpleName();

    public static final int PRACTICE_SIGN = 1000;

    private RefreshRecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private Spinner spinner;
    private RespStudentInfo mSignInfo;

    private AppBarLayout appbar;
    private LinearLayout linear_content;
    private TextView tv_empty;

    private ImageView img_title;
    private TextView tv_company;
    private TextView tv_teach;
    private TextView tv_teach_phone;

    private TextView tv_sign;

    private List<String> monthList = new ArrayList<>();

    private String mCurMonth = "";  //当前查询显示的月份

    // 是否刷新状态
    private boolean isPullRefresh = false;
    //private int curPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseTime();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        parsePracticeInfoView();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        reqSignInfo();
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRACTICE_SIGN && resultCode == Activity.RESULT_OK) {
            mCurMonth = monthList.get(0);

            reqPracticeList();

            setRefreshEnable(false);
        }
    }

    /**
     * 设置下拉刷新
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshing(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setRefreshing(b);  //是否关闭下拉刷新动画
    }


    /**
     * 是否刷新是否可用
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshEnable(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setLoadMoreEnable(b);  //是否关闭上拉刷新
    }

    /**
     * 关闭刷新动画
     */
    private void refreshComplete() {
        isPullRefresh = false;
        mRecyclerView.setRefreshing(false);  //关闭下拉刷新动画
        mRecyclerView.refreshComplete();//关闭上拉刷新动画
    }


    private void initView(View view) {
        appbar = (AppBarLayout) view.findViewById(R.id.appbar);
        linear_content = (LinearLayout) view.findViewById(R.id.linear_content);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);

        img_title = (ImageView) view.findViewById(R.id.img_title);
        RespUserInfo userInfo = mTakuApp.getUserInfo();
        if (userInfo != null && userInfo.getGender() == 1) {
            img_title.setImageResource(R.mipmap.img_practice_boy);
        } else {
            img_title.setImageResource(R.mipmap.img_practice_girl);
        }

        tv_company = (TextView) view.findViewById(R.id.tv_company);
        tv_teach = (TextView) view.findViewById(R.id.tv_teach);
        tv_teach_phone = (TextView) view.findViewById(R.id.tv_teach_phone);

        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_sign.setOnClickListener(this);

        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRecyclerView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqPracticeList();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mTimeLineAdapter = new TimeLineAdapter(getContext());

        mRecyclerView.setAdapter(mTimeLineAdapter);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, monthList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(onSelectedListener);

    }


    AdapterView.OnItemSelectedListener onSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurMonth = monthList.get(position);
            reqPracticeList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    /**
     * 请求签到信息
     */
    private void reqSignInfo() {
        mTakuApp.reqSignInfo(new TakuApp.SignInfo() {
            @Override
            public void success(RespStudentInfo info) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    mSignInfo = info;
                    if (info != null && info.isSuccess()) {
                        processSignInfo(info);
                    }
                }
            }
        });
    }


    /**
     * 学生没有实习信息的时候，UI刷新
     */
    private void parsePracticeInfoView() {
        RespStudentInfo signInfo = mTakuApp.getSignInfo();
        if (signInfo != null && signInfo.isSuccess()) {
            if (signInfo.getInternshipInfo() == null) {
                appbar.setVisibility(View.GONE);
                linear_content.setVisibility(View.GONE);
                tv_empty.setVisibility(View.VISIBLE);
            } else {
                appbar.setVisibility(View.VISIBLE);
                linear_content.setVisibility(View.VISIBLE);
                tv_empty.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 处理签到信息
     *
     * @param signInfo
     */
    private void processSignInfo(RespStudentInfo signInfo) {
        if (signInfo.getInternshipInfo() == null) {
            return;
        }

        String companyName = signInfo.getInternshipInfo().getCompanyName();
        String teacherName = signInfo.getInternshipInfo().getTeacherName();
        String teacherPhone = signInfo.getInternshipInfo().getTeacherPhoneNo();

        tv_company.setText(companyName);
        tv_teach.setText(teacherName);
        tv_teach_phone.setText(teacherPhone);

        int needSign = signInfo.getInternshipInfo().getNeedSign();      //0 不需要签到 1 需要签到
        int signStatus = signInfo.getInternshipInfo().getSignStatus();  //0 未签到 1 已签到
        int signValid = signInfo.getInternshipInfo().getSignValid();    // 0 异常签到 1 有效签到 -1未签到

        if (needSign == 1) { //今天需要签到（上课天）
            if (signStatus == 1) { //今天已经签到
                if (signValid == -1) { //未签到
                    tv_sign.setEnabled(true);
                    tv_sign.setText(R.string.sign_now);
                    tv_sign.setTextColor(ContextCompat.getColor(getContext(), R.color.color_blue));

                } else if (signValid == 0) { //异常签到
                    //已签到，但位置异常
                    tv_sign.setEnabled(false);
                    tv_sign.setText(R.string.signed_unusual);
                    tv_sign.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange));

                } else if (signValid == 1) { //有效签到
                    //已经正常签到（提示今天已经正常签到）
                    tv_sign.setEnabled(false);
                    tv_sign.setText(R.string.signed_normal);
                    tv_sign.setTextColor(ContextCompat.getColor(getContext(), R.color.color_white));

                }

            } else {
                //今天还没有签到
                tv_sign.setEnabled(true);
                tv_sign.setText(R.string.sign_now);
                tv_sign.setTextColor(ContextCompat.getColor(getContext(), R.color.color_blue));
            }
        }


    }


    private void reqPracticeList() {
        String url = AppConfig.PRACTICE_SIGN_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("month", mCurMonth);
        //params.put("page", curPage);
        //params.put("limit", 10);

        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isAdded()) {
                    RespSignList bean = GsonUtil.parse(response, RespSignList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<RespSignList.SignlistBean> list = bean.getSignlist();
                        mTimeLineAdapter.setList(list);

                        //curPage++;

                        /*if (bean.getPageInfo().isLastPage()) {
                            mRecyclerView.setOnNextPageListener(null);
                        } else {
                            mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                            mRecyclerView.setRefreshEnable(false); //关闭下拉刷新
                        }*/
                    }
                }
            }
        });

    }


    private void parseTime() {
        Calendar calendar = Calendar.getInstance(); //当月
        monthList.add(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, -1);    //得到前1个月
        monthList.add(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, -1);    //得到前2个月
        monthList.add(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, -1);    //得到前3个月
        monthList.add(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, -1);    //得到前4个月
        monthList.add(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, -1);    //得到前5个月
        monthList.add(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));
    }


    private static final int RC_ACCESS_FINE_LOCATION = 123;

    @AfterPermissionGranted(RC_ACCESS_FINE_LOCATION)
    private void startSign() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Already have permission, do the thing
            // ...
            Intent intent = new Intent(mContext, MapActivity.class);
            intent.putExtra(MapActivity.SIGN_TYPE, MapActivity.TYPE_PRACTICE);
            intent.putExtra(MapActivity.SIGN_INFO, mSignInfo);

            startActivityForResult(intent, PRACTICE_SIGN);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "没有开启定位权限将无法使用",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_sign:
                startSign();
                break;
        }
    }
}
