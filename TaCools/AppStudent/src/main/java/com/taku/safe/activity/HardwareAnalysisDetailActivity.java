package com.taku.safe.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.adapter.IndoorDetailAdapter;
import com.taku.safe.adapter.NotClassDetailAdapter;
import com.taku.safe.adapter.UnusualOutDetailAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespIndoorDetail;
import com.taku.safe.protocol.respond.RespNoClassDetail;
import com.taku.safe.protocol.respond.RespUnusualOutDetail;
import com.taku.safe.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


/**
 * Created by colin on 2017/6/8.
 */

public class HardwareAnalysisDetailActivity extends BasePermissionActivity {

    public static final String DETAIL_TYPE = "detail_type";  //详细类型
    public static final String STUDENT_ID = "student_id";  //学生ID

    public static final int UNUSUAL_DETAIL = 1; //异常出入详细信息
    public static final int INDOOR_DETAIL = 2; //超级宅详细详细信息
    public static final int NO_CLASS_DETAIL = 3;//严重逃课详细信息


    private TextView tv_name;
    private TextView tv_class;
    private TextView tv_phone;

    private LinearLayout linear_info;
    private TextView tv_info;
    private TextView tv_address;

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_analysis_detail);
        initTitle();
        initView();
        reqHardwareAnalysisDetail();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.user_info);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_class = (TextView) findViewById(R.id.tv_class);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        linear_info = (LinearLayout) findViewById(R.id.linear_info);

        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_address = (TextView) findViewById(R.id.tv_address);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));

    }


    private void reqHardwareAnalysisDetail() {
        final int type = getIntent().getIntExtra(DETAIL_TYPE, UNUSUAL_DETAIL);
        int studentId = getIntent().getIntExtra(STUDENT_ID, 0);

        if (studentId == 0) {
            return;
        }

        final String url;
        if (type == INDOOR_DETAIL) {
            url = AppConfig.DEVICE_STATS_INDOOR_DETAIL; //超级宅详细详细信息
        } else if (type == NO_CLASS_DETAIL) {
            url = AppConfig.DEVICE_STATS_NO_CLASS_DETAIL; //严重逃课详细信息
        } else {
            url = AppConfig.DEVICE_STATS_UNUSUAL_DETAIL;  //异常出入详细信息
        }

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("level", mTakuApp.getLevel());
        params.put("deptId", mTakuApp.getDeptId());
        params.put("studentId", studentId);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                if (type == INDOOR_DETAIL) {
                    parseIndoorDetail(response);
                } else if (type == NO_CLASS_DETAIL) {
                    parseNotClassDetail(response);
                } else {
                    parseUnusualOutDetail(response);
                }
            }
        });
    }

    /**
     * 超级宅详细详细信息
     *
     * @param response
     */
    private void parseIndoorDetail(String response) {
        RespIndoorDetail bean = GsonUtil.parse(response, RespIndoorDetail.class);
        if (bean != null && bean.isSuccess()) {
            tv_name.setText(bean.getName());
            tv_class.setText(bean.getClassName());
            tv_phone.setText(bean.getPhoneNo());

            tv_info.setText(R.string.indoor_place);
            tv_address.setText(bean.getAddress());

            List<RespIndoorDetail.EventListBean> list = bean.getEventList();

            RespIndoorDetail.EventListBean head = new RespIndoorDetail.EventListBean();
            head.setStartDay(getString(R.string.indoor_range));
            head.setDays(-1);
            head.setLastShowDate(getString(R.string.indoor_last_time));
            if (list != null) {
                list.add(0, head);
            }

            IndoorDetailAdapter adapter = new IndoorDetailAdapter(mContext, list);
            mRecyclerView.setAdapter(adapter);
        }
    }

    /**
     * 严重逃课详细信息
     *
     * @param response
     */
    private void parseNotClassDetail(String response) {
        RespNoClassDetail bean = GsonUtil.parse(response, RespNoClassDetail.class);
        if (bean != null && bean.isSuccess()) {
            tv_name.setText(bean.getName());
            tv_class.setText(bean.getClassName());
            tv_phone.setText(bean.getPhoneNo());

            linear_info.setVisibility(View.GONE);
            List<RespNoClassDetail.EventListBean> list = bean.getEventList();

            RespNoClassDetail.EventListBean head = new RespNoClassDetail.EventListBean();
            head.setStartDay(getString(R.string.not_class_range));
            head.setDays(-1);
            head.setLastShowDate(getString(R.string.not_class_last_time));

            if (list != null) {
                list.add(0, head);
            }
            NotClassDetailAdapter adapter = new NotClassDetailAdapter(mContext, list);
            mRecyclerView.setAdapter(adapter);

        }
    }

    /**
     * 异常出入详细信息
     *
     * @param response
     */
    private void parseUnusualOutDetail(String response) {
        RespUnusualOutDetail bean = GsonUtil.parse(response, RespUnusualOutDetail.class);
        if (bean != null && bean.isSuccess()) {
            tv_name.setText(bean.getName());
            tv_class.setText(bean.getClassName());
            tv_phone.setText(bean.getPhoneNo());

            linear_info.setVisibility(View.GONE);
            List<RespUnusualOutDetail.EventListBean> list = bean.getEventList();
            RespUnusualOutDetail.EventListBean head = new RespUnusualOutDetail.EventListBean();
            head.setEventDesc(getString(R.string.unusual_out_place));
            head.setEventTime(getString(R.string.unusual_out_time));

            if (list != null) {
                list.add(0, head);
            }

            UnusualOutDetailAdapter adapter = new UnusualOutDetailAdapter(mContext, list);
            mRecyclerView.setAdapter(adapter);
        }
    }

}
