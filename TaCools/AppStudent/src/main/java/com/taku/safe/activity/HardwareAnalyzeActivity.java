package com.taku.safe.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.adapter.NotClassAdapter;
import com.taku.safe.adapter.NotConnectionAdapter;
import com.taku.safe.adapter.IndoorAdapter;
import com.taku.safe.adapter.UnusualOutAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.dialog.SchoolDialog;
import com.taku.safe.entity.AnalysisItem;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespIndoorList;
import com.taku.safe.protocol.respond.RespNoClassList;
import com.taku.safe.protocol.respond.RespNoConnectList;
import com.taku.safe.protocol.respond.RespUnusualOutList;
import com.taku.safe.refresh.OnNextPageListener;
import com.taku.safe.refresh.RefreshRecyclerView;
import com.taku.safe.ui.Analysis.AnalysisFragment;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardwareAnalyzeActivity extends BasePermissionActivity {
    private static final String TAG = HardwareAnalyzeActivity.class.getSimpleName();

    private Map<Integer, AnalysisItem> inconMap = new HashMap<>();

    private Map<Integer, String> dataMap = new HashMap<>();

    private RefreshRecyclerView mRecyclerView;

    private NotConnectionAdapter notConnectionAdapter;
    private IndoorAdapter notOutAdapter;
    private NotClassAdapter notClassAdapter;
    private UnusualOutAdapter unusualOutAdapter;

    //private MaterialSpinner spinner;
    private TextView tv_choose_school;

    private TextView tv_title;
    private ImageView img_pre;
    private ImageView img_next;
    private TextView tv_date;

    private String today;

    private Date curDate;
    private int timeRange = UnusualAnalysisActivity.DATA_OF_MONTH;


    private Calendar calendar;

    private String startDay;  //统计开始时间
    private String endDay;  //统计结束时间


    private FloatingActionMenu menuGreen;

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    private int level;
    private int indexCollege;
    private int indexMarjor;
    private int indexClass;

    // 是否刷新状态
    private boolean isPullRefresh = false;
    private int curPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_analysis);

        /*level = mTakuApp.getLevel();
        indexCollege = mTakuApp.getIndexCollege();
        indexMarjor = mTakuApp.getIndexMarjor();
        indexClass = mTakuApp.getIndexClass();
        mTakuApp.resetDeptId();*/

        initTabData();
        initTitle();
        initView();
        initDate();
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


    @Override
    protected void onDestroy() {
        //mTakuApp.revertDeptId(level, indexCollege, indexMarjor, indexClass);
        super.onDestroy();
    }

    private void initTitle() {
        String name = getIntent().getStringExtra("name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        tv_title = (TextView) findViewById(R.id.tv_title);
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
        int resId = getIntent().getIntExtra("resId", 0);

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

        tv_choose_school = (TextView) findViewById(R.id.tv_choose_school);
        String deptName = mTakuApp.getDeptName();
        tv_choose_school.setText(deptName);
        tv_choose_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSchoolDialog();
            }
        });

        /*spinner = (MaterialSpinner) findViewById(R.id.spinner);
        List<String> dataSet = mTakuApp.parseSpinnerData();
        if (ListUtils.isEmpty(dataSet)) {
            spinner.setVisibility(View.INVISIBLE);
        } else {
            spinner.setItems(dataSet);
        }

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mTakuApp.setSpinnerData(level, position);
                reqHardwareAnalysis();
            }
        });*/

        mRecyclerView = (RefreshRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));

        notConnectionAdapter = new NotConnectionAdapter(mContext);
        mRecyclerView.setAdapter(notConnectionAdapter);

        mRecyclerView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqHardwareAnalysis();
            }
        });

        List<AnalysisItem> iconList = new ArrayList<>();

        for (AnalysisItem item : AnalysisFragment.iconRes) {
            if (item.resId == resId) {
                iconList.add(item);
                break;
            }
        }

        for (AnalysisItem item : AnalysisFragment.iconRes) {
            if (!iconList.contains(item)) {
                iconList.add(item);
            }
        }


        inconMap.put(0, iconList.get(0));
        inconMap.put(1, iconList.get(1));
        inconMap.put(2, iconList.get(2));
        inconMap.put(3, iconList.get(3));


        menuGreen = (FloatingActionMenu) findViewById(R.id.menu_green);
        menuGreen.getMenuIconView().setImageResource(resId);
        menuGreen.setTag(R.id.menu_green_id, inconMap.get(0).resId);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setTag(1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuGreen.close(true);
                AnalysisItem res_item1 = inconMap.get(1);

                AnalysisItem swap = inconMap.put(0, res_item1);
                inconMap.put(1, swap);

                menuGreen.getMenuIconView().setImageResource(inconMap.get(0).resId);
                tv_title.setText(inconMap.get(0).name);
                fab1.setImageResource(inconMap.get(1).resId);

                menuGreen.setTag(R.id.menu_green_id, inconMap.get(0).resId);

                clearData();
                reqHardwareAnalysis();
            }
        });

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setTag(2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuGreen.close(true);
                AnalysisItem res_item2 = inconMap.get(2);

                AnalysisItem swap = inconMap.put(0, res_item2);
                inconMap.put(2, swap);

                menuGreen.getMenuIconView().setImageResource(inconMap.get(0).resId);
                tv_title.setText(inconMap.get(0).name);
                fab2.setImageResource(inconMap.get(2).resId);

                menuGreen.setTag(R.id.menu_green_id, inconMap.get(0).resId);

                clearData();
                reqHardwareAnalysis();
            }
        });
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setTag(3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuGreen.close(true);
                AnalysisItem res_item3 = inconMap.get(3);

                AnalysisItem swap = inconMap.put(0, res_item3);
                inconMap.put(3, swap);

                menuGreen.getMenuIconView().setImageResource(inconMap.get(0).resId);
                tv_title.setText(inconMap.get(0).name);
                fab3.setImageResource(inconMap.get(3).resId);

                menuGreen.setTag(R.id.menu_green_id, inconMap.get(0).resId);

                clearData();
                reqHardwareAnalysis();
            }
        });

        fab1.setImageResource(iconList.get(1).resId);
        fab2.setImageResource(iconList.get(2).resId);
        fab3.setImageResource(iconList.get(3).resId);

        createCustomAnimation();
    }


    private void clearData() {
        curPage = 1;
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof NotConnectionAdapter) {
            ((NotConnectionAdapter) adapter).clear();
        } else if (adapter instanceof IndoorAdapter) {
            ((IndoorAdapter) adapter).clear();
        } else if (adapter instanceof UnusualOutAdapter) {
            ((UnusualOutAdapter) adapter).clear();
        } else if (adapter instanceof NotClassAdapter) {
            ((NotClassAdapter) adapter).clear();
        }
    }


    /**
     * 选择学校对话框
     */
    private void showSchoolDialog() {
        SchoolDialog.Builder builder = new SchoolDialog.Builder(this);
        builder.teacherInfo(mTakuApp.getTeacherInfo());
        builder.level(mTakuApp.getPrivilegeLevel());
        builder.onConfim(new SchoolDialog.OnConfim() {
            @Override
            public void onConfirm(String content) {
                if (!TextUtils.isEmpty(content)) {
                    tv_choose_school.setText(content);
                }
                clearData();
                reqHardwareAnalysis();
            }
        });
        builder.builder().show();
    }


    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleX", 1.0f, 0.9f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleY", 1.0f, 0.9f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleX", 0.9f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleY", 0.9f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        /*scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menuGreen.getMenuIconView().setImageResource(menuGreen.isOpened()
                        ? R.mipmap.ic_close : resId);
            }
        });*/

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuGreen.setIconToggleAnimatorSet(set);
    }


    private void initTabData() {
        dataMap.put(R.mipmap.ic_not_connection, AppConfig.DEVICE_STATS_NO_CONNECT_LIST);
        dataMap.put(R.mipmap.ic_not_out, AppConfig.DEVICE_STATS_INDOOR_LIST);
        dataMap.put(R.mipmap.ic_access_unusual, AppConfig.DEVICE_STATS_UNUSUAL_OUT_LIST);
        dataMap.put(R.mipmap.ic_not_class, AppConfig.DEVICE_STATS_NO_CLASS_LIST);
    }

    private String parseDataUrl() {
        int key = (int) menuGreen.getTag(R.id.menu_green_id);
        return dataMap.get(key);
    }


    private void initDate() {
        /*String date = mTakuApp.getDate();
        if (!TextUtils.isEmpty(date)) {
            try {
                curDate = TimeUtils.DATE_FORMAT_DATE.parse(date);
                calendar.setTime(curDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            curDate = calendar.getTime();
        }

        timeRange = mTakuApp.getTimeRange();*/

        calendar = Calendar.getInstance();
        curDate = calendar.getTime();
        today = TimeUtils.DATE_FORMAT_DATE.format(curDate);

        if (timeRange == UnusualAnalysisActivity.DATA_OF_MONTH) {
            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);

            //tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(curDate));
        } else {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            //tv_date.setText(startDay + " - " + endDay);
        }

        reqHardwareAnalysis();
    }

    private void preDate() {
        if (timeRange == UnusualAnalysisActivity.DATA_OF_MONTH) {
            calendar.add(Calendar.MONTH, -1);

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);

            //tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));
            //curDate = calendar.getTime();

        } else {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            //tv_date.setText(startDay + " - " + endDay);
            //curDate = calendar.getTime();
        }

        clearData();
        reqHardwareAnalysis();
    }


    private void nextDate() {
        if (timeRange == UnusualAnalysisActivity.DATA_OF_MONTH) {
            calendar.add(Calendar.MONTH, 1);

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfMonth);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfMonth);

            //tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));
            //curDate = calendar.getTime();
        } else {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date firstDayOfWeek = calendar.getTime();
            startDay = TimeUtils.DATE_FORMAT_DATE.format(firstDayOfWeek);

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date lastDayOfWeek = calendar.getTime();
            endDay = TimeUtils.DATE_FORMAT_DATE.format(lastDayOfWeek);

            //tv_date.setText(startDay + " - " + endDay);
            //curDate = calendar.getTime();
        }
        clearData();
        reqHardwareAnalysis();
    }


    private void reqHardwareAnalysis() {
        final String url = parseDataUrl();

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("level", mTakuApp.getLevel());
        params.put("deptId", mTakuApp.getDeptId());

        params.put("page", curPage);
        params.put("limit", 10);

        if (url.equals(AppConfig.DEVICE_STATS_NO_CONNECT_LIST)) {
            img_pre.setVisibility(View.INVISIBLE);
            img_next.setVisibility(View.INVISIBLE);
            tv_date.setText(today);

            params.put("day", today);
        } else {
            img_pre.setVisibility(View.VISIBLE);
            img_next.setVisibility(View.VISIBLE);
            if (timeRange == UnusualAnalysisActivity.DATA_OF_MONTH) {
                tv_date.setText(TimeUtils.MONTH_OF_YEAR.format(calendar.getTime()));
            } else {
                tv_date.setText(startDay + " - " + endDay);
            }

            params.put("startDay", startDay);
            params.put("endDay", endDay);
        }

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                if (url.equals(AppConfig.DEVICE_STATS_NO_CONNECT_LIST)) {
                    RespNoConnectList bean = GsonUtil.parse(response, RespNoConnectList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<RespNoConnectList.PageInfoBean.ListBean> list = bean.getPageInfo().getList();

                        if (notConnectionAdapter == null) {
                            notConnectionAdapter = new NotConnectionAdapter(mContext);
                        }

                        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                        if (adapter == null || !(adapter instanceof NotConnectionAdapter)) {
                            mRecyclerView.setAdapter(notConnectionAdapter);
                        }

                        notConnectionAdapter.setList(list);
                        curPage++;

                        if (bean.getPageInfo().isLastPage()) {
                            mRecyclerView.setOnNextPageListener(null);
                        } else {
                            mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                        }
                        mRecyclerView.setRefreshEnable(false); //关闭下拉刷新

                        if (ListUtils.isEmpty(list)) {
                            mRecyclerView.setOnNextPageListener(null);
                            setRefreshEnable(false);
                        }

                    }
                } else if (url.equals(AppConfig.DEVICE_STATS_INDOOR_LIST)) {
                    RespIndoorList bean = GsonUtil.parse(response, RespIndoorList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<RespIndoorList.PageInfoBean.StudentlistBean> list = bean.getPageInfo().getList();

                        if (notOutAdapter == null) {
                            notOutAdapter = new IndoorAdapter(mContext);
                        }

                        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                        if (adapter == null || !(adapter instanceof IndoorAdapter)) {
                            mRecyclerView.setAdapter(notOutAdapter);
                        }

                        notOutAdapter.setList(list);

                        curPage++;

                        if (bean.getPageInfo().isLastPage()) {
                            mRecyclerView.setOnNextPageListener(null);
                        } else {
                            mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                        }
                        mRecyclerView.setRefreshEnable(false); //关闭下拉刷新

                        if (ListUtils.isEmpty(list)) {
                            mRecyclerView.setOnNextPageListener(null);
                            setRefreshEnable(false);
                        }
                    }
                } else if (url.equals(AppConfig.DEVICE_STATS_UNUSUAL_OUT_LIST)) {
                    RespUnusualOutList bean = GsonUtil.parse(response, RespUnusualOutList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<RespUnusualOutList.PageInfoBean.StudentlistBean> list = bean.getPageInfo().getList();


                        if (unusualOutAdapter == null) {
                            unusualOutAdapter = new UnusualOutAdapter(mContext);
                        }

                        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                        if (adapter == null || !(adapter instanceof UnusualOutAdapter)) {
                            mRecyclerView.setAdapter(unusualOutAdapter);
                        }

                        unusualOutAdapter.setList(list);

                        curPage++;

                        if (bean.getPageInfo().isLastPage()) {
                            mRecyclerView.setOnNextPageListener(null);
                        } else {
                            mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                        }
                        mRecyclerView.setRefreshEnable(false); //关闭下拉刷新

                        if (ListUtils.isEmpty(list)) {
                            mRecyclerView.setOnNextPageListener(null);
                            setRefreshEnable(false);
                        }
                    }
                } else if (url.equals(AppConfig.DEVICE_STATS_NO_CLASS_LIST)) {
                    RespNoClassList bean = GsonUtil.parse(response, RespNoClassList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<RespNoClassList.PageInfoBean.StudentlistBean> list = bean.getPageInfo().getList();


                        if (notClassAdapter == null) {
                            notClassAdapter = new NotClassAdapter(mContext);
                        }

                        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                        if (adapter == null || !(adapter instanceof NotClassAdapter)) {
                            mRecyclerView.setAdapter(notClassAdapter);
                        }

                        notClassAdapter.setList(list);

                        curPage++;

                        if (bean.getPageInfo().isLastPage()) {
                            mRecyclerView.setOnNextPageListener(null);
                        } else {
                            mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                        }
                        mRecyclerView.setRefreshEnable(false); //关闭下拉刷新

                        if (ListUtils.isEmpty(list)) {
                            mRecyclerView.setOnNextPageListener(null);
                            setRefreshEnable(false);
                        }
                    }
                }

                refreshComplete();

            }
        });

    }
}
