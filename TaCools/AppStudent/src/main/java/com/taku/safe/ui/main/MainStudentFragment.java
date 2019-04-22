package com.taku.safe.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.activity.MapActivity;
import com.taku.safe.activity.MessageActivity;
import com.taku.safe.activity.SignTrackActivity;
import com.taku.safe.sos.SosInMapActivity;
import com.taku.safe.dialog.ToSoSDialog;
import com.taku.safe.protocol.respond.RespStudentInfo;
import com.taku.safe.sos.StartSosActivity;
import com.taku.safe.utils.TimeUtils;
import com.taku.safe.views.CountTimeView;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Calendar;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class MainStudentFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = MainStudentFragment.class.getSimpleName();

    private UIHandler mHandler;

    private RespStudentInfo mSignInfo;

    private QBadgeView badgeView;

    private LinearLayout btn_sos;
    private TextView tv_msg;
    private TextView tv_sign;

    private CountTimeView tv_time;
    private TextView tv_sign_info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_student, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        reqSignInfo();
        MobclickAgent.onPageStart(TAG);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }


    private void initView(View view) {
        tv_time = (CountTimeView) view.findViewById(R.id.tv_time);
        tv_sign_info = (TextView) view.findViewById(R.id.tv_sign_info);

        btn_sos = (LinearLayout) view.findViewById(R.id.btn_sos);
        btn_sos.setOnClickListener(this);
        btn_sos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startSos(false);
                return true;
            }
        });

        view.findViewById(R.id.tv_track).setOnClickListener(this);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        tv_msg.setOnClickListener(this);

        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_sign.setOnClickListener(this);

        badgeView = new QBadgeView(mContext);
        Badge badge = badgeView.bindTarget(tv_msg);
        badge.setBadgeTextSize(10, true);
        badge.setBadgeGravity(Gravity.TOP | Gravity.END);
        //badge.setBadgePadding(6, true);

        int offsetX = getResources().getDimensionPixelOffset(R.dimen.badge_offset_x);
        badge.setGravityOffset(offsetX, 5, true);

        badge.setBadgeTextColor(ContextCompat.getColor(getContext(), R.color.color_white));

        int count = mTakuApp.getMsgCount();
        if (count > 0) {
            badgeView.setVisibility(View.VISIBLE);
            badge.setBadgeText(count + "");
        } else {
            badgeView.setVisibility(View.GONE);
        }


    }

    /**
     * 请求签到信息
     */
    private void reqSignInfo() {
        mTakuApp.reqSignInfo(new TakuApp.SignInfo() {
            @Override
            public void success(RespStudentInfo info) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    mSignInfo = info;
                    if (mSignInfo != null
                            && mSignInfo.isSuccess()
                            && mSignInfo.getRestSignInfo() != null) {
                        processSignInfo(info);
                    }
                }
            }
        });
    }


    /**
     * 处理签到信息
     *
     * @param signInfo
     */
    private void processSignInfo(RespStudentInfo signInfo) {
        boolean isEnable = false;

        if (signInfo == null || signInfo.getRestSignInfo() == null) {
            Toast.makeText(mContext, "学生签到信息获取错误！", Toast.LENGTH_SHORT).show();
            return;
        }

        int sosId = signInfo.getActiveSosId();
        int needSign = signInfo.getRestSignInfo().getNeedSign();      //0 不需要签到 1 需要签到
        int signStatus = signInfo.getRestSignInfo().getSignStatus();  //0 未签到 1 已签到
        int signValid = signInfo.getRestSignInfo().getSignValid();    // 0 异常签到 1 有效签到 -1未签到

        if (sosId > 0) {
            Intent intent = new Intent(mContext, SosInMapActivity.class);
            intent.putExtra(SosInMapActivity.IS_TRY, false);
            intent.putExtra(SosInMapActivity.SOS_ID, sosId);
            startActivity(intent);
        }

        if (needSign == 1) { //今天需要签到（上课天）
            if (signStatus == 1) { //今天已经签到
                if (signValid == -1) { //未签到
                    isEnable = true;
                } else if (signValid == 0) { //异常签到
                    //已经正常，但位置异常，可再此正常签到
                    isEnable = false;
                } else if (signValid == 1) { //有效签到
                    //已经正常签到（提示今天已经正常签到）
                    isEnable = false;
                }

            } else {
                //今天还没有签到
                isEnable = true;
            }
        }

        if (isEnable) {
            String curTime = TimeUtils.getTime(System.currentTimeMillis(),
                    TimeUtils.DATE_FORMAT_HOUR);
            String startTime = mSignInfo.getRestSignInfo().getStartTime(); //有效开始时间
            String endTime = mSignInfo.getRestSignInfo().getEndTime(); //有效结束时间

            try {
                Calendar startDate = TimeUtils.parseDate(startTime, TimeUtils.DATE_FORMAT_HOUR);
                int startHour = startDate.get(Calendar.HOUR_OF_DAY);
                int startMin = startDate.get(Calendar.MINUTE);

                Calendar curDate = TimeUtils.parseDate(curTime, TimeUtils.DATE_FORMAT_HOUR);
                int curHour = curDate.get(Calendar.HOUR_OF_DAY);
                int curMin = curDate.get(Calendar.MINUTE);
                int curSecond = curDate.get(Calendar.SECOND);

                Calendar endDate = TimeUtils.parseDate(endTime, TimeUtils.DATE_FORMAT_HOUR);
                int endHour = endDate.get(Calendar.HOUR_OF_DAY);
                int endMin = endDate.get(Calendar.MINUTE);
                int endSecond = endDate.get(Calendar.SECOND);

                if (curTime.compareTo(startTime) >= 0
                        && curTime.compareTo(endTime) <= 0) {
                    tv_sign.setEnabled(true);
                    //显示签到结束倒计时
                    //long time = ((endHour - curHour) * 60 + (endMin - curMin)) * 60 * 1000;

                    long timeSecond = (endHour - curHour) * 60 * 60     //小时->秒
                            + (endMin - curMin) * 60                    //分钟->秒
                            + (endSecond - curSecond);                  //秒->秒
                    tv_time.setCountTime(timeSecond * 1000);            //秒->毫秒

                    tv_sign_info.setText(R.string.sign_end);

                } else if (curTime.compareTo(startTime) < 0) {
                    //显示 签到开始倒计时
                    long time = ((startHour - curHour) * 60 + (startMin - curMin)) * 60 * 1000;
                    tv_time.setCountTime(time);
                    tv_sign_info.setText(R.string.sign_start);

                } else if (curTime.compareTo(endTime) > 0) {  //签到时间已过
                    //超过 签到结束时间
                    tv_time.setVisibility(View.GONE);
                    tv_sign.setEnabled(false);
                    tv_sign_info.setTextColor(ContextCompat.getColor(getContext(), R.color.color_red));
                    tv_sign_info.setText(R.string.sign_out_time);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tv_sign.setEnabled(false);
            tv_time.setVisibility(View.GONE);
            tv_sign_info.setText(R.string.no_need_sign);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_sos:
                startSos(true);
                break;
            case R.id.tv_track: {
                Intent intent = new Intent(mContext, SignTrackActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.tv_sign:
                startSign();
                break;
            case R.id.tv_msg:
                if (badgeView.getVisibility() == View.VISIBLE) {
                    badgeView.setVisibility(View.GONE);
                    mTakuApp.setMsgCount(0);
                }
                startActivity(new Intent(mContext, MessageActivity.class));
                break;
        }

    }


    private static final int RC_ACCESS_FINE_LOCATION = 123;

    @AfterPermissionGranted(RC_ACCESS_FINE_LOCATION)
    private void startSos(boolean isTry) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Already have permission, do the thing
            // ...
            if (isTry) {
                Intent intent = new Intent(mContext, SosInMapActivity.class);
                intent.putExtra(SosInMapActivity.IS_TRY, true);
                startActivity(intent);
            } else {
                ToSoSDialog.CallBack callBack = new ToSoSDialog.CallBack() {
                    @Override
                    public void onEntry() {
                        if (isAdded()) {
                            Intent intent = new Intent(mContext, SosInMapActivity.class);
                            intent.putExtra(SosInMapActivity.IS_TRY, false);
                            startActivity(intent);
                        }
                    }
                };

                ToSoSDialog.Builder builder = new ToSoSDialog.Builder(getContext());
                builder.callBack(callBack);
                builder.builder().show();
            }

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "没有开启定位权限将无法使用",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @AfterPermissionGranted(RC_ACCESS_FINE_LOCATION)
    private void startSign() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Already have permission, do the thing
            // ...
            Intent intent = new Intent(mContext, MapActivity.class);
            intent.putExtra(MapActivity.SIGN_TYPE, MapActivity.TYPE_REST);
            intent.putExtra(MapActivity.SIGN_INFO, mSignInfo);
            startActivity(intent);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "没有开启定位权限将无法使用",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<MainStudentFragment> mTarget;

        UIHandler(MainStudentFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

}
